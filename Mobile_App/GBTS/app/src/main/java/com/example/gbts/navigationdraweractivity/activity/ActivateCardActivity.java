package com.example.gbts.navigationdraweractivity.activity;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.example.gbts.navigationdraweractivity.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;

public class ActivateCardActivity extends AppCompatActivity {
    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag tag;
    String setting = "Info";

    String hostAddress = "https://grinbuz.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //NFC
        adapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};
        //END NFC
        setContentView(R.layout.activity_activate_card);
    }

    static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }

    private class ActivateNFCCard extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String cardId, phone;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(ActivateCardActivity.this);
            pDialog.setMessage("Loading data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jParser = new JSONParser();
            cardId = params[0];
            phone = params[1];
            //thay hostAddress thanh grinbuz
            String strURL = hostAddress + "/Api/ActivateAccountByApp?key=gbts_2016_capstone&cardId=" + cardId + "&phone=" + phone;

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
            try {
                success = jsonObject.getBoolean("success");
                message = jsonObject.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (success) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }


        }
    }

    @Override
    protected void onNewIntent(Intent intent) {


        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

            if (Utility.isNetworkConnected(ActivateCardActivity.this)) {
                final EditText edtPhone = (EditText) findViewById(R.id.edtActivatePhoneNumber);
                String phone = edtPhone.getText().toString().trim();
                if (phone!=null){
                    tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);


                    String cardId = bin2hex(tag.getId());

                    String[] params = {cardId, phone};
                    //TicketResult ticketResult = new TicketResult();
                    new ActivateNFCCard().execute(params);
                }
                else{
                    //Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_LONG).show();
                    edtPhone.setError("Vui lòng nhập số điện thoại");
                }


            } else {
                Toast.makeText(this, "Không thể kết nối với máy chủ!", Toast.LENGTH_SHORT).show();
            }
        }
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