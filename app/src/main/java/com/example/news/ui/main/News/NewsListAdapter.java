package com.example.news.ui.main.News;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.news.R;
import com.example.news.data.ConstantValues;
import com.example.news.support.ImageCrawler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private int mSectionPos;
    private List<JSONObject> mNews;

    private class NewsItemVH extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView author;
        final TextView time;
        final ImageView[] images;
        List<Bitmap> bitmaps;
        ConstantValues.ItemViewType layoutType;
        int mCurrentPosition = -1;

        NewsItemVH(View v, ConstantValues.ItemViewType layoutType) {
            super(v);
            this.layoutType = layoutType;
            title = v.findViewById(R.id.title);
            author = v.findViewById(R.id.author);
            time = v.findViewById(R.id.time);
            int imageNum = ConstantValues.IMAGE_NUM[this.layoutType.ordinal()];
            images = new ImageView[imageNum];
            if (imageNum == 1) {
                images[0] = v.findViewById(R.id.image);
            }
            if (imageNum == 3) {
                images[0] = v.findViewById(R.id.image0);
                images[1] = v.findViewById(R.id.image1);
                images[2] = v.findViewById(R.id.image2);
            }
        }
    }

    private class FootViewHolder extends RecyclerView.ViewHolder {
        private ContentLoadingProgressBar processBar;
        public FootViewHolder(View v) {
            super(v);
            processBar = itemView.findViewById(R.id.pb_progress);
        }
    }

    public NewsListAdapter(Context context, int sectionPos) {
        mContext = context;
        mSectionPos = sectionPos;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        Log.d("NewsAdapter", "OnBind " + position);

        if (holder instanceof NewsItemVH) {
            NewsItemVH itemHolder = (NewsItemVH) holder;
            String imagesUrlStr = "[]";
            try {
                itemHolder.title.setText(mNews.get(position).getString("title"));
                itemHolder.author.setText(mNews.get(position).getString("publisher"));
                itemHolder.time.setText(mNews.get(position).getString("publishTime"));
                imagesUrlStr = mNews.get(position).getString("image");
                itemHolder.mCurrentPosition = position;
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e) {
                throw e;
            }

            List<Bitmap> images = getImages(imagesUrlStr, ConstantValues.IMAGE_NUM[itemHolder.layoutType.ordinal()]);
            if (itemHolder.mCurrentPosition == position) {
                for (int i = 0; i < images.size(); ++i) {
                    itemHolder.images[i].setImageBitmap(images.get(i));
                    itemHolder.images[i].invalidate();
                }
            }

            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNews(mNews.get(position));
                }
            });
        }
        else if (holder instanceof FootViewHolder) {

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
        String imgUrlsStr = "[]";
        try {
            imgUrlsStr = mNews.get(position).getString("image");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /* 选择匹配图片个数的layout*/
        ConstantValues.ItemViewType layoutType = ConstantValues.ItemViewType.values()[viewType];
        Log.d("NewsAdapter", "Create View Holder");
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
        return new NewsItemVH(v, layoutType);
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
        notifyItemRangeInserted(changePos, news.size());
    }

    /**
     * 展示新闻数据
     * */

    private void showNews(JSONObject news) {
        Intent intent = new Intent(mContext, NewsDetailActivity.class);
        intent.putExtra("data", news.toString());
        mContext.startActivity(intent);
    }

    /**
     * 获取图片
     * */

    private List<Bitmap> getImages(String imageUrlsStr, int num) {
        List<String> imageUrls = parseJsonList(imageUrlsStr);
        List<Bitmap> images = new ArrayList<>();
        if (imageUrls.size() == 0) {
            return images;
        }
        /* TODO 其他的策略*/
        imageUrls = imageUrls.subList(0, num);
        List<ImageCrawler> imageCrawlers = new ArrayList<>();
        for (String url : imageUrls) {
            ImageCrawler crawler = new ImageCrawler(mSectionPos, url, true, true);
            crawler.start();
            imageCrawlers.add(crawler);
        }
        for (ImageCrawler crawler : imageCrawlers) {
            try {
                crawler.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }
            images.add(crawler.getBitmap());
        }
        return images;
    }

    private List<String> parseJsonList(String listStr) {
        List<String> jsonList = new ArrayList<>();
        if (listStr.length() > 2) {
            jsonList.addAll(Arrays.asList(listStr.substring(1, listStr.length() - 1).split(",")));
        }
        return jsonList;
    }

}
