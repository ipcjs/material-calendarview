package com.prolificinteractive.materialcalendarview.sample.drag;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.ipcjs.explorer.compat.CompatContextInterface;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarPagerView;
import com.prolificinteractive.materialcalendarview.DayView;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.sample.R;
import com.prolificinteractive.materialcalendarview.sample.decorators.EventDecorator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.github.ipcjs.explorer.ExUtils.p;

/**
 * Created by ipcjs on 2016/3/7.
 */
public class DragActivity extends AppCompatActivity implements CompatContextInterface {

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
        final DayView.OnDrawListener dayViewOnDrawListener = new DayView.OnDrawListener() {
            private Paint paint;

            {
                paint = new Paint();
                paint.setTextSize(30);
            }

            @Override
            public boolean shouldReDraw(DayView view) {
                return /*view.getDate().getDay() % 5 == 0*/true;
            }

            private int count = 0;

            @Override
            public void onDraw(DayView view, Canvas canvas) {
                p("draw", ++count, view.getDate());
                if (shouldReDraw(view)) {
                    CalendarPagerView.LayoutParams params = (CalendarPagerView.LayoutParams) view.getLayoutParams();
                    paint.setColor(setAlpha(paint.getColor(), params.scalePercent));
                    canvas.drawText(params.remainingSpaceOfRow + ", " + params.scalePercent, 0, 100, paint);
                }
            }
        };
        CalendarPagerView.OnDrawListener pagerOnDrawListener = new CalendarPagerView.OnDrawListener() {

            private Paint paint;

            {
                paint = new Paint();
                paint.setTextSize(50);
            }

            @Override
            public void onDraw(CalendarPagerView view, Canvas canvas) {
                CalendarDay firstDay = view.getFirstViewDay();
                final int actualMaximum = firstDay.getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH);
                final int actualMinimum = firstDay.getCalendar().getActualMinimum(Calendar.DAY_OF_MONTH);
                Point point = new Point();
                for (int i = actualMinimum; i <= actualMaximum; i++) {
                    canvas.save();
                    view.getCalendarDayPoint(i, point);
                    canvas.translate(point.x, point.y);
                    canvas.drawText("" + view.getScalePercent(), 0, view.getMeasuredTileSize(), paint);
                    canvas.restore();
                }
            }
        };
//        materialCalendarView.addDayViewOnDrawListener(dayViewOnDrawListener);
//        materialCalendarView.setPagerOnDrawListener(pagerOnDrawListener);
        materialCalendarView.setPagerOnDrawListener(new CalendarPagerView.OnDrawListener.Simple() {
            private Paint paint = new Paint();

            {
                paint.setTextSize(25);
            }

            @Override
            protected void onDrawItem(CalendarPagerView cpv, CalendarDay day, Canvas canvas, float percent, int itemWidth, int itemHeight) {
                paint.setColor((paint.getColor() & 0xffffff) | (int) (percent * 255 + 0.5f) << 24);
                canvas.drawText(day.getMonth() + "-" + day.getDay(), 0, itemWidth, paint);
            }
        });
    }

    private static int setAlpha(int color, float alpha) {
        return (color & 0xffffff) | ((int) (alpha * 255 + 0.5) << 24);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public <V extends View> V $(int id) {
        return (V) findViewById(id);
    }
}
