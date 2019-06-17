package com.linkdump.tchur.ld.utils;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.Person;
import android.util.Log;

import com.linkdump.tchur.ld.objects.Message;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by tchurh on 12/19/2018.
 * Bow down to my greatness.
 */

//TODO: Check to see what is actually needed and scrap the rest
public final class MessageHistoryUtil {
    private static String TAG = "MessageHistoryUtil";

    private MessageHistoryUtil() {
    }

    public static void writeMessages(Context context, String key, Object object) throws IOException {
        FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    public static Object readMessages(Context context, String key) throws IOException,
            ClassNotFoundException {
        FileInputStream fis = context.openFileInput(key);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();
        Log.d(TAG, object.toString());
        return object;
    }

    public static void groupMessageNotificationHistory(Context context, String groupId, Message message) throws IOException, ClassNotFoundException {
        ArrayList<Message> messages = new ArrayList<>();
        try {
            messages = (ArrayList<Message>) MessageHistoryUtil.readMessages(context, groupId);
        } catch (IOException ignored) {
            Log.d(TAG, "inside readIO exception in groupmessagehistory method");
        }
        if (!messages.isEmpty()) {
            Log.d(TAG, "in isEmpty");
            if (messages.size() > 7) {
                Log.d(TAG, "in >7 if");
                Collections.sort(messages);
                messages.remove(0);
            }
            messages.add(message);
            MessageHistoryUtil.writeMessages(context, groupId, messages);
        } else {
            Log.d(TAG, "writing messages");
            messages.add(message);
            MessageHistoryUtil.writeMessages(context, groupId, messages);
        }
    }

    public static ArrayList<NotificationCompat.MessagingStyle.Message> convertToMessagesCompat(Context context, String groupId) throws IOException, ClassNotFoundException {
        ArrayList<Message> messages = (ArrayList<Message>) readMessages(context, groupId);
        ArrayList<NotificationCompat.MessagingStyle.Message> messagesCompat = new ArrayList<>();
        Collections.sort(messages);
        for (Message m : messages) {
            Log.d(TAG, "name of sender: " + m.getUserName());
            Person person = new Person.Builder().setName(m.getUserName()).build();
            messagesCompat.add(new NotificationCompat.MessagingStyle.Message(m.getMessage(), m.getSentTime(), person));
        }
        return messagesCompat;
    }

    public static void clearGroupHistory(Context context, String groupId) throws IOException {
        writeMessages(context, groupId, new ArrayList<>());
    }


}