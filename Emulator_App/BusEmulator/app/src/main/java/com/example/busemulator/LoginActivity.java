package com.example.busemulator;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;

import Util.Utility;

public class LoginActivity extends AppCompatActivity {
    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode,onTrip;
    String hostAddress="https://grinbuz.net";
    String setting = "settingPreference";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        adapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String cardId=bin2hex(tag.getId());
            new GetStaffInfo().execute(cardId);
        }
    }
    private class GetStaffInfo extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String cardId;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Loading data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            Utility jParser = new Utility();
            cardId = params[0];
            String strURL = hostAddress + "/Api/GetStaffInfo?key=gbts_2016_capstone&phone=" + cardId;

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(strURL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            // Hide dialog
            pDialog.dismiss();
            boolean success = false;
            String message = "";
            //check success
            if (jsonObject!=null){
                try {
                    success = jsonObject.getBoolean("success");
                    message = jsonObject.getString("message");
                    if (success) {

                        final SharedPreferences sharedPreferences=getSharedPreferences(setting,MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        onTrip=sharedPreferences.getBoolean("onTrip",false);
                        Log.d("DATA!!!!","OnTrip=="+onTrip);
                        if (onTrip==false){
                            onTrip=true;
                            editor.putBoolean("onTrip",onTrip);
                            editor.commit();
                            final Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                            //Lấy thông tin tuyến
                            String routeName=sharedPreferences.getString("name","");
                            final String departure=Utility.getTripDeparturePoint(routeName);
                            final String destination=Utility.getTripDestinationPoint(routeName);
                            //
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                            alertDialogBuilder
                                    .setTitle("Chọn hướng đi")
                                    .setCancelable(false)
                                    .setPositiveButton("Đi "+departure, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            intent.putExtra("direction", departure);
                                            startActivity(intent);

                                        }
                                    })
                                    .setNeutralButton("Đi "+destination, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            intent.putExtra("direction", destination);
                                            startActivity(intent);

                                        }
                                    });



                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        } else{
                            onTrip=false;
                            editor.putBoolean("onTrip",onTrip);
                            editor.commit();
                        }


                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                        alertDialogBuilder
                                .setTitle("Thẻ không hợp lệ")
                                .setCancelable(false)

                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        dialog.dismiss();

                                    }
                                });



                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else{
                Intent intent=new Intent(LoginActivity.this,SettingActivity.class);
                startActivity(intent);
            }


        }
    }
    static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
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
