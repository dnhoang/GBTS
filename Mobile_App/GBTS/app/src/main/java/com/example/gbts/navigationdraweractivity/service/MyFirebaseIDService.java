package com.example.gbts.navigationdraweractivity.service;

/**
 * Created by HoangDN on 10/3/2016.
 */

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.activity.LoginActivity;
import com.example.gbts.navigationdraweractivity.asyntask.FireBaseIDTask;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cafe on 11/08/2016.
 */

public class MyFirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();

        //Store phone number
        SharedPreferences preferences = getSharedPreferences("Info", MODE_PRIVATE);
        String phoneInfo = preferences.getString("Phonenumber", "Chào mừng bạn đến với thế giới hệ thống xe bus thông minh!!");

        JSONParser jParser = new JSONParser();

        String strURL = Constance.API_NOTIFICATION + "&phone=" + phoneInfo + "&token=" + token;

        // Getting JSON from URL
        JSONObject json = jParser.getJSONFromUrl(strURL);

    }

}

