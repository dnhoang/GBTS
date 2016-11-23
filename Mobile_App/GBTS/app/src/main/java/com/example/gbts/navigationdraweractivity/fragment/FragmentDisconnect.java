package com.example.gbts.navigationdraweractivity.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.gbts.navigationdraweractivity.R;

/**
 * Created by truon on 11/17/2016.
 */

public class FragmentDisconnect extends Fragment {
    private static final String TAG_FRAGMENT = "FragmentDisconnect";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disconnect, container, false);
        final Bundle bundle = getArguments();
        Button btnTryAgain = (Button) view.findViewById(R.id.btn_tryAgain);
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bundle != null) {
                    if (bundle.getString("action").equals("transferCreditCard")) {
                        CreditCard creditCard = new CreditCard();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.flContent, creditCard, TAG_FRAGMENT)
                                .addToBackStack(null)
                                .commit();
                    } else if (bundle.getString("action").equals("transferMainContent")) {
                        MainContent mainContent = new MainContent();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.flContent, mainContent, TAG_FRAGMENT)
                                .addToBackStack(null)
                                .commit();
                    } else if (bundle.getString("action").equals("transferGetReport")) {
                        GetReport getReport = new GetReport();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.flContent, getReport, TAG_FRAGMENT)
                                .addToBackStack(null)
                                .commit();
                    } else if (bundle.getString("action").equals("transferProfile")) {
                        Profile profile = new Profile();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.flContent, profile, TAG_FRAGMENT)
                                .addToBackStack(null)
                                .commit();
                    } else if (bundle.getString("action").equals("transferFragmentChooseCard")) {
                        FragmentChooseCard chooseCard = new FragmentChooseCard();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.flContent, chooseCard, TAG_FRAGMENT)
                                .addToBackStack(null)
                                .commit();
                    }
//                    else if (bundle.getString("action").equals("GetAllBusRoute")) {
//                        GetAllBusRoute getAllBusRoute = new GetAllBusRoute();
//                        FragmentManager manager =getActivity().getFragmentManager();
//                        getAllBusRoute.show(manager,"GetAllBusRoute");
//                    }
                } else {

                }
            }
        });
        return view;
    }
}
