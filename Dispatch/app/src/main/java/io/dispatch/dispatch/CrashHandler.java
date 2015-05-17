package io.dispatch.dispatch;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.List;

/**
 * Created by Daniel on 5/16/2015.
 */
public class CrashHandler extends CrashListener {

    public CrashHandler(final String message, final List<Contact> contacts) {
        super(message, contacts);
    }

    @Override
    public void onPossibleCrash(Context context) {
        soundAlarm(context);
        textContacts(context);
    }

    private void soundAlarm(Context context) {
        final MediaPlayer mp = MediaPlayer.create(context, R.raw.alarm);

        mp.start();
        new CountDownTimer(10000, 1) {

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
        long[] pattern = {1};
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(pattern, 2);
    }


    private void textContacts(Context context) {
        SmsManager sms = SmsManager.getDefault();

        for(Contact contact : getContacts()) {
            sms.sendTextMessage(contact.getNumber(), null, getMessage(), null, null);
        }
    }
}
