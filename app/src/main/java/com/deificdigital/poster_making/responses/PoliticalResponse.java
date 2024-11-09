package com.deificdigital.poster_making.responses;
import com.deificdigital.poster_making.models.PoliticalModel;

import java.util.List;

public class PoliticalResponse {

    private int status;
    private List<PoliticalModel> data;
    private String message;

    public int getStatus() { return status; }
    public List<PoliticalModel> getData() { return data; }
    public String getMessage() { return message; }
}
