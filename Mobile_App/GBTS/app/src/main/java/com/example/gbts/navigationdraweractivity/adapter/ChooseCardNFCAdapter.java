package com.example.gbts.navigationdraweractivity.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.enity.CardNFC;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by truon on 10/5/2016.
 */

public class ChooseCardNFCAdapter extends ArrayAdapter<CardNFC> {
    Context context;
    List<CardNFC> cardNFCList;


    public ChooseCardNFCAdapter(Context context, List<CardNFC> cardNFCList) {
        super(context, 0, cardNFCList);
        this.context = context;
        this.cardNFCList = cardNFCList;
    }


    @Override
    public int getCount() {
        if (cardNFCList != null)
            return cardNFCList.size();
        return 0;

    }

    @Override
    public CardNFC getItem(int position) {
        if (cardNFCList != null)
            return cardNFCList.get(position);
        return null;

    }

    @Override
    public long getItemId(int position) {
        if (cardNFCList != null)
            return cardNFCList.get(position).hashCode();
        return 0;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_listview_cardnfc, null);
        }

        //Format number
        Locale locale = new Locale("vi_VN", "VN");
        Log.d("locale ", locale + "");
        NumberFormat defaultFormat = NumberFormat.getCurrencyInstance(locale);

        CardNFC cardNFC = getItem(position);

        TextView textCardName = (TextView) convertView.findViewById(R.id.txtCardName);
        TextView textBalance = (TextView) convertView.findViewById(R.id.txtCDBalance);
        TextView textStatus = (TextView) convertView.findViewById(R.id.txtStatus);


        double balance = cardNFC.getBalance();

        textCardName.setText(cardNFC.getCardName());
        textBalance.setText(defaultFormat.format(balance));
        int status = cardNFC.getStatus();
        if (status == 1) {
//            textStatus.setText("Đã kích hoạt");
            textStatus.setBackgroundResource(R.drawable.shap_circle_online);
        } else {
            textStatus.setBackgroundResource(R.drawable.shap_circle_offline);
//            textStatus.setText("Chưa kích hoạt");
        }
        return convertView;
    }

}
