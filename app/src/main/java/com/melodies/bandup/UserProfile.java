package com.melodies.bandup;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

public class UserProfile extends AppCompatActivity {

    UserListController ulc = new UserListController();
    private TextView txtName;
    private TextView txtInstruments;
    private TextView txtGenres;
    private TextView txtStatus;
    private TextView txtDistance;
    private TextView txtPercentage;
    private TextView txtAboutMe;
    private TextView txtPromotion;

    private SeekBar seekBarRadius;
    private TextView txtSeekValue;  // displaying searching value
    private int progressMinValue = 1;       // Min 1 Km radius
    private int getProgressMaxValue = 25;   // Max 10 Km radius

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        txtName        = (TextView) findViewById(R.id.txtName);
        txtStatus      = (TextView) findViewById(R.id.txtStatus);
        txtDistance    = (TextView) findViewById(R.id.txtDistance);
        txtInstruments = (TextView) findViewById(R.id.txtInstruments);
        txtGenres      = (TextView) findViewById(R.id.txtGenres);
        txtPercentage  = (TextView) findViewById(R.id.txtPercentage);
        txtSeekValue   = (TextView)findViewById(R.id.txtSeekValue);
        txtAboutMe     = (TextView) findViewById(R.id.txtAboutMe);
        txtPromotion   = (TextView) findViewById(R.id.txtPromotion);


        seekBarRadius  = (SeekBar)  findViewById(R.id.seekBarRadius);
        seekBarRadius.setMax(getProgressMaxValue);
        seekBarRadius.setProgress(progressMinValue);
        txtSeekValue.setText(progressMinValue + " km");

        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressMinValue = i;
                txtSeekValue.setText(progressMinValue + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


}
