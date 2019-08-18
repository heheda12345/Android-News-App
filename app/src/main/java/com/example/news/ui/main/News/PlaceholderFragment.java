package com.example.news.ui.main.News;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

/**
 * A placeholder fragment containing a simple view.
 */


public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_SECTION_NAME = "section_name";
    private String sectionName;

    private NewsPageViewModel newsPageViewModel;

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    public static PlaceholderFragment newInstance(UserConfig.Section section) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_SECTION_NAME, section.getSectionName());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PlaceFragment", "onCreate");
        newsPageViewModel = ViewModelProviders.of(this).get(NewsPageViewModel.class);
        sectionName = ConstantValues.ALL_SECTIONS[0];
        if (getArguments() != null) {
            sectionName = getArguments().getString(ARG_SECTION_NAME);
//            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        newsPageViewModel.setInfo(new NewsCrawler.CrawlerInfo("", getCurrentTime(), sectionName));
        Log.d("Placeholder", "created");
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d("PlaceFragment", "onCreateView");
        newsPageViewModel.setInfo(new NewsCrawler.CrawlerInfo("", getCurrentTime(), sectionName));

        View root = inflater.inflate(R.layout.fragment_main, container, false);
        final Context rootCtx = root.getContext();
        final ListView listView = (ListView) root.findViewById(R.id.listView);
        newsPageViewModel.getVersion().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                ArrayList<JSONObject> news =  newsPageViewModel.getNews();
                NewsListAdapter adapter = new NewsListAdapter(rootCtx, news);
                listView.setAdapter(adapter);
            }
        });
        Log.d("Placeholder", "created view");
        return root;
    }
}