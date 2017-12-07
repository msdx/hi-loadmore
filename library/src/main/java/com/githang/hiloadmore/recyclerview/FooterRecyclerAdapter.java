/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */
package com.githang.hiloadmore.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.githang.recycleradapter.HiRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Geek_Soledad (msdx.android@qq.com)
 * @since 2017-05-03 0.1
 */
public class FooterRecyclerAdapter extends HiRecyclerAdapter {

    private List<View> mFooterViews = new ArrayList<>();

    public FooterRecyclerAdapter(RecyclerView.Adapter wrapperAdapter) {
        super(wrapperAdapter);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateFooterHolder(ViewGroup parent, int viewType) {
        return new FooterViewHolder(mFooterViews.get(viewType));
    }

    @Override
    protected void onBindHeaderHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    protected void onBindFooterHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    protected int getFooterViewType(int position) {
        return position;
    }

    @Override
    public int getFooterCount() {
        return mFooterViews.size();
    }

    void addFooterView(View view) {
        if (!mFooterViews.contains(view)) {
            mFooterViews.add(view);
            notifyFooterInsert(mFooterViews.indexOf(view));
        }
    }

    void removeFooterView(View view) {
        if (mFooterViews.contains(view)) {
            int index = mFooterViews.indexOf(view);
            mFooterViews.remove(view);
            notifyFooterRemoved(index);
        }
    }

    static FooterRecyclerAdapter wrapper(RecyclerView.Adapter adapter) {
        if (adapter instanceof FooterRecyclerAdapter) {
            return (FooterRecyclerAdapter) adapter;
        } else {
            return new FooterRecyclerAdapter(adapter);
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {

        FooterViewHolder(View itemView) {
            super(itemView);
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            if (lp == null) {
                lp = new RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                itemView.setLayoutParams(lp);
            }
        }
    }
}
