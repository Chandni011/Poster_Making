package com.deificdigital.poster_making.models;

public class PoliticalModel {
    private int id;
    private String name;
    private String category_image;
    private String category_type;

    public PoliticalModel(int id, String name, String category_image, String category_type) {
        this.id = id;
        this.name = name;
        this.category_image = category_image;
        this.category_type = category_type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory_image() {
        return category_image;
    }

    public String getCategory_type(){
        return category_type;
    }
}
