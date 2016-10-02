package com.example.emulator;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;

import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;

import sample.dto.BusRoute;
import sample.dto.TicketType;

public class SettingActivity extends AppCompatActivity {
    ArrayList<BusRoute> list = new ArrayList<BusRoute>();
    ArrayList<TicketType> listType = new ArrayList<TicketType>();
    ArrayList<String> ticketTypeName = new ArrayList<String>();
    String setting = "settingPreference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //addPreferencesFromResource(R.xml.setting_preferences);
        setContentView(R.layout.activity_setting);
        //Spinner
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        //Get all ticket type
        try {
            getAllTicketType();
        } catch (IOException e) {
            e.printStackTrace();
        }
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ticketTypeName));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                for (TicketType ticketType : listType
                        ) {
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
                        try {
                            saveSetting(code, ticketType);
                        } catch (IOException e) {
                            e.printStackTrace();
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

    public void getAllTicketType() throws IOException {
        //URL url = new URL("http://domain.com/Api/GetAllTicketType?key=gbts_2016_capstone");
        //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
        //String json_ticketType=bufferedReader.readLine();
        String json_ticketType = "\n" +
                "{\n" +
                "\"success\": true,\n" +
                "\"message\": \"\",\n" +
                "\"data\": [\n" +
                "{\n" +
                "\"Id\": 1,\n" +
                "\"Name\": \"Vé tuyến ngắn\",\n" +
                "\"Description\": \"Đồng giá mọi tuyến dưới 18km\",\n" +
                "\"Price\": 5000,\n" +
                "\"Status\": 0,\n" +
                "\"Tickets\": []\n" +
                "},\n" +
                "{\n" +
                "\"Id\": 2,\n" +
                "\"Name\": \"Vé tuyến dài\",\n" +
                "\"Description\": \"Đồng giá mọi tuyến dài trên 18km\",\n" +
                "\"Price\": 7000,\n" +
                "\"Status\": 0,\n" +
                "\"Tickets\": []\n" +
                "}\n" +
                "]\n" +
                "}";
        try {
            JSONObject object = (JSONObject) new JSONTokener(json_ticketType).nextValue();
            Boolean success=object.getBoolean("success");
            if (success){
                JSONArray data = object.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject json_data = data.getJSONObject(i);

                    TicketType ticketType = new TicketType();
                    ticketType.setId(json_data.getString("Id"));
                    //Get name list
                    String ticketName = json_data.getString("Name");
                    //
                    ticketType.setName(json_data.getString("Name"));
                    ticketType.setDescription(json_data.getString("Description"));
                    ticketType.setPrice(json_data.getString("Price"));
                    ticketType.setStatus(json_data.getString("Status"));
                    ticketType.setTickets(json_data.getString("Tickets"));
                    listType.add(ticketType);
                    ticketTypeName.add(ticketName);

                }
            }



        } catch (JSONException e) {

        }

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

    public BusRoute getRouteByCode(String code) throws IOException {
        //Get result from API by Code then store on json string
        //URL url=new URL("http://domain.com/Api/GetBusRouteByCode?key=gbts_2016_capstone&routeCode="+code);
        //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
        //String json=bufferedReader.readLine();
        String json = "\n" +
                "{\n" +
                "\"success\": true,\n" +
                "\"message\": \"\",\n" +
                "\"data\": {\n" +
                "\"Id\": 1,\n" +
                "\"Code\": \"01\",\n" +
                "\"Name\": \"Bến Thành - Bến xe Chợ Lớn\"\n" +
                "}\n" +
                "}";
        //Put data into DTO and return
        //System.out.println(code+"!!!!");
        try {
            JSONObject object = (JSONObject) new JSONTokener(json).nextValue();
            Boolean success=object.getBoolean("success");
            if (success){
                JSONObject data = object.getJSONObject("data");
                //System.out.println(code+"Sau data");
//            JSONObject json_data = data.getJSONObject(0);
                BusRoute busRoute = new BusRoute();
                System.out.println(data.getString("Name"));
                busRoute.setId(data.getString("Id"));
                busRoute.setCode(data.getString("Code"));
                busRoute.setName(data.getString("Name"));
                //System.out.println(busRoute.getName()+"!!!!");
                return busRoute;
            }
            else {
                return null;
            }



        } catch (JSONException e) {

        }
        return null;
    }

    public void saveSetting(String code, String ticketTypeName) throws IOException {
        SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //EditText edtRoute = (EditText) findViewById(R.id.edtRoute);
        //System.out.println(code);
        BusRoute busRoute = getRouteByCode(code);
        //System.out.println(busRoute.getName());
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

    }

    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader
                    (new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;

        } finally {
            is.close();
        }
    }


}

