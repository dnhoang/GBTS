package com.example.gbts.navigationdraweractivity.module.google.mapsAPI;

import android.os.AsyncTask;
import android.util.Log;

import com.example.gbts.navigationdraweractivity.utils.JSONParser;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by truon on 10/13/2016.
 */

public class DirectionFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?language=vi&";
    private static final String GOOGLE_API_KEY = "AIzaSyC_Nn1lgcszdfpjEH0ySBjdYdIQD8QChaQ";
    //        private static final String GOOGLE_API_KEY = "AIzaSyAxvdx9eKV_HPzrxGWiiNLL9I7WoDe0FSE";
//    private static final String GOOGLE_API_KEY = "AIzaSyA4EpKiXWt_UHlotCQjv1EWqdHrND8nTmA";
    private static final String GOOGLE_MODE = "bus";
    private DirectionFinderListener listener;
    private String origin;
    private String destination;

    public DirectionFinder(DirectionFinderListener listener, String origin, String destination) {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
    }

    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new DownloadRawData().execute(createUrl());
    }

    private String createUrl() throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(origin, "utf-8");
        String urlDestination = URLEncoder.encode(destination, "utf-8");

        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&mode=" + GOOGLE_MODE + "&key=" + GOOGLE_API_KEY;
//        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&key=" + GOOGLE_API_KEY;
    }

    private class DownloadRawData extends AsyncTask<String, Void, String> {
        URL url;

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
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
//            Log.d("anhtruongne ", "parseJSon");
        if (data == null)
            return;

        List<Route> routes = new ArrayList<>();
        List<Steps> stepsList = new ArrayList<>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        List<String> htmlInstruct = new ArrayList<>();
        Route route = new Route();
        /** Traversing all routes */
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");

            /** Traversing all legs */
            JSONObject js = jsonLegs.getJSONObject(0);
            JSONArray jsonSteps = js.getJSONArray("steps");

            /** Traversing all steps */
            for (int j = 0; j < jsonSteps.length(); j++) {
                JSONObject singleStep = jsonSteps.getJSONObject(j);
                try {
                    byte[] tmp = singleStep.getString("html_instructions").getBytes("UTF-8");
                    String htmls = new String(tmp, "UTF-8");
                    htmlInstruct.add(htmls);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
            JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");


            route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
            route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
            route.endAddress = jsonLeg.getString("end_address");
            route.startAddress = jsonLeg.getString("start_address");
            route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
            route.points = decodePolyLine(overview_polylineJson.getString("points"));
            String html = "";
            for (String item : htmlInstruct) {
                html += item + "<br/>";
            }
            try {
                byte[] tmp = html.getBytes("UTF-8");
                String htmls = new String(tmp, "UTF-8");
                route.html_instructions = htmls;
                Log.d("htmls ", htmls);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            routes.add(route);
        }

        listener.onDirectionFinderSuccess(routes);
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            //pheo dich bit << sang trai (shift_left),  >> sang phai (shift_right)
            int shift = 0;
            int result = 0;
            do {
                //Phương thức charAt() trong Java trả về ký tự tại chỉ mục đã xác định của String. Chỉ mục bắt đầu từ zero.
                //String name="vietjack";
                //char ch=name.charAt(4);//tra ve ky tu tai chi muc thu 4   is "j"
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }

}