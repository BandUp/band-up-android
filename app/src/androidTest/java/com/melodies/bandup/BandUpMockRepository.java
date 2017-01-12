package com.melodies.bandup;

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
    public void getFilter(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void getUserList(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void sendGCMRegToken(JSONObject tokenObject, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void postLike(JSONObject userObject, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void postLocation(JSONObject locationObject, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void isLoggedIn(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void logout(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void updateUser(JSONObject updatedObject, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void sendSoundCloudId(JSONObject requestObject, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void sendSoundCloudUrl(JSONObject requestObject, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void getEmailInUse(JSONObject requestObject, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void register(JSONObject requestObject, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void getSearchQuery(JSONObject searchObject, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void sendPasswordResetRequest(JSONObject requestObject, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void delete_user(JSONObject requestObject, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }

    @Override
    public void local_login(JSONObject user, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        responseListener.onBandUpResponse("");
    }
}
