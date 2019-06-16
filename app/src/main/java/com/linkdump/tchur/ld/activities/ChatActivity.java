package com.linkdump.tchur.ld.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageButton;


import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.linkdump.tchur.ld.R;
import com.linkdump.tchur.ld.abstractions.IActivityContainer;
import com.linkdump.tchur.ld.data.ChatActivityContainer;
import com.linkdump.tchur.ld.ui.ChatViewCoordinator;
import com.linkdump.tchur.ld.adapters.GroupChatAdapter;
import com.linkdump.tchur.ld.adapters.IntentAdapter;
import com.linkdump.tchur.ld.adapters.NewGroupChatAdapter;
import com.linkdump.tchur.ld.objects.Message;
import com.linkdump.tchur.ld.persistence.FirebaseDbContext;
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
import java.util.Objects;

public class ChatActivity extends AppCompatActivity implements GroupChatAdapter.ItemClickListener,
                                                                NewGroupChatAdapter.ItemClickListener,
                                                                MyEditText.KeyBoardInputCallbackListener,
                                                                ImageButton.OnClickListener,
                                                                MyEditText.OnKeyListener {


    //Data
    private ChatActivityContainer chatActivityContainer;

    //Config
    private SharedPreferences prefs;

    //Persistence
    private FirebaseDbContext firebaseDbContext;

    //Ui
    private ChatViewCoordinator chatViewCoordinator;

    //EventHandlers/Logic
    //TO BE IMPLEMENTED TYLOR, DAMN STOP RUSHING ME


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        chatViewCoordinator = new ChatViewCoordinator(getApplicationContext(), this);
        firebaseDbContext = new FirebaseDbContext(getApplicationContext());
        chatActivityContainer = new ChatActivityContainer(getApplicationContext());

        chatViewCoordinator.initialiseViewFromXml(R.layout.activity_chat);
        setContentView(chatViewCoordinator.getRootView());

        clearNotifications();

       /* IntentAdapter.Begin()
                .SetIntent(getIntent())
                .GetFromExtra(chatActivityContainer.getCurrentGroup(), (i) -> i.getStringExtra("groupID"))
                .GetFromExtra(chatActivityContainer.getGroupName(), (i) -> i.getStringExtra("groupName"))
                .Deserialize();*/



        Intent intent = getIntent();
        chatActivityContainer.setCurrentGroup(intent.getStringExtra("groupID"));
        chatActivityContainer.setCurrentGroup(intent.getStringExtra("groupName"));

        prefs = this.getSharedPreferences(getPackageName(), MODE_PRIVATE);
        prefs.edit().putString("currentGroup",chatActivityContainer.getCurrentGroup()).apply();


        groupChatListener(chatActivityContainer.getCurrentGroup());

        chatViewCoordinator.chatEditText.setKeyBoardInputCallbackListener(this);
        chatViewCoordinator.chatEditText.setOnKeyListener(this);
        chatViewCoordinator.imageButton.setOnClickListener(this);

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }





    public void groupChatListener(String currentGroup) {
        firebaseDbContext.getDb().collection("groups")
                .document(currentGroup)
                .collection("messages")
                .orderBy("sentTime", Query.Direction.DESCENDING)
                .limit(25)
                .addSnapshotListener((queryDocumentSnapshots, e) ->
                {
            if (e != null)
            {
                Log.w("demo", "Listener Failed", e);
                return;
            }

            for (DocumentChange doc : Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges()) {
                QueryDocumentSnapshot mDoc = doc.getDocument();
                Log.d("demo", mDoc.get("message") + "");
                Message tempMessage = mDoc.toObject(Message.class);
                if (!tempMessage.getUser().equals(firebaseDbContext.getAuth().getUid())) {
                    tempMessage.setIsUser(false);
                } else {
                    tempMessage.setIsUser(true);
                }
                boolean exists = false;
                for (int i = 0; i < firebaseDbContext.getMessages().size(); i++) {
                    if (firebaseDbContext.getMessages().get(i).getSentTime() == tempMessage.getSentTime()) {
                        firebaseDbContext.getMessages().set(i, tempMessage);
                        exists = true;
                    }
                }
                if (!exists) {
                    firebaseDbContext.getMessages().add(tempMessage);
                    firebaseDbContext.getEvents().add(mDoc.getString("message"));
                }
            }



            Collections.sort(firebaseDbContext.getMessages());
            chatViewCoordinator.adapter.notifyDataSetChanged();
//            adapter.notifyItemInserted(adapter.getItemCount() - 1);
            chatViewCoordinator.mLayoutManager.scrollToPosition(firebaseDbContext.getMessages().size() - 1);
        });
    }




    @Override
    public void onItemClick(View view, int position) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        prefs.edit().putString("currentGroup", ChatActivityContainer.getTAG()).apply();
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

    @Override
    public void onCommitContent(InputContentInfoCompat inputContentInfo, int flags, Bundle opts) {

        if (inputContentInfo.getLinkUri() != null) {
            Log.d(ChatActivityContainer.getTAG(), String.valueOf(inputContentInfo.getLinkUri()));

            Map<String, Object> sendMessage = new HashMap<>();

            sendMessage.put("user", Objects.requireNonNull(firebaseDbContext.getAuth().getUid()));
            sendMessage.put("userName", firebaseDbContext.getAuth().getCurrentUser().getDisplayName());
            sendMessage.put("sentTime", Calendar.getInstance().getTimeInMillis());
            sendMessage.put("imageUrl", inputContentInfo.getLinkUri().toString());
            sendMessage.put("messageType", "IMAGE");

            firebaseDbContext.groupRef.collection("messages").add(sendMessage);
        }
    }

    @Override
    public void onClick(View v) {
        if (!chatViewCoordinator.chatEditText.getText().toString().equals("")) {
            Boolean hasLink = false;
            String url = "";
            UrlDetector detector = new UrlDetector(chatViewCoordinator.chatEditText.getText().toString(), UrlDetectorOptions.Default);
            List<Url> urls = detector.detect();
            if (!urls.isEmpty()) {
                hasLink = true;
                url = urls.get(0).getFullUrl();
            }
            Map<String, Object> sendMessage = new HashMap<>();
            sendMessage.put("message", chatViewCoordinator.chatEditText.getText() + "");
            sendMessage.put("user", Objects.requireNonNull(firebaseDbContext.getAuth().getUid()));
            sendMessage.put("userName", firebaseDbContext.getAuth().getCurrentUser().getDisplayName());
            sendMessage.put("sentTime", Calendar.getInstance().getTimeInMillis());
            sendMessage.put("messageType", "TEXT");

            Boolean finalHasLink = hasLink;
            String finalUrl = url;
            Log.d(ChatActivityContainer.getTAG(), "url before message push: " + finalUrl);
            Log.d(ChatActivityContainer.getTAG(), "before database push");

            firebaseDbContext.groupRef.collection("messages").add(sendMessage).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(ChatActivityContainer.getTAG(), "Successfully pushed");
                    DocumentReference messageRef = task.getResult();
                    if (finalHasLink) {
                        Log.d(ChatActivityContainer.getTAG(), "found Link in text");
                        new JsoupAsyncTask().execute(finalUrl, messageRef.getId());
                    }
                }
            });
            chatViewCoordinator.chatEditText.getText().clear();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    chatViewCoordinator.imageButton.callOnClick();
                    return true;
                default:
                    break;
            }
        }
        return false;
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

                if (doc.attr("itemprop") != null)
                {
                    hasSchemaThing = true;
                }
                Element headElement = doc.head();
                Elements metaElements = headElement.getElementsByAttribute("property");
                Log.d(TAG, "all meta elements: " + metaElements.toString());
                Map<String, String> ogTags = new HashMap<>();
                for (Element e : metaElements) {
                    if (e.attributes().get("property").matches(ChatActivityContainer.getOgRegex()) ||
                            e.attributes().get("Property").matches(ChatActivityContainer.getOgRegex())) {
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
                firebaseDbContext.groupRef.collection("messages")
                                          .document(messageId)
                                          .get()
                                          .addOnCompleteListener(task ->
                        {
                    if ((task.getResult() != null) && task.isSuccessful()) {
                        Map<String, Object> data;
                        DocumentSnapshot message = task.getResult();
                        data = message.getData();
                        if (!s.isEmpty()) {
                            data.put("messageType", "LINK");
                            if (s.get("og:image") != null)
                            {
                                data.put("linkImage", s.get("og:image"));
                            }
                            if (s.get("og:title") != null)
                            {
                                data.put("linkTitle", s.get("og:title"));
                            }
                            if (s.get("og:description") != null)
                            {
                                data.put("linkDescription", s.get("og:description"));
                            }
                            if (s.get("og:url") != null)
                            {
                                data.put("linkUrl", s.get("og:url"));
                            }
                            if (s.get("og:video") != null)
                            {
                                data.put("linkVideo", s.get("og:video"));
                            }
                        } else {
                            if (data.get("imageLink") == null)
                            {
                                data.put("messageType", "TEXT");
                            }
                        }

                        DocumentReference messageRef = firebaseDbContext.groupRef.collection("messages").document(messageId);
                        messageRef.set(data, SetOptions.merge());
                        Log.d(TAG, "in post execute DB call");
                    }
                });
            }
            super.onPostExecute(s);
        }
    }
}


