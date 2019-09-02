package com.example.news.ui.main.News;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.news.R;
import com.example.news.data.ConstantValues;
import com.example.news.data.NewsCache;
import com.example.news.data.UserConfig;
import com.example.news.support.ImageLoadingTask;
import com.example.news.support.NewsItem;
import com.example.news.ui.main.Items.FootViewHolder;
import com.example.news.ui.main.Items.NewsItemVH;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = "NewsListAdapter";
    private Context mContext;
    private int mSectionPos;
    private List<NewsItem> mNews;

    private boolean netWorkError = false;

    NewsListAdapter(Context context, int sectionPos) {
        mContext = context;
        mSectionPos = sectionPos;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
//        Log.d("NewsAdapter", "OnBind " + position);

        if (holder instanceof NewsItemVH) {
            final NewsItemVH itemHolder = (NewsItemVH) holder;
            itemHolder.title.setText(mNews.get(position).getTitle());
            itemHolder.author.setText(mNews.get(position).getAuthor());
            itemHolder.time.setText(mNews.get(position).getTime());
            String imagesUrlStr = mNews.get(position).getImageUrlStr();
            itemHolder.mCurrentPosition = position;
            itemHolder.initImages();
            if (mNews.get(position).getRead()) {
                itemHolder.setRead(true);
            }
            else {
                itemHolder.setRead(false);
            }

            List<String> imgUrls = getImageUrlsList(imagesUrlStr, ConstantValues.IMAGE_NUM[itemHolder.layoutType.ordinal()]);
            new ImageLoadingTask(mSectionPos, itemHolder.images).execute(imgUrls.toArray(new String[0]));


            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNews(mNews.get(position));
                    addToKeywords(mNews.get(position));
                    mNews.get(position).setRead(true);
                    itemHolder.setRead(true);

                }
            });
        }
        else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder)holder;
            if (netWorkError) {
                footViewHolder.setNetWorkError();
            }
            else {
                Log.d(LOG_TAG, "Set Loading");
                footViewHolder.setLoading();
            }
        }
    }

    @Override
    public int getItemCount() {
        /* +1 for footer */
        return mNews.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mNews.size()) {
            return ConstantValues.ItemViewType.FOOTER.ordinal();
        }
        String imgUrlsStr = mNews.get(position).getImageUrlStr();
        List<String> imgUrlList = parseJsonList(imgUrlsStr);
        if (imgUrlList.size() == 0) {
            return ConstantValues.ItemViewType.NONE.ordinal();
        }
        else if (imgUrlList.size() == 1) {
            return ConstantValues.ItemViewType.ONE_SMALL.ordinal();
        }
        else if (imgUrlList.size() == 2) {
            return ConstantValues.ItemViewType.ONE_BIG.ordinal();
        }
        else {
            return ConstantValues.ItemViewType.THREE.ordinal();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /* 选择匹配图片个数的layout*/
        ConstantValues.ItemViewType layoutType = ConstantValues.ItemViewType.values()[viewType];
//        Log.d("NewsAdapter", "Create View Holder");
        if (layoutType == ConstantValues.ItemViewType.FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.foot_item, parent, false);
            return new FootViewHolder(v);
        }
        int resource = -1;
        switch (layoutType) {
            case NONE: resource = R.layout.news_item_none; break;
            case ONE_SMALL:resource = R.layout.news_item_onesmall; break;
            case ONE_BIG: resource = R.layout.news_item_onebig; break;
            case THREE: resource = R.layout.news_item_three; break;
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        return new NewsItemVH(mContext, v, layoutType);
    }

    private void checkNewsRead(List<NewsItem> news) {
        NewsCache cache = NewsCache.getInstance();
        for (NewsItem newsItem : news) {
            if (cache.contains(mSectionPos, newsItem.getId())) {
                Log.d(LOG_TAG, "cached news");
                newsItem.setRead(cache.get(mSectionPos, newsItem.getId()).getRead());
            }
        }
    }

    /**
     * 实现加载时候foot view 的状态
     * */
    void setLoading() {
        netWorkError = false;
        notifyItemChanged(mNews.size());
    }

    /**
     * 实现新闻分类改变后存入新的数据
     * */
    void setNews(ConstantValues.NetWorkStatus status, List<JSONObject> news) {
        mNews = NewsItem.convert(news);
        checkNewsRead(mNews);
        if (status == ConstantValues.NetWorkStatus.NORMAL) {
            netWorkError = false;
            notifyDataSetChanged();
        }
        else {
            netWorkError = true;
        }
    }

    /**
     * 实现上拉加载功能
     * */
    void addNews(ConstantValues.NetWorkStatus status, List<JSONObject> news) {
        int changePos = mNews.size();
        mNews.addAll(NewsItem.convert(news));
        checkNewsRead(mNews);
        if (status == ConstantValues.NetWorkStatus.NORMAL) {
            netWorkError = false;
            notifyItemRangeInserted(changePos, news.size());
        }
        else {
            netWorkError = true;
            notifyItemChanged(mNews.size());
        }
    }

    /**
     * 实现下拉刷新功能
     * */
    void addRefreshNews(ConstantValues.NetWorkStatus status, List<JSONObject> news) {
        mNews.addAll(0, NewsItem.convert(news));
        checkNewsRead(mNews);
        if (status == ConstantValues.NetWorkStatus.NORMAL) {
            netWorkError = false;
            if (news.size() > 0) {
                notifyItemRangeInserted(0, news.size());
            }
            else {
                Toast.makeText(mContext, "没有更多新闻啦！", Toast.LENGTH_LONG).show();
            }
        }
        else {
            netWorkError = true;
            Toast.makeText(mContext, "网络连接异常，请稍后再试。", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 展示新闻数据
     * */

    private void showNews(NewsItem news) {
        Intent intent = new Intent(mContext, NewsDetailActivity.class);
        intent.putExtra("data", news.getJsonString());
        intent.putExtra("sectionPos", mSectionPos);
        mContext.startActivity(intent);
    }

    private void addToKeywords(NewsItem news) {
        try {
            JSONArray keywordsArray = news.getKeywordsArray();
            for (int i = 0; i < keywordsArray.length(); ++i) {
                JSONObject obj = keywordsArray.getJSONObject(i);
                UserConfig.getInstance().addKeyWords(obj.getString("word"), obj.getDouble("score"));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<String> getImageUrlsList(String imageUrlsStr, int num) {
        List<String> imageUrls = parseJsonList(imageUrlsStr);
        if (imageUrls.size() == 0) {
            return new ArrayList<>();
        }
        return imageUrls.subList(0, num);
    }

    private List<String> parseJsonList(String listStr) {
        List<String> jsonList = new ArrayList<>();
        if (listStr.length() > 5) {
            jsonList.addAll(Arrays.asList(listStr.substring(1, listStr.length() - 1).split(",")));
        }
        return jsonList;
    }

}
