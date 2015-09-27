package com.codepath.imagesearch.helpers;

import android.widget.AbsListView;

/**
 * Created by litacho on 9/24/15.
 */
public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {
    // Minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 8;
    private int currentPage = 0;
    private int previousTotalItemCount = 0;
    private boolean loading = true;
    private int startingPageIndex = 0;

    public EndlessScrollListener() {
    }

    public EndlessScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public EndlessScrollListener(int visibleThreshold, int startingPage) {
        this.visibleThreshold = visibleThreshold;
        this.startingPageIndex = startingPage;
        this.currentPage = startingPage;
    }

    // This method is called many times a second per scroll, so be wary of what you place here.

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // if totalItemCount is less than previous, set the previous total amount to be what we loaded
        // so far and try keep calling this method.
        if (totalItemCount < this.previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) this.loading = true;
        }

        // if new total count is greater, we are done loading.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
        }

        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            onLoadMore(currentPage + 1, totalItemCount);
            loading = true;
        }
    }

    public abstract void onLoadMore(int page, int totalItemsCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Don't take any action on changed
    }
}
