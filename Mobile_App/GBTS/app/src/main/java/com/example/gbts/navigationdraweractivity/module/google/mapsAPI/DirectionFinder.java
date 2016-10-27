package com.example.gbts.navigationdraweractivity.module.google.mapsAPI;

import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.activity.ActivityGoogleFindPath;
import com.example.gbts.navigationdraweractivity.fragment.FragmentDirection;
import com.example.gbts.navigationdraweractivity.fragment.GetAllButRoute;
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
 * Created by truon on 10/13/2016.
 */

public class DirectionFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?language=vi&";
    private static final String GOOGLE_API_KEY = "AIzaSyC_Nn1lgcszdfpjEH0ySBjdYdIQD8QChaQ";
    private static final String MODE = "transit";
    private static final String TRANSIT_MODE = "bus";
    //    private static final String TRANSIT_ROUTING_PREFERENCE = "less_walking";
    private static final String TRANSIT_ROUTING_PREFERENCE = "less_walking";
    private static final String DEPARTURE_TIME = "1482237000";
    private DirectionFinderListener listener;
    private String origin;
    private String destination;

    private Context context;

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
        String urlOrigin = URLEncoder.encode(origin, "UTF-8");
        String urlDestination = URLEncoder.encode(destination, "UTF-8");
        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&mode=" + MODE + "&transit_mode=" + TRANSIT_MODE + "&transit_routing_preference=" + TRANSIT_ROUTING_PREFERENCE + "&alternatives=true" + "&traffic_model=optimistic" + "&key=" + GOOGLE_API_KEY;
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

        List<Route> routes = new ArrayList<>();
        JSONObject jsonData = new JSONObject(data);
        Route route = new Route();


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


        route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
        route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
        route.endAddress = jsonLeg.getString("end_address");
        route.startAddress = jsonLeg.getString("start_address");
        route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
        route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
        route.points = decodePolyLine(overview_polylineJson.getString("points"));
        String html = "";
        String before = "<li><a href=\"#\"><img src=\"http://i.imgur.com/iSVvT7G.png\" /> ";
        String after = "</a></li>";
        for (String item : busList) {
            html += before + item + after;
        }
        route.html_instructions = "<strong>Bạn cần đi qua " + busList.size() + " tuyến xe bus:</strong>  <br/><br/>" + html;
        routes.add(route);
//        }
        Log.d("meoww", "jsonsLresultst " + busList.toString());
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
