/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */
package com.githang.recycleradapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * @author Geek_Soledad (msdx.android@qq.com)
 * @since 2017-05-03 0.1
 */
public abstract class HiRecyclerAdapter extends RecyclerView.Adapter {
    /**
     * 由于RecyclerView的灵活性，可能会出现不同的Adapter的嵌套，而因此可能导致viewType重复。
     * 因此在这里尝试为每个Adapter定义一个区间，而避免它们的重复问题。
     */
    public static final int TYPE_SECTION = 2 << 10;

    /**
     * 从2<<20开始，留足够的type值给content使用。
     */
    private static final int HEADER_OFFSET = TYPE_SECTION << 10;
    private static final int FOOTER_OFFSET = HEADER_OFFSET + TYPE_SECTION;

    private int mHeaderCount;
    private int mContentCount;
    private int mFooterCount;

    private RecyclerView.Adapter mWrapperAdapter;
    private RecyclerView.AdapterDataObserver mDataObserver;

    public HiRecyclerAdapter(RecyclerView.Adapter wrapperAdapter) {
        mWrapperAdapter = wrapperAdapter;
        mDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                notifyItemRangeChanged(positionStart + mHeaderCount, itemCount);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                notifyItemRangeChanged(positionStart + mHeaderCount, itemCount, payload);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                notifyItemRangeInserted(positionStart + mHeaderCount, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                notifyItemRangeRemoved(positionStart + mHeaderCount, itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                notifyItemMoved(fromPosition + mHeaderCount, toPosition + mHeaderCount);
            }
        };
        mWrapperAdapter.registerAdapterDataObserver(mDataObserver);
    }

    public RecyclerView.Adapter getWrapperAdapter() {
        return mWrapperAdapter;
    }

    public int getContentPosition(int position) {
        return position - mHeaderCount;
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType >= HEADER_OFFSET && viewType < HEADER_OFFSET + TYPE_SECTION) {
            return onCreateHeaderHolder(parent, viewType - HEADER_OFFSET);
        } else if (viewType >= FOOTER_OFFSET && viewType < FOOTER_OFFSET + TYPE_SECTION) {
            return onCreateFooterHolder(parent, viewType - FOOTER_OFFSET);
        } else {
            return mWrapperAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    /**
     * Called when RecyclerView needs a new header {@link RecyclerView.ViewHolder} of the given type to
     * represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    protected abstract RecyclerView.ViewHolder onCreateHeaderHolder(ViewGroup parent, int viewType);

    /**
     * Called when RecyclerView needs a new footer {@link RecyclerView.ViewHolder} of the given type to
     * represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    protected abstract RecyclerView.ViewHolder onCreateFooterHolder(ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < mHeaderCount) {
            onBindHeaderHolder(holder, position);
        } else if (position < mHeaderCount + mContentCount) {
            mWrapperAdapter.onBindViewHolder(holder, position - mHeaderCount);
        } else {
            onBindFooterHolder(holder, position - mHeaderCount - mContentCount);
        }
    }

    /**
     * Called by RecyclerView to display the header at the specified position. This method should
     * update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the header
     * item at the given position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    protected abstract void onBindHeaderHolder(RecyclerView.ViewHolder holder, int position);

    /**
     * Called by RecyclerView to display the footer at the specified position. This method should
     * update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the footer
     * item at the given position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    protected abstract void onBindFooterHolder(RecyclerView.ViewHolder holder, int position);

    public int getHeaderCount() {
        return 0;
    }

    public final int getContentCount() {
        return mWrapperAdapter.getItemCount();
    }

    public int getFooterCount() {
        return 0;
    }

    @Override
    public final int getItemCount() {
        mHeaderCount = getHeaderCount();
        mContentCount = mWrapperAdapter.getItemCount();
        mFooterCount = getFooterCount();
        return mHeaderCount + mContentCount + mFooterCount;
    }

    @Override
    public final int getItemViewType(int position) {
        if (position < mHeaderCount) {
            return validateViewType(getHeaderViewType(position)) + HEADER_OFFSET;
        } else if (position < mHeaderCount + mContentCount) {
            return validateViewType(mWrapperAdapter.getItemViewType(position - mHeaderCount));
        } else {
            return validateViewType(getFooterViewType(position - mHeaderCount - mContentCount)) + FOOTER_OFFSET;
        }
    }

    private int validateViewType(int viewType) {
        if (viewType < 0 || viewType >= TYPE_SECTION) {
            throw new IllegalStateException("viewType must be between 0 and " + TYPE_SECTION);
        }
        return viewType;
    }

    protected int getHeaderViewType(int position) {
        return 0;
    }

    protected int getFooterViewType(int position) {
        return 0;
    }

    public void notifyFooterInsert(int positionStart) {
        notifyItemInserted(mHeaderCount + mContentCount + positionStart);
    }

    public void notifyFooterRemoved(int positionStart) {
        notifyItemRemoved(mHeaderCount + mContentCount + positionStart);
    }
}
