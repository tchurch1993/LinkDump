package com.linkdump.tchur.ld.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.linkdump.tchur.ld.R;
import com.linkdump.tchur.ld.adapters.GroupChatAdapter;
import com.linkdump.tchur.ld.objects.Message;
import com.pusher.pushnotifications.PushNotifications;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements GroupChatAdapter.ItemClickListener {

    private RecyclerView mRecyclerView;
    private GroupChatAdapter adapter;
    private ArrayList<String> events;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentGroup;
    private List<Message> messages;
    private DocumentReference userRef;
    private List<String> userGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        PushNotifications.start(this, "02a53d4a-83f9-4e8d-98c7-c7ef7288e445");


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userRef = db.collection("users").document(mAuth.getUid());
        messages = new ArrayList<>();
        Intent intent = getIntent();
        currentGroup = intent.getStringExtra("groupID");

        events = new ArrayList<>();
        userGroups = new ArrayList<>();
        mRecyclerView = findViewById(R.id.chat_recyclerview);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new GroupChatAdapter(this, messages);
        adapter.setClickListener(this);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                mLayoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mLayoutManager.smoothScrollToPosition(mRecyclerView, null, adapter.getItemCount());
            }
        });
        mRecyclerView.setAdapter(adapter);
        groupChatListener(currentGroup);

        ImageButton imageButton = findViewById(R.id.imageButton);
        final EditText chatEditText = findViewById(R.id.chat_message_edit_text);
        imageButton.setOnClickListener(view -> {
            if (!chatEditText.getText().toString().equals("")) {
                Map<String, Object> sendMessage = new HashMap<>();
                sendMessage.put("message", chatEditText.getText() + "");
                sendMessage.put("user", mAuth.getUid());
                sendMessage.put("userName", mAuth.getCurrentUser().getDisplayName());
                sendMessage.put("sentTime", Calendar.getInstance().getTimeInMillis());
                db.collection("groups").document(currentGroup)
                        .collection("messages").add(sendMessage).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                        }
                    }
                });
                chatEditText.setText("");
            }
        });
    }

    public void groupChatListener(String currentGroup) {
        db.collection("groups").document(currentGroup).collection("messages").orderBy("sentTime").limit(100).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w("demo", "Listener Failed", e);
                return;
            }

            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                QueryDocumentSnapshot mDoc = doc.getDocument();
                Log.d("demo", mDoc.get("message") + "");
                Message tempMessage = mDoc.toObject(Message.class);
                if (!tempMessage.getUser().equals(mAuth.getUid())){
                    tempMessage.setIsUser(false);
                } else {
                    tempMessage.setIsUser(true);
                }
                messages.add(tempMessage);
                events.add(mDoc.getString("message"));
            }
            adapter.notifyItemInserted(adapter.getItemCount() - 1);
        });
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
