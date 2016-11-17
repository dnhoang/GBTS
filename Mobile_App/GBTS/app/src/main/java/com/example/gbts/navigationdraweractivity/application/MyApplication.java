package com.example.gbts.navigationdraweractivity.application;

import android.app.Application;

/**
 * Created by truon on 11/8/2016.
 */

public class MyApplication extends Application {
    public static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
