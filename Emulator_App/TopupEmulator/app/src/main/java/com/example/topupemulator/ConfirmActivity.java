package com.example.topupemulator;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Utility.Utility;

public class ConfirmActivity extends AppCompatActivity {
    //NFC
    Tag tag;
    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    String creditPlanId, amount, staffPhone;
    String hostAddress="https://grinbuz.net";
    String setting="settingPreference";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        getSupportActionBar().hide();
        //Lấy thông tin nạp
        Bundle bundle=getIntent().getExtras();
        creditPlanId=bundle.getString("creditPlanId");
        amount=bundle.getString("amount");
        staffPhone=bundle.getString("staffPhone");
        //NFC
        adapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};
        //NFC
    }
    @Override
    protected void onNewIntent(Intent intent) {

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            if (Utility.isNetworkConnected(ConfirmActivity.this)) {

                tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                //Ghi the
                Utility utility = new Utility();
                if (tag == null) {
                    //Toast.makeText(ctx, ctx.getString(R.string.error_detected), Toast.LENGTH_LONG ).show();
                    Log.d("TAG", "NULL");
                } else {
                    //

                    String[] params = {Utility.bin2hex(tag.getId()), creditPlanId, staffPhone, amount};
                    new TopUpCard().execute(params);
                    //
                    Log.d("TAG", "KHAC NULL");


                }
            }
        }
    }
    private class TopUpCard extends AsyncTask<String, Void, JSONObject> {
        private ProgressDialog pDialog;
        String cardId, creditPlanId, staffPhone;
        String amount;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(ConfirmActivity.this);
            pDialog.setMessage("Loading data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            cardId = params[0];
            creditPlanId = params[1];
            staffPhone = params[2];
            amount = params[3];
            Utility utility = new Utility();
            SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
            hostAddress = sharedPreferences.getString("host", "https://grinbuz.net");
            String strURL = hostAddress+"/Api/AddCardBalanceByCash?key=gbts_2016_capstone" +
                    "&cardId=" + cardId +
                    "&creditPlanId=" + creditPlanId +
                    "&staffPhone=" + staffPhone;
            // Getting JSON from URL
            Log.d("DUC", strURL);
            JSONObject json = utility.getJSONFromUrl(strURL);
            return json;
        }

        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            Log.d("DUC", jsonObject.toString());
            if (jsonObject != null) {
                boolean success;
                try {
                    success = jsonObject.getBoolean("success");
                    if (success) {
                        pDialog.dismiss();
                        //isSuccess = true;
                        Utility utility = new Utility();
                        String cardData = utility.readNDEFMessage(tag);
                        if (cardData != null) {
                            String data[] = utility.getCardDataFromEncryptedString(cardData);
                            String cardBalance = (Integer.parseInt(data[0]) + Integer.parseInt(amount)) + "";
                            String dataToWrite = cardBalance + "|" + data[1];
                            Log.d("TAG", "Tiền trc khi nạp: " + data[0]);
                            Log.d("TAG", "Tiền nạp: " + amount);
                            Log.d("TAG", "Tiền sau khi nạp: " + cardBalance);

                            boolean check = utility.writeCard(dataToWrite, tag);
                            if (check) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConfirmActivity.this);
                                alertDialogBuilder
                                        .setTitle("Nạp tiền thành công")
                                        .setMessage("Số tiền trong thẻ hiện tại: "+cardBalance+"đ")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent = new Intent(ConfirmActivity.this, TopupActivity.class);
                                                startActivity(intent);
                                            }
                                        });


                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }


                        } else {
                            String message = jsonObject.getString("message");
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConfirmActivity.this);
                            alertDialogBuilder
                                    .setTitle("Thất bại")
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });


                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (FormatException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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
