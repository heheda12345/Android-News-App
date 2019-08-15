package com.example.news.ui.main.News;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import com.example.news.R;
import com.example.news.data.ConstantValues;
import com.example.news.data.UserConfig;
import com.example.news.support.NewsCrawler;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */


public class NewsListFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_SECTION_NAME = "section_name";
    private String mSectionName;

    private NewsPageViewModel mNewsPageViewModel;
    private NewsListAdapter mNewsListAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    public NewsListFragment() {
        // Required empty public constructor
    }

    public static NewsListFragment newInstance(UserConfig.Section section) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_SECTION_NAME, section.getSectionName());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PlaceFragment", "onCreate");

        /*构造 NewPageViewModel 用于处理数据*/
        mNewsPageViewModel = ViewModelProviders.of(this).get(NewsPageViewModel.class);
//        mNewsPageViewModel.setInfo(new NewsCrawler.CrawlerInfo("", getCurrentTime(), mSectionName));

        /* 构造News list Adapter 用于新闻列表*/
        mNewsListAdapter = new NewsListAdapter(getContext());
        mSectionName = ConstantValues.ALL_SECTIONS[0];
        if (getArguments() != null) {
            mSectionName = getArguments().getString(ARG_SECTION_NAME);
        }

        Log.d("Placeholder", "created");
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d("PlaceFragment", "onCreateView");

        /* 每次改变section时，重新请求一次数据*/
        mNewsPageViewModel.setInfo(new NewsCrawler.CrawlerInfo("", getCurrentTime(), mSectionName));

        /*从news List 的layout中构造出NewsList 的root view*/
        View root = inflater.inflate(R.layout.fragment_news_list, container, false);
//        final Context rootContext = root.getContext();

        /* 从root view 中构造出recyclerView，存放新闻列表*/
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView = root.findViewById(R.id.news_recyclerView);
        mRecyclerView.setAdapter(mNewsListAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);


        /* 设置PageViewModel，每次有数据更新的时候更新news list*/
        mNewsPageViewModel.getVersion().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                ArrayList<JSONObject> news = mNewsPageViewModel.getNews();
                mNewsListAdapter.setNews(news);
            }
        });
        Log.d("Placeholder", "created view");
        return root;
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }
}