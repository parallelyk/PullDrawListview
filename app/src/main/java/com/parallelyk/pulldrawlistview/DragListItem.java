package com.parallelyk.pulldrawlistview;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * https://github.com/parallelyk
 * 侧滑的核心类
 * Created by YK on 2016/5/13.
 */
public class DragListItem extends LinearLayout {
    private static final String TAG = "DragListItem";

    private Context mContext;
    private View mHidenDragView;
    private LinearLayout mContentView;//将包裹实际的内容
    private LinearLayout mHidenLayout;
    private Scroller mScroller;


    private int mLastX,mLastY;
    private int mDragOutWidth;//完全侧滑出来的距离



    private double mfraction = 0.75;//触发自动侧滑的临界点

    private boolean isDrag = false;


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
        mHidenDragView = View.inflate(mContext,R.layout.hide_drag_item,this);//merge进来整个listItem
        mContentView = (LinearLayout) mHidenDragView.findViewById(R.id.show_content_view);
        mHidenLayout = (LinearLayout) mHidenDragView.findViewById(R.id.hide_view);
        mScroller = new Scroller(mContext);
        mDragOutWidth = 300;





    }


    /**
     * 得到传递进来的事件序列。在此进行侧滑逻辑的判断。
     *
     */
    public void onDragTouchEvent(MotionEvent event) {

        Log.d(TAG, "isDrag" + isDrag);
        if(isDrag){
            setClickable(false);
        }
        else {
            setClickable(true);
        }

        Log.d(TAG,"onDragTouchEvent");
        int x = (int) event.getX();
        int y = (int) event.getY();
        int scrollX = getScrollX();//手机屏幕左上角x轴的值 - view的左上角x轴的值
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                if(Math.abs(deltaX) < Math.abs(deltaY))//纵向的滑动大于横向的滑动
                    break;

                if(deltaX != 0){//手指横向滑动
                    isDrag = true;
                   // mContentView.setClickable(false);
                    int newScrollX = scrollX - deltaX;//当这个值变小时，view视图向左滑动
                    if(newScrollX<0){//保持大于等于0，等于0时view左上角x值和屏幕左上角x值重合
                        newScrollX = 0;
                        setClickable(true);
                    }
                    else if(newScrollX>mDragOutWidth){//不能再侧滑了
                        newScrollX = mDragOutWidth;
                    }
                    scrollTo(newScrollX,0);
                }

                break;

            case MotionEvent.ACTION_UP:
            default :

                int finalScrollX = 0;
                if(scrollX > mDragOutWidth*mfraction){//左滑到足够自动滑动的位置了，否则回缩
                    finalScrollX = mDragOutWidth;

                    autoScrollToX(finalScrollX,500);
                }
                else {
                    rollBack();
                    isDrag = false;
                    //mContentView.setClickable(true);
                }

                break;

        }
        mLastX = x;
        mLastY = y;

    }

    /**
     * 自动回滚到封闭状态
     */
    public void rollBack(){
        if(getScrollX() != 0){
            autoScrollToX(0,100);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    setClickable(true);
                }
            }, 500);
        }
    }
    private void autoScrollToX(int finalX,int duration){

        mScroller.startScroll(getScrollX(),0,finalX-getScrollX(),0,duration);
        invalidate();
    }


    public boolean getDragState(){
        return isDrag;
    }
    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            postInvalidate();
        }
    }

    /**
     * 更改隐藏页的文字
     * @param charSequence
     */
    public void setFirstHidenView(CharSequence charSequence){

        TextView textView = (TextView) mHidenLayout.findViewById(R.id.hide_delete);
        textView.setText(charSequence);
    }

    /**
     * 给使用者添加隐藏页的视图（不仅仅是删除）
     * @param view
     */
    public void addHidenView(TextView view){
        mHidenLayout.addView(view);
    }

    /**
     * 给使用者设置listItem的实际内容
     * @param view
     */
    public void setContentView(View view){
        mContentView.addView(view);
    }
    public double getMfraction() {
        return mfraction;
    }

    public void setMfraction(double mfraction) {
        this.mfraction = mfraction;
    }

}
