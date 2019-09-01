package com.linkdump.tchur.ld.repository.sugar_orm;

import com.orm.SugarRecord;

public class AppConfig extends SugarRecord<AppConfig> {


    String title;
    int appVersion;
    Boolean showReadReceipts;
    Boolean showTypingStatus;
    Boolean saveAllMessagesLocally;
    String backGroundColor;
    String foreGroundColor;


    public AppConfig(){
    }


}
