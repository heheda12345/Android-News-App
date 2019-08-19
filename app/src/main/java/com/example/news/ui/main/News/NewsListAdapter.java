package com.example.news.ui.main.News;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.news.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<JSONObject> mNews;

    public NewsListAdapter(Context context, ArrayList<JSONObject> news) {
        this.mInflater = LayoutInflater.from(context);
        mNews = news;
    }
    @Override
    public int getCount() {
        return mNews.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private void showNews(JSONObject news){
        Intent intent = new Intent(mInflater.getContext(), NewsDetailActivity.class);
        intent.putExtra("data", news.toString());
        mInflater.getContext().startActivity(intent);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView title;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.fragment_main, null);
            title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(title);
        } else {
            title = (TextView) convertView.getTag();
        }

        try {
            title.setText(mNews.get(position).getString("title"));
        } catch (JSONException e) {
                e.printStackTrace();
        }

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNews(mNews.get(position));
            }
        });
        return convertView;
    }
}
