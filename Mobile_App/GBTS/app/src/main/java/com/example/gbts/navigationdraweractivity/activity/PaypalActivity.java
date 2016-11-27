package com.example.gbts.navigationdraweractivity.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.MainActivity;
import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.tabhost.TabhostActivity;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.example.gbts.navigationdraweractivity.utils.Utility;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

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
//        setContentView(R.layout.activity_paypal);
//
//        //Display Home As Up Enabled
//        actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setTitle("BẠN ĐÃ CHỌN GÓI");

//        getSetData();

        //Paypal serivce
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
        onBuyPress();
        Log.d("xemthenao ", "onCreate");
//        Button btnPayment = (Button) findViewById(R.id.btnPayment);
//        btnPayment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBuyPress();
//            }
//        });
    }

    public void getSetData() {
        //Get getIntent from CreditPlanActivity
        //Get bundle from Intent
        Locale locale = new Locale("vi_VN", "VN");
//        Log.d("locale ", locale + "");
        NumberFormat defaultFormat = NumberFormat.getCurrencyInstance(locale);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int id = bundle.getInt("creditPlanID");
            String name = bundle.getString("creditPlanName");
            String description = bundle.getString("creditPlanDescription");
            Log.d("description", "des" + description.toString() + "");
            double price = bundle.getDouble("creditPlanPrice");
            String strPrice = defaultFormat.format(price);
            //Get and Set Text Control
            ImageView imageView = (ImageView) findViewById(R.id.ic_credit_Plan);
            if (price == 30000) {
                imageView.setBackgroundResource(R.drawable.ic_package_normal);
            } else if (price == 500000) {
                imageView.setBackgroundResource(R.drawable.ic_special);
            } else if (price == 100000) {
                imageView.setBackgroundResource(R.drawable.ic_package_long);
            } else {
                imageView.setBackgroundResource(R.drawable.ic_tien_spec);
            }


            txtName = (TextView) findViewById(R.id.txtCreditPlanName);
            txtName.setText(name);

            txtCreditPlanPrice = (TextView) findViewById(R.id.txtCreditPlanPrice);
            txtCreditPlanPrice.setText(strPrice);
        } else {
            Log.d(TAG, "bunle is null ");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundleCheckResume = getIntent().getExtras();
        if (bundleCheckResume != null) {
            String check = bundleCheckResume.getString("checkResumePaypal");
            if (check != null) {
                if (check.equals("checkResumePaypal")) {
//                    startActivity(new Intent(PaypalActivity.this, TabhostActivity.class));
                    finish();
                    Log.d("xemthenao ", "onResume2");
                }
            }
        }
        Log.d("xemthenao ", "onResume1");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        startActivity(new Intent(PaypalActivity.this, TabhostActivity.class));
//            finish();
        Log.d("xemthenao ", "onBackPressed");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("xemthenao ", "onDestroy");
//        startActivity(new Intent(PaypalActivity.this, TabhostActivity.class));
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
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //Get bundle from Intent
            String name = bundle.getString("creditPlanName");
            double price = bundle.getDouble("creditPlanPrice") / Rate;

            PayPalPayment payment = new PayPalPayment(new BigDecimal(price),
                    "USD", name,
                    PayPalPayment.PAYMENT_INTENT_SALE);

            final Intent intent = new Intent(this, PaymentActivity.class);
            //send the same configuration for restart resilience
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);


            if (Utility.isNetworkConnected(PaypalActivity.this)) {
                startActivityForResult(intent, REQUEST_CODE_PAYMENT);

            } else {
                // custom dialog
                final Dialog dialog = new Dialog(PaypalActivity.this);
                dialog.setContentView(R.layout.custom_dialog_login);
                dialog.setTitle("Mất kết nối mạng ...");

                Button dialogButton = (Button) dialog.findViewById(R.id.dialogBtnOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utility.isNetworkConnected(PaypalActivity.this)) {
                            dialog.dismiss();
                            startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                        }
                    }
                });

                Button dialogCancel = (Button) dialog.findViewById(R.id.dialogBtnCancel);
                // if button is clicked, close the custom dialog
                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
                dialog.show();
            }
        } else {
            Log.i(TAG, "bundle is null");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            final Bundle checkResumePaypal = new Bundle();
            final Intent intentSend = getIntent();
            checkResumePaypal.putString("checkResumePaypal", "checkResumePaypal");
            intentSend.putExtras(checkResumePaypal);
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

                        //Get bundle from Intent
                        Bundle bundle = getIntent().getExtras();
                        if (bundle != null) {
                            final String cardId = bundle.getString("cardID");
                            Log.d(TAG, "cardID" + cardId);

                            final String creditPlanId = bundle.getInt("creditPlanID") + "";
                            Log.d(TAG, "creditPlanId " + creditPlanId);
//
//                            String creditPlanPrice = bundle.getInt("creditPlanPrice") + "";
//                            Log.d(TAG, "creditPlanPrice " + creditPlanPrice);

                            JSONObject jsonObj = new JSONObject(confirm.toJSONObject().toString());
                            final String transactionId = jsonObj.getJSONObject("response").getString("id");
                            Log.d(TAG, "transactionId " + transactionId);

                            if (Utility.isNetworkConnected(PaypalActivity.this)) {
                                new AddCardBalance().execute(cardId, creditPlanId, transactionId);
                            } else {
                                // custom dialog
                                final Dialog dialog = new Dialog(PaypalActivity.this);
                                dialog.setContentView(R.layout.custom_dialog_login);
                                dialog.setTitle("Mất kết nối mạng ...");

                                Button dialogButton = (Button) dialog.findViewById(R.id.dialogBtnOK);
                                // if button is clicked, close the custom dialog
                                dialogButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (Utility.isNetworkConnected(PaypalActivity.this)) {
                                            dialog.dismiss();
                                            new AddCardBalance().execute(cardId, creditPlanId, transactionId);
                                        }
                                    }
                                });

                                Button dialogCancel = (Button) dialog.findViewById(R.id.dialogBtnCancel);
                                // if button is clicked, close the custom dialog
                                dialogCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                    }
                                });
                                dialog.show();
                            }
                        } else {
                            Log.e(TAG, "creditPlanId || creditPlanPrice||creditPlanPrice is null ");
                        }


                        //Starting a new activity for the payment details and also putting the payment details with intent
