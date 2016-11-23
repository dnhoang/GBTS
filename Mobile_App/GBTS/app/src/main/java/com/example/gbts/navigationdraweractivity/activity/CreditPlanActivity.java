package com.example.gbts.navigationdraweractivity.activity;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.adapter.CreditPlanAdapter;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.enity.CreditPlan;
import com.example.gbts.navigationdraweractivity.fragment.CreditCard;
import com.example.gbts.navigationdraweractivity.fragment.FragmentTopUp;
import com.example.gbts.navigationdraweractivity.listener.RecyclerTouchListener;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.example.gbts.navigationdraweractivity.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreditPlanActivity extends AppCompatActivity {

    private static final String TAG = "CreditPlanActivity";


    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private RecyclerView mRecyclerView;
    private CreditPlanAdapter mAdapter;
    List<CreditPlan> listCreditPlan;
    CreditPlan creditPlan;
    ActionBar actionBar;
    private static final String TAG_CREDITPLAN_ID = "Id";
    private static final String TAG_CREDITPLAN_NAME = "Name";
    private static final String TAG_CREDITPLAN_DESCRIPTION = "Description";
    private static final String TAG_CREDITPLAN_PRICE = "Price";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_plan);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (Utility.isNetworkConnected(CreditPlanActivity.this)) {
            new AsyncFetch().execute();
        } else {
            // custom dialog
            final Dialog dialog = new Dialog(CreditPlanActivity.this);
            dialog.setContentView(R.layout.custom_dialog_login);
            dialog.setTitle("Mất kết nối mạng ...");

            Button dialogButton = (Button) dialog.findViewById(R.id.dialogBtnOK);
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utility.isNetworkConnected(CreditPlanActivity.this)) {
                        dialog.dismiss();
                        new AsyncFetch().execute();
                    }
                }
            });

            Button dialogCancel = (Button) dialog.findViewById(R.id.dialogBtnCancel);
            // if button is clicked, close the custom dialog
            dialogCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
            dialog.show();
        }
    }

    //ANH TRUONG
    public void clickToTopUp(View v) {
        Intent intentGetCardID = getIntent();
        String cardID = intentGetCardID.getStringExtra("cardIDForPayPal").toString();
//        Log.d("truongne", "getintent " + cardID);
        Bundle bundle = new Bundle();
        bundle.putString("cardId", cardID);
        FragmentTopUp fragmentTopUp = new FragmentTopUp();
        fragmentTopUp.setArguments(bundle);
        FragmentManager manager = getFragmentManager();
        if (bundle != null) {
            fragmentTopUp.show(manager, "TOPUP");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //PARSE JSON AND SET ADAPTER FOR RECYCLE VIEW
    private class AsyncFetch extends AsyncTask<String, Void, JSONObject> {
        String phone;
        String url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences preferences = getBaseContext().getSharedPreferences("Info", Context.MODE_PRIVATE);
            phone = preferences.getString("Phonenumber", "empty");
        }

        //PARSE JSONOBJECT FROM URL
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jParser = new JSONParser();
            url = Constance.API_GET_CREDIT_PLAN + "&phone=" + phone;
            Log.d("hoangga ", "url " + url);
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrlPOST(url);
            return json;
        }

        //GET JSONOBJECT FROM JSONARRAY
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            //this method will be running on UI thread
            //Declare list of CreditPlan
            listCreditPlan = new ArrayList<>();
            try {

                JSONArray jsonArray = jsonObject.getJSONArray("data");

                Log.d(TAG, jsonArray.length() + "");
                Log.d(TAG, jsonArray.toString());

                for (int i = 0; i < jsonArray.length(); i++) {

                    //Get jsonobject from Jsonarray
                    JSONObject object = jsonArray.getJSONObject(i);

                    //Create newintance CreditPlan
                    creditPlan = new CreditPlan();

                    // Storing JSON item in a Variable
                    creditPlan.creditplanId = object.optInt(TAG_CREDITPLAN_ID);
                    creditPlan.creditplanName = object.optString(TAG_CREDITPLAN_NAME);
                    creditPlan.creditplanDescription = object.optString(TAG_CREDITPLAN_DESCRIPTION);
                    creditPlan.creditplanPrice = object.optDouble(TAG_CREDITPLAN_PRICE);

                    //Add item
                    listCreditPlan.add(creditPlan);
                }

                // Setup and Handover data to recyclerview
                mRecyclerView = (RecyclerView) findViewById(R.id.listView_CreditPlan);
                mAdapter = new CreditPlanAdapter(CreditPlanActivity.this, listCreditPlan);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(CreditPlanActivity.this));
                mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView,
                        new RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                CreditPlan creditPlan = listCreditPlan.get(position);
                                int creditPlanID = creditPlan.getCreditplanId();
                                String creditPlanName = creditPlan.getCreditplanName();
                                String creditPlanDescription = creditPlan.getCreditplanDescription();
                                double creditPlanPrice = creditPlan.getCreditplanPrice();

                                Log.d(TAG + "id", creditPlanID + "");
                                Log.d(TAG + "price", creditPlanPrice + "");
                                Log.d(TAG + "desc", creditPlanDescription.toString() + "");
                                Log.d(TAG + "name", creditPlanName + "");

                                //Get Intent contain bundle cardID
                                Intent intentGetCardID = getIntent();
                                Bundle bundle1 = intentGetCardID.getExtras();
                                //get cardid
                                String cardId = bundle1.getString("cardIDForPayPal");
                                Log.d(TAG, "cardId" + cardId);


                                //Create bundle to reference values in next class
                                Bundle bundle = new Bundle();

                                bundle.putString("cardIDForPayPal1", cardId);
                                bundle.putInt("creditPlanID", creditPlanID);
                                bundle.putString("creditPlanName", creditPlanName);
                                bundle.putString("creditPlanDescription", creditPlanDescription);
                                bundle.putDouble("creditPlanPrice", creditPlanPrice);

                                //After all data has been entered and calculated, go to new page for results
                                Intent myIntent = new Intent();

                                myIntent.putExtras(bundle);
                                myIntent.setClass(getBaseContext(), PaypalActivity.class);

                                startActivity(myIntent);


//                                onBuyPress(creditPlan);
//                                Toast.makeText(getApplicationContext(), creditPlan.getCreditplanName() + " is selected!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onLongClick(View view, int position) {

                            }
                        }));

                Log.d(TAG, listCreditPlan.size() + "");
            } catch (JSONException e) {
//                Toast.makeText(CreditPlanActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

        }
    }

}
