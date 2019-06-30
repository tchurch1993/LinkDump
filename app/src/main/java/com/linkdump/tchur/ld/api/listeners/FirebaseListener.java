package com.linkdump.tchur.ld.api.listeners;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.linkdump.tchur.ld.abstractions.listeners.OnDocumentChangedCompletedListener;
import com.linkdump.tchur.ld.abstractions.listeners.OnDocumentChangedListener;
import com.linkdump.tchur.ld.constants.FirebaseConstants;
import com.linkdump.tchur.ld.objects.Message;
import com.linkdump.tchur.ld.persistence.FirebaseDbContext;

import java.util.Collections;
import java.util.EventListener;
import java.util.Objects;

public class FirebaseListener implements EventListener {



   protected FirebaseDbContext firebaseDbContext;

   protected OnDocumentChangedCompletedListener onDocumentChangeCompletedListener;
   protected OnDocumentChangedListener onDocumentChangedListener;

   protected ListenerConfig config;


   public FirebaseListener(FirebaseDbContext firebaseDbContext){
          this.firebaseDbContext = firebaseDbContext;
   }


   public FirebaseListener setOnDocumentChangedListner(OnDocumentChangedListener listener){
        this.onDocumentChangedListener = listener;
        return this;
   }


   public FirebaseListener setOnDocumentChangeCompletedListener(OnDocumentChangedCompletedListener listener){
       this.onDocumentChangeCompletedListener = listener;
       return this;
   }




   public FirebaseListener SetListener(String documentReference){
       firebaseDbContext.getDb()
               .collection(FirebaseConstants.GROUPS)
               .document(documentReference)
               .collection(FirebaseConstants.MESSAGES)
               .orderBy("sentTime", Query.Direction.DESCENDING)
               .limit(config.limit)
               .addSnapshotListener((queryDocumentSnapshots, e) ->
               {


                   if (e != null)
                   {
                       Log.w("demo", "Listener Failed", e);
                       return;
                   }



                   for (DocumentChange doc : Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges())
                   {


                       onDocumentChangedListener.OnDocumentChanged(doc);

                       //ALL Context dependent objects go here
                       QueryDocumentSnapshot mDoc = doc.getDocument();


                       Log.d("demo", mDoc.get("message") + "");


                       Message tempMessage = mDoc.toObject(Message.class);



                       if (!tempMessage.getUser().equals(firebaseDbContext.getAuth().getUid())) {
                           tempMessage.setIsUser(false);
                       } else {
                           tempMessage.setIsUser(true);
                       }

                       boolean exists = false;

                       for (int i = 0; i < firebaseDbContext.getMessages().size(); i++)
                       {
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


                   //ALL INFORMATION NEEDED TO UPDATE UI GOES HERE
                   onDocumentChangeCompletedListener.OnDocumentCompleted();

                   Collections.sort(firebaseDbContext.getMessages());
                   //chatViewCoordinator.adapter.notifyDataSetChanged();
                  // chatViewCoordinator.mLayoutManager.scrollToPosition(firebaseDbContext.getMessages().size() - 1);

               });
       return this;
   }




}
