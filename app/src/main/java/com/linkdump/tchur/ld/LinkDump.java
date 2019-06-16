package com.linkdump.tchur.ld;

import android.app.Application;
import android.content.res.Configuration;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

public class LinkDump extends Application {








    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(this, "This is the application", Toast.LENGTH_LONG).show();





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
    public void onLowMemory() {
        super.onLowMemory();






    }



    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);


    }
}
