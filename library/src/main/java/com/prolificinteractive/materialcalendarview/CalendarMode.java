package com.prolificinteractive.materialcalendarview;

import java.util.Calendar;

public enum CalendarMode {

    MONTHS(6) {
        @Override
        public boolean isSameRange(CalendarDay one, CalendarDay two) {
            return one.getYear() == two.getYear()
                    && one.getMonth() == two.getMonth();
        }
    },
    WEEKS(1) {
        @Override
        public boolean isSameRange(CalendarDay one, CalendarDay two) {
            return one.getYear() == two.getYear()
                    && one.getCalendar().get(Calendar.WEEK_OF_YEAR) == two.getCalendar().get(Calendar.WEEK_OF_YEAR);
        }
    };

    final int visibleWeeksCount;

    CalendarMode(int visibleWeeksCount) {
        this.visibleWeeksCount = visibleWeeksCount;
    }

    public abstract boolean isSameRange(CalendarDay one, CalendarDay two);
}
