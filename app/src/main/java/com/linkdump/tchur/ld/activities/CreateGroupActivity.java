//package com.linkdump.tchur.ld.activities;
//
//import android.os.Bundle;
//
//import androidx.appcompat.app.AppCompatActivity;
//import android.util.Log;
//import android.widget.Toast;
//
////import com.algolia.instantsearch.ui.helpers.InstantSearch;
////import com.algolia.search.saas.Client;
////import com.algolia.search.saas.Index;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.SetOptions;
//import com.google.firebase.firestore.Transaction;
//import com.linkdump.tchur.ld.R;
//import com.pchmn.materialchips.ChipsInput;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
//public class CreateGroupActivity extends AppCompatActivity {
//    private FirebaseFirestore db;
//    private FirebaseAuth mAuth;
//    private List<String> members;
//    private List<String> memberEmails;
//    private CollectionReference usersRef;
//    //private InstantSearch helper;
//    private Map<String, Object> data;
//    private String algoliaAPIKey, algoliaAPPId, algoliaIndex;
//    private ChipsInput searchBox;
//    //private Client client;
//    //private Index index;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setTheme(R.style.LinkDumpDark);
//        setContentView(R.layout.activity_create_group);
//        db = FirebaseFirestore.getInstance();
//        mAuth = FirebaseAuth.getInstance();
//        searchBox = findViewById(R.id.userSearchBox);
//        usersRef = db.collection("users");
//        memberEmails = new ArrayList<>();
//        members = new ArrayList<>();
//        data = new HashMap<>();
//        setupAlgolia();
//    }
//
//    private void setupAlgolia() {
//        db.collection("secrets").document("algolia").get().addOnCompleteListener(task -> {
//            DocumentSnapshot doc = task.getResult();
//            if (task.isSuccessful()) {
//                algoliaAPIKey = doc.getString("apiKey");
//                algoliaAPPId = doc.getString("applicationId");
//                algoliaIndex = doc.getString("index");
//                //helper.search();
//            }
//
////            index = client.getIndex("users");
////            JSONObject settings = new JSONObject();
////            try {
////                settings.put("searchableAttributes", "firstName")
////                        .put("searchableAttributes", "lastName")
////                        .put("searchableAttributes", "email");
////            } catch (JSONException e) {
////                e.printStackTrace();
////            }
////            index.setSettingsAsync(settings, null);
//
//        });
//    }
//
//    public void addGroupToDB(final List<String> memberIDs) {
//        memberIDs.add(mAuth.getUid());
//        data.put("members", memberIDs);
//        Random rand = new Random();
//        int reqCode = rand.nextInt();
//        data.put("groupReqCode", reqCode);
//        db.collection("groups").add(data).addOnCompleteListener(task -> db.runTransaction((Transaction.Function<Void>) transaction -> {
//            if (task.isSuccessful()) {
//
//                ArrayList<DocumentSnapshot> memberSnapshots = new ArrayList<>();
//                for (String memberID : memberIDs) {
//                    memberSnapshots.add(transaction.get(usersRef.document(memberID)));
//                }
//                for (int i = 0; i < memberIDs.size(); i++) {
//                    List<String> memberGroups;
//                    if (memberSnapshots.get(i).contains("groups")) {
//                        memberGroups = (List<String>) memberSnapshots.get(i).get("groups");
//                        memberGroups.add(task.getResult().getId());
//                        transaction.update(usersRef.document(memberIDs.get(i)), "groups", memberGroups);
//                    } else {
//                        memberGroups = new ArrayList<>();
//                        memberGroups.add(task.getResult().getId());
//                        Log.d("demo", memberIDs.get(i));
//                        Map<String, Object> memberData;
//                        memberData = memberSnapshots.get(i).getData();
//                        memberData.put("groups", memberGroups);
//                        transaction.set(usersRef.document(memberIDs.get(i)), memberData, SetOptions.merge());
//                    }
//                }
//            }
//
//            return null;
//        }).addOnFailureListener(e -> {
//            Toast.makeText(CreateGroupActivity.this, "Failed to add group", Toast.LENGTH_SHORT).show();
//            Log.w("demo", "Transaction Failure.", e);
//        }));
//    }
//
//    public void checkMemberInDB(final List<String> memberEmail) {
//        db.collection("users").get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                for (DocumentSnapshot document : task.getResult()) {
//                    String email = document.getString("email");
//                    for (String mEmail : memberEmail) {
//                        if (email.equals(mEmail)) {
//                            String userID = document.getId();
//                            addMemberToList(userID);
//                        }
//                    }
//                }
//                addGroupToDB(members);
//            }
//        });
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    public void addMemberToList(String userID) {
//        members.add(userID);
//    }
//}
