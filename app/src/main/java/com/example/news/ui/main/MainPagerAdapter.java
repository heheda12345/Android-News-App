package com.example.news.ui.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.example.news.ui.main.Mine.MineFragment;
import com.example.news.ui.main.News.NewsFragment;

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    int mNumTabs;
    Context mContext;

    public MainPagerAdapter(Context context, FragmentManager fm, int numTabs) {
        super(fm);
        this.mNumTabs = numTabs;
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new NewsFragment();
            case 1: return new MineFragment();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return mNumTabs;
    }

}
