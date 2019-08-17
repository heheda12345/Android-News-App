package com.example.news.data;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BitMapCache {
    private ArrayList<HashMap<String, Bitmap>> maps;

    private static BitMapCache instance = new BitMapCache();

    private BitMapCache() {
        maps = new ArrayList<>();
    }

    public static BitMapCache getInstance() {
        return instance;
    }

    public void add(int pos, String url, Bitmap bitmap) {
        maps.get(pos).put(url, bitmap);
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

    public void removeSectionCache(int pos) {
        maps.remove(pos);
    }

    public void addAllSections(int numSection) {
         for (int i = 0; i < numSection; ++i) {
             maps.add(new HashMap<String, Bitmap>());
         }
    }
}
