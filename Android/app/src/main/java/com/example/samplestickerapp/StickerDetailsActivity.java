package com.example.samplestickerapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StickerDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StickerAdapter adapter;
    private FetchStickersAsyncTask fetchStickersAsyncTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_details);

        recyclerView = findViewById(R.id.sticker_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        String stickerPackId = getIntent().getStringExtra("sticker_pack_id");
        if (stickerPackId != null) {
            fetchStickersAsyncTask = new FetchStickersAsyncTask(this);
            fetchStickersAsyncTask.execute(stickerPackId);
        }

        findViewById(R.id.add_to_whatsapp_button).setOnClickListener(v -> {
            new DownloadStickersTask(this, adapter.getStickers(), getIntent().getStringExtra("sticker_pack_id")).execute();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fetchStickersAsyncTask != null && !fetchStickersAsyncTask.isCancelled()) {
            fetchStickersAsyncTask.cancel(true);
        }
    }

    private void showStickers(List<Sticker> stickers) {
        adapter = new StickerAdapter(stickers);
        recyclerView.setAdapter(adapter);
    }

    private void showErrorMessage(String message) {
        Log.e("StickerDetailsActivity", "Error: " + message);
    }

    public void addStickerPackToWhatsApp(String identifier, String stickerPackName) {
        Intent intent = new Intent();
        intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK");
        intent.putExtra("sticker_pack_id", identifier);
        intent.putExtra("sticker_pack_authority", BuildConfig.CONTENT_PROVIDER_AUTHORITY);
        intent.putExtra("sticker_pack_name", stickerPackName);
        try {
            startActivityForResult(intent, 200);
        } catch (Exception e) {
            Log.e("StickerDetailsActivity", "Error adding sticker pack to WhatsApp", e);
        }
    }

    static class FetchStickersAsyncTask extends AsyncTask<String, Void, List<Sticker>> {
        private final WeakReference<StickerDetailsActivity> contextWeakReference;
        private String error;

        FetchStickersAsyncTask(StickerDetailsActivity activity) {
            this.contextWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected List<Sticker> doInBackground(String... strings) {
            try {
                final StickerDetailsActivity activity = contextWeakReference.get();
                if (activity != null) {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("https://www.stickers.dev-error.com/api/sticker-packs/" + strings[0])
                            .build();
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        error = response.message();
                        return null;
                    }
                    String json = response.body().string();
                    Gson gson = new Gson();
                    StickerPack stickerPack = gson.fromJson(json, StickerPack.class);
                    return stickerPack.getStickers();
                } else {
                    error = "context is null";
                    return null;
                }
            } catch (IOException e) {
                error = e.getMessage();
                Log.e("StickerDetailsActivity", "error fetching stickers", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Sticker> stickers) {
            final StickerDetailsActivity activity = contextWeakReference.get();
            if (activity != null) {
                if (stickers != null) {
                    activity.showStickers(stickers);
                } else {
                    activity.showErrorMessage(error);
                }
            }
        }
    }
}
