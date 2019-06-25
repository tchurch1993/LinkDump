package com.linkdump.tchur.ld.api;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.linkdump.tchur.ld.activities.CreateGroupActivity;
import com.linkdump.tchur.ld.api.parent.Manager;
import com.linkdump.tchur.ld.constants.FirebaseConstants;
import com.linkdump.tchur.ld.objects.Group;
import com.linkdump.tchur.ld.objects.User;
import com.linkdump.tchur.ld.persistence.FirebaseDbContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

public class GroupManager extends Manager {


     public GroupManager(FirebaseDbContext db) {
        super(db);

    }


    public Group GetGroup(String ref) {


        return new Group();
    }

    public Group GetGroupById(int id) {


        return null;
    }

    public List<Group> GetGroups(int limit, Predicate<String> filter) {
        List<Group> groups;


        return null;
    }


    public void CreateGroup(final List<String> memberIDs) {

        Map<String, Object> data = new HashMap<>();
        memberIDs.add(firebaseDbContext.getAuth().getUid());

        data.put("members", memberIDs);

        Random rand = new Random();
        int reqCode = rand.nextInt();
        data.put("groupReqCode", reqCode);


        firebaseDbContext.getDb().collection(FirebaseConstants.GROUPS)
                .add(data).addOnCompleteListener(task ->


                firebaseDbContext.getDb()
                        .runTransaction((Transaction.Function<Void>) transaction ->
                        {
                            if (task.isSuccessful()) {

                                ArrayList<DocumentSnapshot> memberSnapshots = new ArrayList<>();

                                for (String memberID : memberIDs) {
                                    memberSnapshots.add(transaction.get(firebaseDbContext.usersRef.document(memberID)));
                                }

                                for (int i = 0; i < memberIDs.size(); i++) {

                                    List<String> memberGroups;

                                    if (memberSnapshots.get(i).contains("groups")) {
                                        memberGroups = (List<String>) memberSnapshots.get(i).get("groups");
                                        memberGroups.add(task.getResult().getId());
                                        transaction.update(firebaseDbContext.usersRef.document(memberIDs.get(i)), "groups", memberGroups);

                                    } else {

                                        memberGroups = new ArrayList<>();
                                        memberGroups.add(task.getResult().getId());
                                        Log.d("demo", memberIDs.get(i));
                                        Map<String, Object> memberData;
                                        memberData = memberSnapshots.get(i).getData();
                                        memberData.put("groups", memberGroups);
                                        transaction.set(firebaseDbContext.usersRef.document(memberIDs.get(i)), memberData, SetOptions.merge());
                                    }
                                }

                            }

                            return null;
                        })
                        .addOnFailureListener(e -> {

                        }));
    }


    public Group CreateGroup(Group groups) {


        // Map<String, Object> mUser = new HashMap<>();
        //mUser.put("firstName", user.firstName);
        // mUser.put("lastName", user.lastName);
        //  mUser.put("email", user.email);


        return null;
    }


    public Group UpdateGroup(Group user) {

        return null;
    }

    public boolean DeleteGroup(Group group) {


        return false;
    }


    public boolean DeleteGroup(int id) {


        return false;
    }

}
