package com.example.news.collection;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {CollectionItem.class, NewsListItem.class, NewsSavedItem.class}, version = 3, exportSchema = false)
public abstract class CollectionRoomDatabase extends RoomDatabase {

    public abstract CollectionDao collectionDao();
    private static CollectionRoomDatabase INSTANCE;

    static CollectionRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CollectionDao.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CollectionRoomDatabase.class, "collectionDatabase")
                            // Wipes and rebuilds instead of migrating
                            // if no Migration object.
                            // Migration is not part of this practical.
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
