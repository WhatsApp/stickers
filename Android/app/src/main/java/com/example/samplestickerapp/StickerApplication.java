package com.example.samplestickerapp;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class StickerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
