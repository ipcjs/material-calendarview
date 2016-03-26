package com.prolificinteractive.materialcalendarview.sample.drag;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.github.ipcjs.explorer.compat.CompatContextInterface;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarPagerView;
import com.prolificinteractive.materialcalendarview.DayView;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.sample.R;
import com.prolificinteractive.materialcalendarview.sample.decorators.EventDecorator;

import java.util.ArrayList;
import java.util.Date;

import static com.github.ipcjs.explorer.ExUtils.p;

/**
 * Created by ipcjs on 2016/3/7.
 */
public class DragActivity extends BaseActivity implements CompatContextInterface {

    public static final int DAY_ONE_WEEK = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_calendar_2);
        RecyclerView recyclerView = $(R.id.view_list);
        MaterialCalendarView materialCalendarView = $(R.id.view_calendar_month);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ItemListFragment.SimpleListAdapter(ItemListFragment.generateList(20)));
        ArrayList<CalendarDay> dates = new ArrayList<>();
        dates.add(CalendarDay.from(new Date()));
        materialCalendarView.addDecorator(new EventDecorator(Color.RED, dates));
        materialCalendarView.addDecorator(new DayViewDecorator() {

            private Drawable drawable;

            {
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.calendar_today_bg);
//                drawable = new GradientDrawable();
//                drawable.setUseLevel(false);
//                drawable.setShape(GradientDrawable.RING);
//                drawable.setColor(Color.RED);
            }

            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return CalendarDay.today().equals(day);
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setBackgroundDrawable(drawable);
            }
        });
        materialCalendarView.setAutoSelectOnSingleMode(true);
        materialCalendarView.addDayViewDecorateListener(new DayView.DecorateListener() {
            @Override
            public void decorate(DayView view) {
//                p();
                if (view.getDate().getDay() % 5 == 0) {
                    view.append(".");
                }
            }
        });

        final CalendarPagerView.OnDrawListener.Simple listener = new CalendarPagerView.OnDrawListener.Simple() {
            private Paint paint = new Paint();

            {
                paint.setTextSize(25);
            }

            @Override
            public void onDraw(CalendarPagerView cpv, Canvas canvas) {
                super.onDraw(cpv, canvas);
//                logd("onDraw", cpv.getFirstViewDay());
            }

            @Override
            protected void onDrawItem(CalendarPagerView cpv, CalendarDay day, Canvas canvas, float percent, int itemWidth, int itemHeight) {
                if (day.isBefore(CalendarDay.today())) {
                    paint.setColor((paint.getColor() & 0xffffff) | (int) (percent * 255 + 0.5f) << 24);
                    canvas.drawText((day.getMonth() + 1) + "-" + day.getDay(), 0, itemWidth, paint);
                }
            }
        };
        materialCalendarView.setPagerOnDrawListener(listener);
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                p(date, selected);
            }
        });
    }

}
