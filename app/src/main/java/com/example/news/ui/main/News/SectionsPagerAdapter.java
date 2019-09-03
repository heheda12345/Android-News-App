package com.example.news.ui.main.News;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.example.news.data.UserConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

//    @StringRes
//    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private UserConfig userConfig = UserConfig.getInstance();
    private HashMap<String, NewsListFragment> fragments;

    SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new HashMap<>();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return userConfig.getSection(position).getSectionName();
    }

    @Override
    public Fragment getItem(int position) {
        UserConfig.Section section = userConfig.getSection(position);
        if (fragments.containsKey(section.getSectionName())) {
            return fragments.get(section.getSectionName());
        }
        else {
            NewsListFragment fragment = NewsListFragment.newInstance(section, position, true);
            fragments.put(section.getSectionName(), fragment);
            return fragment;
        }
    }

    @Override
    public int getCount() {
        return userConfig.getSectionNum();
    }

    @Override
    public int getItemPosition(@NonNull Object obj) {
        return PagerAdapter.POSITION_NONE;
    }

}