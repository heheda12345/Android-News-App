package com.example.news.collection;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.example.news.support.NewsItem;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "news_table")
public class NewsSavedItem{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "newsID")
    public String id;
//    @ColumnInfo(name = "title")
//    public String title;
//    @ColumnInfo(name = "time")
//    public String time;
//    @ColumnInfo(name = "author")
//    public String author;
    @ColumnInfo(name = "jsonObj")
    public String jsonObj;
//    @ColumnInfo(name = "content")
//    public String content;
    @ColumnInfo(name = "imgPaths")
    public String imgPath;
    @ColumnInfo(name = "bitmapLoaded")
    public boolean bitmapLoaded = false;

    public NewsSavedItem(@NonNull String id, String jsonObj, String imgPath, boolean bitmapLoaded) {
        this.id = id;
        this.jsonObj = jsonObj;
        this.imgPath = imgPath;
        this.bitmapLoaded = bitmapLoaded;
    }

    public NewsSavedItem(@NonNull NewsItem item) {
        id = item.id;
//        title = item.title;
//        time = item.time;
//        author = item.author;
        jsonObj = item.jsonObj.toString();
//        content = item.content;
        StringBuilder builder = new StringBuilder();
        for (String x: item.bitmapPaths)
            builder.append(x).append(";");
        imgPath = builder.toString();
        bitmapLoaded = item.bitmapLoaded;
    }

    NewsItem toNewsItem() throws JSONException  {
        return new NewsItem(new JSONObject(jsonObj), imgPath.split(";"), bitmapLoaded);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NewsSavedItem) {
            return this.id.equals(((NewsSavedItem) o).id);
        }
        return super.equals(o);
    }
}