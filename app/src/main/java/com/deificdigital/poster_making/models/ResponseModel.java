package com.deificdigital.poster_making.models;

public class ResponseModel {
    private int status;
    private int user_id;
    private String message;

    // Constructor
    public ResponseModel(int status, int user_id, String message) {
        this.status = status;
        this.user_id = user_id;
        this.message = message;
    }

    // Getter and Setter for status
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    // Getter and Setter for user_id
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    // Getter and Setter for message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


