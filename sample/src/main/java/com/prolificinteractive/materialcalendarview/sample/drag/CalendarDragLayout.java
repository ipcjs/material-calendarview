package com.prolificinteractive.materialcalendarview.sample.drag;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.text.style.LineBackgroundSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.sample.R;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by JiangSong on 2016/3/7.
 */
public class CalendarDragLayout extends ViewGroup {

    private ViewDragHelper mDragHelper;
    private View mListView;
    private MaterialCalendarView mCalendarView;
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
        mCalendarView = (MaterialCalendarView) findViewById(R.id.view_calendar);
        mListView = findViewById(R.id.view_list);
        mCaptureView = findViewById(R.id.view_capture);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        measureChildren(widthMeasureSpec, heightMeasureSpec);
//        measureChild(mCalendarView, widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureCalendar();
        measureChild(mListView, widthMeasureSpec, MeasureSpec.makeMeasureSpec(
                        MeasureSpec.getSize(heightMeasureSpec) - mCalendarView.getMeasureMinHeight(),
                        MeasureSpec.getMode(heightMeasureSpec))
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layout();
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
    private void layout() {
//        layoutChildVercital(mCaptureView, mTop); // 歪打正着, mCaptureView不须要layout...
        measureCalendar();
        int top = mTop;
        top = layoutChildVercital(mCalendarView, top);
        top = layoutChildVercital(mListView, top);
    }

    private int mTop = 0;
    public static float mScalePercent = 0;

    private void layoutChildWithScale(View child, int top, float scalePercent) {
        int height = child.getMeasuredHeight();
        int width = child.getMeasuredWidth();
        int paddingLeft = getPaddingLeft();
        int maxHeight = getHeight();

        int scaleHeight = (int) ((maxHeight - height) * scalePercent + 0.5f);
        child.layout(paddingLeft, top, paddingLeft + width, top + height + scaleHeight);
    }

    private int layoutChildVercital(View child, int top) {
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

    /**
     * Span to draw a dot centered under a section of text
     */
    public static class DotSpan implements LineBackgroundSpan {

        /**
         * Default radius used
         */
        public static final float DEFAULT_RADIUS = 3;

        private final float radius;
        private final int color;

        /**
         * Create a span to draw a dot using default radius and color
         *
         * @see #DotSpan(float, int)
         * @see #DEFAULT_RADIUS
         */
        public DotSpan() {
            this.radius = DEFAULT_RADIUS;
            this.color = 0;
        }

        /**
         * Create a span to draw a dot using a specified color
         *
         * @param color color of the dot
         * @see #DotSpan(float, int)
         * @see #DEFAULT_RADIUS
         */
        public DotSpan(int color) {
            this.radius = DEFAULT_RADIUS;
            this.color = color;
        }

        /**
         * Create a span to draw a dot using a specified radius
         *
         * @param radius radius for the dot
         * @see #DotSpan(float, int)
         */
        public DotSpan(float radius) {
            this.radius = radius;
            this.color = 0;
        }

        /**
         * Create a span to draw a dot using a specified radius and color
         *
         * @param radius radius for the dot
         * @param color  color of the dot
         */
        public DotSpan(float radius, int color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void drawBackground(
                Canvas canvas, Paint paint,
                int left, int right, int top, int baseline, int bottom,
                CharSequence charSequence,
                int start, int end, int lineNum
        ) {
            int oldColor = paint.getColor();
            if (color != 0) {
                int alpha = (int) (mScalePercent * 255);
                paint.setColor((color & 0xff_ff_ff) | (alpha << 24));
            }
            canvas.drawCircle((left + right) / 2, bottom + radius, radius, paint);
            paint.setColor(oldColor);
        }
    }

    /**
     * Decorate several days with a dot
     */
    public static class EventDecorator implements DayViewDecorator {

        private int color;
        private HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(5, color));
        }
    }
}
