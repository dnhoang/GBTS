package com.example.topupemulator;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
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

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        String cardId = bundle.getString("cardId");
        new GetCardInfo().execute(cardId);
        new GetAllCreditPlan().execute();
        list = (ListView) findViewById(R.id.listToptup);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TopupActivity.this);
                alertDialogBuilder
                        .setTitle("GBTS")
                        .setMessage("Nạp " + price[position] + "đ vào thẻ")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String creditPlanId = planid[position] + "";
                                Bundle bundle = getIntent().getExtras();
                                String cardId = bundle.getString("cardId");
                                SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
                                String staffPhone = sharedPreferences.getString("staffPhone", "");
                                String[] params = {cardId, creditPlanId, staffPhone};
                                new TopUpCard().execute(params);
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
            cardId=params[0];
            String strURL = "\n" +
                    "https://grinbuz.com/Api/GetCardInfo?key=gbts_2016_capstone&cardId="+cardId;
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
                        String balance=data.getString("Balance");
                        int status=data.getInt("Status");
                        TextView tvCardBalance=(TextView)findViewById(R.id.tvCardBalance);
                        tvCardBalance.setText("Số dư: "+balance+"đ");
                        TextView tvCardStatus=(TextView)findViewById(R.id.tvCardStatus);
                        if (status==0){
                            tvCardStatus.setText("Trạng thái: Chưa kích hoạt");
                            tvCardStatus.setTextColor(Color.WHITE);
                        }
                        if (status==1){
                            tvCardStatus.setText("Trạng thái: Đã kích hoạt");
                            tvCardStatus.setTextColor(Color.WHITE);
                        }
                        if (status==2){
                            tvCardStatus.setText("Trạng thái: Đã khóa");
                            tvCardStatus.setTextColor(Color.RED);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private class TopUpCard extends AsyncTask<String, Void, JSONObject> {
        private ProgressDialog pDialog;
        String cardId, creditPlanId, staffPhone;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(TopupActivity.this);
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
            Utility utility = new Utility();
            String strURL = "https://grinbuz.com/Api/AddCardBalanceByCash?key=gbts_2016_capstone" +
                    "&cardId="+ cardId+
                    "&creditPlanId=" +creditPlanId+
                    "&staffPhone="+staffPhone;
            // Getting JSON from URL
            Log.d("DUC", strURL);
            JSONObject json = utility.getJSONFromUrl(strURL);
            return json;
        }

        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            //this method will be running on UI thread
            //Declare list of CreditPlan
            //listCreditPlan = new ArrayList<>();
            Log.d("DUC", jsonObject.toString());
            if (jsonObject != null) {
                boolean success;
                try {
                    success = jsonObject.getBoolean("success");
                    if (success) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TopupActivity.this);
                        alertDialogBuilder
                                .setTitle("Thành công")
                                .setMessage("Nạp tiền thành công")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent=new Intent(TopupActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                });


                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    } else {
                        String message=jsonObject.getString("message");
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TopupActivity.this);
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

                } catch (JSONException e) {
                    e.printStackTrace();
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
            //listCreditPlan = new ArrayList<>();
            Log.d("DUC", jsonObject.toString());
            if (jsonObject != null) {
                boolean success;
                try {
                    success = jsonObject.getBoolean("success");
                    if (success) {

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
//                            creditPlan.creditplanId = object.optInt("Id");
//                            creditPlan.creditplanName = object.optString("Name");
//                            creditPlan.creditplanDescription = object.optString("Description");
//                            creditPlan.creditplanPrice = object.optString("Price");
//                            //Add item
//                            listCreditPlan.add(creditPlan);
                        }
                        CreditPlanAdapter adapter = new
                                CreditPlanAdapter(TopupActivity.this, name, price);

                        list = (ListView) findViewById(R.id.listToptup);

                        list.setAdapter(adapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
