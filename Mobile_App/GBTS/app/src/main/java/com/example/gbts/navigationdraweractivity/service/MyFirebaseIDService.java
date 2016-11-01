package com.example.gbts.navigationdraweractivity.service;

/**
 * Created by HoangDN on 10/3/2016.
 */

import android.content.SharedPreferences;

import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONObject;

/**
 * Created by cafe on 11/08/2016.
 */

public class MyFirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
//        String token = FirebaseInstanceId.getInstance().getToken();
//
//        //Store phone number
//        SharedPreferences preferences = getSharedPreferences("Info", MODE_PRIVATE);
//        String phoneInfo = preferences.getString("Phonenumber", "Chào mừng bạn đến với thế giới hệ thống xe bus thông minh!!");
//
//        JSONParser jParser = new JSONParser();
//
//        String strURL = Constance.API_NOTIFICATION + "&phone=" + phoneInfo + "&token=" + token;
//
//        // Getting JSON from URL
//        JSONObject json = jParser.getJSONFromUrlPOST(strURL);

    }

}

