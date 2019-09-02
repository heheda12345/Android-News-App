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

    private static final String LOG_TAG = NewsListFragment.class.getSimpleName();
    private static final String ARG_SECTION_POS = "section_number";
    private static final String ARG_SECTION_NAME = "section_name";
    private static final String ARG_INIT_ON_CREATE = "init_on_create";
    private static final String TAG = "News List Fragment";
    private String mSectionName = "";
    private int mSectionPos = 0; // default section is "suggest section"

    private boolean mInitOnCreate = false;
    private String mKeyWord = "";
    private boolean firstCreate = true;

    protected NewsListAdapter mNewsListAdapter;
    protected int mPage = 0;
    protected boolean mRefresh = false;
    protected String mEarliestDate;
    protected String mLatestDate;
    protected NewsPageViewModel mNewsPageViewModel;
    protected SwipeRefreshLayout mRefreshLayout;

    public NewsListFragment() {
        // Required empty public constructor
    }

    public static NewsListFragment newInstance(UserConfig.Section section, int position, boolean initOnCreate) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_SECTION_NAME, section.getSectionName());
        bundle.putInt(ARG_SECTION_POS, position);
        bundle.putBoolean(ARG_INIT_ON_CREATE, initOnCreate);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static NewsListFragment newInstance(boolean initOnCreate) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_INIT_ON_CREATE, initOnCreate);
        bundle.putInt(ARG_SECTION_POS, 0);
        bundle.putString(ARG_SECTION_NAME, "");
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
            mInitOnCreate = getArguments().getBoolean(ARG_INIT_ON_CREATE);
        }
        /*构造 NewPageViewModel 用于处理数据*/
        mNewsPageViewModel = ViewModelProviders.of(this).get(NewsPageViewModel.class);

        /* 构造News list Adapter 用于新闻列表*/
        mNewsListAdapter = new NewsListAdapter(getContext(), mSectionPos);
        mEarliestDate = getCurrentTime();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        /* 每次改变section时，重新请求一次数据*/
        if (mInitOnCreate && firstCreate) {
            Log.d(LOG_TAG, "fitst create");
            setNews("", mSectionName);
            firstCreate = false;
        }

        /*从news List 的layout中构造出NewsList 的root view*/
        View root = inflater.inflate(R.layout.fragment_news_list, container, false);

        /* 从root view 中构造出recyclerView，存放新闻列表*/
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefresh = true;
                setNews(mKeyWord, mLatestDate, getCurrentTime(), mSectionName, ConstantValues.DEFAULT_NEWS_SIZE);
            }
        });

        RecyclerView mRecyclerView = root.findViewById(R.id.news_recyclerView);
        mRecyclerView.setAdapter(mNewsListAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new OnLoadMoreListener() {
            @Override
            protected void onLoading(int countNum, int lastNum) {
                Log.d("Adapter List Fragment", "Loading " + mEarliestDate);
                mPage++;
                mNewsListAdapter.setLoading();
                setNews(mKeyWord, "", mEarliestDate, mSectionName, ConstantValues.DEFAULT_NEWS_SIZE);
            }
        });

        /* 设置PageViewModel，每次有数据更新的时候更新news list*/
        mNewsPageViewModel.getVersion().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                ArrayList<JSONObject> news = mNewsPageViewModel.getNews();
                ConstantValues.NetWorkStatus status =mNewsPageViewModel.getStatus();
                if (mRefresh) {
                    mNewsListAdapter.addRefreshNews(status, news);
                    mRefresh = false;
                    mRefreshLayout.setRefreshing(false);
                    if (news.size() > 0) {
                        mLatestDate = getLatestTime(news);
                    }
                }
                else {
                    if (mPage == 0) {
                        mNewsListAdapter.setNews(status, news);
                        if (news.size() > 0) {
                            mLatestDate = getLatestTime(news);
                        }
                    }
                    else {
                        mNewsListAdapter.addNews(status, news);
                    }
                    if (news.size() > 0) {
                        mEarliestDate = getEarliestTime(news);
                    }
                }
            }
        });
        return root;
    }

    public void setNews(String keyWord) {
        setNews(keyWord, mSectionName);
    }

    public void setNews(String keyword, String sectionName) {
        setNews(keyword, "", getCurrentTime(), sectionName, ConstantValues.DEFAULT_NEWS_SIZE);
    }

    public void setNews(String keyword, String startTime, String endTime, String sectionName, int size) {
        mKeyWord = keyword;
        mSectionName = sectionName;
        mNewsPageViewModel.setInfo(new NewsCrawler.CrawlerInfo(keyword, startTime, endTime, sectionName, size));
    }

    protected String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    protected String getEarliestTime(ArrayList<JSONObject> news) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long earliestDate = Long.MAX_VALUE;
        for (JSONObject newsItem : news) {
            try {
                String d = newsItem.getString("publishTime");
                Date tempDate = new Date();
                try {
                    tempDate = df.parse(d);
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
                if (tempDate.getTime() < earliestDate) {
                    earliestDate = tempDate.getTime();
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Date newDate = new Date(earliestDate - 5000);

        return df.format(newDate);
    }

    protected String getLatestTime(ArrayList<JSONObject> news) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long latestTime = Long.MIN_VALUE;
        for (JSONObject newsItem : news) {
            try {
                String d = newsItem.getString("publishTime");
                Date tempDate = new Date();
                try {
                    tempDate = df.parse(d);
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
                if (tempDate.getTime() > latestTime) {
                    latestTime = tempDate.getTime();
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Date newDate = new Date(latestTime + 5000);

        return df.format(newDate);
    }
}