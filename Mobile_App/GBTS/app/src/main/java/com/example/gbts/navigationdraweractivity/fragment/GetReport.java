package com.example.gbts.navigationdraweractivity.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.adapter.ReportAdapter;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.enity.ReportEntity;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.example.gbts.navigationdraweractivity.utils.Utility;

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

/**
 * Created by truon on 10/9/2016.
 */

public class GetReport extends Fragment {
    final String TAG = "GetReport";
    private static final String TAG_FRAGMENT = "MainContent";
    EditText edtDayStart, edtDayEnd;
    ImageView imgSeach;
    final Calendar myCalendar1 = Calendar.getInstance();//start
    final Calendar myCalendar2 = Calendar.getInstance();//end
    private int myear;
    private int mmonth;
    private int mday;
    ListView listView;
    ReportAdapter mReportAdapter;
    List<ReportEntity> reportEntityList;
    ReportEntity reportEntity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_report, container, false);


        edtDayStart = (EditText) view.findViewById(R.id.edtDayStart);
        setCurrentDateStart(edtDayStart);
        final String edtdateStart = edtDayStart.getText().toString();
        Log.d(TAG, "edtdateStart" + edtdateStart);
        edtDayStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog mDate = new DatePickerDialog(getActivity(), dateStart,
                        myCalendar1.get(Calendar.YEAR),
                        myCalendar1.get(Calendar.MONTH),
                        myCalendar1.get(Calendar.DAY_OF_MONTH));
                mDate.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                mDate.show();
            }
        });

        edtDayEnd = (EditText) view.findViewById(R.id.edtDayEnd);
        final String edtdateEnd = edtDayEnd.getText().toString();
        Log.d(TAG, "edtdateEnd" + edtdateEnd);
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
        imgSeach = (ImageView) view.findViewById(R.id.imgSearchReport);
        imgSeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get phone number
                SharedPreferences preferences = getActivity().getSharedPreferences("Info", Context.MODE_PRIVATE);
                final String phone = preferences.getString("Phonenumber", "Emty phone number");

                final String beginDay = edtDayStart.getText().toString();
//                Log.d("GetReport", "beginDay  " + beginDay);
                final String endDay = edtDayEnd.getText().toString();
