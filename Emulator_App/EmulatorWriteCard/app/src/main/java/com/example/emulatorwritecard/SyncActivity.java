package com.example.emulatorwritecard;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import Util.Utility;

public class SyncActivity extends AppCompatActivity {
    String TAG = "SyncActivity";
    String hostAddress = "";
    String settings = "settingPreference";
    Tag mytag;
    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    String dataToWrite;
    final String keyAES = "ssshhhhhhhhhhh!!!!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        SharedPreferences sharedPreferences = getSharedPreferences(settings, MODE_PRIVATE);
        hostAddress = sharedPreferences.getString("host", "https://grinbuzz.com");
        getSupportActionBar().hide();
        adapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};

    }


    static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }

    protected void onNewIntent(Intent intent) {

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

            if (Utility.isNetworkConnected(SyncActivity.this)) {
                mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                String id = bin2hex(mytag.getId());
                Log.d("TAG ID", id);
//                String[] params = {id};
                new SyncNFCCard().execute(mytag);

            } else {
                Toast.makeText(this, "Không có kết nối với máy chủ", Toast.LENGTH_LONG).show();

            }
        }
    }

    private class SyncNFCCard extends AsyncTask<Tag, Void, JSONObject> {
        private ProgressDialog pDialog;
        String cardId;
//        Tag tag;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(SyncActivity.this);
            pDialog.setMessage("Đồng bộ dữ liệu ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Tag... params) {
            Utility jParser = new Utility();
//            tag = params[0];
            mytag = params[0];
            cardId = bin2hex(mytag.getId());

            String strURL = hostAddress + "/Api/SyncCard?key=gbts_2016_capstone&cardId=" + cardId;

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
            Long balance = new Long(0);
            Long dataVersion = new Long(0);

            //check success
            try {
                success = jsonObject.getBoolean("success");


            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (success) {

                try {
                    JSONObject data = jsonObject.getJSONObject("data");
                    balance = data.getLong("Balance");
                    dataVersion = Long.parseLong(data.getString("DataVersion") == "null" ? "0" : data.getString("DataVersion"));

                } catch (JSONException e) {
                    e.printStackTrace();

                }
                dataToWrite = balance + "|" + dataVersion;
                Boolean isWritten = writeNFCCard(dataToWrite);
                if (isWritten) {
                    new AlertDialog.Builder(SyncActivity.this)
                            .setTitle("GBTS")
                            .setMessage("Đồng bộ số dư thành công")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .show();
                } else{
                    //ko write dc
                }

            } else {
                //success=fail
            }
        }
    }

    public boolean writeNFCCard(String inputString) {
        String encryptedString = Utility.encrypt(inputString, keyAES);

        try {
            NdefRecord[] records = new NdefRecord[]{Utility.createRecord(encryptedString)};
            NdefMessage message = new NdefMessage(records);

            Ndef ndef = Ndef.get(mytag);
            if (ndef != null) {
                ndef.connect();
                ndef.writeNdefMessage(message);
                ndef.close();
            } else {
                NdefFormatable ndefFormatable = NdefFormatable.get(mytag);
                if (ndefFormatable != null) {
                    // initialize tag with new NDEF message

                    try {
                        ndefFormatable.connect();
                        ndefFormatable.format(message);
                    } finally {
                        try {
                            ndefFormatable.close();
                        } catch (Exception e) {
                        }
                    }
                }
            }
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
        if (adapter == null) {
            adapter = NfcAdapter.getDefaultAdapter(this);
        }
        adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }


    private void ReadModeOff() {
        writeMode = false;
        if (adapter == null) {
            adapter = NfcAdapter.getDefaultAdapter(this);
        }
        adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }
}
