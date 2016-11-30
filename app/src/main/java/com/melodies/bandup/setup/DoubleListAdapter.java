package com.melodies.bandup.setup;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.melodies.bandup.R;

import java.util.ArrayList;
import java.util.List;

public final class DoubleListAdapter extends BaseAdapter {
    private List<DoubleListItem> doubleList = new ArrayList<>();
    private final LayoutInflater mInflater;
    private Context mContext;
    private SetupShared sShared;

    /**
     * Constructor for the DoubleListAdapter
     * @param context
     */
    public DoubleListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        doubleList = new ArrayList<>();
        mContext = context;
        sShared = new SetupShared();
    }

    /**
     * @return A list of items currently in the adapter.
     * @see DoubleListItem
     */
    public List<DoubleListItem> getDoubleList() {
        return doubleList;
    }


    /**
     * Returns the number of items in the list.
     *
     * @return      Number of items in the list.
     */
    @Override
    public int getCount() {
        return doubleList.size();
    }

    /**
     * Returns a DoubleListItem.
     *
     * @param  i    the index of the item in question
     * @return      the DoubleListItem at the index 'i'
     * @see         DoubleListItem
     */
    @Override
    public DoubleListItem getItem(int i) {
        return doubleList.get(i);
    }

    /**
     * Returns the item Drawable ID
     * @param i
     * @return
     */
    @Override
    public long getItemId(int i) {
        return doubleList.get(i).drawableId;
    }

    /**
     * Add an item into the GridView
     * @param doubleListItem
     * @return True if adding was successful. False otherwise.
     */
    public Boolean addItem(DoubleListItem doubleListItem) {

        if (doubleListItem == null) {
            return false;
        } else {
            doubleList.add(doubleListItem);
            return true;
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        TextView name;
        ImageView backView;

        // Check if we are creating a new view.
        if (v == null) {
            // Create a new view
            v = mInflater.inflate(R.layout.item_grid_view_cell, viewGroup, false);
            v.setTag(R.id.text, v.findViewById(R.id.itemName));
            v.setTag(R.string.setup_tag_image, v.findViewById(R.id.itemBackground));
            v.setTag(R.string.setup_check_view, v.findViewById(R.id.itemSelected));
        }

        // The border size around the DoubleListItem when it has been selected.
        // This value is changed in res/values/integers.xml
        int selectedPadding = mContext.getResources().getInteger(R.integer.setup_selected_padding);

        // We need to change pixels to display pixels for it to display the same on all devices.
        int selectedPaddingDp = (int) sShared.pixelsToDisplayPixels(mContext, selectedPadding);

        GridView gv = (GridView)viewGroup;
        final int itemWidth = gv.getColumnWidth();

        // The height of the DoubleListItem.
        // This value is changed in res/values/integers.xml
        final double itemHeight = (mContext.getResources().getInteger(R.integer.setup_item_height));

        final double actualItemHeight = itemWidth*(itemHeight/100.0);

        // We need to change pixels to display pixels for it to display the same on all devices.
        int itemHeightDp = (int) actualItemHeight;

        // The size of the text in the view.
        // This value is changed in res/values/integers.xml
        int textSize = mContext.getResources().getInteger(R.integer.setup_initial_text_size);

        // We are ready to display the item.
        DoubleListItem item = getItem(i);

        // Find the background image view and set properties.
        backView = (ImageView) v.getTag(R.string.setup_tag_image);
        backView.setAdjustViewBounds(true);
        backView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Find the TextView where the name of the item is displayed and set properties.
        name = (TextView) v.getTag(R.id.text);
        name.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/caviar_dreams_bold.ttf"));



        if (item.isSelected) {
            // If the user has selected the item.

            // The padding will give the selected effect along with the background color.
            // These values need to be the same as the end values of the animations.
            // Because when we scroll and the view goes off the screen, then on screen again,
            // the view will not have the same appearance.
            v.setPadding(selectedPaddingDp,selectedPaddingDp,selectedPaddingDp,selectedPaddingDp);
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bandUpYellow));
            backView.setMaxHeight(itemHeightDp - (selectedPaddingDp * 2));
            name.setTextSize(TypedValue.COMPLEX_UNIT_PX, sShared.pixelsToDisplayPixels(mContext, textSize-(selectedPadding / 2)));

        } else {
            // If the user has not selected the item.
            // We must do this because we are reusing views that have gone off the screen.
            v.setPadding(0, 0, 0, 0);
            backView.setMaxHeight(itemHeightDp);
            name.setTextSize(TypedValue.COMPLEX_UNIT_PX, sShared.pixelsToDisplayPixels(mContext, textSize));
        }
        // Put an image in the background.
        backView.setImageResource(R.drawable.disco);

        name.setText(item.name);
        return v;
    }
}