package com.example.gbts.navigationdraweractivity.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by ducdmse61486 on 10/7/2016.
 */

public class Utility {
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null) && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
