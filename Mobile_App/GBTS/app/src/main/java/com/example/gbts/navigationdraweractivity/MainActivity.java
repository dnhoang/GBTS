package com.example.gbts.navigationdraweractivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.activity.LoginActivity;
import com.example.gbts.navigationdraweractivity.asyntask.FireBaseIDTask;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.fragment.CreditCard;
import com.example.gbts.navigationdraweractivity.fragment.CreditCardDetails;
import com.example.gbts.navigationdraweractivity.fragment.FragmentAbout;
import com.example.gbts.navigationdraweractivity.fragment.FragmentChooseCard;
import com.example.gbts.navigationdraweractivity.fragment.FragmentDirection;
import com.example.gbts.navigationdraweractivity.fragment.FragmentDisconnect;
import com.example.gbts.navigationdraweractivity.fragment.GetAllBusRoute;
import com.example.gbts.navigationdraweractivity.fragment.GetReport;
import com.example.gbts.navigationdraweractivity.fragment.MainContent;
import com.example.gbts.navigationdraweractivity.fragment.Profile;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.example.gbts.navigationdraweractivity.utils.Utility;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NfcAdapter.CreateNdefMessageCallback {
    static final String keyAES = "ssshhhhhhhhhhh!!!!";

    private final String PREFS_NAME = "mypre";
    private final String PREF_USERNAME = "username";
    private final String PREF_PASSWORD = "password";
    private final String PREF_REMEMBER = "check";
    private static final String TAG_FRAGMENT = "MainActivity";

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    FloatingActionButton fab, fab_search, fab_direction;
    Animation FabOpen, FabClose, FabClockwise, FabantiClockwise;
    boolean isOpen = false;
    boolean refreshCard = false;

    public FloatingActionButton getFloatingActionButton() {
        return fab;
    }

    //NFC Duc
    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag mytag;

    //End NFC Duc
    //Activate NFC card while logged in
    static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }

    public void onPause() {
        super.onPause();
        ReadModeOff();
    }


    private void ReadModeOn() {
        writeMode = true;
        adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }

    private void ReadModeOff() {
        writeMode = false;
        adapter.disableForegroundDispatch(this);
    }


    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

            if (Utility.isNetworkConnected(MainActivity.this)) {

                mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                String cardId = bin2hex(mytag.getId());
                SharedPreferences preferences = getSharedPreferences("Info", MODE_PRIVATE);
                String phone = preferences.getString("Phonenumber", "");
                final String[] params = {cardId, phone};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this);
                alertDialogBuilder
                        .setTitle("Phát hiện thẻ NFC!")
                        .setMessage("Bạn có muốn kích hoạt thẻ này với số điện thoại " + phone + " không?")
                        .setCancelable(false)
                        .setPositiveButton("Kích hoạt", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new ActivateNFCCard().execute(params);
                            }
                        })
                        .setNegativeButton("Thoát",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                //TicketResult ticketResult = new TicketResult();
                //new ActivateNFCCard().execute(params);


            } else {
                Toast.makeText(this, "Không thể kết nối với máy chủ!", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private String tokensv;

    public String getTokensv() {
        return tokensv;
    }

    public void setTokensv(String tokensv) {
        this.tokensv = tokensv;
    }


    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        new AsyncGetToken().execute();

        String tokenApi = getTokensv();
        Log.d("tokenApi ", "tokenApi: " + tokenApi.toString());
        if (tokenApi != null && tokenApi != "") {
            SharedPreferences sharedPreferences = getSharedPreferences("Info", MODE_PRIVATE);
            String message = sharedPreferences.getString("NFCPayment", "");

            String carid_token = message + "@" + tokenApi;
            Log.d("tokenApi ", "carid_token: " + carid_token.toString());
            String encryptcarid_token = Utility.encrypt(carid_token, keyAES);
            Log.d("tokenne ", "encryptcarid_token: " + encryptcarid_token.toString());

            String decryptCardID = Utility.decrypt(encryptcarid_token, keyAES);
            Log.d("ndef1 ", "decryptCardID" + decryptCardID.toString());
            NdefRecord[] records = new NdefRecord[0];
            try {
                records = new NdefRecord[]{Utility.createRecord(encryptcarid_token)};
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            NdefMessage msg = new NdefMessage(records);
            Log.d("ndef1 ", "msg" + msg.toString());
            getSharedPreferences("Info", MODE_PRIVATE).edit().putString("refresh", "successrefresh").commit();
            return msg;
        }
        return null;
    }


    private class ActivateNFCCard extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String cardId, phone;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jParser = new JSONParser();
            cardId = params[0];
            phone = params[1];
            //thay hostAddress thanh grinbuz

            String strURL = Constance.API_ACTIVATE_ACCOUNT + "&cardId=" + cardId + "&phone=" + phone;

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrlPOST(strURL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            super.onPostExecute(jsonObject);
            // Hide dialog
            pDialog.dismiss();
            boolean success = false;
            String message = "";
            //check success
            try {
                success = jsonObject.getBoolean("success");
                message = jsonObject.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (success) {
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }

    //End


    //CHECK CONNECTION
    public static boolean internetConnectionCheck(Activity CurrentActivity) {
        Boolean Connected = false;
        ConnectivityManager connectivity = (ConnectivityManager) CurrentActivity.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) for (int i = 0; i < info.length; i++)
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    Log.e("My Network is: ", "Connected ");
                    Connected = true;
                } else {
                }

        } else {
            Log.e("My Network is: ", "Not Connected");

            Toast.makeText(CurrentActivity.getApplicationContext(),
                    "Please Check Your internet connection",
                    Toast.LENGTH_LONG).show();
            Connected = false;

        }
        return Connected;

    }

    //================================== ON CREATE ===============================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        new AsyncGetToken().execute();

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("XE BUÝT THÔNG MINH");
        setSupportActionBar(toolbar);

        //NFC Duc
        adapter = NfcAdapter.getDefaultAdapter(this);
        adapter.setNdefPushMessageCallback(this, this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};

        //CALL BACK NDEFMESSAGE
        NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
//            Toast.makeText(this, "Sorry this device does not have NFC", Toast.LENGTH_LONG).show();
            return;
        }

        if (!mAdapter.isEnabled()) {
//            Toast.makeText(this, "Please enable NFC via Settings.", Toast.LENGTH_LONG).show();
        }

        mAdapter.setNdefPushMessageCallback(this, this);


        //SHARED PREFERENCES PHONE NUMBER
        SharedPreferences preferences = getSharedPreferences("Info", MODE_PRIVATE);
        String phoneInfo = preferences.getString("Phonenumber", "Chào mừng bạn đến với thế giới hệ thống xe bus thông minh!!");


        // FloatingAction Button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);
        fab_direction = (FloatingActionButton) findViewById(R.id.fab_direction);
        final TextView txtDirection = (TextView) findViewById(R.id.txtDirection);
        final TextView txtSearchRoutes = (TextView) findViewById(R.id.txtSearchRoutes);


        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        FabClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clock);
        FabantiClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlock);

        // Event OnclickListener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen) {
                    fab_search.startAnimation(FabClose);
                    fab_direction.startAnimation(FabClose);
                    fab.startAnimation(FabantiClockwise);
                    fab_search.setClickable(false);
                    fab_direction.setClickable(false);
                    txtDirection.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close));
                    txtSearchRoutes.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close));
                    txtDirection.setVisibility(View.GONE);
                    txtSearchRoutes.setVisibility(View.GONE);
                    isOpen = false;
                } else {
                    fab_search.startAnimation(FabOpen);
                    fab_direction.startAnimation(FabOpen);
                    fab.startAnimation(FabClockwise);
                    fab_search.setClickable(true);
                    fab_direction.setClickable(true);
                    txtDirection.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open));
                    txtSearchRoutes.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open));
                    txtDirection.setVisibility(View.VISIBLE);
                    txtSearchRoutes.setVisibility(View.VISIBLE);
                    isOpen = true;
                }
            }
        });
        //Show Fragment Direction Google map
        final FragmentManager manager = getSupportFragmentManager();
        final FragmentDirection direction = new FragmentDirection();
        final GetAllBusRoute getAllBusRoute = new GetAllBusRoute();

        fab_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                direction.show(manager, "FragmentDirection");
