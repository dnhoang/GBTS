package com.example.gbts.navigationdraweractivity.enity;

/**
 * Created by truon on 10/9/2016.
 */

public class AllBusroutes {
    private int busID;
    private String busCode;
    private String busName;

    public AllBusroutes() {
    }

    public AllBusroutes(int busID, String busCode, String busName) {
        this.busID = busID;
        this.busCode = busCode;
        this.busName = busName;
    }

    public int getBusID() {
        return busID;
    }

    public void setBusID(int busID) {
        this.busID = busID;
    }

    public String getBusCode() {
        return busCode;
    }

    public void setBusCode(String busCode) {
        this.busCode = busCode;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }
}
