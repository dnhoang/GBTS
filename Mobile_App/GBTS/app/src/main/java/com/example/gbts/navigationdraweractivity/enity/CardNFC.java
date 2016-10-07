package com.example.gbts.navigationdraweractivity.enity;

import java.util.Date;

/**
 * Created by truon on 10/5/2016.
 */

public class CardNFC {
    private String cardID;
    private String cardName;
    private String registrationDate;
    private double balance;
    private int status;

    public CardNFC() {
    }

    public CardNFC(String cardName, int status) {
        this.cardName = cardName;
        this.status = status;
    }

    public CardNFC(String cardID, String cardName, String registrationDate, double balance, int status) {
        this.cardID = cardID;
        this.cardName = cardName;
        this.registrationDate = registrationDate;
        this.balance = balance;
        this.status = status;
    }

    public String getCardID() {
        return cardID;
    }

    public void setCardID(String cardID) {
        this.cardID = cardID;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
