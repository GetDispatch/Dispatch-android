package io.dispatch.dispatch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Daniel on 5/16/2015.
 */
public class CrashService extends Service {
    private CrashListener listener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        listener = (CrashListener) intent.getExtras().get("listener");

        while(true) {
            // Return true if a crash happened
            if(checkVelocity()) {
                stopSelf();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean checkVelocity() {
        boolean crashed = true;

        // Do math

        if(crashed) {
            listener.onPossibleCrash(getApplicationContext());
        }

        return crashed;
    }
}
