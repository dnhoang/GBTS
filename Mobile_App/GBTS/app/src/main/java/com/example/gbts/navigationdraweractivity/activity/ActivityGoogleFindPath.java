package com.example.gbts.navigationdraweractivity.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gbts.navigationdraweractivity.MainActivity;
import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.fragment.FragmentDirection;
import com.example.gbts.navigationdraweractivity.module.google.mapsAPI.DirectionFinder;
import com.example.gbts.navigationdraweractivity.module.google.mapsAPI.DirectionFinderListener;
import com.example.gbts.navigationdraweractivity.module.google.mapsAPI.Route;
import com.example.gbts.navigationdraweractivity.utils.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
        final String origin = intent.getStringExtra("origin");
        final String destination = intent.getStringExtra("destination");

        if (origin.isEmpty()) {
            return;
        }
        if (destination.isEmpty()) {
            return;
        }
        try {
            if (Utility.isNetworkConnected(ActivityGoogleFindPath.this)) {
                new DirectionFinder(this, origin, destination).execute();
            } else {
                // custom dialog
                final Dialog dialog = new Dialog(ActivityGoogleFindPath.this);
                dialog.setContentView(R.layout.custom_dialog_login);
                dialog.setTitle("Mất kết nối mạng ...");

                Button dialogButton = (Button) dialog.findViewById(R.id.dialogBtnOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utility.isNetworkConnected(ActivityGoogleFindPath.this)) {
                            dialog.dismiss();
                            try {
                                new DirectionFinder(ActivityGoogleFindPath.this, origin, destination).execute();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                Button dialogCancel = (Button) dialog.findViewById(R.id.dialogBtnCancel);
                // if button is clicked, close the custom dialog
                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
                dialog.show();
            }

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

        int height = 100;
        int width = 100;
        BitmapDrawable bitOrigin = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_start_location);
        BitmapDrawable bitDestination = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_finish);
        Bitmap start = bitOrigin.getBitmap();
        Bitmap end = bitDestination.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(start, width, height, false);
        Bitmap smallMarkerEnd = Bitmap.createScaledBitmap(end, width, height, false);

//        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_start_blue))

        if (routes != null) {
            for (Route route : routes) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 12));
                ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
                ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

                originMarkers.add(mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                        .title(route.startAddress)
                        .position(route.startLocation)));
                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarkerEnd))
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
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityGoogleFindPath.this);
            alertDialogBuilder
                    .setTitle("Tuyến không tồn tại bạn có muốn tiếp tục")
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    FragmentDirection direction = new FragmentDirection();
                                    FragmentManager manager = getSupportFragmentManager();
                                    direction.show(manager, "FragmentDirection");
                                    finish();
                                    dialog.cancel();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // if this button is clicked, just close
                    // the dialog box and do nothing
                    dialog.cancel();
                }
            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

}
