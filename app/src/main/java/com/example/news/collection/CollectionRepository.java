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
    private List<ConfigItem> mConfig;

    private CollectionRepository(Application application) {
        CollectionRoomDatabase db = CollectionRoomDatabase.getDatabase(application);
        mCollectionDao = db.collectionDao();
        getConfigThread td = new getConfigThread(mCollectionDao, this);
        td.start();
        mAllItems = mCollectionDao.getAllItems();
        mAllNews = mCollectionDao.getAllNews();
        mAllList = mCollectionDao.getAllList();
        try {
            td.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public LiveData<List<NewsListItem>> getAllList() { return mAllList; }
    public List<ConfigItem> getConfig() {
        return mConfig;
    }
    void updateConfig() {
        new getConfigThread(mCollectionDao, this).start();
    }

    public void insert(CollectionItem item) { new insertAsyncTask(mCollectionDao).execute(item); }
    public void insert(NewsSavedItem item) { new insertNewsAsyncTask(mCollectionDao).execute(item); }
    public void insert(NewsListItem item) { new insertListAsyncTask(mCollectionDao).execute(item); }
    public void insert(ConfigItem item) {new insertConfigAsyncTask(mCollectionDao, this).execute(item); }

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

    static class insertConfigAsyncTask extends AsyncTask<ConfigItem, Void, Void> {

        private CollectionDao mAsyncTaskDao;
        private CollectionRepository repo;

        insertConfigAsyncTask(CollectionDao dao, CollectionRepository repo) {
            mAsyncTaskDao = dao;
            this.repo = repo;
        }

        @Override
        protected Void doInBackground(final ConfigItem... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            repo.updateConfig();
        }
    }

    static class getConfigThread extends Thread {
        private CollectionDao mAsyncTaskDao;
        private CollectionRepository repo;
        getConfigThread(CollectionDao dao, CollectionRepository repo) {
            mAsyncTaskDao = dao;
            this.repo = repo;
        };
        @Override
        public void run() {
            repo.mConfig = mAsyncTaskDao.getConfig();
            while (repo.mConfig == null) {
                yield();
            }
        }
    }

}
