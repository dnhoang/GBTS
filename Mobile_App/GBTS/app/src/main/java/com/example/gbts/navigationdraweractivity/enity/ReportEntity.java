package com.example.gbts.navigationdraweractivity.enity;

/**
 * Created by truon on 10/10/2016.
 */

public class ReportEntity {
    private String rpBoughtDated;
    private String rpBusCode;
    private String rpCardName;
    private String rpTotal;

    public ReportEntity() {
    }

    public ReportEntity(String rpBoughtDated, String rpBusCode, String rpCardName, String rpTotal) {
        this.rpBoughtDated = rpBoughtDated;
        this.rpBusCode = rpBusCode;
        this.rpCardName = rpCardName;
        this.rpTotal = rpTotal;
    }

    public String getRpBoughtDated() {
        return rpBoughtDated;
    }

    public void setRpBoughtDated(String rpBoughtDated) {
        this.rpBoughtDated = rpBoughtDated;
    }

    public String getRpBusCode() {
        return rpBusCode;
    }

    public void setRpBusCode(String rpBusCode) {
        this.rpBusCode = rpBusCode;
    }

    public String getRpCardName() {
        return rpCardName;
    }

    public void setRpCardName(String rpCardName) {
        this.rpCardName = rpCardName;
    }

    public String getRpTotal() {
        return rpTotal;
    }

    public void setRpTotal(String rpTotal) {
        this.rpTotal = rpTotal;
    }
}
