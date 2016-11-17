package com.example.gbts.navigationdraweractivity.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.constance.Constance;
import com.example.gbts.navigationdraweractivity.utils.JSONParser;
import com.example.gbts.navigationdraweractivity.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by truon on 9/22/2016.
 */

public class Profile extends Fragment {
    final String TAG = "Profile";
    private static final String TAG_FRAGMENT = "Profile";
    String preference = "Info";
    String hostAddress = "https://grinbuz.com";
    Button btUpdate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        if (Utility.isNetworkConnected(getActivity())) {
            new GetProfile().execute();
        } else {
            FragmentDisconnect disconnect = new FragmentDisconnect();
            Bundle bundle = new Bundle();
            bundle.putString("action", "transferProfile");
            disconnect.setArguments(bundle);
            getActivity().getFragmentManager().beginTransaction()
                    .replace(R.id.flContent, disconnect, TAG_FRAGMENT)
                    .addToBackStack(null)
                    .commit();
        }
        //GET preference info
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(preference, Context.MODE_PRIVATE);
        final EditText edtFullName = (EditText) view.findViewById(R.id.edtFullName);
        final EditText edtNewPassword = (EditText) view.findViewById(R.id.edtNewPassword);
        final EditText edtOldPassword = (EditText) view.findViewById(R.id.edtOldPassword);
        final EditText edtConfirmPassword = (EditText) view.findViewById(R.id.edtConfirmPassword);
        final EditText edtMinBalance = (EditText) view.findViewById(R.id.edtGetNotification);
        btUpdate = (Button) view.findViewById(R.id.btnUpdateProfile);


        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utility.isNetworkConnected(getActivity())) {
                    String oldPassword = edtOldPassword.getText().toString().trim();
                    String newPassword = edtNewPassword.getText().toString().trim();
                    String confirmPassword = edtConfirmPassword.getText().toString().trim();
                    String minBalance = edtMinBalance.getText().toString().trim();
                    if (minBalance == "") {
                        edtMinBalance.setText("10000");
                    }
                    if (oldPassword.equals("") && newPassword.equals("") && confirmPassword.equals("")) {
                        String password = sharedPreferences.getString("Password", "");
                        if (minBalance != null) {
                            String[] params = {password, minBalance};
                            Log.d("truongprofile 1 ", "password " + password + "minBalance" + minBalance);
                            new UpdateProfile().execute(params);
                        }
                    } else if (checkPassword(oldPassword, newPassword, confirmPassword)) {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(preference, Context.MODE_PRIVATE);
                        if (oldPassword.equals(sharedPreferences.getString("Password", ""))) {
                            if (minBalance != null) {
                                String[] params = {newPassword, minBalance};
                                Log.d("truongprofile 2 ", "newPassword " + newPassword + "minBalance" + minBalance);
                                new UpdateProfile().execute(params);
                            }
                        }
                    }

                } else {
                    FragmentDisconnect disconnect = new FragmentDisconnect();
                    Bundle bundle = new Bundle();
                    bundle.putString("action", "transferProfile");
                    disconnect.setArguments(bundle);
                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.flContent, disconnect, TAG_FRAGMENT)
                            .addToBackStack(null)
                            .commit();
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
        String phone, fullname, password, balance;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(preference, Context.MODE_PRIVATE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            EditText edtFullname = (EditText) getView().findViewById(R.id.edtFullName);
            TextInputEditText edtBalance = (TextInputEditText) getView().findViewById(R.id.edtGetNotification);

            phone = sharedPreferences.getString("Phonenumber", "");
            fullname = edtFullname.getText().toString().trim();
            balance = edtBalance.getText().toString().trim();


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
            balance = params[1];
            //thay hostAddress thanh grinbuz
            String strURL = null;
            try {
                strURL = Constance.API_UPDATE_PROFILE + "&phone=" + phone + "&fullname=" + URLEncoder.encode(fullname, "UTF-8") + "&password=" + password + "&minBalance=" + balance;
                Log.d("truongprofile1", "strURL " + strURL);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrlPOST(strURL);
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
            if (jsonObject != null) {
                try {
                    success = jsonObject.getBoolean("success");
                    message = jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (success) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Password", password);
                } else {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d(TAG, "JSON OBJECT Null");
            }


        }
    }

    private class GetProfile extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String phone;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(preference, Context.MODE_PRIVATE);
        TextInputEditText edtFullName, edtNewPassword, edtOldPassword, edtConfirmPassword, edtMinBalance;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Cập nhật thông tin ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONParser jParser = new JSONParser();
            phone = sharedPreferences.getString("Phonenumber", "");
            String strURL = Constance.API_GET_PROFILE + "&phone=" + phone;
            Log.d("truongprofile", "strURL " + strURL);

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrlPOST(strURL);
            Log.d(TAG, json.toString());
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            super.onPostExecute(jsonObject);
            // Hide dialog
            pDialog.dismiss();


            edtFullName = (TextInputEditText) getView().findViewById(R.id.edtFullName);
            edtNewPassword = (TextInputEditText) getView().findViewById(R.id.edtNewPassword);
            edtOldPassword = (TextInputEditText) getView().findViewById(R.id.edtOldPassword);
            edtConfirmPassword = (TextInputEditText) getView().findViewById(R.id.edtConfirmPassword);
            edtMinBalance = (TextInputEditText) getView().findViewById(R.id.edtGetNotification);

            boolean success = false;
            String message = "";
            //check success
            if (jsonObject != null) {
                try {
                    success = jsonObject.getBoolean("success");
                    message = jsonObject.getString("message");
                    if (success) {
                        JSONObject object = jsonObject.getJSONObject("data");
                        String fullName = object.getString("Fullname");
                        String mBalance = object.getString("MinBalance");
                        Log.d("mBalance", "mBalance " + mBalance);
                        Log.d("mBalance", "fullName " + fullName);
                        edtFullName.setText(fullName);
                        edtMinBalance.setText(mBalance);
                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "JSON OBJECT Null");
            }
        }
    }
}