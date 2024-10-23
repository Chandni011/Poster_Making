package com.deificdigital.poster_making.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deificdigital.poster_making.FullImageActivity;
import com.deificdigital.poster_making.R;
import com.deificdigital.poster_making.models.NewestModel;

import java.util.List;

public class NewestAdapter extends RecyclerView.Adapter<NewestAdapter.PostViewHolder> {
    private List<NewestModel> posts;
    private Context context;

    public NewestAdapter(List<NewestModel> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.newest_layout, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        NewestModel post = posts.get(position);
        String imageUrl = post.getPostImage();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            String fullImageUrl = "https://postermaking.deifichrservices.com/public/" + imageUrl;
            Glide.with(context)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.image_holder)
                    .error(R.drawable.img)
                    .into(holder.postImageView);

            // Log the full URL to verify
            Log.d("NewestAdapter", "Image URL: " + fullImageUrl);

            holder.postImageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, FullImageActivity.class);
                intent.putExtra("image_url", fullImageUrl);  // Pass the full image URL
                context.startActivity(intent);
            });
        } else {
            Glide.with(context)
                    .load(R.drawable.img)
                    .into(holder.postImageView);
        }
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setPosts(List<NewestModel> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView postImageView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.ivImage);
        }
    }
}