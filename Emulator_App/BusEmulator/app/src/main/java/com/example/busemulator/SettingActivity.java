package com.example.busemulator;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
    String hostAddress = "https://grinbuz.com/";

    Spinner spinner;
    BusRoute busRoute=new BusRoute();

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        //addPreferencesFromResource(R.xml.setting_preferences);
        final SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
        setContentView(R.layout.activity_setting);
        EditText edtHost = (EditText) findViewById(R.id.edtHost);
        edtHost.setText(sharedPreferences.getString("host","https://grinbuz.com"));
        EditText edtRoute=(EditText)findViewById(R.id.edtRoute);
        edtRoute.setText(sharedPreferences.getString("code",""));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Spinner

        //Get all ticket type
//        try {
//            getAllTicketType();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
                        if (ticketType!=null || code!=null){
                            try {
//                                EditText edtHost = (EditText) findViewById(R.id.edtHost);
//                                String host=edtHost.getText().toString().trim();
//                                if (!host.equals("")) hostAddress=host;
                                saveSetting(code, ticketType);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else{
                            Toast.makeText(getApplicationContext(),"Cấu hình chưa thay đổi",Toast.LENGTH_SHORT).show();
                        }



                    }
                });
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
        String host=edtHost.getText().toString().trim();
        if (host.equals("")){

            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("host","https://grinbuz.com");
            editor.commit();
            new GetBusRouteByCode().execute(ticketTypeName);
        } else{
            hostAddress=edtHost.getText().toString().trim();
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("host",hostAddress);
            editor.commit();
            new GetBusRouteByCode().execute(ticketTypeName);
        }
        //EditText edtRoute = (EditText) findViewById(R.id.edtRoute);
        //System.out.println(code);
        //BusRoute busRoute = getRouteByCode(code);



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
            SharedPreferences sharedPreferences=getSharedPreferences(setting,MODE_PRIVATE);
            hostAddress=sharedPreferences.getString("host","https://grinbuz.com");
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

                        TicketType ticketType = new TicketType(id,name,description,price);
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
            pDialog.setMessage("Loading data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            Utility jParser = new Utility();
            ticketTypeName=params[0];
            String strURL = hostAddress+"/Api/GetBusRouteByCode?key=gbts_2016_capstone&routeCode="+code;

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
                JSONObject data=null;
                try {
                    data = jsonObject.getJSONObject("data");

                    String id=data.getString("Id");
                    String code=data.getString("Code");
                    String name=data.getString("Name");
                    busRoute=new BusRoute(id,code,name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SharedPreferences sharedPreferences=getSharedPreferences(setting,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (busRoute != null) {
                    TicketType ticketType = getTicketTypeByName(ticketTypeName);
                    editor.putString("id", busRoute.getId());
                    editor.putString("code", busRoute.getCode());
                    editor.putString("name", busRoute.getName());
                    editor.putString("ticketTypeId", ticketType.getId());
                    editor.putString("typeName", ticketType.getName());
                    editor.putString("description", ticketType.getDescription());
                    editor.putString("price", ticketType.getPrice());
                    //editor.putString("status", ticketType.getStatus());
                    //editor.putString("ticket", ticketType.getTickets());
                }
                editor.commit();
                TextView routeName = (TextView) findViewById(R.id.tvRouteName);
                routeName.setText(busRoute.getName());
                EditText edtRoute=(EditText)findViewById(R.id.edtRoute);
                edtRoute.setText(busRoute.getCode());

            } else {
                Toast.makeText(getApplicationContext(), "Tuyến không tồn tại!", Toast.LENGTH_LONG).show();
            }
        }
    }




}

