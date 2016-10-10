package com.example.gbts.navigationdraweractivity.fragment;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.adapter.ReportAdapter;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.enity.ReportEntity;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by truon on 10/9/2016.
 */

public class GetReport extends Fragment {
    EditText edtDayStart, edtDayEnd;
    ImageView imgSeach;
    Calendar myCalendar = Calendar.getInstance();
    Calendar myCalendar2 = Calendar.getInstance();

    ListView listView;
    ReportAdapter mReportAdapter;
    List<ReportEntity> reportEntityList;
    ReportEntity reportEntity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_report, container, false);


        edtDayStart = (EditText) view.findViewById(R.id.edtDayStart);
        edtDayStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), date2, myCalendar2
                        .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                        myCalendar2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        edtDayEnd = (EditText) view.findViewById(R.id.edtDayEnd);
        edtDayEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        imgSeach = (ImageView) view.findViewById(R.id.imgSearchReport);
        imgSeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get phone number
                SharedPreferences preferences = getActivity().getSharedPreferences("Info", Context.MODE_PRIVATE);
                String phone = preferences.getString("Phonenumber", "Emty phone number");

                String beginDay = edtDayStart.getText().toString();
                String endDay = edtDayEnd.getText().toString();

                new AsyncGetReport().execute(phone, beginDay, endDay);
            }
        });

        return view;
    }

    //ASYNC TASK GETREPORT
    private class AsyncGetReport extends AsyncTask<String, Void, JSONObject> {
        String url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            //Call json parser API
            JSONParser jsonParser = new JSONParser();
            url = Constance.API_GET_REPORT + "&phone=" + params[0] +
                    "&beginDate=" + params[1] +
                    "&endDate=" + params[2];
            JSONObject json = jsonParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            //instance new Arraylist<ReportEntity>
            reportEntityList = new ArrayList<>();
            Log.d("GetReport", "url " + url);
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject json = jsonArray.getJSONObject(i);
                    reportEntity = new ReportEntity();

                    reportEntity.setRpCardName(json.optString("CardName"));
                    reportEntity.setRpTotal(json.optString("Total"));
                    reportEntity.setRpBoughtDated(json.optString("BoughtDated"));
                    reportEntity.setRpBusCode(json.optString("BusCode"));

                    Log.d("GetReport", "CardName  " + json.optString("CardName"));
                    Log.d("GetReport", "Total  " + json.optString("Total"));
                    Log.d("GetReport", "BoughtDated " + json.optString("BoughtDated"));
                    Log.d("GetReport", "BusCode " + json.optString("BusCode"));


                    reportEntityList.add(reportEntity);
                }

                listView = (ListView) getView().findViewById(R.id.listView_GetReport);
                mReportAdapter = new ReportAdapter(getActivity(), reportEntityList);
                listView.setAdapter(mReportAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar2.set(Calendar.YEAR, year);
            myCalendar2.set(Calendar.MONTH, monthOfYear);
            myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel2();
        }

    };

    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edtDayEnd.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabel2() {

//        String myFormat = "MM/dd/yyyy"; //In which you need put here
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edtDayStart.setText(sdf.format(myCalendar2.getTime()));
    }

}
