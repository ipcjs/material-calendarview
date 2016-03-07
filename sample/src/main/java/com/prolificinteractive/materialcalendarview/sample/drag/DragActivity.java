package com.prolificinteractive.materialcalendarview.sample.drag;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.ipcjs.explorer.compat.CompatContextInterface;
import com.prolificinteractive.materialcalendarview.sample.R;

/**
 * Created by ipcjs on 2016/3/7.
 */
public class DragActivity extends AppCompatActivity implements CompatContextInterface {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_calendar);
        RecyclerView recyclerView = $(R.id.view_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ItemListFragment.SimpleListAdapter(ItemListFragment.generateList(20)));
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
