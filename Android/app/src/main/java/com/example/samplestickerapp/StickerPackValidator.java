/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.samplestickerapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;

import com.facebook.animated.webp.WebPImage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

class StickerPackValidator {
    private static final int STATIC_STICKER_FILE_LIMIT_KB = 100;
    private static final int ANIMATED_STICKER_FILE_LIMIT_KB = 500;
    static final int EMOJI_MAX_LIMIT = 3;
    private static final int EMOJI_MIN_LIMIT = 1;
    private static final int IMAGE_HEIGHT = 512;
    private static final int IMAGE_WIDTH = 512;
    private static final int STICKER_SIZE_MIN = 3;
    private static final int STICKER_SIZE_MAX = 30;
    private static final int CHAR_COUNT_MAX = 128;
    private static final long KB_IN_BYTES = 1024;
    private static final int TRAY_IMAGE_FILE_SIZE_MAX_KB = 50;
    private static final int TRAY_IMAGE_DIMENSION_MIN = 24;
    private static final int TRAY_IMAGE_DIMENSION_MAX = 512;
    private static final int ANIMATED_STICKER_FRAME_DURATION_MIN = 8;
    private static final int ANIMATED_STICKER_TOTAL_DURATION_MAX = 10 * 1000; //ms
    private static final String PLAY_STORE_DOMAIN = "play.google.com";
    private static final String APPLE_STORE_DOMAIN = "itunes.apple.com";


