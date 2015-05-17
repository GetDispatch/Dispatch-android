package io.dispatch.dispatch;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Daniel on 5/16/2015.
 */
public class CrashService extends Service {
    private CrashListener listener;
    private Handler handler;
    private Runnable runnable;
    private boolean stopped;
    private boolean crashed;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        listener = (CrashListener) intent.getExtras().get("listener");

        handler = new Handler();

        runnable = new Runnable() {

            @Override
            public void run() {
                if(!checkVelocity()) {
                    handler.postDelayed(runnable, 1000);
                    return;
                }
            }

        };

        runnable.run();

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Runnable task = new Runnable() {

            @Override
            public void run() {
                crashed = true;
            }

        };

        executor.schedule(task, 10, TimeUnit.SECONDS);

        executor.shutdown();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean checkVelocity() {

        // Do math

        if(crashed) {
            listener.onPossibleCrash(getApplicationContext());
        }

        return crashed;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
}
