package com.example.gbts.navigationdraweractivity.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.gbts.navigationdraweractivity.MainActivity;
import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

public class TopUpActivity extends AppCompatActivity {
    //final Animation animAlpha= AnimationUtils.loadAnimation(this,R.anim.anim_alpha);
    String hostAddress = "https://grinbuz.net";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void clickToTopUpByCard(View view) {
        //view.startAnimation(animAlpha);
        EditText edtCoupon = (EditText) findViewById(R.id.edtCoupon);
        String coupon = edtCoupon.getText().toString().trim();
        if (coupon != null) {
            Intent intent = getIntent();
            String[] params = {intent.getStringExtra("cardId"), coupon};
            if (params != null) {
                new TopUpByCoupon().execute(params);
            }
        }

    }

    private class TopUpByCoupon extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String cardId, couponCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(TopUpActivity.this);
            pDialog.setMessage("Vui lòng đợi trong giây lát ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jParser = new JSONParser();
            cardId = params[0];
            couponCode = params[1];
            //thay hostAddress thanh grinbuz
            String strURL = hostAddress + "/Api/Topup?key=gbts_2016_capstone&cardId=" + cardId + "&code=" + couponCode;

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrlPOST(strURL);
            Log.d("DUC", strURL.toString());
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TopUpActivity.this);
                alertDialogBuilder
                        .setTitle("Nạp tiền thành công")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(TopUpActivity.this, MainActivity.class);
                                        intent.putExtra("topup", "topup");
                                        startActivity(intent);
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TopUpActivity.this);
                alertDialogBuilder
                        .setTitle(message)
                        .setCancelable(false)
                        .setNegativeButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }


        }
    }
}
