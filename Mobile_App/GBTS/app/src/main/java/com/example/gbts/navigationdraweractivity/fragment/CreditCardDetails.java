package com.example.gbts.navigationdraweractivity.fragment;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gbts.navigationdraweractivity.MainActivity;
import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.activity.CreditPlanActivity;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by truon on 9/29/2016.
 */

public class CreditCardDetails extends DialogFragment
        implements View.OnClickListener {
    private static final String TAG = "CreditCardDetails";
    View view;
    private static final String TAG_CARD_ID = "CardId";
    private static final String TAG_CARD_NAME = "CardName";
    private static final String TAG_REGISTRATION_DATE = "RegistrationDate";
    private static final String TAG_BALANCE = "Balance";
    private static final String TAG_CARD_STATUS = "Status";


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
        txtCardId = (TextView) view.findViewById(R.id.txtCardIDDetails);
        txtBalance = (TextView) view.findViewById(R.id.txtBalanceDetails);
        txtRegistration = (TextView) view.findViewById(R.id.txtRegistrationDateDetails);
        txtStatus = (TextView) view.findViewById(R.id.txtStatusDetails);
        edtCardName = (EditText) view.findViewById(R.id.edtCardNameDetails);
        imgEditCardName = (ImageView) view.findViewById(R.id.imgEditCardViewDetails);

        txtCardId.setText(card.get(TAG_CARD_ID));
        edtCardName.setText(card.get(TAG_CARD_NAME));
        txtRegistration.setText(card.get(TAG_REGISTRATION_DATE));
        txtBalance.setText(card.get(TAG_BALANCE));
        txtStatus.setText(card.get(TAG_CARD_STATUS));

        //EVENT CHANGE CARD NAME
        imgEditCardName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        imgEditCardName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String cardName = edtCardName.getText().toString().trim();
                String cardID = txtCardId.getText().toString();
                Log.d("changeaname ", "cardName " + cardName);
                Log.d("changeaname ", "cardID " + cardID);
                new AsyncChangeCardName().execute(cardID, cardName);
                return false;
            }
        });

        btnPurchase = (Button) view.findViewById(R.id.btnPurchase);
        btnPurchase.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

        Bundle bundle = getArguments();
        String cardID = bundle.getString(TAG_CARD_ID);

        if (R.id.btnPurchase == v.getId()) {
            Intent intent = new Intent(getActivity(), CreditPlanActivity.class);
            Bundle bundle1 = new Bundle();
            bundle1.putString("cardIDForPayPal", cardID);
            intent.putExtras(bundle1);
            startActivity(intent);
        } else {
            // do the same for signInButton
        }
    }

    private class AsyncChangeCardName extends AsyncTask<String, Void, JSONObject> {
        String url;
        String cardName = edtCardName.getText().toString().trim();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jsonParser = new JSONParser();
            url = Constance.API_CHANGE_CARD_NAME + "&cardId=" + params[0] + "&name=" + params[1];
            JSONObject json = jsonParser.getJSONFromUrlPOST(url);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                Log.d("changeaname ", "url " + url);
                boolean success = jsonObject.optBoolean("success");
                Log.d("changeaname ", "success " + success);
                String message = jsonObject.optString("message");
                Log.d("changeaname ", "changeaname " + message);
                if (success == true) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder
                            .setTitle(message + "\n" + cardName)
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            intent.putExtra("action", "changeCardName");
                                            startActivity(intent);
                                        }
                                    });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
