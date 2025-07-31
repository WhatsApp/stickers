/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.samplestickerapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EntryActivity extends BaseActivity {
    private View progressBar;
    private FetchCategoriesAsyncTask fetchCategoriesAsyncTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        overridePendingTransition(0, 0);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressBar = findViewById(R.id.entry_activity_progress);
        fetchCategoriesAsyncTask = new FetchCategoriesAsyncTask(this);
        fetchCategoriesAsyncTask.execute();
    }

    private void showStickerCategories(ArrayList<StickerCategory> stickerCategories) {
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent(this, StickerCategoryListActivity.class);
        intent.putParcelableArrayListExtra("sticker_categories", stickerCategories);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    private void showErrorMessage(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        Log.e("EntryActivity", "error fetching sticker categories, " + errorMessage);
        final TextView errorMessageTV = findViewById(R.id.error_message);
        errorMessageTV.setText(getString(R.string.error_message, errorMessage));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fetchCategoriesAsyncTask != null && !fetchCategoriesAsyncTask.isCancelled()) {
            fetchCategoriesAsyncTask.cancel(true);
        }
    }

    static class FetchCategoriesAsyncTask extends AsyncTask<Void, Void, ArrayList<StickerCategory>> {
        private final WeakReference<EntryActivity> contextWeakReference;
        private String error;

        FetchCategoriesAsyncTask(EntryActivity activity) {
            this.contextWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected ArrayList<StickerCategory> doInBackground(Void... voids) {
            try {
                final EntryActivity entryActivity = contextWeakReference.get();
                if (entryActivity != null) {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("https://www.stickers.dev-error.com/api/sticker-categories")
                            .build();
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        error = response.message();
                        return null;
                    }
                    String json = response.body().string();
                    Gson gson = new Gson();
                    return gson.fromJson(json, new TypeToken<ArrayList<StickerCategory>>() {
                    }.getType());
                } else {
                    error = "context is null";
                    return null;
                }
            } catch (IOException e) {
                error = e.getMessage();
                Log.e("EntryActivity", "error fetching sticker categories", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<StickerCategory> stickerCategories) {
            final EntryActivity entryActivity = contextWeakReference.get();
            if (entryActivity != null) {
                if (stickerCategories != null) {
                    entryActivity.showStickerCategories(stickerCategories);
                } else {
                    entryActivity.showErrorMessage(error);
                }
            }
        }
    }
}
