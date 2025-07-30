package com.example.samplestickerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class StickerPackAdapter extends RecyclerView.Adapter<StickerPackAdapter.ViewHolder> {

    private List<StickerPack> stickerPacks;

    public StickerPackAdapter(List<StickerPack> stickerPacks) {
        this.stickerPacks = stickerPacks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_packs_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StickerPack stickerPack = stickerPacks.get(position);
        holder.packName.setText(stickerPack.name);
        holder.publisher.setText(stickerPack.publisher);
        holder.trayImage.setImageURI("https://www.stickers.dev-error.com/" + stickerPack.trayImageFile);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), StickerDetailsActivity.class);
            intent.putExtra("sticker_pack_id", stickerPack.identifier);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return stickerPacks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView trayImage;
        TextView packName;
        TextView publisher;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            trayImage = itemView.findViewById(R.id.sticker_pack_tray_icon);
            packName = itemView.findViewById(R.id.sticker_pack_title);
            publisher = itemView.findViewById(R.id.sticker_pack_publisher);
        }
    }
}
