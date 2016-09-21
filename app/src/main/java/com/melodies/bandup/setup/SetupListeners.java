package com.melodies.bandup.setup;

import android.content.Context;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SetupListeners {
    private Response.Listener<JSONArray> responseListener;
    private Response.Listener pickListener;
    private Response.ErrorListener errorListener;

    SetupListeners(final Context context, final GridView gridView, final ProgressBar progressBar) {
        responseListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<DoubleListItem> list = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject item = response.getJSONObject(i);
                        String id    = item.getString("_id");
                        int    order = item.getInt   ("order");
                        String name  = item.getString("name");

                        DoubleListItem myItems = new DoubleListItem(id, order, name);
                        list.add(myItems);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                progressBar.setVisibility(progressBar.GONE);
                gridView.setAdapter(new DoubleListAdapter(context, list));
            }
        };

        pickListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(context, "Connection error!", Toast.LENGTH_LONG).show();
                }
                else if (error instanceof AuthFailureError) {
                    Toast.makeText(context, "Invalid username or password", Toast.LENGTH_LONG).show();
                }
                else if (error instanceof ServerError) {
                    Toast.makeText(context, "Server error!", Toast.LENGTH_LONG).show();
                }
                else if (error instanceof NetworkError) {
                    Toast.makeText(context, "Network error!", Toast.LENGTH_LONG).show();
                }
                else if (error instanceof ParseError) {
                    Toast.makeText(context, "Server parse error!", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(context, "Unknown error! Contact Administrator", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    public Response.Listener<JSONArray> getResponseListener() {
        return responseListener;
    }

    public Response.Listener<JSONArray> getPickListener() {
        return pickListener;
    }

    public Response.ErrorListener getErrorListener() {
        return errorListener;
    }
}
