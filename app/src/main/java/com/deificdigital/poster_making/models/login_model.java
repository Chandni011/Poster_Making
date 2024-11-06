package com.deificdigital.poster_making.models;

import android.net.Uri;

public class login_model {
    private String name;
    private String email;
    private String phone;
    private String login_with;
    private String profile_pic; // Changed Uri to String
    private String company_name;
    private String designation;

    // Constructor
    public login_model(String name, String email, String phone, String login_with, String profile_pic) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.login_with = login_with;
        this.profile_pic = profile_pic;
    }

    public String getName(){return name;}

    public String getEmail(){return email;}

    public String getPhone(){return phone;}
    // Getters and Setters
    public String getCompanyName() {
        return company_name;
    }

    public void setCompanyName(String company_name) {
        this.company_name = company_name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
