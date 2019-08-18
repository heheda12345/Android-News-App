package com.example.news.collection;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.os.AsyncTask;

import java.util.Collection;
import java.util.List;

class CollectionRepository {

    private CollectionDao mCollectionDao;
    private LiveData<List<CollectionItem>> mAllItems;

    CollectionRepository(Application application) {
        CollectionRoomDatabase db = (CollectionRoomDatabase) CollectionRoomDatabase.getDatabase(application);
        mCollectionDao = db.collectionDao();
        mAllItems = mCollectionDao.getAllItems();
    }

    public LiveData<List<CollectionItem>> getAllItems() {
        return mAllItems;
    }

    void insert(CollectionItem item) {
        new insertAsyncTask(mCollectionDao).execute(item);
    }
    void erase(CollectionItem item) { new eraseAsyncTask(mCollectionDao).execute(item); }

    static class insertAsyncTask extends AsyncTask<CollectionItem, Void, Void> {

        private CollectionDao mAsyncTaskDao;

        insertAsyncTask(CollectionDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final CollectionItem... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    static class eraseAsyncTask extends AsyncTask<CollectionItem, Void, Void> {

        private CollectionDao mAsyncTaskDao;

        eraseAsyncTask(CollectionDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final CollectionItem... params) {
            mAsyncTaskDao.erase(params[0]);
            return null;
        }
    }
}
