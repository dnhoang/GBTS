package com.example.gbts.navigationdraweractivity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gbts.navigationdraweractivity.R;

/**
 * Created by truon on 9/26/2016.
 */

public class DataAdapter extends BaseAdapter {
    Context mContext;
    private String[] stt = {"S001", "S002", "S003", "S004", "S005"};
    private String[] busroute = {"Bến Thành - Thủ Đức", "An Sương - Bến Xe Miền Tây",
            "Suối Tiên - Quận 8", "An Dương Vương - ĐH Nông Lâm","ĐH Quốc Gia - ĐH Nông Lâm"};
    private String[] price = {"5000 đ", "7000 đ", "5000 đ", "5000 đ", "7000 đ"};
    private LayoutInflater mInflater;

    public DataAdapter(Context c) {
        mContext = c;
        mInflater = LayoutInflater.from(c);
    }

    public int getCount() {
        return stt.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.customgrid,
                    parent, false);
            System.out.println("ADAPTER convertView " + convertView);
            holder = new ViewHolder();
            holder.txtSTT = (TextView) convertView.findViewById(R.id.txtSTT);
            holder.txtSTT.setPadding(20, 10,10 , 10);
            System.out.println("ADAPTER txtSTT " + holder.txtSTT);
            holder.txtBusRoute = (TextView) convertView.findViewById(R.id.txtBusroute);
            holder.txtBusRoute.setPadding(20, 10,10 , 10);
            System.out.println("ADAPTER txtBusRoute " + holder.txtBusRoute);
//            holder.txtPrice = (TextView) convertView.findViewById(R.id.txtPrice);
//            holder.txtPrice.setPadding(20, 10,10 , 10);
            System.out.println("ADAPTER txtPrice " + holder.txtPrice);
            if (position == 0) {
                convertView.setTag(holder);
            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtSTT.setText(stt[position]);
        holder.txtBusRoute.setText(busroute[position]);
        holder.txtPrice.setText(price[position]);
        return convertView;
    }

    static class ViewHolder {
        TextView txtSTT;
        TextView txtBusRoute;
        TextView txtPrice;
    }
}
