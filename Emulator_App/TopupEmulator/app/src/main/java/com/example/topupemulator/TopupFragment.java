package com.example.topupemulator;

/**
 * Created by ducdmse61486 on 10/23/2016.
 */
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import Adapter.CreditPlanAdapter;

public class TopupFragment extends Fragment {
    ListView list;
    int count;
    int[] id;
    String[] name;
    String[] price;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_topup, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        Bundle bundle=getArguments();
        count=bundle.getInt("count");
        id=bundle.getIntArray("id");
        name=bundle.getStringArray("name");
        price=bundle.getStringArray("price");
        Log.d("DUC ",price[1]+name[1]);
        CreditPlanAdapter adapter = new
                CreditPlanAdapter(getActivity(), name,price);
        if (adapter.isEmpty()) Log.d("DUC","NULL");
        list=(ListView)rootView.findViewById(R.id.listToptup);
        if (list!=null) Log.d("DUC","List view khac null");
        list.setAdapter(adapter);
        return inflater.inflate(R.layout.fragment_topup, container, false);
    }

}
