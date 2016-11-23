package com.example.gbts.navigationdraweractivity.fragment;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.MainActivity;
import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.adapter.ChooseCardNFCAdapter;
import com.example.gbts.navigationdraweractivity.adapter.CreditCardAdapter;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.enity.CardNFC;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.example.gbts.navigationdraweractivity.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

;

/**
 * Created by truon on 9/22/2016.
 */

public class CreditCard extends Fragment {
    View view;
    ListView listView;
    private static final String TAG_CARD_ID = "CardId";
    private static final String TAG_CARD_NAME = "CardName";
    private static final String TAG_REGISTRATION_DATE = "RegistrationDate";
    private static final String TAG_BALANCE = "Balance";
    private static final String TAG_CARD_STATUS = "Status";
    private static final String TAG_FRAGMENT = "CreditCard";
    JSONArray jsonArray = null;
    List<CardNFC> listCardNFC = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_card, container, false);
        if (Utility.isNetworkConnected(getActivity())) {
            new JSONParseCardNFC().execute();
        } else {
            FragmentDisconnect disconnect = new FragmentDisconnect();
            Bundle bundle = new Bundle();
            bundle.putString("action", "transferCreditCardDetails");
            disconnect.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flContent, disconnect, TAG_FRAGMENT)
                    .addToBackStack(null)
                    .commit();
        }

        Bundle bundleSend = new Bundle();
        bundleSend.putString("currentContext", "CreditCard");
        Intent intent = getActivity().getIntent();
        intent.putExtras(bundleSend);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("truongneee", "onResume");
        Bundle bundleResume = getArguments();
        if (bundleResume != null) {
            String checkBundle = bundleResume.getString("onResumeCreditCard");
            if (checkBundle != null && checkBundle != "") {
                if (checkBundle.equals("CreditCard")) {
                    if (Utility.isNetworkConnected(getActivity())) {
                        new JSONParseCardNFC().execute();
                    } else {
                        FragmentDisconnect disconnect = new FragmentDisconnect();
                        Bundle bundle = new Bundle();
                        bundle.putString("action", "transferCreditCardDetails");
                        disconnect.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.flContent, disconnect, TAG_FRAGMENT)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            } else {
                //
                Log.d("bunble", "not existed");
            }
        }

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

                    NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();

                    String cardID = object.optString(TAG_CARD_ID);
                    String name = object.optString(TAG_CARD_NAME);
                    String registrationDate = object.optString(TAG_REGISTRATION_DATE);
                    double balance = object.optDouble(TAG_BALANCE);
                    String strBalance = defaultFormat.format(balance);
                    int status = object.optInt(TAG_CARD_STATUS);

                    String strStatus = "";
                    if (status == 0) {
                        strStatus = "Chưa kích hoạt";
                    } else if (status == 1) {
                        strStatus = "Ðã kích hoạt";
                    } else {
                        strStatus = "Thẻ khoá";
                    }

                    CardNFC cardNFC = new CardNFC();
                    cardNFC.setCardID(cardID);
                    cardNFC.setCardName(name);
                    cardNFC.setRegistrationDate(registrationDate);
                    cardNFC.setBalance(balance);
                    cardNFC.setStatus(status);
                    listCardNFC.add(cardNFC);
                }
                if (getActivity() != null) {
                    CreditCardAdapter creditCardAdapter = new CreditCardAdapter(getActivity(), listCardNFC);
                    listView = (ListView) getView().findViewById(R.id.listViewCard);
                    listView.setAdapter(creditCardAdapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            CardNFC cardNFC = listCardNFC.get(position);
                            Bundle bundle = new Bundle();
                            bundle.putString(TAG_CARD_ID, cardNFC.getCardID());
                            Intent intent = getActivity().getIntent();
                            intent.putExtras(bundle);


                            CreditCardDetails creditCardDetails = new CreditCardDetails();
                            creditCardDetails.setArguments(bundle);

                            final FragmentManager manager = getFragmentManager();
                            creditCardDetails.show(manager, "Details Account");
                        }
                    });
                }

//                    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                        @Override
//                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                            Toast.makeText(getActivity(), "Long Touch item", Toast.LENGTH_LONG).show();
//                            return false;
//                        }
//                    });


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}



