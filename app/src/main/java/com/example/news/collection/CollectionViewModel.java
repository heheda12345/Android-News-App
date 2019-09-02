package com.example.news.collection;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.news.support.NewsItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CollectionViewModel extends AndroidViewModel {

    private static final String LOG_TAG = CollectionViewModel.class.getSimpleName();;
    private CollectionRepository mRepository;

    private LiveData<List<CollectionItem>> mAllItems;
    private LiveData<List<NewsSavedItem>> mAllNews;
    private LiveData<List<NewsListItem>> mAllList;

    public CollectionViewModel (Application application) {
        super(application);
        mRepository = CollectionRepository.getInstance();
        mAllItems = mRepository.getAllItems();
        mAllNews = mRepository.getAllNews();
        mAllList = mRepository.getAllList();
    }

    public LiveData<List<CollectionItem>> getAllItems() { return mAllItems; }
    public LiveData<List<NewsSavedItem>> getAllNews() { return mAllNews; }
    public LiveData<List<NewsListItem>> getAllList() { return mAllList; }

    public void insert(CollectionItem item) { mRepository.insert(item); }

    public boolean contains(CollectionItem item) {
        try {
            return mAllItems.getValue().contains(item);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
            return false;
        }

    }

    public void erase(CollectionItem item) {
        mRepository.erase(item);
    }

    public void updateNewsItem(NewsItem newsItem) { mRepository.insert(new NewsSavedItem(newsItem));}

    public NewsItem getNewsItem(String newsID) { // 找不到就返回null
        Log.d(LOG_TAG, "getNewsItem: " + newsID);
        try {
            List<NewsSavedItem> l = mAllNews.getValue();
            int x = l.indexOf(new NewsSavedItem(newsID, "", "", false, false));
            return l.get(x).toNewsItem();
        } catch (Exception e) {
            // 如果找不到会抛异常，懒得好好写了
            Log.e(LOG_TAG, e.getMessage());
//            e.printStackTrace();
            return null;
        }
    }

    public void saveNewsList(String key, String[] newsID) {
        mRepository.insert(new NewsListItem(key.trim(), newsID));
    }

    public ArrayList<NewsItem> getNewsList(String key) {
        ArrayList<NewsItem> ret = new ArrayList<>();
        try {
            List<NewsListItem> l = mAllList.getValue();
            int x = l.indexOf(new NewsListItem(key.trim(), new String[]{}));
            String[] ids = l.get(x).ids.split(";");
            for (String id : ids) {
                NewsItem item = getNewsItem(id);
                if (item != null)
                    ret.add(item);
            }
        } catch (Exception e) {
            // 如果找不到这个列表会抛异常，懒得好好写了
            Log.e(LOG_TAG, e.getMessage());
//            e.printStackTrace();
        }
        return ret;
    }

    public void updateCollection() {
        List<CollectionItem> items = mAllItems.getValue();
        String[] ids = new String[items.size()];
        for (int i=0; i<items.size(); i++)
            ids[i] = items.get(i).getNewsID();
        saveNewsList("CCC", ids);
    }
}