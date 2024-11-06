package com.deificdigital.poster_making.responses;

import com.deificdigital.poster_making.models.CustomModel;

import java.util.List;

public class CustomResponse {
    private int status;
    private List<CustomModel> data;
    private String message;

    public int getStatus() { return status; }
    public List<CustomModel> getData() { return data; }
    public String getMessage() { return message; }
}
