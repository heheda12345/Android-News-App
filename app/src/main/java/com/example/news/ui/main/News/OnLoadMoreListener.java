package com.example.news.ui.main.News;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

public abstract class OnLoadMoreListener extends RecyclerView.OnScrollListener {
    private int countItem;
    private int lastItem;
    private RecyclerView.LayoutManager layoutManager;

    protected abstract void onLoading(int countNum, int lastNum);

    @Override
    public void onScrollStateChanged(RecyclerView view, int newState) {
        super.onScrollStateChanged(view, newState);
        layoutManager = view.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            lastItem = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
            countItem = layoutManager.getItemCount();
        }
        if (newState == RecyclerView.SCROLL_STATE_IDLE && lastItem + 1 == countItem) {
            onLoading(countItem, lastItem);
        }

    }

}
