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
import com.deificdigital.poster_making.FullCategoryImage;
import com.deificdigital.poster_making.R;
import com.deificdigital.poster_making.models.CustomModel;
import com.deificdigital.poster_making.models.PoliticalModel;

import java.util.List;

public class PoliticalAdapter extends RecyclerView.Adapter<PoliticalAdapter.ViewHolder> {

    private Context context;
    private List<PoliticalModel> itemList;

    public PoliticalAdapter(Context context, List<PoliticalModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public PoliticalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_item, parent, false);
        return new PoliticalAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PoliticalAdapter.ViewHolder holder, int position) {
        PoliticalModel item = itemList.get(position);

        holder.textView.setText(item.getName());

        // Load image using Glide
        String imageUrl = "https://postermaking.deifichrservices.com/public/" + item.getCategory_image();
        Glide.with(context)
                .load(imageUrl)
                .into(holder.imageView);

        Log.d("CustomAdapter", "CategoryType " + item.getCategory_type());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullCategoryImage.class);
            intent.putExtra("id", item.getId());
            intent.putExtra("category_type", item.getCategory_type());
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
