package com.example.emulator;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by ducdmse61486 on 9/29/2016.
 */

public class SettingFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//---load the preferences from an XML file---
        addPreferencesFromResource(R.xml.setting_preferences);

    }
}
