package com.example.gbts.navigationdraweractivity.module.google.mapsAPI;

import java.util.List;

/**
 * Created by truon on 10/27/2016.
 */

public interface DirectionBusRouteListener {
    void onDirectionBusRouteStart();
    void onDirectionBusRouteSuccess(List<BusStop> busStops);
}
