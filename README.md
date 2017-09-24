HiLoadMore
===

HiLoadMore是一个通用可扩展的上拉加载控件，实现对任何子View的上拉加载。

![预览图](./images/1.gif)

# 使用示例
```java
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
```