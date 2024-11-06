package com.deificdigital.poster_making.responses;

import com.deificdigital.poster_making.models.User;
import com.deificdigital.poster_making.models.UserUpdate;

import java.util.List;

public class UserUpdateResponse {
    private int status;
    private List<UserUpdate> data;
    private String message;

    // Getters and Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<UserUpdate> getData() {
        return data;
    }

    public void setData(List<UserUpdate> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}