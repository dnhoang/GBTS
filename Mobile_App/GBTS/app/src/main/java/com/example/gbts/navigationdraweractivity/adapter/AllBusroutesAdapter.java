package com.example.gbts.navigationdraweractivity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.enity.AllBusroutes;

import java.util.List;

/**
 * Created by truon on 10/9/2016.
 */

public class AllBusroutesAdapter extends ArrayAdapter<AllBusroutes> {
    Context context;
    List<AllBusroutes> allBusroutesList;
    TextView txtBusrouteId, txtBusCode, txtBusName;

    public AllBusroutesAdapter(Context context, List<AllBusroutes> allBusroutesList) {
        super(context, 0, allBusroutesList);
        this.context = context;
        this.allBusroutesList = allBusroutesList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_listview_getbusroute, null);
        }
        txtBusrouteId = (TextView) convertView.findViewById(R.id.txtBusrouteId);
        txtBusCode = (TextView) convertView.findViewById(R.id.txtBusCode);
        txtBusName = (TextView) convertView.findViewById(R.id.txtBusName);

        AllBusroutes allBusroutes = getItem(position);
        Log.d("AllBusroutesAdapter", "allBusroutes " + allBusroutes.toString());
        Log.d("AllBusroutesAdapter", "txtBusrouteId " + allBusroutes.getBusID());

        txtBusrouteId.setText(allBusroutes.getBusID() + "");
        txtBusCode.setText("Tuyến số " + allBusroutes.getBusCode());
        txtBusName.setText(allBusroutes.getBusName());

        return convertView;
    }
}
