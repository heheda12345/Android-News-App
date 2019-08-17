package com.example.news.ui.main.News;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

public abstract class OnLoadMoreListener extends RecyclerView.OnScrollListener {
    private int countItem;
    private int lastItem;
    private boolean isScolled = false;
    private RecyclerView.LayoutManager layoutManager;

    protected abstract void onLoading(int countNum, int lastNum);

    @Override
    public void onScrollStateChanged(RecyclerView view, int newState) {
        if (newState == SCROLL_STATE_DRAGGING || newState == SCROLL_STATE_SETTLING) {
            isScolled = true;
        }
        else {
            isScolled = false;
        }
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        if (view.getLayoutManager() instanceof LinearLayoutManager) {
            layoutManager = view.getLayoutManager();
            countItem = layoutManager.getItemCount();
            lastItem = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
        }
        if (isScolled && countItem != lastItem && lastItem == countItem - 1) {
            onLoading(countItem, lastItem);
        }
    }
}
