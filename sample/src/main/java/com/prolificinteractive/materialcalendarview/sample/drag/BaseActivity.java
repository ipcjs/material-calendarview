package com.prolificinteractive.materialcalendarview.sample.drag;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by ipcjs on 2016/3/19.
 */
public class BaseActivity extends AppCompatActivity {
    public Context getContext() {
        return this;
    }

    public <V extends View> V $(int id) {
        return (V) findViewById(id);
    }
}
