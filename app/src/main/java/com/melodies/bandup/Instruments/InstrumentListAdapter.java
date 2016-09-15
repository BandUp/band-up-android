package com.melodies.bandup.Instruments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.melodies.bandup.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bergthor on 15.9.2016.
 */
final class InstrumentListAdapter extends BaseAdapter {
    private List<Instrument> instruments = new ArrayList();
    private final LayoutInflater mInflater;

    public InstrumentListAdapter(Context context, List<Instrument> list) {
        mInflater = LayoutInflater.from(context);
        this.instruments = list;
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
        CheckBox isSelected;

        if (v == null) {
            v = mInflater.inflate(R.layout.instrument_grid_view_cell, viewGroup, false);
            v.setTag(R.id.text, v.findViewById(R.id.instrumentName));
            v.setTag(R.id.instrumentSelected, v.findViewById(R.id.instrumentSelected));
        }

        name = (TextView) v.getTag(R.id.text);
        Instrument item = getItem(i);
        name.setText(item.name);
        return v;
    }

    public static class Instrument {
        public final String name;
        public final int drawableId;
        public Boolean isSelected;

        Instrument (int drawableId, String name) {
            this.name = name;
            this.drawableId = drawableId;
            this.isSelected = false;
        }
    }
}