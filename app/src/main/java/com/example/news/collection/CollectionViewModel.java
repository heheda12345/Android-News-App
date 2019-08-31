package com.example.news.collection;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.news.support.NewsItem;

import java.util.List;
import java.util.Objects;

public class CollectionViewModel extends AndroidViewModel {

    private static final String LOG_TAG = CollectionViewModel.class.getSimpleName();;
    private CollectionRepository mRepository;

    private LiveData<List<CollectionItem>> mAllItems;

    public CollectionViewModel (Application application) {
        super(application);
        mRepository = new CollectionRepository(application);
        mAllItems = mRepository.getAllItems();
    }

    public LiveData<List<CollectionItem>> getAllItems() { return mAllItems; }

    public void insert(CollectionItem item) { mRepository.insert(item); }

    public boolean contains(CollectionItem item) {
        try {
            return mAllItems.getValue().contains(item);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            return false;
        }

    }

    public void erase(CollectionItem item) {
        mRepository.erase(item);
    }

    public static void updateNewsItem(String NewsItem) {}
    public static NewsItem getNewsItem(String newsID) { return null; }
    public static void saveNewsList(String key, String[] newsID) {}
    public static NewsItem[] getNewsList(String key) { return null;}
}