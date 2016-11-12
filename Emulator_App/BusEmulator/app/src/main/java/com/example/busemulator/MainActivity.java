package com.example.busemulator;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

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

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
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
    boolean countdownisRunning = false;
    //    String cardDataVersion;
//    String cardBalance;
    final String secretKey = "ssshhhhhhhhhhh!!!!";
    CountDownTimer timer;
    private DBAdapter dbAdapter = null;

    //GPS
    double latitude;
    double longitude;
    private Location location;
    private GoogleApiClient gac;

    protected synchronized void buildGoogleApiClient() {
        if (gac == null) {
            gac = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1000).show();
            } else {
                Toast.makeText(this, "Thiết bị này không hỗ trợ.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Đã kết nối với google api, lấy vị trí
        getLocation();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        } else {

            location = LocationServices.FusedLocationApi.getLastLocation(gac);
            SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
//                Log.d("LOCATION", latitude+""+" "+longitude);
                if (sharedPreferences.getString("direction", "").equals(sharedPreferences.getString("startName", ""))) {
                    double startLong = Double.parseDouble(sharedPreferences.getString("startLong", ""));
                    double startLat = Double.parseDouble(sharedPreferences.getString("startLat", ""));
                    Log.d("LOCATION", sharedPreferences.getString("startLong", "") + " " + sharedPreferences.getString("startLat", ""));
                    double distance = Utility.distance(latitude, startLat, longitude, startLong, 0, 0);
                    if (Math.abs(distance) < 100) {
                        Intent intent = new Intent(this, LoginActivity.class);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("onTrip", false).commit();
                        startActivity(intent);
                        finish();
                    }
                } else if (sharedPreferences.getString("direction", "").equals(sharedPreferences.getString("endName", ""))) {
                    double endLong = Double.parseDouble(sharedPreferences.getString("endLong", ""));
                    double endLat = Double.parseDouble(sharedPreferences.getString("endLat", ""));
                    Log.d("LOCATION", sharedPreferences.getString("endLong", "") + " " + sharedPreferences.getString("endLat", ""));
                    double distance = Utility.distance(latitude, endLat, longitude, endLong, 0, 0);
                    if (Math.abs(distance) < 100) {
                        Intent intent = new Intent(this, LoginActivity.class);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("onTrip", false).commit();
                        startActivity(intent);
                        finish();
                    }
                }
            } else {

                Log.d("LOCATION", "KHONG CO LOCATION");
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        gac.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Lỗi kết nối: " + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    protected void onStart() {
        gac.connect();
        super.onStart();
    }

    protected void onStop() {
        gac.disconnect();
        super.onStop();
    }

    //End GPS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Lấy thông tin hướng đi và GPS
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }
        SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
        TextView tvDirection = (TextView) findViewById(R.id.tvDirection);
        String direction = sharedPreferences.getString("direction", "");
        tvDirection.setText("Đi " + direction);
        //
        //TIMER
        timer = new CountDownTimer(10 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //fabOffline.show();
                countdownisRunning = true;
            }

            @Override
            public void onFinish() {
                FloatingActionButton fabOffline = (FloatingActionButton) findViewById(R.id.fabOffline);
                fabOffline.hide();
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.hide();
                countdownisRunning = false;
            }
        };
        //
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
        fab.hide();
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
        fabOffline.hide();
        fabOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countdownisRunning == true) {

                    countdownisRunning = false;
                    showFabOffline();
                }
                if (!Utility.isNetworkConnected(getApplicationContext())) {
                    showFabOffline();

                    SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);

                    ticketTypeId = sharedPreferences.getString("ticketTypeId", "");
                    routeCode = sharedPreferences.getString("code", "");
                    boughtDate = Utility.getBoughtDateString();

                    dbAdapter.insertOfflineCashTicket(ticketTypeId, routeCode, boughtDate);
