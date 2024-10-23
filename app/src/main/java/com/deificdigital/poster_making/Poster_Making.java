package com.deificdigital.poster_making;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class Poster_Making extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this); // Initialize Firebase
    }
}
