package com.linkdump.tchur.ld.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.function.Function;

public class Navigation {




    Intent intent;
    Context source;
    Class destination;
    Activity activity;



    public Navigation(){

    }




    public Navigation from(Context source){
        this.source = source;
        return this;
    }



    public Navigation withExtra(String extraTitle, Serializable extraContent) {
        intent.putExtra(extraTitle, extraContent);
        return this;
    }



    public Navigation withExtra(String extraTitle, String extraContent) {
        intent.putExtra(extraTitle, extraContent);
        return this;
    }




    public Navigation withExtra(String extraTitle, Parcelable extraContent) {
        intent.putExtra(extraTitle, extraContent);
        return this;
    }




    public Navigation withExtra(String extraTitle, Gson extraContent) {
        intent.putExtra(extraTitle, extraContent.toString());
        return this;
    }


    public Navigation gatherFromIntent(Function<Intent,Intent> function){
        function.apply(intent);
        return this;
    }


    public Navigation moveTo(Class destination){
        this.destination = destination;
        intent = new Intent(source,destination);
        activity = new Activity();
        activity.startActivity(intent);
        return this;
    }



    public static Navigation build(){
         return new Navigation();
    }
}
