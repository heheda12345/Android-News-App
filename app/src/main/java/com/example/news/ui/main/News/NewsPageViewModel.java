package com.example.news.ui.main.News;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.news.support.NewsCrawler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class NewsPageViewModel extends ViewModel {
    private static final String LOG_TAG =
            NewsCrawler.class.getSimpleName();

    private MutableLiveData<NewsCrawler.CrawlerInfo> mCrawlerInfo = new MutableLiveData<>();
    private ArrayList<JSONObject> mNews;
    private LiveData<String> mVersion = Transformations.map(mCrawlerInfo, new Function<NewsCrawler.CrawlerInfo, String>() {
        @Override
        public String apply(NewsCrawler.CrawlerInfo input) {
            NewsCrawler newsCrawler = new NewsCrawler(input);
            newsCrawler.start();
            try {
                newsCrawler.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mNews = newsCrawler.getNewsResp();
            return input + "-" + mNews.hashCode();
        }
    });

    public void setInfo(NewsCrawler.CrawlerInfo info) {
        Log.d("ViewModel", "SetInfo");
        mCrawlerInfo.setValue(info);
    }

    public LiveData<String> getVersion() {
        return mVersion;
    }

    public ArrayList<JSONObject> getNews() {
        return mNews;
    }

}