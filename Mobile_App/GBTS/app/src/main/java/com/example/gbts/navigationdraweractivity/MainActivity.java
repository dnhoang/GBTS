package com.example.gbts.navigationdraweractivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.gbts.navigationdraweractivity.activity.LoginActivity;
import com.example.gbts.navigationdraweractivity.asyntask.FireBaseIDTask;
import com.example.gbts.navigationdraweractivity.fragment.AccountInfo;
import com.example.gbts.navigationdraweractivity.fragment.CreditCard;
import com.example.gbts.navigationdraweractivity.fragment.FragmentDirection;
import com.example.gbts.navigationdraweractivity.fragment.GmapFragment;
import com.example.gbts.navigationdraweractivity.fragment.MainContent;
import com.example.gbts.navigationdraweractivity.fragment.Profile;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    FloatingActionButton fab, fab_search, fab_direction;
    Animation FabOpen, FabClose, FabClockwise, FabantiClockwise;
    boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Xe bus thông minh");
        setSupportActionBar(toolbar);

        //NOTIFICATION
        FirebaseMessaging.getInstance().subscribeToTopic("GBTS");
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("MainActivitytq token", token);
        SharedPreferences preferences = getSharedPreferences("Info", MODE_PRIVATE);
        String phoneInfo = preferences.getString("Phonenumber", "Chào mừng bạn đến với thế giới hệ thống xe bus thông minh!!");
        Log.d("MainActivitytq p", phoneInfo);
        new FireBaseIDTask().execute(phoneInfo, token);

        //Set Default first Fragment MainContain
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
        //Show Fragment
        final FragmentManager manager = getFragmentManager();
        final FragmentDirection direction = new FragmentDirection();

        fab_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                direction.show(manager, "FragmentDirection");
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
            Intent newAct = new Intent(this, LoginActivity.class);
            startActivity(newAct);
        } else {
            // Create a new fragment and specify the fragment to show based on nav item clicked
            Fragment fragment = null;
            Class fragmentClass = null;
            switch (id) {
                case R.id.nav_card:
                    toolbar.setTitle("Thẻ của bạn");
                    fragmentClass = CreditCard.class;
                    break;
                case R.id.nav_profile:
                    toolbar.setTitle("Thông tin cá nhân");
                    fragmentClass = Profile.class;
                    break;
                case R.id.nav_user:
                    toolbar.setTitle("Thông tin tài khoản");
                    fragmentClass = AccountInfo.class;
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
