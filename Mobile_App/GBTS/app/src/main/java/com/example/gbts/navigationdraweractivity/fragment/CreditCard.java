package com.example.gbts.navigationdraweractivity.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.gbts.navigationdraweractivity.R;

;

/**
 * Created by truon on 9/22/2016.
 */

public class CreditCard extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_card, container, false);

        final FragmentManager manager = getFragmentManager();
        final DetailsInfo detailsInfo = new DetailsInfo();

        TextView txtUIDCard = (TextView) view.findViewById(R.id.txtUIDCard);
        txtUIDCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View anchorView) {
                detailsInfo.show(manager,"Details Account");
            }
        });
        return view;
    }
}
