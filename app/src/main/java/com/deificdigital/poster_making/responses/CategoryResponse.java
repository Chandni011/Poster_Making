package com.deificdigital.poster_making.responses;

import com.deificdigital.poster_making.models.Category;

import java.util.List;

public class CategoryResponse {
    private int status;
    private List<Category> data;
    private String message;

    // Getters and Setters
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public List<Category> getData() { return data; }
    public void setData(List<Category> data) { this.data = data; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
