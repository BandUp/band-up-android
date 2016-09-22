package com.melodies.bandup.setup;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by Bergthor on 22.9.2016.
 */

public class JsonArrayToObjectRequest extends JsonRequest<JSONObject> {

    Response.Listener<JSONObject> mListener;

    public JsonArrayToObjectRequest(int method, String url, JSONArray requestBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody.toString(), listener, errorListener);
        mListener = listener;
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}
