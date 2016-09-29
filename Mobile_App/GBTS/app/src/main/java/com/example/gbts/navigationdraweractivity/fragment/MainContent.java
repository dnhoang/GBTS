package com.example.gbts.navigationdraweractivity.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.adapter.DataAdapter;

/**
 * Created by truon on 9/23/2016.
 */

public class MainContent extends Fragment {
    private static final String RESULT_OK = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_content, container, false);

        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(new DataAdapter(getActivity()));
        return view;
    }
}
