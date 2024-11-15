package com.deificdigital.poster_making.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.deificdigital.poster_making.Adapters.ImageAdapter;
import com.deificdigital.poster_making.R;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DraftFragment extends Fragment {

    private static final String PREFS_NAME = "AppPreferences";
    private static final String KEY_IMAGE_PATHS = "image_paths";
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Bitmap> imageList;
    private List<String> savedPaths;
    private ImageView ivDelete;
    private ImageView ivEmptyDraft;
    private TextView tvEmptyDraftText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_draft, container, false);

        ivEmptyDraft = rootView.findViewById(R.id.ivEmptyDraft);
        tvEmptyDraftText = rootView.findViewById(R.id.tvEmptyDraftText);
        ivDelete = rootView.findViewById(R.id.ivDelete);
        ivDelete.setOnClickListener(v -> confirmDeletion());

        recyclerView = rootView.findViewById(R.id.rvImages);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));

        imageList = new ArrayList<>();
        savedPaths = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageList, savedPaths);
        recyclerView.setAdapter(imageAdapter);

        return rootView;
    }

    private void confirmDeletion() {
        Set<String> selectedPaths = imageAdapter.getSelectedItems();
        if (!selectedPaths.isEmpty()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Images")
                    .setMessage("Do you want to delete the selected images?")
                    .setPositiveButton("OK", (dialog, which) -> deleteSelectedImages(selectedPaths))
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSavedImages();
    }

    private void deleteSelectedImages(Set<String> selectedPaths) {
        // Delete the actual files
        for (String path : selectedPaths) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }

        imageAdapter.removeItems(selectedPaths);
        savedPaths.removeAll(selectedPaths);
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_IMAGE_PATHS, new Gson().toJson(savedPaths)).apply();

        loadSavedImages();
    }

    private void loadSavedImages() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = prefs.getString(KEY_IMAGE_PATHS, null);
        if (json != null) {
            savedPaths = gson.fromJson(json, new TypeToken<List<String>>() {}.getType());
        } else {
            savedPaths = new ArrayList<>();
        }

        imageList.clear();
        List<String> validPaths = new ArrayList<>();

        for (String path : savedPaths) {
            File file = new File(path);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageList.add(bitmap);
                validPaths.add(path);
            }
        }
        savedPaths = validPaths;
        prefs.edit().putString(KEY_IMAGE_PATHS, gson.toJson(savedPaths)).apply();

        imageAdapter = new ImageAdapter(imageList, savedPaths);
        recyclerView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();

        if(imageAdapter.getItemCount() > 0) {
            ivEmptyDraft.setVisibility(View.GONE);
            tvEmptyDraftText.setVisibility(View.GONE);
        }
        else {
            ivEmptyDraft.setVisibility(View.VISIBLE);
            tvEmptyDraftText.setVisibility(View.VISIBLE);
        }
    }
}