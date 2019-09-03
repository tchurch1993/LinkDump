package com.linkdump.tchur.ld.app;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;

public class LinkDump extends Application {




    public LinkDump() {
        super();
    }

    @Override
    public void onCreate() {

        super.onCreate();

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
