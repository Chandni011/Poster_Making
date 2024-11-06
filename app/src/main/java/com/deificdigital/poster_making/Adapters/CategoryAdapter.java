package com.deificdigital.poster_making.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deificdigital.poster_making.FullCategoryImage;
import com.deificdigital.poster_making.R;
import com.deificdigital.poster_making.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private List<Category> categories;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.upcoming_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.tvUpcoming.setText(category.getName());

        // Set the date
        holder.tvDate.setText(category.getFestivalDate());

        String imageUrl = "https://postermaking.deifichrservices.com/public/" + category.getCategoryImage(); // Adjust base URL as needed
        Glide.with(context)
                .load(imageUrl)
                .into(holder.ivUpcoming);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullCategoryImage.class);
            intent.putExtra("id", category.getId());
            intent.putExtra("category_type", category.getCategoryType());
            intent.putExtra("category_name", category.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvUpcoming;
        ImageView ivUpcoming;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvUpcoming = itemView.findViewById(R.id.tvUpcoming);
            ivUpcoming = itemView.findViewById(R.id.ivUpcoming);
        }
    }
}

