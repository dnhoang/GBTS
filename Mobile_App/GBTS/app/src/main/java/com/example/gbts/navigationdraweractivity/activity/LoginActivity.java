package com.example.gbts.navigationdraweractivity.activity;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.MainActivity;
import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.enity.Message;
import com.example.gbts.navigationdraweractivity.fragment.AccountInfo;
import com.example.gbts.navigationdraweractivity.fragment.ActivateAccount;
import com.example.gbts.navigationdraweractivity.fragment.DetailsInfo;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    //define controls
    Message message;
    EditText edtPhone, edtPass;
    Button btnLogin, btnActive;

    //JSON Node Names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        //initalise controle
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnActive = (Button) findViewById(R.id.btnActive);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JSONParse().execute();
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
            }
        });


        final FragmentManager manager = getFragmentManager();
        final ActivateAccount account = new ActivateAccount();
        btnActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account.show(manager, "Activate Account");

            }
        });

    }

    //ASYNC TASK
    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String phone, pwd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            edtPhone = (EditText) findViewById(R.id.edtPhone);
            edtPass = (EditText) findViewById(R.id.edtPassword);
            phone = edtPhone.getText().toString();
            pwd = edtPass.getText().toString();

            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jParser = new JSONParser();

            String strURL = Constance.API_LOGIN + "&phone=" + phone + "&password=" + pwd;
//            String strURL = Constance.API_LOGIN + "&phone=01212184802&password=123456";

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(strURL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            // Hide dialog
            pDialog.dismiss();
            //check success
            boolean success = jsonObject.optBoolean(TAG_SUCCESS);

            if (success) {
                SharedPreferences preferences = getSharedPreferences("Info", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Phonenumber", phone);
                editor.commit();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Sai điện thoại hoặc mật khẩu!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
