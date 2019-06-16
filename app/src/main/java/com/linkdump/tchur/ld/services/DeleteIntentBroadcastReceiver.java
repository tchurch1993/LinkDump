package com.linkdump.tchur.ld.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.linkdump.tchur.ld.utils.MessageHistoryUtil;

import java.io.IOException;

/**
 * Created by tchurh on 12/19/2018.
 * Bow down to my greatness.
 */
public class DeleteIntentBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("demo", "in deleteIntent");
        try {
            MessageHistoryUtil.clearGroupHistory(context, intent.getStringExtra("groupId"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
