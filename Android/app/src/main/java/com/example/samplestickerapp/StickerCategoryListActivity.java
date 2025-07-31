package com.example.samplestickerapp;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StickerCategoryListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_category_list);

        ArrayList<StickerCategory> stickerCategories = getIntent().getParcelableArrayListExtra("sticker_categories");

        RecyclerView recyclerView = findViewById(R.id.sticker_category_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(new StickerCategoryAdapter(stickerCategories));
    }
}
