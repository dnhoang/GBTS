package com.example.emulator;

import android.app.ProgressDialog;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class SettingActivity extends AppCompatActivity {
    ArrayList<BusRoute> list = new ArrayList<BusRoute>();
    ArrayList<TicketType> listType = new ArrayList<TicketType>();
    ArrayList<String> ticketTypeName = new ArrayList<String>();
    String setting = "settingPreference";
    String hostAddress = "http://grinbuz.com/";
    //EditText host = (EditText) findViewById(R.id.edtHost);
    Spinner spinner;
    BusRoute busRoute=new BusRoute();

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        //addPreferencesFromResource(R.xml.setting_preferences);
        setContentView(R.layout.activity_setting);

        actionBar = getSupportActionBar();
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
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
        final SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);

        TextView routeName = (TextView) findViewById(R.id.tvRouteName);
        routeName.setText(sharedPreferences.getString("name", "Chưa chọn tuyến"));
        Button btSave = (Button) findViewById(R.id.btSave);
        btSave.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View arg0) {
                        EditText edtRoute = (EditText) findViewById(R.id.edtRoute);
                        String code = edtRoute.getText().toString();

                        String ticketType = spinner.getSelectedItem().toString();
                        System.out.println(ticketType + "!!!!!");
                        if (ticketType!=null || code!=null){
                            try {
                                saveSetting(code, ticketType);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else{
                            Toast.makeText(getApplicationContext(),"Cấu hình chưa thay đổi",Toast.LENGTH_SHORT).show();
                        }


                        //chua chay
//                        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//
//                        View header = navigationView.getHeaderView(0);
//
//                        TextView headerLine = (TextView) header.findViewById(R.id.tvHeaderRoute);
//                        headerLine.setText(sharedPreferences.getString("code","Chưa chọn tuyến"));
//                        TextView headerName = (TextView) header.findViewById(R.id.tvHeaderRouteName);
//                        headerName.setText(sharedPreferences.getString("name","Chưa chọn tuyến"));

                    }
                });
    }
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction =
//                fragmentManager.beginTransaction();
//        SettingFragment settingFragment = new SettingFragment();
//        fragmentTransaction.replace(android.R.id.content, settingFragment);
//
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();

//        //JSON


//            SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
//                @Override
//                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//                    int routeID = sharedPreferences.getInt("editRoute",MODE_PRIVATE);
//                    for (BusRoute route : list
//                            ) {
//                        if (routeID==route.getId()) {
//                            TextView routeName = (TextView) findViewById(R.id.tvRouteName);
//                            routeName.setText(route.getName());
//                        }
//                    }
//
//
//                }
//            };

//            //JSON TicketType

//
//            data=object.getJSONArray("data");
//            ArrayList<TicketType> ticketTypeArrayListlist=new ArrayList<TicketType>();
//            for(int i=0; i<data.length(); i++){
//                JSONObject json_data = data.getJSONObject(i);
//
//                TicketType ticketType=new TicketType();
//                ticketType.setId(json_data.getInt("Id"));
//                ticketType.setName(json_data.getString("Name"));
//                ticketType.setDescription(json_data.getString("Description"));
//                ticketType.setPrice(json_data.getDouble("Price"));
//                ticketType.setStatus(json_data.getString("Status"));
//                ticketTypeArrayListlist.add(ticketType);
//
//
//            }
//            //End JSON TICKET TYPE

