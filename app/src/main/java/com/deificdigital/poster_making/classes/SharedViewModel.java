package com.deificdigital.poster_making.classes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<String> savedImagePath = new MutableLiveData<>();

    public LiveData<String> getSavedImagePath() {
        return savedImagePath;
    }

    public void setSavedImagePath(String path) {
        savedImagePath.setValue(path);
    }
}