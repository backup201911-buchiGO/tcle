package org.blogsite.youngsoft.piggybank.utils;

import android.content.Context;
import android.widget.ListView;
import android.widget.ScrollView;
import org.blogsite.youngsoft.tableview.listeners.OnScrollListener;

/**
 * Created by klee on 2018-02-05.
 */

public class PBOnScrollListener implements OnScrollListener {
    private final ScrollView scrollView;
    private final Context context;

    public PBOnScrollListener(Context context, ScrollView scrollView){
        this.context = context;
        this.scrollView = scrollView;
    }

    @Override
    public void onScroll(final ListView tableDataView, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onScrollStateChanged(final ListView tableDateView, final ScrollState scrollState) {
        // listen for scroll state changes
    }
}