//                    fabOffline.hide();
                    fab.hide();
                    successTicket = (RelativeLayout) findViewById(R.id.container);
                    successTicket.setVisibility(View.VISIBLE);
                    MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.b7);
                    mediaPlayer.start();
                    changeLayout(true);
                } else {
                    showFabOffline();
                    //Toast.makeText(getApplicationContext(), "Đã có kết nối mạng, vui lòng sử dụng thẻ!", Toast.LENGTH_LONG).show();
                    new SellCashTicketOnline().execute();
                }
            }
        });
    }

    private void showFabOffline() {
        final FloatingActionButton fabOffline = (FloatingActionButton) findViewById(R.id.fabOffline);
        fabOffline.show();
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.show();
        timer.cancel();
        countdownisRunning = true;
        timer.start();
    }

    @Override
    protected void onNewIntent(Intent intent) {

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (bin2hex(tag.getId()).equals("DD7F7F81")) {
                showFabOffline();
            } else if (Utility.isNetworkConnected(MainActivity.this)) {
                mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
                cardId = bin2hex(mytag.getId());
                ticketTypeId = sharedPreferences.getString("ticketTypeId", "");
                routeCode = sharedPreferences.getString("code", "");

                String[] techList = mytag.getTechList();
                String searchedTech = Ndef.class.getName();
                boolean readError=true;
                for (String tech : techList) {
                    if (searchedTech.equals(tech)) {
                        Log.d("TAG", "BTH");
                        new NdefReaderTask().execute(mytag);
                        readError=false;
                        break;
                    }
                }
                if (readError==true){
                    Log.d("TAG", "Card bi loi");
                    String[] params = {cardId, routeCode, "0", "-1"};
                    new VerifyTicket().execute(params);
                }
            } else {
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
                        try {
                            readNDEFMessage(mytag);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                                    MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.b7);
                                    mediaPlayer.start();
                                    changeLayout(false);
                                }


                            } else {
                                failTicket = (RelativeLayout) findViewById(R.id.containerfail);
                                failTicket.setVisibility(View.VISIBLE);
                                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.b7);
                                mediaPlayer.start();
                                changeLayout(false);
                            }

                        } catch (Exception e) {
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
                getLocation();
                if (Utility.isNetworkConnected(getApplicationContext())) {
                    if (!dbAdapter.isOfflineDataEmpty()) {
                        new PushOfflineData().execute();
                    }
                    if (!dbAdapter.isOfflineCashDataEmpty()) {
                        new PushOfflineCashData().execute();
                    }

                }
            }

        }, 0, 10 * 1000);
    }

    //Read NDEF message
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
        Tag tag;

        @Override
        protected String doInBackground(Tag... params) {
            tag = params[0];

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
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = payload[0] & 0063;
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Utility utility = new Utility();
                String cardData[] = utility.getCardDataFromEncryptedString(result);
                if (cardData[1] != null) {
                    String cardBalance = cardData[0];
                    String cardDataVersion = cardData[1];

                    String[] params = {cardId, routeCode, cardBalance, cardDataVersion};
                    new VerifyTicket().execute(params);
                } else {
                    String[] decryptPhoneData = utility.getDataFromPhoneEncryptedString(result);
                    VerifyPhoneTicket(decryptPhoneData);
                }


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

                        successTicket = (RelativeLayout) findViewById(R.id.container);
                        successTicket.setVisibility(View.VISIBLE);
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.b7);
                        mediaPlayer.start();
                        //End encrypt
                        String message = null;
                        Boolean needUpdate;
                        Long balance;
                        Integer amount = 0;
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
                        tvSuccessPrice.setText("Thẻ của bạn đã bị trừ " + amount + "" + " đồng");
                        changeLayout(true);
                    } else {
                        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                        fab.hide();
                        failTicket = (RelativeLayout) findViewById(R.id.containerfail);
                        failTicket.setVisibility(View.VISIBLE);

                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.b7);
                        mediaPlayer.start();

                        try {
                            String message = jsonObject.getString("message");
                            final TextView tvFail = (TextView) findViewById(R.id.tvFail);

                            tvFail.setText(message);

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

    //Verify Ticket for Phone
    private class VerifyPhoneTicketWithToken extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String cardId, ticketTypeId, routeCode, cardDataVersion, cardBalance, token;

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
            token = params[4];

            hostAddress = sharedPreferences.getString("host", "https://grinbuzz.com");
            String strURL = hostAddress + "/Api/SellTicketWithToken?key=gbts_2016_capstone&cardId=" + cardId +
                    "&ticketTypeId=" + ticketTypeId +
                    "&routeCode=" + routeCode +
                    "&currentBalance=" + cardBalance +
                    "&dataVersion=" + cardDataVersion +
                    "&token=" + token;
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(strURL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            // Hide dialog
            pDialog.dismiss();
            boolean success = false;
            //check success
            if (jsonObject != null) {
                try {
                    success = jsonObject.getBoolean("success");
                    if (success) {
                        String amount = jsonObject.getString("amount");

                        successTicket = (RelativeLayout) findViewById(R.id.container);
                        successTicket.setVisibility(View.VISIBLE);
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.b7);
                        mediaPlayer.start();
                        final TextView tvSuccessPrice = (TextView) findViewById(R.id.tvSuccessPrice);
                        tvSuccessPrice.setText("Thẻ của bạn đã bị trừ " + amount + "" + " đồng");
                        changeLayout(true);
                    } else {
                        failTicket = (RelativeLayout) findViewById(R.id.containerfail);
                        failTicket.setVisibility(View.VISIBLE);

                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.b7);
                        mediaPlayer.start();
                        try {
                            String message = jsonObject.getString("message");
                            final TextView tvFail = (TextView) findViewById(R.id.tvFail);

                            tvFail.setText(message);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        changeLayout(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    //End phone
    private void updateCard(Boolean checkUpdate, Tag tag, Long balance, Integer amount,
                            Long dataVersion, Long cardBalance, Long cardVersion) throws IOException, FormatException {
        if (checkUpdate) {
            Utility utility = new Utility();
            String dataToWrite = balance + "|" + dataVersion;

            utility.writeCard(dataToWrite, tag);
        } else {
            Utility utility = new Utility();
            String dataToWrite = (cardBalance - amount) + "|" + cardVersion;

            utility.writeCard(dataToWrite, tag);
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
                } else {
                    //fail
                    fail.setVisibility(View.INVISIBLE);
                    TextView tvFail = (TextView) findViewById(R.id.tvFail);
                    tvFail.setText("Mua vé không thành công");

                }
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
        //Quet the bang dien thoai
//        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
//            VerifyPhoneTicket(getIntent());
//        }
        //End dien thoai
        TextView routeName = (TextView) findViewById(R.id.tvRouteName);
        SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
        routeName.setText(sharedPreferences.getString("name", "Chưa chọn tuyến"));
        TextView routeNumber = (TextView) findViewById(R.id.tvRouteNumber);
        routeNumber.setText("Tuyến " + sharedPreferences.getString("code", "Chưa chọn tuyến"));
        TextView price = (TextView) findViewById(R.id.tvPrice);
        price.setText("Giá vé: " + sharedPreferences.getString("price", "") + " đồng");
        ReadAndWriteModeOn();
    }

    //Quet the bang dien thoai
    private void VerifyPhoneTicket(String[] cardIdNDEF) {

        if (cardIdNDEF != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
            String cardId = "", routeCode, cardDataVersion, cardBalance, token = "";
            routeCode = sharedPreferences.getString("code", "");
            cardBalance = "0";
            cardDataVersion = "-1";
            try {
                cardId = cardIdNDEF[0];
                token = cardIdNDEF[1];
                String params[] = {cardId, routeCode, cardBalance, cardDataVersion, token};
                new VerifyPhoneTicketWithToken().execute(params);
            } catch (NullPointerException e) {
                failTicket = (RelativeLayout) findViewById(R.id.containerfail);
                TextView tvFail = (TextView) findViewById(R.id.tvFail);
                tvFail.setText("Không có token, vui lòng thử lại");
                failTicket.setVisibility(View.VISIBLE);
                MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.b7);
                mediaPlayer.start();
                changeLayout(false);
            }
        }
    }

    //End Quet the bang dien thoai
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
    private class PushOfflineData extends AsyncTask<Void, Void, Void> {

        String cardId, ticketTypeId, routeCode, boughtDate, id;


        @Override
        protected Void doInBackground(Void... params) {

            if (!dbAdapter.isOfflineDataEmpty()) {
                List<OfflineTicket> list = dbAdapter.getAllOfflineTicket();
                for (OfflineTicket ticket : list
                        ) {
                    //Send request
                    if (!dbAdapter.isOfflineDataEmpty()) {

                        cardId = ticket.getCardid();

                        ticketTypeId = ticket.getTickettypeid();
                        routeCode = ticket.getRoutecode();
                        boughtDate = ticket.getBoughtdate();
                        id = ticket.getId() + "";
                        SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
                        hostAddress = sharedPreferences.getString("host", "https://grinbuzz.com");
                        String strURL = hostAddress + "/Api/PushOfflineData?key=gbts_2016_capstone&cardId=" + cardId +
                                "&ticketTypeId=" + ticketTypeId +
                                "&routeCode=" + routeCode +
                                "&boughtDate=" + boughtDate;
                        // Getting JSON from URL
                        Utility jParser = new Utility();
                        JSONObject json = jParser.getJSONFromUrl(strURL);
                        //check success
                        if (json != null) {
                            try {
                                boolean success = json.getBoolean("success");
                                if (success == true) {
                                    dbAdapter.deleteOfflineTicket(Long.parseLong(id));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else break;

                }
            }
            return null;
        }


    }

    //End push offline data
//Push offline cash data
    private class PushOfflineCashData extends AsyncTask<Void, Void, Void> {

        String ticketTypeId, routeCode, boughtDate, id;

        @Override
        protected Void doInBackground(Void... params) {
            if (!dbAdapter.isOfflineCashDataEmpty()) {
                List<OfflineTicket> list = dbAdapter.getAllOfflineCashTicket();
                for (OfflineTicket ticket : list) {
                    if (!dbAdapter.isOfflineCashDataEmpty()) {
                        Utility jParser = new Utility();

                        ticketTypeId = ticket.getTickettypeid();
                        routeCode = ticket.getRoutecode();
                        boughtDate = ticket.getBoughtdate();
                        id = ticket.getId() + "";
                        SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
                        hostAddress = sharedPreferences.getString("host", "https://grinbuzz.net");
                        String strURL = hostAddress + "/Api/PushCashTicketOffline?key=gbts_2016_capstone" +
                                "&ticketTypeId=" + ticketTypeId +
                                "&routeCode=" + routeCode +
                                "&boughtDate=" + boughtDate;
                        // Getting JSON from URL
                        JSONObject json = jParser.getJSONFromUrl(strURL);
                        //check success
                        if (json != null) {
                            try {
                                boolean success = json.getBoolean("success");
                                if (success) {
                                    //Toast.makeText(getApplicationContext(), "Pushed offline cash data successfully", Toast.LENGTH_SHORT);
                                    boolean check = dbAdapter.deleteOfflineCashTicket(Long.parseLong(id));
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
            return null;

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
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.b7);
                        mediaPlayer.start();
                        changeLayout(true);
                    } else {

                        failTicket = (RelativeLayout) findViewById(R.id.containerfail);
                        failTicket.setVisibility(View.VISIBLE);
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.b7);
                        mediaPlayer.start();
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