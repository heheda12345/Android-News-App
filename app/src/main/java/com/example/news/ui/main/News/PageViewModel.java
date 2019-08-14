package com.example.news.ui.main.News;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.example.news.support.NewsCrawler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PageViewModel extends ViewModel {
    private static final String LOG_TAG =
            NewsCrawler.class.getSimpleName();

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private ArrayList<JSONObject> mNews;
    private LiveData<String> mVersion = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            NewsCrawler newsCrawler = new NewsCrawler();
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

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<String> getVersion() {
        return mVersion;
    }

    public ArrayList<JSONObject> getNews() {
        return mNews;
    }

}