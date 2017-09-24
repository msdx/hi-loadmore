/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */
package com.githang.hiloadmore;

/**
 * @author Geek_Soledad (msdx.android@qq.com)
 * @since 2017-05-03 0.1
 */
public interface LoadMoreUIHandler {
    void onPrepare();

    void onBegin();

    void onComplete(boolean hasMore);

    void onPositionChange(int offsetY, int offsetToLoadMore);
}
