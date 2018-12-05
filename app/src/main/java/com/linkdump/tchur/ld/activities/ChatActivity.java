package com.linkdump.tchur.ld.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.linkdump.tchur.ld.R;
import com.linkdump.tchur.ld.adapters.GroupChatAdapter;
import com.linkdump.tchur.ld.adapters.MyRecyclerViewAdapter;
import com.linkdump.tchur.ld.objects.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

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

        Button sendButton = findViewById(R.id.chat_send_button);
        final EditText chatEditText = findViewById(R.id.chat_message_edit_text);
        sendButton.setOnClickListener(v -> {
            if (!chatEditText.getText().equals("")) {
                Map<String, Object> sendMessage = new HashMap<>();
                sendMessage.put("message", chatEditText.getText() + "");
                sendMessage.put("user", mAuth.getUid());
                sendMessage.put("userName", mAuth.getCurrentUser().getDisplayName());
                sendMessage.put("sentTime", Calendar.getInstance().getTimeInMillis());
                db.collection("groups").document(currentGroup)
                        .collection("messages").add(sendMessage);
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
