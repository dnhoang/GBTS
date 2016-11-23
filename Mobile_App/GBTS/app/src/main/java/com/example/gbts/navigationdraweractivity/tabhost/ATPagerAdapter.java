package com.example.gbts.navigationdraweractivity.tabhost;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by truon on 11/23/2016.
 */

public class ATPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ATPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TabHostCreditPlan thCredit = new TabHostCreditPlan();
                return thCredit;
            case 1:
                TabHostTopup thTopup = new TabHostTopup();
                return thTopup;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
