package com.example.news.ui.main.Mine;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.news.R;
import com.example.news.collection.CollectionItem;
import com.example.news.data.ConstantValues;
import com.example.news.ui.main.Items.NewsItemVH;

import java.util.List;

public class CollectionListAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<CollectionItem> news;

    public CollectionListAdapter(Context context, List<CollectionItem> news) {
        this.context = context;
        this.news = news;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstantValues.ItemViewType layoutType = ConstantValues.ItemViewType.values()[viewType];
        int resource = -1;
        switch (layoutType) {
            case NONE: resource = R.layout.news_item_none; break;
            case ONE_SMALL:resource = R.layout.news_item_onesmall; break;
            case ONE_BIG: resource = R.layout.news_item_onebig; break;
            case THREE: resource = R.layout.news_item_three; break;
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        return new NewsItemVH(context, v, layoutType);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof NewsItemVH) {

        }
    }

    @Override
    public int getItemCount() {
        return news.size() + 1;
    }
}
