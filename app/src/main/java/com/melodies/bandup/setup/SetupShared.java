package com.melodies.bandup.setup;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.melodies.bandup.DatabaseSingleton;
import com.melodies.bandup.R;
import com.melodies.bandup.VolleySingleton;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A shared class that both Genres and Instruments
 * use to GET and POST data to and from the server.
 */
public class SetupShared {

    int SELECTED_BORDER_SIZE = 15;

    /**
     * This function GETs instruments.
     * @param context     The context we are working in.
     * @param gridView    The GridView we are going to put the data into.
     * @param progressBar The ProgressBar that displays when we are getting the data.
     */
    public void getInstruments(Context context, GridView gridView, ProgressBar progressBar, TextView txtNoInstruments) {
        progressBar.setVisibility(progressBar.VISIBLE);
        DatabaseSingleton.getInstance(context).getBandUpDatabase().getInstruments(
                getSetupItemsListener(context, gridView, progressBar, txtNoInstruments),
                getSetupItemsErrorListener(context, progressBar));
    }

    /**
     * This function GETs genres.
     * @param context     The context we are working in.
     * @param gridView    The GridView we are going to put the data into.
     * @param progressBar The ProgressBar that displays when we are getting the data.
     */
    public void getGenres(Context context, GridView gridView, ProgressBar progressBar, TextView txtNoGenres) {
        progressBar.setVisibility(progressBar.VISIBLE);
        DatabaseSingleton.getInstance(context).getBandUpDatabase().getGenres(
                getSetupItemsListener(context, gridView, progressBar, txtNoGenres),
                getSetupItemsErrorListener(context, progressBar)
        );
    }

