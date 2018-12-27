

package com.linkdump.tchur.ld.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.Person;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.linkdump.tchur.ld.R;
import com.linkdump.tchur.ld.activities.MainActivity;
import com.linkdump.tchur.ld.objects.Message;
import com.linkdump.tchur.ld.utils.MessageHistoryUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";
    public static final String KEY_TEXT_REPLY = "key_text_reply";
    public static final int NOTIFICATION_ID = 101;
    public String foregroundGroup;
    private SharedPreferences prefs;


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getTo());
        prefs = this.getSharedPreferences(
                getPackageName(), MODE_PRIVATE);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            Map<String, String> data = remoteMessage.getData();

            foregroundGroup = prefs.getString("currentGroup", "NONE");
            if (!foregroundGroup.equals("NONE")) {
                Log.d(TAG, "foregroundGroupId is: " + foregroundGroup);
                Log.d(TAG, "groupId is: " + data.get("groupId"));
                if (foregroundGroup.equals(data.get("groupId"))) {
                    Log.d(TAG, "not showing notification cause in same group as notification");
                    return;
                }
            }
            Log.d(TAG, data.get("senderId") + " : " + mAuth.getCurrentUser().getUid());
            if (data.get("click_action") != null && data.get("click_action").equals("UPDATE")) {
                buildUpdateNotification();
                return;
            }

            if (!data.get("senderId").equals(mAuth.getCurrentUser().getUid())) {
                try {
                    sendGroupChatNotification(data);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            return;
        }
        sendRegistrationToServer(token);
    }


    // [END on_new_token]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        db.collection("users").document(Objects.requireNonNull(mAuth.getUid())).set(data, SetOptions.merge()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "successfully pushed token");
            } else {
                Log.d(TAG, "token failed to add to DB");
            }
        });
        Log.d(TAG, token);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param data FCM message body received.
     */
    private void sendGroupChatNotification(Map<String, String> data) {
        String groupId = data.get("groupId");
        String groupReqCode = data.get("groupReqCode");
        String sentMessage = data.get("message");
        String sender = data.get("sender");
        String title = data.get("title");
        Long sentTime = Long.parseLong(data.get("sentTime"));
        assert groupReqCode != null;
        int intGroupReCode = Integer.parseInt(groupReqCode);
        Log.d(TAG, "req code from FCM: " + groupReqCode);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("groupID", groupId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.d(TAG, "inside sendNotification");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, intGroupReCode /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Group Chat";

        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY).setLabel("Reply").build();

        Person person = new Person.Builder().setName(sender).build();
        NotificationCompat.MessagingStyle.Message message = new NotificationCompat.MessagingStyle.Message(sentMessage, sentTime, person);
        Log.d(TAG, sender);
        Message messageHistory = new Message();
        messageHistory.setMessage(sentMessage);
        messageHistory.setSentTime(sentTime);
        messageHistory.setUserName(sender);
        try {
            MessageHistoryUtil.groupMessageNotificationHistory(this, groupId, messageHistory);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Intent replyIntent = new Intent(this, NotificationBroadcastReceiver.class);
        replyIntent.putExtra("groupID", groupId);
        replyIntent.putExtra("message", message.getExtras());
        replyIntent.putExtra("reqCode", groupReqCode);

        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(this, intGroupReCode, replyIntent, 0);

        Intent deleteIntent = new Intent(this, DeleteIntentBroadcastReceiver.class);
        deleteIntent.putExtra("groupId", groupId);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(this, 0, deleteIntent, 0);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_link_dump, "Reply",
                replyPendingIntent).addRemoteInput(remoteInput).setAllowGeneratedReplies(true).build();

        Person me = new Person.Builder().setName("Me").build();

        NotificationCompat.MessagingStyle messagingStyle =
                new NotificationCompat.MessagingStyle(me);
        messagingStyle.setConversationTitle(title)
                .setGroupConversation(true);
        ArrayList<NotificationCompat.MessagingStyle.Message> messagesHistory = new ArrayList<>();
        try {
            messagesHistory = MessageHistoryUtil.convertToMessagesCompat(this, groupId);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (!messagesHistory.isEmpty()) {
            for (NotificationCompat.MessagingStyle.Message m : messagesHistory) {
                messagingStyle.addMessage(m);
            }
        } else {
            messagingStyle.addMessage(message);
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri sound = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.wednesday);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_link_dump)
                        .setStyle(messagingStyle)
                        .setColor(getResources().getColor(R.color.colorPrimary, getTheme()))
                        .setContentTitle(title)
                        .setContentText(sentMessage)
                        .addAction(action)
                        .setOnlyAlertOnce(true)
                        .setDeleteIntent(deletePendingIntent)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setDefaults(Notification.FLAG_ONLY_ALERT_ONCE)
                        .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "test",
                    NotificationManager.IMPORTANCE_HIGH);
//            AudioAttributes attributes = new AudioAttributes.Builder()
//                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                    .build();
//            channel.setSound(sound, attributes);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(intGroupReCode, notificationBuilder.build());
        Log.d(TAG, "showing notification");
    }

    public void buildUpdateNotification() {

    }
}