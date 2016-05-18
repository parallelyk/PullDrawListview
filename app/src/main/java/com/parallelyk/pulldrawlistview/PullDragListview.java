package com.parallelyk.pulldrawlistview;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.Toast;

/**
 *
 * 下拉刷新的核心类，继承了Listview
 * Created by YK on 2016/5/13.
 */
public class PullDragListview extends ListView implements AbsListView.OnScrollListener{

    private final static String TAG = "PullDragListview";

    private Context mContext ;
    private HeadView mHeadView;
    private DragListItem mDragView;
    private Scroller mScroller;

    private boolean mPullable;
    private boolean isRefreshing = false;

    private float mDownY;



    private int mRefreshHeight;//触发刷新的高度



    private int mFraction;//下拉的速度



    private int currentState ;

    public static final int STATE_INIT = 0;
    public static final int STATE_PULLTOREFRESH = 1;
    public static final int STATE_REFRESHING = 2;
    public static final int STATE_RELEASETOREFRESH = 3;
    public static final int STATE_FINISHREFRESH = 4;

    public PullDragListview(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public PullDragListview(Context context, AttributeSet attr) {
        super(context,attr);
        mContext = context;
        init();
    }

    /**
     * 初始化操作
     */
    private void init(){

        mHeadView = new HeadView(mContext);
        addHeaderView(mHeadView);
        mRefreshHeight = 170;//默认触发刷新的高度
        mFraction = 2;//默认下拉的速度
        mPullable =true;
        mScroller = new Scroller(mContext,new DecelerateInterpolator());

        setOnScrollListener(this);

        setAndUpdate(STATE_INIT);


    }




//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//
//        boolean intercept = false;
//
//        float x =  ev.getRawX();
//        float y =  ev.getRawY();
//
//        switch (ev.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                mDownY = ev.getRawY();
//                intercept = false;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float deltaX = x -mLastX;
//                float deltaY = y -mLastY;
//
//
//                if( isTop && deltaY <= 0  ){
//                    return false;
//                }
//                //当处于可下拉状态 且 手指向下滑动的时候，拦截事件 ，执行下拉
//                if(mPullable && Math.abs(deltaY)>Math.abs(deltaX)){
//                    intercept = true;
//                }
//                else{
//                    intercept = false;
//                }
//                break;
//
//            default:
//                break;
//
//        }
//        mLastX = x;
//        mLastY = y;
//        return intercept;
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float y =  event.getRawY();
        if(mPullable){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    mDownY = event.getRawY();
                    if(mHeadView.getHeadHeight() == 0){
                        setAndUpdate(STATE_INIT);
                        isRefreshing =false;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:

                    int deltaY = (int) (y-mDownY)/mFraction;
                    //Log.d(TAG,"deltaY---->"+deltaY +"state"+currentState);
                    if(getFirstVisiblePosition() == 0
                            && (deltaY>0 || mHeadView.getHeadHeight()>0) ){
                        //listview正好在顶部 或者 已经被往下拉了（考虑往下拉了一点又往回退的情况）


                        // Math.abs(deltaY)> touchSlop

                        mHeadView.setHeadHeight(deltaY);


                        setSelection(0);//在touch模式下，会让listview滚动到0位置，保证了Head头部的正常缩放，不受scroller影响
                        if(currentState == STATE_REFRESHING ||currentState == STATE_FINISHREFRESH){
                            mDownY = y;
                            break;

                        }
                        if(mHeadView.getHeadHeight()>mRefreshHeight){

                            setAndUpdate(STATE_RELEASETOREFRESH);
                        }
                        else {
                            setAndUpdate(STATE_PULLTOREFRESH);
                        }
                    }
                    mDownY = y;
                    //currentState = STATE_NONE;

                    break;
                case MotionEvent.ACTION_UP:
                default:
                    if(getFirstVisiblePosition() == 0){

                        if(!isRefreshing && mHeadView.getHeadHeight()>mRefreshHeight){
                            //触发刷新 回调
                            Log.d(TAG, "====>Refreshing");
                            isRefreshing =true;
                            setAndUpdate(STATE_REFRESHING);
                            mScroller.startScroll(0, mHeadView.getHeadHeight(),
                                    0, mRefreshHeight - mHeadView.getHeadHeight(), 500);


                            Toast.makeText(mContext,"Refreshing",Toast.LENGTH_LONG).show();

                            new Handler().postDelayed(new Runnable() {

                                public void run() {
                                    Log.d(TAG,"post");
                                    mScroller.startScroll(0, mHeadView.getHeadHeight(),
                                            0, -mHeadView.getHeadHeight(), 500);
                                    invalidate();

                                    setAndUpdate(STATE_FINISHREFRESH);


                                }

                            }, 2000);

                        }
                        else{
                            mScroller.startScroll(0, mHeadView.getHeadHeight(),
                                    0,  -mRefreshHeight-mHeadView.getHeadHeight(), 500);
                            invalidate();
                        }

                    }

                    break;

            }
        }

        /**
         * 采用直接传递的方式，避免用拦截造成焦点的丢失
         */
        if(mDragView != null){
            mDragView.onDragTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }

    public void setAndUpdate(int state){
        if(currentState != state){
            currentState = state;
            mHeadView.updateHeadValue(currentState);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {

                Log.d(TAG, String.valueOf(mHeadView.getHeadHeight()));

                mHeadView.setAbsHeight(mScroller.getCurrY());

                postInvalidate();



        }

        super.computeScroll();
    }
    public int getmRefreshHeight() {
        return mRefreshHeight;
    }

    public void setmRefreshHeight(int mRefreshHeight) {
        this.mRefreshHeight = mRefreshHeight;
    }
    public int getmFraction() {
        return mFraction;
    }

    public void setmFraction(int mFraction) {
        this.mFraction = mFraction;
    }

    public interface OnRefreshListener{
        public void onRefresh();
        public void onFinish();
    }
}
