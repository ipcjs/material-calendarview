package com.prolificinteractive.materialcalendarview.format;

import java.text.DateFormatSymbols;

/**
 * Use a {@linkplain java.util.Calendar} to get week day labels.
 *
 * @see java.util.Calendar#getDisplayName(int, int, java.util.Locale)
 */
public class CalendarWeekDayFormatter implements WeekDayFormatter {
    /**
     * Format with a default calendar
     */
    public CalendarWeekDayFormatter() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharSequence format(int dayOfWeek) {
        return DateFormatSymbols.getInstance().getShortWeekdays()[dayOfWeek];
    }
}
