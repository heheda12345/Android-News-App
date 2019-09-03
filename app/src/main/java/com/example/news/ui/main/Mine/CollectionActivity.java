package com.example.news.ui.main.Mine;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.news.R;
import com.example.news.collection.CollectionViewModel;
import com.example.news.support.NewsItem;

import java.util.List;

public class CollectionActivity extends AppCompatActivity {
    private CollectionViewModel db;
    private List<NewsItem> news;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        db = ViewModelProviders.of(this).get(CollectionViewModel.class);
        news = db.getNewsList("CCC");
        recyclerView = findViewById(R.id.collection_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        CollectionListAdapter collectionListAdapter = new CollectionListAdapter(this, news);
        recyclerView.setAdapter(collectionListAdapter);
        recyclerView.setHasFixedSize(true);
    }
}
