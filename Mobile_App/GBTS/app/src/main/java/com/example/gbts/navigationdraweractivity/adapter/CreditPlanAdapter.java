package com.example.gbts.navigationdraweractivity.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.enity.CreditPlan;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by truon on 10/7/2016.
 */

public class CreditPlanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<CreditPlan> creditPlen = Collections.emptyList();
    CreditPlan current;
    private OnItemClickListener clickListener;//2


    //interface that specifies listener’s behaviour
    public interface OnItemClickListener { //1
        void onClick(View view, int position);
    }
    //The constructor will receive an object that implements this interface, along with the items to be rendered:


    // create constructor to innitilize context and data sent from CreditPlanActivity
    public CreditPlanAdapter(Context context, List<CreditPlan> creditPlen) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.creditPlen = creditPlen;
    }


    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_listview_credit_plan, parent, false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Get current position of item in recyclerview to bind data and assign values from list
        Locale locale = new Locale("vi_VN", "VN");
        Log.d("locale ", locale + "");
        NumberFormat defaultFormat = NumberFormat.getCurrencyInstance(locale);
        MyHolder myHolder = (MyHolder) holder;
        current = creditPlen.get(position);
//        myHolder.imgIcon.setBackgroundResource(R.drawable.ic_normal);
        myHolder.txtCreditPlanName.setText(current.creditplanName);
        myHolder.txtPrice.setText("Gói: " + defaultFormat.format(current.creditplanPrice));
        if (current.creditplanPrice == 30000) {
            myHolder.imgIcon.setBackgroundResource(R.drawable.ic_package_normal);
        } else if (current.creditplanPrice == 500000) {
            myHolder.imgIcon.setBackgroundResource(R.drawable.ic_tien_spec);
        } else if (current.creditplanPrice == 100000) {
            myHolder.imgIcon.setBackgroundResource(R.drawable.ic_package_long);
        } else {
            myHolder.imgIcon.setBackgroundResource(R.drawable.ic_special);
        }
        myHolder.txtPrice.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

        // load image into imageview using glide
//        Glide.with(context).load("http://192.168.1.7/test/images/" + current.fishImage)
//                .placeholder(R.drawable.ic_img_error)
//                .error(R.drawable.ic_img_error)
//                .into(myHolder.ivFish);
    }

    // return total item from List
    @Override
    public int getItemCount() {
        return creditPlen.size();
    }

    public void setClickListener(OnItemClickListener onItemClickListener) {
        this.clickListener = onItemClickListener;
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {//3 implement OnclickListener

        ImageView imgIcon;
        TextView txtCreditPlanName;
        TextView txtPrice;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            imgIcon = (ImageView) itemView.findViewById(R.id.ic_ct_credit_Plan);
            txtCreditPlanName = (TextView) itemView.findViewById(R.id.txt_ct_CreditPlanName);
            txtPrice = (TextView) itemView.findViewById(R.id.txt_ct_cdlPrice);
            // bind the listener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {//4
            if (clickListener != null) clickListener.onClick(itemView, getAdapterPosition());
        }
    }
}