//    public void getAllTicketType() throws IOException {
//        URL url = new URL("http://172.20.10.2:1185/Api/GetAllTicketType?key=gbts_2016_capstone");
//        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//        StringBuilder stringBuilder = new StringBuilder();
//        String line;
//        while ((line = bufferedReader.readLine()) != null) {
//            stringBuilder.append(line).append("\n");
//        }
//        bufferedReader.close();
//        urlConnection.disconnect();
//        String json_ticketType = stringBuilder.toString();
//        System.out.println(json_ticketType);
////        String json_ticketType = "\n" +
////                "{\n" +
////                "\"success\": true,\n" +
////                "\"message\": \"\",\n" +
////                "\"data\": [\n" +
////                "{\n" +
////                "\"Id\": 1,\n" +
////                "\"Name\": \"Vé tuyến ngắn\",\n" +
////                "\"Description\": \"Đồng giá mọi tuyến dưới 18km\",\n" +
////                "\"Price\": 5000,\n" +
////                "\"Status\": 0,\n" +
////                "\"Tickets\": []\n" +
////                "},\n" +
////                "{\n" +
////                "\"Id\": 2,\n" +
////                "\"Name\": \"Vé tuyến dài\",\n" +
////                "\"Description\": \"Đồng giá mọi tuyến dài trên 18km\",\n" +
////                "\"Price\": 7000,\n" +
////                "\"Status\": 0,\n" +
////                "\"Tickets\": []\n" +
////                "}\n" +
////                "]\n" +
////                "}";
//        try {
//            JSONObject object = (JSONObject) new JSONTokener(json_ticketType).nextValue();
//            Boolean success = object.getBoolean("success");
//            if (success) {
//                JSONArray data = object.getJSONArray("data");
//                for (int i = 0; i < data.length(); i++) {
//                    JSONObject json_data = data.getJSONObject(i);
//
//                    TicketType ticketType = new TicketType();
//                    ticketType.setId(json_data.getString("Id"));
//                    //Get name list
//                    String ticketName = json_data.getString("Name");
//                    //
//                    ticketType.setName(json_data.getString("Name"));
//                    ticketType.setDescription(json_data.getString("Description"));
//                    ticketType.setPrice(json_data.getString("Price"));
//
//                    listType.add(ticketType);
//                    ticketTypeName.add(ticketName);
//
//                }
//            }
//
//
//        } catch (JSONException e) {
//
//        }
//
//    }

    public TicketType getTicketTypeByName(String name) {

        for (TicketType type : listType
                ) {
            if (name.equals(type.getName())) {
                return type;
            }
        }
        return null;
    }

//    public BusRoute getRouteByCode(String code) throws IOException {
//        //Get result from API by Code then store on json string
//        URL url = new URL(hostAddress + "/Api/GetBusRouteByCode?key=gbts_2016_capstone&routeCode=" + code);
//        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//        StringBuilder stringBuilder = new StringBuilder();
//        String line;
//        while ((line = bufferedReader.readLine()) != null) {
//            stringBuilder.append(line).append("\n");
//        }
//        bufferedReader.close();
//        urlConnection.disconnect();
//        String json = stringBuilder.toString();
////        String json = "\n" +
////                "{\n" +
////                "\"success\": true,\n" +
////                "\"message\": \"\",\n" +
////                "\"data\": {\n" +
////                "\"Id\": 1,\n" +
////                "\"Code\": \"01\",\n" +
////                "\"Name\": \"Bến Thành - Bến xe Chợ Lớn\"\n" +
////                "}\n" +
////                "}";
//        //Put data into DTO and return
//        //System.out.println(code+"!!!!");
//        try {
//            JSONObject object = (JSONObject) new JSONTokener(json).nextValue();
//            Boolean success = object.getBoolean("success");
//            if (success) {
//                JSONObject data = object.getJSONObject("data");
//                //System.out.println(code+"Sau data");
////            JSONObject json_data = data.getJSONObject(0);
//                BusRoute busRoute = new BusRoute();
//                System.out.println(data.getString("Name"));
//                busRoute.setId(data.getString("Id"));
//                busRoute.setCode(data.getString("Code"));
//                busRoute.setName(data.getString("Name"));
//                //System.out.println(busRoute.getName()+"!!!!");
//                return busRoute;
//            } else {
//                return null;
//            }
//
//
//        } catch (JSONException e) {
//
//        }
//        return null;
//    }

    public void saveSetting(String code, String ticketTypeName) throws IOException {
        SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);

        //EditText edtRoute = (EditText) findViewById(R.id.edtRoute);
        //System.out.println(code);
        //BusRoute busRoute = getRouteByCode(code);
        new GetBusRouteByCode().execute(ticketTypeName);


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

            } else {
                Toast.makeText(getApplicationContext(), "Tuyến không tồn tại!", Toast.LENGTH_LONG).show();
            }
        }
    }




}

