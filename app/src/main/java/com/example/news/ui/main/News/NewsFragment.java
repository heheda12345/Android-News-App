package com.example.news.ui.main.News;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.news.R;
import com.example.news.data.BitMapCache;
import com.example.news.data.ConstantValues;
import com.example.news.data.UserConfig;

public class NewsFragment extends Fragment {

    private TabLayout mSectionTabLayout;
    private ViewPager mNewsViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    public NewsFragment() {
        // Required empty public constructor
    }

    public static NewsFragment newInstance() {
        Log.d("NewsFragment", "newInstance");
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_section, container, false);
        mNewsViewPager = view.findViewById(R.id.sections_viewPager);
        mSectionTabLayout = view.findViewById(R.id.sections_tabLayout);
        for (int i = 0; i < UserConfig.getInstance().getSectionNum(); ++i) {
            mSectionTabLayout.addTab(mSectionTabLayout.newTab());
        }
        mNewsViewPager.setAdapter(mSectionsPagerAdapter);
        mSectionTabLayout.setupWithViewPager(mNewsViewPager);

        BitMapCache.getInstance().addAllSections(UserConfig.getInstance().getSectionNum());
        return view;
    }

}
