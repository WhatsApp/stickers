package com.example.samplestickerapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DownloadStickersTask extends AsyncTask<Void, Void, Void> {

    private final WeakReference<Context> contextWeakReference;
    private final List<Sticker> stickers;
    private final String stickerPackIdentifier;

    public DownloadStickersTask(Context context, List<Sticker> stickers, String stickerPackIdentifier) {
        this.contextWeakReference = new WeakReference<>(context);
        this.stickers = stickers;
        this.stickerPackIdentifier = stickerPackIdentifier;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Context context = contextWeakReference.get();
        if (context != null) {
            File stickerPackDir = new File(context.getFilesDir(), stickerPackIdentifier);
            if (!stickerPackDir.exists()) {
                stickerPackDir.mkdirs();
            }

            for (Sticker sticker : stickers) {
                try {
                    URL url = new URL("https://www.stickers.dev-error.com/" + sticker.imageFileName);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    File file = new File(stickerPackDir, sticker.imageFileName.substring(sticker.imageFileName.lastIndexOf('/') + 1));
                    FileOutputStream fos = new FileOutputStream(file);
                    InputStream is = connection.getInputStream();

                    byte[] buffer = new byte[1024];
                    int len1;
                    while ((len1 = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len1);
                    }

                    fos.close();
                    is.close();
                } catch (IOException e) {
                    Log.e("DownloadStickersTask", "Error downloading sticker", e);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        StickerDetailsActivity activity = (StickerDetailsActivity) contextWeakReference.get();
        if (activity != null) {
            activity.addStickerPackToWhatsApp(stickerPackIdentifier, stickerPackIdentifier);
        }
    }
}
