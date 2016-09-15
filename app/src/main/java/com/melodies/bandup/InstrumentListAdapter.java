package com.melodies.bandup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bergthor on 15.9.2016.
 */
final class InstrumentListAdapter extends BaseAdapter {
    private final List<Instrument> instruments = new ArrayList();
    private final LayoutInflater mInflater;

    public InstrumentListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);

        instruments.add(new Instrument(0, "Guitar",   false));
        instruments.add(new Instrument(1, "Keyboard", false));
        instruments.add(new Instrument(2, "Bass",     false));
    }

    @Override
    public int getCount() {
        return instruments.size();
    }

    @Override
    public Instrument getItem(int i) {
        return instruments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return instruments.get(i).drawableId;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        TextView name;

        if (v == null) {
            v = mInflater.inflate(R.layout.instrument_grid_view_cell, viewGroup, false);
            v.setTag(R.id.text, v.findViewById(R.id.instrumentName));
        }

        name = (TextView) v.getTag(R.id.text);
        Instrument item = getItem(i);
        name.setText(item.name);
        return v;
    }

    private static class Instrument {
        public final String name;
        public final int drawableId;
        public Boolean isSelected;

        Instrument (int drawableId, String name, Boolean isSelected) {
            this.name = name;
            this.drawableId = drawableId;
            this.isSelected = isSelected;
        }
    }
}