package com.example.gbts.navigationdraweractivity.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.gbts.navigationdraweractivity.R;

import java.util.Calendar;

/**
 * Created by truon on 10/10/2016.
 */

public class FragmentReport extends Fragment {

    EditText edtDayStart, edtDayEnd;
    ImageView imgSeach;
    Calendar myCalendar1 = Calendar.getInstance();//start
    Calendar myCalendar2 = Calendar.getInstance();//end
    private int myear;
    private int mmonth;
    private int mday;

    static final int DATE_DIALOG_ID = 999;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_report, container, false);


        edtDayStart = (EditText) view.findViewById(R.id.edtDayStart1);

        setCurrentDateOnView(edtDayStart);
        String edtdateStart = edtDayStart.getText().toString();
        Log.d("FragmentReport", "edtdateStart " + edtdateStart);
        edtDayStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog mDate = new DatePickerDialog(getActivity(), dateStart,
                        myCalendar1.get(Calendar.YEAR),
                        myCalendar1.get(Calendar.MONTH),
                        myCalendar1.get(Calendar.DAY_OF_MONTH));
                mDate.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                mDate.show();
            }
        });


        edtDayEnd = (EditText) view.findViewById(R.id.edtDayEnd1);
        setCurrentDateEnd(edtDayEnd);
        edtDayEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog mDate = new DatePickerDialog(getActivity(), dateEnd,
                        myCalendar2.get(Calendar.YEAR),
                        myCalendar2.get(Calendar.MONTH),
                        myCalendar2.get(Calendar.DAY_OF_MONTH));
                mDate.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                mDate.show();
            }
        });

        return view;
    }

    final DatePickerDialog.OnDateSetListener dateStart = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar1.set(Calendar.YEAR, year);
            myCalendar1.set(Calendar.MONTH, monthOfYear);
            myCalendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            view.setMaxDate(System.currentTimeMillis() - 1000);
        }
    };

    final DatePickerDialog.OnDateSetListener dateEnd = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar2.set(Calendar.YEAR, year);
            myCalendar2.set(Calendar.MONTH, monthOfYear);
            myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            view.setMaxDate(System.currentTimeMillis() - 1000);
        }
    };

    // display current date
    public void setCurrentDateOnView(EditText edtStart) {
        int x = -30;
        myCalendar1.add(Calendar.DAY_OF_MONTH, x);

        myear = myCalendar1.get(Calendar.YEAR);
        mmonth = myCalendar1.get(Calendar.MONTH);
        mday = myCalendar1.get(Calendar.DAY_OF_MONTH);

        // set current date into Edit text
        edtStart.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(mday).append("/")
                .append(mmonth + 1).append("/")
                .append(myear).append(" "));
    }

    // display current date
    public void setCurrentDateEnd(EditText edtEnd) {


        myear = myCalendar2.get(Calendar.YEAR);
        mmonth = myCalendar2.get(Calendar.MONTH);
        mday = myCalendar2.get(Calendar.DAY_OF_MONTH);

        // set current date into Edit text
        edtEnd.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(mday).append("/")
                .append(mmonth + 1).append("/")
                .append(myear).append(" "));
    }

    public void addListenerOnButton(EditText edtStart) {
        edtStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onCreateDialog(DATE_DIALOG_ID);

            }

        });

    }

    protected Dialog onCreateDialog(int id) {
        final Calendar now = Calendar.getInstance();

        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                DatePickerDialog _date = new DatePickerDialog(getActivity(), datePickerListener, myear, mmonth,
                        mday) {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if (year < myear)
                            view.updateDate(myear, mmonth, mday);

                        if (monthOfYear < mmonth && year == myear)
                            view.updateDate(myear, mmonth, mday);

                        if (dayOfMonth < mday && year == myear && monthOfYear == mmonth)
                            view.updateDate(myear, mmonth, mday);
                    }
                };
                return _date;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            myear = selectedYear;
            mmonth = selectedMonth;
            mday = selectedDay;

            // set selected date into textview
            edtDayStart.setText(new StringBuilder()
                    .append("/").append(mday)
                    .append(mmonth + 1)
                    .append("/").append(myear)
                    .append(" "));

        }
    };

}
