package com.example.news.support;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageCrawler extends Thread {
    private static final String LOG_TAG =
            ImageCrawler.class.getSimpleName();
    private Bitmap bitmap;
    private String url;

    public ImageCrawler(String url) {
        this.url = url;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public void run() {
        try {
//            Log.d(LOG_TAG, url);
            URL imgUrl = new URL(url);
//            Log.d(LOG_TAG, url);
            HttpURLConnection conn = (HttpURLConnection) imgUrl
                    .openConnection();
            conn.setRequestMethod("GET");
//            Log.d(LOG_TAG, "resp-code:");
            InputStream is = conn.getInputStream();
//            Log.d(LOG_TAG, "resp-code:");
            bitmap = BitmapFactory.decodeStream(is);
//            Log.d(LOG_TAG, "resp-code:");
            is.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }
//        Log.d(LOG_TAG, bitmap == null ? "null" : bitmap.toString());
    }

}