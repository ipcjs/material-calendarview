package com.prolificinteractive.materialcalendarview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView.ShowOtherDates;
import com.prolificinteractive.materialcalendarview.format.DayFormatter;
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.SHOW_DEFAULTS;
import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.showOtherMonths;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_WEEK;

public abstract class CalendarPagerView extends ViewGroup implements View.OnClickListener {

    protected static final int DEFAULT_DAYS_IN_WEEK = 7;
    private static final Calendar tmpCalendar = CalendarUtils.getInstance();
    private final ArrayList<WeekDayView> weekDayViews = new ArrayList<>();
    private final List<DayView> dayViews = new ArrayList<>();
    private final ArrayList<DecoratorResult> decoratorResults = new ArrayList<>();
    @ShowOtherDates
    protected int showOtherDates = SHOW_DEFAULTS;
    private MaterialCalendarView mcv;
    private CalendarDay firstViewDay;
    private CalendarDay minDate = null;
    private CalendarDay maxDate = null;
    private int firstDayOfWeek;
    private int measureTileSize;


    public CalendarPagerView(@NonNull MaterialCalendarView view,
                             CalendarDay firstViewDay,
                             int firstDayOfWeek) {
        super(view.getContext());
        this.mcv = view;
        this.firstViewDay = firstViewDay;
        this.firstDayOfWeek = firstDayOfWeek;
        setBackgroundColor(Color.YELLOW);

        setClipChildren(false);
        setClipToPadding(false);

        buildWeekDays(getFirstDayOfGrid());
        buildDayViews(getFirstDayOfGrid());
    }

    protected abstract int getAddedWeekCount();

    protected abstract int getActualWeekCount();

    private void buildWeekDays(Calendar calendar) {
        for (int i = 0; i < DEFAULT_DAYS_IN_WEEK; i++) {
            WeekDayView weekDayView = new WeekDayView(getContext(), CalendarUtils.getDayOfWeek(calendar));
            weekDayViews.add(weekDayView);
            addView(weekDayView);
            calendar.add(DATE, 1);
        }
    }

