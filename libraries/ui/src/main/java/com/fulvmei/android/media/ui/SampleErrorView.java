package com.fulvmei.android.media.ui;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;
import androidx.media3.common.Timeline;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SampleErrorView extends BaseStateView {

    protected final ComponentListener componentListener;

    protected OnReplayListener onReplayListener;

    protected boolean showInDetachPlayer;

    public interface OnReplayListener {
        boolean onReplay(Player player);
    }

    public SampleErrorView(@NonNull Context context) {
        this(context, null);
    }

    public SampleErrorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SampleErrorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = onCreateView(LayoutInflater.from(context), this);

        if (view != null) {
            addView(view);
        }

        componentListener = new ComponentListener();

        updateVisibility();

        View retry = findViewById(R.id.fu_state_error_retry);
        if (retry != null) {
            retry.setOnClickListener(v -> {
                if (onReplayListener != null && onReplayListener.onReplay(player)) {
                    return;
                }
                if (player != null) {
                    player.prepare();
                }
            });
        }
    }

    @Override
    protected void onFullScreenChanged(boolean fullScreen) {

    }

    protected View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.sample_error_view, parent, false);
    }

    protected void updateVisibility() {
        if (isInShowState()) {
            show();
        } else {
            hide();
        }
    }

    protected boolean isInShowState() {
        if (player == null) {
            return showInDetachPlayer;
        }
        return player.getPlaybackState() == Player.STATE_IDLE && player.getPlayerError() != null;
    }

    @Override
    protected void onAttachedToPlayer(@NonNull Player player) {
        player.addListener(componentListener);
        updateVisibility();
    }

    @Override
    protected void onDetachedFromPlayer(@NonNull Player player) {
        player.removeListener(componentListener);
        updateVisibility();
    }

    protected int getLayoutResourcesId() {
        return R.layout.sample_error_view;
    }

    public OnReplayListener getOnReplayListener() {
        return onReplayListener;
    }

    public void setOnReplayListener(OnReplayListener onReplayListener) {
        this.onReplayListener = onReplayListener;
    }

    private final class ComponentListener implements Player.Listener {

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            updateVisibility();
        }

        @Override
        public void onTimelineChanged(@NonNull Timeline timeline, int reason) {
            updateVisibility();
        }
    }
}
