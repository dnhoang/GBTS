package com.example.gbts.navigationdraweractivity.fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gbts.navigationdraweractivity.R;

/**
 * Created by truon on 9/30/2016.
 */

public class FragmentDirection extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_direction, container, false);
        return view;
    }
}
