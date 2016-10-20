package com.melodies.bandup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.melodies.bandup.MainScreenActivity.MainScreenActivity;
import com.melodies.bandup.setup.DoubleListAdapter;
import com.melodies.bandup.setup.SetupShared;

import org.json.JSONArray;

public class Filter extends AppCompatActivity {

    private GridView    gridView;
    private SetupShared sShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        sShared          = new SetupShared();
        gridView         = (GridView) findViewById(R.id.instrumentGridView);
    }

    public void onClickSave (View v) {
        if (v.getId() == R.id.btnSave) {
            DoubleListAdapter dla = (DoubleListAdapter) gridView.getAdapter();

            // The adapter for the GridView hasn't been set.
            // This means we didn't get data from the server.
            if (dla == null) {
                Toast.makeText(Filter.this, "I cannot contact the server.\nPlease contact support.", Toast.LENGTH_LONG).show();
                return;
            }

            // Send the items that the user selected to the server.
            JSONArray selectedInstruments = sShared.prepareSelectedList(Filter.this, dla);
            if (selectedInstruments.length() > 0) {
                sShared.postInstruments(Filter.this, selectedInstruments);
                Intent toUserListIntent = new Intent(Filter.this, MainScreenActivity.class);
                Filter.this.startActivity(toUserListIntent);
                overridePendingTransition(R.anim.no_change, R.anim.slide_out_left);
                finish();
            } else {
                Toast.makeText(Filter.this, R.string.setup_no_instrument_selection, Toast.LENGTH_LONG).show();
            }
        }
    }
}
