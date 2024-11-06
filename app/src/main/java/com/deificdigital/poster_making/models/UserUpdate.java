package com.deificdigital.poster_making.models;

public class UserUpdate {
    private int user_id;
    private String name;
    private String email;
    private String number;
    private String designation;
    private String companyName;

    // Constructor
    public UserUpdate(int user_id, String name, String email, String number, String designation, String companyName) {
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.number = number;
        this.designation = designation;
        this.companyName = companyName;
    }

    // Getters and setters
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}