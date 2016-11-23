package com.example.gbts.navigationdraweractivity.tabhost;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.gbts.navigationdraweractivity.MainActivity;
import com.example.gbts.navigationdraweractivity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by truon on 11/22/2016.
 */

public class TabHostFragment extends Fragment
        implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener, View.OnTouchListener {
    // Fragment TabHost as mTabHost
    private TabHost mTabHost;
    private ViewPager mViewPager;
    private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabInfo>();
    private PagerAdapter mPagerAdapter;
    Toolbar toolbar;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    /**
     * @author mwho
     *         Maintains extrinsic info of a tab's construct
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
     *
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

        /**
         * (non-Javadoc)
         *
         * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
         */
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabhost_purchase, container, false);
        Log.d("tabvoichahost", "onCreateView " + "TabHostFragment");
        // Initialise the TabHost
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("CHỌN LOẠI DỊCH VỤ");


        mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
        mTabHost.setup();


        TabHostFragment.TabInfo tabInfo = null;
        TabHostFragment.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab1")
                .setIndicator(getTabIndicator(mTabHost.getContext(), "NẠP TRỰC TUYẾN", R.drawable.ic_action_card)), (tabInfo = new TabHostFragment.TabInfo("Tab1", TabHostCreditPlan.class, savedInstanceState)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        TabHostFragment.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab2")
                .setIndicator(getTabIndicator(mTabHost.getContext(), "NẠP BẰNG THẺ CÀO", R.drawable.ic_action_card)), (tabInfo = new TabHostFragment.TabInfo("Tab2", TabHostTopup.class, savedInstanceState)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        // Default to first tab
        //this.onTabChanged("Tab1");
        //
        mTabHost.setOnTabChangedListener(this);
        // Intialise ViewPager
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(Fragment.instantiate(getActivity(), TabHostCreditPlan.class.getName()));
        fragments.add(Fragment.instantiate(getActivity(), TabHostTopup.class.getName()));
        this.mPagerAdapter = new PagerAdapter(getActivity().getSupportFragmentManager(), fragments);
        //
        this.mViewPager = (ViewPager) view.findViewById(R.id.viewpager1);
        this.mViewPager.setAdapter(this.mPagerAdapter);
        this.mViewPager.setOnPageChangeListener(this);

        FloatingActionButton floatingActionButton = ((MainActivity) getActivity()).getFloatingActionButton();
        floatingActionButton.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("tabvoichahost", "onResume " + "TabHostFragment");
    }


    private View getTabIndicator(Context context, String title, int icon) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
//        ImageView iv = (ImageView) view.findViewById(R.id.imgTabhost);
//        iv.setImageResource(icon);
        TextView tv = (TextView) view.findViewById(R.id.txtTabHost);
        tv.setText(title);
//        tv.setTextColor(getResources().getColor(R.color.fab1_color));
        return view;
    }
//    private View getTabIndicatorRed(Context context, String title, int icon) {
//        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
////        ImageView iv = (ImageView) view.findViewById(R.id.imgTabhost);
////        iv.setImageResource(icon);
//        TextView tv = (TextView) view.findViewById(R.id.txtTabHost);
//        tv.setText(title);
//        tv.setTextColor(getResources().getColor(R.color.colorTabHost));
//        return view;
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        CreditCardDetails creditCardDetails = new CreditCardDetails();
//        FragmentManager manager = getFragmentManager();
//        creditCardDetails.show(manager, "Details Account");

//        Bundle bundleSend = new Bundle();
//        bundleSend.putString("currentContext", "CreditCard");
//        Intent intent = getActivity().getIntent();
//        intent.putExtras(bundleSend);

        mTabHost = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
        super.onSaveInstanceState(outState);
    }

    /** (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
     */

    /**
     * Add Tab content to the Tabhost
     *
     * @param activity
     * @param tabHost
     * @param tabSpec
     */
    private static void AddTab(TabHostFragment activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabHostFragment.TabInfo tabInfo) {
        // Attach a Tab view factory to the spec
        tabSpec.setContent(activity.new TabFactory(activity.getActivity()));
        tabHost.addTab(tabSpec);
    }

    /**
     * (non-Javadoc)
     *
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

}
