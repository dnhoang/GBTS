package com.example.gbts.navigationdraweractivity.module.google.mapsAPI;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by truon on 10/20/2016.
 */

public class Step {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;
    public String html_instructions;
    public List<LatLng> points;
    public String travel_mode;
}
