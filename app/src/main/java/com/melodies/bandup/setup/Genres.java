package com.melodies.bandup.setup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.melodies.bandup.R;
import com.melodies.bandup.UserList;
import com.melodies.bandup.VolleySingleton;

import org.json.JSONArray;

public class Genres extends AppCompatActivity {
    private String url;
    private String route = "/genres";
    private GridView gridView;
    private SetupListeners sl;
    private ProgressBar progressBar;
    private SetupShared ss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);

        url = getResources().getString(R.string.api_address).concat(route);
        gridView = (GridView)findViewById(R.id.genreGridView);
        progressBar = (ProgressBar) findViewById(R.id.genreProgressBar);
        sl = new SetupListeners(getBaseContext(), gridView, progressBar);
        ss = new SetupShared();

        JsonArrayRequest jsonGenreRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                new JSONArray(),
                sl.getResponseListener(),
                sl.getErrorListener()
        );

        VolleySingleton.getInstance(this).addToRequestQueue(jsonGenreRequest);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ss.onItemClick(getApplicationContext(), parent, view, position, id);
            }
        });
    }

    public void onClickFinish(View v) {
        final Button btnGoToInstruments = (Button) findViewById(R.id.btnFinish);
        if (v.getId() == R.id.btnFinish) {
            DoubleListAdapter dla = (DoubleListAdapter) gridView.getAdapter();
            ss.postSelectedItems(dla, sl, this, url);
            Intent toUserListIntent = new Intent(Genres.this, UserList.class);
            Genres.this.startActivity(toUserListIntent);
            overridePendingTransition(R.anim.no_change, R.anim.slide_out_left);
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