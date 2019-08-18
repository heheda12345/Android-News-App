package com.example.news.collection;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface CollectionDao {
    @Insert
    void insert(CollectionItem item);

    @Delete
    void erase(CollectionItem item);

    @Query("DELETE FROM collection_table")
    void deleteAll();

    @Query("SELECT * from collection_table")
    LiveData<List<CollectionItem>> getAllItems();
}
