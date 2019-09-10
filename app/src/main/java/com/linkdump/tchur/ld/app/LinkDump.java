package com.linkdump.tchur.ld.app;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import com.linkdump.tchur.ld.utils.CollectionExtensions;
import com.linkdump.tchur.ld.repository.sugar_orm.AppConfig;

import java.util.ArrayList;

public class LinkDump extends Application {




    public LinkDump() {
        super();



    }






    @Override
    public void onCreate() {

        super.onCreate();


        AndroidXLaunchRules androidXLaunchRules = new AndroidXLaunchRules();
        androidXLaunchRules.setAppConfiguration();
        androidXLaunchRules.setAppPersistence();
        androidXLaunchRules.setDeviceParameters();


        ArrayList<AppConfig> list = new ArrayList<>();
        CollectionExtensions collectionExtensions = new CollectionExtensions();


    }







    public AppConfig setAppConfig()
    {
        if(AppConfig.findById(AppConfig.class,0L) == null)
        {
            AppConfig appConfig = AppConfig.findById(AppConfig.class,0L);
            appConfig.setTitle("LinkDump");
            appConfig.setAppVersion(1);
            appConfig.setBackGroundColor("blue");
            appConfig.setForeGroundColor("red");
            appConfig.setSaveAllMessagesLocally(true);
            appConfig.setShowReadReceipts(true);
            appConfig.setShowTypingStatus(true);
            appConfig.save();
            return appConfig;
        }
        return null;
    }






    @Override
    public void onTerminate() {

        super.onTerminate();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();

    }




    @Override
    public void onTrimMemory(int level) {

        super.onTrimMemory(level);

    }

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {

        super.registerComponentCallbacks(callback);

    }

    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {

        super.unregisterComponentCallbacks(callback);

    }
}
