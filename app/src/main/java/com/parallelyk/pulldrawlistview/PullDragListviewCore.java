package com.parallelyk.pulldrawlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by YK on 2016/5/13.
 */
public class PullDragListviewCore extends ListView {

    public PullDragListviewCore(Context context) {
        super(context);
    }

    public PullDragListviewCore(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {



        return super.onTouchEvent(ev);
    }
}
