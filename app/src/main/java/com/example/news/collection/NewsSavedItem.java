//package com.example.news.collection;
//
//import android.arch.persistence.room.ColumnInfo;
//import android.arch.persistence.room.Entity;
//import android.arch.persistence.room.PrimaryKey;
//import android.graphics.Bitmap;
//import android.support.annotation.NonNull;
//
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
//@Entity(tableName = "news_table")
//public class NewsSavedItem{
//    @PrimaryKey
//    @NonNull
//    @ColumnInfo(name = "newsID")
//    private String id;
//    @ColumnInfo(name = "title")
//    private String title;
//    @ColumnInfo(name = "time")
//    private String time;
//    @ColumnInfo(name = "author")
//    private String author;
//    @ColumnInfo(name = "jsonObj")
//    private JSONObject jsonObj;
//    @ColumnInfo(name = "content")
//    private String content;
//    private ArrayList<Bitmap> bitmaps;
//    private boolean bitmapLoaded = false;
//
//    public CollectionItem(@NonNull String newsID) {this.mNewsID = newsID;}
//
//    @NonNull
//    public String getNewsID() {
//        return mNewsID;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (o instanceof CollectionItem) {
//            return this.getNewsID().equals(((CollectionItem) o).getNewsID());
//        }
//        return super.equals(o);
//    }
//}