package com.githang.hiloadmorelayout.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.hiloadmore.LoadMoreHandler;
import com.githang.hiloadmore.LoadMoreLayout;
import com.githang.hiloadmore.LoadMoreUIHandler;

public class MainActivity extends AppCompatActivity {

    private LoadMoreLayout mLoadMoreLayout;
    private TextView mContent;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        mLoadMoreLayout = findViewById(R.id.load_more);
        mContent = findViewById(R.id.content);
        mLoadMoreLayout.setLoadMoreUIHandler(new LoadMoreUIHandler() {
            @Override
            public void onPrepare() {
                mContent.setText("上拉可以加载");
                mToast.setText(mContent.getText());
                mToast.show();
            }

            @Override
            public void onBegin() {
                mContent.setText("开始加载");
                mToast.setText(mContent.getText());
                mToast.show();
            }

            @Override
            public void onComplete(boolean hasMore) {
                mContent.setText("加载完成");
                mToast.setText(mContent.getText());
                mToast.show();
            }

            @Override
            public void onPositionChange(int offsetY, int offsetToLoadMore) {
                mContent.setText("位移：" + offsetY + "...触发加载需要的位移:" + offsetToLoadMore);
            }
        });

        mLoadMoreLayout.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore() {
                mContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLoadMoreLayout.loadMoreComplete(true);
                    }
                }, 2000);
            }
        });
        mLoadMoreLayout.setHasMore(true);
    }
}