    protected void buildDayViews(Calendar calendar) {
        for (int r = 0; r < getAddedWeekCount(); r++) {
            for (int i = 0; i < DEFAULT_DAYS_IN_WEEK; i++) {
                CalendarDay day = CalendarDay.from(calendar);
                DayView dayView = new DayView(getContext(), day, mcv);
                dayView.setOnClickListener(this);
                dayViews.add(dayView);
                addView(dayView);

                calendar.add(DATE, 1);
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

    @DayOfWeek
    protected int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public void setFirstDayOfWeek(int dayOfWeek) {
        this.firstDayOfWeek = dayOfWeek;

        Calendar calendar = getFirstDayOfGrid();
        calendar.set(DAY_OF_WEEK, dayOfWeek);
        for (WeekDayView dayView : weekDayViews) {
            dayView.setDayOfWeek(calendar);
            calendar.add(DATE, 1);
        }

        calendar = getFirstDayOfGrid();
        for (DayView dayView : dayViews) {
            CalendarDay day = CalendarDay.from(calendar);
            dayView.setDay(day);
            calendar.add(DATE, 1);
        }

        updateUi();
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
        this.showOtherDates = showFlags;
        updateUi();
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
        this.minDate = minDate;
        updateUi();
    }

    public void setMaximumDate(CalendarDay maxDate) {
        this.maxDate = maxDate;
        updateUi();
    }

    public void setSelectedDates(Collection<CalendarDay> dates) {
        for (DayView dayView : dayViews) {
            CalendarDay day = dayView.getDate();
            dayView.setChecked(dates != null && dates.contains(day));
        }
        postInvalidate();
    }

    protected void updateUi() {
        for (DayView dayView : dayViews) {
            CalendarDay day = dayView.getDate();
            dayView.setupSelection(
                    showOtherDates,
                    day.isInRange(minDate, maxDate),
                    isDayEnabled(day));
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
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    private int actualRowHeight;

    public int getActualRowHeight() {
        return actualRowHeight;
    }

    public int getMeasureTileSize() {
        return measureTileSize;
    }

    private float scalePercent;

    public float getScalePercent() {
        return scalePercent;
    }

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
        measureTileSize = specWidthSize / DEFAULT_DAYS_IN_WEEK;

        //Just use the spec sizes
        setMeasuredDimension(specWidthSize, specHeightSize);

        int spaceOfRow = 0;
        scalePercent = 0;
        if (mcv.isVerticalSplit()) {
            int actualTileRowCount = getActualWeekCount() + (mcv.isShowWeekDayView() ? 1 : 0);
            actualRowHeight = getMeasuredHeight() / actualTileRowCount;
            spaceOfRow = actualRowHeight - measureTileSize;
            scalePercent = mcv.getScalePercent();
        }

        if (mcv.isShowWeekDayView()) {
            measure(false, weekDayViews, spaceOfRow, scalePercent);
        }
        measure(true, dayViews, spaceOfRow, scalePercent);
    }

    private void measure(boolean isDayView, List<? extends View> views, int space, float scalePercent) {
        for (View view : views) {
            if (isDayView) {
                boolean shouldReDraw = false;
                for (DayView.OnDrawListener listener : mcv.getDayViewOnDrawListeners()) {
                    if (listener.shouldReDraw((DayView) view)) {
                        shouldReDraw = true;
                        break;
                    }
                }
                if (shouldReDraw) {
                    ViewCompat.postInvalidateOnAnimation(view);
                }
            }
            LayoutParams params = (LayoutParams) view.getLayoutParams();
            params.scalePercent = scalePercent;
            params.remainingSpaceOfRow = space;
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    measureTileSize,
                    MeasureSpec.EXACTLY
            );

            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    measureTileSize,
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
        final int parentLeft = 0;

        int childTop = 0;
        if (mcv.isShowWeekDayView()) {
            childTop = layout(weekDayViews, parentLeft, parentLeft, childTop);
        }
        childTop = layout(dayViews, parentLeft, parentLeft, childTop);
    }

    private int layout(List<? extends View> views, int parentLeft, int childLeft, int top) {
        int space;
        for (int i = 0; i < views.size(); i++) {
            final View child = views.get(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            space = params.remainingSpaceOfRow;

            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            child.layout(childLeft, top, childLeft + width, top + height);

            childLeft += width;

            //We should warp every so many children
            if (i % DEFAULT_DAYS_IN_WEEK == (DEFAULT_DAYS_IN_WEEK - 1)) {
                childLeft = parentLeft;
                top += height + space;
            }
        }
        return top;
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

    public CalendarDay getFirstViewDay() {
        return firstViewDay;
    }

    /**
     * Simple layout params class for MonthView, since every child is the same size
     */
    public static class LayoutParams extends MarginLayoutParams {
        public int remainingSpaceOfRow;
        public float scalePercent;

        /**
         * {@inheritDoc}
         */
        public LayoutParams() {
            super(WRAP_CONTENT, WRAP_CONTENT);
        }
    }

    List<DayView> getDayViews() {
        return dayViews;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mcv.getPagerOnDrawListener() != null) {
            mcv.getPagerOnDrawListener().onDraw(this, getFirstViewDay(), canvas);
        }
    }

    private int getOtherRowCount() {
        return mcv.isShowWeekDayView() ? 1 : 0;
    }

    @IntDef({
            Calendar.SUNDAY,
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface DayOfWeek {

    }

    public Point getCalendarDayPoint(int day, Point outPoint) {
        return getCalendarDayPoint(CalendarDay.from(getFirstViewDay().getYear(), getFirstViewDay().getMonth(), day), outPoint);
    }

    public Point getCalendarDayPoint(CalendarDay day, Point outPoint) {
        day.copyTo(tmpCalendar);
        final int firstDayOfWeek = getFirstDayOfWeek();
        tmpCalendar.setFirstDayOfWeek(firstDayOfWeek);
        final int dayOfWeek = tmpCalendar.get(DAY_OF_WEEK);
        int col = dayOfWeek - firstDayOfWeek;
        if (col < 0) {
            col += DEFAULT_DAYS_IN_WEEK;
        }
        final int row = tmpCalendar.get(Calendar.WEEK_OF_MONTH) - 1 + getOtherRowCount();

        outPoint.set(col * getMeasureTileSize(), row * getActualRowHeight());
        return outPoint;
    }

    public interface OnDrawListener {
        void onDraw(CalendarPagerView view, CalendarDay firstDay, Canvas canvas);
    }
}
