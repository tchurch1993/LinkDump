package com.linkdump.tchur.ld.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.linkdump.tchur.ld.adapters.GroupNameAdapter;
import com.linkdump.tchur.ld.api.GroupManager;
import com.linkdump.tchur.ld.constants.FirebaseConstants;
import com.linkdump.tchur.ld.objects.Group;
import com.linkdump.tchur.ld.objects.User;
import com.linkdump.tchur.ld.persistence.FirebaseDbContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;


public class CreateGroupActivity extends AppCompatActivity implements Button.OnClickListener {

    /* Much of this class could be put into an API of sorts to limit the amount of network calls and logic on the users device */

    private FirebaseDbContext firebaseDbContext;
    private GroupManager groupManager;
    private Map<String, Object> data;

    Button button;
    EditText groupName;
    EditText member1;
    EditText member2;
    EditText member3;
    EditText member4;
    EditText member5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDbContext = new FirebaseDbContext(getApplicationContext());
        groupManager = new GroupManager(firebaseDbContext);

        setTheme(R.style.LinkDumpDark);
        setContentView(R.layout.activity_create_group);

        data = new HashMap<>();


        button = findViewById(R.id.createGroupButton);
        button.setOnClickListener(this);

        groupName = findViewById(R.id.groupNameEditText);
        member1 = findViewById(R.id.groupMemberEditText1);
        member2 = findViewById(R.id.groupMemberEditText2);
        member3 = findViewById(R.id.groupMemberEditText3);
        member4 = findViewById(R.id.groupMemberEditText4);
        member5 = findViewById(R.id.groupMemberEditText5);
    }


    public void addGroupToDB(final List<String> memberIDs) {

        memberIDs.add(firebaseDbContext.getAuth().getUid());

        data.put("members", memberIDs);

        Random rand = new Random();
        int reqCode = rand.nextInt();
        data.put("groupReqCode", reqCode);


        firebaseDbContext
                .getDb()
                .collection(FirebaseConstants.GROUPS)
                .add(data)
                .addOnCompleteListener(task ->

                        firebaseDbContext.getDb()
                                         .runTransaction((Transaction.Function<Void>) transaction ->
                        {
                            if (task.isSuccessful()) {

                                ArrayList<DocumentSnapshot> memberSnapshots = new ArrayList<>();
                                for (String memberID : memberIDs) {
                                    memberSnapshots.add(transaction.get(firebaseDbContext
                                                                   .getUserRef()
                                                                   .document(memberID)));
                                }
                                for (int i = 0; i < memberIDs.size(); i++) {
                                    List<String> memberGroups;
                                    if (memberSnapshots.get(i).contains("groups")) {
                                        memberGroups = (List<String>) memberSnapshots.get(i).get("groups");
                                        memberGroups.add(task.getResult().getId());

                                        transaction.update(firebaseDbContext
                                                   .getUserRef()
                                                   .document(memberIDs.get(i)), "groups", memberGroups);
                                    } else
                                        {
                                        memberGroups = new ArrayList<>();
                                        memberGroups.add(task.getResult().getId());
                                        Log.d("demo", memberIDs.get(i));
                                        Map<String, Object> memberData;
                                        memberData = memberSnapshots.get(i).getData();
                                        memberData.put("groups", memberGroups);

                                        transaction.set(firebaseDbContext
                                                   .getUserRef()
                                                   .document(memberIDs.get(i)), memberData, SetOptions.merge());
                                    }
                                }
                            }


                        }).addOnFailureListener(e -> {
                            Toast.makeText(CreateGroupActivity.this, "Failed to add group", Toast.LENGTH_SHORT).show();
                            Log.w("demo", "Transaction Failure.", e);
                        }));
    }


    public void checkMemberInDB(final List<String> memberEmail) {


        firebaseDbContext.getDb()
                .collection(FirebaseConstants.USERS)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            String email = document.getString("email");
                            for (String mEmail : memberEmail) {
                                if (email.equals(mEmail)) {
                                    String userID = document.getId();
                                    addMemberToList(userID);
                                }
                            }
                        }

                        // GroupManager.Create(firebaseDbContext)
                        //   .CreateGroup(members)
                        // .OnSuccess(t -> { updateUi(t)})
                        //  .OnFailure(t-> {updateui(ex});

                        addGroupToDB(firebaseDbContext.getMembers());
                    }
                });
    }

    public void subscribeAllMembers(List<String> memberIds) {


    }


    public void addMemberToList(String userID) {
        firebaseDbContext.getMembers().add(userID);
    }


    @Override
    public void onClick(View v) {


        if (!groupName.getText().toString().isEmpty()) {
            data.put("groupName", groupName.getText() + "");
            data.put("owner", firebaseDbContext.getAuth().getUid());
            if (!member1.getText().toString().isEmpty()) {
                firebaseDbContext.getMembers().add(member1.getText() + "");
            }
            if (!member2.getText().toString().isEmpty()) {
                firebaseDbContext.getMembers().add(member2.getText() + "");
            }
            if (!member3.getText().toString().isEmpty()) {
                firebaseDbContext.getMembers().add(member3.getText() + "");
            }
            if (!member4.getText().toString().isEmpty()) {
                firebaseDbContext.getMembers().add(member4.getText() + "");
            }
            if (!member5.getText().toString().isEmpty()) {
                firebaseDbContext.getMembers().add(member5.getText() + "");
            }
            if (firebaseDbContext.getMemberEmails().size() > 0) {
                checkMemberInDB(firebaseDbContext.getMemberEmails());
                GroupManager.Create(firebaseDbContext).checkMemberInDB(firebaseDbContext.getMemberEmails());
                finish();
            } else {
                Toast.makeText(CreateGroupActivity.this, "Please Input a Group Member", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(CreateGroupActivity.this, "Please Input a Group Name", Toast.LENGTH_SHORT).show();
        }


    }
}
