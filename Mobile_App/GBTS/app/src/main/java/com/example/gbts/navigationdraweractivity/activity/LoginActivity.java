package com.example.gbts.navigationdraweractivity.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.MainActivity;
import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.enity.Message;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "LoginActivity";
    //define controls
    EditText edtPhone, edtPass;
    CheckBox checkBox;
    Button btnLogin, btnActive;
    private final String PREFS_NAME = "mypre";
    private final String PREF_USERNAME = "username";
    private final String PREF_PASSWORD = "password";
    private final String PREF_REMEMBER = "check";


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
//                startActivity(new Intent(getBaseContext(), MainActivity.class));
                new JSONParse().execute();
//                doLogin();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("truongtest", "onResume");
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtPass = (EditText) findViewById(R.id.edtPassword);
        checkBox = (CheckBox) findViewById(R.id.saveLoginCheckBox);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            String phone = intent.getExtras().getString("rememberPhone");
            Log.d("truongtest", "phone " + phone);
            String pass = intent.getExtras().getString("rememberPass");
            Log.d("truongtest", "pass " + pass);
            String check = intent.getExtras().getString("rememberChecked");
            Log.d("truongtest", "check " + check);
            if (phone != null && pass != null && check != null) {
                edtPhone.setText(phone);
                edtPass.setText(pass);
                //Set checkBox
                checkBox.setChecked(!checkBox.isChecked());
            }
        }
    }

    public void doLogin() {

        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtPass = (EditText) findViewById(R.id.edtPassword);
        checkBox = (CheckBox) findViewById(R.id.saveLoginCheckBox);

        if (checkBox.isChecked()) {
            SharedPreferences sharedPreferences = getSharedPreferences("Info", Context.MODE_PRIVATE);
            String usernameAPI = sharedPreferences.getString("Phonenumber", "");
            String passwordAPI = sharedPreferences.getString("Password", "");

            if (edtPhone.getText().toString().equals(usernameAPI) && edtPass.getText().toString().equals(passwordAPI)) {
                rememberMe(usernameAPI, passwordAPI); //save username and password
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void rememberMe(String user, String password) {
        //save username and password in SharedPreferences
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(PREF_USERNAME, user)
                .putString(PREF_PASSWORD, password)
                .putString(PREF_REMEMBER, "checked")
                .commit();
    }


    public void clickToActivateCard(View view) {
        Intent intent = new Intent(this, ActivateCardActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

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
            checkBox = (CheckBox) findViewById(R.id.saveLoginCheckBox);
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

            String strURL = Constance.API_LOGIN + "&phone=" + phone.trim() + "&password=" + pwd.trim();
//            String strURL = Constance.API_LOGIN + "&phone=01212184802&password=123456";
            Log.d("meow", "url " + strURL);

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
            Log.d(TAG, "jsonObject " + jsonObject.toString());
            if (success) {
                JSONObject data = null;
                try {
                    data = jsonObject.getJSONObject("data");
                    String fullname = data.getString("Fullname");
                    Log.d(TAG, "fullname " + fullname);
                    SharedPreferences preferences = getSharedPreferences("Info", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("Phonenumber", phone);
                    editor.putString("Password", pwd);
                    editor.putString("Fullname", fullname);
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (checkBox.isChecked()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    rememberMe(phone, pwd);
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Sai điện thoại hoặc mật khẩu!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
