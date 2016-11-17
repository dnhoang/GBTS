package com.example.gbts.navigationdraweractivity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
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
    TextView txtrpCardName, txtrpTotal, txtrpCountTicket, txtrpFrequently;

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
            convertView = mInflater.inflate(R.layout.custom_listview_report, null);
        }
        txtrpCardName = (TextView) convertView.findViewById(R.id.txtrpCardName);
        txtrpCountTicket = (TextView) convertView.findViewById(R.id.txtrpCountTicket);
        txtrpTotal = (TextView) convertView.findViewById(R.id.txtrpTotal);
        txtrpFrequently = (TextView) convertView.findViewById(R.id.txtrpFrequently);

        ReportEntity entity = getItem(position);
//
        txtrpCardName.setText(entity.getRpCardName());
        txtrpCountTicket.setText(entity.getRpCount() + " vé");
        txtrpTotal.setText(entity.getRpTotal());

        List<String> listRoutes = entity.getRpFrequently();
        StringBuilder builder = new StringBuilder();
        for (String list : listRoutes) {
            if (builder.length() > 0) {
                builder.append("  "); // some divider between the different texts
            }
            builder.append(list);
//            txtrpFrequently.setBackgroundResource(R.drawable.shap_circle_online);
        }
        txtrpFrequently.setText(builder.toString());


        return convertView;
    }
}
