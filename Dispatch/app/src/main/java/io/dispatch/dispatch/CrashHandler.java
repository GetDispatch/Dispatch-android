package io.dispatch.dispatch;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.List;

/**
 * Created by Daniel on 5/16/2015.
 */
public class CrashHandler extends CrashListener {
    private MediaPlayer mediaPlayer = new MediaPlayer();

    public CrashHandler(final String message, final List<Contact> contacts) {
        super(message, contacts);
    }

    @Override
    public void onPossibleCrash(Context context) {
        soundAlarm(context);
        textContacts(context);
    }

    private void soundAlarm(Context context) {
        try {
            mediaPlayer.setDataSource(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL));
            mediaPlayer.prepare();
            mediaPlayer.setLooping(false);

            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mediaPlayer) {
                    mediaPlayer.stop();
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                }
            });

            mediaPlayer.start();
        } catch (Exception e) {
            Log.e(e.toString(), e.toString());
        }

        // Vibrate the phone
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }


    private void textContacts(Context context) {
        SmsManager sms = SmsManager.getDefault();

        for(Contact contact : getContacts()) {
            sms.sendTextMessage(contact.getNumber(), null, getMessage(), null, null);
        }
    }
}
