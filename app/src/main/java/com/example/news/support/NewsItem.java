package com.example.news.support;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;

public class NewsItem {
    public String id;
    public String title;
    public String time;
    public String author;
    public JSONObject jsonObj;
    public String content;
    public ArrayList<Bitmap> bitmaps;
    public ArrayList<String> bitmapPaths; // 应和bitmaps一一对应
    public boolean bitmapLoaded = false;

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
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public NewsItem(JSONObject jsonObj, String[] imgPath, boolean bitmapLoaded) {
        this(jsonObj);
        setImages(imgPath);
        this.bitmapLoaded = bitmapLoaded;
    }

    public void setImages(ArrayList<Bitmap> bitmaps, ArrayList<String> bitmapPaths) {
        this.bitmaps = bitmaps;
        this.bitmapPaths = bitmapPaths;
        this.bitmapLoaded = true;
    }

    public void setImages(String[] imgPath) {
        for (String s : imgPath) {
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

}
