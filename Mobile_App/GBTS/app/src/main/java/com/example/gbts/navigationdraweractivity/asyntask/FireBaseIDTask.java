package com.example.gbts.navigationdraweractivity.asyntask;

/**
 * Created by HoangDN on 10/3/2016.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;

import org.json.JSONObject;

public class FireBaseIDTask extends AsyncTask<String, Void, JSONObject> {

    //JSON Node Names
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONParser jParser = new JSONParser();

        String strURL = Constance.API_NOTIFICATION + "&phone=" + params[0] + "&token=" + params[1];

        // Getting JSON from URL
        JSONObject json = jParser.getJSONFromUrl(strURL);
        return json;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        boolean success = false;
        //check success
        try {
            success = jsonObject.optBoolean(TAG_SUCCESS);
            if (success) {
                Log.i("FireBaseIDTask", "Đăng ký token thành công");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}