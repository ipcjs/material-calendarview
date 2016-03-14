package com.prolificinteractive.materialcalendarview.format;

import com.prolificinteractive.materialcalendarview.CalendarUtils;

import org.junit.Test;

import java.util.Calendar;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;

/**
 * Created by JiangSong on 2016/3/11.
 */
public class CalendarWeekDayFormatterTest {
    @Test
    public void testFormat() {
        assertEquals("周一", format(Calendar.MONDAY));
        assertEquals("周六", format(Calendar.SATURDAY));
    }

    @Test
    public void test() {
//        CalendarWeekDayFormatter formatter = new CalendarWeekDayFormatter(CalendarUtils.getInstance());
//        assertEquals("周一", formatter.format(Calendar.MONDAY));
//        assertEquals("周六", formatter.format(Calendar.SATURDAY));
    }

    private String format(int dayOfWeek) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        return c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
    }

    @Test
    public void testSet() {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(Calendar.YEAR, 2016);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.getTime();

        c.set(Calendar.DAY_OF_WEEK, Calendar.MONTH);
        assertEquals("周一", c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
    }

}