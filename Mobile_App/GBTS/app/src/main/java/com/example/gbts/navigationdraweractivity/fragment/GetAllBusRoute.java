package com.example.gbts.navigationdraweractivity.fragment;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.FragmentHostCallback;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gbts.navigationdraweractivity.MainActivity;
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

public class GetAllBusRoute extends DialogFragment {
    ListView listView;
    List<AllBusroutes> busroutesList;
    AllBusroutes busroutes;
    AllBusroutesAdapter allBusroutesAdapter;
    EditText edtFillterBusStop;
    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_all_busroutes, container, false);
        if (Utility.isNetworkConnected(getActivity())) {
            new ASYNCGetBusroute().execute();
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
                        new ASYNCGetBusroute().execute();
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

        edtFillterBusStop = (EditText) view.findViewById(R.id.edtFilterBusStop);
        edtFillterBusStop.addTextChangedListener(filterTextWatcher);

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
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

            if (jsonObject != null) {
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
                    if (busroutesList != null) {
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
                                    if (routeCode != "" && routeName != "") {
                                        Intent intent = new Intent(getActivity(), BusStopActivity.class);
                                        intent.putExtra("routeCode", routeCode);
                                        intent.putExtra("routeName", routeName);
                                        startActivity(intent);
                                    } else {

                                    }
                                }
                            });
                        } else {
                            listView.setAdapter(null);
                        }
                    }else {
                        // set ko co du

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("filter", "json object is null");
            }
        }
    }
}
