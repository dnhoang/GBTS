package com.example.gbts.navigationdraweractivity.enity;

/**
 * Created by truon on 10/9/2016.
 */

public class AllBusroutes {
    private String busCode;
    private String busName;

    public AllBusroutes() {
    }

    public AllBusroutes(String busCode, String busName) {
        this.busCode = busCode;
        this.busName = busName;
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

    @Override
    public String toString() {
        return "AllBusroutes{" +
                "busCode='" + busCode + '\'' +
                ", busName='" + busName + '\'' +
                '}';
    }
}
