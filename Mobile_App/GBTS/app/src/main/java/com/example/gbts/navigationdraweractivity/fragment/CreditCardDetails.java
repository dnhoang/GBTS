package com.example.gbts.navigationdraweractivity.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.MainActivity;
import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.activity.CreditPlanActivity;
import com.example.gbts.navigationdraweractivity.activity.LoginActivity;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.enity.CardNFC;
import com.example.gbts.navigationdraweractivity.tabhost.TabHostCreditPlan;
import com.example.gbts.navigationdraweractivity.tabhost.TabHostFragment;
import com.example.gbts.navigationdraweractivity.tabhost.TabhostActivity;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.example.gbts.navigationdraweractivity.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by truon on 9/29/2016.
 */

public class CreditCardDetails extends DialogFragment
        implements View.OnClickListener {
    private static final String TAG = "CreditCardDetails";
    View view;
    private static final String TAG_CARD_ID = "CardId";
    private static final String TAG_CARD_NAME = "CardName";
    private static final String TAG_REGISTRATION_DATE = "RegistrationDate";
    private static final String TAG_BALANCE = "Balance";
    private static final String TAG_CARD_STATUS = "Status";
    private static final String TAG_FRAGMENT = "CreditCardDetails";

    HashMap<String, String> card = new HashMap<>();

    TextView txtCardId, txtBalance, txtRegistration, txtStatus, txtStatusName;
    EditText edtCardName;
    ImageView imgEditCardName;
    Button btnPurchase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contain_details, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            final String cardId = bundle.getString(TAG_CARD_ID);
            if (Utility.isNetworkConnected(getActivity())) {
                new AsyncGetCardInfo().execute(cardId);
            } else {
                // custom dialog
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.custom_dialog_login);
                dialog.setTitle("Mất kết nối mạng ...");

                Button dialogButton = (Button) dialog.findViewById(R.id.dialogBtnOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utility.isNetworkConnected(getActivity())) {
                            dialog.dismiss();
                            new AsyncGetCardInfo().execute(cardId);
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

        btnPurchase = (Button) view.findViewById(R.id.btnPurchase);
        btnPurchase.setOnClickListener(this);

        //EVENT CHANGE CARD NAME
        imgEditCardName = (ImageView) view.findViewById(R.id.imgEditCardViewDetails);
        imgEditCardName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String cardID = txtCardId.getText().toString();
                final String cardName = edtCardName.getText().toString().trim();

                Bundle bundleSend = new Bundle();
                bundleSend.putString("changeCardNameCallCreditCard", "changeCardNameCallCreditCard");
                Intent intent = getActivity().getIntent();
                intent.putExtras(bundleSend);

                if (Utility.isNetworkConnected(getActivity())) {
                    new AsyncChangeCardName().execute(cardID, cardName);
                } else {
                    // custom dialog
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.custom_dialog_login);
                    dialog.setTitle("Mất kết nối mạng ...");

                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogBtnOK);
                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Utility.isNetworkConnected(getActivity())) {
                                dialog.dismiss();
                                new AsyncChangeCardName().execute(cardID, cardName);
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
        });


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("CREDITDETAILS ", "onDestroy " + "onDestroy");
        CreditCard creditCard = new CreditCard();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent, creditCard, TAG)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onClick(View v) {
        txtStatusName = (TextView) getView().findViewById(R.id.txtDetailsStatusName);
        txtCardId = (TextView) getView().findViewById(R.id.txtCardIDDetails);
        String status = txtStatusName.getText().toString();
        final String cardID = txtCardId.getText().toString();

        if (status.equals("Đã kích hoạt")) {
            if (R.id.btnPurchase == v.getId()) {
//                final Intent intent = new Intent(getActivity(), CreditPlanActivity.class);
//                Bundle bundleCreditPlanActivity = new Bundle();
//                bundleCreditPlanActivity.putString("cardIDCreditDetails", cardID);
//                intent.putExtras(bundleCreditPlanActivity);
                Log.d("haizzz", "cardID " + cardID);
                Bundle bundle1 = new Bundle();
                bundle1.putString("cardIDCreditDetails", cardID);
//                TabHostCreditPlan tabHostCreditPlan = new TabHostCreditPlan();
//                tabHostCreditPlan.setArguments(bundle1);
//                Intent intent = getActivity().getIntent();
//                intent.putExtras(bundle1);
//                TabHostCreditPlan tabHostCreditPlan = new TabHostCreditPlan();
//                tabHostCreditPlan.setArguments(bundle1);

                final Intent intentTanhost = new Intent(getActivity(), TabhostActivity.class);
                intentTanhost.putExtras(bundle1);
//                final TabHostFragment tabHostFragment = new TabHostFragment();

                if (Utility.isNetworkConnected(getActivity())) {
//                    startActivity(intent);
                    startActivity(intentTanhost);
//                    getDialog().dismiss();
//                    getActivity().getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.flContent, tabHostFragment, TAG_FRAGMENT)
//                            .addToBackStack(null)
//                            .commit();
                } else {
                    // custom dialog
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.custom_dialog_login);
                    dialog.setTitle("Mất kết nối mạng ...");

                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogBtnOK);
                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Utility.isNetworkConnected(getActivity())) {
                                dialog.dismiss();
//                                startActivity(intent);
                                startActivity(intentTanhost);
//                                getDialog().dismiss();
//                                getActivity().getSupportFragmentManager().beginTransaction()
//                                        .replace(R.id.flContent, tabHostFragment, TAG_FRAGMENT)
//                                        .addToBackStack(null)
//                                        .commit();
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

            } else {
                // do the same for signInButton
            }
        } else {
            Toast.makeText(getActivity(), "Thẻ của bạn đã bị khoá xin vui lòng liên hệ với nhà cung cấp dịch vụ", Toast.LENGTH_SHORT).show();
//            btnPurchase.
        }
    }

    private class AsyncGetCardInfo extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jsonParser = new JSONParser();
            String url = Constance.API_GET_CARD_INFO + "&cardId=" + params[0];
            Log.d("CREDITDETAILS ", "url " + url);
            JSONObject json = jsonParser.getJSONFromUrlGET(url);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (jsonObject != null) {
                boolean success = jsonObject.optBoolean("success");
                if (success) {
                    try {
                        JSONObject object = jsonObject.getJSONObject("data");

                        Locale locale = new Locale("vi_VN", "VN");
                        NumberFormat defaultFormat = NumberFormat.getCurrencyInstance(locale);

                        String cardID = object.optString(TAG_CARD_ID);
                        String name = object.optString(TAG_CARD_NAME);
                        String registrationDate = object.optString(TAG_REGISTRATION_DATE);
                        double balance = object.optDouble(TAG_BALANCE);
                        String strBalance = defaultFormat.format(balance);
                        int status = object.optInt(TAG_CARD_STATUS);

                        txtCardId = (TextView) getView().findViewById(R.id.txtCardIDDetails);
                        txtBalance = (TextView) getView().findViewById(R.id.txtBalanceDetails);
                        txtRegistration = (TextView) getView().findViewById(R.id.txtRegistrationDateDetails);
                        txtStatus = (TextView) getView().findViewById(R.id.txtStatusDetails);
                        txtStatusName = (TextView) getView().findViewById(R.id.txtDetailsStatusName);
                        edtCardName = (EditText) getView().findViewById(R.id.edtCardNameDetails);
                        imgEditCardName = (ImageView) getView().findViewById(R.id.imgEditCardViewDetails);

                        txtCardId.setText(cardID);
                        edtCardName.setSingleLine(true);
                        edtCardName.setEllipsize(TextUtils.TruncateAt.END);
                        edtCardName.setText(name);
                        txtRegistration.setText(registrationDate);
                        txtBalance.setText(strBalance);
                        if (status == 1) {
                            txtStatus.setBackgroundResource(R.drawable.shap_circle_online);
                            txtStatusName.setText("Đã kích hoạt");

                        } else {
                            txtStatus.setBackgroundResource(R.drawable.shap_circle_offline);
                            txtStatusName.setText("Đã bị khoá");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String message = jsonObject.optString("message");
                    Log.d("CREDITDETAILS ", "changeaname " + message);
                }

            } else {
                Log.d("CREDITDETAILS ", "Json Object is Null ");
            }

        }
    }


    private class AsyncChangeCardName extends AsyncTask<String, Void, JSONObject> {
        String url;
        String cardName = edtCardName.getText().toString().trim();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jsonParser = new JSONParser();
            try {
                url = Constance.API_CHANGE_CARD_NAME + "&cardId=" + params[0] + "&name=" + URLEncoder.encode(params[1], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            JSONObject json = jsonParser.getJSONFromUrlPOST(url);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                Log.d("changeaname ", "url " + url);
                boolean success = jsonObject.optBoolean("success");
                Log.d("changeaname ", "success " + success);
                String message = jsonObject.optString("message");
                Log.d("changeaname ", "changeaname " + message);
                if (success == true) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder
                            .setTitle(message + "\n" + cardName)
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Bundle bundle = getArguments();
                                            if (bundle != null) {
                                                String cardId = bundle.getString(TAG_CARD_ID);
                                                new AsyncGetCardInfo().execute(cardId);

                                            } else {
                                                Log.d("AsyncGetCardInfo", "cardId is null");
                                            }
                                        }
                                    });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
