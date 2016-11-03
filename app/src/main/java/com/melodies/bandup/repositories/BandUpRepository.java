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

    private Context mContext;

    public BandUpRepository(Context _context){
        mContext = _context;
    }

    private JsonObjectRequest createObjectRequest(int httpMethod, String url, JSONObject data, final BandUpResponseListener responseListener, final BandUpErrorListener errorListener) {
        System.out.println("DATA1");
        System.out.println(data);
        System.out.println("DATA2");
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
    public void local_login(final JSONObject user, final BandUpResponseListener responseListener, final BandUpErrorListener errorListener) {

        String url = mContext.getResources().getString(R.string.api_address).concat("/login-local");
        JsonObjectRequest jsonObjectRequest = createObjectRequest(Request.Method.POST, url, user, responseListener, errorListener);

        // Insert request into queue
        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
    }

    public void getUserProfile(final JSONObject user, final BandUpResponseListener responseListener, final BandUpErrorListener errorListener) {

        String url = mContext.getResources().getString(R.string.api_address).concat("/user");
        System.out.println(user);
        JsonObjectRequest jsonObjectRequest = createObjectRequest(Request.Method.POST, url, user, responseListener, errorListener);

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
    }


    @Override
    public void getInstruments(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        String url = mContext.getResources().getString(R.string.api_address).concat("/instruments");

        JsonArrayRequest jsonInstrumentRequest = createArrayRequest(Request.Method.GET, url, new JSONArray(), responseListener, errorListener);

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonInstrumentRequest);
    }

    @Override
    public void getGenres(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        String url = mContext.getResources().getString(R.string.api_address).concat("/genres");

        JsonArrayRequest jsonInstrumentRequest = createArrayRequest(Request.Method.GET, url, new JSONArray(), responseListener, errorListener);

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonInstrumentRequest);
    }

    @Override
    public void postInstruments(JSONArray instruments, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        String url = mContext.getResources().getString(R.string.api_address).concat("/instruments");
        System.out.println(instruments);
        JsonArrayToObjectRequest jsonInstrumentRequest = createArrayObjectRequest(Request.Method.POST, url, instruments, responseListener, errorListener);

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonInstrumentRequest);
    }

    @Override
    public void postGenres(JSONArray genres, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        String url = mContext.getResources().getString(R.string.api_address).concat("/genres");

        JsonArrayToObjectRequest jsonInstrumentRequest = createArrayObjectRequest(Request.Method.POST, url, genres, responseListener, errorListener);

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonInstrumentRequest);
    }

    @Override
    public void getFilter(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        String url = mContext.getResources().getString(R.string.api_address).concat("/filter");

        JsonArrayToObjectRequest jsonFilterRequest = createArrayObjectRequest(Request.Method.GET, url, new JSONArray(), responseListener, errorListener);

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonFilterRequest);
    }

    @Override
    public void getUserList(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        String url = mContext.getResources().getString(R.string.api_address).concat("/nearby-users");

        JsonArrayRequest jsonInstrumentRequest = createArrayRequest(Request.Method.GET, url, new JSONArray(), responseListener, errorListener);

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonInstrumentRequest);
    }

    @Override
    public void sendGCMRegToken(final JSONObject tokenObject,
                                final BandUpResponseListener responseListener,
                                final BandUpErrorListener errorListener){
        String url = mContext.getResources().getString(R.string.api_address).concat("/gcmRegToken");

        JsonObjectRequest jsonObjectRequest = createObjectRequest(Request.Method.POST, url,
                tokenObject, responseListener, errorListener);

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void postLike(JSONObject userObject, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        String url = mContext.getResources().getString(R.string.api_address).concat("/like");

        JsonObjectRequest jsonObjectRequest = createObjectRequest(
                Request.Method.POST,
                url,
                userObject,
                responseListener,
                errorListener);

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void postLocation(JSONObject locationObject, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        String url = mContext.getResources().getString(R.string.api_address).concat("/login-local/location");

        JsonObjectRequest jsonObjectRequest = createObjectRequest(
                Request.Method.POST,
                url,
                locationObject,
                responseListener,
                errorListener);

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void isLoggedIn(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        String url = mContext.getResources().getString(R.string.api_address).concat("/isloggedin");

        JsonObjectRequest jsonObjectRequest = createObjectRequest(
                Request.Method.GET,
                url,
                null,
                responseListener,
                errorListener);

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void logout(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
        String url = mContext.getResources().getString(R.string.api_address).concat("/logout");

        JsonObjectRequest jsonObjectRequest = createObjectRequest(
                Request.Method.GET,
                url,
                null,
                responseListener,
                errorListener);

        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);

    }
}
