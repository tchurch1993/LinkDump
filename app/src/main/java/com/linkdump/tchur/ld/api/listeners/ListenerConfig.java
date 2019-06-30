package com.linkdump.tchur.ld.api.listeners;

import com.google.firebase.firestore.Query;
import com.linkdump.tchur.ld.constants.FirebaseConstants;

public class ListenerConfig {


    public String collectionTitle;
    public String property;
    public Query.Direction direction;
    public int limit;




    public String getCollectionTitle() {
        return collectionTitle;
    }

    public void setCollectionTitle(String collectionTitle) {
        this.collectionTitle = collectionTitle;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Query.Direction getDirection() {
        return direction;
    }

    public void setDirection(Query.Direction direction) {
        this.direction = direction;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }


}
