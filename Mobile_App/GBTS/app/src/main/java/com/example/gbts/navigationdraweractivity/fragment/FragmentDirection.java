package com.example.gbts.navigationdraweractivity.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.activity.ActivityGoogleFindPath;
import com.example.gbts.navigationdraweractivity.enity.AutoCompleteBean;
import com.example.gbts.navigationdraweractivity.utils.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by truon on 9/30/2016.
 */

public class FragmentDirection extends DialogFragment {

    AutoCompleteTextView AutoOrigin, AutoDestination;
    TextView txtClear1, txtClear2;
    Button btnFindPath;


    private ArrayList<AutoCompleteBean> resultList;
    private ArrayList<Double> locationResult;

    private static final String LOG_TAG = "FragmentDirection";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_SEARCH = "/search";
    private static final String TYPE_DETAILS = "/details";
    private static final String OUT_JSON = "/json";
    //    private static final String API_KEY = "AIzaSyAnG0oOnOt1pNqhpbU_uwmCbaPjeBYl6VU";
    private static final String API_KEY = "AIzaSyA65-eqSvIefv4lY3vARmN4fwVc1d4lPaE";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_direction, container, false);

        //Get Control Widget
        AutoOrigin = (AutoCompleteTextView) view.findViewById(R.id.AutoOrigin);
        AutoDestination = (AutoCompleteTextView) view.findViewById(R.id.AutoDestination);

        txtClear1 = (TextView) view.findViewById(R.id.txtClear1);
        txtClear2 = (TextView) view.findViewById(R.id.txtClear2);
        txtClear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoOrigin.setText("");
            }
        });
        txtClear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoDestination.setText("");
            }
        });


        btnFindPath = (Button) view.findViewById(R.id.btnFind);

        //Event onclick
        AutoOrigin.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.list_item));
        AutoOrigin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        AutoDestination.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.list_item));
        AutoDestination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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


        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String origin = AutoOrigin.getText().toString();
                String destination = AutoDestination.getText().toString();
                if (origin.length() == 0) {
                    Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                    AutoOrigin.startAnimation(shake);
                    AutoOrigin.requestFocus();
                } else if (origin.length() > 0 && destination.length() == 0) {
                    Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                    AutoDestination.startAnimation(shake);
                    AutoDestination.requestFocus();
                } else if (origin.length() == 0 && destination.length() > 0) {
                    Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                    AutoOrigin.startAnimation(shake);
                    AutoOrigin.requestFocus();
                } else {
                    final Intent intent = new Intent(getActivity(), ActivityGoogleFindPath.class);
                    intent.putExtra("origin", origin);
                    intent.putExtra("destination", destination);
                    if (Utility.isNetworkConnected(getActivity())) {
                        startActivity(intent);
                    } else {
                        // custom dialog
                        final Dialog dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.custom_dialog_login);
                        dialog.setTitle("Mất kết nối mạng ...");

                        Button dialogButton = (Button) dialog.findViewById(R.id.dialogBtnOK);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Utility.isNetworkConnected(getActivity())) {
                                    dialog.dismiss();
                                    startActivity(intent);
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
                }
            }
        });
        return view;
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
                        result = new ArrayList<>(resultList.size());
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
            System.out.println("truongtq url " + url.toString());
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
            resultList = new ArrayList<>(predsJsonArray.length());
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

            resultList = new ArrayList<>(2);
            resultList.add(jsonObjLocation.getDouble("lat"));
            resultList.add(jsonObjLocation.getDouble("lng"));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }
}
