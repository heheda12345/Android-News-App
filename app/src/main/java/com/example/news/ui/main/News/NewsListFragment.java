package com.example.news.ui.main.News;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */


public class NewsListFragment extends Fragment {

    private static final String ARG_SECTION_POS = "section_number";
    private static final String ARG_SECTION_NAME = "section_name";
    private static final String TAG = "News List Fragment";
    private String mSectionName;
    private int mSectionPos;

    private NewsPageViewModel mNewsPageViewModel;
    private NewsListAdapter mNewsListAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private String mEarliestDate;
    private String mLatestDate;
    private SwipeRefreshLayout mRefreshLayout;
    private int mPage = 0;
    private boolean mRefresh = false;

    public NewsListFragment() {
        // Required empty public constructor
    }

    public static NewsListFragment newInstance(UserConfig.Section section, int position) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_SECTION_NAME, section.getSectionName());
        bundle.putInt(ARG_SECTION_POS, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSectionName = ConstantValues.ALL_SECTIONS[0];
        if (getArguments() != null) {
            mSectionName = getArguments().getString(ARG_SECTION_NAME);
            mSectionPos = getArguments().getInt(ARG_SECTION_POS);
        }
        /*构造 NewPageViewModel 用于处理数据*/
        mNewsPageViewModel = ViewModelProviders.of(this).get(NewsPageViewModel.class);
//        mNewsPageViewModel.setInfo(new NewsCrawler.CrawlerInfo("", getCurrentTime(), mSectionName));

        /* 构造News list Adapter 用于新闻列表*/
        mNewsListAdapter = new NewsListAdapter(getContext(), mSectionPos);
        mEarliestDate = getCurrentTime();

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d("PlaceFragment", "onCreateView");

        /* 每次改变section时，重新请求一次数据*/
        mNewsPageViewModel.setInfo(new NewsCrawler.CrawlerInfo("", "", getCurrentTime(), mSectionName));

        /*从news List 的layout中构造出NewsList 的root view*/
        View root = inflater.inflate(R.layout.fragment_news_list, container, false);
//        final Context rootContext = root.getContext();

        /* 从root view 中构造出recyclerView，存放新闻列表*/
        mLayoutManager = new LinearLayoutManager(getContext());
        mRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefresh = true;
                mNewsPageViewModel.setInfo(new NewsCrawler.CrawlerInfo("", mLatestDate, getCurrentTime(), mSectionName));
            }
        });

        mRecyclerView = root.findViewById(R.id.news_recyclerView);
        mRecyclerView.setAdapter(mNewsListAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new OnLoadMoreListener() {
            @Override
            protected void onLoading(int countNum, int lastNum) {
                Log.d("Adapter List Fragment", "Loading " + mEarliestDate);
                mPage++;
                mNewsListAdapter.setLoading();
                mNewsPageViewModel.setInfo(new NewsCrawler.CrawlerInfo("", "", mEarliestDate, mSectionName));
            }
        });



        /* 设置PageViewModel，每次有数据更新的时候更新news list*/
        mNewsPageViewModel.getVersion().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                ArrayList<JSONObject> news = mNewsPageViewModel.getNews();
                if (mRefresh) {
                    mNewsListAdapter.addRefreshNews(news);
                    mRefresh = false;
                    mRefreshLayout.setRefreshing(false);
                    if (news.size() > 0) {
                        mLatestDate = getLatestTime(news);
                    }
                }
                else {
                    if (mPage == 0) {
                        mNewsListAdapter.setNews(news);
                        if (news.size() > 0) {
                            mLatestDate = getLatestTime(news);
                        }
                    }
                    else {
                        mNewsListAdapter.addNews(news);
                    }
                    if (news.size() > 0) {
                        mEarliestDate = getEarliestTime(news);
                    }
                }
            }
        });
        Log.d("Placeholder", "created view");
        return root;
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    private String getEarliestTime(ArrayList<JSONObject> news) {
        String earliestDate = "";
        try {
            earliestDate = news.get(news.size() - 1).getString("publishTime");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date tempDate = new Date();
        try {
            tempDate = df.parse(earliestDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        Date newDate = new Date(tempDate.getTime() - 5000);
        Log.d(TAG, df.format(tempDate));
        Log.d(TAG, df.format(newDate));
        return df.format(newDate);
    }

    private String getLatestTime(ArrayList<JSONObject> news) {
        String latestTime = "";
        try {
            latestTime = news.get(0).getString("publishTime");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date tempDate = new Date();
        try {
            tempDate = df.parse(latestTime);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        Date newDate = new Date(tempDate.getTime() + 5000);
        Log.d(TAG, df.format(tempDate));
        Log.d(TAG, df.format(newDate));
        return df.format(newDate);
    }
}