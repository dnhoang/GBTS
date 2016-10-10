package com.example.gbts.navigationdraweractivity.enity;

/**
 * Created by truon on 10/7/2016.
 */

public class CreditPlan {
    public int creditplanId;
    public String creditplanName;
    public String creditplanDescription;
    public double creditplanPrice;

    public CreditPlan() {
    }

    public CreditPlan(double creditplanPrice, String creditplanName, String creditplanDescription) {
        this.creditplanPrice = creditplanPrice;
        this.creditplanName = creditplanName;
        this.creditplanDescription = creditplanDescription;
    }

    public CreditPlan(int creditplanId, String creditplanName, String creditplanDescription, double creditplanPrice) {
        this.creditplanId = creditplanId;
        this.creditplanName = creditplanName;
        this.creditplanDescription = creditplanDescription;
        this.creditplanPrice = creditplanPrice;
    }

    public int getCreditplanId() {
        return creditplanId;
    }

    public void setCreditplanId(int creditplanId) {
        this.creditplanId = creditplanId;
    }

    public String getCreditplanName() {
        return creditplanName;
    }

    public void setCreditplanName(String creditplanName) {
        this.creditplanName = creditplanName;
    }

    public String getCreditplanDescription() {
        return creditplanDescription;
    }

    public void setCreditplanDescription(String creditplanDescription) {
        this.creditplanDescription = creditplanDescription;
    }

    public double getCreditplanPrice() {
        return creditplanPrice;
    }

    public void setCreditplanPrice(double creditplanPrice) {
        this.creditplanPrice = creditplanPrice;
    }
}
