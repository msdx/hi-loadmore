/*
 * Copyright (c) 2017-2018. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
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
 * @version 0.2
 * @since 2017-06-09 0.1
 */
public class LoadMoreLayout extends FrameLayout {

    private static final byte STATUS_INIT = 0;
    private static final byte STATUS_PREPARE = 1;
    private static final byte STATUS_LOADING = 2;
    private static final byte STATUS_COMPLETE = 3;

    private byte mStatus = STATUS_INIT;
    private int mDuration = 1000;

    private View mContent;
    private int mCurrentPosition;
    private int mOffsetYToLoadMore = 200;
    private float mResistance = (float) Math.PI;

    private float mDownY;
    private float mLastY;

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
        if (mContent == null) {
            mContent = getChildAt(0);
            mContent.bringToFront();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mContent != null) {
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin;
            final int right = left + mContent.getMeasuredWidth();
            final int bottom = top + mContent.getMeasuredHeight();
            mContent.layout(left, top, right, bottom);
            mContent.setTranslationY(mCurrentPosition);
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
                mLastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetY = (int) (ev.getY() - mDownY);
                if (Math.abs(offsetY) < mDragSlop) {
                    break;
                }
                boolean moveUp = offsetY < 0;
                boolean canMoveDown = mCurrentPosition < 0;
                if (moveUp && mContent.canScrollVertically(1)) {
                    //如果子View可以继续往上滑动，则不处理
                    break;
                }
                if (moveUp || canMoveDown) {
                    intercept = true;
                    mScrollChecker.abortIfRunning();
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
                float offsetY = event.getY() - mLastY;
                if (mStatus != STATUS_LOADING && mStatus != STATUS_PREPARE) {
                    mStatus = STATUS_PREPARE;
                    mLoadMoreUIHandler.onPrepare();
                }
                movePos((int) (offsetY / mResistance) + mCurrentPosition);
                if (mStatus == STATUS_PREPARE) {
                    mLoadMoreUIHandler.onPositionChange(mCurrentPosition, mOffsetYToLoadMore);
                }
                mLastY = event.getY();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onRelease();
                return true;
            default:
        }
        return super.onTouchEvent(event);
    }

    private void movePos(int position) {
        if (position > 0 && mCurrentPosition == 0) {
            return;
        }
        if (position > 0) {
            position = 0;
        }
        mContent.setTranslationY(position);
        mCurrentPosition = position;
    }

    private void onRelease() {
        performLoadMore();
        mCurrentPosition = (int) mContent.getTranslationY();
        mScrollChecker.tryToScrollTo(0, mDuration);
    }

    private void performLoadMore() {
        if (mStatus != STATUS_PREPARE) {
            return;
        }
        if (Math.abs(mCurrentPosition) >= mOffsetYToLoadMore) {
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
        setHasMore(hasMore);
        mStatus = STATUS_COMPLETE;
    }

    public View getContentView() {
        return mContent;
    }

    public void setHasMore(boolean hasMore) {
        mHasMore = hasMore;
        mLoadMoreUIHandler.onComplete(hasMore);
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

        private boolean mIsRunning;

        ScrollChecker() {
            mScroller = new Scroller(getContext());
        }

        @Override
        public void run() {
            boolean isFinish = !mScroller.computeScrollOffset();
            if (!isFinish) {
                movePos(mScroller.getCurrY());
                postDelayed(this, MOVE_DELAY);
            } else {
                reset();
            }
        }

        private void reset() {
            mIsRunning = false;
            removeCallbacks(this);
        }

        void tryToScrollTo(int to, int duration) {
            if (mCurrentPosition == to) {
                return;
            }
            removeCallbacks(this);
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            mScroller.startScroll(0, mCurrentPosition, 0, -mCurrentPosition - to, duration);
            post(this);
            mIsRunning = true;
        }

        void abortIfRunning() {
            if (mIsRunning) {
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                reset();
            }
        }
    }
}
