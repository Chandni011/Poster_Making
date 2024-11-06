package com.deificdigital.poster_making.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.deificdigital.poster_making.models.FontModel;
import java.util.List;

public class FontAdapter extends RecyclerView.Adapter<FontAdapter.FontViewHolder> {
    private Context context;
    private List<FontModel> fontList;
    private OnFontClickListener listener;

    public FontAdapter(Context context, List<FontModel> fontList, OnFontClickListener listener) {
        this.context = context;
        this.fontList = fontList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FontViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new FontViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FontViewHolder holder, int position) {
        FontModel font = fontList.get(position);
        holder.fontTextView.setText(font.getFontName());

        // Set the font style using ResourcesCompat
        int fontResId = context.getResources().getIdentifier(font.getFontResName(), "font", context.getPackageName());
        Typeface typeface = ResourcesCompat.getFont(context, fontResId);
        holder.fontTextView.setTypeface(typeface);

        holder.itemView.setOnClickListener(v -> {
            listener.onFontClick(font);
        });
    }

    @Override
    public int getItemCount() {
        return fontList.size();
    }

    public static class FontViewHolder extends RecyclerView.ViewHolder {
        TextView fontTextView;

        public FontViewHolder(@NonNull View itemView) {
            super(itemView);
            fontTextView = itemView.findViewById(android.R.id.text1);
        }
    }

    public interface OnFontClickListener {
        void onFontClick(FontModel font);
    }
}
