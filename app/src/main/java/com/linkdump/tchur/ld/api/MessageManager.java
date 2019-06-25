package com.linkdump.tchur.ld.api;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.linkdump.tchur.ld.constants.FirebaseConstants;
import com.linkdump.tchur.ld.objects.Message;
import com.linkdump.tchur.ld.objects.User;
import com.linkdump.tchur.ld.persistence.FirebaseDbContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class MessageManager {


    private FirebaseDbContext db;

    private OnSuccessListener successListener;
    private OnFailureListener failureListener;
    public MessageManager(FirebaseDbContext db) {
        this.db = db;
    }


    public Message GetMessage() {

        return new Message();
    }


    public List<Message> GetMessages(int limit, String currentGroup, Predicate<String> filter) {

        final List<Message> messages;

        db.getDb().collection(FirebaseConstants.GROUPS)
                .document(currentGroup)
                .collection(FirebaseConstants.MESSAGES)
                .orderBy("sentTime", Query.Direction.DESCENDING)
                .limit(25)
                .addSnapshotListener((queryDocumentSnapshots, e) ->
                {
                    if (e != null) {
                        Log.w("demo", "Listener Failed", e);
                        return;
                    }

                    for (DocumentChange doc : Objects.requireNonNull(queryDocumentSnapshots)
                            .getDocumentChanges()) {

                        QueryDocumentSnapshot mDoc = doc.getDocument();
                        Log.d("demo", mDoc.get("message") + "");
                        Message tempMessage = mDoc.toObject(Message.class);


                        //Map Messages to info here
                        if (!tempMessage.getUser().equals(db.getAuth().getUid())) {
                            tempMessage.setIsUser(false);
                        } else {
                            tempMessage.setIsUser(true);
                        }

                        boolean exists = false;

                        for (int i = 0; i < db.getMessages().size(); i++) {
                            if (db.getMessages().get(i).getSentTime() == tempMessage.getSentTime()) {
                                db.getMessages().set(i, tempMessage);
                                exists = true;
                            }
                        }

                        if (!exists) {
                            db.getMessages().add(tempMessage);
                            db.getEvents().add(mDoc.getString("message"));
                        }
                    }

                    Collections.sort(db.getMessages());
                 });


        return db.getMessages();
    }


    public Message CreateMessage(Message message) {

        // Map<String, Object> mUser = new HashMap<>();
        // mUser.put("firstName", user.firstName);
        // mUser.put("lastName", user.lastName);
        // mUser.put("email", user.email);

      return null;
    }


    public Message UpdateMessage(Message message) {


        return null;
    }

    public boolean DeleteMessage(Message message) {


        return false;
    }


    public boolean DeleteMessage(int id) {


        return false;
    }


}
