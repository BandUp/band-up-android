package com.melodies.bandup.setup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.melodies.bandup.R;
import com.melodies.bandup.UserList;
import com.melodies.bandup.VolleySingleton;

import org.json.JSONArray;

public class Genres extends AppCompatActivity {
    private String url;
    private String route = "/genres";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getResources().getString(R.string.api_address).concat(route);
        setContentView(R.layout.activity_genres);

        final GridView gridView = (GridView)findViewById(R.id.genreGridView);
        JSONArray req = new JSONArray();
        SetupListeners sl = new SetupListeners(getBaseContext(), gridView);

        JsonArrayRequest jsonGenresRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                req,
                sl.getResponseListener(),
                sl.getErrorListener()
        );

        VolleySingleton.getInstance(this).addToRequestQueue(jsonGenresRequest);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DoubleListItem inst = (DoubleListItem)parent.getAdapter().getItem(0);
                ImageView itemSelected = (ImageView) view.findViewById(R.id.itemSelected);

                // TODO: Find a better solution
                if (itemSelected.getVisibility() == view.VISIBLE) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shrink);
                    itemSelected.startAnimation(animation);
                    itemSelected.setVisibility(view.INVISIBLE);
                    inst.isSelected = false;

                } else {
                    itemSelected.setVisibility(view.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pop);
                    itemSelected.startAnimation(animation);
                    inst.isSelected = true;
                }
            }
        });
    }

    public void onClickFinish(View v) {

        final Button btnGoToInstruments = (Button) findViewById(R.id.btnFinish);
        if (v.getId() == R.id.btnFinish) {
            Intent toInstrumentsIntent = new Intent(Genres.this, UserList.class);
            Genres.this.startActivity(toInstrumentsIntent);
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