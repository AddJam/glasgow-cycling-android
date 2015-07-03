package com.fcd.glasgow_cycling.utils;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.fcd.glasgow_cycling.BuildConfig;

/**
 * Created by michaelhayes on 18/06/15.
 */
public class AddJam {
    public static void log(int priority, String tag, String message) {
        if (BuildConfig.DEBUG_MODE) {
            Log.println(priority, tag, message);
        } else {
            Crashlytics.getInstance().core.log(priority, tag, message);
        }
    }
}