//                Intent intent = new Intent(getApplicationContext(), ActivityGoogleFindPath.class);
//                startActivity(intent);
            }
        });
        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNetworkConnected(MainActivity.this)) {
                    getAllBusRoute.show(manager, "Fragment Get Busroute");
                } else {
                    // custom dialog
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.custom_dialog_login);
                    dialog.setTitle("Mất kết nối mạng ...");

                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogBtnOK);
                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Utility.isNetworkConnected(MainActivity.this)) {
                                dialog.dismiss();
                                getAllBusRoute.show(manager, "Fragment Get Busroute");
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

            }
        });

        // Find our drawer view
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Toggle ActionBarDrawer
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //NavigationView and NavigationItemSelectedListener
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //CHECK INTERNET CONNECTION
        if (Utility.isNetworkConnected(MainActivity.this)) {
            //ASYNC GET TOKEN SERVER API
            new AsyncGetToken().execute();
            //START FRAGMENT MAIN && INTEGRATION FB, PROMOTION
            if (savedInstanceState == null) {
                Fragment fragment = null;
                Class fragmentClass = null;
                fragmentClass = MainContent.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putString("action", "transferMainContent");
                    fragment.setArguments(bundle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment)
                        .addToBackStack(null)
                        .commit();
            }
            //NOTIFICATION FIRE BASE
            FirebaseMessaging.getInstance().subscribeToTopic("GBTS");
            String token = FirebaseInstanceId.getInstance().getToken();
            try {
                Log.d("so dien thoai dc luu", "phone id " + phoneInfo);
                new FireBaseIDTask().execute(phoneInfo, token);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            FragmentDisconnect disconnect = new FragmentDisconnect();
            Bundle bundle = new Bundle();
            bundle.putString("action", "MainContent");
            disconnect.setArguments(bundle);
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flContent, disconnect, TAG_FRAGMENT)
                    .addToBackStack(null)
                    .commit();
        }
    }
    //===================================== ON RESUME ======================================

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Bundle clearParameter = new Bundle();
        ReadModeOn();
        Log.d("truongtqqq ", "onResume");
        if (intent != null) {
            String check = intent.getStringExtra("afterPay");
            String checkChangeCardName = intent.getStringExtra("action");
            String checkTopUp = intent.getStringExtra("TabHostTopUP");
            String checkUpdateBalance = intent.getStringExtra("notiUpdateCard");
            String checkChangeNameCreditDetails = intent.getStringExtra("changeCardNameCallCreditCard");
            String cardIDPaypal = intent.getStringExtra("cardIDPaypal");
            if (checkChangeNameCreditDetails != null) {
                if (checkChangeNameCreditDetails.equals("checkChangeNameCreditDetails")) {
                    Log.d("haizzzz ", "call Back changeCardNameCallCreditCard");
                    Fragment fragment = null;
                    Class fragmentClass = null;
                    fragmentClass = CreditCard.class;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            } else if (checkChangeCardName != null) {
                CreditCardDetails cardDetails = new CreditCardDetails();
                FragmentManager manager = getSupportFragmentManager();
                cardDetails.show(manager, "CreditCardDetails");
            } else if (check != null || cardIDPaypal != null) {
                if (check.equals("afterPay")) {
                    Log.d("truongtqqqq ", "check " + check);
                    Log.d("truongtqqqq ", "cardIDPaypal " + cardIDPaypal);
                    Log.d("haizzzz ", "call Back afterPay");
                    Fragment fragment = null;
                    Class fragmentClass = null;
                    fragmentClass = CreditCard.class;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                        fragment.setArguments(clearParameter);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment)
                                .addToBackStack(null)
                                .commit();
                        CreditCardDetails cardDetails = new CreditCardDetails();
                        Intent intent1 = getIntent();
                        intent1.putExtra("cardIDDetails", cardIDPaypal);
                        FragmentManager manager = getSupportFragmentManager();
                        cardDetails.show(manager, "CreditCardDetails");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } else if (checkTopUp != null) {
                if (checkTopUp.equals("TabHostTopUP")) {
                    clearParameter.putString("TabHostTopUP", "");
                    Log.d("haizzzz ", "call Back TabHostTopUP");
                    Fragment fragment = null;
                    Class fragmentClass = null;
                    fragmentClass = CreditCard.class;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment)
                                .addToBackStack(null)
                                .commit();
//                        CreditCardDetails cardDetails = new CreditCardDetails();
//                        Intent intent1 = getIntent();
//                        intent1.putExtra("cardIDDetails", cardIDPaypal);
//                        FragmentManager manager = getSupportFragmentManager();
//                        cardDetails.show(manager, "CreditCardDetails");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } else if (checkUpdateBalance != null) {
                Log.d("notiUpdateCard  ", " onResume not null");
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder
                        .setTitle("Mua vé thành công!")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Bundle bundleCreditCard = getIntent().getExtras();
                                        if (bundleCreditCard != null) {
                                            String checkTransfer = bundleCreditCard.getString("currentContext");
                                            if (checkTransfer != null) {
                                                if (checkTransfer.equals("CreditCard")) {
                                                    Fragment fragment = null;
                                                    Class fragmentClass = null;
                                                    fragmentClass = CreditCard.class;
                                                    try {
                                                        fragment = (Fragment) fragmentClass.newInstance();
                                                        FragmentManager fragmentManager = getSupportFragmentManager();
                                                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment)
                                                                .addToBackStack(null)
                                                                .commit();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }

                                        }
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }

            //LOGIN GET INFO NOTIFICATION
            String body = intent.getStringExtra("messageBody");
            String titlte = intent.getStringExtra("messageTile");
            if (body != null && titlte != null) {
                Log.d("truongne1 ", body);
                Log.d("truongne1 ", titlte);
                Fragment fragment = null;
                Class fragmentClass = null;
                fragmentClass = MainContent.class;
                Bundle bundle = new Bundle();
                bundle.putString("notiBody", body);
                bundle.putString("notiTitle", titlte);

                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                    fragment.setArguments(bundle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment)
                        .addToBackStack(null)
                        .commit();
            }
            //NOT LOGIN GET INFO NOTIFICATION
            String bodyNoLogin = intent.getStringExtra("lgNotiBody");
            String titlteNoLogin = intent.getStringExtra("lgNotiTitle");
            if (bodyNoLogin != null && titlteNoLogin != null) {
                Log.d("truonglg2 ", bodyNoLogin);
                Log.d("truongne2 ", titlteNoLogin);
                Fragment fragment = null;
                Class fragmentClass = null;
                fragmentClass = MainContent.class;
                Bundle bundle = new Bundle();
                bundle.putString("notiBody", bodyNoLogin);
                bundle.putString("notiTitle", titlteNoLogin);
                bundle.putString("action", "transferMainContent");
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                    fragment.setArguments(bundle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        }

    }

    @Override
    public void onBackPressed() {
        try{
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d("MainActive", "onbackPRess");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Class fragmentClass = null;
        Bundle bundle = new Bundle();

        Fragment fragment = new Fragment();
        FragmentManager fragmentManager = getSupportFragmentManager();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_creditCard) {
            toolbar.setTitle("THẺ CỦA TÔI");
            if (Utility.isNetworkConnected(MainActivity.this)) {
                fragmentClass = CreditCard.class;
                bundle.putString("fragment", "CreditCard");
            } else {
                fragmentClass = FragmentDisconnect.class;
                bundle.putString("action", "transferCreditCard");
            }
        } else if (id == R.id.action_Report) {
            toolbar.setTitle("BÁO CÁO CHI TIÊU");
            if (Utility.isNetworkConnected(MainActivity.this)) {
                fragmentClass = GetReport.class;
                bundle.putString("fragment", "GetReport");
            } else {
                fragmentClass = FragmentDisconnect.class;
                bundle.putString("action", "transferGetReport");
            }
        } else if (id == R.id.action_Profile) {
            toolbar.setTitle("TÀI KHOẢN");
            if (Utility.isNetworkConnected(MainActivity.this)) {
                fragmentClass = Profile.class;
                bundle.putString("fragment", "Profile");
            } else {
                fragmentClass = FragmentDisconnect.class;
                bundle.putString("action", "transferProfile");
            }
        } else if (id == R.id.action_Logout) {
            boolean isLogout = true;
            SharedPreferences sharedPreferences = getSharedPreferences("Info", MainActivity.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean("isLogout", isLogout).commit();
            Intent intent = getIntent();
            if (intent.getStringExtra("saveAccount") != null) {
                SharedPreferences shareSaveAccount = getSharedPreferences(PREFS_NAME, MainActivity.MODE_PRIVATE);
                String save = intent.getStringExtra("saveAccount");
                Log.d("test1 ", "save " + save);
                if (save != null) {

                    String prePhone = shareSaveAccount.getString(PREF_USERNAME, "");
                    Log.d("test1 ", "prePhone " + prePhone);
                    String prePass = shareSaveAccount.getString(PREF_PASSWORD, "");
                    Log.d("test1 ", "prePass " + prePass);
                    String checkedBox = shareSaveAccount.getString(PREF_REMEMBER, "");
                    Log.d("test1 ", "checkedBox " + checkedBox);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("rememberPhone", prePhone);
                    bundle1.putString("rememberPass", prePass);
                    bundle1.putString("rememberChecked", checkedBox);

                    Intent intent1 = new Intent(this, LoginActivity.class);
                    intent1.putExtras(bundle1);
                    startActivity(intent1);
                    finish();
                }
            } else {
                Log.d("saveaccount", "not save not check");
                Log.d("MainActivityclear2", "Clear get share preference");
                Intent intent1 = new Intent(this, LoginActivity.class);
                startActivity(intent1);
            }
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment)
                .addToBackStack(null)
                .commit();
//        fragmentManagerTab.beginTransaction().remove(fragmentTab).commit();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Class fragmentClass = null;
        Bundle bundle = new Bundle();
        Bundle checkContext = getIntent().getExtras();

        Fragment fragment = new Fragment();
        final FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_logout) {
            boolean isLogout = true;
            SharedPreferences sharedPreferences = getSharedPreferences("Info", MainActivity.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean("isLogout", isLogout).commit();
            Intent intent = getIntent();
            if (intent.getStringExtra("saveAccount") != null) {
                SharedPreferences shareSaveAccount = getSharedPreferences(PREFS_NAME, MainActivity.MODE_PRIVATE);
                String save = intent.getStringExtra("saveAccount");
                Log.d("test1 ", "save " + save);
                if (save != null) {

                    String prePhone = shareSaveAccount.getString(PREF_USERNAME, "");
                    Log.d("test1 ", "prePhone " + prePhone);
                    String prePass = shareSaveAccount.getString(PREF_PASSWORD, "");
                    Log.d("test1 ", "prePass " + prePass);
                    String checkedBox = shareSaveAccount.getString(PREF_REMEMBER, "");
                    Log.d("test1 ", "checkedBox " + checkedBox);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("rememberPhone", prePhone);
                    bundle1.putString("rememberPass", prePass);
                    bundle1.putString("rememberChecked", checkedBox);

                    Intent intent1 = new Intent(this, LoginActivity.class);
                    intent1.putExtras(bundle1);
                    startActivity(intent1);
                    finish();
                }
            } else {
                Log.d("saveaccount", "not save not check");
                Log.d("MainActivityclear2", "Clear get share preference");
                Intent intent1 = new Intent(this, LoginActivity.class);
                startActivity(intent1);
            }
        } //End If Log Out
        else {
            // Create a new fragment and specify the fragment to show based on nav item clicked

            switch (id) {
                case R.id.nav_card:
                    toolbar.setTitle("THẺ CỦA TÔI");
                    if (Utility.isNetworkConnected(MainActivity.this)) {
                        fragmentClass = CreditCard.class;
                        bundle.putString("fragment", "CreditCard");
                    } else {
                        fragmentClass = FragmentDisconnect.class;
                        bundle.putString("action", "transferCreditCard");
                    }
                    break;
                case R.id.nav_getReport:
                    toolbar.setTitle("BÁO CÁO CHI TIÊU");
                    if (Utility.isNetworkConnected(MainActivity.this)) {
                        fragmentClass = GetReport.class;
                        bundle.putString("fragment", "GetReport");
                    } else {
                        fragmentClass = FragmentDisconnect.class;
                        bundle.putString("action", "transferGetReport");
                    }
                    break;
                case R.id.nav_profile:
                    toolbar.setTitle("TÀI KHOẢN");
                    if (Utility.isNetworkConnected(MainActivity.this)) {
                        fragmentClass = Profile.class;
                        bundle.putString("fragment", "Profile");
                    } else {
                        fragmentClass = FragmentDisconnect.class;
                        bundle.putString("action", "transferProfile");
                    }
                    break;
//                case R.id.nav_choose_card:
//                    toolbar.setTitle("MUA VÉ BẰNG ĐIỆN THOẠI");
//                    if (Utility.isNetworkConnected(MainActivity.this)) {
//                        fragmentClass = FragmentChooseCard.class;
//                        bundle.putString("fragment", "FragmentChooseCard");
//                    } else {
//                        fragmentClass = FragmentDisconnect.class;
//                        bundle.putString("action", "transferFragmentChooseCard");
//                    }
//                    break;
                case R.id.nav_search_routes:
                    final GetAllBusRoute busRoute = new GetAllBusRoute();
                    if (Utility.isNetworkConnected(MainActivity.this)) {
                        if (checkContext != null) {
                            String checkFragment = checkContext.getString("currentContext");
                            Log.d("prothayon", "checkFragment " + checkFragment);
                            if (checkContext.getString("currentContext").equals("CreditCard")) {
                                fragmentClass = CreditCard.class;
                            } else if (checkContext.getString("currentContext").equals("MainContent")) {
                                fragmentClass = MainContent.class;
                            } else if (checkContext.getString("currentContext").equals("GetReport")) {
                                fragmentClass = GetReport.class;
                            } else if (checkContext.getString("currentContext").equals("Profile")) {
                                fragmentClass = Profile.class;
                            } else if (checkContext.getString("currentContext").equals("FragmentAbout")) {
                                fragmentClass = FragmentAbout.class;
                            }
                        }
                        busRoute.show(fragmentManager, "GetAllBusRoute");
                    } else {
                        fragmentClass = FragmentDisconnect.class;
                        bundle.putString("action", "transferMainContent");
                        // custom dialog
                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.custom_dialog_login);
                        dialog.setTitle("Mất kết nối mạng ...");

                        Button dialogButton = (Button) dialog.findViewById(R.id.dialogBtnOK);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Utility.isNetworkConnected(MainActivity.this)) {
                                    dialog.dismiss();
                                    busRoute.show(fragmentManager, "GetAllBusRoute");
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
                    break;
                case R.id.nav_find_direction:
                    final FragmentDirection direction = new FragmentDirection();
                    if (Utility.isNetworkConnected(getApplicationContext())) {
                        if (checkContext != null) {
                            String checkFragment = checkContext.getString("currentContext");
                            Log.d("prothayon", "checkFragment " + checkFragment);
                            if (checkContext.getString("currentContext").equals("CreditCard")) {
                                fragmentClass = CreditCard.class;
                            } else if (checkContext.getString("currentContext").equals("MainContent")) {
                                fragmentClass = MainContent.class;
                            } else if (checkContext.getString("currentContext").equals("GetReport")) {
                                fragmentClass = GetReport.class;
                            } else if (checkContext.getString("currentContext").equals("Profile")) {
                                fragmentClass = Profile.class;
                            } else if (checkContext.getString("currentContext").equals("FragmentAbout")) {
                                fragmentClass = FragmentAbout.class;
                            }
                        }
                        direction.show(fragmentManager, "FragmentDirection");
                    } else {
                        fragmentClass = FragmentDisconnect.class;
                        bundle.putString("action", "transferMainContent");
                        // custom dialog
                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.custom_dialog_login);
                        dialog.setTitle("Mất kết nối mạng ...");

                        Button dialogButton = (Button) dialog.findViewById(R.id.dialogBtnOK);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Utility.isNetworkConnected(MainActivity.this)) {
                                    dialog.dismiss();
                                    direction.show(fragmentManager, "FragmentDirection");
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
                    break;
                case R.id.nav_about:
                    toolbar.setTitle("LIÊN HỆ");
                    fragmentClass = FragmentAbout.class;
                    bundle.putString("fragment", "FragmentAbout");
                    break;
                default:
                    break;
            }
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                fragment.setArguments(bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Insert the fragment by replacing any existing fragment
            fragmentManager.beginTransaction()
                    .replace(R.id.flContent, fragment)
                    .addToBackStack(null)
                    .commit();
        }


        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());
        // Close the navigation drawer

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void clickToHome(View view) {
        ImageView imageView = (ImageView) findViewById(R.id.nav_icon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar.setTitle("Xe bus thông minh");
                Fragment fragment = null;
                Class fragmentClass = null;
                fragmentClass = MainContent.class;
                try {
                    Intent intent = getIntent();
                    if (intent != null) {
                        String body = intent.getStringExtra("messageBody");
                        String title = intent.getStringExtra("messageTile");
                        if (body != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("bodyMsg", body);
                            bundle.putString("titleMsg", title);
                            bundle.putString("action", "transferMainContent");
                            Log.d("bodymess", body);
                            MainContent mainContent = new MainContent();
                            mainContent.setArguments(bundle);
                        }
                    }
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment)
                        .addToBackStack(null)
                        .commit();
                drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    private class AsyncGetToken extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jParser = new JSONParser();

            String strURL = Constance.API_GET_TOKEN;

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrlPOST(strURL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            super.onPostExecute(jsonObject);
            // Hide dialog
            boolean success = false;
            String message = "";
            //check success
            if (jsonObject != null) {
                try {
                    success = jsonObject.getBoolean("success");
                    if (success) {
                        String token = jsonObject.getString("token");
                        setTokensv(token);
                        Log.d("gettoken ", token);
                        getSharedPreferences("Info", MODE_PRIVATE).edit()
                                .putString("token", token).commit();
                        String token1 = getSharedPreferences("Info", MODE_PRIVATE).getString("token", "");
                        Log.d("tokensv ", "token1" + token1);
                    } else {
                        message = jsonObject.getString("message");
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("MainActivity ", "jsonObject get token is null ");
            }
        }
    }

}
