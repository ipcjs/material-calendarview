package com.prolificinteractive.materialcalendarview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Calendar;

/**
 * Display a month of {@linkplain DayView}s and
 * seven {@linkplain WeekDayView}s.
 */
@SuppressLint("ViewConstructor")
class MonthView extends CalendarPagerView {

    private static final int DEFAULT_DAYS_IN_WEEK = 7;
    private static final int DEFAULT_MAX_WEEKS = 6;

    @Override
    protected int getActualWeekCount() {
        return getLastViewDay().getCalendar().get(Calendar.WEEK_OF_MONTH);
    }

    @Override
    protected int getAddedWeekCount() {
        return DEFAULT_MAX_WEEKS;
    }

    public MonthView(Context context, @NonNull MaterialCalendarView mcv) {
        super(context, mcv);
    }

    @Override
    protected CalendarDay computeLastViewDay() {
        return CalendarUtils.getLastDayOfMonth(getFirstViewDay());
    }

    public CalendarDay getMonth() {
        return getFirstViewDay();
    }

    @Override
    protected boolean isDayEnabled(CalendarDay day) {
        return day.getMonth() == getFirstViewDay().getMonth();
    }
}
