package com.example.news.ui.main.Mine;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.news.R;
import com.example.news.collection.CollectionItem;
import com.example.news.collection.CollectionViewModel;

import java.util.List;

public class CollectionActivity extends AppCompatActivity {
    private CollectionViewModel collectionViewModel;
    private List<CollectionItem> news;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        collectionViewModel = ViewModelProviders.of(this).get(CollectionViewModel.class);
        news = collectionViewModel.getAllItems().getValue();
        recyclerView = findViewById(R.id.collection_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

    }
}
