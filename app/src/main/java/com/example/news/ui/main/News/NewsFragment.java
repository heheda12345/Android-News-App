package com.example.news.ui.main.News;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.news.MainActivity;
import com.example.news.R;
import com.example.news.data.BitMapCache;
import com.example.news.data.ConstantValues;
import com.example.news.data.UserConfig;

public class NewsFragment extends Fragment {

    private TabLayout mSectionTabLayout;
    private ViewPager mNewsViewPager;
    private Button mPopSectionsButton;
    private SectionsPopWindow mPopWindow;
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
        mPopSectionsButton = view.findViewById(R.id.popSections_button);

        for (int i = 0; i < UserConfig.getInstance().getSectionNum(); ++i) {
            mSectionTabLayout.addTab(mSectionTabLayout.newTab());
        }
        mNewsViewPager.setAdapter(mSectionsPagerAdapter);
        mSectionTabLayout.setupWithViewPager(mNewsViewPager);

        mPopSectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Fragment", "BUtton Clicked");
                mPopWindow = new SectionsPopWindow(getActivity(), sectionsOnClick);
                mPopWindow.showAtLocation(v, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mPopWindow.backGroundAlpha(getActivity(), 1f);
                    }
                });
            }
        });

        BitMapCache.getInstance().addAllSections(UserConfig.getInstance().getSectionNum());
        return view;
    }

    private View.OnClickListener sectionsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sectionsChangeHandler();
        }
    };

    private void sectionsChangeHandler() {
        Toast.makeText(getActivity(), "Change Section", Toast.LENGTH_LONG);
    }

}
