package com.melodies.bandup.repositories;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melodies.bandup.JsonArrayToObjectRequest;
import com.melodies.bandup.R;
import com.melodies.bandup.VolleySingleton;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Bergthor on 16.10.2016.
 */

public class BandUpRepository implements BandUpDatabase {


    private JsonObjectRequest createObjectRequest(int httpMethod, String url, JSONObject data, final BandUpResponseListener responseListener, final BandUpErrorListener errorListener) {

        return new JsonObjectRequest (
                httpMethod,
                url,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        responseListener.onBandUpResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorListener.onBandUpErrorResponse(error);
                    }
                }
        );
    }

    private JsonArrayRequest createArrayRequest(int httpMethod, String url, JSONArray data, final BandUpResponseListener responseListener, final BandUpErrorListener errorListener) {

        return new JsonArrayRequest(
                httpMethod,
                url,
                data,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        responseListener.onBandUpResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorListener.onBandUpErrorResponse(error);
                    }
                }
        );
    }

    private JsonArrayToObjectRequest createArrayObjectRequest(int httpMethod, String url, JSONArray data, final BandUpResponseListener responseListener, final BandUpErrorListener errorListener) {

        return new JsonArrayToObjectRequest(
                httpMethod,
                url,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        responseListener.onBandUpResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorListener.onBandUpErrorResponse(error);
                    }
                }
        );
    }

    @Override
    public void local_login(final Context context,
                            final JSONObject user,
                            final BandUpResponseListener responseListener,
                            final BandUpErrorListener errorListener) {

        String url = context.getResources().getString(R.string.api_address).concat("/login-local");
        JsonObjectRequest jsonObjectRequest = createObjectRequest(Request.Method.POST, url, user, responseListener, errorListener);

        // Insert request into queue
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void getUserProfile(final Context context,
                               final JSONObject user,
                               final BandUpResponseListener responseListener,
                               final BandUpErrorListener errorListener) {

        String url = context.getResources().getString(R.string.api_address).concat("/get-user");
        JsonObjectRequest jsonObjectRequest = createObjectRequest(Request.Method.POST, url, user, responseListener, errorListener);

        // insert request into queue
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void getInstruments(Context context, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        String url = context.getResources().getString(R.string.api_address).concat("/instruments");

        JsonArrayRequest jsonInstrumentRequest = createArrayRequest(Request.Method.GET, url, new JSONArray(), responseListener, errorListener);

        VolleySingleton.getInstance(context).addToRequestQueue(jsonInstrumentRequest);
    }

    @Override
    public void getGenres(Context context, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        String url = context.getResources().getString(R.string.api_address).concat("/genres");

        JsonArrayRequest jsonInstrumentRequest = createArrayRequest(Request.Method.GET, url, new JSONArray(), responseListener, errorListener);

        VolleySingleton.getInstance(context).addToRequestQueue(jsonInstrumentRequest);
    }

    @Override
    public void postInstruments(Context context, JSONArray instruments, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        String url = context.getResources().getString(R.string.api_address).concat("/instruments");
        System.out.println(instruments);
        JsonArrayToObjectRequest jsonInstrumentRequest = createArrayObjectRequest(Request.Method.POST, url, instruments, responseListener, errorListener);

        VolleySingleton.getInstance(context).addToRequestQueue(jsonInstrumentRequest);
    }

    @Override
    public void postGenres(Context context, JSONArray genres, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        String url = context.getResources().getString(R.string.api_address).concat("/genres");

        JsonArrayToObjectRequest jsonInstrumentRequest = createArrayObjectRequest(Request.Method.POST, url, genres, responseListener, errorListener);

        VolleySingleton.getInstance(context).addToRequestQueue(jsonInstrumentRequest);
    }
}
