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
import com.example.gbts.navigationdraweractivity.enity.BusStop;

import java.util.List;

/**
 * Created by truon on 10/18/2016.
 */

public class GoStopAdapter extends ArrayAdapter<BusStop> {

    List<BusStop> busStopList;
    Context context;

    TextView txtBusStopName;

    public GoStopAdapter(Context context, List<BusStop> busStopList) {
        super(context, 0, busStopList);
        this.context = context;
        this.busStopList = busStopList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_listview_go_stop, null);
        }

        txtBusStopName = (TextView) convertView.findViewById(R.id.txtBusStopName);

        BusStop busStop = getItem(position);
        Log.d("GoStopAdapter", "busStop " + busStop.toString());
        Log.d("GoStopAdapter", "getStopId " + busStop.getStopId());
        txtBusStopName.setText("- " + busStop.getName());

        Log.d("GoStopAdapter", "txtBusStopName " + "-" + busStop.getName());

        return convertView;
    }
}
