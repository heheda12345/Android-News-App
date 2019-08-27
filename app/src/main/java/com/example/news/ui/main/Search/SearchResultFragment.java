package com.example.news.ui.main.Search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.news.R;
import com.example.news.ui.main.News.NewsPageViewModel;

public class SearchResultFragment extends Fragment {
    private String LOG_TAG = getClass().getSimpleName();
    private NewsPageViewModel newsPageViewModel;
    private SearchResultAdapter searchResultAdapter;

    public SearchResultFragment() {

    }

    public static SearchResultFragment newInstance() {
        SearchResultFragment fragment = new SearchResultFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "On Create Search Detail" );
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);
        return view;
    }
}
