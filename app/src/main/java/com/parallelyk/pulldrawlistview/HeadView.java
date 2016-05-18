package com.parallelyk.pulldrawlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *
 * 下拉的头部
 * Created by YK on 2016/5/16.
 */
public class HeadView extends LinearLayout {
    private final static String TAG = "HeadView";
    private Context mContext;
    private LinearLayout mHead;
    private LayoutParams headLayoutParams;
    private ImageView mArrow;
    private TextView mText;

    private RotateAnimation arrowUp,arrowDown;

    public HeadView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public HeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView(){
        mHead  = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.pulldown_head, null, true);
        LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0);
        addView(mHead, layoutParams);// 把头部放在一个LinearLayout里面
        headLayoutParams = (LayoutParams) mHead.getLayoutParams();

        mArrow = (ImageView) mHead.findViewById(R.id.head_arrow);
        mText = (TextView) mHead.findViewById(R.id.head_text);

        initAnimation();

    }

    private void initAnimation(){

        arrowUp = new RotateAnimation(0.0f,180.0f,RotateAnimation.RELATIVE_TO_SELF,0.5f,
                RotateAnimation.RELATIVE_TO_SELF,0.5f);
        arrowUp.setDuration(100);//动画时长
        arrowUp.setFillAfter(true);//过滤后面相同的动画

        arrowDown = new RotateAnimation(180.0f,0.0f,RotateAnimation.RELATIVE_TO_SELF,0.5f,
                RotateAnimation.RELATIVE_TO_SELF,0.5f);
        arrowDown.setDuration(100);//动画时长
        arrowDown.setFillAfter(true);//过滤后面相同的动画
    }
    public void setHeightZero(){
        headLayoutParams.height = 0;
        mHead.setLayoutParams(headLayoutParams);
    }
    public void setAbsHeight(int height){
        int h = height;
        if(h <0){
            h = 0;
        }
        headLayoutParams.height = h;
        mHead.setLayoutParams(headLayoutParams);
    }
    public void setHeadHeight(int deltaY){


        Log.d(TAG + "HeadHeight", String.valueOf(headLayoutParams.height));

        headLayoutParams.height += deltaY;
        if(headLayoutParams.height<0){
            headLayoutParams.height = 0;
        }
        if(headLayoutParams.height>400){
            headLayoutParams.height = 400;
        }
        mHead.setLayoutParams(headLayoutParams);
    }

    public int getHeadHeight(){
        return headLayoutParams.height;
    }


    /**
     * 更新下拉头部的状态文字和图案
     */
    public void updateHeadValue(int state){

        switch (state){
            case PullDragListview.STATE_INIT:
                mArrow.setVisibility(View.VISIBLE);
                mText.setText("下拉刷新");
                break;
            case PullDragListview.STATE_PULLTOREFRESH:

                mArrow.setVisibility(View.VISIBLE);
                mArrow.startAnimation(arrowDown);
                mText.setText("下拉刷新");

                break;
            case PullDragListview.STATE_REFRESHING:


                mText.setText("正在刷新...");
                mArrow.clearAnimation();//很关键，不去除动画无法使箭头消失（实际消失了，残影还在）
                mArrow.setVisibility(View.INVISIBLE);

                break;
            case PullDragListview.STATE_RELEASETOREFRESH:
                mText.setText("释放立即刷新");
                mArrow.startAnimation(arrowUp);

                break;
            case PullDragListview.STATE_FINISHREFRESH:
                mText.setText("刷新成功");
                mArrow.setVisibility(View.INVISIBLE);

                break;

            default:
                break;



        }




    }
}
