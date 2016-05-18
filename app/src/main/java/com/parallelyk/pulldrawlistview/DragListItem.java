package com.parallelyk.pulldrawlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 *
 * 侧滑的核心类
 * Created by YK on 2016/5/13.
 */
public class DragListItem extends LinearLayout {
    private static final String TAG = "DragListItem";

    private Context mContext;
    private View mHideDragView;
    private LinearLayout mContentView;

    public DragListItem(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public DragListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView(){

        setOrientation(HORIZONTAL);
        mHideDragView = View.inflate(mContext,R.layout.hide_drag_item,this);
        mContentView = (LinearLayout) mHideDragView.findViewById(R.id.show_content_view);



    }
    public void onDragTouchEvent(MotionEvent event){

    }
}
