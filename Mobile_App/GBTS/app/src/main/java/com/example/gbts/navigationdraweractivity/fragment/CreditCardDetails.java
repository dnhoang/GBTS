package com.example.gbts.navigationdraweractivity.fragment;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.activity.CreditPlanActivity;
import com.example.gbts.navigationdraweractivity.adapter.CardNFCDetailAdapter;
import com.example.gbts.navigationdraweractivity.asyntask.AsyncCardNFCListViewLoader;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.enity.CardNFC;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by truon on 9/29/2016.
 */

public class CreditCardDetails extends DialogFragment
        implements View.OnClickListener {
    private static final String TAG = "CreditCardDetails";
    View view;
    ListView listView;
    private static final String TAG_CARD_ID = "CardId";
    private static final String TAG_CARD_NAME = "CardName";
    private static final String TAG_REGISTRATION_DATE = "RegistrationDate";
    private static final String TAG_BALANCE = "Balance";
    private static final String TAG_CARD_STATUS = "Status";

    ArrayList<HashMap<String, String>> listCard = new ArrayList<>();

    JSONArray jsonArray = null;

    HashMap<String, String> card = new HashMap<>();

    TextView txtCardId, txtBalance, txtRegistration, txtStatus;
    EditText edtCardName;
    ImageView imgEditCardName;
    Button btnPurchase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contain_details, container, false);

        //Send data by intent
        //Get bundle
        Bundle bundle = getArguments();
        card.put(TAG_CARD_ID, bundle.getString(TAG_CARD_ID));
        card.put(TAG_CARD_NAME, bundle.getString(TAG_CARD_NAME));
        card.put(TAG_REGISTRATION_DATE, bundle.getString(TAG_REGISTRATION_DATE));
        card.put(TAG_BALANCE, bundle.getString(TAG_BALANCE));
        card.put(TAG_CARD_STATUS, bundle.getString(TAG_CARD_STATUS));

        //Get control
        txtCardId = (TextView) view.findViewById(R.id.txtCardID);
        txtBalance = (TextView) view.findViewById(R.id.txtBalance);
        txtRegistration = (TextView) view.findViewById(R.id.txtRegistrationDate);
        txtStatus = (TextView) view.findViewById(R.id.txtStatus);
        edtCardName = (EditText) view.findViewById(R.id.edtCardName);
        imgEditCardName = (ImageView) view.findViewById(R.id.imgEditCardView);

        txtCardId.setText(card.get(TAG_CARD_ID));
        edtCardName.setText(card.get(TAG_CARD_NAME));
        txtRegistration.setText(card.get(TAG_REGISTRATION_DATE));
        txtBalance.setText(card.get(TAG_BALANCE));
        txtStatus.setText(card.get(TAG_CARD_STATUS));

        btnPurchase = (Button) view.findViewById(R.id.btnPurchase);
        btnPurchase.setOnClickListener(this);
        return view;
    }
//    public void clickCreditPlan(View view){
//
//    }

    @Override
    public void onClick(View v) {

        Bundle bundle = getArguments();
        String cardID = bundle.getString(TAG_CARD_ID);
        Log.d("meowmeow: ", cardID);

        if (R.id.btnPurchase == v.getId()) {
            Intent intent = new Intent(getActivity(), CreditPlanActivity.class);
            Bundle bundle1 = new Bundle();
            bundle1.putString("cardIDForPayPal", cardID);
            intent.putExtras(bundle1);
            startActivity(intent);
        } else {
            // do the same for signInButton
        }
        Log.d(TAG, R.id.btnPurchase + "");
    }
}
