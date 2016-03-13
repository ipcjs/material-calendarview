package com.prolificinteractive.materialcalendarview;

import org.junit.Test;

import java.util.Calendar;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;

/**
 * Created by ipcjs on 2016/3/8.
 */
public class UnitTest {
    @Test
    public void testSet() {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(Calendar.YEAR, 2016);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DAY_OF_MONTH, 1);
//        c.getTime();

        c.set(Calendar.DAY_OF_WEEK, Calendar.MONTH);
        assertEquals("周一", c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
    }

    @Test
    public void testDayOfWeek() {
        // 1,2,3,4,5,6,7,1,2,3,4,5,6,7,
        // 0,1,2,3,4,5,6,0,1,2,3,4,5,6,
        int day = 1;
        day -= 1;

    }
}
