package io.dispatch.dispatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Daniel on 5/16/2015.
 */
public class CrashHandler extends CrashListener {
    private Dialog dialog;
    private CountDownTimer timer;
    private Vibrator vibrator;

    public CrashHandler(final String message, final List<Contact> contacts) {
        super(message, contacts);
    }

    @Override
    public void onPossibleCrash(final CrashService service, Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Are you in an accident?");

        new CountDownTimer(15000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                service.startLocationListener();
                dialog.dismiss();
            }

        }.start();

        builder.setNeutralButton("No!", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Force it to stop, end the sound/vibration
                timer.onFinish();
                timer.cancel();
                vibrator.cancel();
            }

        });

        soundAlarm(context);

        dialog = builder.show();
    }

    private void closeDialog() {
        dialog.dismiss();
    }

    private void soundAlarm(Context context) {
        final MediaPlayer mp = MediaPlayer.create(context, R.raw.alarm);
        mp.setLooping(true);
        mp.start();

        timer = new CountDownTimer(15000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if(mp.isPlaying()) {
                    mp.stop();
                }
            }
        }.start();

        // Vibrate the phone
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15000);
    }

    private void textContacts(Context context) {
        SmsManager sms = SmsManager.getDefault();

        for (Contact contact : getContacts()) {
            sms.sendTextMessage(contact.getNumber(), null, getMessage(), null, null);
        }
    }
}
