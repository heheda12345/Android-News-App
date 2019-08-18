package com.example.news.ui.main.News;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.news.R;
import com.example.news.data.UserConfig;
import com.example.news.ui.main.News.PlaceholderFragment;

import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

//    @StringRes
//    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final String[] mSectionList;
    UserConfig userConfig = UserConfig.getInstance();

    public SectionsPagerAdapter(FragmentManager fm, final String[] sectionsList) {
        super(fm);
        mSectionList = sectionsList;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return userConfig.getSection(position).getSectionName();
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(userConfig.getSection(position));
    }

    @Override
    public int getCount() {
        return userConfig.getSectionNum();
    }
}