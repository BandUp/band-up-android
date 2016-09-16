package com.melodies.bandup.setup;

import android.content.Context;
import android.widget.GridView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bergthor on 16.9.2016.
 */
public class SetupListeners {
    Response.Listener<JSONArray> responseListener;
    Response.ErrorListener errorListener;

    SetupListeners(final Context context, final GridView gridView) {
        responseListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<DoubleListItem> list = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject item = response.getJSONObject(i);
                        int    order = item.getInt("order");
                        String name  = item.getString("name");

                        DoubleListItem myItems = new DoubleListItem(order, name);
                        list.add(myItems);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                gridView.setAdapter(new DoubleListAdapter(context, list));
            }
        };
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
            }
        };
    }



    public Response.Listener<JSONArray> getResponseListener() {
        return responseListener;
    }

    public Response.ErrorListener getErrorListener() {
        return errorListener;
    }
}
