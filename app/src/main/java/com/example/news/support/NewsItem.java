package com.example.news.support;

import android.graphics.Bitmap;
import android.widget.ListPopupWindow;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
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
    public ArrayList<String> bitmapPaths; // 应和bitmaps一一对应


    public NewsItem(JSONObject jsonObj) {
        this.jsonObj = jsonObj;
        bitmaps = new ArrayList<>();
        bitmapPaths = new ArrayList<>();
        try {
            this.id = jsonObj.getString("newsID").trim();
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

    public NewsItem(JSONObject jsonObj, String[] imgPath, boolean bitmapLoaded, boolean isRead) {
        this(jsonObj);
        setImages(imgPath);
        this.bitmapLoaded = bitmapLoaded;
        this.isRead = isRead;
    }

    public void setImages(ArrayList<Bitmap> bitmaps, ArrayList<String> bitmapPaths) {
        this.bitmaps = bitmaps;
        this.bitmapPaths = bitmapPaths;
        this.bitmapLoaded = true;
    }

    public void setImages(String[] imgPath) {
        for (String s : imgPath) {
            if (s.length() < 5)
                continue;
            Bitmap bitmap = BitmapFactory.decodeFile(s);
            this.bitmaps.add(bitmap);
            this.bitmapPaths.add(s);
        }
        this.bitmapLoaded = true;
    }

    public void setImages(ArrayList<Bitmap> bitmaps) {
        this.bitmaps = new ArrayList<>();
        this.bitmapPaths = new ArrayList<>();
        for (Bitmap bitmap: bitmaps) {
            File file = new File(Environment.getExternalStorageDirectory(), bitmap.hashCode()+".jpg");
            try {
                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                continue;
            }
            this.bitmaps.add(bitmap);
            this.bitmapPaths.add(file.getAbsolutePath());
        }
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

    public boolean isBitmapLoaded() {
        return bitmapLoaded;
    }

}
