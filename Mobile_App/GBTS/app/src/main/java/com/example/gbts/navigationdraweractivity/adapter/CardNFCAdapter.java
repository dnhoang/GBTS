package com.example.gbts.navigationdraweractivity.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.enity.CardNFC;

import java.util.List;

/**
 * Created by truon on 10/5/2016.
 */

public class CardNFCAdapter extends BaseAdapter {
    Context context;
    List<CardNFC> cardNFCList;


    public CardNFCAdapter(Context context, List<CardNFC> cardNFCList) {
        this.context = context;
        this.cardNFCList = cardNFCList;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtCardName, txtStatus;
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
        TextView textCardName = (TextView) convertView.findViewById(R.id.txtCardName);
        TextView textStatus = (TextView) convertView.findViewById(R.id.txtStatus);

        textCardName.setText(cardNFC.getCardName());
        int status = cardNFC.getStatus();
        if (status == 1) {
            textStatus.setText("Đã kích hoạt");
        } else {
            textStatus.setText("Chưa kích hoạt");
        }
        return convertView;
    }

    public List<CardNFC> getCardNFCList() {
        return cardNFCList;
    }

    public void setCardNFCList(List<CardNFC> cardNFCList) {
        this.cardNFCList = cardNFCList;
    }
}
