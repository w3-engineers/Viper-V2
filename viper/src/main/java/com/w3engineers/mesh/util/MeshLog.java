package com.w3engineers.mesh.util;

import android.util.Log;

import com.w3engineers.mesh.BuildConfig;

public class MeshLog {

    private static String TAG = "MeshLog";

    public static final String INFO = "(I)";
    public static final String WARNING = "(W)";
    public static final String ERROR = "(E)";
    public static final String SPECIAL = "(S)";

    public static void v(String msg) {
        if (BuildConfig.DEBUG) {
            v(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (BuildConfig.DEBUG) {
            i(TAG, msg);
        }
    }


    public static void e(String msg) {
        if (BuildConfig.DEBUG) {
            e(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (BuildConfig.DEBUG) {
            w(TAG, msg);
        }
    }

    private static void v(String tag, String msg) {
        Log.v(tag, msg);
    }

    private static void i(String tag, String msg) {
        Log.i(tag, msg);
    }

    private static void w(String tag, String msg) {
        Log.w(tag, msg);
    }

    private static void e(String tag, String msg) {
        Log.e(tag, msg);
    }
}
