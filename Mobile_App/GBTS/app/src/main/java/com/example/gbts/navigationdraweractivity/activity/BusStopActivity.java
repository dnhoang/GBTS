package com.example.gbts.navigationdraweractivity.activity;

import android.app.FragmentManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.adapter.GoStopAdapter;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.enity.BusStop;
import com.example.gbts.navigationdraweractivity.fragment.FragmentDirection;
import com.example.gbts.navigationdraweractivity.fragment.GetAllButRoute;
import com.example.gbts.navigationdraweractivity.module.google.mapsAPI.DirectionFinder;
import com.example.gbts.navigationdraweractivity.module.google.mapsAPI.DirectionFinderListener;
import com.example.gbts.navigationdraweractivity.module.google.mapsAPI.Route;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class BusStopActivity extends FragmentActivity
        implements OnMapReadyCallback, DirectionFinderListener {

    private final String GEOCODING_API = "https://maps.googleapis.com/maps/api/geocode/json?";

    private GoogleMap mMap;

    List<BusStop> busStopList;
    GoStopAdapter goStopAdapter;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stop);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.btMap);
        mapFragment.getMapAsync(this);

        new AsyncGoStop().execute();


        Intent intent = getIntent();
        String routeName = intent.getStringExtra("routeName").trim();
        String[] words = routeName.split("-");
        Log.d("slipt length: ", words.length + "");

        String tmp = null;
        String origin = words[0];
        String destination = words[1];
        if (destination != null && destination != "" && words.length == 3) {
            destination = words[2];
        }

        if (origin.isEmpty() && destination.isEmpty()) {
//             Add a marker in Vietnam, and move the camera.
            LatLng marker = new LatLng(10.762622, 106.660172);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
            mMap.addMarker(new MarkerOptions().title("Hello HCM").position(marker));
        } else {
            try {
                new DirectionFinder(this, origin, destination).execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Vietnam, and move the camera.
//        LatLng marker = new LatLng(10.762622, 106.660172);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
//        mMap.addMarker(new MarkerOptions().title("Hello HCM").position(marker));


    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Vui lòng chờ...",
                "Đang tìm đường..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        if (routes == null) {
            progressDialog.dismiss();
            return;
        }
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.txtDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.txtDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.GRAY).
                    width(12);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }

    }


    private class AsyncGoStop extends AsyncTask<String, Void, JSONObject> {
        private ProgressDialog pDialog;
        Intent intent;
        String url;
        String busCode, routeName;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Get busCode from Intent
            intent = getIntent();
            busCode = intent.getStringExtra("routeCode");
//            routeName = intent.getStringExtra("routeName");
            Log.d("meow", "busCode " + busCode);

            pDialog = new ProgressDialog(BusStopActivity.this);
            pDialog.setMessage("Vui lòng đợi ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jsonParser = new JSONParser();
            url = Constance.API_GET_BUSSTOP + "&routeCode=" + busCode;
            JSONObject json = jsonParser.getJSONFromUrlGET(url);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            // Hide dialog
            pDialog.dismiss();
            Log.d("meow", "url " + url);
            String busStopName;
            JSONArray jsonArray = null;

            busStopList = new ArrayList<>();
            try {
                jsonArray = jsonObject.optJSONArray("goStops");
                for (int i = 0; i < jsonArray.length(); i++) {
                    BusStop busStop = new BusStop();
                    JSONObject json = jsonArray.getJSONObject(i);
                    busStopName = json.optString("Name");
                    busStop.setName(busStopName);
                    busStopList.add(busStop);
                }
//                Log.d("BusStopActivity1 ", "busName " + busName.toString());
//                Log.d("BusStopActivity1 ", "busName " + busName.size());

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
