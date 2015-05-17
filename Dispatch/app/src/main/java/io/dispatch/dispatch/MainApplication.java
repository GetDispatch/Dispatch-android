package io.dispatch.dispatch;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.PushService;
import com.parse.SaveCallback;

/**
 * Created by Daniel on 5/17/2015.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);

        Parse.initialize(this, getString(R.string.parseAppID), getString(R.string.parseClientID));

        PushService.setDefaultPushCallback(this, MainActivity.class);

        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParsePush.subscribeInBackground("crash", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }


}
