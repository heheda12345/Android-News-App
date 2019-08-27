package com.example.news.ui.main.Search;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.news.R;
import com.example.news.data.ConstantValues;
import com.example.news.ui.main.Items.FootViewHolder;
import com.example.news.ui.main.Items.NewsItemVH;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter {
    private List<JSONObject>  news;
    private Context context;

    public SearchResultAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == news.size()) {
            return ConstantValues.ItemViewType.FOOTER.ordinal();
        }
        String imgUrlsStr = "[]";
        try {
            imgUrlsStr = news.get(position).getString("image");
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
        return new NewsItemVH(context, v, layoutType);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return news.size() + 1;
    }

    private List<String> parseJsonList(String listStr) {
        List<String> jsonList = new ArrayList<>();
        if (listStr.length() > 5) {
            jsonList.addAll(Arrays.asList(listStr.substring(1, listStr.length() - 1).split(",")));
        }
        return jsonList;
    }
}
