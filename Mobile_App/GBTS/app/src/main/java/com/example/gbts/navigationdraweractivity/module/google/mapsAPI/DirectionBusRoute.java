package com.example.gbts.navigationdraweractivity.module.google.mapsAPI;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by truon on 10/27/2016.
 */

public class DirectionBusRoute {

    private DirectionBusRouteListener listener;
    private String origin;
    private String destination;

    private Context context;

    public DirectionBusRoute(DirectionBusRouteListener listener, String origin, String destination) {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
    }

    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionBusRouteStart();
        new DirectionBusRoute.DownloadRawData().execute(createUrl());
    }

    private String createUrl() throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(origin, "UTF-8");
        String urlDestination = URLEncoder.encode(destination, "UTF-8");
        return  "origin=" + urlOrigin + "&destination=" + urlDestination ;
    }

    private class DownloadRawData extends AsyncTask<String, Void, String> {
        URL url;

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                url = new URL(link);
                URLConnection urlConn = url.openConnection();
//                SET TIMEOUT FOR URL Connection
                urlConn.setConnectTimeout(10000);
                urlConn.setReadTimeout(10000);
                urlConn.setAllowUserInteraction(false);
                urlConn.setDoOutput(true);

                InputStream is = urlConn.getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                Log.d("anhtruong", "url " + createUrl().toString());
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseJSon(String data) throws JSONException {

        if (data == null)
            return;

        List<BusStop> busStops = new ArrayList<>();
        JSONObject jsonData = new JSONObject(data);
        BusStop busStop = new BusStop();


        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        //NOT routes
        if (jsonRoutes.length() == 0) {
            Log.d("anhtruong", "Route don't exist");
            return;
        }
        /** Traversing all routes */
//        for (int i = 0; i < jsonRoutes.length(); i++) {
        JSONObject jsonRoute = jsonRoutes.getJSONObject(0);

        JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
        JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
//            Log.d("meoww", "jsonsLegsObject " + jsonLegs.toString());
        /** Traversing all legs */
        JSONObject js = jsonLegs.getJSONObject(0);
        JSONArray jsonSteps = js.getJSONArray("steps");

        List<String> busList = new ArrayList<>();
        for (int j = 0; j < jsonSteps.length(); j++) {
            JSONObject singleStep = jsonSteps.getJSONObject(j);

            if (singleStep.getString("travel_mode").equals("TRANSIT")) {
//                JSONObject html_instructions = singleStep.getJSONObject("html_instructions");
                JSONObject transit_details = singleStep.getJSONObject("transit_details");
                JSONObject line = transit_details.getJSONObject("line");
                String busName = line.getString("name");
                busList.add(busName);
            }
        }

//        results.add(busList);
        JSONObject jsonLeg = jsonLegs.getJSONObject(0);
        JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
        JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
        JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
        JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");


        busStops.add(busStop);
//        }
        Log.d("meoww", "jsonsLresultst " + busList.toString());
        listener.onDirectionBusRouteSuccess(busStops);
    }


}
