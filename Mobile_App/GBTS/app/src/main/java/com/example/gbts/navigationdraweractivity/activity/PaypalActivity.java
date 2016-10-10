package com.example.gbts.navigationdraweractivity.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.MainActivity;
import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.enity.CreditPlan;
import com.example.gbts.navigationdraweractivity.fragment.CreditCard;
import com.example.gbts.navigationdraweractivity.fragment.CreditCardDetails;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class PaypalActivity extends AppCompatActivity {


    private static final String TAG = "PaypalActivity";
    private static final double Rate = 22304;

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;
    /**
     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
     * <p>
     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     * <p>
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
//    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AVArNPvtcbSIddr5eJuZ6liutApaPELRprlAnDvVGaIdmKut-IzRE_rFz1apCHDnpEi7hllDuni4Ryye";

    ActionBar actionBar;
    TextView txtName, txtCreditPlanPrice, txtCreditDescription;

    private static final String TAG_CARD_ID = "CardId";
    private static final String TAG_CARD_NAME = "CardName";
    private static final String TAG_REGISTRATION_DATE = "RegistrationDate";
    private static final String TAG_BALANCE = "Balance";
    private static final String TAG_CARD_STATUS = "Status";
    ArrayList<HashMap<String, String>> listCard = new ArrayList<>();
    JSONArray jsonArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);

        //Display Home As Up Enabled
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getSetData();

        //Paypal serivce
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        Button btnPayment = (Button) findViewById(R.id.btnPayment);
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBuyPress();
            }
        });
    }

    public void getSetData() {
        //Get getIntent from CreditPlanActivity
        Intent getIntent = getIntent();
        //Get bundle from Intent
        Bundle bundle = getIntent.getExtras();
        int id = bundle.getInt("creditPlanID");
        String name = bundle.getString("creditPlanName");
        String description = bundle.getString("creditPlanDescription");
        double price = bundle.getDouble("creditPlanPrice");
        //Get and Set Text Control
        txtName = (TextView) findViewById(R.id.txtCreditPlanName);
        txtName.setText(name);

        txtCreditDescription = (TextView) findViewById(R.id.txtCreditDescription);
        txtCreditDescription.setText(description);

        txtCreditPlanPrice = (TextView) findViewById(R.id.txtCreditPlanPrice);
        txtCreditPlanPrice.setText(price + "");

//        Log.d(TAG, "id: " + id);+rice);
    }

    // IMPLEMENTATION PAYMENT
    //clientId Buyer
    private static PayPalConfiguration config = new PayPalConfiguration()
            //Start with mock environment. When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            //or live (ENVIRONMENT_PRODUCTION)
            //or ENVIRONMENT_NO_NETWORK
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("GBTS Merchant")
            .merchantPrivacyPolicyUri(Uri.parse("http://grinbuz.com/PrivacyPolicy"))
            .merchantUserAgreementUri(Uri.parse("http://grinbuz.com/UserAgreement"));


    //EVENT ON BY PRESS
    public void onBuyPress() {
//        startPayPalService();
        //Get getIntent from CreditPlanActivity
        Intent getIntent = getIntent();
        //Get bundle from Intent
        Bundle bundle = getIntent.getExtras();
        String name = bundle.getString("creditPlanName");
        double price = bundle.getDouble("creditPlanPrice") / Rate;

        PayPalPayment payment = new PayPalPayment(new BigDecimal(price),
                "USD", name,
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        //send the same configuration for restart resilience
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));

                        //Get getIntent from CreditPlanActivity
                        Intent getIntent = getIntent();
                        //Get bundle from Intent
                        Bundle bundle = getIntent.getExtras();

                        String cardId = bundle.getString("cardIDForPayPal1");
                        Log.d(TAG, "cardID" + cardId);

                        String creditPlanId = bundle.getInt("creditPlanID") + "";
                        Log.d(TAG, "creditPlanId " + creditPlanId);

                        String creditPlanPrice = bundle.getInt("creditPlanPrice") + "";
                        Log.d(TAG, "creditPlanPrice " + creditPlanPrice);

                        JSONObject jsonObj = new JSONObject(confirm.toJSONObject().toString());
                        String transactionId = jsonObj.getJSONObject("response").getString("id");
                        Log.d(TAG, "transactionId " + transactionId);

                        new AddCardBalance().execute(cardId, creditPlanId, transactionId);

                        //Starting a new activity for the payment details and also putting the payment details with intent
//                        Intent intent = new Intent(this, ConfirmationActivity.class);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("afterPay", "1");
                        startActivity(intent);


                        Toast.makeText(
                                getApplicationContext(),
                                "PaymentConfirmation info received from PayPal", Toast.LENGTH_LONG)
                                .show();
                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (requestCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "the user canceled.");
            } else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(TAG, "An invalid Payment or PayPalConfiguration was submitted. Please see the docs");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //ASYNC TASK AddCardBalance
    public class AddCardBalance extends AsyncTask<String, Void, JSONObject> {
        String url;
        String phone;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SharedPreferences preferences = getSharedPreferences("Info", MODE_PRIVATE);
            phone = preferences.getString("Phonenumber", "TRuong dep trai");
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jsonParser = new JSONParser();
            url = Constance.API_GET_ADDCARDBALANCE +
                    "&cardId=" + params[0] +
                    "&creditPlanId=" + params[1] +
                    "&transactionId=" + params[2];
            JSONObject json = jsonParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            Log.d(TAG + "tq", jsonObject.toString());
            boolean message = jsonObject.optBoolean("success");
            Log.d(TAG + "tq", "message " + message);
            if (message) {
                Toast.makeText(getApplicationContext(), jsonObject.optBoolean("success") + "", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), jsonObject.optBoolean("message") + "", Toast.LENGTH_LONG).show();
            }
        }
    }


}
