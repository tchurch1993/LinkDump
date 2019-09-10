package com.linkdump.tchur.ld.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.Person;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.linkdump.tchur.ld.R;
import com.linkdump.tchur.ld.activities.MainActivity;
import com.linkdump.tchur.ld.objects.Message;
import com.linkdump.tchur.ld.utils.MessageHistoryUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.NETWORK_STATS_SERVICE;
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
            Message replyMessage = new Message();
            replyMessage.setMessage((String) message);
            replyMessage.setSentTime(Calendar.getInstance().getTimeInMillis());
            Intent deleteIntent = new Intent(context, DeleteIntentBroadcastReceiver.class);
            deleteIntent.putExtra("groupId", groupId);
            PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context, 0, deleteIntent, 0);

            Intent defaultIntent = new Intent(context, MainActivity.class);
            intent.putExtra("groupID", groupId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent defaultPendingIntent = PendingIntent.getActivity(context, intGroupReqCode /* Request code */, defaultIntent,
                    PendingIntent.FLAG_ONE_SHOT);

            db.collection("users").document(userId).get().addOnCompleteListener(task ->
            {

                if (task.isSuccessful()) {
                    String fullName;
                    DocumentSnapshot userDoc = task.getResult();
                    String firstName = userDoc.getString("firstName");
                    String lastName = userDoc.getString("lastName");
                    fullName = firstName + " " + lastName;
                    data.put("userName", fullName);
                    replyMessage.setUserName(fullName);
                    try {
                        MessageHistoryUtil.groupMessageNotificationHistory(context, groupId, replyMessage);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    Log.d("demo", groupId + "");
                    db.collection("groups").document(groupId).collection("messages").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Person person = new Person.Builder().setName("Me").build();
                                NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(person);
                                try {
                                    ArrayList<NotificationCompat.MessagingStyle.Message> messages = MessageHistoryUtil.convertToMessagesCompat(context, groupId);

                                    for (NotificationCompat.MessagingStyle.Message m : messages) {
                                        messagingStyle.addMessage(m);
                                    }
                                } catch (IOException | ClassNotFoundException e) {
                                    e.printStackTrace();
                                }

                                Notification repliedNotification = new NotificationCompat.Builder(context, "Group Chat")
                                        .setSmallIcon(R.drawable.ic_link_dump)
                                        .setColor(context.getResources().getColor(R.color.colorPrimary, context.getTheme()))
                                        .setContentText("Replied")
                                        .setContentIntent(defaultPendingIntent)
                                        .setDeleteIntent(deletePendingIntent)
                                        .setStyle(messagingStyle)
                                        .build();

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                notificationManager.notify(intGroupReqCode, repliedNotification);
                            } else {
                                Notification repliedNotification = new NotificationCompat.Builder(context, "Group Chat")
                                        .setSmallIcon(R.drawable.ic_link_dump)
                                        .setColor(context.getResources().getColor(R.color.colorPrimary, context.getTheme()))
                                        .setContentIntent(defaultPendingIntent)
                                        .setDeleteIntent(deletePendingIntent)
                                        .setContentText("Reply Failed")
                                        .build();

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                notificationManager.notify(intGroupReqCode, repliedNotification);
                            }
                        }
                    });
                } else
                    {
                    Notification repliedNotification = new NotificationCompat.Builder(context, "Group Chat")
                            .setSmallIcon(R.drawable.ic_link_dump)
                            .setColor(context.getResources().getColor(R.color.colorPrimary, context.getTheme()))
                            .setContentIntent(defaultPendingIntent)
                            .setDeleteIntent(deletePendingIntent)
                            .setContentText("Reply Failed")
                            .build();

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.notify(intGroupReqCode, repliedNotification);
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
