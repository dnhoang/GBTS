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
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;

import sample.dto.BusRoute;
import sample.dto.TicketType;

public class SettingActivity extends AppCompatActivity {
    ArrayList<BusRoute> list = new ArrayList<BusRoute>();
    ArrayList<TicketType> listType = new ArrayList<TicketType>();
    String setting="settingPreference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //addPreferencesFromResource(R.xml.setting_preferences);
        setContentView(R.layout.activity_setting);

        final SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);

        TextView routeName = (TextView) findViewById(R.id.tvRouteName);
        routeName.setText(sharedPreferences.getString("name","Chưa chọn tuyến"));
        Button btSave=(Button) findViewById(R.id.btSave);
        btSave.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View arg0) {
                        EditText edtRoute = (EditText) findViewById(R.id.edtRoute);
                        String route = edtRoute.getText().toString();
                        int id = Integer.parseInt(route);
                        System.out.println(id+"!!!!!");
                        saveSetting(id);
                        TextView routeName = (TextView) findViewById(R.id.tvRouteName);
                        routeName.setText(sharedPreferences.getString("name","Chưa chọn tuyến"));
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
//        final Spinner spinner = (Spinner) findViewById(R.id.spLine);
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
//            spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, codeList));
//            //JSON TicketType
//            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                    for (BusRoute route:list
//                            ) {
//                        if (Integer.parseInt(spinner.getSelectedItem().toString())==route.getId()){
//                            TextView routeName=(TextView)findViewById(R.id.tvRouteName);
//                            routeName.setText(route.getName());
//                        }
//                    }
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
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


    public TicketType getTicketType(int id) {
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
            JSONArray data = object.getJSONArray("data");


            for (int i = 0; i < data.length(); i++) {
                JSONObject json_data = data.getJSONObject(i);

                TicketType ticketType = new TicketType();
                ticketType.setId(json_data.getInt("Id"));

                ticketType.setName(json_data.getString("Name"));
                ticketType.setDescription(json_data.getString("Description"));
                ticketType.setPrice(Float.parseFloat(json_data.getString("Price")));
                ticketType.setStatus(json_data.getString("Status"));
                ticketType.setTickets(json_data.getString("Tickets"));
                listType.add(ticketType);


            }
            for (TicketType type : listType
                    ) {
                if (id == type.getId()) {
                    return type;
                }
            }

        } catch (JSONException e) {

        }
        return null;
    }

    public BusRoute getRoute(int id) {
        String json = "{\n" +
                "\"success\": true,\n" +
                "\"message\": \"\",\n" +
                "\"data\": [\n" +
                "{\n" +
                "\"Id\": 1,\n" +
                "\"Code\": \"01\",\n" +
                "\"Name\": \"Bến Thành - Bến xe Chợ Lớn\",\n" +
                "\"Tickets\": []\n" +
                "},\n" +
                "{\n" +
                "\"Id\": 2,\n" +
                "\"Code\": \"02\",\n" +
                "\"Name\": \"\\tBến Thành - Bến xe Miền Tây\",\n" +
                "\"Tickets\": []\n" +
                "}\n" +
                "]\n" +
                "}";

        try {
            JSONObject object = (JSONObject) new JSONTokener(json).nextValue();
            JSONArray data = object.getJSONArray("data");


            for (int i = 0; i < data.length(); i++) {
                JSONObject json_data = data.getJSONObject(i);

                BusRoute busRoute = new BusRoute();
                busRoute.setId(json_data.getInt("Id"));
                busRoute.setCode(json_data.getString("Code"));
                busRoute.setName(json_data.getString("Name"));
                busRoute.setTickets(json_data.getString("Tickets"));
                list.add(busRoute);


            }
            for (BusRoute route : list
                    ) {
                if (id == route.getId()) {
                    return route;
                }
            }

        } catch (JSONException e) {

        }
        return null;
    }

    public void saveSetting(int id) {
        SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        EditText edtRoute = (EditText) findViewById(R.id.edtRoute);

        BusRoute dto = getRoute(id);
        TicketType dtoType = getTicketType(id);
        if (dto != null) {
            editor.putInt("id", dto.getId());
            editor.putString("code", dto.getCode());
            editor.putString("name", dto.getName());
            editor.putString("tickets", dto.getTickets());
            editor.putString("typeName", dtoType.getName());
            editor.putString("description", dtoType.getDescription());
            editor.putFloat("price", dtoType.getPrice());
            editor.putString("status", dtoType.getStatus());
            editor.putString("ticket", dtoType.getTickets());
        }
        editor.commit();
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

    public void clickToSave() {
        //saveSetting();

        SharedPreferences sharedPreferences=getSharedPreferences(setting, MODE_PRIVATE);
        //routeName.setText(sharedPreferences.getString("name","Chưa chọn tuyến"));
    }
}

