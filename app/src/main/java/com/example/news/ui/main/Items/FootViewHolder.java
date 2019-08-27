package com.example.news.ui.main.Items;

import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.news.R;

public class FootViewHolder extends RecyclerView.ViewHolder {
    private ContentLoadingProgressBar processBar;
    private TextView textView;
    public FootViewHolder(View v) {
        super(v);
        processBar = itemView.findViewById(R.id.pb_progress);
        textView = itemView.findViewById(R.id.foot_view_text);
    }

    public void setNetWorkError() {
        textView.setVisibility(View.VISIBLE);
        processBar.setVisibility(View.INVISIBLE);
    }

    public void setLoading() {
        textView.setVisibility(View.INVISIBLE);
        processBar.setVisibility(View.VISIBLE);
    }

}