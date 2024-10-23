package com.deificdigital.poster_making.responses;

import com.deificdigital.poster_making.models.NewestModel;

import java.util.List;

public class NewestResponse {

    private int status;
    private List<NewestModel> data;
    private String message;

    // Getter for status
    public int getStatus() {
        return status;
    }

    // Setter for status
    public void setStatus(int status) {
        this.status = status;
    }

    // Getter for data (List of PostModel)
    public List<NewestModel> getData() {
        return data;
    }

    // Setter for data
    public void setData(List<NewestModel> data) {
        this.data = data;
    }

    // Getter for message
    public String getMessage() {
        return message;
    }

    // Setter for message
    public void setMessage(String message) {
        this.message = message;
    }
}
