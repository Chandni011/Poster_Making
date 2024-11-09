package com.deificdigital.poster_making.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deificdigital.poster_making.Adapters.ImageAdapter;
import com.deificdigital.poster_making.R;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DraftFragment extends Fragment {

    private static final String PREFS_NAME = "AppPreferences";
    private static final String KEY_IMAGE_PATHS = "image_paths";
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Bitmap> imageList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_draft, container, false);

        recyclerView = rootView.findViewById(R.id.rvImages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageList);
        recyclerView.setAdapter(imageAdapter);

        loadSavedImages();

        return rootView;
    }

    private void loadSavedImages() {
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        // Check if stored as a JSON string or Set<String> and handle appropriately
        Object savedPaths = prefs.getAll().get("image_paths");

        List<String> imagePaths = new ArrayList<>();
        if (savedPaths instanceof String) {
            // Handle JSON String
            String json = (String) savedPaths;
            imagePaths = gson.fromJson(json, new TypeToken<List<String>>() {
            }.getType());
        } else if (savedPaths instanceof Set) {
            // Handle legacy Set<String> and convert to JSON String
            Set<String> pathsSet = prefs.getStringSet("image_paths", new HashSet<>());
            imagePaths = new ArrayList<>(pathsSet);

            // Save the converted list back as JSON for future use
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("image_paths", gson.toJson(imagePaths));
            editor.remove("image_paths_set");  // Optionally remove legacy set entry
            editor.apply();
        }

        // Clear imageList and load bitmaps from paths
        imageList.clear();
        for (String path : imagePaths) {
            Bitmap bitmap = loadBitmapFromFile(path);
            if (bitmap != null) {
                imageList.add(bitmap);
            }
        }
        imageAdapter.notifyDataSetChanged();
    }

    private Bitmap loadBitmapFromFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return null;
    }
}