package com.example.news.ui.main.Mine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.news.R;
import com.example.news.data.NewsCache;
import com.example.news.data.UserConfig;
import com.example.news.support.NewsItem;
import com.r0adkll.slidr.Slidr;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private NewsCache newsCache;
    private List<NewsItem> news;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        if (UserConfig.getInstance().getNightMode()) {
            setDark();
        }
        else {
            setBright();
        }
        newsCache = NewsCache.getInstance();
        news = newsCache.getAllNews();
        recyclerView = findViewById(R.id.collection_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        SimpleNewsListAdapter simpleNewsListAdapter = new SimpleNewsListAdapter(this, news, MineFragment.NEWS_LIST_HISTORY);
        recyclerView.setAdapter(simpleNewsListAdapter);
        recyclerView.setHasFixedSize(true);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Slidr.attach(this);
    }

    public void setBright() {
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.alpha = 1.0f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(p);
    }

    public void setDark() {
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.alpha = 0.3f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(p);
    }
}
