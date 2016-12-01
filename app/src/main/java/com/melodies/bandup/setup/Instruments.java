package com.melodies.bandup.setup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.melodies.bandup.R;

import org.json.JSONArray;

import java.util.List;


/**
 * An activity class that controls the Instruments view.
 */
public class Instruments extends AppCompatActivity {
    private TextView txtTitleGetStarted, txtTitleHint, txtTitleProgress, txtNoInstruments;
    private GridView    gridView;
    private SetupShared sShared;
    private AdView mAdView;

    private void initializeTextViews() {
        txtTitleGetStarted = (TextView) findViewById(R.id.txt_title_get_started);
        txtTitleHint       = (TextView) findViewById(R.id.txt_title_hint);
        txtTitleProgress   = (TextView) findViewById(R.id.txt_title_progress);
        txtNoInstruments   = (TextView) findViewById(R.id.txtNoInstruments);
        mAdView            = (AdView)   findViewById(R.id.adView);
    }

    private void setFonts() {
        txtTitleGetStarted.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams.ttf"));
        txtTitleProgress  .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams.ttf"));
        txtTitleHint      .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams_bold.ttf"));
        txtNoInstruments  .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams_bold.ttf"));
    }

    Boolean isSetup = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        List<String> preselectedItems = null;
        if (extras != null) {
            isSetup = extras.getBoolean("IS_SETUP_PROCESS");
            preselectedItems = extras.getStringArrayList("PRESELECTED_ITEMS");
        }
        setContentView(R.layout.activity_instruments);

        // The shared class between Instruments and Genres.
        sShared  = new SetupShared();

        // Find the GridView that should display the instruments.
        gridView = (GridView) findViewById(R.id.instrumentGridView);

        // The spinning indicator when loading instruments.
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.instrumentProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        initializeTextViews();
        setFonts();

        Button btnNext = (Button) findViewById(R.id.btnNext);

        if (!isSetup) {
            txtTitleGetStarted.setText("");
            txtTitleProgress.setText("");
            btnNext.setText(R.string.edit_instrument_genres_save);

            sShared.getInstruments(Instruments.this, gridView, progressBar, txtNoInstruments, preselectedItems);
        } else {
            // Gets the list of instruments.
            sShared.getInstruments(Instruments.this, gridView, progressBar, txtNoInstruments, null);
        }


        // What to do when an item on the GridView is clicked.
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sShared.toggleItemSelection(getApplicationContext(), parent, view, position);
            }
        });

        // Adding ad Banner
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void onClickNext(View v) {
        if (v.getId() == R.id.btnNext) {
            DoubleListAdapter dla = (DoubleListAdapter) gridView.getAdapter();

            // The adapter for the GridView hasn't been set.
            // This means we didn't get data from the server.
            if (dla == null) {
                Toast.makeText(Instruments.this, "I cannot contact the server.\nPlease contact support.", Toast.LENGTH_LONG).show();
                return;
            }

            // Send the items that the user selected to the server.
            JSONArray selectedInstruments = sShared.prepareSelectedList(Instruments.this, dla);
            if (selectedInstruments.length() > 0) {
                sShared.postInstruments(Instruments.this, selectedInstruments);
                if (isSetup) {
                    Intent toUserListIntent = new Intent(Instruments.this, Genres.class);
                    Instruments.this.startActivity(toUserListIntent);
                    overridePendingTransition(R.anim.no_change, R.anim.slide_out_left);
                } else {
                    Intent resultIntent = new Intent();
                    resultIntent.putStringArrayListExtra("SELECTED_INSTRUMENTS", sShared.prepareSelectedListNames(Instruments.this, dla));
                    setResult(Activity.RESULT_OK, resultIntent);
                }

                finish();
            } else {
                Toast.makeText(Instruments.this, R.string.setup_no_instrument_selection, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // If there is something on the TaskRoot
        // then activities are in the background.
        if (!isTaskRoot()) {
            overridePendingTransition(R.anim.no_change, R.anim.slide_out_left);
        }
    }
}