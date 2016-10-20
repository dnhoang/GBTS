package com.example.gbts.navigationdraweractivity.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LogWriter;
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
        implements OnMapReadyCallback, DirectionFinderListener, LocationListener {


    private ArrayList<AutoCompleteBean> resultList;
    private ArrayList<Double> locationResult;
    GoogleMap googleMap;
    double latitude = 0;
    double longitude = 0;

    private static final String LOG_TAG = "ExampleApp";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_SEARCH = "/search";
    private static final String TYPE_DETAILS = "/details";
    private static final String OUT_JSON = "/json";
    //    private static final String API_KEY = "AIzaSyAnG0oOnOt1pNqhpbU_uwmCbaPjeBYl6VU";
    private static final String API_KEY = "AIzaSyA65-eqSvIefv4lY3vARmN4fwVc1d4lPaE";


    GoogleMap mMap;
    private Button btnFindPath;
    private WebView webView;
    AutoCompleteTextView atcltOrigin, atcltDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private List<String> html = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_find_path);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.findMap);
        mapFragment.getMapAsync(this);

        atcltOrigin = (AutoCompleteTextView) findViewById(R.id.atcltOrigin);
        atcltOrigin.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
        atcltOrigin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.d(LOG_TAG, "atcltOrigin click");
                System.out.println(resultList.get(position).getDescription());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        locationResult = Details(resultList.get(position).getDescription(), resultList.get(position).getReference());
                    }
                }).start();
            }
        });

        atcltDestination = (AutoCompleteTextView) findViewById(R.id.atcltDestination);
        atcltDestination.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));
        atcltDestination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.d(LOG_TAG, "atcltOrigin click");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        locationResult = Details(resultList.get(position).getDescription(), resultList.get(position).getReference());
                    }
                }).start();
            }
        });

        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

    }

    private void sendRequest() {
        String origin = atcltOrigin.getText().toString();
        String destination = atcltDestination.getText().toString();
//        Log.d("anhtruongne ", "origin" + origin);
//        Log.d("anhtruongne ", "destination" + destination);
        if (origin.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập điểm bắt đầu ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập điểm đến ", Toast.LENGTH_SHORT).show();
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
        LatLng fptUni = new LatLng(10.852862, 106.629524);
        mMap.addMarker(new MarkerOptions()
                .position(fptUni)
                .title("Fpt University HCM")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_pushpin)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fptUni, 18));// di chuyen marker den vi tri FPT zoom 18
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
                    color(Color.GREEN).
                    width(12);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));

            webView = (WebView) findViewById(R.id.webview_Maps);
            WebSettings settings = webView.getSettings();
            settings.setDefaultTextEncodingName("UTF-8");
            webView.loadData("<html><body>" + route.html_instructions + "</body></html>", "text/html; charset=UTF-8", null);
//            Log.d("truongtq ", "intruc " + intruc);

            Log.d("truongtq ", "sendRequest " + route.html_instructions);
        }
    }
    //AUTO COMPLETE TEXTVIEW


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        System.out.println("latitude :::: " + latitude + " longitude :::: " + longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> result;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index).getDescription();
        }


        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        System.out.println("truongtq click");
                        resultList = autocomplete(constraint.toString());
                        result = new ArrayList<String>(resultList.size());
                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };

            return filter;
        }
    }

    private ArrayList<AutoCompleteBean> autocomplete(String input) {

        ArrayList<AutoCompleteBean> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?input=" + URLEncoder.encode(input, "utf-8"));
            sb.append("&sensor=true&key=" + API_KEY);

            URL url = new URL(sb.toString());
            System.out.println("truongtq url" + url.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<AutoCompleteBean>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(new AutoCompleteBean(predsJsonArray.getJSONObject(i).getString("description"),
                        predsJsonArray.getJSONObject(i).getString("reference")));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    private ArrayList<Double> Details(String description, String reference) {

        ArrayList<Double> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAILS + OUT_JSON);
            sb.append("?reference=" + URLEncoder.encode(reference, "utf8"));
            sb.append("&key=" + API_KEY);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONObject jsonObjResult = jsonObj.getJSONObject("result");
            JSONObject jsonObjGemmetry = jsonObjResult.getJSONObject("geometry");
            JSONObject jsonObjLocation = jsonObjGemmetry.getJSONObject("location");

            System.out.println("jsonObj.toString() :::: " + jsonObj.toString());
            System.out.println("jsonObjLocation.toString() :::: " + jsonObjLocation.toString());

            resultList = new ArrayList<Double>(2);
            resultList.add(jsonObjLocation.getDouble("lat"));
            resultList.add(jsonObjLocation.getDouble("lng"));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    public String getAddress(double lat, double lng) {
        String address = "";

        Geocoder geocoder = new Geocoder(this, Locale.KOREA);

        List<Address> list = null;

        try {
            list = geocoder.getFromLocation(lat, lng, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (list == null) {
            Log.d(LOG_TAG, "List Address Null");
            return null;
        }

        if (list.size() > 0) {
            Address addr = list.get(0);
            Log.d(String.valueOf(addr.getMaxAddressLineIndex()), addr.toString());
            for (int j = 0; j <= addr.getMaxAddressLineIndex(); j++) {
                address = address + addr.getAddressLine(j);
            }

        }

        return address;
    }

}
