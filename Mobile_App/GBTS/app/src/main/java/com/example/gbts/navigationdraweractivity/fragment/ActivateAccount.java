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
 * Created by truon on 10/5/2016.
 */

public class ActivateAccount extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activate_account, container, false);
    }
}
