package com.linkdump.tchur.ld.persistence;

import com.linkdump.tchur.ld.abstractions.INotificationsRepo;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NotificationsRepo implements INotificationsRepo {

    Map<String, Object> sendMessage;


    public NotificationsRepo(Map<String, Object> sendMessage) {
        this.sendMessage = sendMessage;
    }




}
