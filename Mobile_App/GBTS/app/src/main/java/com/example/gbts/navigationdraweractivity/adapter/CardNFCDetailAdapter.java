package com.example.gbts.navigationdraweractivity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.enity.CardNFC;

import java.util.List;

/**
 * Created by truon on 10/6/2016.
 */

public class CardNFCDetailAdapter extends BaseAdapter {
    Context context;
    List<CardNFC> cardNFCList;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<CardNFC> getCardNFCList() {
        return cardNFCList;
    }

    public void setCardNFCList(List<CardNFC> cardNFCList) {
        this.cardNFCList = cardNFCList;
    }

    public CardNFCDetailAdapter(Context context, List<CardNFC> cardNFCList) {
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
        CardNFC cardNFC = getItem(position);

        TextView txtCardID = (TextView) convertView.findViewById(R.id.txtCardIDDetails);
        TextView textCardName = (TextView) convertView.findViewById(R.id.edtCardNameDetails);
        TextView txtRegistrationDate = (TextView) convertView.findViewById(R.id.txtRegistrationDateDetails);
        TextView txtBalance = (TextView) convertView.findViewById(R.id.txtBalanceDetails);
        TextView textStatus = (TextView) convertView.findViewById(R.id.txtDetailsStatusName);
        TextView textStatusName = (TextView) convertView.findViewById(R.id.txtDetailsStatusName);


        txtCardID.setText(cardNFC.getCardID());
        textCardName.setText(cardNFC.getCardName());
        txtRegistrationDate.setText(cardNFC.getRegistrationDate());
        txtBalance.setText(cardNFC.getBalance() + "");

        int status = cardNFC.getStatus();
        if (status == 1) {
            textStatusName.setText("Đã kích hoạt");
            textStatus.setBackgroundResource(R.drawable.shap_circle_online);
        } else {
            textStatusName.setText("Chưa kích hoạt");
            textStatus.setBackgroundResource(R.drawable.shap_circle_offline);
        }
        return convertView;
    }
}
