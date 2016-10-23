package com.example.topupemulator;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {
    String setting = "settingPreference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences sharedPreferences = getSharedPreferences(setting, MODE_PRIVATE);
        EditText edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtPhone.setText(sharedPreferences.getString("staffPhone", ""));

    }

    public void clickToSave(View view) {

        SharedPreferences.Editor editor = getSharedPreferences(setting, MODE_PRIVATE).edit();
        EditText edtPhone = (EditText) findViewById(R.id.edtPhone);
        String phone = edtPhone.getText().toString();
        if (phone.isEmpty()) {
            Toast.makeText(SettingActivity.this, "Vui lòng nhập số điện thoại!", Toast.LENGTH_SHORT).show();
        } else {
            editor.putString("staffPhone", phone);
            editor.commit();
        }
    }
}
