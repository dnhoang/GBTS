package com.example.gbts.navigationdraweractivity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gbts.navigationdraweractivity.R;

/**
 * Created by truon on 9/22/2016.
 */

public class ReportFinance extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_finance, container, false);
        return view;
    }
}
