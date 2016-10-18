package com.melodies.bandup;

import android.content.Context;

import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.melodies.bandup.repositories.BandUpDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Bergthor on 16.10.2016.
 */

public class BandUpMockRepository implements BandUpDatabase {
    @Override
    public void getUserProfile(JSONObject user, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void getInstruments(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void getGenres(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void postInstruments(JSONArray instruments, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void postGenres(JSONArray genres, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void local_login(JSONObject user, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

}
