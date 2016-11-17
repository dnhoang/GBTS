package com.example.gbts.navigationdraweractivity.fragment;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
    ArrayList<HashMap<String, String>> listCard = new ArrayList<>();
    JSONArray jsonArray = null;
    List<CardNFC> listCardNFC = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_card, container, false);
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

                    // Adding value HashMap key => value
                    HashMap<String, String> map = new HashMap<>();
                    map.put(TAG_CARD_ID, cardID);
                    map.put(TAG_CARD_NAME, name);
                    map.put(TAG_REGISTRATION_DATE, registrationDate);
                    map.put(TAG_BALANCE, strBalance);
                    map.put(TAG_CARD_STATUS, strStatus);
                    listCard.add(map);

                    CardNFC cardNFC = new CardNFC();
                    cardNFC.setCardID(cardID);
                    cardNFC.setCardName(name);
                    cardNFC.setRegistrationDate(registrationDate);
                    cardNFC.setBalance(balance);
                    cardNFC.setStatus(status);
                    listCardNFC.add(cardNFC);
                }
                ListAdapter adapter = new SimpleAdapter(getActivity(), listCard,
                        R.layout.custom_listview_cardnfc,
                        new String[]{TAG_CARD_NAME, TAG_BALANCE, TAG_CARD_STATUS}, new int[]{R.id.txtCardName, R.id.txtCDBalance, R.id.txtStatus});

                ChooseCardNFCAdapter chooseCardNFCAdapter = new ChooseCardNFCAdapter(getActivity(), listCardNFC);
                listView = (ListView) getView().findViewById(R.id.listViewCard);
                listView.setAdapter(chooseCardNFCAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        HashMap<String, String> card = listCard.get(position);
                        CardNFC cardNFC = listCardNFC.get(position);
                        Bundle bundle = new Bundle();
                        bundle.putString(TAG_CARD_ID, cardNFC.getCardID());
                        bundle.putString(TAG_CARD_NAME, cardNFC.getCardName());
                        bundle.putString(TAG_REGISTRATION_DATE, cardNFC.getRegistrationDate());
                        bundle.putString(TAG_BALANCE, cardNFC.getBalance() + "");
                        bundle.putString(TAG_CARD_STATUS, cardNFC.getStatus() + "");

                        CreditCardDetails creditCardDetails = new CreditCardDetails();
                        creditCardDetails.setArguments(bundle);

                            final FragmentManager manager = getFragmentManager();
                            creditCardDetails.show(manager, "Details Account");
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}



