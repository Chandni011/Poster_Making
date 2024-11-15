package com.deificdigital.poster_making.models;

public class PlanData {
    private int id;
    private String plan_name;
    private String price;
    private String discount_price;
    private int duration;
    private int plan_limit;
    private int status;
    private String update_at;
    private String created_at;

    public int getId(){
        return id;
    }

    public String getPlan_name(){
        return plan_name;
    }

    public String getPrice() {
        return price;
    }

    public int getDuration() {
        return duration;
    }
}