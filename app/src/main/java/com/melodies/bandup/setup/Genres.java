package com.melodies.bandup.setup;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.melodies.bandup.MainScreenActivity.MainScreenActivity;
import com.melodies.bandup.R;

import org.json.JSONArray;

/**
 * An activity class that controls the Genres view.
 */
public class Genres extends AppCompatActivity {
    private GridView gridView;
    private TextView txtTitleGetStarted, txtTitleHint, txtTitleProgress, txtNoGenres;
    private SetupShared sShared;


    private void initializeTextViews() {
        txtTitleGetStarted = (TextView) findViewById(R.id.txt_title_get_started);
        txtTitleHint       = (TextView) findViewById(R.id.txt_title_hint);
        txtTitleProgress   = (TextView) findViewById(R.id.txt_title_progress);
        txtNoGenres        = (TextView) findViewById(R.id.txtNoGenres);
    }

    private void setFonts() {
        txtTitleGetStarted.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams.ttf"));
        txtTitleProgress  .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams.ttf"));
        txtTitleHint      .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams_bold.ttf"));
        txtNoGenres       .setTypeface(Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams_bold.ttf"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);

        // The shared class between Instruments and Genres.
        sShared     = new SetupShared();

        // Find the GridView that should display the instruments.
        gridView    = (GridView) findViewById(R.id.genreGridView);

        // The spinning indicator when loading instruments.
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.genreProgressBar);
        progressBar.setVisibility(View.VISIBLE);


        initializeTextViews();
        setFonts();

        // Gets the list of genres.
        sShared.getGenres(Genres.this, gridView, progressBar, txtNoGenres);

        // What to do when an item on the GridView is clicked.
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sShared.toggleItemSelection(getApplicationContext(), parent, view, position);
            }
        });
    }

    public void onClickFinish(View v) {
        if (v.getId() == R.id.btnFinish) {
            DoubleListAdapter dla = (DoubleListAdapter) gridView.getAdapter();

            // The adapter for the GridView hasn't been set.
            // This means we didn't get data from the server.
            if (dla == null) {
                Toast.makeText(Genres.this, "I cannot contact the server.\nPlease contact support.", Toast.LENGTH_LONG).show();
                return;
            }

            // Send the items that the user selected to the server.
            JSONArray selectedGenres = sShared.prepareSelectedList(Genres.this, dla);
            if (selectedGenres.length() > 0) {
                sShared.postGenres(Genres.this, selectedGenres);
                Intent toUserListIntent = new Intent(Genres.this, MainScreenActivity.class);
                Genres.this.startActivity(toUserListIntent);
                overridePendingTransition(R.anim.no_change, R.anim.slide_out_left);
                finish();
            } else {
                Toast.makeText(Genres.this, R.string.setup_no_genre_selection, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // If there is something on the TaskRoot
        // then activities are in the background.
        if(!isTaskRoot()) {
            overridePendingTransition(R.anim.no_change, R.anim.slide_out_left);
        }
    }
}