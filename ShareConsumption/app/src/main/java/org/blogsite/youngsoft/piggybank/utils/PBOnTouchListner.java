package org.blogsite.youngsoft.piggybank.utils;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by klee on 2018-02-05.
 */

public class PBOnTouchListner implements View.OnTouchListener {
    private final ScrollView scrollView;
    private final Context context;

    public PBOnTouchListner(Context context, ScrollView scrollView){
        this.context = context;
        this.scrollView = scrollView;
    }

    @Override
    public boolean onTouch( View v, MotionEvent event )
    {
        scrollView.requestDisallowInterceptTouchEvent( true );
        return false;
    }

}
