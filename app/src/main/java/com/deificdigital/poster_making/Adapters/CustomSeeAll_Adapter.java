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
import com.deificdigital.poster_making.CustomSeeAllActivity;
import com.deificdigital.poster_making.FullCategoryImage;
import com.deificdigital.poster_making.R;
import com.deificdigital.poster_making.models.CustomModel;

import java.util.List;

public class CustomSeeAll_Adapter extends RecyclerView.Adapter<CustomSeeAll_Adapter.ViewHolder>{

    private Context context;
    private List<CustomModel> itemList;

    public CustomSeeAll_Adapter(Context context, List<CustomModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public CustomSeeAll_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_see_all_layout, parent, false);
        return new CustomSeeAll_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomSeeAll_Adapter.ViewHolder holder, int position) {
        CustomModel item = itemList.get(position);

        holder.textView.setText(item.getName());

        // Load image using Glide
        String imageUrl = "https://postermaking.deifichrservices.com/public/" + item.getCategoryImage();
        Glide.with(context)
                .load(imageUrl)
                .into(holder.imageView);

        Log.d("CustomAdapter", "CategoryType " + item.getCategoryType());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullCategoryImage.class);
            intent.putExtra("id", item.getId());
            intent.putExtra("category_type", item.getCategoryType());
            intent.putExtra("category_name", item.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvCustom);
            imageView = itemView.findViewById(R.id.ivCustom);
        }
    }
}
