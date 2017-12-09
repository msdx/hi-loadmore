/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */
package com.githang.hiloadmore.recyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.githang.hiloadmore.LoadMoreLayout;

/**
 * RecyclerView的上拉加载以及自动加载实现。
 *
 * @author Geek_Soledad (msdx.android@qq.com)
 * @since 2017-05-03 0.1
 */
public class LoadMoreRecyclerViewContainer extends LoadMoreLayout {
    private View mFooterView;

    private boolean mAutoLoadMore;

    public LoadMoreRecyclerViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View content = getContentView();
        if (content instanceof RecyclerView) {
            ((RecyclerView) content).addOnScrollListener(new RecyclerView.OnScrollListener() {

                private LinearLayoutManager mLinearLayoutManager;

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (!mAutoLoadMore || !hasMore()) {
                        return;
                    }
                    if (mLinearLayoutManager == null) {
                        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                        if (layoutManager instanceof LinearLayoutManager) {
                            mLinearLayoutManager = (LinearLayoutManager) layoutManager;
                        }
                    }
                    if (mLinearLayoutManager == null) {
                        return;
                    }

                    if ((mLinearLayoutManager.getItemCount() - recyclerView.getChildCount())
                            <= mLinearLayoutManager.findFirstVisibleItemPosition()) {
                        triggerToLoadMore();
                    }
                }
            });
        } else {
            throw new IllegalStateException("LoadMoreRecyclerViewContainer only support RecyclerView");
        }
    }

    public void setFooterView(final View footer) {
        final RecyclerView recyclerView = (RecyclerView) getContentView();
        final FooterRecyclerAdapter adapter = FooterRecyclerAdapter.wrapper(recyclerView.getAdapter());
        if (mFooterView != null && mFooterView != footer) {
            adapter.removeFooterView(mFooterView);
        }
        adapter.addFooterView(footer);
        mFooterView = footer;
        recyclerView.setAdapter(adapter);
        footer.post(new Runnable() {
            @Override
            public void run() {
                int height = footer.getHeight();
                if (height > 0) {
                    setOffsetYToLoadMore(height);
                }
            }
        });
    }

    public void setAutoLoadMore(boolean autoLoadMore) {
        mAutoLoadMore = autoLoadMore;
    }
}