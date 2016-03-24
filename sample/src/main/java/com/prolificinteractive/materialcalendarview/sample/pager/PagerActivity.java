package com.prolificinteractive.materialcalendarview.sample.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static com.github.ipcjs.explorer.ExUtils.p;

/**
 * Created by ipcjs on 2016/3/19.
 */
public class PagerActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new F())
                    .commit();
        }
    }

    public static class F extends LazyFragment implements OnDateSelectedListener, OnMonthChangedListener {

        @Override
        protected View onCreateViewAsync(LayoutInflater inflater, ViewGroup container) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final View view = inflater.inflate(R.layout.activity_pager, container, false);
//            mcv.setOnMonthChangedListener(this);
//            mcv.setOnDateChangedListener(this);
            return view;
        }

        @Override
        protected void onViewAdded(View view) {
            ButterKnife.bind(this, view);
        }

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
//        mcv.setCurrentItemByDelta(1, false);
//        mcv.setCurrentItemByDelta(-1, false);
            refresh(mcv);
        }

        private void refresh(View v) {
            if (v instanceof ViewGroup) {
                final int count = ((ViewGroup) v).getChildCount();
                for (int i = 0; i < count; i++) {
                    refresh(((ViewGroup) v).getChildAt(i));
                }
            } else {
                v.invalidate();
            }
        }

        @OnCheckedChanged(R.id.check_visible)
        public void checkVisible(boolean checked) {
            mcv.setVisibility(checked ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
            p("onDateSelected", date, selected);
        }

        @Override
        public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
            p("onMonthChanged", date);
        }
    }

}

