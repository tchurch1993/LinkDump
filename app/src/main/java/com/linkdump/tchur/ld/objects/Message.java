package com.linkdump.tchur.ld.objects;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Map;

public class Message implements Comparable, Serializable {
    private String message, user, userName, messageType, imageUrl;
    private Map<String, Object> linkData;
    private long sentTime;
    private boolean isUser;


    public Message() {
    }

    public Message(String mMessage, String mUser, long mSentTime) {
        message = mMessage;
        user = mUser;
        sentTime = mSentTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Map<String, Object> getLinkData() {
        return linkData;
    }

    public void setLinkData(Map<String, Object> linkData) {
        this.linkData = linkData;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String mUserName) {
        userName = mUserName;
    }

    public boolean getIsUser() {
        return isUser;
    }

    public void setIsUser(boolean user) {
        isUser = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return Long.compare(this.sentTime, ((Message) o).sentTime);
    }
}
