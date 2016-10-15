package com.example.gbts.navigationdraweractivity.module.google.mapsAPI;


import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by truon on 10/13/2016.
 */

public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}
