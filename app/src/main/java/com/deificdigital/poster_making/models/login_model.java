package com.deificdigital.poster_making.models;

import android.net.Uri;

public class login_model {
    private String name;
    private String email;
    private String phone;
    private String login_with;
    private Uri profile_pic;

    public login_model(String name, String email, String phone, String login_with, Uri profile_pic) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.login_with = login_with;
        this.profile_pic = profile_pic;
    }
}
