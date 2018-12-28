package com.linkdump.tchur.ld.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.view.inputmethod.EditorInfoCompat;
import android.support.v13.view.inputmethod.InputConnectionCompat;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.os.BuildCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.linkdump.tchur.ld.R;
import com.linkdump.tchur.ld.adapters.GroupChatAdapter;
import com.linkdump.tchur.ld.adapters.NewGroupChatAdapter;
import com.linkdump.tchur.ld.objects.Message;
import com.linkdump.tchur.ld.utils.MyEditText;
import com.linkedin.urls.Url;
import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements GroupChatAdapter.ItemClickListener, NewGroupChatAdapter.ItemClickListener {
    private static String TAG = "ChatActivity";
    private static String OG_REGEX = "og:image|og:title|og:description|og:type|og:url|og:video";

    private RecyclerView mRecyclerView;
    private NewGroupChatAdapter adapter;
    private ArrayList<String> events;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentGroup;
    private List<Message> messages;
    private DocumentReference userRef;
    private List<String> userGroups;
    private SharedPreferences prefs;
    private LinearLayoutManager mLayoutManager;
    private DocumentReference groupRef;
    private ImageButton imageButton;
    private MyEditText chatEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        clearNotifications();


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userRef = db.collection("users").document(mAuth.getUid());
        messages = new ArrayList<>();
        Intent intent = getIntent();
        currentGroup = intent.getStringExtra("groupID");
        groupRef = db.collection("groups").document(currentGroup);
        prefs = this.getSharedPreferences(
                getPackageName(), MODE_PRIVATE);

        prefs.edit().putString("currentGroup", currentGroup).apply();

        events = new ArrayList<>();
        userGroups = new ArrayList<>();
        mRecyclerView = findViewById(R.id.chat_recyclerview);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new NewGroupChatAdapter(this, messages);
        adapter.setClickListener(this);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mLayoutManager.smoothScrollToPosition(mRecyclerView, null, adapter.getItemCount());
            }
        });
        mRecyclerView.setAdapter(adapter);
        groupChatListener(currentGroup);

        imageButton = findViewById(R.id.imageButton);
        chatEditText = findViewById(R.id.chat_message_edit_text);
        chatEditText.setKeyBoardInputCallbackListener((inputContentInfo, flags, opts) -> {
            if (inputContentInfo.getLinkUri() != null){
                Log.d(TAG, String.valueOf(inputContentInfo.getLinkUri()));
                Map<String, Object> sendMessage = new HashMap<>();
                sendMessage.put("user", mAuth.getUid());
                sendMessage.put("userName", mAuth.getCurrentUser().getDisplayName());
                sendMessage.put("sentTime", Calendar.getInstance().getTimeInMillis());
                sendMessage.put("imageUrl", inputContentInfo.getLinkUri().toString());
                sendMessage.put("messageType", "IMAGE");
                groupRef.collection("messages").add(sendMessage);
            }

        });

        imageButton.setOnClickListener(view -> {
            if (!chatEditText.getText().toString().equals("")) {
                Boolean hasLink = false;
                String url = "";
                UrlDetector detector = new UrlDetector(chatEditText.getText().toString(), UrlDetectorOptions.Default);
                List<Url> urls = detector.detect();
                if (!urls.isEmpty()) {
                    hasLink = true;
                    url = urls.get(0).getFullUrl();
                }
                Map<String, Object> sendMessage = new HashMap<>();
                sendMessage.put("message", chatEditText.getText() + "");
                sendMessage.put("user", mAuth.getUid());
                sendMessage.put("userName", mAuth.getCurrentUser().getDisplayName());
                sendMessage.put("sentTime", Calendar.getInstance().getTimeInMillis());
                sendMessage.put("messageType", "TEXT");

                Boolean finalHasLink = hasLink;
                String finalUrl = url;
                Log.d(TAG, "url before message push: " + finalUrl);
                Log.d(TAG, "before database push");
                groupRef.collection("messages").add(sendMessage).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Successfully pushed");
                        DocumentReference messageRef = task.getResult();
                        if (finalHasLink) {
                            Log.d(TAG, "found Link in text");
                            new JsoupAsyncTask().execute(finalUrl, messageRef.getId());
                        }
                    }
                });
                chatEditText.getText().clear();
            }
        });
        chatEditText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        imageButton.callOnClick();
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });
    }

    public void groupChatListener(String currentGroup) {
        db.collection("groups").document(currentGroup).collection("messages").orderBy("sentTime", Query.Direction.DESCENDING).limit(25).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w("demo", "Listener Failed", e);
                return;
            }

            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                QueryDocumentSnapshot mDoc = doc.getDocument();
                Log.d("demo", mDoc.get("message") + "");
                Message tempMessage = mDoc.toObject(Message.class);
                if (!tempMessage.getUser().equals(mAuth.getUid())) {
                    tempMessage.setIsUser(false);
                } else {
                    tempMessage.setIsUser(true);
                }
                boolean exists = false;
                for (int i = 0; i < messages.size(); i++){
                    if (messages.get(i).getSentTime() == tempMessage.getSentTime()){
                        messages.set(i, tempMessage);
                        exists = true;
                    }
                }
                if (!exists){
                    messages.add(tempMessage);
                    events.add(mDoc.getString("message"));
                }
            }
            Collections.sort(messages);
            adapter.notifyDataSetChanged();
