package com.githang.hiloadmorelayout.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.githang.hiloadmore.LoadMoreHandler;
import com.githang.hiloadmore.recyclerview.LoadMoreRecyclerViewContainer;
import com.githang.hiloadmore.recyclerview.RecyclerFooterView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Geek_Soledad (msdx.android@qq.com)
 * @since 2017-12-09
 */
public class RecyclerViewActivity extends AppCompatActivity {
    private LoadMoreRecyclerViewContainer mLoadMoreLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<ItemViewHolder> mAdapter = new RecyclerView.Adapter<ItemViewHolder>() {
        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(RecyclerViewActivity.this);
            return new ItemViewHolder(inflater.inflate(R.layout.item_recycler_view, parent, false));
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            holder.update(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    };
    private final List<String> mData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        mLoadMoreLayout = findViewById(R.id.load_more);
        mRecyclerView = findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mData.addAll(mockNewData(10));
        mAdapter.notifyDataSetChanged();

        mLoadMoreLayout.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore() {
                mLoadMoreLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mData.addAll(mockNewData(10));
                        mAdapter.notifyDataSetChanged();
                        mLoadMoreLayout.loadMoreComplete(true);
                    }
                }, 1000);
            }
        });

        RecyclerFooterView footerView = new RecyclerFooterView(this);
        mLoadMoreLayout.setFooterView(footerView);
        mLoadMoreLayout.setLoadMoreUIHandler(footerView);
        mLoadMoreLayout.setHasMore(true);
    }

    private List<String> mockNewData(int count) {
        final List<String> data = new ArrayList<>(count);
        for (int i = mData.size() + 1, max = i + count; i < max; i++) {
            data.add("第" + i + "项");
        }
        return data;
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTextView;

        ItemViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(android.R.id.text1);
        }

        void update(String text) {
            mTextView.setText(text);
        }
    }
}
