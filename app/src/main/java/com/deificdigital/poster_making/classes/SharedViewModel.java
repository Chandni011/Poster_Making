package com.deificdigital.poster_making.classes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<String>> imagePaths = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<String>> getImagePaths() {
        return imagePaths;
    }

    public void addImagePath(String imagePath) {
        List<String> currentPaths = imagePaths.getValue();
        if (currentPaths != null) {
            currentPaths.add(imagePath);
            imagePaths.setValue(currentPaths);
        }
    }
}