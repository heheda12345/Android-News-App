package com.example.news.ui.main.Items;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.news.R;
import com.example.news.ui.main.Mine.MineFragment;

public class CollectionFootViewHolder extends RecyclerView.ViewHolder {
    public CollectionFootViewHolder(Context context, View v, String type) {
        super(v);
        TextView textView = v.findViewById(R.id.foot_view_text);
        if (type == MineFragment.NEWS_LIST_COLLECTION) {
            textView.setText(context.getString(R.string.no_collection_text));
        }
        else {
            textView.setText(context.getString(R.string.no_history_text));
        }
    }
}
