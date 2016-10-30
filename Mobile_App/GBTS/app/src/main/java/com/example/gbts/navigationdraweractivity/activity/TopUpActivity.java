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
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.MainActivity;
import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

public class TopUpActivity extends AppCompatActivity {
    //final Animation animAlpha= AnimationUtils.loadAnimation(this,R.anim.anim_alpha);
    String hostAddress = "https://grinbuz.net";
    String couponCode = "GBNAPTHE20K";

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
            if (coupon.equals(couponCode)) {
                Intent intent = getIntent();
                String[] params = {intent.getStringExtra("cardId"), coupon};
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
            JSONObject json = jParser.getJSONFromUrl(strURL);
            Log.d("DUC",strURL.toString());
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

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                alertDialogBuilder
                        .setTitle("Nạp tiền thành công")
                        .setCancelable(false)

                        .setNegativeButton("Thoát",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }


        }
    }
}
