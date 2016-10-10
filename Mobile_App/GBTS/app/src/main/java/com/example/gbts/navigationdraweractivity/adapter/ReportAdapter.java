package com.example.gbts.navigationdraweractivity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.enity.ReportEntity;

import java.util.List;

/**
 * Created by truon on 10/10/2016.
 */

public class ReportAdapter extends ArrayAdapter<ReportEntity> {
    LayoutInflater mInflater;
    Context context;
    List<ReportEntity> reportEntityList;
    TextView txtrpCardName, txtrpTotal, txtrpBoughtDated, txtrpBusCode;

    public ReportAdapter(Context context, List<ReportEntity> reportEntityList) {
        super(context, 0, reportEntityList);
        this.context = context;
        this.reportEntityList = reportEntityList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.custom_listview_get_report, null);
        }
        txtrpCardName = (TextView) convertView.findViewById(R.id.txtrpCardName);
        txtrpTotal = (TextView) convertView.findViewById(R.id.txtrpTotal);
        txtrpBoughtDated = (TextView) convertView.findViewById(R.id.txtrpBoughtDated);
        txtrpBusCode = (TextView) convertView.findViewById(R.id.txtrpBusCode);

        ReportEntity entity = getItem(position);

        txtrpCardName.setText(entity.getRpCardName());
        txtrpTotal.setText(entity.getRpTotal());
        txtrpBoughtDated.setText(entity.getRpBoughtDated());
        txtrpBusCode.setText(entity.getRpBusCode());


        return convertView;
    }
}
