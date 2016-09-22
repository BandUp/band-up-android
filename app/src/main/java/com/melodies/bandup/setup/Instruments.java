package com.melodies.bandup.setup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.melodies.bandup.R;
import com.melodies.bandup.VolleySingleton;

import org.json.JSONArray;

public class Instruments extends AppCompatActivity {
    private String url;
    private String route = "/instruments";
    private GridView gridView;
    private ProgressBar progressBar;
    private SetupShared sShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruments);

        url         = getResources().getString(R.string.api_address).concat(route);
        gridView    = (GridView) findViewById(R.id.instrumentGridView);
        progressBar = (ProgressBar) findViewById(R.id.instrumentProgressBar);
        sShared     = new SetupShared();

        JsonArrayRequest jsonInstrumentRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                new JSONArray(),
                sShared.getResponseListener(Instruments.this, gridView, progressBar),
                sShared.getErrorListener(Instruments.this)
        );

        VolleySingleton.getInstance(this).addToRequestQueue(jsonInstrumentRequest);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sShared.toggleItemSelection(getApplicationContext(), parent, view, position);
            }
        });
    }

    public void onClickNext (View v) {
        if (v.getId() == R.id.btnNext) {
            DoubleListAdapter dla = (DoubleListAdapter) gridView.getAdapter();
            if (sShared.postSelectedItems(dla, Instruments.this, url)) {
                Intent toInstrumentsIntent = new Intent(Instruments.this, Genres.class);
                Instruments.this.startActivity(toInstrumentsIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.no_change);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!isTaskRoot()) {
            overridePendingTransition(R.anim.no_change, R.anim.slide_out_left);
        }
    }
}