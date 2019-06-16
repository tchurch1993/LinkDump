package com.linkdump.tchur.ld.objects;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Message implements Comparable, Serializable {

    private String message, user, userName, messageType, imageUrl, linkImage, linkTitle, linkDescription, linkUrl, linkVideo;
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

    public String getLinkVideo() {
        return linkVideo;
    }

    public void setLinkVideo(String linkVideo) {
        this.linkVideo = linkVideo;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getLinkImage() {
        return linkImage;
    }

    public void setLinkImage(String linkImage) {
        this.linkImage = linkImage;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
    }

    public String getLinkDescription() {
        return linkDescription;
    }

    public void setLinkDescription(String linkDescription) {
        this.linkDescription = linkDescription;
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
