package com.example.news.ui.main.News;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.example.news.R;

import java.util.List;

public class NewsFragment extends Fragment {

    private TabLayout mSectionTabLayout;
    private ViewPager mNewsViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static String[] sections = {
            "综合",
            "还是综合",
            "Games",
            "CS",
            "Good",
            "Hello",
            "daishen",
            "Tsinghua"
    };

    public NewsFragment() {
        // Required empty public constructor
    }

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), sections);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        mNewsViewPager = view.findViewById(R.id.sections_viewPager);
        mSectionTabLayout = view.findViewById(R.id.sections_tabLayout);
        for (int i = 0; i < sections.length; ++i) {
            mSectionTabLayout.addTab(mSectionTabLayout.newTab());
        }
        mNewsViewPager.setAdapter(mSectionsPagerAdapter);
        mSectionTabLayout.setupWithViewPager(mNewsViewPager);

        return view;
    }

}
