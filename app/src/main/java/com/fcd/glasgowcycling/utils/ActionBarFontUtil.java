package com.fcd.glasgowcycling.utils;

import android.app.Activity;
import android.text.Spannable;
import android.text.SpannableString;

import com.fcd.glasgowcycling.R;
import com.fcd.glasgowcycling.TypefaceSpan;

/**
 * Created by michaelhayes on 11/11/14.
 */
public class ActionBarFontUtil {
    public static void setFont(Activity activity) {
        SpannableString s = new SpannableString(activity.getString(R.string.app_name));
        s.setSpan(new TypefaceSpan(activity, "FutureCitySemiBold.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        activity.getActionBar().setTitle(s);
    }
}
