package com.melodies.bandup;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    Integer mYear = null, mMonth = null, mDay = null;
    // values used to determine if age is within our guideline max and min values
    private static final int MIN_AGE = 13;
    private static final int MAX_AGE = 100;

    Context mContext;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        if (mYear == null) mYear = c.get(Calendar.YEAR);
        if (mMonth == null) mMonth = c.get(Calendar.MONTH);
        if (mDay == null) mDay = c.get(Calendar.DAY_OF_MONTH);

        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        @SuppressWarnings("ResourceType") DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                AlertDialog.THEME_HOLO_DARK, this, mYear, mMonth, mDay);
        datePickerDialog.setMessage(getString(R.string.register_dob_hint));

        // Get the DatePicker of the DatePickerDialog
        DatePicker datePicker = datePickerDialog.getDatePicker();

        // Set the boundaries for the DatePicker
        final Calendar maxDate = Calendar.getInstance();
        maxDate.set(Calendar.YEAR, c.get(Calendar.YEAR) - MIN_AGE);

        // Max age is 99. Then we need to subtract 100 and then add one day.
        final Calendar minDate = Calendar.getInstance();
        minDate.set(Calendar.YEAR, c.get(Calendar.YEAR) - MAX_AGE);

        // We add one day (not subtract) because adding to a min value brings us forward in time.
        minDate.add(Calendar.MILLISECOND, 86400000);

        datePicker.setMaxDate(maxDate.getTimeInMillis());
        datePicker.setMinDate(minDate.getTimeInMillis() - 1000);

        return datePickerDialog;
    }

    public void setDate(int year, int month, int day) {
        mYear = year;
        mMonth = month;
        mDay = day;
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

        return userAge.toString();
    }
}