/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.samplestickerapp;

import androidx.recyclerview.widget.RecyclerView;

import com.example.samplestickerapp.databinding.StickerPacksListItemBinding;

class StickerPackListItemViewHolder extends RecyclerView.ViewHolder {

    final StickerPacksListItemBinding binding;

    StickerPackListItemViewHolder(final StickerPacksListItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

}