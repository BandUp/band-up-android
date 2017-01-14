package com.melodies.bandup.gcm_tools;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.firebase.crash.FirebaseCrash;
import com.melodies.bandup.DatabaseSingleton;
import com.melodies.bandup.R;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {

    private static final String[] TOPICS = {"global"};
    private static final String TAG = "RegistrationService";


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
        super(TAG);
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
            FirebaseCrash.report(e);
        }
    }

    /**
     * send token to server to be able to recieve messages
     *
     * @param token
     */
    private void sendRegistrationToServer(String token){
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("regToken", token);

            DatabaseSingleton.getInstance(this).getBandUpDatabase().sendGCMRegToken(jsonObject, new BandUpResponseListener() {
                @Override
                public void onBandUpResponse(Object response) {
                    Log.d(TAG, "it worked");
                    //startService(new Intent(getApplicationContext(), BandUpGCMListenerService.class));
                }
            }, new BandUpErrorListener() {
                @Override
                public void onBandUpErrorResponse(VolleyError error) {
                    if (error == null) {
                        return;
                    }
                    Log.d(TAG, error.getMessage());
                }
            });

        } catch (JSONException e) {
            FirebaseCrash.report(e);
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
                FirebaseCrash.report(e);
            }
        }
    }
}
