package com.example.gbts.navigationdraweractivity.tabhost;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.example.gbts.navigationdraweractivity.MainActivity;
import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.tabhost.TabHostCreditPlan;
import com.example.gbts.navigationdraweractivity.tabhost.TabHostTopup;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by truon on 11/20/2016.
 */

public class TabHostPurchase extends FragmentActivity
        implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener{
    // Fragment TabHost as mTabHost
    private TabHost mTabHost;
    private ViewPager mViewPager;
    private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabHostPurchase.TabInfo>();
    private PagerAdapter mPagerAdapter;

    /**
     *
     * @author mwho
     * Maintains extrinsic info of a tab's construct
     */
    private class TabInfo {
        private String tag;
        private Class<?> clss;
        private Bundle args;
        private Fragment fragment;
        TabInfo(String tag, Class<?> clazz, Bundle args) {
            this.tag = tag;
            this.clss = clazz;
            this.args = args;
        }

    }
    /**
     * A simple factory that returns dummy views to the Tabhost
     * @author mwho
     */
    class TabFactory implements TabHost.TabContentFactory {

        private final Context mContext;

        /**
         * @param context
         */
        public TabFactory(Context context) {
            mContext = context;
        }

        /** (non-Javadoc)
         * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
         */
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabhost_purchase);
        // Initialise the TabHost
        this.initialiseTabHost(savedInstanceState);
        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
        }
        // Intialise ViewPager
        this.intialiseViewPager();
    }
    /** (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
     */
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
//        super.onSaveInstanceState(outState);
//    }

    /**
     * Initialise ViewPager
     */
    private void intialiseViewPager() {

        List<Fragment> fragments = new Vector<>();
        fragments.add(Fragment.instantiate(this, Tab1Fragment.class.getName()));
        fragments.add(Fragment.instantiate(this, Tab2Fragment.class.getName()));
        fragments.add(Fragment.instantiate(this, Tab3Fragment.class.getName()));
        this.mPagerAdapter  = new PagerAdapter(super.getSupportFragmentManager(), fragments);
        //
        this.mViewPager = (ViewPager)super.findViewById(R.id.viewpager1);
        this.mViewPager.setAdapter(this.mPagerAdapter);
        this.mViewPager.setOnPageChangeListener(this);
    }

    /**
     * Initialise the Tab Host
     */
    private void initialiseTabHost(Bundle args) {
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TabInfo tabInfo = null;
        TabHostPurchase.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator("Tab 1"), ( tabInfo = new TabInfo("Tab1", Tab1Fragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        TabHostPurchase.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator("Tab 2"), ( tabInfo = new TabInfo("Tab2", Tab2Fragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        TabHostPurchase.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab3").setIndicator("Tab 3"), ( tabInfo = new TabInfo("Tab3", Tab3Fragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        // Default to first tab
        //this.onTabChanged("Tab1");
        //
        mTabHost.setOnTabChangedListener(this);
    }

    /**
     * Add Tab content to the Tabhost
     * @param activity
     * @param tabHost
     * @param tabSpec
     */
    private static void AddTab(TabHostPurchase activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
        // Attach a Tab view factory to the spec
        tabSpec.setContent(activity.new TabFactory(activity));
        tabHost.addTab(tabSpec);
    }

    /** (non-Javadoc)
     * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
     */
    public void onTabChanged(String tag) {
        //TabInfo newTab = this.mapTabInfo.get(tag);
        int pos = this.mTabHost.getCurrentTab();
        this.mViewPager.setCurrentItem(pos);
    }

    /* (non-Javadoc)
     * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled(int, float, int)
     */
    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)
     */
    @Override
    public void onPageSelected(int position) {
        // TODO Auto-generated method stub
        this.mTabHost.setCurrentTab(position);
    }

    /* (non-Javadoc)
     * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrollStateChanged(int)
     */
    @Override
    public void onPageScrollStateChanged(int state) {
        // TODO Auto-generated method stub

    }

//    View view = inflater.inflate(R.layout.tabhost_purchase, container, false);
    //    mTabHost.addTab(mTabHost.newTabSpec("simple").setIndicator("CREDIT PLAN"),
//    TabHostCreditPlan.class, null);
//    mTabHost.addTab(mTabHost.newTabSpec("contacts").setIndicator("TOP UP"),
//    TabHostTopup.class, null);



}
