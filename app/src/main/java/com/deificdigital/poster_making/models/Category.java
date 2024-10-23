package com.deificdigital.poster_making.models;

public class Category {
    private int id;
    private String name;
    private String category_type;
    private String festival_date;
    private String category_image;
    private int status;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategoryType() { return category_type; }
    public void setCategoryType(String category_type) { this.category_type = category_type; }

    public String getFestivalDate() { return festival_date; }
    public void setFestivalDate(String festival_date) { this.festival_date = festival_date; }

    public String getCategoryImage() { return category_image; }
    public void setCategoryImage(String category_image) { this.category_image = category_image; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}
