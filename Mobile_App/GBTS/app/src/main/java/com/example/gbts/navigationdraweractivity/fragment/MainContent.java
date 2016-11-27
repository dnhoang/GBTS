package com.example.gbts.navigationdraweractivity.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.activity.BusStopActivity;
import com.example.gbts.navigationdraweractivity.adapter.GoStopAdapter;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.module.google.mapsAPI.BusStop;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.example.gbts.navigationdraweractivity.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by truon on 9/23/2016.
 */

public class MainContent extends Fragment {
    private static final String RESULT_OK = null;
    TextView txtNoti;
    WebView webViewPromotion;
    private static final String TAG_FRAGMENT = "MainContent";
    private String tokensv = "";
    Context context;

    public void setTokensv(String tokensv) {
        this.tokensv = tokensv;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_content, container, false);
        webViewPromotion = (WebView) view.findViewById(R.id.webViewPromotion);
        if (Utility.isNetworkConnected(getActivity())) {
            new AsyncPromotion().execute();
        } else {
            FragmentDisconnect disconnect = new FragmentDisconnect();
            Bundle bundle = new Bundle();
            bundle.putString("action", "transferMainContent");
            disconnect.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flContent, disconnect, TAG_FRAGMENT)
                    .addToBackStack(null)
                    .commit();
        }

        Bundle bundleSend = new Bundle();
        bundleSend.putString("currentContext", "MainContent");
        Intent intent = getActivity().getIntent();
        intent.putExtras(bundleSend);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        txtNoti = (TextView) getView().findViewById(R.id.txtNotification);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Log.d("notificationq ", "not null");
            String body = bundle.getString("notiBody");
            String title = bundle.getString("notiTitle");
            if (body != null) {
                txtNoti.setText(body);
                txtNoti.setBackgroundResource(R.drawable.background_table);
                txtNoti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CreditCard creditCard = new CreditCard();
                        getFragmentManager().beginTransaction()
                                .replace(R.id.flContent, creditCard, TAG_FRAGMENT)
                                .addToBackStack(null)
                                .commit();
                    }
                });
            } else {
                txtNoti.setText("");
            }
        } else {
            txtNoti.setText("");
            Log.d("notificationq ", "null cmnr");
        }
    }

    private class AsyncGetToken extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            super.onPostExecute(jsonObject);
            // Hide dialog

        }
    }


    private class AsyncPromotion extends AsyncTask<String, Void, JSONObject> {
        private ProgressDialog pDialog;
        String url;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Get busCode from Intent
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Vui lòng đợi ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jsonParser = new JSONParser();
            url = Constance.API_GET_PROMOTION;
            JSONObject json = jsonParser.getJSONFromUrlGET(url);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            Log.d("meow1", "url " + url);
            if (jsonObject != null) {
                // Hide dialog
                pDialog.dismiss();
                try {
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        boolean hasPromotion = jsonObject.getBoolean("hasPromotion");
                        if (hasPromotion) {
                            String html = jsonObject.getString("html");
                            Log.d("meow1", "html " + html);

                            WebSettings settings = webViewPromotion.getSettings();
                            settings.setDefaultTextEncodingName("UTF-8");

                            String before = "<html><body>";
                            String after = "</body></html>";
                            webViewPromotion.loadData(before + html + after, "text/html; charset=UTF-8", null);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("Maincontent", "get promotion null ");
            }


        }
    }

}
