package com.melodies.bandup.setup;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.melodies.bandup.R;

import java.util.ArrayList;
import java.util.List;

public final class DoubleListAdapter extends BaseAdapter {
    private List<DoubleListItem> doubleList = new ArrayList<>();
    private final LayoutInflater mInflater;
    private Context mContext;

    public DoubleListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        doubleList = new ArrayList<>();
        mContext = context;
    }
    public List<DoubleListItem> getDoubleList() {
        return doubleList;
    }

    public static float pxToDp(final Context context, final float px) {
        return px * (context.getResources().getDisplayMetrics().density);
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

    @Override
    public long getItemId(int i) {
        return doubleList.get(i).drawableId;
    }

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
        ImageView checkView;

        if (v == null) {
            v = mInflater.inflate(R.layout.item_grid_view_cell, viewGroup, false);
            v.setTag(R.id.text, v.findViewById(R.id.itemName));
            v.setTag(R.string.setup_tag_image, v.findViewById(R.id.itemBackground));
            v.setTag(R.string.setup_check_view, v.findViewById(R.id.itemSelected));
        }
        int selectedPadding = mContext.getResources().getInteger(R.integer.setup_selected_padding);
        int selectedPaddingDp = (int) pxToDp(mContext, selectedPadding);

        final int itemHeight = mContext.getResources().getInteger(R.integer.setup_item_height);
        int itemHeightDp = (int) pxToDp(mContext, itemHeight);
        int textSize = mContext.getResources().getInteger(R.integer.setup_imitial_text_size);

        DoubleListItem item = getItem(i);

        backView = (ImageView) v.getTag(R.string.setup_tag_image);
        backView.setAdjustViewBounds(true);
        backView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        name = (TextView) v.getTag(R.id.text);
        name.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/caviar_dreams_bold.ttf"));


        if (item.isSelected) {
            v.setPadding(selectedPaddingDp,selectedPaddingDp,selectedPaddingDp,selectedPaddingDp);
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bandUpYellow));
            backView.setMaxHeight(itemHeightDp - (selectedPaddingDp * 2));
            name.setTextSize(TypedValue.COMPLEX_UNIT_PX, pxToDp(mContext, textSize-(selectedPadding / 2)));

        } else {
            v.setPadding(0, 0, 0, 0);
            backView.setMaxHeight(itemHeightDp);
            name.setTextSize(TypedValue.COMPLEX_UNIT_PX, pxToDp(mContext, textSize));
        }
        backView.setImageResource(R.drawable.disco);

        name.setText(item.name);
        return v;
    }

}