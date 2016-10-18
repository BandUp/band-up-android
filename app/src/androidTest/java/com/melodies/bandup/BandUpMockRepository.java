package com.melodies.bandup;

import android.content.Context;

import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.melodies.bandup.repositories.BandUpDatabase;

import org.json.JSONObject;

/**
 * Created by Bergthor on 16.10.2016.
 */

public class BandUpMockRepository implements BandUpDatabase {
    @Override
    public void getUserProfile(Context context, JSONObject user, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void getInstruments(Context context, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void getGenres(Context context, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void local_login(Context context, JSONObject user, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

}
