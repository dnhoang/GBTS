package com.example.gbts.navigationdraweractivity.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.activity.LoginActivity;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by truon on 9/22/2016.
 */

public class Profile extends Fragment {
    final String TAG = "Profile";
    String preference = "Info";
    String hostAddress = "https://grinbuz.com";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        final EditText edtFullName = (EditText) view.findViewById(R.id.edtFullName);

        final EditText edtNewPassword = (EditText) view.findViewById(R.id.edtNewPassword);
        final EditText edtOldPassword = (EditText) view.findViewById(R.id.edtOldPassword);
        final EditText edtConfirmPassword = (EditText) view.findViewById(R.id.edtConfirmPassword);
        final Button btUpdate = (Button) view.findViewById(R.id.btnUpdateProfile);

        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(preference, Context.MODE_PRIVATE);
        edtFullName.setText(sharedPreferences.getString("Fullname", ""));
        Log.d(TAG, "fullname" + sharedPreferences.getString("Fullname", ""));

        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = edtOldPassword.getText().toString().trim();
                String newPassword = edtNewPassword.getText().toString().trim();
                String confirmPassword = edtConfirmPassword.getText().toString().trim();
                if (oldPassword.equals("") && newPassword.equals("") && confirmPassword.equals("")) {
                    String password = sharedPreferences.getString("Password", "");
                    String[] params = {password};
                    new UpdateProfile().execute(params);
                } else if (checkPassword(oldPassword, newPassword, confirmPassword)) {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(preference, Context.MODE_PRIVATE);
                    if (oldPassword.equals(sharedPreferences.getString("Password", ""))) {

                        String[] params = {newPassword};
                        new UpdateProfile().execute(params);
                    }

                }
            }
        });

        return view;
    }

    private boolean checkPassword(String oldPassword, String newPassword, String confirmPassword) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(preference, Context.MODE_PRIVATE);
        if (oldPassword.equals(sharedPreferences.getString("Password", ""))) {
            if (newPassword.equals(confirmPassword)) {
                return true;
            } else {
                Toast.makeText(getActivity(), "Mật khẩu mới không trùng khớp", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), "Sai mật khẩu", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private class UpdateProfile extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String phone, fullname, password;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(preference, Context.MODE_PRIVATE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            EditText edtFullname = (EditText) getView().findViewById(R.id.edtFullName);

            phone = sharedPreferences.getString("Phonenumber", "");
            fullname = edtFullname.getText().toString().trim();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Cập nhật thông tin ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jParser = new JSONParser();

            password = params[0];
            //thay hostAddress thanh grinbuz
            String strURL = hostAddress + "/Api/UpdateProfile?key=gbts_2016_capstone&phone=" + phone + "&fullname=" + fullname + "&password=" + password;

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(strURL);
            Log.d(TAG, json.toString());
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
            try {
                success = jsonObject.getBoolean("success");
                message = jsonObject.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (success) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Password", password);
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }


        }
    }
}