package com.example.gbts.navigationdraweractivity.fragment;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.activity.BusStopActivity;
import com.example.gbts.navigationdraweractivity.adapter.AllBusroutesAdapter;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.enity.AllBusroutes;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_all_busroutes, container, false);
        new ASYNCGetBusroute().execute();
        return view;
    }

    private class ASYNCGetBusroute extends AsyncTask<String, Void, JSONObject> {
        String url;

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jsonParser = new JSONParser();
            url = Constance.API_GET_BUSROUTE;
            JSONObject json = jsonParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            //instance new Arraylist<Allbusroute>
            busroutesList = new ArrayList<>();

            try {

                JSONArray jsonArray = jsonObject.getJSONArray("data");
                Log.d("GetAllbusroute ", "url " + url);
                Log.d("GetAllbusroute ", "jsonArray " + jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);

                    busroutes = new AllBusroutes();
                    int busID = json.optInt("Id");
                    String busCode = json.optString("Code");
                    String busName = json.optString("Name");
                    Log.d("GetAllbusroute ", "busID " + busID);
                    Log.d("GetAllbusroute ", "busCode " + busCode);
                    Log.d("GetAllbusroute ", "busName " + busName);

                    busroutes.setBusID(busID);
                    busroutes.setBusCode(busCode);
                    busroutes.setBusName(busName);

                    busroutesList.add(busroutes);
                }
                Log.d("GetAllbusroute ", "busroutesList " + busroutesList.toString());

                listView = (ListView) getView().findViewById(R.id.listView_getBusroute);
                allBusroutesAdapter = new AllBusroutesAdapter(getActivity(), busroutesList);
                listView.setAdapter(allBusroutesAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        busroutes = busroutesList.get(position);
                        // ID is tuyen duong
                        String routeCode = busroutes.getBusCode();
                        Intent intent = new Intent(getActivity(), BusStopActivity.class);
                        intent.putExtra("routeCode", routeCode);
                        startActivity(intent);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