//            adapter.notifyItemInserted(adapter.getItemCount() - 1);
            mLayoutManager.scrollToPosition(messages.size() - 1);
        });
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs.edit().putString("currentGroup", currentGroup).apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs.edit().putString("currentGroup", "NONE").apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prefs.edit().putString("currentGroup", "NONE").apply();
    }

    public void clearNotifications() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.cancel(0);
    }

    public EditText addMimeTypes(Context context){
        return new android.support.v7.widget.AppCompatEditText(context) {
            @Override
            public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
                final InputConnection ic = super.onCreateInputConnection(editorInfo);
                EditorInfoCompat.setContentMimeTypes(editorInfo,
                        new String [] {"image/png"});

                final InputConnectionCompat.OnCommitContentListener callback =
                        (inputContentInfo, flags, opts) -> {
                            // read and display inputContentInfo asynchronously
                            if (BuildCompat.isAtLeastNMR1() && (flags &
                                    InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
                                try {
                                    inputContentInfo.requestPermission();
                                }
                                catch (Exception e) {
                                    return false; // return false if failed
                                }
                            }

                            Log.d(TAG, "contentURI: " + inputContentInfo.getContentUri());
                            if (inputContentInfo.getLinkUri() != null){
                                Log.d(TAG, "LinkUri: " + inputContentInfo.getLinkUri());
                            }

                            // read and display inputContentInfo asynchronously.
                            // call inputContentInfo.releasePermission() as needed.

                            return true;  // return true if succeeded
                        };
                return InputConnectionCompat.createWrapper(ic, editorInfo, callback);
            }
        };
    }

    public class JsoupAsyncTask extends AsyncTask<String, Void, Map<String, String>> {
        private final String TAG = JsoupAsyncTask.class.getSimpleName();
        private String messageId = null;

        @Override
        protected Map<String, String> doInBackground(String... strings) {
            String guessedUrl = URLUtil.guessUrl(strings[0]);
            messageId = strings[1];
            Document doc = null;
            try {
                Log.d(TAG, "first URL is: " + guessedUrl);
                doc = Jsoup.connect(guessedUrl).get();
            } catch (IOException e) {
                guessedUrl = guessedUrl.replace("http", "https");
                Log.d(TAG, "second URL after http replaced with https is: " + guessedUrl);
                try {
                    doc = Jsoup.connect(guessedUrl).get();
                } catch (IOException error) {
                    error.printStackTrace();
                }
                e.printStackTrace();
            }
            if (doc != null) {
                Log.d(TAG, doc.title());
                Boolean hasSchemaThing = false;
                if (doc.attr("itemprop") != null) {
                    hasSchemaThing = true;
                }
                Element headElement = doc.head();
                Elements metaElements = headElement.getElementsByAttribute("property");
                Log.d(TAG, "all meta elements: " + metaElements.toString());
                Map<String, String> ogTags = new HashMap<>();
                for (Element e : metaElements) {
                    if (e.attributes().get("property").matches(OG_REGEX) ||
                            e.attributes().get("Property").matches(OG_REGEX)) {
                        ogTags.put(e.attr("property"), e.attr("content"));
                        Log.d(TAG, e.attr("content"));
                    }
                }
                Log.d(TAG, "right before return: " + ogTags.toString());
                return ogTags;
            } else {
                return null;
            }

        }

        @Override
        protected void onPostExecute(Map<String, String> s) {
            if (s != null) {
                Log.d(TAG, "Map in onPostExecute: " + s.toString());
                groupRef.collection("messages").document(messageId).get().addOnCompleteListener(task -> {
                    if ((task.getResult() != null) && task.isSuccessful()) {
                        Map<String, Object> data;
                        DocumentSnapshot message = task.getResult();
                        data = message.getData();
                        if (!s.isEmpty()) {
                            data.put("messageType", "LINK");
                            if (s.get("og:image") != null) {
                                data.put("linkImage", s.get("og:image"));
                            }
                            if (s.get("og:title") != null) {
                                data.put("linkTitle", s.get("og:title"));
                            }
                            if (s.get("og:description") != null) {
                                data.put("linkDescription", s.get("og:description"));
                            }
                            if (s.get("og:url") != null){
                                data.put("linkUrl", s.get("og:url"));
                            }
                            if (s.get("og:video") != null){
                                data.put("linkVideo", s.get("og:video"));
                            }
                        } else {
                            if (data.get("imageLink") == null){
                                data.put("messageType", "TEXT");
                            }
                        }

                        DocumentReference messageRef = groupRef.collection("messages").document(messageId);
                        messageRef.set(data, SetOptions.merge());
                        Log.d(TAG, "in post execute DB call");
                    }
                });
            }
            super.onPostExecute(s);
        }
    }
}


