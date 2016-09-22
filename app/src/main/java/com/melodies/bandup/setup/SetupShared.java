package com.melodies.bandup.setup;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.melodies.bandup.R;
import com.melodies.bandup.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SetupShared {

    public Response.Listener<JSONArray> getResponseListener(final Context context, final GridView gridView, final ProgressBar progressBar) {
        return new Response.Listener<JSONArray>() {
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
    }

    public Response.Listener<JSONArray> getPickListener() {
        return new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

            }
        };
    }

    public Response.ErrorListener getErrorListener(final Context context) {
        return new Response.ErrorListener() {
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

    public void toggleItemSelection(Context context, AdapterView<?> parent, View view, int position) {
        DoubleListItem inst = (DoubleListItem) parent.getAdapter().getItem(position);
        ImageView itemSelected = (ImageView) view.findViewById(R.id.itemSelected);

        // TODO: Find a better solution
        if (itemSelected.getVisibility() == view.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.shrink);
            itemSelected.startAnimation(animation);
            itemSelected.setVisibility(view.INVISIBLE);
            inst.isSelected = false;
        }
        else {
            itemSelected.setVisibility(view.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.pop);
            itemSelected.startAnimation(animation);
            inst.isSelected = true;
        }
    }

    public void postSelectedItems(DoubleListAdapter dla, Context c, String url) {
        JSONArray selectedItems = new JSONArray();

        for (DoubleListItem dli:dla.getDoubleList()) {
            if (dli.isSelected) {
                selectedItems.put(dli.id);
            }
        }

        JsonArrayRequest postItems = new JsonArrayRequest(
                Request.Method.POST,
                url,
                selectedItems,
                this.getPickListener(),
                this.getErrorListener(c)
        );

        VolleySingleton.getInstance(c).addToRequestQueue(postItems);
    }
}
