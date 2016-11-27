package com.example.gbts.navigationdraweractivity.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.utils.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by truon on 11/18/2016.
 */

public class FragmentAbout extends Fragment
        implements OnMapReadyCallback {
    private final String TAG_FRAGMENT="FragmentAbout";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        Bundle bundleSend = new Bundle();
        bundleSend.putString("currentContext", "FragmentAbout");
        Intent intent = getActivity().getIntent();
        intent.putExtras(bundleSend);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapAddress);
        fragment.getMapAsync(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Utility.isNetworkConnected(getActivity())) {
            //ASYNC GET TOKEN SERVER API
            //START FRAGMENT MAIN && INTEGRATION FB, PROMOTION
            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = MainContent.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("action", "transferMainContent");
                fragment.setArguments(bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            try {
                FragmentDisconnect disconnect = new FragmentDisconnect();
                Bundle bundle = new Bundle();
                bundle.putString("action", "MainContent");
                disconnect.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, disconnect, TAG_FRAGMENT)
                        .addToBackStack(null)
                        .commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        int height = 100;
        int width = 70;
        BitmapDrawable icLocation = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_location3);
        Bitmap location = icLocation.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(location, width, height, false);

        LatLng marker = new LatLng(10.852830, 106.629542);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 16));
        googleMap.addMarker(new MarkerOptions()
                .title("Trường Đại học FPT, TP. Hồ Chí Minh")
                .position(marker)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
    }


}
