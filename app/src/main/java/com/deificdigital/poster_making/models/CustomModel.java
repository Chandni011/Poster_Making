package com.deificdigital.poster_making.models;

public class CustomModel {
    private int id;
    private String name;
    private String category_type;
    private String festival_date;
    private String category_image;
    private String featured;
    private int status;
    private String created_at;
    private String updated_at;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategoryType() { return category_type; }
    public String getCategoryImage() { return category_image; }
}