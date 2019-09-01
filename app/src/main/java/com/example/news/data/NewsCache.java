package com.example.news.data;

import android.arch.lifecycle.ViewModelProviders;

import com.example.news.collection.CollectionRepository;
import com.example.news.collection.CollectionViewModel;
import com.example.news.collection.NewsListItem;
import com.example.news.collection.NewsSavedItem;
import com.example.news.support.NewsItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewsCache {
    private List<HashMap<String, NewsItem>> maps;

    private static NewsCache instance = new NewsCache();

    private NewsCache() { maps = new ArrayList<>(); }

    public static NewsCache getInstance() { return instance; }

    public void add(int pos, String newsID, NewsItem item) {
        CollectionRepository.getInstance().insert(new NewsSavedItem(item));
        maps.get(pos).put(newsID, item);
    }

    public void clear(int pos) { maps.get(pos).clear(); }

    public NewsItem get(int pos, String newsID) { return maps.get(pos).get(newsID); }

    public boolean contains(int pos, String newsID) { return maps.get(pos).containsKey(newsID); }

    public void addAllSections(int numSections) {
        for (int i = 0; i < numSections; ++i) {
            maps.add(new HashMap<String, NewsItem>());
        }
    }

    public void addSection() { maps.add(new HashMap<String, NewsItem>()); }

    public void removeSection(int pos) {maps.remove(pos); }
}
