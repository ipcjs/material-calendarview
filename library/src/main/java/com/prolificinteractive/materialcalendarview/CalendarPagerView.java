package com.prolificinteractive.materialcalendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView.ShowOtherDates;
import com.prolificinteractive.materialcalendarview.format.DayFormatter;
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import static com.prolificinteractive.materialcalendarview.CalendarUtils.DEBUG;
import static com.prolificinteractive.materialcalendarview.CalendarUtils.logd;
import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.DEFAULT_DAYS_IN_WEEK;
import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.SHOW_NONE;
import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.showOtherMonths;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_WEEK;

public abstract class CalendarPagerView extends ViewGroup implements View.OnClickListener {
    private static final Calendar tmpCalendar = CalendarUtils.getInstance();
    private final ArrayList<WeekDayView> weekDayViews = new ArrayList<>();
    private final List<DayView> dayViews = new ArrayList<>();
    private final ArrayList<DecoratorResult> decoratorResults = new ArrayList<>();
    @ShowOtherDates
    protected int showOtherDates = SHOW_NONE;
    private MaterialCalendarView mcv;
    private CalendarDay firstViewDay;
    private CalendarDay lastViewDay;
    private CalendarDay minDate = null;
    private CalendarDay maxDate = null;
    private int firstDayOfWeek;
    private int measuredTileSize;
    private float scalePercent;
    private int actualRowHeight;

    public CalendarPagerView(Context context, @NonNull MaterialCalendarView mcv) {
        super(context);
        this.mcv = mcv;

        setClipChildren(false);
        setClipToPadding(false);

        if (mcv.isShowWeekDayView()) {
            addWeekDays();
        }
        addDayViews();
//        setWillNotDraw(false);
    }

    List<DayView> getDayViews() {
        return dayViews;
    }

    public CalendarDay getFirstViewDay() {
        return firstViewDay;
    }

    public CalendarDay getLastViewDay() {
        if (lastViewDay == null) {
            lastViewDay = computeLastViewDay();
        }
        return lastViewDay;
    }

    protected abstract CalendarDay computeLastViewDay();

    public int getActualRowHeight() {
        return actualRowHeight;
    }

    public int getMeasuredTileSize() {
        return measuredTileSize;
    }

    public float getScalePercent() {
        return scalePercent;
    }

    private int getOtherRowCount() {
        return mcv.isShowWeekDayView() ? 1 : 0;
    }

    protected abstract int getAddedWeekCount();

    protected abstract int getActualWeekCount();

    private void addWeekDays() {
        for (int i = 0; i < DEFAULT_DAYS_IN_WEEK; i++) {
            WeekDayView weekDayView = new WeekDayView(getContext());
            weekDayViews.add(weekDayView);
            addView(weekDayView);
        }
    }

    protected void addDayViews() {
        for (int r = 0; r < getAddedWeekCount(); r++) {
            for (int i = 0; i < DEFAULT_DAYS_IN_WEEK; i++) {
                DayView dayView = new DayView(getContext(), mcv);
                dayView.setOnClickListener(this);
                dayViews.add(dayView);
                addView(dayView);
            }
        }
    }

    /**
     * 获取当前图表网格的第一格DayView的日期
     * @return
     */
    protected Calendar getFirstDayOfGrid() {
        getFirstViewDay().copyTo(tmpCalendar);
        //noinspection ResourceType
        tmpCalendar.setFirstDayOfWeek(getFirstDayOfWeek());
        int dow = CalendarUtils.getDayOfWeek(tmpCalendar);
        int delta = getFirstDayOfWeek() - dow;
        //If the delta is positive, we want to remove a week
        boolean removeRow = showOtherMonths(showOtherDates) ? delta >= 0 : delta > 0;
        if (removeRow) {
            delta -= DEFAULT_DAYS_IN_WEEK;
        }
        tmpCalendar.add(DATE, delta);
        return tmpCalendar;
    }

