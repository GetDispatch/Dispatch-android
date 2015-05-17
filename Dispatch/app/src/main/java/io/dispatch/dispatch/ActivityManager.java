package io.dispatch.dispatch;

import android.app.Activity;

/**
 * Created by Daniel on 5/17/2015.
 */
public class ActivityManager {
    private static MainActivity activity;

    public static void setActivity(MainActivity activity) {
        ActivityManager.activity = activity;
    }

    public static MainActivity getActivity() {
        return activity;
    }
}
