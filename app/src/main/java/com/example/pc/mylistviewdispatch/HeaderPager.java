package com.example.pc.mylistviewdispatch;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * Created by pc on 2016/7/4.
 */
public class HeaderPager extends LinearLayout {

    private final int UP = 0;
    private final int DOWN = 1;
    private int headerSize;
    private Scroller scroller;
    private View headerView;
    private View listView;
    private VelocityTracker mVelocityTracker;
    private int mMaximumVelocity;   //允许执行一个fling手势动作的最大速度值
    private int mTouchSlop;         //表示滑动的时候，手的移动要大于这个距离才开始移动控件。

    private int direction;

    public HeaderPager(Context context) {
        this(context, null);
    }

    public HeaderPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }



    public HeaderPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.setOrientation(LinearLayout.VERTICAL);
        scroller = new Scroller(context);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int count = getChildCount();
        if (count > 0) {
            View view = getChildAt(0);
            headerSize = view.getMeasuredHeight();
            height += headerSize;
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int count = getChildCount();
        if (count > 0) {
            headerView = getChildAt(0);
        }
        if (count > 1) {
            listView = getChildAt(1);
        }
    }

    private float lastX, lastY;
    private float currX, currY;


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        currX = ev.getX();
        currY = ev.getY();
        obtainVelocityTracker(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                scroller.abortAnimation();
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("xhc", "lastY - currY " + (lastY - currY));
                if(lastY - currY > 0){
                    //向上滑 ,先判断头部是否被遮盖了
                    if(isHideHead()){
                        //true是已经被遮盖
//                       boolean flag =  listView.dispatchTouchEvent(ev);
//                        boolean flag =  ((ListView)listView).dispatchTouchEvent(ev);
//                        Log.e("xhc","向上滑向listview 分发事件--> "+flag);
                        return super.dispatchTouchEvent(ev);
                    }
                    else{
                        //未被遮盖
                        scrollBy(0, (int) (lastY - currY));
                        invalidate();
                    }
                }
                else{
                    //向下滑 ,先判断内部的listview是否已经滑动完成了
                    if(listViewIsTop()){
                        //true  listview已经滑动到了顶部 , 那么就滑动外部
                        scrollBy(0, (int) (lastY - currY));
                        invalidate();
                    }
                    else{
                        //false listview没有滑动到顶部，那么就滑动里面的listview
//                       boolean flag =  ((ListView) listView).dispatchTouchEvent(ev);
//                        Log.e("xhc","向--下--滑向listview 分发事件"+flag);
                        return super.dispatchTouchEvent(ev);
                    }
                }


                break;
            case MotionEvent.ACTION_UP:


                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                float yVelocity = mVelocityTracker.getYVelocity();  //获取当前的滑动速度
                direction = yVelocity > 0 ? DOWN : UP;
                scroller.fling(0, getScrollY(), 0, -(int) yVelocity, 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                invalidate();
                ev.setAction(MotionEvent.ACTION_CANCEL);
                boolean dd = super.dispatchTouchEvent(ev);
                recycleVelocityTracker();
                return dd;
            case MotionEvent.ACTION_CANCEL:

                recycleVelocityTracker();
                break;
        }
        lastX = currX;
        lastY = currY;
//        super.dispatchTouchEvent(ev);
        return true;
    }

    private void obtainVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y >= headerSize) {
            y = headerSize;
        }
        if (y <= 0) {
            y = 0;
        }
        super.scrollTo(x, y);
    }

    //判断头部是否已经滑动完成,true是已经被盖住
    private boolean isHideHead(){
        return getScrollY() >= headerSize;
    }

    //判断头部是否有被遮盖。true是没有一点遮盖
    private boolean isNotHideHead(){
        return getScrollY() == 0;
    }

    private float getVelocity(){
        if(scroller == null){
            return 0;
        }
        return scroller.getCurrVelocity();
    }

    @Override
    public void computeScroll() {

        if (scroller.computeScrollOffset()) {
            int curryY = scroller.getCurrY();
            if(direction == UP){
                if(!isHideHead()){
                    //没有被遮盖
                    //还在滑动。
                    scrollTo(0, curryY);
                    invalidate();
                }
                else{
                    //已经被遮盖滑动子view
                    if(listView instanceof AbsListView){
                        AbsListView abListView = (AbsListView)listView;
                        int distance = scroller.getFinalY() - curryY;
                        int duration = scroller.getDuration() - scroller.timePassed();
                        Log.e("xhc" , " distance -> "+distance+" duration "+duration);
                        abListView.smoothScrollBy(distance, duration);
                        scroller.abortAnimation();
                    }
                }
            }
            else{
                //向下滑先判断里面的listview是否已经已经滑动完毕
                if(listViewIsTop()){
                    //listview已经在顶部了
                    scrollTo(0, curryY);
                    invalidate();
                    if(isNotHideHead()){
                        scroller.abortAnimation();
                    }
                }
                else{
                    //listview没有在顶部就滑动到顶部
                    if(listView instanceof AbsListView){
                        AbsListView abListView = (AbsListView)listView;
                        int distance = scroller.getFinalY() - curryY;
                        int duration = scroller.getDuration() - scroller.timePassed();
                        abListView.smoothScrollBy(distance, duration);
//                        scroller.abortAnimation();
                    }
                }

//                if(!isNotHideHead()){
//                    //没有被遮盖
//                    scrollTo(0, curryY);
//                    invalidate();
//                }else{
//                    //已经被遮盖滑动子view
//                    if(listView instanceof AbsListView){
//                        AbsListView abListView = (AbsListView)listView;
//                        int distance = scroller.getFinalY() - curryY;
//                        int duration = scroller.getDuration() - scroller.timePassed();
//                        Log.e("xhc" , " distance -> "+distance+" duration "+duration);
//                        abListView.smoothScrollBy(distance, duration);
//                        scroller.abortAnimation();
//                    }
//                }
            }
        }

    }

    private boolean listViewIsTop(){
        if(listView == null){
            return false;
        }
        if(listView instanceof AbsListView){
            AbsListView absListView = (AbsListView)listView;

            int count = absListView.getChildCount();
            View firstChild = null;
            if(count > 0){
                firstChild = absListView.getChildAt(0);
            }
            if(firstChild != null){
                return absListView.getFirstVisiblePosition() == 0 && firstChild.getTop() == 0;
            }
            else{
                return true;
            }
        }
        return false;
    }

}


























