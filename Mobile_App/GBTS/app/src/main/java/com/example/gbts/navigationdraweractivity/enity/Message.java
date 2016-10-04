package com.example.gbts.navigationdraweractivity.enity;

/**
 * Created by truon on 10/3/2016.
 */

public class Message {
    private boolean success;
    private String message;

    public Message() {
    }

    public Message(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
