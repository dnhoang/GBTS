package com.example.gbts.navigationdraweractivity.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.adapter.GoStopAdapter;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.module.google.mapsAPI.BusStop;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BusStopActivity extends FragmentActivity
        implements OnMapReadyCallback {


    private GoogleMap mMap;
    private GoStopAdapter goStopAdapter;

    private List<BusStop> busStopList;
    private List<BusStop> drawBusStop = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    ListView listView;


    public void setDrawBusStop(List<BusStop> drawBusStop) {
        this.drawBusStop = drawBusStop;
        Log.d("BusStopActivity1 ", "drawBusStop" + drawBusStop.size());
        for (BusStop item : drawBusStop) {
            Log.d("onMapReady ", "getLng " + item.getLng());
            Log.d("onMapReady ", "getLng " + item.getLat());

        }
        onMapReady(mMap);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stop);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.btMap);
        mapFragment.getMapAsync(this);

        new AsyncGoStop(this).execute();


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Intent intent;
        String routeName;
        mMap = googleMap;

        if (drawBusStop == null || drawBusStop.size() == 0) {
            return;
        }


        PolylineOptions polylineOptions = new PolylineOptions().
                geodesic(true).
                color(Color.BLUE).
                width(15);

        for (int i = 0; i < drawBusStop.size(); i++) {
            BusStop item = drawBusStop.get(i);
            double lng = Double.parseDouble(item.getLng());
            double lat = Double.parseDouble(item.getLat());
            LatLng latLng = new LatLng(lat, lng);

            polylineOptions.add(latLng);
        }
        BusStop item = drawBusStop.get(0);
        double latOrigin = Double.parseDouble(item.getLat());
        double lngOrigin = Double.parseDouble(item.getLng());
        LatLng origin = new LatLng(latOrigin, lngOrigin);

        BusStop itemDes = drawBusStop.get(drawBusStop.size() - 1);
        double lngDestination = Double.parseDouble(itemDes.getLng());
        double latDestination = Double.parseDouble(itemDes.getLat());
        LatLng destination = new LatLng(latDestination, lngDestination);

        intent = getIntent();
        routeName = intent.getStringExtra("routeName").trim();


        String[] words = routeName.split("-");
        Log.d("slipt length: ", words.length + "");

        String orginName = words[0];
        String destinationName = words[1];
        if (destinationName != null && destinationName != "" && words.length == 3) {
            destinationName = words[2];
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 14));
        mMap.addMarker(new MarkerOptions()
                .title(orginName)
                .position(origin));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 14));
        mMap.addMarker(new MarkerOptions()
                .title(destinationName)
                .position(destination));

        polylinePaths.add(mMap.addPolyline(polylineOptions));

    }


    private class AsyncGoStop extends AsyncTask<String, Void, JSONObject> {
        private ProgressDialog pDialog;
        Intent intent;
        String url;
        String busCode;
        String busStopName;
        String Lng, Lat;

        private BusStopActivity busStopActivity;

        public AsyncGoStop(BusStopActivity busStopActivity) {
            this.busStopActivity = busStopActivity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Get busCode from Intent
            intent = getIntent();
            busCode = intent.getStringExtra("routeCode");


            pDialog = new ProgressDialog(BusStopActivity.this);
            pDialog.setMessage("Vui lòng đợi ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jsonParser = new JSONParser();
            url = Constance.API_GET_BUS_STOP + "&routeCode=" + busCode;

            JSONObject json = jsonParser.getJSONFromUrlGET(url);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            // Hide dialog
            pDialog.dismiss();
            Log.d("meow1", "url " + url);
            JSONArray jsonArray = null;
            try {
                jsonArray = jsonObject.optJSONArray("goStops");
                busStopList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    BusStop busStop = new BusStop();
                    JSONObject json = jsonArray.getJSONObject(i);
                    busStopName = json.optString("Name");
                    Lng = json.optString("Lng");
                    Lat = json.optString("Lat");

                    busStop.setName(busStopName);
                    busStop.setLng(Lng);
                    busStop.setLat(Lat);
                    busStopList.add(busStop);
                }
                //GET LIST FROM ASYNC TASK
                busStopActivity.setDrawBusStop(busStopList);

                listView = (ListView) findViewById(R.id.listViewBusStop);
                goStopAdapter = new GoStopAdapter(getBaseContext(), busStopList);
                listView.setAdapter(goStopAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        BusStop busStop = busStopList.get(position);
                        // ID is tuyen duong

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
