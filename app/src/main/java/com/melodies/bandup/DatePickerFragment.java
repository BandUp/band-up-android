package com.melodies.bandup;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

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
}