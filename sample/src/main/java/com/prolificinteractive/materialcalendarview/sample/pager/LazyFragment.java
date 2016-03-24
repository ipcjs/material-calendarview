package com.prolificinteractive.materialcalendarview.sample.pager;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.ipcjs.explorer.compat.CompatFragment;

/**
 * Created by JiangSong on 2016/3/24.
 */
public abstract class LazyFragment extends CompatFragment {

    private View emptyView;
    private boolean mViewLoaded;
    private HandlerThread task;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewLoaded = false;
        emptyView = new ProgressBar(getContext());
        tryLoadView();
        return emptyView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewLoaded = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        tryLoadView();
    }

    private void tryLoadView() {
        if (getUserVisibleHint() && !mViewLoaded && task == null) {
            task = new HandlerThread("LoadViewTask");
            task.start();
            new Handler(task.getLooper()).post(new Runnable() {
                @Override
                public void run() {
                    final View view = onCreateViewAsync(LayoutInflater.from(getContext()), (ViewGroup) emptyView.getParent());
                    emptyView.post(new Runnable() {
                        @Override
                        public void run() {
                            replaceView(view);
                        }
                    });
                    task.quit();
                }
            });
        }
    }

    protected abstract View onCreateViewAsync(LayoutInflater inflater, ViewGroup container);

    protected abstract void onViewAdded(View view);

    private void replaceView(View view) {
        final ViewGroup container = (ViewGroup) emptyView.getParent();
        final int index = container.indexOfChild(emptyView);
        container.removeViewAt(index);
        container.addView(view, index);
        emptyView = null;
        task = null;
        mViewLoaded = true;
        onViewAdded(view);
    }
}
