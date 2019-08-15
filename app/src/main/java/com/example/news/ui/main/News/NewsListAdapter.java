package com.example.news.ui.main.News;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.news.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<JSONObject> mNews;

    public class NewsItemVH extends RecyclerView.ViewHolder {
        public final TextView title;
        public final TextView author;
        public final TextView time;
        public NewsItemVH(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            author = v.findViewById(R.id.author);
            time = v.findViewById(R.id.time);
        }

    }

    public NewsListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof NewsItemVH) {
            NewsItemVH itemHolder = (NewsItemVH) holder;
            try {
                itemHolder.title.setText(mNews.get(position).getString("title"));
                itemHolder.author.setText(mNews.get(position).getString("publisher"));
                itemHolder.time.setText(mNews.get(position).getString("publishTime"));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e) {
                throw e;
            }
            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNews(mNews.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new NewsItemVH(v);
    }

    /**
     * 实现新闻分类改变后存入新的数据
     * */
    public void setNews(List<JSONObject> news) {
        mNews = news;
        notifyDataSetChanged();
    }

    /**
     * 实现下拉刷新功能
     * */
    public void addNews(List<JSONObject> news) {
        int changePos = mNews.size();
        mNews.addAll(news);
        notifyItemRangeChanged(changePos, getItemCount());
    }

    /**
     * 展示新闻数据
     * */

    private void showNews(JSONObject news) {
        Intent intent = new Intent(mContext, NewsDetailActivity.class);
        intent.putExtra("data", news.toString());
        mContext.startActivity(intent);
    }
}
