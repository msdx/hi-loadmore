/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */
package com.githang.hiloadmore;

/**
 * 上拉加载更多的 UI 处理接口。
 * @author Geek_Soledad (msdx.android@qq.com)
 * @since 2017-05-03 0.1
 */
public interface LoadMoreUIHandler {
    /**
     * 可以上拉加载。
     */
    void onPrepare();

    /**
     * 开始加载。
     */
    void onBegin();

    /**
     * 加载完成。
     *
     * @param hasMore 是否还可以上拉加载。
     */
    void onComplete(boolean hasMore);

    /**
     * 纵向位移发生变化后的回调。
     *
     * @param offsetY          纵向相对于初始位置的位移量。
     * @param offsetToLoadMore 纵向相对于到达加载更多的位移量。
     */
    void onPositionChange(int offsetY, int offsetToLoadMore);
}
