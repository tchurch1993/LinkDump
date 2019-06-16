package com.linkdump.tchur.ld.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.linkdump.tchur.ld.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CreateGroupActivity extends AppCompatActivity {


    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private List<String> members;
    private List<String> memberEmails;

    private CollectionReference usersRef;
    private Map<String, Object> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTheme(R.style.LinkDumpDark);
        setContentView(R.layout.activity_create_group);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        usersRef = db.collection("users");


        memberEmails = new ArrayList<>();
        members = new ArrayList<>();
        data = new HashMap<>();

        Button button = findViewById(R.id.createGroupButton);




        final EditText groupName = findViewById(R.id.groupNameEditText);
        final EditText member1 = findViewById(R.id.groupMemberEditText1);
        final EditText member2 = findViewById(R.id.groupMemberEditText2);
        final EditText member3 = findViewById(R.id.groupMemberEditText3);
        final EditText member4 = findViewById(R.id.groupMemberEditText4);
        final EditText member5 = findViewById(R.id.groupMemberEditText5);






        button.setOnClickListener(v -> {

            if (!groupName.getText().toString().isEmpty()) {
                data.put("groupName", groupName.getText() + "");
                data.put("owner", mAuth.getUid());
                if (!member1.getText().toString().isEmpty()) {
                    memberEmails.add(member1.getText() + "");
                }
                if (!member2.getText().toString().isEmpty()) {
                    memberEmails.add(member2.getText() + "");
                }
                if (!member3.getText().toString().isEmpty()) {
                    memberEmails.add(member3.getText() + "");
                }
                if (!member4.getText().toString().isEmpty()) {
                    memberEmails.add(member4.getText() + "");
                }
                if (!member5.getText().toString().isEmpty()) {
                    memberEmails.add(member5.getText() + "");
                }
                if (memberEmails.size() > 0) {
                    checkMemberInDB(memberEmails);
                    finish();
                } else {
                    Toast.makeText(CreateGroupActivity.this, "Please Input a Group Member", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CreateGroupActivity.this, "Please Input a Group Name", Toast.LENGTH_SHORT).show();
            }


        });



    }






    public void addGroupToDB(final List<String> memberIDs) {
        memberIDs.add(mAuth.getUid());
        data.put("members", memberIDs);
        Random rand = new Random();
        int reqCode = rand.nextInt();
        data.put("groupReqCode", reqCode);
        db.collection("groups").add(data).addOnCompleteListener(task -> db.runTransaction((Transaction.Function<Void>) transaction -> {
            if (task.isSuccessful()) {

                ArrayList<DocumentSnapshot> memberSnapshots = new ArrayList<>();
                for (String memberID : memberIDs) {
                    memberSnapshots.add(transaction.get(usersRef.document(memberID)));
                }
                for (int i = 0; i < memberIDs.size(); i++) {
                    List<String> memberGroups;
                    if (memberSnapshots.get(i).contains("groups")) {
                        memberGroups = (List<String>) memberSnapshots.get(i).get("groups");
                        memberGroups.add(task.getResult().getId());
                        transaction.update(usersRef.document(memberIDs.get(i)), "groups", memberGroups);
                    } else {
                        memberGroups = new ArrayList<>();
                        memberGroups.add(task.getResult().getId());
                        Log.d("demo", memberIDs.get(i));
                        Map<String, Object> memberData;
                        memberData = memberSnapshots.get(i).getData();
                        memberData.put("groups", memberGroups);
                        transaction.set(usersRef.document(memberIDs.get(i)), memberData, SetOptions.merge());
                    }
                }
            }

            return null;
        }).addOnFailureListener(e -> {
            Toast.makeText(CreateGroupActivity.this, "Failed to add group", Toast.LENGTH_SHORT).show();
            Log.w("demo", "Transaction Failure.", e);
        }));
    }






    public void checkMemberInDB(final List<String> memberEmail) {
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    String email = document.getString("email");
                    for (String mEmail : memberEmail) {
                        if (email.equals(mEmail)) {
                            String userID = document.getId();
                            addMemberToList(userID);
                        }
                    }
                }
                addGroupToDB(members);
            }
        });
    }

    public void subscribeAllMembers(List<String> memberIds)
    {

    }

    public void addMemberToList(String userID) {
        members.add(userID);
    }
}