    /**
     * Checks whether a sticker pack contains valid data
     */
    static void verifyStickerPackValidity(@NonNull Context context, @NonNull StickerPack stickerPack) throws IllegalStateException {
        if (TextUtils.isEmpty(stickerPack.identifier)) {
            throw new IllegalStateException("sticker pack identifier is empty");
        }
        if (stickerPack.identifier.length() > CHAR_COUNT_MAX) {
            throw new IllegalStateException("sticker pack identifier cannot exceed " + CHAR_COUNT_MAX + " characters");
        }
        checkStringValidity(stickerPack.identifier);
        if (TextUtils.isEmpty(stickerPack.publisher)) {
            throw new IllegalStateException("sticker pack publisher is empty, sticker pack identifier: " + stickerPack.identifier);
        }
        if (stickerPack.publisher.length() > CHAR_COUNT_MAX) {
            throw new IllegalStateException("sticker pack publisher cannot exceed " + CHAR_COUNT_MAX + " characters, sticker pack identifier: " + stickerPack.identifier);
        }
        if (TextUtils.isEmpty(stickerPack.name)) {
            throw new IllegalStateException("sticker pack name is empty, sticker pack identifier: " + stickerPack.identifier);
        }
        if (stickerPack.name.length() > CHAR_COUNT_MAX) {
            throw new IllegalStateException("sticker pack name cannot exceed " + CHAR_COUNT_MAX + " characters, sticker pack identifier: " + stickerPack.identifier);
        }
        if (TextUtils.isEmpty(stickerPack.trayImageFile)) {
            throw new IllegalStateException("sticker pack tray id is empty, sticker pack identifier:" + stickerPack.identifier);
        }
        if (!TextUtils.isEmpty(stickerPack.androidPlayStoreLink) && !isValidWebsiteUrl(stickerPack.androidPlayStoreLink)) {
            throw new IllegalStateException("Make sure to include http or https in url links, android play store link is not a valid url: " + stickerPack.androidPlayStoreLink);
        }
        if (!TextUtils.isEmpty(stickerPack.androidPlayStoreLink) && !isURLInCorrectDomain(stickerPack.androidPlayStoreLink, PLAY_STORE_DOMAIN)) {
            throw new IllegalStateException("android play store link should use play store domain: " + PLAY_STORE_DOMAIN);
        }
        if (!TextUtils.isEmpty(stickerPack.iosAppStoreLink) && !isValidWebsiteUrl(stickerPack.iosAppStoreLink)) {
            throw new IllegalStateException("Make sure to include http or https in url links, ios app store link is not a valid url: " + stickerPack.iosAppStoreLink);
        }
        if (!TextUtils.isEmpty(stickerPack.iosAppStoreLink) && !isURLInCorrectDomain(stickerPack.iosAppStoreLink, APPLE_STORE_DOMAIN)) {
            throw new IllegalStateException("iOS app store link should use app store domain: " + APPLE_STORE_DOMAIN);
        }
        if (!TextUtils.isEmpty(stickerPack.licenseAgreementWebsite) && !isValidWebsiteUrl(stickerPack.licenseAgreementWebsite)) {
            throw new IllegalStateException("Make sure to include http or https in url links, license agreement link is not a valid url: " + stickerPack.licenseAgreementWebsite);
        }
        if (!TextUtils.isEmpty(stickerPack.privacyPolicyWebsite) && !isValidWebsiteUrl(stickerPack.privacyPolicyWebsite)) {
            throw new IllegalStateException("Make sure to include http or https in url links, privacy policy link is not a valid url: " + stickerPack.privacyPolicyWebsite);
        }
        if (!TextUtils.isEmpty(stickerPack.publisherWebsite) && !isValidWebsiteUrl(stickerPack.publisherWebsite)) {
            throw new IllegalStateException("Make sure to include http or https in url links, publisher website link is not a valid url: " + stickerPack.publisherWebsite);
        }
        if (!TextUtils.isEmpty(stickerPack.publisherEmail) && !Patterns.EMAIL_ADDRESS.matcher(stickerPack.publisherEmail).matches()) {
            throw new IllegalStateException("publisher email does not seem valid, email is: " + stickerPack.publisherEmail);
        }
        try {
            final byte[] stickerAssetBytes = StickerPackLoader.fetchStickerAsset(stickerPack.identifier, stickerPack.trayImageFile, context.getContentResolver());
            if (stickerAssetBytes.length > TRAY_IMAGE_FILE_SIZE_MAX_KB * KB_IN_BYTES) {
                throw new IllegalStateException("tray image should be less than " + TRAY_IMAGE_FILE_SIZE_MAX_KB + " KB, tray image file: " + stickerPack.trayImageFile);
            }
            Bitmap bitmap = BitmapFactory.decodeByteArray(stickerAssetBytes, 0, stickerAssetBytes.length);
            if (bitmap.getHeight() > TRAY_IMAGE_DIMENSION_MAX || bitmap.getHeight() < TRAY_IMAGE_DIMENSION_MIN) {
                throw new IllegalStateException("tray image height should between " + TRAY_IMAGE_DIMENSION_MIN + " and " + TRAY_IMAGE_DIMENSION_MAX + " pixels, current tray image height is " + bitmap.getHeight() + ", tray image file: " + stickerPack.trayImageFile);
            }
            if (bitmap.getWidth() > TRAY_IMAGE_DIMENSION_MAX || bitmap.getWidth() < TRAY_IMAGE_DIMENSION_MIN) {
                throw new IllegalStateException("tray image width should be between " + TRAY_IMAGE_DIMENSION_MIN + " and " + TRAY_IMAGE_DIMENSION_MAX + " pixels, current tray image width is " + bitmap.getWidth() + ", tray image file: " + stickerPack.trayImageFile);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot open tray image, " + stickerPack.trayImageFile, e);
        }
        final List<Sticker> stickers = stickerPack.getStickers();
        if (stickers.size() < STICKER_SIZE_MIN || stickers.size() > STICKER_SIZE_MAX) {
            throw new IllegalStateException("sticker pack sticker count should be between 3 to 30 inclusive, it currently has " + stickers.size() + ", sticker pack identifier: " + stickerPack.identifier);
        }
        for (final Sticker sticker : stickers) {
            validateSticker(context, stickerPack.identifier, sticker, stickerPack.animatedStickerPack);
        }
    }

    private static void validateSticker(@NonNull Context context, @NonNull final String identifier, @NonNull final Sticker sticker, final boolean animatedStickerPack) throws IllegalStateException {
        if (sticker.emojis.size() > EMOJI_MAX_LIMIT) {
            throw new IllegalStateException("emoji count exceed limit, sticker pack identifier: " + identifier + ", filename: " + sticker.imageFileName);
        }
        if (sticker.emojis.size() < EMOJI_MIN_LIMIT) {
            throw new IllegalStateException("To provide best user experience, please associate at least 1 emoji to this sticker, sticker pack identifier: " + identifier + ", filename: " + sticker.imageFileName);
        }
        if (TextUtils.isEmpty(sticker.imageFileName)) {
            throw new IllegalStateException("no file path for sticker, sticker pack identifier:" + identifier);
        }
        validateStickerFile(context, identifier, sticker.imageFileName, animatedStickerPack);
    }

    private static void validateStickerFile(@NonNull Context context, @NonNull String identifier, @NonNull final String fileName, final boolean animatedStickerPack) throws IllegalStateException {
        try {
            final byte[] stickerInBytes = StickerPackLoader.fetchStickerAsset(identifier, fileName, context.getContentResolver());
            if (!animatedStickerPack && stickerInBytes.length > STATIC_STICKER_FILE_LIMIT_KB * KB_IN_BYTES) {
                throw new IllegalStateException("static sticker should be less than " + STATIC_STICKER_FILE_LIMIT_KB + "KB, current file is " + stickerInBytes.length / KB_IN_BYTES + " KB, sticker pack identifier: " + identifier + ", filename: " + fileName);
            }
            if (animatedStickerPack && stickerInBytes.length > ANIMATED_STICKER_FILE_LIMIT_KB * KB_IN_BYTES) {
                throw new IllegalStateException("animated sticker should be less than " + ANIMATED_STICKER_FILE_LIMIT_KB + "KB, current file is " + stickerInBytes.length / KB_IN_BYTES + " KB, sticker pack identifier: " + identifier + ", filename: " + fileName);
            }
            try {
                final WebPImage webPImage = WebPImage.createFromByteArray(stickerInBytes);
                if (webPImage.getHeight() != IMAGE_HEIGHT) {
                    throw new IllegalStateException("sticker height should be " + IMAGE_HEIGHT + ", current height is " + webPImage.getHeight() + ", sticker pack identifier: " + identifier + ", filename: " + fileName);
                }
                if (webPImage.getWidth() != IMAGE_WIDTH) {
                    throw new IllegalStateException("sticker width should be " + IMAGE_WIDTH + ", current width is " + webPImage.getWidth() + ", sticker pack identifier: " + identifier + ", filename: " + fileName);
                }
                if (animatedStickerPack) {
                    if (webPImage.getFrameCount() <= 1) {
                        throw new IllegalStateException("this pack is marked as animated sticker pack, all stickers should animate, sticker pack identifier: " + identifier + ", filename: " + fileName);
                    }
                    checkFrameDurationsForAnimatedSticker(webPImage.getFrameDurations(), identifier, fileName);
                    if (webPImage.getDuration() > ANIMATED_STICKER_TOTAL_DURATION_MAX) {
                        throw new IllegalStateException("sticker animation max duration is: " + ANIMATED_STICKER_TOTAL_DURATION_MAX + " ms, current duration is: " + webPImage.getDuration() + " ms, sticker pack identifier: " + identifier + ", filename: " + fileName);
                    }
                } else if (webPImage.getFrameCount() > 1) {
                    throw new IllegalStateException("this pack is not marked as animated sticker pack, all stickers should be static stickers, sticker pack identifier: " + identifier + ", filename: " + fileName);
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Error parsing webp image, sticker pack identifier: " + identifier + ", filename: " + fileName, e);
            }
        } catch (IOException e) {
            throw new IllegalStateException("cannot open sticker file: sticker pack identifier: " + identifier + ", filename: " + fileName, e);
        }
    }

    private static void checkFrameDurationsForAnimatedSticker(@NonNull final int[] frameDurations, @NonNull final String identifier, @NonNull final String fileName) {
        for (int frameDuration : frameDurations) {
            if (frameDuration < ANIMATED_STICKER_FRAME_DURATION_MIN) {
                throw new IllegalStateException("animated sticker frame duration limit is " + ANIMATED_STICKER_FRAME_DURATION_MIN + ", sticker pack identifier: " + identifier + ", filename: " + fileName);
            }
        }
    }

    private static void checkStringValidity(@NonNull String string) {
        String pattern = "[\\w-.,'\\s]+"; // [a-zA-Z0-9_-.' ]
        if (!string.matches(pattern)) {
            throw new IllegalStateException(string + " contains invalid characters, allowed characters are a to z, A to Z, _ , ' - . and space character");
        }
        if (string.contains("..")) {
            throw new IllegalStateException(string + " cannot contain ..");
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isValidWebsiteUrl(String websiteUrl) throws IllegalStateException {
        try {
            new URL(websiteUrl);
        } catch (MalformedURLException e) {
            Log.e("StickerPackValidator", "url: " + websiteUrl + " is malformed");
            throw new IllegalStateException("url: " + websiteUrl + " is malformed", e);
        }
        return URLUtil.isHttpUrl(websiteUrl) || URLUtil.isHttpsUrl(websiteUrl);

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isURLInCorrectDomain(String urlString, String domain) throws IllegalStateException {
        try {
            URL url = new URL(urlString);
            if (domain.equals(url.getHost())) {
                return true;
            }
        } catch (MalformedURLException e) {
            Log.e("StickerPackValidator", "url: " + urlString + " is malformed");
            throw new IllegalStateException("url: " + urlString + " is malformed");
        }
        return false;
    }
}
