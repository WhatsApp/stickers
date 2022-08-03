/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.samplestickerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EntryActivity extends BaseActivity {
    private View progressBar;
    private Future<Void> taskFuture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        overridePendingTransition(0, 0);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressBar = findViewById(R.id.entry_activity_progress);
        postBackgroundTask();
    }

    private void showStickerPack(ArrayList<StickerPack> stickerPackList) {
        progressBar.setVisibility(View.GONE);
        if (stickerPackList.size() > 1) {
            final Intent intent = new Intent(this, StickerPackListActivity.class);
            intent.putParcelableArrayListExtra(StickerPackListActivity.EXTRA_STICKER_PACK_LIST_DATA, stickerPackList);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        } else {
            final Intent intent = new Intent(this, StickerPackDetailsActivity.class);
            intent.putExtra(StickerPackDetailsActivity.EXTRA_SHOW_UP_BUTTON, false);
            intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_DATA, stickerPackList.get(0));
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        }
    }

    private void showErrorMessage(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        Log.e("EntryActivity", "error fetching sticker packs, " + errorMessage);
        final TextView errorMessageTV = findViewById(R.id.error_message);
        errorMessageTV.setText(getString(R.string.error_message, errorMessage));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (taskFuture != null && !taskFuture.isCancelled()) {
            taskFuture.cancel(true);
        }
    }

    private void postBackgroundTask() {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final WeakReference<EntryActivity> activityWeakReference = new WeakReference<>(this);
        taskFuture = executor.submit(() -> {
            Pair<String, ArrayList<StickerPack>> result;
            try {
                final Context context = activityWeakReference.get();
                if (context != null) {
                    ArrayList<StickerPack> stickerPackList = StickerPackLoader.fetchStickerPacks(context);
                    if (stickerPackList.size() == 0) {
                        result = new Pair<>("could not find any packs", null);
                    } else {
                        result = new Pair<>(null, stickerPackList);
                    }
                } else {
                    result = new Pair<>("could not fetch sticker packs", null);
                }
            } catch (Exception e) {
                Log.e("EntryActivity", "error fetching sticker packs", e);
                result = new Pair<>(e.getMessage(), null);
            }

            final Pair<String, ArrayList<StickerPack>> stringListPair = result;
            runOnUiThread(() -> {
                final EntryActivity entryActivity = activityWeakReference.get();
                if (entryActivity != null) {
                    if (stringListPair.first != null) {
                        entryActivity.showErrorMessage(stringListPair.first);
                    } else {
                        entryActivity.showStickerPack(stringListPair.second);
                    }
                }
            });
            return (Void) null;
        });
    }
}
