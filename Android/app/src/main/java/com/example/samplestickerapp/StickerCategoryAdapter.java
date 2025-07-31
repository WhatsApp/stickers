package com.example.samplestickerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class StickerCategoryAdapter extends RecyclerView.Adapter<StickerCategoryAdapter.ViewHolder> {

    private List<StickerCategory> stickerCategories;

    public StickerCategoryAdapter(List<StickerCategory> stickerCategories) {
        this.stickerCategories = stickerCategories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StickerCategory stickerCategory = stickerCategories.get(position);
        holder.categoryName.setText(stickerCategory.getName());
        holder.categoryImage.setImageURI("https://www.stickers.dev-error.com/" + stickerCategory.getImage());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), StickerPackActivity.class);
            intent.putExtra("category_id", stickerCategory.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return stickerCategories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView categoryImage;
        TextView categoryName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.category_image);
            categoryName = itemView.findViewById(R.id.category_name);
        }
    }
}
