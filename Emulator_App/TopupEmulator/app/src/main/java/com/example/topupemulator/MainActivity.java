package com.example.topupemulator;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Entity.CreditPlan;
import Utility.Utility;

public class MainActivity extends AppCompatActivity {
    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag tag;
    String setting = "settingPreference";
    String hostAddress = "https://grinbuz.com/";
    String name[] = new String[10];
    String price[] = new String[10];
    int id[] = new int[10];
    int count;
    List<CreditPlan> listCreditPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new GetAllCreditPlan().execute();
        getSupportActionBar().hide();
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);

            }
        });
        adapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};
    }

    @Override
    protected void onNewIntent(Intent intent) {

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            if (Utility.isNetworkConnected(MainActivity.this)) {

                tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                //Read card balance and dataversion
                Utility utility = new Utility();
                String cardData = utility.readNDEFMessage(tag);
                if (cardData != null) {
                    String data[]=utility.getCardDataFromEncryptedString(cardData);
                    Intent topupIntent = new Intent(this, TopupActivity.class);
                    topupIntent.putExtra("cardId", Utility.bin2hex(tag.getId()));
                    topupIntent.putExtra("cardData",data);
                    startActivity(topupIntent);
                }


            }
        }
    }


    private class GetAllCreditPlan extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            Utility utility = new Utility();
            String strURL = "https://grinbuz.com/Api/GetAllCreditPlan?key=gbts_2016_capstone";
            // Getting JSON from URL
            JSONObject json = utility.getJSONFromUrl(strURL);
            return json;
        }

        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            //this method will be running on UI thread
            //Declare list of CreditPlan

            if (jsonObject != null) {
                boolean success;
                try {
                    success = jsonObject.getBoolean("success");
                    if (success) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            //Get jsonobject from Jsonarray
                            JSONObject object = jsonArray.getJSONObject(i);

                            //Create newintance CreditPlan
                            CreditPlan creditPlan = new CreditPlan();

                            // Storing JSON item in a Variable
                            count = i;
                            id[i] = object.optInt("Id");
                            name[i] = object.optString("Name");
                            price[i] = object.optString("Price");
//                            creditPlan.creditplanId = object.optInt("Id");
//                            creditPlan.creditplanName = object.optString("Name");
//                            creditPlan.creditplanDescription = object.optString("Description");
//                            creditPlan.creditplanPrice = object.optString("Price");
//                            //Add item
//                            listCreditPlan.add(creditPlan);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onPause() {
        super.onPause();
        ReadAndWriteModeOff();
    }

    @Override
    public void onResume() {
        super.onResume();
        ReadAndWriteModeOn();
    }

    private void ReadAndWriteModeOn() {
        writeMode = true;
        adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }

    private void ReadAndWriteModeOff() {
        writeMode = false;
        adapter.disableForegroundDispatch(this);
    }
}
