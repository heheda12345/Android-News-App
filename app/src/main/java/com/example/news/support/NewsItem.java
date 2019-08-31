package com.example.news.support;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsItem {
    private String id;
    private String title;
    private String time;
    private String author;
    private JSONObject jsonObj;
    private String content;
    private ArrayList<Bitmap> bitmaps;
    private boolean bitmapLoaded = false;

    public NewsItem(JSONObject jsonObj) {
        this.jsonObj = jsonObj;
        bitmaps = new ArrayList<>();
        try {
            this.id = jsonObj.getString("newsID");
            this.title = jsonObj.getString("title");
            this.time = jsonObj.getString("publishTime");
            this.content = jsonObj.getString("content");
            this.author = jsonObj.getString("publisher");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setImages(ArrayList<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
        this.bitmapLoaded = true;
    }

    public ArrayList<Bitmap> getBitmaps() {
        return bitmaps;
    }

}
