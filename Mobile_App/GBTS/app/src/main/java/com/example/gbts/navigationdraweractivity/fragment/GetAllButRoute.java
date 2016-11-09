package com.example.gbts.navigationdraweractivity.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.activity.BusStopActivity;
import com.example.gbts.navigationdraweractivity.adapter.AllBusroutesAdapter;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.enity.AllBusroutes;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.example.gbts.navigationdraweractivity.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by truon on 10/9/2016.
 */

public class GetAllButRoute extends DialogFragment {
    ListView listView;
    List<AllBusroutes> busroutesList;
    AllBusroutes busroutes;
    AllBusroutesAdapter allBusroutesAdapter;
    EditText edtFillterBusStop;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_all_busroutes, container, false);

        if (Utility.isNetworkConnected(getActivity())) {
            new ASYNCGetBusroute().execute();
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
                        new ASYNCGetBusroute().execute();
                    }
                }
            });
            dialog.show();
        }


        edtFillterBusStop = (EditText) view.findViewById(R.id.edtFilterBusStop);
        edtFillterBusStop.addTextChangedListener(filterTextWatcher);

        return view;
    }

    /**
     * Filter for filtering items in the list.
     */
    private TextWatcher filterTextWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            Log.d("filter", "s " + s);
            if (allBusroutesAdapter != null) {
                allBusroutesAdapter.getFilter().filter(s);
            } else {
                Log.d("filter", "no filter availible");
            }
        }
    };

    private class ASYNCGetBusroute extends AsyncTask<String, Void, JSONObject> {
        String url;

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jsonParser = new JSONParser();
            url = Constance.API_GET_ALL_BUS_ROUTES;
            JSONObject json = jsonParser.getJSONFromUrlPOST(url);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            //instance new Arraylist<Allbusroute>
            busroutesList = new ArrayList<>();

            try {

                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    busroutes = new AllBusroutes();
                    JSONObject json = jsonArray.getJSONObject(i);

                    String busCode = json.optString("Code");
                    String busName = json.optString("Name");

                    busroutes.setBusCode(busCode);
                    busroutes.setBusName(busName);

                    busroutesList.add(busroutes);
                }

                allBusroutesAdapter = new AllBusroutesAdapter(getActivity(), busroutesList);

                listView = (ListView) getView().findViewById(R.id.listView_getBusroute);
                if (busroutesList != null) {

                    listView.setTextFilterEnabled(true);
                    listView.setAdapter(allBusroutesAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            busroutes = busroutesList.get(position);
                            // ID is tuyen duong
                            String routeCode = busroutes.getBusCode();
                            String routeName = busroutes.getBusName();
                            Intent intent = new Intent(getActivity(), BusStopActivity.class);
                            intent.putExtra("routeCode", routeCode);
                            intent.putExtra("routeName", routeName);
                            startActivity(intent);
                        }
                    });
                } else {
                    listView.setAdapter(null);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
