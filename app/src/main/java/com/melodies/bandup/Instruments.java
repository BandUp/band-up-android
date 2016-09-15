package com.melodies.bandup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

public class Instruments extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruments);
        GridView gridView = (GridView)findViewById(R.id.instrumentGridView);
        gridView.setAdapter(new InstrumentListAdapter(this));
    }
}
