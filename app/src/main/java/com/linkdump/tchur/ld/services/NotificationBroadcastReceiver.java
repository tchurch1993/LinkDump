package com.linkdump.tchur.ld.services;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.linkdump.tchur.ld.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.linkdump.tchur.ld.services.MyFirebaseMessagingService.KEY_TEXT_REPLY;

public class NotificationBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("demo", "Context: " + context.getPackageName());
        Log.d("demo", "in onRecieve method");
        CharSequence message = getReplyMessage(intent);
        if (message != null) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String groupId = intent.getStringExtra("groupID");
            String groupReqCode = intent.getStringExtra("reqCode");
            int intGroupReqCode = Integer.parseInt(groupReqCode);
            Log.d("demo", "req code is: " + groupReqCode);
            String userId = mAuth.getCurrentUser().getUid();
            Map<String, Object> data = new HashMap<>();
            data.put("message", message.toString());
            data.put("sentTime", Calendar.getInstance().getTimeInMillis());
            data.put("user", userId);
            db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String fullName;
                        DocumentSnapshot userDoc = task.getResult();
                        String firstName = userDoc.getString("firstName");
                        String lastName = userDoc.getString("lastName");
                        fullName = firstName + " " + lastName;
                        data.put("userName", fullName);
                        Log.d("demo", groupId + "");
                        db.collection("groups").document(groupId).collection("messages").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {

                                    Notification repliedNotification = new NotificationCompat.Builder(context, "Group Chat")
                                            .setSmallIcon(R.drawable.ic_link_dump)
                                            .setColor(context.getResources().getColor(R.color.colorPrimary, context.getTheme()))
                                            .setContentText("Replied")
                                            .build();

                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                    notificationManager.notify(intGroupReqCode, repliedNotification);
                                } else {
                                    Notification repliedNotification = new NotificationCompat.Builder(context, "Group Chat")
                                            .setSmallIcon(R.drawable.ic_link_dump)
                                            .setColor(context.getResources().getColor(R.color.colorPrimary, context.getTheme()))
                                            .setContentText("Reply Failed")
                                            .build();

                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                    notificationManager.notify(intGroupReqCode, repliedNotification);
                                }
                            }
                        });
                    }
                }
            });


        }
    }


    private CharSequence getReplyMessage(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_TEXT_REPLY);
        }
        return null;
    }
}
