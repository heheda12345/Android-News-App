package com.example.news.ui.main.News;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.example.news.data.UserConfig;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

//    @StringRes
//    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private UserConfig userConfig = UserConfig.getInstance();

    SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return userConfig.getSection(position).getSectionName();
    }

    @Override
    public Fragment getItem(int position) {
        return NewsListFragment.newInstance(userConfig.getSection(position), position, true);
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