package com.example.gbts.navigationdraweractivity.asyntask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.adapter.CardNFCDetailAdapter;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.enity.CardNFC;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by truon on 10/5/2016.
 */

public class AsyncCardNFCListViewLoader extends AsyncTask<String, Void, List<CardNFC>> {
    List<CardNFC> result = new ArrayList<>();
    JSONParser jParser = new JSONParser();
    String strURL;

    String cardID;
    String name;
    String registrationDate;
    double balance;
    int status;

    private static final String TAG_CARD_ID = "CardId";
    private static final String TAG_CARD_NAME = "CardName";
    private static final String TAG_REGISTRATION_DATE = "RegistrationDate";
    private static final String TAG_BALANCE = "Balance";
    private static final String TAG_CARD_STATUS = "Status";


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(List<CardNFC> result) {
        super.onPostExecute(result);
        Log.d("AsyncCardNFC List", result.size() + "");
    }

    @Override
    protected List<CardNFC> doInBackground(String... params) {
        result = new ArrayList<>();
        jParser = new JSONParser();

        strURL = Constance.API_GETALLCARD + "&phone=" + params[0];

        // Getting JSON from URL
        JSONObject json = jParser.getJSONFromUrl(strURL);

        try {
            //Get array json from json object
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                result.add(convertCardNfc(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private CardNFC convertCardNfc(JSONObject obj) {

        cardID = obj.optString(TAG_CARD_ID);
        name = obj.optString(TAG_CARD_NAME);
        registrationDate = obj.optString(TAG_REGISTRATION_DATE);
        balance = obj.optDouble(TAG_BALANCE);
        status = obj.optInt(TAG_CARD_STATUS);

        return new CardNFC(cardID, name, registrationDate, balance, status);
    }
}
