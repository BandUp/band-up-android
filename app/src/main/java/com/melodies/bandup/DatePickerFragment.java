package com.melodies.bandup;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

import static com.facebook.FacebookSdk.getApplicationContext;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    Integer mYear = null, mMonth = null, mDay = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        if (mYear == null) mYear = c.get(Calendar.YEAR);
        if (mMonth == null) mMonth = c.get(Calendar.MONTH);
        if (mDay == null) mDay = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, mYear, mMonth, mDay);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        mYear = year;
        mMonth = month;
        mDay = day;

        ((DatePickable)getActivity()).onDateSet(year, month, day);
    }

    // Calculating real user age and return it
    public String ageCalculator(int year, int month, int day) {
        Calendar dayOfBirth = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dayOfBirth.set(year, month, day);
        Integer userAge = today.get(Calendar.YEAR) - dayOfBirth.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) <= dayOfBirth.get(Calendar.DAY_OF_YEAR)) {
            userAge--;
        }

        if (userAge < 13) {
            Toast.makeText(getApplicationContext(), R.string.register_min_age, Toast.LENGTH_SHORT).show();
        }
        else if (userAge > 99) {
            Toast.makeText(getApplicationContext(), R.string.register_max_age, Toast.LENGTH_SHORT).show();
        }

        String age = userAge.toString();
        return age;
    }
}