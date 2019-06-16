package com.linkdump.tchur.ld.data;

import android.content.Context;

import com.linkdump.tchur.ld.abstractions.IActivityContainer;
import com.linkdump.tchur.ld.objects.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatActivityContainer implements IActivityContainer {


    private Context context;
    private String groupName;
    private String currentGroup;
    private static String TAG = "ChatActivity";
    private static String OG_REGEX = "og:image|og:title|og:description|og:type|og:url|og:video";




    public ChatActivityContainer(Context context){
        this.context = context;
    }

    public static String getTAG() {
        return TAG;
    }

    public static void setTAG(String TAG) {
        ChatActivityContainer.TAG = TAG;
    }

    public static String getOgRegex() {
        return OG_REGEX;
    }

    public static void setOgRegex(String ogRegex) {
        OG_REGEX = ogRegex;
    }

    public String getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(String currentGroup) {
        this.currentGroup = currentGroup;
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }






}
