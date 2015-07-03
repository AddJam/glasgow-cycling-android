package com.fcd.glasgow_cycling.utils;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.fcd.glasgow_cycling.BuildConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by michaelhayes on 18/06/15.
 */
public class AddJam {
    public static void log(int priority, String tag, String message) {
        if (BuildConfig.DEBUG_MODE || Fabric.isInitialized() == false) {
            Log.println(priority, tag, message);
        } else {
            Crashlytics.getInstance().core.log(priority, tag, message);
        }
    }
}
