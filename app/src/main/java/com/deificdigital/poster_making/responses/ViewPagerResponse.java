package com.deificdigital.poster_making.responses;

import com.deificdigital.poster_making.models.ImageData;

import java.util.List;

public class ViewPagerResponse {
    private int status;
    private List<ImageData> data;
    private String message;

    // Getters and Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ImageData> getData() {
        return data;
    }

    public void setData(List<ImageData> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

