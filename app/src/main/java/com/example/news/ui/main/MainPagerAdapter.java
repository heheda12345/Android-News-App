package com.example.news.ui.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.example.news.ui.main.News.NewsFragment;

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    int numTabs;

    public MainPagerAdapter(FragmentManager fm, int numTabs) {
        super(fm);
        this.numTabs = numTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new NewsFragment();
            case 1: return new NewsFragment();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return numTabs;
    }

}
