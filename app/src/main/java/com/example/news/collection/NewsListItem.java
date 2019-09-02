package com.example.news.collection;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "news_list_table")
public class NewsListItem {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "cid")
    public String cid;
    @ColumnInfo(name = "ids")
    public String ids;

    public NewsListItem(String cid, String ids) {
        this.cid = cid;
        this.ids = ids;
    }
    public NewsListItem(String key, String[] ids) {
        this.cid = key;
        StringBuilder builder = new StringBuilder();
        for (String id : ids) builder.append(id+";");
        this.ids = builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NewsListItem) {
            return this.cid.equals(((NewsListItem) o).cid);
        }
        return super.equals(o);
    }
}
