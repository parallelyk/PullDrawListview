package com.parallelyk.pulldrawlistview;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
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
    private DragListItem mDragView,mLastDragView;
    private Scroller mScroller;



    private boolean isRefreshing = false;
    private boolean isDrag = false,isLastDrag = false;
    private boolean isPull = false;

    private float mDownY,mDownX;


    private int mPosition;
    private int mRefreshHeight;//触发刷新的高度
    private int mTouchSlop;


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
        mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        mScroller = new Scroller(mContext,new DecelerateInterpolator());

        setOnScrollListener(this);

        setAndUpdate(STATE_INIT);


    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        float x =  ev.getRawX();
        float y =  ev.getRawY();
        int position = pointToPosition((int) ev.getX(), (int) ev.getY());

        if(mDragView != null && !isPull){//&& !mDragView.getDragState()
            if(mLastDragView==null){

                mDragView.onDragTouchEvent(ev);
            }
            else if(mLastDragView !=null && !mLastDragView.getDragState()){

                mDragView.onDragTouchEvent(ev);
            }
        }
        switch (ev.getAction()){

            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onInterceptTouchEvent ACTION_DOWN;");
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                if(mHeadView.getHeadHeight() == 0){
                    setAndUpdate(STATE_INIT);
                    Log.d(TAG, "setAndUpdate(STATE_INIT);");
                    isRefreshing =false;
                }
                if(position != INVALID_POSITION ){//选中列表中的某一项dragListItem
                    DragListItem tmpView = (DragListItem) getItemAtPosition(position);
                    if(mDragView!=null && tmpView != mDragView){
                        mLastDragView = mDragView;
                        mLastDragView.setClickable(true);
                        mLastDragView.rollBack();

                    }
                    mDragView = tmpView;
                    Log.d(TAG, "mdrag"+mDragView+mDragView.getDragState());
                }
                mPosition = position;
                mDownY = ev.getRawY();
                mDownX = ev.getRawX();

                break;

            default:
                break;

        }
        return super.dispatchTouchEvent(ev);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float y =  event.getRawY();
        if(mDragView != null){
            isDrag = mDragView.getDragState();
        }
        Log.d(TAG,"isDrag"+isDrag);
        int position = pointToPosition((int) event.getX(), (int) event.getY());
        Log.d(TAG,"position"+position);
        Log.d(TAG,"DragView ====>"+mDragView);

        if(!isDrag){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "ACTION_DOWN;");
                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                    mDownY = event.getRawY();
                    if(mHeadView.getHeadHeight() == 0){
                        setAndUpdate(STATE_INIT);
                        Log.d(TAG, "setAndUpdate(STATE_INIT);");
                        isRefreshing =false;
                    }
                    //检测侧滑

                    Log.d(TAG,"t1");
                    if(position != INVALID_POSITION ){//选中列表中的某一项dragListItem
                        Log.d(TAG,"t2");
                        DragListItem tmpView = (DragListItem) getItemAtPosition(position);
                        Log.d("TAG","tmpDragView"+tmpView);
                        if(mDragView!=null && tmpView != mDragView){
                            mDragView.rollBack();

                        }
                        mDragView = tmpView;

                    }
                    mPosition = position;
                    break;
                case MotionEvent.ACTION_MOVE:

                    Log.d(TAG, "ACTION_MOVE;");

                    int deltaY = (int) (y-mDownY)/mFraction;
                    Log.d(TAG,"deltaY---->"+deltaY +"y-->"+y +"mdowny->"+mDownY +"state"+currentState);
                    if(getFirstVisiblePosition() == 0
                            && (deltaY>0 || mHeadView.getHeadHeight()>0)
                            && !isDrag){//listview正好在顶部 或者 已经被往下拉了（考虑往下拉了一点又往回退的情况）
                        isPull = true;

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
                    Log.d(TAG, "UPUP");
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
                    isPull = false;

                    break;

            }
        }
        else if(isDrag && !isPull){

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if(mPosition != position && mDragView != null){
                        mDragView.rollBack();
                        invalidate();
                    }

                    break;
                case MotionEvent.ACTION_MOVE:

                    break;
                case MotionEvent.ACTION_UP:

                default:

                    isDrag = false;

                    break;

            }


        }
        if(mPosition != position && isDrag){
            return true;
        }

        /**
         * 采用直接传递的方式，避免用拦截造成焦点的丢失
         */
        if(!isPull && mDragView != null  ){
            Log.d(TAG,"=====>onDragTouchEvent");
            mDragView.onDragTouchEvent(event);
            //mDragView.performClick();
            isDrag = mDragView.getDragState();
        }

        if(isDrag){
            return true;
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

                //Log.d(TAG, String.valueOf(mHeadView.getHeadHeight()));

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
