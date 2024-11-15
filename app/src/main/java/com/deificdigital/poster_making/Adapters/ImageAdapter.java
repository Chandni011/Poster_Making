package com.deificdigital.poster_making.Adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deificdigital.poster_making.FullImageActivity;
import com.deificdigital.poster_making.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<Bitmap> imageList;
    private List<String> imagePaths;
    private Set<String> selectedItems = new HashSet<>();

    public ImageAdapter(List<Bitmap> imageList, List<String> imagePaths) {
        this.imageList = imageList;
        this.imagePaths = imagePaths;
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.draft_layout, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        if (position < imageList.size() && position < imagePaths.size()) {
            Bitmap currentImage = imageList.get(position);
            String currentPath = imagePaths.get(position);

            if (currentImage != null) {
                holder.imageView.setImageBitmap(currentImage);
            }

            holder.checkBox.setChecked(selectedItems.contains(currentPath));
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) selectedItems.add(currentPath);
                else selectedItems.remove(currentPath);
            });

            holder.imageView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), FullImageActivity.class);
                intent.putExtra("image_path", currentPath); // Pass the image path
                intent.putExtra("source", "drafts"); // Pass an extra to indicate it's from drafts
                v.getContext().startActivity(intent);
            });

        } else {
            holder.imageView.setImageBitmap(null);
            holder.checkBox.setChecked(false);
        }
    }
    public Set<String> getSelectedItems() {
        return selectedItems;
    }
    public void removeItems(Set<String> pathsToDelete) {
        List<Bitmap> newImageList = new ArrayList<>();
        List<String> newPathList = new ArrayList<>();

        for (int i = 0; i < imageList.size(); i++) {
            if (!pathsToDelete.contains(imagePaths.get(i))) {
                newImageList.add(imageList.get(i));
                newPathList.add(imagePaths.get(i));
            }
        }
        imageList = newImageList;
        imagePaths = newPathList;
        selectedItems.clear();
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return imageList.size();
    }
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CheckBox checkBox;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivImage);
            checkBox = itemView.findViewById(R.id.cbCheck);
        }
    }
}