    @CalendarUtils.DayOfWeek
    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public void setDate(CalendarDay firstViewDay, int firstDayOfWeek) {
        if (!firstViewDay.equals(this.firstViewDay) || firstDayOfWeek != this.firstDayOfWeek) {
            this.firstViewDay = firstViewDay;
            this.lastViewDay = null;// clear lastViewDay
            this.firstDayOfWeek = firstDayOfWeek;

//            final int firstDayOfWeek = getFirstDayOfWeek();
            final Calendar firstDayOfGrid = getFirstDayOfGrid();

            Calendar calendar = (Calendar) firstDayOfGrid.clone();
            calendar.set(DAY_OF_WEEK, firstDayOfWeek);
            for (WeekDayView dayView : weekDayViews) {
                dayView.setDayOfWeek(calendar);
                calendar.add(DATE, 1);
            }

            calendar = (Calendar) firstDayOfGrid.clone();
            for (DayView dayView : dayViews) {
                CalendarDay day = CalendarDay.from(calendar);
                dayView.setDay(day);
                calendar.add(DATE, 1);
            }

            enableStateChangeFlag = true;
            postRefresh();
        }
    }

    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            refresh();
            postRefreshingFlag = false;
        }
    };

    private void postRefresh() {
        if (!postRefreshingFlag) {
            postRefreshingFlag = true;
            post(refreshRunnable);
        }
    }

    private boolean postRefreshingFlag;
    private boolean enableStateChangeFlag;

    private void refresh() {
        if (enableStateChangeFlag) {
            for (DayView dayView : dayViews) {
                CalendarDay day = dayView.getDate();
                dayView.setupSelection(
                        showOtherDates,
                        day.isInRange(minDate, maxDate),
                        isDayEnabled(day));
            }
            postInvalidate();

            enableStateChangeFlag = false;
        }
    }

    protected abstract boolean isDayEnabled(CalendarDay day);

    void setDayViewDecorators(List<DecoratorResult> results) {
        this.decoratorResults.clear();
        if (results != null) {
            this.decoratorResults.addAll(results);
        }
        invalidateDecorators();
    }

    public void setWeekDayTextAppearance(int taId) {
        for (WeekDayView weekDayView : weekDayViews) {
            weekDayView.setTextAppearance(getContext(), taId);
        }
    }

    public void setDateTextAppearance(int taId) {
        for (DayView dayView : dayViews) {
            dayView.setTextAppearance(getContext(), taId);
        }
    }

    public void setShowOtherDates(@ShowOtherDates int showFlags) {
        if (this.showOtherDates != showFlags) {
            this.showOtherDates = showFlags;
            enableStateChangeFlag = true;
            postRefresh();
        }
    }

    public void setSelectionEnabled(boolean selectionEnabled) {
        for (DayView dayView : dayViews) {
            dayView.setOnClickListener(selectionEnabled ? this : null);
            dayView.setClickable(selectionEnabled);
        }
    }

    public void setSelectionColor(int color) {
        for (DayView dayView : dayViews) {
            dayView.setSelectionColor(color);
        }
    }

    public void setWeekDayFormatter(WeekDayFormatter formatter) {
        for (WeekDayView dayView : weekDayViews) {
            dayView.setWeekDayFormatter(formatter);
        }
    }

    public void setDayFormatter(DayFormatter formatter) {
        for (DayView dayView : dayViews) {
            dayView.setDayFormatter(formatter);
        }
    }

    public void setMinimumDate(CalendarDay minDate) {
        if (!(minDate == null ? this.minDate == null : minDate.equals(this.minDate))) {
            this.minDate = minDate;
            enableStateChangeFlag = true;
            postRefresh();
        }
    }

    public void setMaximumDate(CalendarDay maxDate) {
        if (!(maxDate == null ? this.maxDate == null : maxDate.equals(this.maxDate))) {
            this.maxDate = maxDate;
            enableStateChangeFlag = true;
            postRefresh();
        }
    }

    public void setSelectedDates(Collection<CalendarDay> dates) {
        for (DayView dayView : dayViews) {
            CalendarDay day = dayView.getDate();
            dayView.setChecked(dates != null && dates.contains(day));
        }
        postInvalidate();
    }

    protected void invalidateDecorators() {
        final DayViewFacade facadeAccumulator = new DayViewFacade();
        for (DayView dayView : dayViews) {
            facadeAccumulator.reset();
            for (DecoratorResult result : decoratorResults) {
                if (result.decorator.shouldDecorate(dayView.getDate())) {
                    result.result.applyTo(facadeAccumulator);
                }
            }
            dayView.applyFacade(facadeAccumulator);
        }
    }

    void invalidateDrawView() {
        this.invalidate();
    }

    @Override
    public void onClick(View v) {
        if (v instanceof DayView) {
            DayView dayView = (DayView) v;
            mcv.onDateClicked(dayView.getDate(), !dayView.isChecked());
        }
    }

    /*
     * Custom ViewGroup Code
     */

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        //We expect to be somewhere inside a MaterialCalendarView, which should measure EXACTLY
        if (specHeightMode == MeasureSpec.UNSPECIFIED || specWidthMode == MeasureSpec.UNSPECIFIED) {
            throw new IllegalStateException("CalendarPagerView should never be left to decide it's size");
        }

        //The spec width should be a correct multiple
        measuredTileSize = specWidthSize / DEFAULT_DAYS_IN_WEEK;

        //Just use the spec sizes
        setMeasuredDimension(specWidthSize, specHeightSize);

        if (mcv.isVerticalSplit()) {
            actualRowHeight = getMeasuredHeight() / (getActualWeekCount() + getOtherRowCount());
            scalePercent = mcv.computeScalePercent();
        } else {
            actualRowHeight = measuredTileSize;
            scalePercent = 0;
        }

        if (mcv.isShowWeekDayView()) {
            measure(false, weekDayViews);
        }
        measure(true, dayViews);
    }

    private void measure(boolean isDayView, List<? extends View> views) {
        for (View view : views) {
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    measuredTileSize,
                    MeasureSpec.EXACTLY
            );

            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    measuredTileSize,
                    MeasureSpec.EXACTLY
            );
            view.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (DEBUG) logd("CPV.onLayout", getFirstViewDay(), changed, left, top, right, bottom);
        final int parentLeft = 0;
        int childTop = 0;
        if (mcv.isShowWeekDayView()) {
            childTop = layout(weekDayViews, parentLeft, parentLeft, childTop);
        }
        childTop = layout(dayViews, parentLeft, parentLeft, childTop);
    }

    private int layout(List<? extends View> views, int parentLeft, int childLeft, int top) {
        for (int i = 0; i < views.size(); i++) {
            final View child = views.get(i);

            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            child.layout(childLeft, top, childLeft + width, top + height);

            childLeft += width;

            //We should warp every so many children
            if (i % DEFAULT_DAYS_IN_WEEK == (DEFAULT_DAYS_IN_WEEK - 1)) {
                childLeft = parentLeft;
                top += actualRowHeight;
            }
        }
        return top;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (DEBUG) logd("CPV.dispatchDraw", getFirstViewDay());
        super.dispatchDraw(canvas);
        if (mcv.getPagerOnDrawListener() != null) {
            final int save = canvas.save();
            canvas.translate(0, getOtherRowCount() * actualRowHeight);
            mcv.getPagerOnDrawListener().onDraw(this, canvas);
            canvas.restoreToCount(save);
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams();
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams();
    }

    @Override
    public void onInitializeAccessibilityEvent(@NonNull AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(CalendarPagerView.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(@NonNull AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(CalendarPagerView.class.getName());
    }

    /**
     * Simple layout params class for MonthView, since every child is the same size
     */
    public static class LayoutParams extends MarginLayoutParams {
        /**
         * {@inheritDoc}
         */
        public LayoutParams() {
            super(WRAP_CONTENT, WRAP_CONTENT);
        }
    }

    public interface OnDrawListener {
        /**
         * @param cpv    current drawing view
         * @param canvas current canvas
         * @see #getMeasuredTileSize()
         * @see #getActualRowHeight()
         * @see #getScalePercent()
         * @see #getFirstViewDay()
         * @see #getFirstDayOfWeek()
         */
        void onDraw(CalendarPagerView cpv, Canvas canvas);

        /**
         * simple
         */
        abstract class Simple implements OnDrawListener {
            public static final long ONE_DAY = 24L * 60 * 60 * 1000;
            private Calendar lastDay = Calendar.getInstance();
            private Calendar firstDay = Calendar.getInstance();

            @Override
            public void onDraw(CalendarPagerView cpv, Canvas canvas) {
                cpv.getFirstViewDay().copyTo(firstDay);
                cpv.getLastViewDay().copyTo(lastDay);
                int first = firstDay.get(Calendar.DAY_OF_MONTH);
                int last = first + (int) ((lastDay.getTimeInMillis() - firstDay.getTimeInMillis()) / ONE_DAY);
                int itemWidth = cpv.getMeasuredTileSize();
                int itemHeight = cpv.getActualRowHeight();
                int firstDayOfWeek = cpv.getFirstDayOfWeek();
                float percent = cpv.getScalePercent();

                Calendar calendar = firstDay;
                int col = getCol(calendar, firstDayOfWeek);
                int row = 0;
                canvas.translate(col * itemWidth, 0);
                int day = first;
                while (day <= last) {
                    // do something
                    CalendarDay c = CalendarDay.from(calendar);
                    onDrawItem(cpv, c, canvas, percent, itemWidth, itemHeight);

                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    day++;
                    col++;
                    if (col < DEFAULT_DAYS_IN_WEEK) {// cur week, move a item's width
                        canvas.translate(itemWidth, 0);
                    } else {// next week, move to next line
                        col = 0;
                        row++;
                        canvas.translate(-(DEFAULT_DAYS_IN_WEEK - 1) * itemWidth, itemHeight);
                    }
                }
            }

            protected abstract void onDrawItem(
                    CalendarPagerView cpv,
                    CalendarDay day,
                    Canvas canvas,
                    float percent,
                    int itemWidth, int itemHeight
            );

            protected int getCol(Calendar day, int firstDayOfWeek) {
                int col = day.get(DAY_OF_WEEK) - firstDayOfWeek;
                return col < 0 ? (col + DEFAULT_DAYS_IN_WEEK) : col;
            }

            protected int getRow(Calendar day) {
                return day.get(Calendar.WEEK_OF_MONTH) - 1;
            }
        }
    }
}
