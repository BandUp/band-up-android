package com.melodies.bandup.Instruments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.melodies.bandup.Genres;
import com.melodies.bandup.R;
import com.melodies.bandup.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Instruments extends AppCompatActivity {
    private String url = "https://band-up-server.herokuapp.com/instruments";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruments);
        final GridView gridView = (GridView)findViewById(R.id.instrumentGridView);

        JSONArray req = new JSONArray();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                req,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<InstrumentListAdapter.Instrument> list = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {

                            try {
                                JSONObject instrument = response.getJSONObject(i);
                                InstrumentListAdapter.Instrument myInst = new InstrumentListAdapter.Instrument(instrument.getInt("order"), instrument.getString("name"));
                                list.add(myInst);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        gridView.setAdapter(new InstrumentListAdapter(getBaseContext(), list));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }
        );
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InstrumentListAdapter.Instrument inst = (InstrumentListAdapter.Instrument)parent.getAdapter().getItem(0);
                ImageView instrumentSelected = (ImageView) view.findViewById(R.id.instrumentSelected);

                // TODO: Find a better solution
                if (instrumentSelected.getVisibility() == view.VISIBLE) {
                    instrumentSelected.setVisibility(view.INVISIBLE);
                    inst.isSelected = false;

                } else {
                    instrumentSelected.setVisibility(view.VISIBLE);
                    inst.isSelected = true;
                }

            }
        });
    }
    public void onClickNext (View v) {
        final Button btnGoToInstruments = (Button) findViewById(R.id.btnNext);
        if (v.getId() == R.id.btnNext) {
            Intent toInstrumentsIntent = new Intent(Instruments.this, Genres.class);
            Instruments.this.startActivity(toInstrumentsIntent);
        }
    }
}