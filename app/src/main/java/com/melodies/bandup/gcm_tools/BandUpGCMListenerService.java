package com.melodies.bandup.gcm_tools;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.melodies.bandup.MainScreenActivity.MainScreenActivity;

/**
 * Created by Notandi on 20.10.2016.
 */

public class BandUpGCMListenerService extends GcmListenerService {
    private static String TAG = "BandUpGCMListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data){
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

    private void sendNotification(String message){
        Intent intent = new Intent(this, MainScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
