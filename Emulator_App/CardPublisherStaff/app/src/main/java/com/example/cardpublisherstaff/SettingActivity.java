package com.example.cardpublisherstaff;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import Util.Utility;

public class SettingActivity extends AppCompatActivity {
    String settings = "settingPreference";

    String hostAddress = "";

    private class GetStaffInfo extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String phone;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(SettingActivity.this);
            pDialog.setMessage("Loading data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            Utility jParser = new Utility();
            phone = params[0];
            String strURL = hostAddress + "/Api/GetStaffInfo?key=gbts_2016_capstone&phone=" + phone;

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
            if (jsonObject!=null){
                try {
                    success = jsonObject.getBoolean("success");
                    message = jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (success) {
                    try {
                        JSONObject data = jsonObject.getJSONObject("data");

                        String userId = data.getString("UserId");
                        String phoneNumber = data.getString("PhoneNumber");
                        String fullname = data.getString("Fullname");
                        SharedPreferences sharedPreferences = getSharedPreferences(settings, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("id", userId);
                        editor.putString("phone", phoneNumber);
                        editor.putString("name", fullname);

                        editor.commit();
                        TextView tvName = (TextView) findViewById(R.id.tvName);
                        tvName.setText(fullname);
                        EditText edtPhone=(EditText)findViewById(R.id.edtPhone);
                        edtPhone.setText(phoneNumber);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            }


        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        final SharedPreferences sharedPreferences = getSharedPreferences(settings, MODE_PRIVATE);
        hostAddress=sharedPreferences.getString("host","https://grinbuz.com");
        EditText edtHost=(EditText)findViewById(R.id.edtHost);
        edtHost.setText(hostAddress);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        TextView tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(sharedPreferences.getString("name", "Chưa đăng nhập"));
        EditText edtPhone=(EditText)findViewById(R.id.edtPhone);
        edtPhone.setText(sharedPreferences.getString("phone", "Chưa đăng nhập"));
        Button btSave = (Button) findViewById(R.id.btSave);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNetworkConnected(getApplicationContext())) {
                    EditText edtPhone = (EditText) findViewById(R.id.edtPhone);
                    String phone = edtPhone.getText().toString();
                    EditText edtHost=(EditText)findViewById(R.id.edtHost);
                    String host=edtHost.getText().toString().trim();

                    if (host.equals("")){

                        new GetStaffInfo().execute(phone);

                        edtHost.setText(sharedPreferences.getString("host","https://grinbuz.com"));
                    } else{
                        hostAddress=host;
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("host",host);
                        editor.commit();
                        new GetStaffInfo().execute(phone);
                    }

                } else {
                    Toast.makeText(getApplicationContext(),"Không có kết nối với máy chủ",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
