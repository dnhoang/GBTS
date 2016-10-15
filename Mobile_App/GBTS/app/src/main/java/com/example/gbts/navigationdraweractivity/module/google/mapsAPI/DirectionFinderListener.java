package com.example.gbts.navigationdraweractivity.module.google.mapsAPI;

import java.util.List;

/**
 * Created by truon on 10/13/2016.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> routes);
}
