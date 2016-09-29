package com.example.gbts.navigationdraweractivity.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.gbts.navigationdraweractivity.R;

/**
 * Created by truon on 9/29/2016.
 */

public class DetailsInfo extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.detail_account, container, false);
        Button btnPurchase = (Button) view.findViewById(R.id.btnPurchase);
        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });
        return view;
    }
}
