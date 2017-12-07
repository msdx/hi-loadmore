/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */
package com.githang.hiloadmore.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.githang.hiloadmore.LoadMoreUIHandler;
import com.githang.hiloadmore.R;

/**
 * @author Geek_Soledad (msdx.android@qq.com)
 * @since 2017-05-03 0.1
 */
public class RecyclerFooterView extends FrameLayout implements LoadMoreUIHandler {
    private TextView mText;

    public RecyclerFooterView(@NonNull Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.footer_load_more, this, true);
        mText = findViewById(R.id.footer_text);
    }

    @Override
    public void onPrepare() {
        mText.setVisibility(VISIBLE);
        mText.setText(R.string.pull_to_load_more);
    }

    @Override
    public void onBegin() {
        mText.setText(R.string.tip_loading_more);
    }

    @Override
    public void onComplete(boolean hasMore) {
        mText.setText(hasMore ? R.string.pull_to_load_more : R.string.tip_load_finish);
    }

    @Override
    public void onPositionChange(int offsetY, int offsetToLoadMore) {
        if (Math.abs(offsetY) > offsetToLoadMore) {
            mText.setText(R.string.release_to_load_more);
        } else {
            mText.setText(R.string.pull_to_load_more);
        }
    }
}
