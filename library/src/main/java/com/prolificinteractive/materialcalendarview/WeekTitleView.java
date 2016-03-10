package com.prolificinteractive.materialcalendarview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by JiangSong on 2016/3/10.
 */
public class WeekTitleView extends ViewGroup {
    public WeekTitleView(Context context) {
        this(context, null);
    }

    public WeekTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFirstDayOfWeek(Calendar.MONDAY);
    }

    private int firstDayOfWeek = -1;
    private List<WeekDayView> weekDayViews;

    public void setFirstDayOfWeek(int firstDayOfWeek) {
        if (this.firstDayOfWeek == firstDayOfWeek) {
            return;
        }
        int size = CalendarPagerView.DEFAULT_DAYS_IN_WEEK;
        boolean needCreate = weekDayViews == null;
        if (needCreate) {
            weekDayViews = new ArrayList<>();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        for (int i = 0; i < size; i++) {
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (needCreate) {
                WeekDayView week = new WeekDayView(getContext(), dayOfWeek);
                addView(week);
                weekDayViews.add(week);
            } else {
                weekDayViews.get(i).setDayOfWeek(dayOfWeek);
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    public void setTextAppearance(int taId) {
        for (WeekDayView weekDayView : weekDayViews) {
            weekDayView.setTextAppearance(getContext(), taId);
        }
    }

    public void setFormatter(WeekDayFormatter formatter) {
        for (WeekDayView dayView : weekDayViews) {
            dayView.setWeekDayFormatter(formatter);
        }
    }

    private boolean childHeightEqualWidth = false;

    public void setChildHeightEqualWidth(boolean childHeightEqualWidth) {
        this.childHeightEqualWidth = childHeightEqualWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureTileSize = (widthSpecSize - getPaddingLeft() - getPaddingRight()) / weekDayViews.size();

        int heightPadding = getPaddingTop() + getPaddingBottom();
        int childHeightSpecSize;
        int childHeightSpecMode;
        switch (heightSpecMode) {
            case MeasureSpec.AT_MOST:
                if (childHeightEqualWidth) {
                    childHeightSpecSize = measureTileSize - heightPadding;
                    childHeightSpecMode = MeasureSpec.EXACTLY;
                } else {
                    childHeightSpecSize = heightSpecSize - heightPadding;
                    childHeightSpecMode = MeasureSpec.AT_MOST;
                }
                break;
            default:
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.EXACTLY:
                childHeightSpecSize = heightSpecSize - heightPadding;
                childHeightSpecMode = MeasureSpec.EXACTLY;
                break;
        }
        if (heightSpecMode == MeasureSpec.AT_MOST) {
            if (childHeightEqualWidth) {

            }
        }
        int measuredHeight = 0;
        for (WeekDayView view : weekDayViews) {
            view.measure(
                    MeasureSpec.makeMeasureSpec(measureTileSize, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(childHeightSpecSize, childHeightSpecMode)
            );
            if (view.getMeasuredHeight() > measuredHeight) {
                measuredHeight = view.getMeasuredHeight();
            }
        }

        setMeasuredDimension(widthSpecSize, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        for (WeekDayView view : weekDayViews) {
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            view.layout(left, top, left + width, top + height);
            left += width;
        }
    }
}
