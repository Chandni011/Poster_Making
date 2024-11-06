package com.deificdigital.poster_making.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deificdigital.poster_making.FullImageActivity;
import com.deificdigital.poster_making.R;
import com.deificdigital.poster_making.models.CustomImageModel;

import java.util.List;

public class CategoryImageAdapter extends RecyclerView.Adapter<CategoryImageAdapter.ViewHolder> {

    private Context context;
    private List<CustomImageModel> imageList;

    public CategoryImageAdapter(Context context, List<CustomImageModel> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.newest_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomImageModel imageModel = imageList.get(position);

        // Assuming post_image is a full URL or a relative URL to be appended to the base URL
        String imageUrl = "https://postermaking.deifichrservices.com/public/" + imageModel.getPost_image();

        // Load image using Glide
        Glide.with(context)
                .load(imageUrl)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(v -> {
            // Create an intent to start FullImageActivity
            Intent intent = new Intent(context, FullImageActivity.class);
            intent.putExtra("image_url", imageUrl);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    // Method to update data in the adapter
    public void updateData(List<CustomImageModel> newData) {
        this.imageList.clear();
        this.imageList.addAll(newData);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivImage); // Make sure this ID matches your layout
        }
    }
}
