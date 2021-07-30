package com.w3engineers.ext.viper;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.mesh.util.ObjectBox;

public class ViperApp extends Application {
    private static ViperApp sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        SharedPref.on(this);
        ObjectBox.init(this);
    }

    public static Context getContext() {

        if (sContext != null) {
            return sContext;
        }
        return null;
    }
}