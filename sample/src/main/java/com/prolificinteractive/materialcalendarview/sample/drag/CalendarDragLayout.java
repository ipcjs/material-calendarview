package com.prolificinteractive.materialcalendarview.sample.drag;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.sample.R;

/**
 * Created by JiangSong on 2016/3/7.
 */
public class CalendarDragLayout extends ViewGroup {

    private ViewDragHelper mDragHelper;
    private View mListView;
    private MaterialCalendarView mCalendarView;
    private MaterialCalendarView mWeekCalendarView;
    private View mCaptureView;

    public CalendarDragLayout(Context context) {
        this(context, null);
    }

    public CalendarDragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDragHelper = ViewDragHelper.create(this, mCallback);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return mDragHelper.shouldInterceptTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            mDragHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCalendarView = (MaterialCalendarView) findViewById(R.id.view_calendar_month);
        mWeekCalendarView = (MaterialCalendarView) findViewById(R.id.view_calendar_week);

        mCalendarView.setLayoutMode(MaterialCalendarView.LAYOUT_MODE_VERTICAL_SPLIT);
        mCalendarView.setTopbarVisible(false);
        mCalendarView.setShowWeekDayView(false);

        mWeekCalendarView.setLayoutMode(MaterialCalendarView.LAYOUT_MODE_NONE);
        mWeekCalendarView.setCalendarDisplayMode(CalendarMode.WEEKS);
        mWeekCalendarView.setTopbarVisible(false);
        mWeekCalendarView.setShowWeekDayView(false);

        mListView = findViewById(R.id.view_list);
        mCaptureView = findViewById(R.id.view_capture);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        measureChildren(widthMeasureSpec, heightMeasureSpec);
//        measureChild(mCalendarView, widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChild(mWeekCalendarView, widthMeasureSpec, heightMeasureSpec);
        measureCalendar();
        measureChild(mListView, widthMeasureSpec, MeasureSpec.makeMeasureSpec(
                        MeasureSpec.getSize(heightMeasureSpec) - mCalendarView.getMeasureMinHeight(),
                        MeasureSpec.getMode(heightMeasureSpec))
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChildVertical(mWeekCalendarView, 0);
        layout();
    }

    private void layout() {
//        layoutChildVertical(mCaptureView, mTop); // 歪打正着, mCaptureView不须要layout...
        measureCalendar();
        int top = mTop;
        top = layoutChildVertical(mCalendarView, top);
        top = layoutChildVertical(mListView, top);
        mWeekCalendarView.setVisibility(mTop + mCalendarView.getCurSelectedItemBottom() > mWeekCalendarView.getMeasuredHeight() ? GONE : VISIBLE);
    }

    private void measureCalendar() {
        if (mScalePercent == 0f) {// mScalePercent时, 使用AT_MOST测量, 显示Calendar本来的大小
            mCalendarView.measure(
                    MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.AT_MOST)
            );
        } else {// 否则, 使用EXACTLY测量, Calendar设置成指定大小
            int scaleHeight = (int) ((getMeasuredHeight() - mCalendarView.getMeasureNormalHeight()) * mScalePercent + 0.5f);
            mCalendarView.measure(
                    MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mCalendarView.getMeasureNormalHeight() + scaleHeight, MeasureSpec.EXACTLY)
            );
        }
    }

    private int mTop = 0;
    private float mScalePercent = 0;

    @Deprecated
    private void layoutChildWithScale(View child, int top, float scalePercent) {
        int height = child.getMeasuredHeight();
        int width = child.getMeasuredWidth();
        int paddingLeft = getPaddingLeft();
        int maxHeight = getHeight();

        int scaleHeight = (int) ((maxHeight - height) * scalePercent + 0.5f);
        child.layout(paddingLeft, top, paddingLeft + width, top + height + scaleHeight);
    }

    private int layoutChildVertical(View child, int top) {
        int height = child.getMeasuredHeight();
        int width = child.getMeasuredWidth();
        int paddingLeft = getPaddingLeft();
        child.layout(paddingLeft, top, paddingLeft + width, top + height);
        return top + height;
    }

    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            // 统统capture mCaptureView..
            mDragHelper.captureChildView(mCaptureView, pointerId);
            return false;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int delta = child == mListView ? mCalendarView.getHeight() : 0;
            int temp = top - delta;
            int min = -(mCalendarView.getMeasureNormalHeight() - mCalendarView.getMeasureMinHeight());
            int max = child == mCaptureView ? getHeight() - mCalendarView.getMeasureNormalHeight() : 0;
            temp = Math.min(Math.max(min, temp), max);
            return temp + delta;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == mCalendarView) {
                mTop = top;
            } else if (changedView == mListView) {
                mTop = top - mCalendarView.getHeight();
            } else if (changedView == mCaptureView) {
                mTop = top;
                if (mTop > 0) {
                    mScalePercent = 1.0f * mTop / (getHeight() - mCalendarView.getMeasureNormalHeight());
                    mTop = 0;
                } else {
                    mScalePercent = 0;
                }
            }
            layout();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {

        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight();
        }
    };
}
