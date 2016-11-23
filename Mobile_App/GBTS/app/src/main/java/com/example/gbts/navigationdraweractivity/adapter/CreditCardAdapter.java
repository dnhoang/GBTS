package com.example.gbts.navigationdraweractivity.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.enity.CardNFC;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by truon on 11/18/2016.
 */

public class CreditCardAdapter extends ArrayAdapter<CardNFC> {
    Context context;
    List<CardNFC> cardNFCList;


    public CreditCardAdapter(Context context, List<CardNFC> cardNFCList) {
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
            convertView.setLayoutParams(new ListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 280));
            convertView.setBackgroundResource(R.drawable.card_bg_2);
        }

        //Format number
        Locale locale = new Locale("vi_VN", "VN");
        Log.d("locale ", locale + "");
        NumberFormat defaultFormat = NumberFormat.getCurrencyInstance(locale);

        CardNFC cardNFC = getItem(position);

        TextView textCardName = (TextView) convertView.findViewById(R.id.txtCDCardName);
        TextView textBalance = (TextView) convertView.findViewById(R.id.txtCDBalance);
        TextView textStatus = (TextView) convertView.findViewById(R.id.txtCDStatus);
        TextView textStatusName = (TextView) convertView.findViewById(R.id.txtCDStatusName);


        double balance = cardNFC.getBalance();

        textCardName.setText(cardNFC.getCardName());
        textBalance.setText(defaultFormat.format(balance));
        int status = cardNFC.getStatus();
        if (status == 1) {
            textStatusName.setText("Đã kích hoạt");
            textStatus.setBackgroundResource(R.drawable.shap_circle_online);
        } else {
            textStatus.setBackgroundResource(R.drawable.shap_circle_offline);
            textStatusName.setText("Chưa kích hoạt");
        }
        return convertView;
    }
}
