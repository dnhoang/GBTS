package com.example.gbts.navigationdraweractivity.constance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.MainActivity;
import com.example.gbts.navigationdraweractivity.activity.LoginActivity;
import com.example.gbts.navigationdraweractivity.application.MyApplication;

/**
 * Created by truon on 10/3/2016.
 */

public class Constance {


    public static String SERVER_API = "https://grinbuz.net/";
    public static String KEY_API = "gbts_2016_capstone";

    public static String API_LOGIN =
            SERVER_API + "Api/Login?key=" + KEY_API;
    public static String API_NOTIFICATION =
            SERVER_API + "Api/RegisterNotificationToken?key=gbts_2016_capstone";
    public static String API_GET_CARD_INFO =
            SERVER_API + "Api/GetCardInfo?key=" + KEY_API;
    public static String API_GET_ALL_CARD =
            SERVER_API + "Api/GetAllCard?key=" + KEY_API;
    public static String API_GET_CREDIT_PLAN =
            SERVER_API + "Api/GetAllCreditPlan?key=" + KEY_API;
    public static String API_GET_ADD_CARD_BALANCE =
            SERVER_API + "Api/AddCardBalance?key=" + KEY_API;
    public static String API_GET_ALL_BUS_ROUTES =
            SERVER_API + "Api/GetAllBusRoutes?key=" + KEY_API;
    public static String API_GET_REPORT =
            SERVER_API + "Api/GetReport?key=" + KEY_API;
    public static String API_GET_BUS_STOP =
            SERVER_API + "Api/GetBusStop?key=" + KEY_API;
    public static String API_CHANGE_CARD_NAME =
            SERVER_API + "Api/ChangeCardName?key=" + KEY_API;
    public static String API_GET_TOKEN =
            SERVER_API + "Api/RequestSellTicketToken?key=" + KEY_API;
    public static String API_GET_PROMOTION =
            SERVER_API + "Api/GetPromotion?key=" + KEY_API;
    public static String API_ACTIVATE_ACCOUNT =
            SERVER_API + "Api/ActivateAccountByApp?key=" + KEY_API;
    public static String API_TOPUP =
            SERVER_API + "Api/Topup?key=" + KEY_API;
    public static String API_GET_PROFILE =
            SERVER_API + "Api/GetProfile?key=" + KEY_API;
    public static String API_UPDATE_PROFILE =
            SERVER_API + "Api/UpdateProfile?key=" + KEY_API;

    public Constance() {
    }

    public void HostString(Context context) {
        SERVER_API = context.getSharedPreferences("Info", Context.MODE_PRIVATE).getString("HostName", "https://grinbuz.netx/");
//        Toast.makeText(context.getApplicationContext(), "SERVER_API " + SERVER_API, Toast.LENGTH_LONG).show();
        Log.d("SERVER_API: ", SERVER_API);

        API_LOGIN =
                SERVER_API + "Api/Login?key=" + KEY_API;
        API_NOTIFICATION =
                SERVER_API + "Api/RegisterNotificationToken?key=gbts_2016_capstone";
        API_GET_CARD_INFO =
                SERVER_API + "Api/GetCardInfo?key=" + KEY_API;
        API_GET_ALL_CARD =
                SERVER_API + "Api/GetAllCard?key=" + KEY_API;
        API_GET_CREDIT_PLAN =
                SERVER_API + "Api/GetAllCreditPlan?key=" + KEY_API;
        API_GET_ADD_CARD_BALANCE =
                SERVER_API + "Api/AddCardBalance?key=" + KEY_API;
        API_GET_ALL_BUS_ROUTES =
                SERVER_API + "Api/GetAllBusRoutes?key=" + KEY_API;
        API_GET_REPORT =
                SERVER_API + "Api/GetReport?key=" + KEY_API;
        API_GET_BUS_STOP =
                SERVER_API + "Api/GetBusStop?key=" + KEY_API;
        API_CHANGE_CARD_NAME =
                SERVER_API + "Api/ChangeCardName?key=" + KEY_API;
        API_GET_TOKEN =
                SERVER_API + "Api/RequestSellTicketToken?key=" + KEY_API;
        API_GET_PROMOTION =
                SERVER_API + "Api/GetPromotion?key=" + KEY_API;
        API_ACTIVATE_ACCOUNT =
                SERVER_API + "Api/ActivateAccountByApp?key=" + KEY_API;
        API_TOPUP =
                SERVER_API + "Api/Topup?key=" + KEY_API;
        API_GET_PROFILE =
                SERVER_API + "Api/GetProfile?key=" + KEY_API;
        API_UPDATE_PROFILE =
                SERVER_API + "Api/UpdateProfile?key=" + KEY_API;
    }


}
