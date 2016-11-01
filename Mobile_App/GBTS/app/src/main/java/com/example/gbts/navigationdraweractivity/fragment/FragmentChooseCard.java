package com.example.gbts.navigationdraweractivity.fragment;

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
import android.widget.ListView;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.MainActivity;
import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.activity.TopUpActivity;
import com.example.gbts.navigationdraweractivity.adapter.ChooseCardNFCAdapter;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.enity.CardNFC;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        new JSONParseCardNFC().execute();
        return inflater.inflate(R.layout.fragment_choose_card, container, false);
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
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CardNFC card = listCard.get(position);
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Info", Context.MODE_PRIVATE);
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
                                                dialog.cancel();
                                            }
                                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
