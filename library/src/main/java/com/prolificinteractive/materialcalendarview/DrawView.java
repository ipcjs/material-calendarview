package com.prolificinteractive.materialcalendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created by ipcjs on 2016/3/13.
 */
class DrawView extends View {
    private MaterialCalendarView mcv;
    private CalendarPagerView cpv;

    public DrawView(Context context, MaterialCalendarView mcv, CalendarPagerView cpv) {
        super(context);
        this.mcv = mcv;
        this.cpv = cpv;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mcv.getPagerOnDrawListener() != null) {
            mcv.getPagerOnDrawListener().onDraw(cpv, canvas);
        }
    }

}
