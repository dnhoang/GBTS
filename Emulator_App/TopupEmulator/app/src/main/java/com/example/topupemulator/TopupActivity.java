package com.example.topupemulator;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import Adapter.CreditPlanAdapter;
import Entity.CreditPlan;
import Utility.Utility;

public class TopupActivity extends AppCompatActivity {
    ListView list;
    String setting = "settingPreference";
    String hostAddress = "https://grinbuz.com/";
    String name[];
    String price[];
    int planid[];
    int count;
    Boolean isSuccess;
    String creditPlanId, amount, staffPhone;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);
        getSupportActionBar().hide();
        isSuccess = false;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopupActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        new GetAllCreditPlan().execute();
        list = (ListView) findViewById(R.id.listToptup);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                creditPlanId = planid[position] + "";

                SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
                staffPhone = sharedPreferences.getString("phone", "");
                amount = price[position];


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TopupActivity.this);
                alertDialogBuilder
                        .setTitle("GBTS")
                        .setMessage("Nạp " + price[position] + "đ vào thẻ")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(TopupActivity.this, ConfirmActivity.class);
                                intent.putExtra("creditPlanId", creditPlanId);
                                intent.putExtra("amount", amount);
                                intent.putExtra("staffPhone", staffPhone);
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("Thoát",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }


    private class GetCardInfo extends AsyncTask<String, Void, JSONObject> {
        String cardId;

        @Override
        protected JSONObject doInBackground(String... params) {
            Utility utility = new Utility();
            cardId = params[0];
            SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
            hostAddress = sharedPreferences.getString("host", "https://grinbuz.net");
            String strURL = hostAddress+"/Api/GetCardInfo?key=gbts_2016_capstone&cardId=" + cardId;
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
                        JSONObject data = jsonObject.getJSONObject("data");
                        String balance = data.getString("Balance");
                        int status = data.getInt("Status");
                        //TextView tvCardBalance = (TextView) findViewById(R.id.tvCardBalance);
                        //tvCardBalance.setText("Số dư: " + balance + "đ");
                        Bundle bundle = getIntent().getExtras();
                        String cardData[] = bundle.getStringArray("cardData");
                        //tvCardBalance.setText("Số dư: " + cardData[0] + "đ");
                        //TextView tvCardStatus = (TextView) findViewById(R.id.tvCardStatus);
                        if (status == 0) {
                            //tvCardStatus.setText("Trạng thái: Chưa kích hoạt");
                            //tvCardStatus.setTextColor(Color.WHITE);
                        }
                        if (status == 1) {
                            //tvCardStatus.setText("Trạng thái: Đã kích hoạt");
                            //tvCardStatus.setTextColor(Color.WHITE);
                        }
                        if (status == 2) {
                            //tvCardStatus.setText("Trạng thái: Đã khóa");
                            //tvCardStatus.setTextColor(Color.RED);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private class GetAllCreditPlan extends AsyncTask<String, Void, JSONObject> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(TopupActivity.this);
            progressDialog.setMessage("Vui lòng đợi trong giây lát ...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            Utility utility = new Utility();
            SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);

            hostAddress = sharedPreferences.getString("host", "https://grinbuz.net");
            String strURL = hostAddress + "/Api/GetAllCreditPlan?key=gbts_2016_capstone";
            // Getting JSON from URL
            JSONObject json = utility.getJSONFromUrl(strURL);
            return json;
        }

        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            //this method will be running on UI thread
            //Declare list of CreditPlan
            //listCreditPlan = new ArrayList<>();
//            Log.d("DUC", jsonObject.toString());
            if (jsonObject != null) {
                boolean success;
                try {
                    success = jsonObject.getBoolean("success");
                    if (success) {
                        progressDialog.dismiss();
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        name = new String[jsonArray.length()];
                        price = new String[jsonArray.length()];
                        planid = new int[jsonArray.length()];
                        count = jsonArray.length();
                        for (int i = 0; i < jsonArray.length(); i++) {

                            //Get jsonobject from Jsonarray
                            JSONObject object = jsonArray.getJSONObject(i);

                            //Create newintance CreditPlan
                            CreditPlan creditPlan = new CreditPlan();

                            // Storing JSON item in a Variable

                            planid[i] = object.optInt("Id");
                            name[i] = object.optString("Name");
                            price[i] = object.optString("Price");
                            //Log.d("DUC", name.toString());
                        }
                        CreditPlanAdapter adapter = new
                                CreditPlanAdapter(TopupActivity.this, name, price);

                        list = (ListView) findViewById(R.id.listToptup);

                        list.setAdapter(adapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Intent intent=new Intent(TopupActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        }
    }


}
