package com.example.gbts.navigationdraweractivity.tabhost;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.gbts.navigationdraweractivity.R;

public class TabhostActivity extends AppCompatActivity {
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabhost);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("HÌNH THỨC THANH TOÁN");
        String cardId = "";
        Bundle bundle1 = getIntent().getExtras();
        if (bundle1 != null) {
            //get cardid
            cardId = bundle1.getString("cardIDCreditDetails");
            Bundle bundle = new Bundle();
            bundle.putString("cardIDPaypal", cardId);
            Intent intent = getIntent();
            intent.putExtras(bundle);
        }
        Log.d("haizzz", "cardIDCreditDetails " + cardId);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("NẠP THẺ TRỰC TUYẾN"));
        tabLayout.addTab(tabLayout.newTab().setText("NẠP BẰNG THẺ CÀO"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final ATPagerAdapter adapter = new ATPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
