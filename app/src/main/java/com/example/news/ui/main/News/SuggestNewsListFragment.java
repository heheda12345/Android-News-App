package com.example.news.ui.main.News;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.news.R;
import com.example.news.data.ConstantValues;
import com.example.news.data.UserConfig;
import com.example.news.support.NewsCrawler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SuggestNewsListFragment extends NewsListFragment {
    private List<Map.Entry<String, Double>> keyWords;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setSuggestNews("", getCurrentTime());
        View root = inflater.inflate(R.layout.fragment_news_list, container, false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefresh = true;
                setSuggestNews(mLatestDate, getCurrentTime());
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
                setSuggestNews("", mEarliestDate);
            }
        });

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

    public void setSuggestNews(String startTime, String endTime) {
        int allNum = ConstantValues.DEFAULT_NEWS_SIZE;
        keyWords = UserConfig.getInstance().getKeyWords(allNum);
        double allScore = 0;
        for (Map.Entry<String, Double> entry : keyWords) {
            allScore += entry.getValue();
        }
        List<Integer> newsNum = new ArrayList<>();
        for (Map.Entry<String, Double> entry : keyWords) {
            int num = (int)((allNum + 1) * entry.getValue() / allScore);
            newsNum.add(num);
        }

        for (int i = 0; i < newsNum.size(); ++i) {
            if (newsNum.get(i) > 0) {
                mNewsPageViewModel.setInfo(new NewsCrawler.CrawlerInfo(keyWords.get(i).getKey(), startTime, endTime, "", newsNum.get(i)));
            }
        }
    }

}
