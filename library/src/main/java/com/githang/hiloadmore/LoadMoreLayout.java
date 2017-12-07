/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */
package com.githang.hiloadmore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * 上拉加载更多
 *
 * @author Geek_Soledad (msdx.android@qq.com)
 * @since 2017-06-09 0.1
 */
public class LoadMoreLayout extends FrameLayout {

    private static final byte STATUS_INIT = 0;
    private static final byte STATUS_PREPARE = 1;
    private static final byte STATUS_LOADING = 2;
    private static final byte STATUS_COMPLETE = 3;

    private byte mStatus = STATUS_INIT;
    private int mDuration = 1000;

    View mContent;
    private int mCurrentOffsetY;
    private int mOffsetYToLoadMore = 200;
    private float mResistance = (float) Math.PI;

    private float mDownY;

    private int mDragSlop;

    private boolean mHasMore;

    private LoadMoreHandler mLoadMoreHandler;
    private LoadMoreUIHandler mLoadMoreUIHandler;

    private ScrollChecker mScrollChecker;

    public LoadMoreLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDragSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mScrollChecker = new ScrollChecker();
    }

    public void setContentView(View view) {
        mContent = view;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int childCount = getChildCount();
        if (childCount < 1) {
            throw new IllegalStateException("LoadMoreLayout needs at least one child");
        }
        if (mContent == null) {
            mContent = getChildAt(0);
            mContent.bringToFront();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int offsetY = mCurrentOffsetY;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        if (mContent != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + offsetY;
            final int right = left + mContent.getMeasuredWidth();
            final int bottom = top + mContent.getMeasuredHeight();
            mContent.layout(left, top, right, bottom);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled() || mContent == null || !mHasMore) {
            return super.onInterceptTouchEvent(ev);
        }

        boolean intercept = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();
                mScrollChecker.abortIfRunning();
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetY = (int) (ev.getY() - mDownY);
                if (Math.abs(offsetY) < mDragSlop) {
                    break;
                }
                boolean moveUp = offsetY < 0;
                boolean canMoveDown = mCurrentOffsetY < 0;
                if (moveUp && mContent.canScrollVertically(1)) {
                    //如果可以继续往下滑动，则不处理
                    break;
                }
                if (moveUp || canMoveDown) {
                    intercept = true;
                }
                break;
            default:
        }

        return intercept || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float offsetY = event.getY() - mDownY;
                if (mStatus != STATUS_LOADING && mStatus != STATUS_PREPARE) {
                    mStatus = STATUS_PREPARE;
                    mLoadMoreUIHandler.onPrepare();
                }
                movePos((int) (offsetY / mResistance));
                if (mStatus == STATUS_PREPARE) {
                    mLoadMoreUIHandler.onPositionChange(mCurrentOffsetY, mOffsetYToLoadMore);
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onRelease();
                return true;
            default:
        }
        return super.onTouchEvent(event);
    }

    private void movePos(int offsetY) {
        if (offsetY > 0 && mCurrentOffsetY == 0) {
            return;
        }
        if (offsetY > 0) {
            offsetY = 0;
        }
        mContent.setTranslationY(offsetY);
        mCurrentOffsetY = offsetY;
    }

    private void onRelease() {
        performLoadMore();
        mScrollChecker.tryToScrollTo(0, mDuration);
    }

    private void performLoadMore() {
        if (mStatus != STATUS_PREPARE) {
            return;
        }
        if (Math.abs(mCurrentOffsetY) >= mOffsetYToLoadMore) {
            mStatus = STATUS_LOADING;
            mLoadMoreHandler.onLoadMore();
            mLoadMoreUIHandler.onBegin();
        } else {
            mLoadMoreUIHandler.onPrepare();
        }
    }

    public void triggerToLoadMore() {
        if (!mHasMore || mStatus == STATUS_LOADING) {
            return;
        }
        mStatus = STATUS_LOADING;
        mLoadMoreHandler.onLoadMore();
        mLoadMoreUIHandler.onBegin();
    }

    public void loadMoreComplete(boolean hasMore) {
        mHasMore = hasMore;
        mLoadMoreUIHandler.onComplete(hasMore);
        mStatus = STATUS_COMPLETE;
    }

    public void setHasMore(boolean hasMore) {
        mHasMore = hasMore;
    }

    protected boolean hasMore() {
        return mHasMore;
    }

    public void setOffsetYToLoadMore(int offsetYToLoadMore) {
        mOffsetYToLoadMore = offsetYToLoadMore;
    }

    public void setResistance(float resistance) {
        mResistance = resistance;
    }

    public void setLoadMoreHandler(LoadMoreHandler loadMoreHandler) {
        mLoadMoreHandler = loadMoreHandler;
    }

    public void setLoadMoreUIHandler(LoadMoreUIHandler loadMoreUIHandler) {
        mLoadMoreUIHandler = loadMoreUIHandler;
    }


    class ScrollChecker implements Runnable {
        private static final int MOVE_DELAY = 12;

        private final Scroller mScroller;

        private int mStart;
        private boolean mIsRunning;

        ScrollChecker() {
            mScroller = new Scroller(getContext());
        }

        @Override
        public void run() {
            boolean isFinish = !mScroller.computeScrollOffset() || mScroller.isFinished();
            int curY = mScroller.getCurrY();
            if (!isFinish) {
                movePos(curY + mStart);
                postDelayed(this, MOVE_DELAY);
            } else {
                reset();
            }
        }

        private void reset() {
            mIsRunning = false;
            mStart = 0;
        }

        void tryToScrollTo(int to, int duration) {
            if (mCurrentOffsetY == to) {
                return;
            }
            removeCallbacks(this);
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            mStart = mCurrentOffsetY;
            mScroller.startScroll(0, 0, 0, to - mStart, duration);
            post(this);
            mIsRunning = true;
        }

        void abortIfRunning() {
            if (mIsRunning) {
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                reset();
            }
        }
    }
}
