package com.example.gbts.navigationdraweractivity.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.gbts.navigationdraweractivity.MainActivity;
import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.activity.CreditPlanActivity;
import com.example.gbts.navigationdraweractivity.activity.TopUpActivity;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.example.gbts.navigationdraweractivity.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by truon on 11/15/2016.
 */

public class FragmentTopUp extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topup, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String cardId = bundle.getString("cardId", "null");
            Log.d("truongne", "cardId  " + cardId);

            Button btn_topup = (Button) view.findViewById(R.id.btn_FM_TopUp);
            btn_topup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText edtCoupon = (EditText) getView().findViewById(R.id.edtFMCoupon);
                    final String coupon = edtCoupon.getText().toString().trim();
                    Log.d("truongne", "coupon " + coupon);

                    if (coupon != null && coupon != "") {
                        Bundle bundle = getArguments();
//                    String cardId = bundle.getString("cardId","null");
                        final String[] params = {bundle.getString("cardId"), coupon};
                        if (params != null) {
                            if (Utility.isNetworkConnected(getActivity())) {
                                new TopUpByCoupon().execute(params);
                            } else {
                                // custom dialog
                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setContentView(R.layout.custom_dialog_login);
                                dialog.setTitle(null);

                                Button dialogButton = (Button) dialog.findViewById(R.id.dialogBtnOK);
                                // if button is clicked, close the custom dialog
                                dialogButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (Utility.isNetworkConnected(getActivity())) {
                                            dialog.dismiss();
                                            new TopUpByCoupon().execute(params);
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
                    } else {
                        Log.d("truongne", "coupon is null ");
                    }
                }
            });
        }

        return view;
    }

    private class TopUpByCoupon extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String cardId, couponCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(getActivity());
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
            String strURL = Constance.API_TOPUP + "&cardId=" + cardId + "&code=" + couponCode;

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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder
                        .setTitle("Nạp tiền thành công")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        intent.putExtra("topup", "topup");
                                        startActivity(intent);
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
