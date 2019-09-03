package com.example.news.collection;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CollectionDao {
    @Insert
    void insert(CollectionItem item);

    @Delete
    void erase(CollectionItem item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NewsSavedItem item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NewsListItem item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ConfigItem item);

    @Query("DELETE FROM collection_table")
    void deleteAll();

    @Query("SELECT * from collection_table")
    LiveData<List<CollectionItem>> getAllItems();

    @Query("SELECT * from news_table")
    LiveData<List<NewsSavedItem>> getAllNews();

    @Query("SELECT * from news_list_table")
    LiveData<List<NewsListItem>> getAllList();

    @Query("SELECT * from config_table")
    List<ConfigItem> getConfig();


}
