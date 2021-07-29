package com.w3engineers.mesh.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;


import androidx.multidex.MultiDexApplication;

import com.w3engineers.mesh.application.data.local.db.SharedPref;

public class MeshApp extends MultiDexApplication {
    private static Context context;
    private static Activity mActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        SharedPref.on(this);


        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                mActivity = activity;
            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }

            /** Unused implementation **/
            @Override
            public void onActivityStarted(Activity activity) {
                mActivity = activity;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                mActivity = activity;
            }
            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
//                MeshLog.v("activity found onstopped " + activity.getLocalClassName());
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
        });
    }

    public static Context getContext(){
        return context;
    }


    public static Activity getCurrentActivity() {
        return mActivity;
    }


}
