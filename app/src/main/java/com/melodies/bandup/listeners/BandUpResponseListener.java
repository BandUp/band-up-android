package com.melodies.bandup.listeners;

import org.json.JSONException;

/**
 * Created by Bergthor on 16.10.2016.
 */

public interface BandUpResponseListener {

    void onBandUpResponse(Object response) throws JSONException;
}