//                        Intent intent = new Intent(this, ConfirmationActivity.class);
                        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("afterPay", "afterPay");
                        intent.putExtra("cardIDPaypal", bundle.getString("cardID"));
                        if (Utility.isNetworkConnected(PaypalActivity.this)) {
                            startActivity(intent);
                        } else {
                            // custom dialog
                            final Dialog dialog = new Dialog(PaypalActivity.this);
                            dialog.setContentView(R.layout.custom_dialog_login);
                            dialog.setTitle("Mất kết nối mạng ...");

                            Button dialogButton = (Button) dialog.findViewById(R.id.dialogBtnOK);
                            // if button is clicked, close the custom dialog
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (Utility.isNetworkConnected(PaypalActivity.this)) {
                                        dialog.dismiss();
                                        startActivity(intent);
                                    }
                                }
                            });

                            Button dialogCancel = (Button) dialog.findViewById(R.id.dialogBtnCancel);
                            // if button is clicked, close the custom dialog
                            dialogCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                }
                            });
                            dialog.show();
                        }
//                        Toast.makeText(
//                                getApplicationContext(),
//                                "PaymentConfirmation info received from PayPal", Toast.LENGTH_LONG)
//                                .show();
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
            url = Constance.API_GET_ADD_CARD_BALANCE +
                    "&cardId=" + params[0] +
                    "&creditPlanId=" + params[1] +
                    "&transactionId=" + params[2];
            JSONObject json = jsonParser.getJSONFromUrlPOST(url);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (jsonObject != null) {
                Log.d(TAG + "tq", jsonObject.toString());
                boolean message = jsonObject.optBoolean("success");
                Log.d(TAG + "tq", "message " + message);
                if (message) {
                    Log.d(TAG + "tq", "message " + message);
//                Toast.makeText(getApplicationContext(), jsonObject.optBoolean("success") + "", Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG + "tq", "message " + message);
//                Toast.makeText(getApplicationContext(), jsonObject.optBoolean("message") + "", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


}
