package com.prolificinteractive.materialcalendarview;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

/**
 * Display a week of {@linkplain DayView}s and
 * seven {@linkplain WeekDayView}s.
 */
@SuppressLint("ViewConstructor")
public class WeekView extends CalendarPagerView {

    public WeekView(@NonNull MaterialCalendarView view,
                    CalendarDay firstViewDay,
                    int firstDayOfWeek) {
        super(view, firstViewDay, CalendarUtils.getLastDayOfWeek(firstViewDay, firstDayOfWeek), firstDayOfWeek);
    }

    @Override
    protected int getActualWeekCount() {
        return 1;
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
