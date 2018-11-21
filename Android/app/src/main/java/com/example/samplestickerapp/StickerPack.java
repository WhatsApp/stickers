/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.samplestickerapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

class StickerPack implements Parcelable {
    String identifier;
    String name;
    String publisher;
    String trayImageFile;
    final String publisherEmail;
    final String publisherWebsite;
    final String privacyPolicyWebsite;
    final String licenseAgreementWebsite;

    String iosAppStoreLink;
    private List<Sticker> stickers;
    private long totalSize;
    String androidPlayStoreLink;
    private boolean isWhitelisted;

    StickerPack(String identifier, String name, String publisher, String trayImageFile, String publisherEmail, String publisherWebsite, String privacyPolicyWebsite, String licenseAgreementWebsite) {
        this.identifier = identifier;
        this.name = name;
        this.publisher = publisher;
        this.trayImageFile = trayImageFile;
        this.publisherEmail = publisherEmail;
        this.publisherWebsite = publisherWebsite;
        this.privacyPolicyWebsite = privacyPolicyWebsite;
        this.licenseAgreementWebsite = licenseAgreementWebsite;
    }

    void setIsWhitelisted(boolean isWhitelisted) {
        this.isWhitelisted = isWhitelisted;
    }

    boolean getIsWhitelisted() {
        return isWhitelisted;
    }

    protected StickerPack(Parcel in) {
        identifier = in.readString();
        name = in.readString();
        publisher = in.readString();
        trayImageFile = in.readString();
        publisherEmail = in.readString();
        publisherWebsite = in.readString();
        privacyPolicyWebsite = in.readString();
        licenseAgreementWebsite = in.readString();
        iosAppStoreLink = in.readString();
        stickers = in.createTypedArrayList(Sticker.CREATOR);
        totalSize = in.readLong();
        androidPlayStoreLink = in.readString();
        isWhitelisted = in.readByte() != 0;
    }

    public static final Creator<StickerPack> CREATOR = new Creator<StickerPack>() {
        @Override
        public StickerPack createFromParcel(Parcel in) {
            return new StickerPack(in);
        }

        @Override
        public StickerPack[] newArray(int size) {
            return new StickerPack[size];
        }
    };

    void setStickers(List<Sticker> stickers) {
        this.stickers = stickers;
        totalSize = 0;
        for (Sticker sticker : stickers) {
            totalSize += sticker.size;
        }
    }

    public void setAndroidPlayStoreLink(String androidPlayStoreLink) {
        this.androidPlayStoreLink = androidPlayStoreLink;
    }

    public void setIosAppStoreLink(String iosAppStoreLink) {
        this.iosAppStoreLink = iosAppStoreLink;
    }

    public List<Sticker> getStickers() {
        return stickers;
    }

    public long getTotalSize() {
        return totalSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(identifier);
        dest.writeString(name);
        dest.writeString(publisher);
        dest.writeString(trayImageFile);
        dest.writeString(publisherEmail);
        dest.writeString(publisherWebsite);
        dest.writeString(privacyPolicyWebsite);
        dest.writeString(licenseAgreementWebsite);
        dest.writeString(iosAppStoreLink);
        dest.writeTypedList(stickers);
        dest.writeLong(totalSize);
        dest.writeString(androidPlayStoreLink);
        dest.writeByte((byte) (isWhitelisted ? 1 : 0));
    }
}
