package com.example.gbts.navigationdraweractivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.activity.LoginActivity;
import com.example.gbts.navigationdraweractivity.asyntask.FireBaseIDTask;
import com.example.gbts.navigationdraweractivity.fragment.CreditCard;
import com.example.gbts.navigationdraweractivity.fragment.FragmentDirection;
import com.example.gbts.navigationdraweractivity.fragment.GetAllButRoute;
import com.example.gbts.navigationdraweractivity.fragment.GetReport;
import com.example.gbts.navigationdraweractivity.fragment.GmapFragment;
import com.example.gbts.navigationdraweractivity.fragment.MainContent;
import com.example.gbts.navigationdraweractivity.fragment.Profile;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.example.gbts.navigationdraweractivity.utils.Utility;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String PREFS_NAME = "mypre";
    private final String PREF_USERNAME = "username";
    private final String PREF_PASSWORD = "password";
    private final String PREF_REMEMBER = "check";

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    FloatingActionButton fab, fab_search, fab_direction;
    Animation FabOpen, FabClose, FabClockwise, FabantiClockwise;
    boolean isOpen = false;
    //Duc
    String hostAddress = "https://grinbuz.com";

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

            String strURL = hostAddress + "/Api/ActivateAccountByApp?key=gbts_2016_capstone&cardId=" + cardId + "&phone=" + phone;

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
            String message = "";
            //check success
            try {
                success = jsonObject.getBoolean("success");
                message = jsonObject.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (success) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }

    //End
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //NFC Duc
        adapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};


        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Xe bus thông minh");
        setSupportActionBar(toolbar);


        //SHARED PREFERENCES PHONE NUMBER
        SharedPreferences preferences = getSharedPreferences("Info", MODE_PRIVATE);
        String phoneInfo = preferences.getString("Phonenumber", "Chào mừng bạn đến với thế giới hệ thống xe bus thông minh!!");


        //NOTIFICATION
        FirebaseMessaging.getInstance().subscribeToTopic("GBTS");
        String token = FirebaseInstanceId.getInstance().getToken();
        try {
            new FireBaseIDTask().execute(phoneInfo, token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //START FRAGMENT MAIN && INTEGRATION FB, PROMOTION
        if (savedInstanceState == null) {
            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = MainContent.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }

        // FloatingAction Button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);
        fab_direction = (FloatingActionButton) findViewById(R.id.fab_direction);

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
                    isOpen = false;

                } else {
                    fab_search.startAnimation(FabOpen);
                    fab_direction.startAnimation(FabOpen);
                    fab.startAnimation(FabClockwise);
                    fab_search.setClickable(true);
                    fab_direction.setClickable(true);
                    isOpen = true;
                }
            }
        });
        //Show Fragment Direction Google map
        final FragmentManager manager = getFragmentManager();
        final FragmentDirection direction = new FragmentDirection();
        final GetAllButRoute getAllButRoute = new GetAllButRoute();

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
                getAllButRoute.show(manager, "Fragment Get Busroute");
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        ReadModeOn();

        if (intent.getExtras() != null) {

            String check = intent.getStringExtra("afterPay");
            if (check != null) {
                Fragment fragment = null;
                Class fragmentClass = null;
                fragmentClass = CreditCard.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_logout) {

            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String prePhone = sharedPreferences.getString(PREF_USERNAME, "");
            Log.d("test1 ", "prePhone " + prePhone);
            String prePass = sharedPreferences.getString(PREF_PASSWORD, "");
            Log.d("test1 ", "prePass " + prePass);
            String checkedBox = sharedPreferences.getString(PREF_REMEMBER, "");
            Log.d("test1 ", "checkedBox " + checkedBox);


            Bundle bundle = new Bundle();
            bundle.putString("rememberPhone", prePhone);
            bundle.putString("rememberPass", prePass);
            bundle.putString("rememberChecked", checkedBox);

            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            // Create a new fragment and specify the fragment to show based on nav item clicked
            Fragment fragment = new Fragment();
            Class fragmentClass = null;
            switch (id) {
                case R.id.nav_card:
                    toolbar.setTitle("Thẻ của bạn");
                    fragmentClass = CreditCard.class;
                    break;
                case R.id.nav_getReport:
                    toolbar.setTitle("Báo cáo chi tiêu");
                    fragmentClass = GetReport.class;
//                    fragmentClass = FragmentReport.class;
                    break;
                case R.id.nav_profile:
                    toolbar.setTitle("Thông tin cá nhân ");
                    fragmentClass = Profile.class;
                    break;
                case R.id.nav_gmaps:
                    toolbar.setTitle("Bản đồ");
                    fragmentClass = GmapFragment.class;
                    break;
                default:
                    break;
            }
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

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
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }
}
