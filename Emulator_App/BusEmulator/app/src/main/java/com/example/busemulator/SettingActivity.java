package com.example.busemulator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import Util.Utility;
import sample.dto.BusRoute;
import sample.dto.TicketType;

/**
 * Created by ducdmse61486 on 10/5/2016.
 */

public class SettingActivity extends AppCompatActivity {
    ArrayList<BusRoute> list = new ArrayList<BusRoute>();
    ArrayList<TicketType> listType = new ArrayList<TicketType>();
    ArrayList<String> ticketTypeName = new ArrayList<String>();
    String setting = "settingPreference";
    String hostAddress = "https://grinbuz.net";

    Spinner spinner;
    BusRoute busRoute = new BusRoute();

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
        setContentView(R.layout.activity_setting);



        EditText edtHost = (EditText) findViewById(R.id.edtHost);
        edtHost.setText(sharedPreferences.getString("host", "https://grinbuz.net"));
        EditText edtRoute = (EditText) findViewById(R.id.edtRoute);
        edtRoute.setText(sharedPreferences.getString("code", ""));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new getAllTicketType().execute();


        spinner = (Spinner) findViewById(R.id.spinnerTick);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) parent.getChildAt(0)).setTextSize(14);
                for (TicketType ticketType : listType
                        ) {
//
                    if (spinner.getSelectedItem().toString().equals(ticketType.getName())) {
                        TextView price = (TextView) findViewById(R.id.tvPrice);
                        price.setText(ticketType.getPrice());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //SharedPreference


        TextView routeName = (TextView) findViewById(R.id.tvRouteName);
        routeName.setText(sharedPreferences.getString("name", "Chưa chọn tuyến"));
        Button btSave = (Button) findViewById(R.id.btSave);
        btSave.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View arg0) {
                        EditText edtRoute = (EditText) findViewById(R.id.edtRoute);
                        String code = edtRoute.getText().toString();

                        String ticketType = spinner.getSelectedItem().toString();
                        if (ticketType != null || code != null) {
                            try {
//                                EditText edtHost = (EditText) findViewById(R.id.edtHost);
//                                String host=edtHost.getText().toString().trim();
//                                if (!host.equals("")) hostAddress=host;
                                saveSetting(code, ticketType);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {

                            Toast.makeText(getApplicationContext(), "Cấu hình chưa thay đổi", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }
    public void clickToSaveHost(View view){
        SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        EditText edtHost = (EditText) findViewById(R.id.edtHost);
        String host = edtHost.getText().toString().trim();
        editor.putString("host", host);
        editor.commit();
    }

    public TicketType getTicketTypeByName(String name) {

        for (TicketType type : listType
                ) {
            if (name.equals(type.getName())) {
                return type;
            }
        }
        return null;
    }


    public void saveSetting(String code, String ticketTypeName) throws IOException {
        SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
        EditText edtHost = (EditText) findViewById(R.id.edtHost);
        String host = edtHost.getText().toString().trim();
        if (host.equals("")) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("host", "https://grinbuz.net");
            editor.commit();
            new GetBusRouteByCode().execute(ticketTypeName);
        } else {
            hostAddress = edtHost.getText().toString().trim();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("host", hostAddress);
            editor.commit();
            new GetBusRouteByCode().execute(ticketTypeName);
        }


    }
    public void clickToLogout(View view){
        Intent intent=new Intent(this,LoginActivity.class);
        SharedPreferences sharedPreferences=getSharedPreferences(setting,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("onTrip",false);
        editor.commit();
        startActivity(intent);
    }
    //ASYNC TASK
    private class getAllTicketType extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;

        boolean success = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SettingActivity.this);
            pDialog.setMessage("Loading data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            Utility jParser = new Utility();
            SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
            hostAddress = sharedPreferences.getString("host", "https://grinbuz.net");
            //hostAddress="https://grinbuz.com";
            String strURL = hostAddress + "/Api/GetAllTicketType?key=gbts_2016_capstone";

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(strURL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            // Hide dialog
            pDialog.dismiss();
            //check success
            if (jsonObject == null) {

            } else {
                try {

                    success = jsonObject.getBoolean("success");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (success) {
                    try {
                        JSONArray data = jsonObject.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject json_data = data.getJSONObject(i);


                            String id = json_data.getString("Id");
                            String name = json_data.getString("Name");
                            String description = json_data.getString("Description");
                            String price = json_data.getString("Price");

                            TicketType ticketType = new TicketType(id, name, description, price);
                            listType.add(ticketType);
                            ticketTypeName.add(name);


                        }
                        spinner = (Spinner) findViewById(R.id.spinnerTick);
                        spinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, ticketTypeName));
                    } catch (JSONException e) {
                        e.printStackTrace();
//
                    }
                } else {


                    Toast.makeText(getApplicationContext(), "Không thể kết nối với máy chủ", Toast.LENGTH_LONG).show();


                }
            }

        }
    }

    private class GetBusRouteByCode extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String code;
        String ticketTypeName;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            EditText edtCode = (EditText) findViewById(R.id.edtRoute);
            code = edtCode.getText().toString();

            pDialog = new ProgressDialog(SettingActivity.this);
            pDialog.setMessage("Vui lòng đợi trong giây lát ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            Utility jParser = new Utility();
            ticketTypeName = params[0];
            String strURL = hostAddress + "/Api/GetBusRouteByCode?key=gbts_2016_capstone&routeCode=" + code;

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(strURL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            // Hide dialog
            pDialog.dismiss();
            boolean success = false;
            //check success
            try {
                success = jsonObject.getBoolean("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (success) {
                JSONObject data = null;
                try {
                    data = jsonObject.getJSONObject("data");

                    String id = data.getString("Id");
                    String code = data.getString("Code");
                    String name = data.getString("Name");
                    JSONObject start=data.getJSONObject("Start");
                    String startName=start.getString("Name");
                    String startLong=start.getString("Lng");

                    String startLat=start.getString("Lat");
                    JSONObject end=data.getJSONObject("End");
                    String endName=end.getString("Name");
                    String endLong=end.getString("Lng");
                    String endLat=end.getString("Lat");
                    SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("id", id);
                    editor.putString("code", code);
                    editor.putString("name", name);
                    TicketType ticketType = getTicketTypeByName(ticketTypeName);
                    editor.putString("ticketTypeId", ticketType.getId());
                    editor.putString("typeName", ticketType.getName());
                    editor.putString("description", ticketType.getDescription());
                    editor.putString("price", ticketType.getPrice());
                    editor.putBoolean("onTrip",false);
                    editor.putString("startName",startName);
                    editor.putString("endName",endName);
                    editor.putString("startLong",startLong);
                    editor.putString("startLat",startLat);
                    editor.putString("endLong",endLong);
                    editor.putString("endLat",endLat);
                    editor.commit();
                    TextView routeName = (TextView) findViewById(R.id.tvRouteName);
                    routeName.setText(name);
                    EditText edtRoute = (EditText) findViewById(R.id.edtRoute);
                    edtRoute.setText(code);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent=new Intent(SettingActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Tuyến không tồn tại!", Toast.LENGTH_LONG).show();
            }
        }
    }


}

