package com.example.emulatorwritecard;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;

import Util.Utility;

public class MainActivity extends AppCompatActivity {
    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag mytag;
    String hostAddress = "";
    RelativeLayout succesLayout;
    RelativeLayout failLayout;
    TextView messageResult;
    String settings = "settingPreference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences(settings, MODE_PRIVATE);
        hostAddress = sharedPreferences.getString("host", "https://grinbuzz.com");
        getSupportActionBar().hide();
        succesLayout = (RelativeLayout) findViewById(R.id.container);
        succesLayout.setVisibility(View.INVISIBLE);
        failLayout = (RelativeLayout) findViewById(R.id.containerfail);
        failLayout.setVisibility(View.INVISIBLE);
        adapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNetworkConnected(getApplicationContext())) {
                    Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Vui lòng kiểm tra kết nối!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }

    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            if (Utility.isNetworkConnected(MainActivity.this)) {
                mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                SharedPreferences sharedPreferences = getSharedPreferences(settings, MODE_PRIVATE);
                String id = bin2hex(mytag.getId());
                String phone = sharedPreferences.getString("phone", "");
                if (phone.equals("")) {
                    Toast.makeText(this, "Vui lòng cấu hình máy", Toast.LENGTH_LONG).show();
                } else {

                    String[] params = {id, phone};
                    new WriteNFCCard().execute(params);
                }
            } else {
                Toast.makeText(this, "Không có kết nối với máy chủ", Toast.LENGTH_LONG).show();

            }
        }
    }

    private class WriteNFCCard extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String cardId, phone;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            Utility jParser = new Utility();
            cardId = params[0];
            phone = params[1];
            String strURL = hostAddress + "/Api/RequestAddCard?key=gbts_2016_capstone&phone=" + phone + "&cardId=" + cardId;
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
            //check success
            try {
                success = jsonObject.getBoolean("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (success) {


                succesLayout = (RelativeLayout) findViewById(R.id.container);
                succesLayout.setVisibility(View.VISIBLE);
                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.b7);
                mediaPlayer.start();
                String message = null;
                try {
                    message = jsonObject.getString("message");

                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                messageResult = (TextView) findViewById(R.id.tvSuccess);
                messageResult.setText(message);

                changeLayout(true);
            } else {
                failLayout = (RelativeLayout) findViewById(R.id.containerfail);
                failLayout.setVisibility(View.VISIBLE);
                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.b7);
                mediaPlayer.start();

                String message = null;
                try {
                    message = jsonObject.getString("message");

                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                messageResult = (TextView) findViewById(R.id.tvFail);
                messageResult.setText(message);
                changeLayout(false);
            }


        }
    }

    private void changeLayout(final boolean result) {

        final RelativeLayout sucess = (RelativeLayout) findViewById(R.id.container);
        final RelativeLayout fail = (RelativeLayout) findViewById(R.id.containerfail);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
        CountDownTimer timer = new CountDownTimer(2000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {


            }

            @Override
            public void onFinish() {
                if (result == true) {
                    sucess.setVisibility(View.INVISIBLE);
                    fab.show();
                    Intent intent = new Intent(getApplicationContext(), SyncActivity.class);
                    startActivity(intent);

                } else {
                    //fail
                    fail.setVisibility(View.INVISIBLE);
                    fab.show();
                }
            }
        };
        timer.start();
    }

    public void onPause() {
        super.onPause();
        ReadModeOff();
    }

    @Override
    public void onResume() {
        super.onResume();

        ReadModeOn();
    }

    private void ReadModeOn() {
        writeMode = true;
        adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }

    private void ReadModeOff() {
        writeMode = false;
        adapter.disableForegroundDispatch(this);
    }
}
