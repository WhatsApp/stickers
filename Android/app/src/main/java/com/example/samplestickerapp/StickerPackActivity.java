package com.example.samplestickerapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StickerPackActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StickerPackAdapter adapter;
    private FetchStickerPacksAsyncTask fetchStickerPacksAsyncTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_pack);

        recyclerView = findViewById(R.id.sticker_pack_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int categoryId = getIntent().getIntExtra("category_id", -1);
        if (categoryId != -1) {
            fetchStickerPacksAsyncTask = new FetchStickerPacksAsyncTask(this);
            fetchStickerPacksAsyncTask.execute(categoryId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fetchStickerPacksAsyncTask != null && !fetchStickerPacksAsyncTask.isCancelled()) {
            fetchStickerPacksAsyncTask.cancel(true);
        }
    }

    private void showStickerPacks(ArrayList<StickerPack> stickerPacks) {
        adapter = new StickerPackAdapter(stickerPacks);
        recyclerView.setAdapter(adapter);
    }

    private void showErrorMessage(String message) {
        Log.e("StickerPackActivity", "Error: " + message);
    }

    static class FetchStickerPacksAsyncTask extends AsyncTask<Integer, Void, ArrayList<StickerPack>> {
        private final WeakReference<StickerPackActivity> contextWeakReference;
        private String error;

        FetchStickerPacksAsyncTask(StickerPackActivity activity) {
            this.contextWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected ArrayList<StickerPack> doInBackground(Integer... integers) {
            try {
                final StickerPackActivity activity = contextWeakReference.get();
                if (activity != null) {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("https://www.stickers.dev-error.com/api/sticker-packs/category/" + integers[0])
                            .build();
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        error = response.message();
                        return null;
                    }
                    String json = response.body().string();
                    Gson gson = new Gson();
                    return gson.fromJson(json, new TypeToken<ArrayList<StickerPack>>() {
                    }.getType());
                } else {
                    error = "context is null";
                    return null;
                }
            } catch (IOException e) {
                error = e.getMessage();
                Log.e("StickerPackActivity", "error fetching sticker packs", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<StickerPack> stickerPacks) {
            final StickerPackActivity activity = contextWeakReference.get();
            if (activity != null) {
                if (stickerPacks != null) {
                    activity.showStickerPacks(stickerPacks);
                } else {
                    activity.showErrorMessage(error);
                }
            }
        }
    }
}
