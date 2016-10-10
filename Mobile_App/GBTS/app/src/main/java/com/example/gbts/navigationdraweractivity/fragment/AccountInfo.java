package com.example.gbts.navigationdraweractivity.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.adapter.CardNFCDetailAdapter;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.enity.CardNFC;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by truon on 9/22/2016.
 */

public class AccountInfo extends Fragment {
    View view;
    ListView listView;
    private static final String TAG_CARD_ID = "CardId";
    private static final String TAG_CARD_NAME = "CardName";
    private static final String TAG_REGISTRATION_DATE = "RegistrationDate";
    private static final String TAG_BALANCE = "Balance";
    private static final String TAG_CARD_STATUS = "Status";
    ArrayList<HashMap<String, String>> listCard = new ArrayList<>();
    JSONArray jsonArray = null;

    CardNFCDetailAdapter cardNFCDetailAdapter;
    List<CardNFC> cardNFCList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_info, container, false);
        new JSONParseCardNFC().execute();

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
            JSONObject json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                // Getting JSON Array from URL
                jsonArray = jsonObject.getJSONArray("data");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject object = jsonArray.getJSONObject(i);
                    // Storing JSON item in a Variable

                    String cardID = object.optString(TAG_CARD_ID);
                    String name = object.optString(TAG_CARD_NAME);
                    String registrationDate = object.optString(TAG_REGISTRATION_DATE);
                    double balance = object.optDouble(TAG_BALANCE);
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
                    map.put(TAG_BALANCE, balance + "");
                    map.put(TAG_CARD_STATUS, strStatus);
                    listCard.add(map);
                    Log.d("CreditCardtq listCard2", listCard.size() + "");
                }
                ListAdapter adapter = new SimpleAdapter(getActivity(), listCard,
                        R.layout.custom_listview_cardnfc,
                        new String[]{TAG_CARD_NAME, TAG_CARD_STATUS}, new int[]{R.id.txtCardName, R.id.txtStatus});
                listView = (ListView) getView().findViewById(R.id.listViewCard);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        HashMap<String, String> card = listCard.get(position);
                        Log.d("TRUONGTQ map ", card + "");
                        final FragmentManager manager = getFragmentManager();
                        Bundle bundle = new Bundle();
                        bundle.putString(TAG_CARD_ID, card.get(TAG_CARD_ID));
                        bundle.putString(TAG_CARD_NAME, card.get(TAG_CARD_NAME));
                        bundle.putString(TAG_REGISTRATION_DATE, card.get(TAG_REGISTRATION_DATE));
                        bundle.putString(TAG_BALANCE, card.get(TAG_BALANCE));
                        bundle.putString(TAG_CARD_STATUS, card.get(TAG_CARD_STATUS));

                        CreditCardDetails creditCardDetails = new CreditCardDetails();

                        creditCardDetails.setArguments(bundle);

                        // detailsInfo.show(manager, "Details Account");
                        Toast.makeText(getActivity(), "You click at " + listCard.get(position).get(TAG_CARD_NAME), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}