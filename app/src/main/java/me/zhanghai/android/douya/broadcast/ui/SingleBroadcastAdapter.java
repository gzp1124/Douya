/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.util.ViewUtils;

public class SingleBroadcastAdapter
        extends RecyclerView.Adapter<SingleBroadcastAdapter.ViewHolder> {

    private Listener mListener;

    private Broadcast mBroadcast;

    public SingleBroadcastAdapter(Broadcast broadcast, Listener listener) {

        mBroadcast = broadcast;
        mListener = listener;

        setHasStableIds(true);
    }

    public Broadcast getBroadcast() {
        return mBroadcast;
    }

    public boolean hasBroadcast() {
        return mBroadcast != null;
    }

    public void setBroadcast(Broadcast broadcast) {
        // Don't check for whether mBroadcast == broadcast because we always want to invalidate.
        Broadcast oldBroadcast = mBroadcast;
        mBroadcast = broadcast;
        if (oldBroadcast == null) {
            notifyItemInserted(0);
        } else if (mBroadcast == null) {
            notifyItemRemoved(0);
        } else {
            notifyItemChanged(0);
        }
    }

    public void notifyBroadcastChanged() {
        notifyItemChanged(0);
    }

    @Override
    public int getItemCount() {
        return mBroadcast != null ? 1 : 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ViewUtils.inflate(R.layout.single_broadcast_item, parent));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Broadcast effectiveBroadcast = mBroadcast.getEffectiveBroadcast();
        holder.broadcastLayout.bindBroadcast(mBroadcast);
        holder.broadcastLayout.setListener(new BroadcastLayout.Listener() {
            @Override
            public void onLikeClicked() {
                mListener.onLike(effectiveBroadcast, !effectiveBroadcast.isLiked);
            }
            @Override
            public void onRebroadcastClicked(boolean isLongClick) {
                if (mBroadcast.isSimpleRebroadcastByOneself()) {
                    mListener.onUnrebroadcast(mBroadcast, isLongClick);
                } else {
                    mListener.onRebroadcast(effectiveBroadcast, isLongClick);
                }
            }
            @Override
            public void onCommentClicked() {
                mListener.onComment(effectiveBroadcast);
            }
        });
        holder.viewActivityButton.setOnClickListener(view -> mListener.onViewActivity(effectiveBroadcast));
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.broadcastLayout.releaseBroadcast();
    }

    public interface Listener {
        void onLike(Broadcast broadcast, boolean like);
        void onRebroadcast(Broadcast broadcast, boolean quick);
        void onUnrebroadcast(Broadcast broadcast, boolean quick);
        void onComment(Broadcast broadcast);
        void onViewActivity(Broadcast broadcast);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.broadcast)
        public BroadcastLayout broadcastLayout;
        @BindView(R.id.view_activity)
        public Button viewActivityButton;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
