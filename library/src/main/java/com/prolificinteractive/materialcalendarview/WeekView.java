package com.prolificinteractive.materialcalendarview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Display a week of {@linkplain DayView}s and
 * seven {@linkplain WeekDayView}s.
 */
@SuppressLint("ViewConstructor")
public class WeekView extends CalendarPagerView {
    public WeekView(Context context, @NonNull MaterialCalendarView mcv) {
        super(context, mcv);
    }

    @Override
    protected int getActualWeekCount() {
        return 1;
    }

    @Override
    protected CalendarDay computeLastViewDay() {
        return CalendarUtils.getLastDayOfWeek(getFirstViewDay(), getFirstDayOfWeek());
    }

    @Override
    protected int getAddedWeekCount() {
        return 1;
    }

    @Override
    protected boolean isDayEnabled(CalendarDay day) {
        return true;
    }
}
