package com.example.busemulator;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;


import Util.Utility;

public class MainActivity extends AppCompatActivity {
    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag mytag;
    String setting = "settingPreference";
    String hostAddress = "https://grinbuz.com/";
    RelativeLayout successTicket;
    RelativeLayout failTicket;

    //
    String cardId;
    String ticketTypeId;
    String routeCode;
    String cardDataVersion;
    String cardBalance;
    final String secretKey = "ssshhhhhhhhhhh!!!!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //NFC
        RelativeLayout sucess = (RelativeLayout) findViewById(R.id.container);
        sucess.setVisibility(View.INVISIBLE);
        RelativeLayout fail = (RelativeLayout) findViewById(R.id.containerfail);
        fail.setVisibility(View.INVISIBLE);
        adapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};
        //Update thong tin

        //End update
        //END NFC
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNetworkConnected(getApplicationContext())) {
                    Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Vui lòng kiểm tra kết nối!", Toast.LENGTH_LONG).show();
                }
            }
        });
        final FloatingActionButton fabOffline = (FloatingActionButton) findViewById(R.id.fabOffline);

        fabOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utility.isNetworkConnected(getApplicationContext())) {
                    SharedPreferences sharedPreferences = getSharedPreferences("Info", MODE_PRIVATE);
                    Integer offlineTicket = sharedPreferences.getInt("OfflineTicket", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("OfflineTicket", offlineTicket + 1);
                    fabOffline.hide();
                    fab.hide();
                    successTicket = (RelativeLayout) findViewById(R.id.container);
                    successTicket.setVisibility(View.VISIBLE);
                    changeLayout(true);
                } else {
                    Toast.makeText(getApplicationContext(), "Đã có kết nối mạng, vui lòng sử dụng thẻ!", Toast.LENGTH_LONG).show();
                }
            }
        });
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//
//            }
//        });
    }

    @Override
    protected void onNewIntent(Intent intent) {

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            //con tien
            if (Utility.isNetworkConnected(MainActivity.this)) {
                //het tien
                mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);


                final SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
                cardId = bin2hex(mytag.getId());
                ticketTypeId = sharedPreferences.getString("ticketTypeId", "");
                routeCode = sharedPreferences.getString("code", "");

                //String[] params = {cardId, ticketTypeId, routeCode};
                //Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                String[] techList = mytag.getTechList();
                String searchedTech = Ndef.class.getName();

                for (String tech : techList) {
                    if (searchedTech.equals(tech)) {
                        new NdefReaderTask().execute(mytag);
                        break;
                    }
                }


            } else {
                //Toast.makeText(this, "Không thể kết nối với máy chủ!", Toast.LENGTH_SHORT).show();
                mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                String[] techList = mytag.getTechList();
                String searchedTech = Ndef.class.getName();

                for (String tech : techList) {
                    if (searchedTech.equals(tech)) {
                        new NdefReaderTaskOffline().execute(mytag);

                        break;
                    }
                }
            }
        }

