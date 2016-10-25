package com.example.gbts.navigationdraweractivity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.gbts.navigationdraweractivity.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by truon on 10/20/2016.
 */

public class PlacesAutoCompleteAdapter extends ArrayAdapter<PlacesAutoCompleteAdapter>
        implements Filterable {

    List<PlacesAutoCompleteAdapter> listPlaces = null;

    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, 0, textViewResourceId);

    }

    @Override
    public Filter getFilter() {

        return null;
    }



}
