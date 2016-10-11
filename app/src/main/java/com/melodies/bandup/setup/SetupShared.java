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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.melodies.bandup.JsonArrayToObjectRequest;
import com.melodies.bandup.R;
import com.melodies.bandup.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A shared class that both Genres and Instruments
 * use to GET and POST data to and from the server.
 */
public class SetupShared {


    /**
     * This function GETs instruments or genres, depending on the URL.
     * @param context     The context we are working in.
     * @param url         The URL where we are going to GET the data.
     * @param gridView    The GridView we are going to put the data into.
     * @param progressBar The ProgressBar that displays when we are getting the data.
     */
    public void getSetupItems(Context context, String url, GridView gridView, ProgressBar progressBar) {
        progressBar.setVisibility(progressBar.VISIBLE);
        JsonArrayRequest jsonInstrumentRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                new JSONArray(),
                this.getSetupItemsListener(context, gridView, progressBar),
                this.getSetupItemsErrorListener(context, progressBar)
        );

        VolleySingleton.getInstance(context).addToRequestQueue(jsonInstrumentRequest);
    }

    /**
     * This listener is used when listening to responses to
     * GET /instruments and GET /genres.
     *
     * The progress bar's visibility must be set before sending the request.
     *
     * It parses the JSON object and puts every item into an array in the DoubleListAdapter.
     *
     * {
     *     "_id":"57dafe54dcba0f51172fb163"  The ID of the instrument/genre
     *     "name":"Drums"                    The name of the instrument/genre
     * }
     *
     * @param context
     * @param gridView     The GridView that will be displaying the data.
     * @param progressBar  A ProgressBar that will be displayed when fetching data.
     * @return             the listener
     * @see DoubleListAdapter
     */
    private Response.Listener<JSONArray> getSetupItemsListener(final Context context, final GridView gridView, final ProgressBar progressBar) {
        return new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                // Create a new adapter for the GridView.
                DoubleListAdapter dlAdapter = new DoubleListAdapter(context);
                gridView.setAdapter(dlAdapter);

                // Go through every item in the list the server sent us.
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject item = response.getJSONObject(i);
                        String id    = item.getString("_id");
                        String name  = item.getString("name");

                        DoubleListItem dlItem = new DoubleListItem(id, i, name);
                        dlAdapter.addItem(dlItem);

                    } catch (JSONException e) {
                        Toast.makeText(context, "Could not parse the JSON object.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                progressBar.setVisibility(progressBar.GONE);
            }
        };
    }

    /**
     * This error listener is used when listening to responses to
     * GET /instruments and GET /genres.
     *
     * The progress bar's visibility must be set before sending the request.
     *
     * @param context     The context we are working in.
     * @param progressBar The ProgressBar in the view.
     * @return the listener.
     */
    private Response.ErrorListener getSetupItemsErrorListener(final Context context, final ProgressBar progressBar) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleySingleton.getInstance(context).checkCauseOfError(error);
                progressBar.setVisibility(progressBar.GONE);
            }
        };
    }

    /**
     * This function POSTs the selected items to the server.
     *
     * @param c   The context we are working in.
     * @param dla The DoubleListAdapter we want to read from.
     * @param url The URL where we are going to POST the data.
     * @return True if all preconditions are met. False otherwise.
     */
    public Boolean postSelectedItems(Context c, DoubleListAdapter dla, String url) {
        JSONArray selectedItems = new JSONArray();

        // Go through all items in the GridView and put its IDs into an array.
        for (DoubleListItem dli:dla.getDoubleList()) {
            if (dli.isSelected) {
                selectedItems.put(dli.id);
            }
        }

        if (selectedItems.length() == 0) {
            Toast.makeText(c, "You need to select at least one item.", Toast.LENGTH_LONG).show();
            return false;
        }

        JsonArrayToObjectRequest postItems = new JsonArrayToObjectRequest(
                Request.Method.POST,
                url,
                selectedItems,
                this.getPickListener(),
                this.getPickErrorListener(c)
        );

        VolleySingleton.getInstance(c).addToRequestQueue(postItems);
        return true;
    }

    /**
     * This listener is used when listening to responses to
     * POST /instruments and POST /genres.
     *
     * @return the listener
     */
    private Response.Listener<JSONObject> getPickListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        };
    }

    /**
     * This error listener is used when listening to responses to
     * POST /instruments and POST /genres.
     *
     * @param context The context we are working in.
     * @return the listener.
     */
    private Response.ErrorListener getPickErrorListener(final Context context) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleySingleton.getInstance(context).checkCauseOfError(error);
            }
        };
    }

    /**
     * This toggles the selected/deselected state of an item
     * in the list when the user taps on a particular item.
     *
     * @param context  The context we are working in.
     * @param parent   The parent of the adapter. (?)
     * @param view     The view we are working with.
     * @param position The index of the item that was tapped.
     */
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
}