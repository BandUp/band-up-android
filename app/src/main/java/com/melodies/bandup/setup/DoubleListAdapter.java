package com.melodies.bandup.setup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.melodies.bandup.R;

import java.util.ArrayList;
import java.util.List;

final class DoubleListAdapter extends BaseAdapter {
    private List<DoubleListItem> doubleList = new ArrayList<>();
    private final LayoutInflater mInflater;

    public List<DoubleListItem> getDoubleList() {
        return doubleList;
    }

    public DoubleListAdapter(Context context, List<DoubleListItem> list) {
        mInflater = LayoutInflater.from(context);
        this.doubleList = list;
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        TextView name;

        if (v == null) {
            v = mInflater.inflate(R.layout.item_grid_view_cell, viewGroup, false);
            v.setTag(R.id.text, v.findViewById(R.id.itemName));
        }

        name = (TextView) v.getTag(R.id.text);
        DoubleListItem item = getItem(i);
        name.setText(item.name);
        return v;
    }

}