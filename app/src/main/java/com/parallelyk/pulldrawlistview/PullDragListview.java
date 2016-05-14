package com.parallelyk.pulldrawlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by YK on 2016/5/13.
 */
public class PullDragListview extends LinearLayout {


    private Context mContext ;
    private View mHeadLinearlayout ;
    private int mHeadOffSet;
    private MarginLayoutParams marginLayoutParams;

    private ListView mListView;

    private float mLastX ,mLastY;
    private boolean mPullable;
    private float mDownY;


    private int currentState ;

    public static final int STATE_PREPEARTOREFRESH = 0;
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
        mHeadLinearlayout = LayoutInflater.from(mContext).inflate(R.layout.pulldown_head,null,true);

        mPullable =true;

        currentState = STATE_PREPEARTOREFRESH;


        setOrientation(VERTICAL);
        addView(mHeadLinearlayout, 0);

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if(changed){
            mHeadOffSet = -mHeadLinearlayout.getHeight();
            marginLayoutParams = (MarginLayoutParams) mHeadLinearlayout.getLayoutParams();
            marginLayoutParams.topMargin = mHeadOffSet;
            //marginLayoutParams.setMarginStart(mHeadOffSet);

            mListView = (ListView) getChildAt(1);

        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {


        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean intercept = false;

        float x =  ev.getRawX();
        float y =  ev.getRawY();

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getRawY();
                intercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x-mLastX;
                float deltaY = y-mLastY;
                if(mPullable && Math.abs(deltaY)>Math.abs(deltaX)){
                    intercept = true;
                }
                else{
                    intercept = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;
            default:
                break;

        }
        mLastX = x;
        mLastY = y;
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(mPullable){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:

                    break;
                case MotionEvent.ACTION_MOVE:

                    int distance = (int) (event.getRawY()-mDownY);
                    if(distance<0){

                    }
                    if(mPullable ){
                        marginLayoutParams.topMargin = distance/2 + mHeadOffSet;
                        mHeadLinearlayout.setLayoutParams(marginLayoutParams);
                    }



                    break;
                case MotionEvent.ACTION_UP:

                    break;
                default:
                    break;

            }
        }

        return super.onTouchEvent(event);
    }
}
