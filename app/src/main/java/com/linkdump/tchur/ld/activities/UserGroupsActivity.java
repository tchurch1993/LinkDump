package com.linkdump.tchur.ld.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.linkdump.tchur.ld.R;
import com.linkdump.tchur.ld.adapters.GroupNameAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserGroupsActivity extends AppCompatActivity implements GroupNameAdapter.ItemClickListener{
    DocumentReference userRef;
    CollectionReference groupsRef;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    List<String> userGroups;
    List<String> groupIDs;
    RecyclerView mRecyclerView;
    GroupNameAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_groups);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users").document(mAuth.getUid());
        groupsRef = db.collection("groups");
        userGroups = new ArrayList<>();
        groupIDs = new ArrayList<>();
        getGroupIDs();

        mRecyclerView = findViewById(R.id.groupNameRecyclerView);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new GroupNameAdapter(this, userGroups);
        adapter.setClickListener(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(adapter);
    }

    public void getGroupIDs(){
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if (task.getResult().exists()) {
                    List<String> tempGroupIDs;
                    tempGroupIDs = (List<String>) task.getResult().get("groups");
                    getGroupNames(tempGroupIDs);
                }
            }
        });
    }

    public void getGroupNames(final List<String> mGroupIDs){
        groupsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (DocumentSnapshot document : task.getResult()){
                    String mID = document.getId();
                    for (String id : mGroupIDs){
                        if (mID.equals(id)){
                            groupIDs.add(document.getId());
                            Log.d("demo", "Inside GroupNames: " + mID);
                            userGroups.add((String) document.get("groupName"));

                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        for (String mID : groupIDs){
            Log.d("demo", "Group IDs: " + mID);
        }
        Intent intent = new Intent(UserGroupsActivity.this, ChatActivity.class);
        intent.putExtra("groupID", groupIDs.get(position));
        startActivity(intent);
    }
}
