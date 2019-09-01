package com.example.news.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

public class BitMapCache {
    private ArrayList<HashMap<String, Bitmap>> maps;

    private static BitMapCache instance = new BitMapCache();

    private BitMapCache() {
        maps = new ArrayList<>();
    }

    public static BitMapCache getInstance() {
        return instance;
    }

    public String add(int pos, String url, Bitmap bitmap) throws IOException {
        File file = new File(Environment.getExternalStorageDirectory(), url.hashCode()+".jpg");
        FileOutputStream fOut = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        fOut.flush();
        fOut.close();
        maps.get(pos).put(url, bitmap);
        return file.getAbsolutePath();
    }

    public void clear(int pos) {
        maps.get(pos).clear();
    }

    public Bitmap get(int pos, String url) {
        return maps.get(pos).get(url);
    }

    public boolean contains(int pos, String url) {
        return maps.get(pos).containsKey(url);
    }

    public void addAllSections(int numSection) {
         for (int i = 0; i < numSection; ++i) {
             maps.add(new HashMap<String, Bitmap>());
         }
    }

    public void addSection() {
        maps.add(new HashMap<String, Bitmap>());
    }

    public void removeSection(int pos) {
        maps.remove(pos);
    }
}
