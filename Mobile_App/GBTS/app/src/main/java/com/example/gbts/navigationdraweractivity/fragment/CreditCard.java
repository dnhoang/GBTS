package com.example.gbts.navigationdraweractivity.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_card, container, false);
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
                    if (status == 1) {
                        strStatus = "Ðã kích hoạt";
                    } else {
                        strStatus = "Chưa kích hoạt";
                    }

                    // Adding value HashMap key => value
                    HashMap<String, String> map = new HashMap<>();
                    map.put(TAG_CARD_NAME, name);
                    map.put(TAG_CARD_STATUS, strStatus);
                    listCard.add(map);
                    Log.d("CreditCardtq listCard2", listCard.size() + "");

                    ListAdapter adapter = new SimpleAdapter(getActivity(), listCard,
                            R.layout.custom_listview_cardnfc,
                            new String[]{TAG_CARD_NAME, TAG_CARD_STATUS}, new int[]{R.id.txtCardName, R.id.txtStatus});
                    listView = (ListView) getView().findViewById(R.id.listViewCard);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                            final FragmentManager manager = getFragmentManager();
//                            final DetailsInfo detailsInfo = new DetailsInfo();
//                            detailsInfo.show(manager, "Details Account");


                            DetailsInfo detailsInfo = new DetailsInfo();
                            /** Creating a bundle object to store the position of the selected Card */
                            Bundle b = new Bundle();
                            /** Storing the position in the bundle object */
                            b.putInt("position", position);
                            detailsInfo.setArguments(b);
                            /** Getting FragmentManager object */
                            FragmentManager fragmentManager = getFragmentManager();

                            /** Starting a FragmentTransaction */
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            DetailsInfo tPrev = (DetailsInfo) fragmentManager.findFragmentById(R.layout.custom_detail_listview_card);
                            /** If the previously created fragment object still exists, then that has to be removed */
                            if (tPrev != null) {
                                fragmentTransaction.remove(tPrev);

                                /** Opening the fragment object */
                            }
                            detailsInfo.show(fragmentTransaction, "time_dialog");
//                            listCard.get(position).get(TAG_CARD_NAME);


//
//                            final FragmentManager manager = getFragmentManager();
//                            final List<DetailsInfo> detailsInfo = new ArrayList<>();
//                            detailsInfo.get(position).show(manager,"Empty item");

//                            Toast.makeText(getActivity(), "You click at " + listCard.get(position).get(TAG_CARD_NAME), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}



