package com.example.gbts.navigationdraweractivity.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.adapter.AllBusroutesAdapter;
import com.example.gbts.navigationdraweractivity.adapter.GoStopAdapter;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.enity.BusStop;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BusStopActivity extends FragmentActivity
        implements OnMapReadyCallback {
    private GoogleMap mMap;

    List<BusStop> busStopList;
    GoStopAdapter goStopAdapter;
    BusStop busStop;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stop);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.btMap);
        mapFragment.getMapAsync(this);
        new AsyncGoStop().execute();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Vietnam, and move the camera.
        LatLng marker = new LatLng(10.762622, 106.660172);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
        mMap.addMarker(new MarkerOptions().title("Hello HCM").position(marker));
    }


    private class AsyncGoStop extends AsyncTask<String, Void, JSONObject> {
        Intent intent;
        String url;
        String busCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Get busCode from Intent
            intent = getIntent();
            busCode = intent.getStringExtra("routeCode");
            Log.d("meow", "busCode " + busCode);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jsonParser = new JSONParser();
            url = Constance.API_GET_BUSSTOP + "&routeCode=" + busCode;
            JSONObject json = jsonParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            Log.d("meow", "url " + url);
            String busStopName;
            busStop = new BusStop();
            JSONArray jsonArray = null;
            List<String> busName = new ArrayList<>();
            busStopList = new ArrayList<>();
            try {
                jsonArray = jsonObject.optJSONArray("goStops");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    busStopName = json.optString("Name");
                    busName.add(busStopName);

                    busStop.setName(busName);

                }
                Log.d("BusStopActivity1 ", "busName " + busStop.getName());
                busStopList.add(busStop);
//
                Log.d("BusStopActivity1 ", "busStopList " + busStopList.size());
                Log.d("BusStopActivity1 ", "busStopList " + busStopList.toString());


                listView = (ListView) findViewById(R.id.listViewBusStop);
                goStopAdapter = new GoStopAdapter(getBaseContext(), busStopList);
                listView.setAdapter(goStopAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        busStop = busStopList.get(position);
                        // ID is tuyen duong
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