//                new AsyncGetReport().execute(phone, beginDay, endDay);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                try {
                    Date dateStart = formatter.parse(beginDay);
                    Date dateEnd = formatter.parse(endDay);
                    if (dateEnd.compareTo(dateStart) < 0) {
                        Log.d("SimpleDateFormat ", "dateStart is Greater than my dateEnd");
                        Toast.makeText(getActivity(), "Ngày kết thúc nhỏ hơn ngày hiên tại, vui lòng nhập lại", Toast.LENGTH_LONG).show();
                    }

                } catch (ParseException e1) {
                    Log.d("SimpleDateFormat ", "invalid format datetime ");
                }

                //CALL METHOD
                if (Utility.isNetworkConnected(getActivity())) {
                    new AsyncGetReport().execute(phone, beginDay, endDay);
                } else {
                    FragmentDisconnect disconnect = new FragmentDisconnect();
                    Bundle bundle = new Bundle();
                    bundle.putString("action", "transferGetReport");
                    disconnect.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.flContent, disconnect, TAG_FRAGMENT)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        Bundle bundleSend = new Bundle();
        bundleSend.putString("currentContext", "GetReport");
        Intent intent = getActivity().getIntent();
        intent.putExtras(bundleSend);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Utility.isNetworkConnected(getActivity())) {
            //ASYNC GET TOKEN SERVER API
            //START FRAGMENT MAIN && INTEGRATION FB, PROMOTION
            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = MainContent.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("action", "transferMainContent");
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment)
                        .addToBackStack(null)
                        .commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                FragmentDisconnect disconnect = new FragmentDisconnect();
                Bundle bundle = new Bundle();
                bundle.putString("action", "MainContent");
                disconnect.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, disconnect, TAG_FRAGMENT)
                        .addToBackStack(null)
                        .commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            JSONObject json = jsonParser.getJSONFromUrlPOST(url);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            //instance new Arraylist<ReportEntity>
            reportEntityList = new ArrayList<>();

            Log.d("GetReport1", "url " + url);
            if (jsonObject != null) {
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject json = jsonArray.getJSONObject(i);
                        reportEntity = new ReportEntity();
                        List<String> busFrequently = new ArrayList<>();

                        reportEntity.setRpCardUID(json.optString("cardUID"));
                        reportEntity.setRpCardName(json.optString("cardName"));
                        reportEntity.setRpCount(json.optString("count"));
                        reportEntity.setRpTotal(json.optString("total"));

                        JSONArray arrayBusNumber = json.getJSONArray("frequently");
                        for (int j = 0; j < arrayBusNumber.length(); j++) {
                            String busNumber = arrayBusNumber.getString(j);
                            busFrequently.add(busNumber);
                        }
                        int check = 5;
                        List<String> listBusNumber = new ArrayList<>();
                        if (busFrequently.size() < check) {
                            check = busFrequently.size();
                        }
                        for (int j = 0; j < check; j++) {
                            String busName = busFrequently.get(j);
                            listBusNumber.add(busName);
                        }
                        reportEntity.setRpFrequently(listBusNumber);

                        Log.d("GetReport1", "cardUID  " + json.optString("cardUID"));
                        Log.d("GetReport1", "cardName  " + json.optString("cardName"));
                        Log.d("GetReport1", "count  " + json.optString("count"));
                        Log.d("GetReport1", "json  " + busFrequently.toString());
                        Log.d("GetReport1", "busFrequently  " + busFrequently.size());
                        reportEntityList.add(reportEntity);
                    }

                    listView = (ListView) getView().findViewById(R.id.listView_GetReport);
                    mReportAdapter = new ReportAdapter(getActivity(), reportEntityList);
                    listView.setAdapter(mReportAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("GetReport", "JSON OBJECT IS NULL  ");
            }
        }
    }

    //Fill beginDay
    // display current date for BEGIN and END DAY
    public void setCurrentDateStart(EditText edtStart) {
        int x = -30;
        myCalendar1.add(Calendar.DAY_OF_MONTH, x);

        myear = myCalendar1.get(Calendar.YEAR);
        mmonth = myCalendar1.get(Calendar.MONTH) + 1;
        mday = myCalendar1.get(Calendar.DAY_OF_MONTH);
        String checkMonth = "";
        String checkDay = "";
        if (mday <= 9) {
            checkDay = "0" + mday;
        } else {
            checkDay = mday + "";
        }
        if (mmonth < 9) {
            checkMonth = "0" + (mmonth + 1);
            Log.d("checkMonth", "checkMonth1 " + checkMonth);
        } else {
            checkMonth = mmonth + "";
            Log.d("checkMonth", "checkMonth2 " + checkMonth);
        }
        // set current date into textview
        edtStart.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(checkDay).append("/")
                .append(checkMonth).append("/")
                .append(myear));
    }

    public void setCurrentDateEnd(EditText edtEnd) {

        myear = myCalendar2.get(Calendar.YEAR);
        mmonth = myCalendar2.get(Calendar.MONTH) + 1;
        mday = myCalendar2.get(Calendar.DAY_OF_MONTH);
        String checkMonth = "";
        String checkDay = "";
        if (mday <= 9) {
            checkDay = "0" + mday;
        } else {
            checkDay = mday + "";
        }
        if (mmonth < 9) {
            checkMonth = "0" + (mmonth + 1);
        } else {
            checkMonth = mmonth + "";
        }
        // set current date into textview
        edtEnd.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(checkDay).append("/")
                .append(checkMonth).append("/")
                .append(myear));
    }


    // DatePickerDialog.OnDateSetListener for dateSTART for dateEND
    DatePickerDialog.OnDateSetListener dateEnd = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar2.set(Calendar.YEAR, year);
            myCalendar2.set(Calendar.MONTH, monthOfYear);
            myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
            view.setMaxDate(System.currentTimeMillis() - 1000);
        }

    };

    DatePickerDialog.OnDateSetListener dateStart = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar1.set(Calendar.YEAR, year);
            myCalendar1.set(Calendar.MONTH, monthOfYear);
            myCalendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel2();
            view.setMaxDate(System.currentTimeMillis() - 1000);
        }

    };

    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edtDayEnd.setText(sdf.format(myCalendar2.getTime()));
    }

    private void updateLabel2() {

//        String myFormat = "MM/dd/yyyy"; //In which you need put here
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edtDayStart.setText(sdf.format(myCalendar1.getTime()));
    }

}
