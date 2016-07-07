package com.example.pc.mylistviewdispatch;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by pc on 2016/7/7.
 */
public class MyLinearLayout  extends LinearLayout {


    public MyLinearLayout(Context context) {
        this(context, null);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private View childView;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int count = getChildCount();
        if(count > 0){
            childView = getChildAt(0);
        }

    }

    private void init(Context context){
        this.setOrientation(LinearLayout.VERTICAL);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        Log.e("xhc","----------MyLinearLayout--------------dispatchTouchEvent-");
        childView.dispatchTouchEvent(ev);
        return true;
    }
}






















