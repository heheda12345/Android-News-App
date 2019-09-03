package com.example.news.collection;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "config_table")
public class ConfigItem{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "setting")
    private String setting;

    public ConfigItem(@NonNull String info) {
        this.id = "o";
        this.setting = info;
    }

    public ConfigItem(@NonNull String id, @NonNull String setting) {
        this.id = id;
        this.setting = setting;
    }

    @NonNull
    public String getSetting() {
        return setting;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ConfigItem) {
            return this.id.equals(((ConfigItem) o).id);
        }
        return super.equals(o);
    }
}