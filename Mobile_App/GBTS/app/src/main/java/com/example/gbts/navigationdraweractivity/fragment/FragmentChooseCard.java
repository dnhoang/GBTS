package com.example.gbts.navigationdraweractivity.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.adapter.ChooseCardNFCAdapter;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.enity.CardNFC;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.example.gbts.navigationdraweractivity.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by truon on 11/1/2016.
 */

public class FragmentChooseCard extends Fragment {
    View view;
    ListView listView;
    private static final String TAG_CARD_ID = "CardId";
    private static final String TAG_CARD_NAME = "CardName";
    private static final String TAG_REGISTRATION_DATE = "RegistrationDate";
    private static final String TAG_BALANCE = "Balance";
    private static final String TAG_CARD_STATUS = "Status";
    List<CardNFC> listCard = new ArrayList<>();
    ChooseCardNFCAdapter chooseCardNFCAdapter;
    JSONArray jsonArray = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_card, container, false);
        if (Utility.isNetworkConnected(getActivity())) {
            new JSONParseCardNFC().execute();
        } else {
            // custom dialog
            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.custom_dialog);
            dialog.setTitle("Mất kết nối mạng ...");

            // set the custom dialog components - text, image and button
            TextView text = (TextView) dialog.findViewById(R.id.text);
            text.setText("Kiểm tra mạng wifi hoặc 3g");
            ImageView image = (ImageView) dialog.findViewById(R.id.image);
            image.setImageResource(R.drawable.ic_icon_wifi);

            Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utility.isNetworkConnected(getActivity())) {
                        dialog.dismiss();
                        new JSONParseCardNFC().execute();
                    }
                }
            });
            dialog.show();
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private class JSONParseCardNFC extends AsyncTask<String, String, JSONObject> {
        String phone;
        String url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences preferences = getActivity().getSharedPreferences("Info", Context.MODE_PRIVATE);
            phone = preferences.getString("Phonenumber", "empty");
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONParser jParser = new JSONParser();
            url = Constance.API_GET_ALL_CARD + "&phone=" + phone;
            // Getting JSON from URL
            Log.d("truongURL", "JSONParseCardNFC: " + url);
            JSONObject json = jParser.getJSONFromUrlPOST(url);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                // Getting JSON Array from URL
                int checkPurchase;
                jsonArray = jsonObject.getJSONArray("data");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject object = jsonArray.getJSONObject(i);
                    // Storing JSON item in a Variable


                    String cardID = object.optString(TAG_CARD_ID);
                    String name = object.optString(TAG_CARD_NAME);
                    String registrationDate = object.optString(TAG_REGISTRATION_DATE);
                    double balance = object.optDouble(TAG_BALANCE);
                    int status = object.optInt(TAG_CARD_STATUS);

                    CardNFC cardNFC = new CardNFC();
                    cardNFC.setCardID(cardID);
                    cardNFC.setCardName(name);
                    cardNFC.setRegistrationDate(registrationDate);
                    cardNFC.setBalance(balance);
                    cardNFC.setStatus(status);
                    listCard.add(cardNFC);

                }
                chooseCardNFCAdapter = new ChooseCardNFCAdapter(getActivity(), listCard);
                listView = (ListView) getView().findViewById(R.id.listViewChooseCard);
                listView.setAdapter(chooseCardNFCAdapter);
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        final CardNFC card = listCard.get(position);
                        //Format number
                        final NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();
                        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Info", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putString("NFCPayment", card.getCardID()).commit();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder
                                .setTitle(card.getCardName() + "Đã được chọn")
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                String cardid = getActivity().getSharedPreferences("Info", Context.MODE_PRIVATE)
                                                        .getString("NFCPayment", "");

                                                Log.d("NFCPayment ", "NFCPayment " + cardid);
                                                new AsyncGetCardInfo().execute(cardid);

                                                dialog.cancel();
                                            }
                                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        return false;
                    }
                });

                final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Info", Context.MODE_PRIVATE);
                String checkCardID = sharedPreferences.getString("NFCPayment", "");
                if (checkCardID == "") {
                    TextView textCardName = (TextView) getView().findViewById(R.id.txtChooseCardName);
                    TextView textBalance = (TextView) getView().findViewById(R.id.txtChooseCDBalance);
                    TextView textStatus = (TextView) getView().findViewById(R.id.txtChooseStatus);
                    textCardName.setText("");
                    textBalance.setText("");
                    textStatus.setText("");

                    String choosenCardID = "";
                    for (CardNFC card : listCard) {
                        if (card.getStatus() == 1) {
                            choosenCardID = card.getCardID();
                            break;
                        }
                    }
                    sharedPreferences.edit().putString("NFCPayment", choosenCardID);
                    if (choosenCardID != null) {
                        new AsyncGetCardInfo().execute(choosenCardID);
                    }
                } else {
                    new AsyncGetCardInfo().execute(checkCardID);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            String urlGetCardInfo = Constance.API_GET_CARD_INFO + "&cardId=" + params[0];
            Log.d("truongURL", "AsyncGetCardInfo: " + urlGetCardInfo);
            JSONObject json = jsonParser.getJSONFromUrlGET(urlGetCardInfo);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            final NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();
            try {
                JSONObject object = jsonObject.getJSONObject("data");
                String cardID = object.getString("CardId");
                String cardName = object.getString("CardName");
                double balance = object.getDouble("Balance");
                String registrationDate = object.getString("RegistrationDate");
                int status = object.getInt("Status");
                String strStatus = "";
                String strBalance = defaultFormat.format(balance);
                if (status == 1) {
                    strStatus = "Đã kích hoạt";
                } else {
                    strStatus = "Chưa kích hoạt";
                }

                TextView textCardName = (TextView) getView().findViewById(R.id.txtChooseCardName);
                TextView textBalance = (TextView) getView().findViewById(R.id.txtChooseCDBalance);
                TextView textStatus = (TextView) getView().findViewById(R.id.txtChooseStatus);
                textCardName.setText(cardName);
                textBalance.setText(strBalance);
                textStatus.setText(strStatus);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}