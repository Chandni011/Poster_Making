package com.deificdigital.poster_making.responses;

import com.deificdigital.poster_making.models.CustomImageModel;
import java.util.List;

public class CustomImageResponse {
    private int status;
    private List<CustomImageModel> data;
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<CustomImageModel> getData() {
        return data;
    }

    public void setData(List<CustomImageModel> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
