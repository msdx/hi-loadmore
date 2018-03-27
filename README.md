HiLoadMore
===

 [ ![Download](https://api.bintray.com/packages/msdx/maven/HiLoadMore/images/download.svg) ](https://bintray.com/msdx/maven/HiLoadMore/_latestVersion)

HiLoadMore是一个通用可扩展的上拉加载控件，实现对任何子View的上拉加载。

![预览图](./images/1.gif)

# Gradle 依赖

```groovy
    compile 'com.githang:hi-loadmore:0.2'
    // OR
    implementation 'com.githang:hi-loadmore:0.2'
```

# Javadoc

请访问：http://githang.com/hi-loadmore/

# 目前实现功能

- 通用上拉加载
- 可扩展
- 包含了 RecyclerView 上拉加载及自动加载的扩展

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

        mLoadMoreLayout.setHasMore(true);
```

# 参考文章及项目

- [《我眼中的下拉刷新》](https://www.liaohuqiu.net/cn/posts/the-pull-to-refresh-in-my-eyes/)
- liaohuqiu/[android-Ultra-Pull-To-Refresh](https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh)
- nukc/[LoadMoreLayout](https://github.com/nukc/LoadMoreLayout)
