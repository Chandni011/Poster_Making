package com.deificdigital.poster_making.models;

import com.google.gson.annotations.SerializedName;

public class NewestModel {

    private int id;

    private String name;

    @SerializedName("category")
    private String category;

    @SerializedName("category_type")
    private String categoryType;

    private int plan;

    private String language;

    @SerializedName("post_image")
    private String post_Image;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public int getPlan() {
        return plan;
    }

    public String getLanguage() {
        return language;
    }

    public String getPostImage() {
        return post_Image;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public void setPlan(int plan) {
        this.plan = plan;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setPostImage(String postImage) {
        this.post_Image = postImage;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
