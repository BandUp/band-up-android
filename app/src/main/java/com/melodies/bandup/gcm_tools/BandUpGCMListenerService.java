package com.melodies.bandup.gcm_tools;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.melodies.bandup.ChatActivity;
import com.melodies.bandup.MainScreenActivity.MainScreenActivity;
import com.melodies.bandup.R;

public class BandUpGCMListenerService extends GcmListenerService {
    // name for service thread
    private static String TAG = "BandUpGCMListenerService";

    // constants for message types
    private static final String MATCH_NOTIFICATION = "matchNotification";
    private static final int MATCH_NOTIFICATION_ID = 1;
    private static final String MSG_NOTIFICATION = "msgNotification";
    private static final int MSG_NOTIFICATION_ID = 2;

    /**
     * gets called for every notification that arrives
     * @param from
     * @param data
     */
    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(String from, Bundle data){
        /*String message = data.getBundle("notification").getString("body");
        String type = data.getString("type");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);


        if (from.startsWith("/topics/")){
            // message recieved from a topic (currently not in use)
        } else {
            // normal downstream message
            if (type != null) {
                if (Objects.equals(type, MATCH_NOTIFICATION) && loadUserSwitch("switchMatches")) {
                    senMatchNotification(data);
                }
                if (Objects.equals(type, MSG_NOTIFICATION) && loadUserSwitch("switchMessages")) {
                    sendMessageNotification(data);
                }
                if (loadUserSwitch("switchAlert")) {
                    sendNotification(data);
                }
            }

        }*/
    }

    // loading switch state
    public boolean loadUserSwitch(String valueName) {
        SharedPreferences sharedPreferences = getSharedPreferences("SettingsFileSwitch", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(valueName, true);
    }

    /**
     * notify user of a new message
     * pending intent will contain chat activity for user
     *
     * accepts a bundle object containing at least the following fields
     * data.from = userID of the matched user
     * data.fromName
     * notification object
     * @param data
     */
    private void sendMessageNotification(Bundle data) {
        String message = data.getBundle("notification").getString("body");
        String title = data.getBundle("notification").getString("title");
        // create intent to start activity on notification click
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("SEND_TO_USER_ID", data.getBundle("data").getString("from"));
        intent.putExtra("SEND_TO_USERNAME", data.getBundle("data").getString("fromName"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /*Request code*/, intent,
                PendingIntent.FLAG_ONE_SHOT);

        // need uri if we want notification sound (we can make custom sounds if we want)
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = null;        // atach activity intent
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_band_up_logo_notification) // set icon for notification
                .setContentTitle(title)         // title for notification
                .setContentText(message)                 // text to display
                .setAutoCancel(true)
                .setSound(defaultSoundUri)               // play notification sound
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //display notification
        notificationManager.notify(MSG_NOTIFICATION_ID, notificationBuilder.build());
    }

    /**
     * create push notification to show a new match has happened
     *
     * accepts a bundle object containing at least the following fields
     * data.from = userID of the matched user
     * notification object
     * @param data
     */
    private void senMatchNotification(Bundle data) {
        String message = data.getBundle("notification").getString("body");
        String title = data.getBundle("notification").getString("title");
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
                .setContentTitle(title)         // title for notification
                .setContentText(message)                 // text to display
                .setAutoCancel(true)
                .setSound(defaultSoundUri)               // play notification sound
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //display notification
        notificationManager.notify(MATCH_NOTIFICATION_ID, notificationBuilder.build());
    }

    /**
     * display message as notification
     * @param data
     */
    private void sendNotification(Bundle data){
        String message = data.getBundle("notification").getString("body");
        String title = data.getBundle("notification").getString("title");
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
                .setContentTitle(title)         // title for notification
                .setContentText(message)                 // text to display
                .setAutoCancel(true)
                .setSound(defaultSoundUri)               // play notification sound
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //display notification
        notificationManager.notify(0 /*ID of notification*/, notificationBuilder.build());
    }
}
