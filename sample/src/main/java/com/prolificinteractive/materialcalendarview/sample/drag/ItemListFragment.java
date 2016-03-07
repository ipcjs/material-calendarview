package com.prolificinteractive.materialcalendarview.sample.drag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ipcjs.explorer.compat.CompatFragment;
import com.prolificinteractive.materialcalendarview.sample.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiangSong on 2016/3/3.
 */
public class ItemListFragment extends CompatFragment {

    public static final String SIZE = "size";

    public static ItemListFragment newInstance(int size) {
        ItemListFragment fragment = new ItemListFragment();
        Bundle args = new Bundle();
        args.putInt(SIZE, size);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int size = getArguments() != null ? getArguments().getInt(SIZE) : 66;
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new SimpleListAdapter(generateList(size)));
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return recyclerView;
    }

    public static List<String> generateList(int size) {
        List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add("Item:" + i);
        }
        return list;
    }

    private static final class ItemViewHolder extends RecyclerView.ViewHolder {
        public final TextView tv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv);
        }
    }

    public static class SimpleListAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        private List<? extends Object> mList;

        public SimpleListAdapter(List<? extends Object> list) {
            mList = list;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false));
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            holder.tv.setText(mList.get(position).toString());
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

//    public static class ScrollingFabBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
//        public ScrollingFabBehavior(Context context, AttributeSet attrs) {
//            super(context, attrs);
//        }
//
//        @Override
//        public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
//            return dependency instanceof AppBarLayout;
//        }
//
//        @Override
//        public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
//            if (dependency instanceof AppBarLayout) {
//                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
//                int verticalMargin = params.bottomMargin + params.topMargin;
//                int height = child.getHeight();
//                int toolbarHeight = parent.findViewById(R.id.toolbar).getLayoutParams().height;
//                float percent = dependency.getY() / toolbarHeight;
//                child.setTranslationY(-(height + verticalMargin) * percent);
//                return true;
//            }
//            return false;
//        }
//    }
}
