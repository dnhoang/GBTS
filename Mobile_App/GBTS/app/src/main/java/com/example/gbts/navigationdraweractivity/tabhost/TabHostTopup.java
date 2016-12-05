package com.example.gbts.navigationdraweractivity.tabhost;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.MainActivity;
import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.example.gbts.navigationdraweractivity.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by truon on 11/20/2016.
 */

public class TabHostTopup extends Fragment {
    Button btn_topup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabhost_top_up, container, false);
        Log.d("tabvoichahost", "onCreateView " + "TabHostTopup");
        Bundle bundle = getActivity().getIntent().getExtras();
//        Bundle bundle = getArguments();
        final int[] clickcount = {0};
        if (bundle != null) {
            final String cardId = bundle.getString("cardIDCreditDetails", "null");
            Log.d("truongne", "cardId  " + cardId);
            btn_topup = (Button) view.findViewById(R.id.btn_tab_TopUp);
            btn_topup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickcount[0] = clickcount[0] + 1;
//                    Bundle getCheckTopup = getActivity().getIntent().getExtras();
//                    if (getCheckTopup != null && getCheckTopup.getString("inputthan5times").equals("inputthan5times")) {
//                        TextView txtMsg = (TextView) getView().findViewById(R.id.txtMessageTopup);
//                        TextView txtTitle = (TextView) getView().findViewById(R.id.txtTileTopUp);
//                        txtTitle.setText("THÔNG BÁO");
//                        txtMsg.setText("Bạn đã nhập sai mã thẻ cào quá 5 lần \n Chức năng nạp bằng thẻ tạm thời bị khoá ");
//                    } else
                    if (clickcount[0] < 5 ) {
                        EditText edtCoupon = (EditText) getView().findViewById(R.id.edtFMCoupon);
                        final String coupon = edtCoupon.getText().toString().trim();
                        Log.d("truongne", "coupon " + coupon);

                        if (coupon != null && coupon != "") {
                            Bundle bundle = getArguments();
//                    String cardId = bundle.getString("cardId","null");
                            final String[] params = {cardId, coupon};
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
                    } else {
                        TextView txtMsg = (TextView) getView().findViewById(R.id.txtMessageTopup);
                        TextView txtTitle = (TextView) getView().findViewById(R.id.txtTileTopUp);
                        txtTitle.setText("THÔNG BÁO");
                        txtMsg.setText("Bạn đã nhập sai mã thẻ cào quá 5 lần \n Chức năng nạp bằng thẻ tạm thời bị khoá ");
                        clickcount[0] = 5;
//                        Bundle checkTopup = new Bundle();
//                        checkTopup.putString("inputthan5times", "inputthan5times");
//                        Intent intentCheckTopup = getActivity().getIntent();
//                        intentCheckTopup.putExtras(checkTopup);
                    }

                }
            });
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("tabvoichahost", "onResume " + "TabHostTopup");
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
                                        intent.putExtra("TabHostTopUP", "TabHostTopUP");
                                        startActivity(intent);
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
//                \nXin vui lòng nhập mã khác
                TextView txtMsg = (TextView) getView().findViewById(R.id.txtMessageTopup);
                TextView txtTitle = (TextView) getView().findViewById(R.id.txtTileTopUp);
                txtTitle.setText("THÔNG BÁO");
                txtMsg.setText(message + "\nXin vui lòng nhập mã khác");
            }
        }
    }
}
