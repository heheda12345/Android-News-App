package com.example.news.ui.main.News;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.example.news.data.UserConfig;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

//    @StringRes
//    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    UserConfig userConfig = UserConfig.getInstance();

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return userConfig.getSection(position).getSectionName();
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a NewsListFragment (defined as a static inner class below).
        return NewsListFragment.newInstance(userConfig.getSection(position), position);
    }

    @Override
    public int getCount() {
        return userConfig.getSectionNum();
    }

    @Override
    public int getItemPosition(Object obj) {
        return PagerAdapter.POSITION_NONE;
    }

}