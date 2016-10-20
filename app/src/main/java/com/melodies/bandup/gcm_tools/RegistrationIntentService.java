package com.melodies.bandup.gcm_tools;

import android.app.IntentService;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.melodies.bandup.DatabaseSingleton;
import com.melodies.bandup.R;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.melodies.bandup.repositories.BandUpRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Notandi on 20.10.2016.
 */

public class RegistrationIntentService extends IntentService {

    private static final String[] TOPICS = {"global"};


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RegistrationIntentService(String name) {
        super(name);
    }

    /**
     * Creates an IntentService.
     *
     */
    public RegistrationIntentService() {
        super("RegistrationService");
    }

    /**
     * creates an instanceID and gets token to send to server and subscribe to topics
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            sendRegistrationToServer(token);
            subscribeTopics(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * send token to server to be able to recieve messages
     *
     * @param token
     */
    private void sendRegistrationToServer(String token){
        JSONObject jsonObject = new JSONObject();
        Toast.makeText(this, "token:" + token, Toast.LENGTH_LONG).show();

        try {
            jsonObject.put("regToken", token);

            DatabaseSingleton.getInstance(this).getBandUpDatabase().sendGCMRegToken(jsonObject, new BandUpResponseListener() {
                @Override
                public void onBandUpResponse(Object response) {
                    Log.d("token", response.toString());
                }
            }, new BandUpErrorListener() {
                @Override
                public void onBandUpErrorResponse(VolleyError error) {
                    Log.d("token", error.getMessage());
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * subscribe user to topics in gcm
     *
     * @param token
     */
    private void subscribeTopics(String token){
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for(String topic : TOPICS){
            try {
                pubSub.subscribe(token, "/topics/" + topic, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
