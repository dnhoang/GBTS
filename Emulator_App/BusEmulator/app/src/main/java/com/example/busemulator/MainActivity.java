package com.example.busemulator;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


import Util.DBAdapter;
import Util.Utility;
import sample.dto.OfflineTicket;

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
    String boughtDate;
    //    String cardDataVersion;
//    String cardBalance;
    final String secretKey = "ssshhhhhhhhhhh!!!!";

    private DBAdapter dbAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbAdapter = new DBAdapter(this);
        synchronizeOfflineTicket();
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
                    SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);

                    ticketTypeId = sharedPreferences.getString("ticketTypeId", "");
                    routeCode = sharedPreferences.getString("code", "");
                    boughtDate = Utility.getBoughtDateString();

                    dbAdapter.insertOfflineCashTicket(ticketTypeId, routeCode, boughtDate);
                    fabOffline.hide();
                    fab.hide();
                    successTicket = (RelativeLayout) findViewById(R.id.container);
                    successTicket.setVisibility(View.VISIBLE);
                    changeLayout(true);
                } else {
                    //Toast.makeText(getApplicationContext(), "Đã có kết nối mạng, vui lòng sử dụng thẻ!", Toast.LENGTH_LONG).show();
                    new SellCashTicketOnline().execute();
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

            if (Utility.isNetworkConnected(MainActivity.this)) {

                mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);


                SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
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
                cardId = bin2hex(mytag.getId());
                final SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
                ticketTypeId = sharedPreferences.getString("ticketTypeId", "");
                routeCode = sharedPreferences.getString("code", "");
                boughtDate = Utility.getBoughtDateString();
                String[] techList = mytag.getTechList();
                String searchedTech = Ndef.class.getName();

                for (String tech : techList) {
                    if (searchedTech.equals(tech)) {
                        //new NdefReaderTaskOffline().execute(mytag);
                        readNDEFMessage(mytag);
                        break;
                    }
                }


            }
        }
// Read Offline


