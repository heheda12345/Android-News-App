package com.example.news.collection;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "collection_table")
public class CollectionItem{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "newsID")
    private String mNewsID;

    public CollectionItem(@NonNull String newsID) {this.mNewsID = newsID;}

    @NonNull
    public String getNewsID() {
        return mNewsID;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CollectionItem) {
            return this.getNewsID().equals(((CollectionItem) o).getNewsID());
        }
        return super.equals(o);
    }
}