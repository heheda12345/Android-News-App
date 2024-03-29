package com.example.news.ui.main.News;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import com.example.news.R;
import com.example.news.data.BitMapCache;
import com.example.news.data.NewsCache;
import com.example.news.data.UserConfig;

public class NewsFragment extends Fragment {

    private SectionsPopWindow mPopWindow;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private BitMapCache bitMapCache;
    private NewsCache newsCache;

    public NewsFragment() {
        // Required empty public constructor
    }

    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        bitMapCache = BitMapCache.getInstance();
        newsCache = NewsCache.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_section, container, false);
        ViewPager mNewsViewPager = view.findViewById(R.id.sections_viewPager);
        TabLayout mSectionTabLayout = view.findViewById(R.id.sections_tabLayout);
        Button mPopSectionsButton = view.findViewById(R.id.popSections_button);

        for (int i = 0; i < UserConfig.getInstance().getSectionNum(); ++i) {
            mSectionTabLayout.addTab(mSectionTabLayout.newTab());
        }
        mNewsViewPager.setAdapter(mSectionsPagerAdapter);
        mSectionTabLayout.setupWithViewPager(mNewsViewPager);

        mPopSectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow = new SectionsPopWindow(getActivity(), removeSectionOnClick, addSectionOnClick);
                mPopWindow.showAtLocation(v, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mPopWindow.backGroundAlpha(getActivity(), 1f);
                    }
                });
            }
        });

        bitMapCache.addAllSections(UserConfig.getInstance().getSectionNum());
        newsCache.addAllSections(UserConfig.getInstance().getSectionNum());
        return view;
    }

    private View.OnClickListener removeSectionOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = (int)v.getTag();
            delPage(pos);
            bitMapCache.removeSection(pos);
            newsCache.removeSection(pos);
        }
    };

    private View.OnClickListener addSectionOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = (int)v.getTag();
            addPage(pos);
            bitMapCache.addSection();
            newsCache.addSection();
        }
    };

    private void delPage(int position) {
        UserConfig.getInstance().removeSection(position);
        mSectionsPagerAdapter.notifyDataSetChanged();
        mPopWindow.updatePage();
    }

    private void addPage(int position) {
        UserConfig.getInstance().addSection(position);
        mSectionsPagerAdapter.notifyDataSetChanged();
        mPopWindow.updatePage();
    }

}
