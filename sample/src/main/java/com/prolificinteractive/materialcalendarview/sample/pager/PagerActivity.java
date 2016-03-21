package com.prolificinteractive.materialcalendarview.sample.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.sample.R;
import com.prolificinteractive.materialcalendarview.sample.drag.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import static com.prolificinteractive.materialcalendarview.CalendarUtils.logd;

/**
 * Created by ipcjs on 2016/3/19.
 */
public class PagerActivity extends BaseActivity implements OnDateSelectedListener, OnMonthChangedListener {

    @Bind(R.id.mcv)
    MaterialCalendarView mcv;
    @Bind(R.id.check_smooth)
    CheckBox checkSmooth;
    @Bind(R.id.et_delta)
    EditText etDelta;

    int getDelta() {
        try {
            return Integer.valueOf(etDelta.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 1;
        }
    }

    boolean isSmooth() {
        return checkSmooth.isChecked();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        ButterKnife.bind(this);
        mcv.setOnMonthChangedListener(this);
        mcv.setOnDateChangedListener(this);
    }

    @OnClick(R.id.to_left)
    public void toLeft() {
        mcv.setCurrentItemByDelta(-getDelta(), isSmooth());
    }

    @OnClick(R.id.to_right)
    public void toRight() {
        mcv.setCurrentItemByDelta(getDelta(), isSmooth());
    }

    @OnClick(R.id.refresh)
    public void refresh() {
        mcv.setCurrentItemByDelta(1, false);
        mcv.setCurrentItemByDelta(-1, false);
    }

    @OnCheckedChanged(R.id.check_visible)
    public void checkVisible(boolean checked) {
        mcv.setVisibility(checked ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        logd("onDateSelected", date, selected);
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        logd("onMonthChanged", date);
    }
}

