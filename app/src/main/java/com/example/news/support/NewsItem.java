package com.example.news.support;

import android.graphics.Bitmap;
import android.widget.ListPopupWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsItem {
    private String id;
    private String title;
    private String time;
    private String author;
    private JSONObject jsonObj;
    private String content;
    private String imageUrlStr;
    private ArrayList<Bitmap> bitmaps;
    private JSONArray keywordsArray;
    private boolean isRead = false;
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
            this.imageUrlStr = jsonObj.getString("image");
            this.keywordsArray = jsonObj.getJSONArray("keywords");
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

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public boolean getRead() {
        return isRead;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getImageUrlStr() {
        return imageUrlStr;
    }

    public String getId() {
        return id;
    }

    public JSONArray getKeywordsArray() {
        return keywordsArray;
    }

    public String getJsonString() {
        return jsonObj.toString();
    }

    public static List<NewsItem> convert(List<JSONObject> jsonObjects) {
        List<NewsItem> newsItemList = new ArrayList<>();
        for (JSONObject jsonObject : jsonObjects) {
            newsItemList.add(new NewsItem(jsonObject));
        }
        return newsItemList;
    }
}
