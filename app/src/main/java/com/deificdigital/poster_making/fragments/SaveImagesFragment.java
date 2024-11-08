package com.deificdigital.poster_making.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deificdigital.poster_making.Adapters.SavedImagesAdapter;
import com.deificdigital.poster_making.R;
import com.deificdigital.poster_making.Saved_image_Activity;

import java.util.ArrayList;
import java.util.List;

public class SaveImagesFragment extends Fragment implements SavedImagesAdapter.OnImageClickListener {


    private static final String PREFS_NAME = "SavedImagePrefs";
    private static final String KEY_IMAGE_PATHS = "image_paths";
    private RecyclerView rvImages;
    private SavedImagesAdapter imagesAdapter;
    private ImageView ivEmpty;
    private TextView tvEmptyText;
    private List<String> savedImagePaths = new ArrayList<>();
    private static final int REQUEST_IMAGE_DELETE = 1001; // Define a request code

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_save_images, container, false);

        rvImages = view.findViewById(R.id.rvImages);
        ivEmpty = view.findViewById(R.id.ivEmpty);
        tvEmptyText = view.findViewById(R.id.tvEmptyText);
        imagesAdapter = new SavedImagesAdapter((ArrayList<String>) savedImagePaths, this);

        rvImages.setAdapter(imagesAdapter);
        rvImages.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));

        loadSavedImages();
        return view;
    }

    @Override
    public void onImageClick(String imagePath) {
        Intent intent = new Intent(getActivity(), Saved_image_Activity.class);
        intent.putExtra("imagePath", imagePath);
        startActivityForResult(intent, REQUEST_IMAGE_DELETE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_DELETE && resultCode == getActivity().RESULT_OK) {
            if (data != null) {
                String deletedImagePath = data.getStringExtra("deletedImagePath");
                if (deletedImagePath != null) {
                    onImageDeleted(deletedImagePath);
                }
            }
        }
    }

    public void onImageDeleted(String imagePath) {
        if (savedImagePaths.contains(imagePath)) {
            savedImagePaths.remove(imagePath);
            imagesAdapter.notifyDataSetChanged();
            saveImagePathsToPrefs();
        }
    }

    private void saveImagePathsToPrefs() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        StringBuilder sb = new StringBuilder();
        for (String path : savedImagePaths) {
            sb.append(path.trim()).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        prefs.edit().putString(KEY_IMAGE_PATHS, sb.toString()).apply();
    }

    private void loadSavedImages() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedPaths = prefs.getString(KEY_IMAGE_PATHS, "");

        savedImagePaths.clear();
        if (!savedPaths.isEmpty()) {
            String[] pathsArray = savedPaths.split(",");
            for (String path : pathsArray) {
                String trimmedPath = path.trim();
                if (!trimmedPath.isEmpty()) {
                    savedImagePaths.add(trimmedPath);
                }
            }
        }
        imagesAdapter.notifyDataSetChanged();

        if (imagesAdapter.getItemCount() != 0) {
            ivEmpty.setVisibility(View.GONE);
            tvEmptyText.setVisibility(View.GONE);
        }
        else {
            ivEmpty.setVisibility(View.VISIBLE);
            tvEmptyText.setVisibility(View.VISIBLE);
        }
    }

    public void addImagePath(String imagePath) {
        String trimmedPath = imagePath.trim();
        if (!trimmedPath.isEmpty()) {
            savedImagePaths.add(trimmedPath);
            imagesAdapter.notifyItemInserted(savedImagePaths.size() - 1);
            saveImagePathsToPrefs();
        }
    }
}