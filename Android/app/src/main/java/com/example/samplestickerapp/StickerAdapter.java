package com.example.samplestickerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

    private List<Sticker> stickers;

    public StickerAdapter(List<Sticker> stickers) {
        this.stickers = stickers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sticker sticker = stickers.get(position);
        holder.stickerImage.setImageURI("https://www.stickers.dev-error.com/" + sticker.imageFileName);
    }

    @Override
    public int getItemCount() {
        return stickers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView stickerImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            stickerImage = itemView.findViewById(R.id.sticker_image);
        }
    }
}
