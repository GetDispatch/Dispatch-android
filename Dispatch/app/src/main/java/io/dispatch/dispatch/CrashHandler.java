package io.dispatch.dispatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Daniel on 5/16/2015.
 */
public class CrashHandler extends CrashListener {
    private AlertDialog alertDialog;
    private CountDownTimer timer;
    private Vibrator vibrator;
    private AudioManager audioManager;
    private long vibrateTimeLeft;

    public CrashHandler(String message, final List<Contact> contacts) {
        super(message, contacts);
    }

    @Override
    public void onPossibleCrash(final CrashService service, Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle("Are you in an accident?");

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);

        new CountDownTimer(15000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                // Only do this if nothing has been done yet
                if(alertDialog.isShowing()) {
//                service.startLocationListener();
                    // Show a progress dialog for texting the contacts
                    ActivityManager.getActivity().showNotifyContactsDialog();

                    // Text all the contaxts
                    textContacts(ActivityManager.getActivity());

                    ActivityManager.getActivity().hideNotifyContactsDialog();

                    // Re-enable the start button
                            (((Button) ActivityManager.getActivity().findViewById(R.id.startButton))).setEnabled(true);
                    ((Button) ActivityManager.getActivity().findViewById(R.id.startButton)).setText(R.string.start);

                    // Dissmiss the alert dialog
                    alertDialog.dismiss();
                }
            }

        }.start();

        (((Button) ActivityManager.getActivity().findViewById(R.id.startButton))).setEnabled(false);

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No!", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Force it to stop, end the sound/vibration
                timer.onFinish();
                timer.cancel();

                // Cancel the vibrator
                vibrator.cancel();

                // Re-enable the start button
                (((Button) ActivityManager.getActivity().findViewById(R.id.startButton))).setEnabled(true);
            }

        });

        soundAlarm(context);

        alertDialog.show();
    }

    private void closeDialog() {
        alertDialog.dismiss();
    }

    private void soundAlarm(Context context) {
        // Use max volume
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        final MediaPlayer mp = MediaPlayer.create(context, R.raw.alarm);
        mp.setLooping(true);
        mp.start();

        timer = new CountDownTimer(15000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                vibrateTimeLeft = millisUntilFinished;
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

        BroadcastReceiver vibrateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // Cancel and resume the vibrator at how much time is left
                if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    vibrator.cancel();
                    vibrator.vibrate(vibrateTimeLeft);
                }
            }
        };

        // Register
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        context.registerReceiver(vibrateReceiver, filter);
    }

    private void textContacts(Context context) {
        SmsManager sms = SmsManager.getDefault();

        for (Contact contact : getContacts()) {
            if(contact.getNumber().equals("7033623714")) {
                sms.sendTextMessage(contact.getNumber(), null, getMessage(), null, null);
            }
        }
    }
}
