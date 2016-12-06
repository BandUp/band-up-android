package com.melodies.bandup.MainScreenActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.melodies.bandup.DatabaseSingleton;
import com.melodies.bandup.DatePickable;
import com.melodies.bandup.DatePickerFragment;
import com.melodies.bandup.R;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.melodies.bandup.setup.Genres;
import com.melodies.bandup.setup.Instruments;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.melodies.bandup.MainScreenActivity.ProfileFragment.DEFAULT;

public class UpdateAboutMe extends AppCompatActivity implements DatePickable {

    private static final int EDIT_PROFILE_REQUEST_CODE = 3929;
    private AdView mAdView;

    String mId;
    String mName;
    Calendar mDateOfBirth;
    String mFavoriteInstrument;
    ArrayList<String> mInstruments;
    ArrayList<String> mGenres;
    String mAboutMe;
    private DatePickerFragment datePickerFragment = null;

    int EDIT_INSTRUMENTS_REQUEST_CODE = 4939;
    int EDIT_GENRES_REQUEST_CODE = 4989;

    private EditText etName, etDateOfBirth, etFavouriteInstrument, etInstruments, etGenres, etAboutMe;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateaboutme);
        setTitle(getResources().getString(R.string.main_title_edit_profile));
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mId                 = extras.getString         ("USER_ID");
            mName               = extras.getString         ("USER_NAME");
            mFavoriteInstrument = extras.getString         ("USER_FAVOURITE_INSTRUMENT");
            mInstruments        = extras.getStringArrayList("USER_INSTRUMENTS");
            mGenres             = extras.getStringArrayList("USER_GENRES");
            mAboutMe            = extras.getString         ("USER_ABOUT_ME");

            Date dob;
            dob = (Date) getIntent().getSerializableExtra("USER_DATE_OF_BIRTH");
            if (dob != null) {
                mDateOfBirth = Calendar.getInstance();
                mDateOfBirth.setTime(dob);
            }
        }

        if (datePickerFragment == null) {
            datePickerFragment = new DatePickerFragment();
        }

        etName = (EditText) findViewById(R.id.etName);
        etDateOfBirth = (EditText) findViewById(R.id.etDateOfBirth);
        etFavouriteInstrument = (EditText) findViewById(R.id.etFavouriteInstrument);
        etInstruments = (EditText) findViewById(R.id.etInstruments);
        etGenres = (EditText) findViewById(R.id.etGenres);
        etAboutMe = (EditText) findViewById(R.id.etAboutMe);

        etInstruments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent instrumentsIntent = new Intent(UpdateAboutMe.this, Instruments.class);
                instrumentsIntent.putExtra("IS_SETUP_PROCESS", false);
                instrumentsIntent.putStringArrayListExtra("PRESELECTED_ITEMS", (ArrayList<String>) mInstruments);
                startActivityForResult(instrumentsIntent, EDIT_INSTRUMENTS_REQUEST_CODE);
            }
        });

        etGenres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent genresIntent = new Intent(UpdateAboutMe.this, Genres.class);
                genresIntent.putExtra("IS_SETUP_PROCESS", false);
                genresIntent.putStringArrayListExtra("PRESELECTED_ITEMS", (ArrayList<String>) mGenres);
                startActivityForResult(genresIntent, EDIT_GENRES_REQUEST_CODE);
            }
        });

        etFavouriteInstrument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder fs = new AlertDialog.Builder(UpdateAboutMe.this);
                fs.setTitle(R.string.update_about_me_favorite_instrument);

                //final User currUser = ((MainScreenActivity) getActivity()).currentUser;
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        UpdateAboutMe.this,
                        android.R.layout.select_dialog_singlechoice);

                for (int i = 0; i < mInstruments.size(); i++) {
                    arrayAdapter.add(mInstruments.get(i));
                }
                fs.setNegativeButton(
                        R.string.update_about_me_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int inst) {
                                dialog.dismiss();
                            }
                        });

                fs.setAdapter(
                        arrayAdapter,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int inst) {
                                String instrument = arrayAdapter.getItem(inst);
                                //updateUser(mId, "favoriteinstrument", instrument);
                                etFavouriteInstrument.setText(instrument);
                                //((MainScreenActivity)getActivity()).updateFavouriteInstrument(instrument);
                            }
                        });
                fs.show();
            }
        });

        etDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDateOfBirth == null) {
                    mDateOfBirth = Calendar.getInstance();
                }
                int year  = mDateOfBirth.get(Calendar.YEAR);
                int month = mDateOfBirth.get(Calendar.MONTH);
                int day   = mDateOfBirth.get(Calendar.DAY_OF_MONTH);
                datePickerFragment.setDate(year, month, day);
                datePickerFragment.show(UpdateAboutMe.this.getFragmentManager(), "DatePicker");
            }
        });

        if (mName != null && etName != null) {
            etName.setText(mName);
        }

        if (mDateOfBirth != null && etDateOfBirth != null) {
            updateAgeText();
        }

        if (mFavoriteInstrument != null && etFavouriteInstrument != null) {
            etFavouriteInstrument.setText(mFavoriteInstrument);
        }

        updateItems(mInstruments, etInstruments);
        updateItems(mGenres, etGenres);

        if (mAboutMe != null && etAboutMe != null) {
            etAboutMe.setText(mAboutMe);
        }


        // back to activity before
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Adding ad Banner
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView = (AdView)findViewById(R.id.adView);
        mAdView.loadAd(adRequest);
    }

    private void updateAgeText() {
        // Get the locale date format.
        if (mDateOfBirth != null)
        {
            java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(UpdateAboutMe.this);
            // Formatted date.
            String date = dateFormat.format(mDateOfBirth.getTime());

            String age = datePickerFragment.ageCalculator(mDateOfBirth.get(Calendar.YEAR), mDateOfBirth.get(Calendar.MONTH), mDateOfBirth.get(Calendar.DAY_OF_MONTH));

            String dateString = String.format("%s (%s)", date, age);
            etDateOfBirth.setText(dateString);
        }


    }

    @Override
    public void onDateSet(int year, int month, int day) {
        mDateOfBirth.set(Calendar.YEAR, year);
        mDateOfBirth.set(Calendar.MONTH, month);
        mDateOfBirth.set(Calendar.DAY_OF_MONTH, day);

        updateAgeText();
    }

    private void updateItems(ArrayList<String> list, EditText textField) {
        if (list != null && textField != null) {
            String genreString = "";
            for (int i = 0; i < list.size(); i++) {
                genreString = genreString.concat(list.get(i));
                if (i != list.size()-1) genreString = genreString.concat(", ");
            }
            textField.setText(genreString);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_INSTRUMENTS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Bundle extras = data.getExtras();
                ArrayList<String> list = extras.getStringArrayList("SELECTED_INSTRUMENTS");
                if (list != null) {
                    mInstruments = list;
                    updateItems(mInstruments, etInstruments);
                }
            }
        } else if (requestCode == EDIT_GENRES_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                ArrayList<String> list = extras.getStringArrayList("SELECTED_GENRES");
                if (list != null) {
                    mGenres = list;
                    updateItems(mGenres, etGenres);
                }
            }
        }
    }

    // Return to previous Activity
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    // Get the userid of logged in user
    public String getUserId() throws JSONException {
        SharedPreferences srdPref = getSharedPreferences("UserIdRegister", Context.MODE_PRIVATE);
        String id = srdPref.getString("userID", DEFAULT);
        return (!id.equals(DEFAULT)) ? id : "User ID Not Found";
    }

    // Send user AboutMe data to server
    public void updateUser() {
        JSONObject userUpdated = new JSONObject();
        try {
            userUpdated.put("_id", mId);
            userUpdated.put("aboutme", etAboutMe.getText().toString());
            userUpdated.put("username", etName.getText().toString());
            userUpdated.put("favoriteinstrument", etFavouriteInstrument.getText().toString());
            userUpdated.put("dateOfBirth", mDateOfBirth.getTime().toString());

            DatabaseSingleton.getInstance(this).getBandUpDatabase().updateUser(userUpdated, new BandUpResponseListener() {
            @Override
            public void onBandUpResponse(Object response) {
                // we were successful send about me data to previous view:
                Intent i = new Intent();

                i.putExtra               ("USER_ID", mId);
                i.putExtra               ("USER_NAME", etName.getText().toString());
                i.putExtra               ("USER_FAVOURITE_INSTRUMENT", etFavouriteInstrument.getText().toString());
                i.putStringArrayListExtra("USER_INSTRUMENTS", mInstruments);
                i.putStringArrayListExtra("USER_GENRES", mGenres);
                i.putExtra               ("USER_ABOUT_ME", etAboutMe.getText().toString());
                i.putExtra               ("USER_DATE_OF_BIRTH", mDateOfBirth.getTime());
                
                setResult(EDIT_PROFILE_REQUEST_CODE, i);
                finish();
            }
            }, new BandUpErrorListener() {
                @Override
                public void onBandUpErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error" + error, Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Update About Me and send it to server
    public void onClickSave(View view) throws JSONException {
        updateUser();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
