package com.example.gbts.navigationdraweractivity.fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.gbts.navigationdraweractivity.R;
import com.example.gbts.navigationdraweractivity.activity.ActivityGoogleFindPath;

/**
 * Created by truon on 9/30/2016.
 */

public class FragmentDirection extends DialogFragment {
    EditText edtOrigin, edtDestination;
    Button btnFindPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_direction, container, false);

        edtOrigin = (EditText) view.findViewById(R.id.edtOrigin);
        edtDestination = (EditText) view.findViewById(R.id.edtDestination);

        btnFindPath = (Button) view.findViewById(R.id.btnFindPath);
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityGoogleFindPath.class);
//                intent.putExtra()
            }
        });
        return view;
    }
}
