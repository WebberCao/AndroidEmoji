package com.webber.androidemoji.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class NoHorizontalScrollerViewPager extends ViewPager {

    public NoHorizontalScrollerViewPager(Context context) {
        super(context);
    }

    public NoHorizontalScrollerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 重写拦截事件，返回值设置为false，这时便不会横向滑动了。
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    /**
     * 重写拦截事件，返回值设置为false，这时便不会横向滑动了。
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
