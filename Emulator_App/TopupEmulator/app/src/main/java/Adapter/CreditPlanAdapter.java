package Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.topupemulator.R;

import java.util.Collections;
import java.util.List;

import Entity.CreditPlan;

/**
 * Created by truon on 10/7/2016.
 */

public class CreditPlanAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] name;
    private final String[] price;


    public CreditPlanAdapter(Activity context,
                      String[] name, String[] price) {
        super(context, R.layout.custom_listview_credit_plan, name);
        this.context = context;
        this.name = name;
        this.price = price;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.custom_listview_credit_plan, null, true);
        TextView tvTitle = (TextView) rowView.findViewById(R.id.tvCreditPlanName);
        TextView tvPrice = (TextView) rowView.findViewById(R.id.tvCreditPlanPrice);
        //Log.d("DUC!!!",name[position]);
        tvTitle.setText(name[position]);
        tvPrice.setText(price[position]+"đ");

        return rowView;
    }
}


