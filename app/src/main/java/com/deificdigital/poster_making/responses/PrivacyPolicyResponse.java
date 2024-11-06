package com.deificdigital.poster_making.responses;

import java.util.List;

public class PrivacyPolicyResponse {
    private int status;
    private List<Data> data;
    private String message;

    public int getStatus() { return status; }
    public List<Data> getData() { return data; }
    public String getMessage() { return message; }
}