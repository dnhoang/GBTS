package com.example.gbts.navigationdraweractivity.enity;

import java.util.List;

/**
 * Created by truon on 10/10/2016.
 */

public class ReportEntity {
    private String rpCardUID;
    private String rpCardName;
    private String rpCount;
    private String rpTotal;
    private List<String> rpFrequently;

    public ReportEntity() {
    }

    public ReportEntity(String rpCardUID, String rpCardName, String rpCount, String rpTotal, List<String> rpFrequently) {
        this.rpCardUID = rpCardUID;
        this.rpCardName = rpCardName;
        this.rpCount = rpCount;
        this.rpTotal = rpTotal;
        this.rpFrequently = rpFrequently;
    }

    public String getRpCardUID() {
        return rpCardUID;
    }

    public void setRpCardUID(String rpCardUID) {
        this.rpCardUID = rpCardUID;
    }

    public String getRpCardName() {
        return rpCardName;
    }

    public void setRpCardName(String rpCardName) {
        this.rpCardName = rpCardName;
    }

    public String getRpCount() {
        return rpCount;
    }

    public void setRpCount(String rpCount) {
        this.rpCount = rpCount;
    }

    public String getRpTotal() {
        return rpTotal;
    }

    public void setRpTotal(String rpTotal) {
        this.rpTotal = rpTotal;
    }

    public List<String> getRpFrequently() {
        return rpFrequently;
    }

    public void setRpFrequently(List<String> rpFrequently) {
        this.rpFrequently = rpFrequently;
    }
}
