package com.example.gbts.navigationdraweractivity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.enity.AllBusroutes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by truon on 10/9/2016.
 */

public class AllBusroutesAdapter extends ArrayAdapter<AllBusroutes> {
    Context context;
    List<AllBusroutes> allBusroutesList;
    private Filter filter;

    TextView txtBusrouteId, txtBusCode, txtBusName;

    public AllBusroutesAdapter(Context context, List<AllBusroutes> allBusroutesList) {
        super(context, 0, allBusroutesList);
        this.context = context;
        this.allBusroutesList = allBusroutesList;
    }

    public int getCount() {
        return allBusroutesList.size();
    }

    public AllBusroutes getItem(int position) {
        return allBusroutesList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("ConvertView", String.valueOf(position));
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_listview_getbusroute, null);
        }
        txtBusrouteId = (TextView) convertView.findViewById(R.id.txtBusrouteId);
        txtBusCode = (TextView) convertView.findViewById(R.id.txtBusCode);
        txtBusName = (TextView) convertView.findViewById(R.id.txtBusName);

        AllBusroutes allBusroutes = allBusroutesList.get(position);

        txtBusrouteId.setText(allBusroutes.getBusCode() + "");
        txtBusCode.setText("Tuyến số " + allBusroutes.getBusCode());
        txtBusName.setText(allBusroutes.getBusName());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new AppFilter<>(allBusroutesList);
        return filter;
    }

    /**
     * Class for filtering in Arraylist listview. Objects need a valid
     * 'toString()' method.
     *
     * @author Tobias Schürg inspired by Alxandr
     *         (http://stackoverflow.com/a/2726348/570168)
     */
    private class AppFilter<T> extends Filter {

        private ArrayList<T> sourceObjects;

        public AppFilter(List<T> objects) {
            sourceObjects = new ArrayList<T>();
            synchronized (this) {
                sourceObjects.addAll(objects);
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence chars) {
            String filterSeq = chars.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (filterSeq != null && filterSeq.length() > 0) {
                ArrayList<T> filter = new ArrayList<T>();

                for (T object : sourceObjects) {

                    // the filtering itself:
                    if (object.toString().toLowerCase().contains(filterSeq))
                        filter.add(object);
                }
                result.count = filter.size();
                result.values = filter;
            } else {
                // add all objects
                synchronized (this) {
                    result.values = sourceObjects;
                    result.count = sourceObjects.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            // NOTE: this function is *always* called from the UI thread.
            ArrayList<T> filtered = (ArrayList<T>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = filtered.size(); i < l; i++)
                add((AllBusroutes) filtered.get(i));
            notifyDataSetInvalidated();
        }
    }
}
