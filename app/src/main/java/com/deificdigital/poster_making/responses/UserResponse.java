package com.deificdigital.poster_making.responses;

import com.deificdigital.poster_making.models.User;

import java.util.List;

public class UserResponse {
    private int status;
    private List<User> data;
    private String message;

    // Getters and Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<User> getData() {
        return data;
    }

    public void setData(List<User> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}