package com.melodies.bandup.repositories;

import android.content.Context;

import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;

import org.json.JSONObject;

/**
 * Created by Bergthor on 16.10.2016.
 */

public interface BandUpDatabase {

    void local_login(JSONObject user,
                     BandUpResponseListener responseListener,
                     BandUpErrorListener errorListener);


    void getUserProfile(final JSONObject user,
                        final BandUpResponseListener responseListener,
                        final BandUpErrorListener errorListener);

    void getInstruments(final BandUpResponseListener responseListener,
                        final BandUpErrorListener errorListener);

    void getGenres(final BandUpResponseListener setupItemsListener,
                   final BandUpErrorListener setupItemsErrorListener);
}

