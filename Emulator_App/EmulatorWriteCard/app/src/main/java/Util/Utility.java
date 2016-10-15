package Util;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by ducdmse61486 on 10/4/2016.
 */

public class Utility  extends Application{

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public Utility() {

    }
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null) && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    public JSONObject getJSONFromUrl(String apiUrl) {
        // Making HTTP request
        try {
            URL url = new URL(apiUrl);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
//            urlConnection.setRequestProperty("User-Agent", "");
//            urlConnection.setRequestMethod("POST");
//            urlConnection.setDoInput(true);
            urlConnection.connect();

            if (urlConnection != null) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }

                    //GET String JSON FROM API
                    json = stringBuilder.toString();

                    bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Không thể kết nối với server!", Toast.LENGTH_SHORT).show();
            }
//        }  catch (SocketTimeoutException e){
//
//                    Toast.makeText(getApplicationContext(), "Không thể kết nối với server!", Toast.LENGTH_SHORT).show();

        }catch (IOException e) {
            e.printStackTrace();
        }
        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON OBJECT
        return jObj;

    }
}
