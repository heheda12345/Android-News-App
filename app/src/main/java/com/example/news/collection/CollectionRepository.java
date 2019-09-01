package com.example.news.collection;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.os.AsyncTask;
import android.util.Log;

import com.example.news.support.NewsItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class CollectionRepository {

    private static final String TAG = CollectionRepository.class.getSimpleName();
    private static CollectionRepository instance;
    private CollectionDao mCollectionDao;
    private LiveData<List<CollectionItem>> mAllItems;
    private LiveData<List<NewsSavedItem>> mAllNews;
    private LiveData<List<NewsListItem>> mAllList;

    private CollectionRepository(Application application) {
        CollectionRoomDatabase db = CollectionRoomDatabase.getDatabase(application);
        mCollectionDao = db.collectionDao();
        mAllItems = mCollectionDao.getAllItems();
        mAllNews = mCollectionDao.getAllNews();
        mAllList = mCollectionDao.getAllList();
    }

    public static void setApp(Application app) {
        instance = new CollectionRepository(app);
    }

    public static CollectionRepository getInstance() {
        return instance;
    }

    public LiveData<List<CollectionItem>> getAllItems() {
        return mAllItems;
    }
    public LiveData<List<NewsSavedItem>> getAllNews() { return mAllNews; }
    public LiveData<List<NewsListItem>> getAllList() {return mAllList; }

    public void insert(CollectionItem item) { new insertAsyncTask(mCollectionDao).execute(item); }
    public void insert(NewsSavedItem item) { new insertNewsAsyncTask(mCollectionDao).execute(item); }
    public void insert(NewsListItem item) { new insertListAsyncTask(mCollectionDao).execute(item); }

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

    static class insertNewsAsyncTask extends AsyncTask<NewsSavedItem, Void, Void> {

        private CollectionDao mAsyncTaskDao;

        insertNewsAsyncTask(CollectionDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final NewsSavedItem... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    static class insertListAsyncTask extends AsyncTask<NewsListItem, Void, Void> {

        private CollectionDao mAsyncTaskDao;

        insertListAsyncTask(CollectionDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final NewsListItem... params) {
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
