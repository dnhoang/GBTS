package com.example.gbts.navigationdraweractivity.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gbts.navigationdraweractivity.R;

/**
 * Created by truon on 9/23/2016.
 */

public class MainContent extends Fragment {
    private static final String RESULT_OK = null;
    TextView txtNoti;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_content, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        txtNoti = (TextView) getView().findViewById(R.id.txtNotification);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Log.d("notificationq ", "not null");
            String body = bundle.getString("notiBody");
            String title = bundle.getString("notiTitle");
            if (body != null) {
                txtNoti.setText(body);
                txtNoti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CreditCard creditCard = new CreditCard();
                        getFragmentManager().beginTransaction()
                                .replace(R.id.flContent, creditCard, null)
                                .addToBackStack(null)
                                .commit();
                    }
                });
            } else {
                txtNoti.setText("");
            }
        } else {
            txtNoti.setText("");
            Log.d("notificationq ", "null cmnr");
        }
    }
}
