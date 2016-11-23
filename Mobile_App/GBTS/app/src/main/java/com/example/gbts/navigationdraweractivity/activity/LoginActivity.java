package com.example.gbts.navigationdraweractivity.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.MainActivity;
import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.example.gbts.navigationdraweractivity.utils.Utility;

import org.json.JSONObject;
import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "LoginActivity";
    Context context;
    //define controls
    EditText edtPhone, edtPass;
    CheckBox checkBox;
    Button btnLogin, btnActive;
    ImageView imgLogin;
    TextView txtActive;
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
        SharedPreferences sharedPreferences = getSharedPreferences("Info", MODE_PRIVATE);
        boolean isLoggedout = sharedPreferences.getBoolean("isLogout", true);
        if (isLoggedout == false) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        getSupportActionBar().hide();

        //HOST NAME INITIAL
        Constance constance = new Constance();
        constance.HostString(getApplicationContext());

        //ICON NAVIGATION SET HOST NAME
        final ImageView[] nav_icon = {(ImageView) findViewById(R.id.nav_icon)};
        nav_icon[0].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final String[] m_Text = {""};
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("What do you want?");

                // Set up the input
                final EditText input = new EditText(LoginActivity.this);
                String textValue = getSharedPreferences("Info", MODE_PRIVATE).getString("HostName", "https://ginbuz.netx");
                if (textValue != null && textValue != "") {
                    input.setText(textValue);
                }
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text[0] = input.getText().toString().trim();
                        getSharedPreferences("Info", MODE_PRIVATE).edit().putString("HostName", m_Text[0]).commit();
                        Constance constance = new Constance();
                        constance.HostString(getApplicationContext());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

                return false;
            }
        });


        //initalise controle
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtActive = (TextView) findViewById(R.id.btnActive);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getBaseContext(), MainActivity.class));
                if (Utility.isNetworkConnected(LoginActivity.this)) {
                    new JSONParse().execute();
                } else {
                    // custom dialog
                    final Dialog dialog = new Dialog(LoginActivity.this);
                    dialog.setContentView(R.layout.custom_dialog_login);
                    dialog.setTitle("Mất kết nối mạng ...");

                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogBtnOK);
                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Utility.isNetworkConnected(LoginActivity.this)) {
                                dialog.dismiss();
                                new JSONParse().execute();
                            }
                        }
                    });

                    Button dialogCancel = (Button) dialog.findViewById(R.id.dialogBtnCancel);
                    // if button is clicked, close the custom dialog
                    dialogCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });
                    dialog.show();
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtPass = (EditText) findViewById(R.id.edtPassword);
        checkBox = (CheckBox) findViewById(R.id.saveLoginCheckBox);
        SharedPreferences sharedPreferences = getSharedPreferences("Info", MODE_PRIVATE);
        boolean isLogout = sharedPreferences.getBoolean("isLogout", true);
        if (isLogout == false) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
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

    public void rememberMe(String user, String password) {
        //save username and password in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        sharedPreferences.edit()
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
        SharedPreferences preferences = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            edtPhone = (EditText) findViewById(R.id.edtPhone);
            edtPass = (EditText) findViewById(R.id.edtPassword);
            checkBox = (CheckBox) findViewById(R.id.saveLoginCheckBox);
            phone = edtPhone.getText().toString();
            pwd = edtPass.getText().toString();

            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Vui lòng đợi giây lát ...");
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
            if (Utility.isNetworkConnected(LoginActivity.this)) {
                JSONObject json = jParser.getJSONFromUrlPOST(strURL);
                return json;
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            JSONObject data = null;
            // Hide dialog
            pDialog.dismiss();
            //check success
            if (jsonObject != null) {
                boolean success = jsonObject.optBoolean(TAG_SUCCESS);
                if (success) {
                    try {
                        data = jsonObject.getJSONObject("data");
                        String fullname = data.getString("Fullname");
                        Log.d(TAG, "fullname " + fullname);

                        //SharedPreferences
                        preferences = getSharedPreferences("Info", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("Phonenumber", phone);
                        editor.putString("Password", pwd);
                        editor.putBoolean("isLogout", false);
                        editor.putString("Fullname", fullname);
                        editor.commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intentNoti = getIntent();
                    String lgNotiBody = intentNoti.getStringExtra("messageBody");
                    String lgNotiTitle = intentNoti.getStringExtra("messageTile");
                    if (checkBox.isChecked()) {
                        //check box is checked
                        rememberMe(phone, pwd);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("saveAccount", "saveAccount");
                        if (lgNotiBody != null && lgNotiTitle != null) {
                            intent.putExtra("lgNotiBody", lgNotiBody);
                            intent.putExtra("lgNotiTitle", lgNotiTitle);
                        }
                        startActivity(intent);
                    } else {
                        //not check
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        if (lgNotiBody != null && lgNotiTitle != null) {
                            intent.putExtra("lgNotiBody", lgNotiBody);
                            intent.putExtra("lgNotiTitle", lgNotiTitle);
                        }
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Sai điện thoại hoặc mật khẩu!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getBaseContext(), "Server Does Not Exist", Toast.LENGTH_LONG).show();
                Log.d("meow", "jsonObject null ");
            }
        }
    }
}
