package com.example.busemulator;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;

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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNetworkConnected(getApplicationContext())){
                    Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                    startActivity(intent);
                } else{
                    Toast.makeText(getApplicationContext(),"Vui lòng kiểm tra kết nối!",Toast.LENGTH_LONG).show();
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
                String[] params = {cardId, ticketTypeId, routeCode};
                //TicketResult ticketResult = new TicketResult();
                new VerifyTicket().execute(params);


            } else {
                Toast.makeText(this, "Không thể kết nối với máy chủ!", Toast.LENGTH_SHORT).show();
            }
        }

//Verify ticket Async
    }
    static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }
    private class VerifyTicket extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String cardId, ticketTypeId, routeCode;

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
            String strURL = hostAddress + "/Api/SellTicket?key=gbts_2016_capstone&cardId=" + cardId + "&ticketTypeId=" + ticketTypeId + "&routeCode=" + routeCode;

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(strURL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            final SharedPreferences sharedPreferences=getSharedPreferences(setting,MODE_PRIVATE);
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

                System.out.println("SUCCESS!");
                successTicket = (RelativeLayout) findViewById(R.id.container);
                successTicket.setVisibility(View.VISIBLE);
                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.b7);
                mediaPlayer.start();
                String message = null;
                try {
                    message = jsonObject.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final TextView tvSuccessPrice=(TextView) findViewById(R.id.tvSuccessPrice);
                tvSuccessPrice.setText("Thẻ của bạn đã bị trừ "+sharedPreferences.getString("price","0")+ " đồng");
                changeLayout(true);
            } else {
                failTicket = (RelativeLayout) findViewById(R.id.containerfail);
                failTicket.setVisibility(View.VISIBLE);
                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.b7);
                mediaPlayer.start();

                String message = null;
                try {
                    message = jsonObject.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                changeLayout(false);
            }


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

                }
            }
        };
        timer.start();
    }
    public void onPause() {
        super.onPause();
        ReadModeOff();
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
        ReadModeOn();
    }

    private void ReadModeOn() {
        writeMode = true;
        adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }

    private void ReadModeOff() {
        writeMode = false;
        adapter.disableForegroundDispatch(this);
    }
    //NFC
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
