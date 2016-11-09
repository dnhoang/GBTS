package com.example.gbts.navigationdraweractivity.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.MainActivity;
import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.activity.TopUpActivity;
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




        View view = inflater.inflate(R.layout.fragment_choose_card, container, false);
        String sCardName = getActivity().getSharedPreferences("Info", Context.MODE_PRIVATE)
                .getString("cCardName", "");
        String sBalance = getActivity().getSharedPreferences("Info", Context.MODE_PRIVATE)
                .getString("cBalance", "");
        String sStatus = getActivity().getSharedPreferences("Info", Context.MODE_PRIVATE)
                .getString("cStatus", "");
        TextView textCardName = (TextView) view.findViewById(R.id.txtChooseCardName);
        TextView textBalance = (TextView) view.findViewById(R.id.txtChooseCDBalance);
        TextView textStatus = (TextView) view.findViewById(R.id.txtChooseStatus);

        textCardName.setText(sCardName);
        textBalance.setText(sBalance);
        textStatus.setText(sStatus);

        return view;
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
            url = Constance.API_GETALLCARD + "&phone=" + phone;
            // Getting JSON from URL
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

                                                double balance = card.getBalance();
                                                int status = card.getStatus();
                                                String strStatus = "";
                                                String cardName = card.getCardName();
                                                String strBalance = defaultFormat.format(balance);
                                                if (status == 1) {
                                                    strStatus = "Đã kích hoạt";
                                                } else {
                                                    strStatus = "Chưa kích hoạt";
                                                }
                                                if (cardid == card.getCardID()) {
                                                    sharedPreferences.edit()
                                                            .putString("cCardName", cardName)
                                                            .putString("cBalance", strBalance)
                                                            .putString("cStatus", strStatus)
                                                            .commit();
                                                }
                                                String sCardName = getActivity().getSharedPreferences("Info", Context.MODE_PRIVATE)
                                                        .getString("cCardName", "");
                                                String sBalance = getActivity().getSharedPreferences("Info", Context.MODE_PRIVATE)
                                                        .getString("cBalance", "");
                                                String sStatus = getActivity().getSharedPreferences("Info", Context.MODE_PRIVATE)
                                                        .getString("cStatus", "");
                                                TextView textCardName = (TextView) getView().findViewById(R.id.txtChooseCardName);
                                                TextView textBalance = (TextView) getView().findViewById(R.id.txtChooseCDBalance);
                                                TextView textStatus = (TextView) getView().findViewById(R.id.txtChooseStatus);

                                                textCardName.setText(sCardName);
                                                textBalance.setText(sBalance);
                                                textStatus.setText(sStatus);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
