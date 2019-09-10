package com.linkdump.tchur.ld.repository.sugar_orm;

import android.widget.Toast;

import com.orm.SugarRecord;

public class AppConfig extends SugarRecord<AppConfig> {




    private String title;
    private int appVersion;
    private Boolean showReadReceipts;
    private Boolean showTypingStatus;
    private Boolean saveAllMessagesLocally;
    private String backGroundColor;
    private String foreGroundColor;



    public AppConfig(){

    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    public Boolean getShowReadReceipts() {
        return showReadReceipts;
    }

    public void setShowReadReceipts(Boolean showReadReceipts) {
        this.showReadReceipts = showReadReceipts;
    }

    public Boolean getShowTypingStatus() {
        return showTypingStatus;
    }

    public void setShowTypingStatus(Boolean showTypingStatus) {
        this.showTypingStatus = showTypingStatus;
    }

    public Boolean getSaveAllMessagesLocally() {
        return saveAllMessagesLocally;
    }

    public void setSaveAllMessagesLocally(Boolean saveAllMessagesLocally) {
        this.saveAllMessagesLocally = saveAllMessagesLocally;
    }

    public String getBackGroundColor() {
        return backGroundColor;
    }

    public void setBackGroundColor(String backGroundColor) {
        this.backGroundColor = backGroundColor;
    }

    public String getForeGroundColor() {
        return foreGroundColor;
    }

    public void setForeGroundColor(String foreGroundColor) {
        this.foreGroundColor = foreGroundColor;
    }



}
