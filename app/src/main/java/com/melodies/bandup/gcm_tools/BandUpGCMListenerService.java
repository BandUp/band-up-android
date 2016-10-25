package com.melodies.bandup.gcm_tools;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.melodies.bandup.MainScreenActivity.MainScreenActivity;
import com.melodies.bandup.R;

/**
 * Created by Notandi on 20.10.2016.
 */

public class BandUpGCMListenerService extends GcmListenerService {
    // name for service thread
    private static String TAG = "BandUpGCMListenerService";

    /**
     * gets called for every notification that arrives
     * @param from
     * @param data
     */
    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(String from, Bundle data){
        System.out.println("Got a message");
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);


        if (from.startsWith("/topics/")){
            // message recieved from a topic
        }else{
            // normal downstream message
        }

        // proccess message

        //send message as notification
        sendNotification(message);
    }

    /**
     * display message as notification
     * @param message
     */
    private void sendNotification(String message){
        System.out.println(message);
        // create intent to start activity on notification click
        Intent intent = new Intent(this, MainScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /*Request code*/, intent,
                                                                PendingIntent.FLAG_ONE_SHOT);

        // need uri if we want notification sound (we can make custom sounds if we want)
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = null;        // atach activity intent
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_band_up_logo_notification) // set icon for notification
                .setContentTitle("Bad Melodies")         // title for notification TODO: get this from notification object
                .setContentText(message)                 // text to display
                .setAutoCancel(true)
                .setSound(defaultSoundUri)               // play notification sound
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //display notification
        notificationManager.notify(0 /*ID of notification*/, notificationBuilder.build());
    }
}