    public void getFilter(Context c, JSONArray filteredInstruments){
        DatabaseSingleton.getInstance(getApplicationContext()).getBandUpDatabase().postInstruments(
                filteredInstruments,
                new BandUpResponseListener() {
                    @Override
                    public void onBandUpResponse(Object response) {

                    }
                },
                new BandUpErrorListener() {
                    @Override
                    public void onBandUpErrorResponse(VolleyError error) {

                    }
                }
        );
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
    private BandUpResponseListener getSetupItemsListener(final Context context, final GridView gridView, final ProgressBar progressBar, final TextView txtNoItems) {
        return new BandUpResponseListener() {
            @Override
            public void onBandUpResponse(Object response) {
                if (response == "" || response == null) {
                    txtNoItems.setVisibility(TextView.VISIBLE);
                    return;
                }

                JSONArray responseArr = null;

                if (response instanceof JSONArray) {
                    responseArr = (JSONArray) response;
                } else {
                    txtNoItems.setVisibility(TextView.VISIBLE);
                    return;
                }

                if (responseArr.length() == 0) {
                    txtNoItems.setVisibility(TextView.VISIBLE);
                } else {
                    // Create a new adapter for the GridView.
                    DoubleListAdapter dlAdapter = new DoubleListAdapter(context);
                    gridView.setAdapter(dlAdapter);

                    // Go through every item in the list the server sent us.
                    for (int i = 0; i < responseArr.length(); i++) {
                        try {
                            JSONObject item = responseArr.getJSONObject(i);
                            String id    = item.getString("_id");
                            String name  = item.getString("name");

                            DoubleListItem dlItem = new DoubleListItem(id, i, name);
                            dlAdapter.addItem(dlItem);

                        } catch (JSONException e) {
                            Toast.makeText(context, "Could not parse the JSON object.", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
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
    private BandUpErrorListener getSetupItemsErrorListener(final Context context, final ProgressBar progressBar) {
        return new BandUpErrorListener() {
            @Override
            public void onBandUpErrorResponse(VolleyError error) {
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
     * @return True if all preconditions are met. False otherwise.
     */
    public JSONArray prepareSelectedList(Context c, DoubleListAdapter dla) {
        JSONArray selectedItems = new JSONArray();

        // Go through all items in the GridView and put its IDs into an array.
        for (DoubleListItem dli:dla.getDoubleList()) {
            if (dli.isSelected) {
                selectedItems.put(dli.id);
            }
        }

        return selectedItems;
    }

    public void postInstruments(Context c, JSONArray instrumentArr) {
        DatabaseSingleton.getInstance(getApplicationContext()).getBandUpDatabase().postInstruments(
                instrumentArr,
                getPickListener(),
                getPickErrorListener(c)
        );
    }

    public void postGenres(Context c, JSONArray genresArr) {
        DatabaseSingleton.getInstance(getApplicationContext()).getBandUpDatabase().postGenres(
                genresArr,
                getPickListener(),
                getPickErrorListener(c)
        );
    }

    /**
     * This listener is used when listening to responses to
     * POST /instruments and POST /genres.
     *
     * @return the listener
     */
    private BandUpResponseListener getPickListener() {
        return new BandUpResponseListener() {
            @Override
            public void onBandUpResponse(Object response) {

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
    private BandUpErrorListener getPickErrorListener(final Context context) {
        return new BandUpErrorListener() {
            @Override
            public void onBandUpErrorResponse(VolleyError error) {
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
    public void toggleItemSelection(final Context context, AdapterView<?> parent, final View view, int position) {
        DoubleListItem item = (DoubleListItem) parent.getAdapter().getItem(position);
        final ImageView backView = (ImageView) view.findViewById(R.id.itemBackground);
        final TextView txtName = (TextView) view.findViewById(R.id.itemName);

        // The border size around the DoubleListItem when it has been selected.
        // This value is changed in res/values/integers.xml
        int selectedPadding = context.getResources().getInteger(R.integer.setup_selected_padding);

        // We need to change pixels to display pixels for it to display the same on all devices.
        int selectedPaddingDp = (int) pixelsToDisplayPixels(context, selectedPadding);

        // The height of the DoubleListItem.
        // This value is changed in res/values/integers.xml
        final int itemHeight = context.getResources().getInteger(R.integer.setup_item_height);

        // We need to change pixels to display pixels for it to display the same on all devices.
        final int itemHeightDp = (int) pixelsToDisplayPixels(context, itemHeight);

        // The duration of the animation when selecting.
        int animDuration = context.getResources().getInteger(R.integer.setup_select_animation_time);

        // We are going to use two ValueAnimators.
        // One for animating the padding change
        // and one for animating the change of the text size.
        ValueAnimator paddingAnimator;
        ValueAnimator textSizeAnimator;

        if (!item.isSelected) {
            // We are going to animate the selection of the item.

            // Initialize the animators with the values we want to animate from and to.
            paddingAnimator  = ValueAnimator.ofInt(0, selectedPaddingDp);
            textSizeAnimator = ValueAnimator.ofFloat(txtName.getTextSize(), txtName.getTextSize()-(selectedPaddingDp/2));

            // Set custom AnimatorUpdateListeners
            paddingAnimator .addUpdateListener(this.getPaddingChangeListener(backView, view, itemHeightDp));
            textSizeAnimator.addUpdateListener(this.getTextSizeChangeListener(txtName));

            paddingAnimator .setDuration(animDuration);
            textSizeAnimator.setDuration(animDuration);

            view.setBackgroundColor(ContextCompat.getColor(context, R.color.bandUpYellow));

            // Start both animators at the same time.
            paddingAnimator.start();
            textSizeAnimator.start();

            // And finally keep track in the DoubleListItem
            item.isSelected = true;

        } else {
            // We are going to animate the deselection of the item.

            // Initialize the animators with the values we want to animate from and to.
            paddingAnimator  = ValueAnimator.ofInt(selectedPaddingDp, 0);
            textSizeAnimator = ValueAnimator.ofFloat(txtName.getTextSize(), txtName.getTextSize()+(selectedPaddingDp/2));

            // Set custom AnimatorUpdateListeners
            paddingAnimator .addUpdateListener(this.getPaddingChangeListener(backView, view, itemHeightDp));
            textSizeAnimator.addUpdateListener(this.getTextSizeChangeListener(txtName));

            paddingAnimator.setDuration(animDuration);
            textSizeAnimator.setDuration(animDuration);

            // Start both animators at the same time.
            paddingAnimator.start();
            textSizeAnimator.start();

            // And finally keep track in the DoubleListItem
            item.isSelected = false;
        }
    }

    private ValueAnimator.AnimatorUpdateListener getPaddingChangeListener(final ImageView backView, final View view, final int itemHeightDp) {
        return new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // A new step of the animation has arrived.
                // Let's extract it and use it.
                int animVal = (Integer) valueAnimator.getAnimatedValue();
                // We need to shrink the image view to give space for the padding border.
                backView.setMaxHeight(itemHeightDp - (animVal * 2));
                // Set the padding equally on all sides.
                view.setPadding(animVal, animVal, animVal, animVal);
            }
        };
    }

    private ValueAnimator.AnimatorUpdateListener getTextSizeChangeListener(final TextView textView) {
        return new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator){
                // A new step of the animation has arrived.
                // Let's extract it and use it.
                float animVal = (Float) valueAnimator.getAnimatedValue();
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, animVal);
            }
        };
    }

    public static float pixelsToDisplayPixels(final Context context, final float px) {
        return px * (context.getResources().getDisplayMetrics().density);
    }
}