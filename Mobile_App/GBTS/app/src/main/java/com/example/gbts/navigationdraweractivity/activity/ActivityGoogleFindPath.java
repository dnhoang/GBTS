package com.example.gbts.navigationdraweractivity.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LogWriter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.enity.AutoCompleteBean;
import com.example.gbts.navigationdraweractivity.module.google.mapsAPI.DirectionFinder;
import com.example.gbts.navigationdraweractivity.module.google.mapsAPI.DirectionFinderListener;
import com.example.gbts.navigationdraweractivity.module.google.mapsAPI.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivityGoogleFindPath extends AppCompatActivity
        implements OnMapReadyCallback, DirectionFinderListener {

    GoogleMap mMap;
    private WebView webView;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_find_path);

        //Display Home As Up Enabled
        actionBar = getSupportActionBar();
        actionBar.hide();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.findMap);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        String origin = intent.getStringExtra("origin");
        String destination = intent.getStringExtra("destination");

        if (origin.isEmpty()) {
            return;
        }
        if (destination.isEmpty()) {
            return;
        }
        try {
            new DirectionFinder(this, origin, destination).execute();


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in FPT and move the camera
//        LatLng fptUni = new LatLng(10.852862, 106.629524);
//        mMap.addMarker(new MarkerOptions()
//                .position(fptUni)
//                .title("Fpt University HCM")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_pushpin)));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fptUni, 18));
        // di chuyen marker den vi tri FPT zoom 18
//        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // set current location
        mMap.setMyLocationEnabled(true);
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
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

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

            webView = (WebView) findViewById(R.id.webview_Maps);
            WebSettings settings = webView.getSettings();
            settings.setDefaultTextEncodingName("UTF-8");
            String before = "<html> <head> <style> .list-type1{ margin-left: 20px; } .list-type1 ul{ list-style: none; font-size: 15px; padding: 0; } .list-type1 ul li{ padding-top: 10px; } .list-type1 a { position: relative; display: block; padding: 0px 10px; background: #f3f3f3 ; color: #000; text-decoration: none; } .list-type1 a img{ border-radius: 50%; margin-left: -30px; border: 2px solid #03a234 ; padding: 10px; max-width: 48px; background: #03a234 ; vertical-align: middle; } </style> <head> <body> <div class=\"list-type1\"> <ul>";
            String after = "</ul></div></body></html>";
            String result = route.html_instructions;
            if (result != null) {
                webView.loadData(before + result + after, "text/html; charset=UTF-8", null);
            } else {
                webView.loadData(before + "Không tìm thấy tuyến xe phù hợp..!" + after, "text/html; charset=UTF-8", null);
            }
        }
    }

}
