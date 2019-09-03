package com.example.news.ui.main.Mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.news.R;
import com.example.news.data.ConstantValues;
import com.example.news.support.NewsItem;
import com.example.news.ui.main.Items.CollectionFootViewHolder;
import com.example.news.ui.main.Items.NewsItemVH;
import com.example.news.ui.main.News.NewsDetailActivity;

import java.util.List;

public class SimpleNewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<NewsItem> news;
    private String type;

    public SimpleNewsListAdapter(Context context, List<NewsItem> news, String type) {
        this.context = context;
        this.news = news;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstantValues.ItemViewType layoutType = ConstantValues.ItemViewType.values()[viewType];
        int resource = -1;
        if (layoutType == ConstantValues.ItemViewType.FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_foot_item, parent, false);
            return new CollectionFootViewHolder(context, v, type);
        }
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
            final NewsItemVH itemHolder = (NewsItemVH) holder;
            itemHolder.title.setText(news.get(position).getTitle());
            itemHolder.author.setText(news.get(position).getAuthor());
            itemHolder.time.setText(news.get(position).getTime());
            itemHolder.mCurrentPosition = position;
            itemHolder.initImages();
//            if (news.get(position).getRead()) {
//                itemHolder.setRead(true);
//            }
//            else {
//                itemHolder.setRead(false);
//            }
            List<Bitmap> bitmaps = news.get(position).getBitmaps();
            int imageNum = Math.min(itemHolder.images.length, bitmaps.size());
            for (int i = 0; i < imageNum; ++i) {
                itemHolder.images[i].setImageBitmap(bitmaps.get(i));
            }

            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNews(news.get(position));
//                    news.get(position).setRead(true);
//                    itemHolder.setRead(true);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (news.size() == 0) {
            return 1;
        }
        else {
            return news.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (news.size() == 0) {
            return ConstantValues.ItemViewType.FOOTER.ordinal();
        }
        int imgNum = news.get(position).getBitmaps().size();
        if (imgNum == 0) {
            return ConstantValues.ItemViewType.NONE.ordinal();
        }
        else if (imgNum == 1) {
            return ConstantValues.ItemViewType.ONE_SMALL.ordinal();
        }
        else if (imgNum == 2) {
            return ConstantValues.ItemViewType.ONE_BIG.ordinal();
        }
        else {
            return ConstantValues.ItemViewType.THREE.ordinal();
        }
    }

    private void showNews(NewsItem newsItem) {
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra("data", newsItem.getJsonString());
        context.startActivity(intent);
    }
}