//Verify ticket Async
    }

    private void readNDEFMessage(Tag tag) {
        Ndef ndef = Ndef.get(tag);


        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
        if (ndefMessage != null) {
            NdefRecord[] records = ndefMessage.getRecords();
            if (records != null) {
                for (NdefRecord ndefRecord : records) {
                    if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                        try {
                            byte[] payload = ndefRecord.getPayload();

                            // Get the Text Encoding
                            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

                            // Get the Language Code
                            int languageCodeLength = payload[0] & 0063;

                            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
                            // e.g. "en"

                            // Get the Text
                            String result = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
                            if (result != null) {
                                Utility utility = new Utility();
                                String cardData[] = utility.getCardDataFromEncryptedString(result);
                                SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
                                Integer price = Integer.parseInt(sharedPreferences.getString("price", "0"));
                                String cardBalance = cardData[0];
                                Long cardBalanceLong = Long.parseLong(cardBalance);
                                if (cardBalanceLong >= price) {

                                    String dataVersion = utility.getDataVersion();
                                    String dataToWrite = "" + (cardBalanceLong - price) + "|" + dataVersion;

                                    try {
                                        utility.writeCard(dataToWrite, tag);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (FormatException e) {
                                        e.printStackTrace();
                                    }
                                    //ghi database
                                    dbAdapter.insertOfflineTicket(cardId, ticketTypeId, routeCode, boughtDate);

                                    //
                                    successTicket = (RelativeLayout) findViewById(R.id.container);
                                    successTicket.setVisibility(View.VISIBLE);
                                    changeLayout(true);

                                } else {

                                    failTicket = (RelativeLayout) findViewById(R.id.containerfail);
                                    failTicket.setVisibility(View.VISIBLE);
                                    changeLayout(false);
                                }


                            } else {
                                failTicket = (RelativeLayout) findViewById(R.id.containerfail);
                                failTicket.setVisibility(View.VISIBLE);
                                changeLayout(false);
                            }

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        } else {
            failTicket = (RelativeLayout) findViewById(R.id.containerfail);
            TextView tvFail = (TextView) findViewById(R.id.tvFail);
            tvFail.setText("Thẻ không hợp lệ");
            failTicket.setVisibility(View.VISIBLE);
            changeLayout(false);
        }

    }

    private void synchronizeOfflineTicket() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (Utility.isNetworkConnected(getApplicationContext())) {
                    if (!dbAdapter.isOfflineDataEmpty()) {
                        List<OfflineTicket> list = dbAdapter.getAllOfflineTicket();
                        for (OfflineTicket ticket : list
                                ) {
                            //Send request
                            if (!dbAdapter.isOfflineDataEmpty()) {
                                String[] params = {ticket.getCardid(), ticket.getTickettypeid(), ticket.getRoutecode(), ticket.getBoughtdate(), ticket.getId() + ""};
                                new PushOfflineData().execute(params);
                            }

                        }
                    }
                    if (!dbAdapter.isOfflineCashDataEmpty()) {
                        List<OfflineTicket> list = dbAdapter.getAllOfflineCashTicket();
                        for (OfflineTicket ticket : list
                                ) {
                            //Send request
                            if (!dbAdapter.isOfflineCashDataEmpty()) {

                                String[] params = {ticket.getTickettypeid(), ticket.getRoutecode(), ticket.getBoughtdate(), ticket.getId() + ""};
                                new PushOfflineCashData().execute(params);
                            }

                        }
                    }
                }

            }

        }, 0, 2 * 1000);
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
            if (ndefMessage != null) {
                NdefRecord[] records = ndefMessage.getRecords();
                if (records != null) {
                    for (NdefRecord ndefRecord : records) {
                        if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                            try {
                                return readText(ndefRecord);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    return null;
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
                String cardBalance = cardData[0];
                String cardDataVersion = cardData[1];

                String[] params = {cardId, routeCode, cardBalance, cardDataVersion};
                new VerifyTicket().execute(params);
            } else {
                failTicket = (RelativeLayout) findViewById(R.id.containerfail);
                TextView tvFail = (TextView) findViewById(R.id.tvFail);
                tvFail.setText("Thẻ không hợp lệ");
                failTicket.setVisibility(View.VISIBLE);
                changeLayout(false);
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

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Kiểm tra thẻ ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            Utility jParser = new Utility();
            cardId = params[0];
            SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
            ticketTypeId = sharedPreferences.getString("ticketTypeId", "");
            routeCode = params[1];
            cardBalance = params[2];
            cardDataVersion = params[3];


            hostAddress = sharedPreferences.getString("host", "https://grinbuzz.com");
            String strURL = hostAddress + "/Api/SellTicket?key=gbts_2016_capstone&cardId=" + cardId +
                    "&ticketTypeId=" + ticketTypeId +
                    "&routeCode=" + routeCode +
                    "&currentBalance=" + cardBalance +
                    "&dataVersion=" + cardDataVersion;

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(strURL);

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
            if (jsonObject != null) {
                try {
                    success = jsonObject.getBoolean("success");
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
                            needUpdate = jsonObject.getBoolean("needUpdate");
                            balance = jsonObject.getLong("balance");
                            amount = jsonObject.getInt("amount");
                            version = jsonObject.getLong("version");
                            updateCard(needUpdate, mytag, balance, amount, version, Long.parseLong(cardBalance), Long.parseLong(cardDataVersion));
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


                        try {
                            String message = jsonObject.getString("message");
                            final TextView tvFail = (TextView) findViewById(R.id.tvFail);

                            tvFail.setText(message);

                            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        changeLayout(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {//JSON ==null
                TextView tvError = (TextView) findViewById(R.id.tvError);
                tvError.setText("Vui lòng chạm và giữ thẻ");
            }
        }

    }

    private void updateCard(Boolean checkUpdate, Tag tag, Long balance, Integer amount,
                            Long dataVersion, Long cardBalance, Long cardVersion) throws IOException, FormatException {
        if (checkUpdate) {
            Utility utility = new Utility();
            String dataToWrite = balance + "|" + dataVersion;

            utility.writeCard(dataToWrite, tag);

            //For debug
            //this.cardBalance = dataToWrite;
        } else {
            Utility utility = new Utility();
            String dataToWrite = (cardBalance - amount) + "|" + cardVersion;

            utility.writeCard(dataToWrite, tag);
            //For debug
            //this.cardBalance = dataToWrite;
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
                    TextView tvFail = (TextView) findViewById(R.id.tvFail);
                    tvFail.setText("Mua vé không thành công");
                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                    fab.show();
                    FloatingActionButton fabOffline = (FloatingActionButton) findViewById(R.id.fabOffline);
                    fabOffline.show();

                }
                //Toast.makeText(getApplicationContext(), "So tiền trên thẻ hiện tại " + cardBalance, Toast.LENGTH_SHORT).show();
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
    //Push offline data
    private class PushOfflineData extends AsyncTask<String, String, JSONObject> {

        String cardId, ticketTypeId, routeCode, boughtDate, id;

        @Override
        protected JSONObject doInBackground(String... params) {
            Utility jParser = new Utility();
            cardId = params[0];

            ticketTypeId = params[1];
            routeCode = params[2];
            boughtDate = params[3];
            id = params[4];
            SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
            hostAddress = sharedPreferences.getString("host", "https://grinbuzz.com");
            String strURL = hostAddress + "/Api/PushOfflineData?key=gbts_2016_capstone&cardId=" + cardId +
                    "&ticketTypeId=" + ticketTypeId +
                    "&routeCode=" + routeCode +
                    "&boughtDate=" + boughtDate;
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(strURL);
            Log.d("TICKET", strURL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            // Hide dialog

            boolean success;
            //check success
            if (jsonObject != null) {
                Log.d("TICKET", jsonObject.toString());
                try {
                    success = jsonObject.getBoolean("success");
                    if (success) {
                        Log.d("TICKET", "Push data success");
                        dbAdapter.deleteOfflineTicket(Long.parseLong(id));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //End push offline data

    //Push offline cash data
    private class PushOfflineCashData extends AsyncTask<String, String, JSONObject> {

        String ticketTypeId, routeCode, boughtDate, id;

        @Override
        protected JSONObject doInBackground(String... params) {
            Utility jParser = new Utility();

            ticketTypeId = params[0];
            routeCode = params[1];
            boughtDate = params[2];
            id = params[3];
            SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
            hostAddress = sharedPreferences.getString("host", "https://grinbuzz.com");
            String strURL = hostAddress + "/Api/PushCashTicketOffline?key=gbts_2016_capstone" +
                    "&ticketTypeId=" + ticketTypeId +
                    "&routeCode=" + routeCode +
                    "&boughtDate=" + boughtDate;
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(strURL);
            Log.d("TICKET", strURL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            // Hide dialog

            boolean success;
            //check success
            if (jsonObject != null) {
                try {
                    success = jsonObject.getBoolean("success");
                    if (success) {
                        dbAdapter.deleteOfflineCashTicket(Long.parseLong(id));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //End push offline cash data

    private class SellCashTicketOnline extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String ticketTypeId, routeCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Kiểm tra thẻ ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            Utility jParser = new Utility();
            SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
            ticketTypeId = sharedPreferences.getString("ticketTypeId", "");
            routeCode = sharedPreferences.getString("code", "");

            hostAddress = sharedPreferences.getString("host", "https://grinbuzz.com");
            String strURL = hostAddress + "/Api/SellCashTicket?key=gbts_2016_capstone&ticketTypeId=" + ticketTypeId +
                    "&routeCode=" + routeCode;
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(strURL);
            Log.d("TICKET", strURL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            // Hide dialog
            pDialog.dismiss();
            boolean success;
            //check success
            if (jsonObject != null) {
                try {
                    success = jsonObject.getBoolean("success");
                    if (success) {
                        successTicket = (RelativeLayout) findViewById(R.id.container);
                        successTicket.setVisibility(View.VISIBLE);
                        changeLayout(true);
                    } else {
                        failTicket = (RelativeLayout) findViewById(R.id.containerfail);
                        failTicket.setVisibility(View.VISIBLE);
                        changeLayout(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}