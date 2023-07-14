package com.fulvmei.android.media.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;
import androidx.media3.common.Timeline;

public class ControlView extends FrameLayout {

    private static final String TAG = "ControlView";

    private Player player;

    public ControlView(@NonNull Context context) {
        super(context);
    }

    public ControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        if (this.player == player) {
            return;
        }
        if (this.player != null) {
//            this.player.removeListener(playerEventsHandler);
        }

        this.player = player;
        if (player != null) {
//            player.addListener(playerEventsHandler);
        }
//        if (mProgressAdapter != null) {
//            mProgressAdapter.setPlayer(player);
//        }
//        updateAll();
    }

    protected class PlayerEventsHandler implements Player.Listener {
        @Override
        public void onTimelineChanged(@NonNull Timeline timeline, int reason) {
            FuLog.d(TAG, "onTimelineChanged : timeline=" + timeline + ",reason=" + reason);
//            updateNavigation();
//            updateProgress();
        }

        @Override
        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
            FuLog.d(TAG, "onPlayWhenReadyChanged : playWhenReady=" + playWhenReady + ",reason=" + reason);
//            updateShowOrHide();
//            updatePlayPauseView();
//            updateProgress();
        }

        @Override
        public void onPlaybackStateChanged(int state) {
            FuLog.d(TAG, "onPlayerStateChanged : state=" + state);
//            updateShowOrHide();
//            updatePlayPauseView();
//            updateProgress();
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            FuLog.d(TAG, "onRepeatModeChanged : repeatMode=" + repeatMode);
//            updateNavigation();
//            updateRepeatView();
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            FuLog.d(TAG, "onShuffleModeEnabledChanged : shuffleModeEnabled=" + shuffleModeEnabled);
//            updateNavigation();
//            updateRepeatView();
//            updateShuffleView();
        }

        @Override
        public void onVolumeChanged(float volume) {
            FuLog.d(TAG, "onVolumeChanged : volume=" + volume);
//            updateVolumeView();
        }

        @Override
        public void onPositionDiscontinuity(@NonNull Player.PositionInfo oldPosition, @NonNull Player.PositionInfo newPosition, int reason) {
//            updateNavigation();
        }

    }
}
