package com.melodies.bandup.listeners;

import com.android.volley.VolleyError;

public interface BandUpErrorListener {

    void onBandUpErrorResponse(VolleyError error);
}