//Verify ticket Async
    }
    private class NdefReaderTaskOffline extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {

                Utility utility = new Utility();
                String cardData[] = utility.getCardDataFromEncryptedString(result);
                SharedPreferences sharedPreferences=getSharedPreferences(setting,MODE_PRIVATE);
                Integer price=Integer.parseInt(sharedPreferences.getString("price","0"));
                cardBalance = cardData[0];
                Long cardBalanceLong=Long.parseLong(cardBalance);
                if (cardBalanceLong>=price){

                    String dataVersion=utility.getDataVersion();
                    String dataToWrite=""+(cardBalanceLong-price)+"|"+dataVersion;
                    //For debug
                    cardBalance = dataToWrite;
                    try {
                        utility.writeCard(dataToWrite,mytag);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (FormatException e) {
                        e.printStackTrace();
                    }
                    successTicket = (RelativeLayout) findViewById(R.id.container);
                    successTicket.setVisibility(View.VISIBLE);
                    changeLayout(true);

                } else{

                    failTicket = (RelativeLayout) findViewById(R.id.containerfail);
                    failTicket.setVisibility(View.VISIBLE);
                    changeLayout(false);
                }




            }
        }
    }
    //Read NDEF message
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Utility utility = new Utility();
                String cardData[] = utility.getCardDataFromEncryptedString(result);
                cardBalance = cardData[0];
                cardDataVersion = cardData[1];

                String[] params = {cardId, ticketTypeId, routeCode, cardBalance, cardDataVersion};
                new VerifyTicket().execute(params);
            }
        }
    }

    //End read NDEF message
    static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }

    private class VerifyTicket extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String cardId, ticketTypeId, routeCode, cardDataVersion, cardBalance;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
            ticketTypeId = sharedPreferences.getString("ticketTypeId", "");
            EditText edtCode = (EditText) findViewById(R.id.edtRoute);
            //code = edtCode.getText().toString();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            Utility jParser = new Utility();
            cardId = params[0];
            ticketTypeId = params[1];
            routeCode = params[2];
            cardBalance = params[3];
            cardDataVersion = params[4];
            Log.d("INFO!!!",cardBalance.toString()+"|"+cardDataVersion+" cardId "+cardId);
            SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
            hostAddress = sharedPreferences.getString("host", "https://grinbuzz.com");
            String strURL = hostAddress + "/Api/SellTicket?key=gbts_2016_capstone&cardId=" + cardId +
                    "&ticketTypeId=" + ticketTypeId +
                    "&routeCode=" + routeCode +
                    "&currentBalance=" + cardBalance +
                    "&dataVersion=" + cardDataVersion;

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(strURL);
            Log.d("INFO!!!",strURL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            final SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
            super.onPostExecute(jsonObject);
            // Hide dialog
            pDialog.dismiss();
            boolean success = false;
            //check success
            try {
                success = jsonObject.getBoolean("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (success) {
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.hide();
                FloatingActionButton fabOffline = (FloatingActionButton) findViewById(R.id.fabOffline);
                fabOffline.hide();
                successTicket = (RelativeLayout) findViewById(R.id.container);
                successTicket.setVisibility(View.VISIBLE);
                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.b7);
                mediaPlayer.start();



                //End encrypt
                String message = null;
                Boolean needUpdate;
                Long balance;
                Integer amount;
                Long version;
                try {
                    message = jsonObject.getString("message");
                    needUpdate=jsonObject.getBoolean("needUpdate");
                    balance=jsonObject.getLong("balance");
                    amount=jsonObject.getInt("amount");
                    version=jsonObject.getLong("version");
                    updateCard(needUpdate,mytag,balance,amount,version,Long.parseLong(cardBalance),Long.parseLong(cardDataVersion));
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();



                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (FormatException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final TextView tvSuccessPrice = (TextView) findViewById(R.id.tvSuccessPrice);
                tvSuccessPrice.setText("Thẻ của bạn đã bị trừ " + sharedPreferences.getString("price", "0") + " đồng");
                changeLayout(true);
            } else {
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.hide();
                FloatingActionButton fabOffline = (FloatingActionButton) findViewById(R.id.fabOffline);
                fabOffline.hide();
                failTicket = (RelativeLayout) findViewById(R.id.containerfail);
                failTicket.setVisibility(View.VISIBLE);
                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.b7);
                mediaPlayer.start();

                String message = null;
                try {
                    message = jsonObject.getString("message");
                    final TextView tvFail = (TextView) findViewById(R.id.tvFail);
                    tvFail.setText(message);
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                changeLayout(false);
            }


        }
    }
    private void updateCard(Boolean checkUpdate,Tag tag, Long balance, Integer amount,
                            Long dataVersion, Long cardBalance, Long cardVersion) throws IOException, FormatException {
        if (checkUpdate){
            Utility utility=new Utility();
            String dataToWrite=balance+"|"+dataVersion;

            utility.writeCard(dataToWrite,tag);

            //For debug
            this.cardBalance = dataToWrite;
        } else{
            Utility utility=new Utility();
            String dataToWrite=(cardBalance-amount)+"|"+cardVersion;

            utility.writeCard(dataToWrite,tag);
            //For debug
            this.cardBalance = dataToWrite;
        }
    }

    private void changeLayout(final boolean result) {

        final RelativeLayout sucess = (RelativeLayout) findViewById(R.id.container);
        final RelativeLayout fail = (RelativeLayout) findViewById(R.id.containerfail);

        CountDownTimer timer = new CountDownTimer(2000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {


            }

            @Override
            public void onFinish() {
                if (result == true) {
                    sucess.setVisibility(View.INVISIBLE);
                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                    fab.show();
                    FloatingActionButton fabOffline = (FloatingActionButton) findViewById(R.id.fabOffline);
                    fabOffline.show();

                } else {
                    //fail

                    fail.setVisibility(View.INVISIBLE);
                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                    fab.show();
                    FloatingActionButton fabOffline = (FloatingActionButton) findViewById(R.id.fabOffline);
                    fabOffline.show();

                }
                Toast.makeText(getApplicationContext(),"So tiền trên thẻ hiện tại " +cardBalance, Toast.LENGTH_SHORT).show();
            }
        };
        timer.start();
    }

    public void onPause() {
        super.onPause();
        ReadAndWriteModeOff();
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView routeName = (TextView) findViewById(R.id.tvRouteName);
        SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
        routeName.setText(sharedPreferences.getString("name", "Chưa chọn tuyến"));
        TextView routeNumber = (TextView) findViewById(R.id.tvRouteNumber);
        routeNumber.setText("Tuyến " + sharedPreferences.getString("code", "Chưa chọn tuyến"));
        TextView price = (TextView) findViewById(R.id.tvPrice);
        price.setText("Giá vé: " + sharedPreferences.getString("price", "") + " đồng");
        ReadAndWriteModeOn();
    }

    private void ReadAndWriteModeOn() {
        writeMode = true;
        adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }

    private void ReadAndWriteModeOff() {
        writeMode = false;
        adapter.disableForegroundDispatch(this);
    }

    //NFC

}
