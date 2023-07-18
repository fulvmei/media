package com.fulvmei.android.media.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;

public class ControlView extends FrameLayout {

    private static final String TAG = "ControlView";

    protected SeekBar seekBar;

    @Nullable
    private Player player;
    @NonNull
    protected final PlayerListener playerListener;
    @NonNull
    protected final SeekBarChangeListener seekBarChangeListener;

    protected boolean dragging;

    public ControlView(@NonNull Context context) {
        this(context, null);
    }

    public ControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        playerListener = getPlayerListener();
        seekBarChangeListener = getSeekBarChangeListener();
//        actionHandler = initActionHandler();

        LayoutInflater.from(context).inflate(getLayoutResources(), this);

        initView(context, attrs, defStyleAttr);

    }


    @LayoutRes
    private int getLayoutResources() {
        return R.layout.fu_controller_view;
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        seekBar = findViewById(R.id.fu_controller_seek);
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        }

    }

    @NonNull
    protected PlayerListener getPlayerListener() {
        return new PlayerListener();
    }

    @NonNull
    protected SeekBarChangeListener getSeekBarChangeListener() {
        return new SeekBarChangeListener();
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(@Nullable Player player) {
        if (this.player == player) {
            return;
        }
        if (this.player != null) {
            this.player.removeListener(playerListener);
        }

        this.player = player;
        if (this.player != null) {
            player.addListener(playerListener);
        }
        updateAll();
    }

    protected void updateAll() {
//        updatePlayPauseView();
//        updateProgress();
//        updateNavigation();
//        updateRepeatView();
//        updateShuffleView();
    }

    protected class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser || player == null) {
                return;
            }

            long duration = player.getDuration();
//            long newposition = (duration * progress) / 1000L;
//            mPlayer.seekTo( (int) newposition);
//            if (mCurrentTime != null)
//                mCurrentTime.setText(stringForTime( (int) newposition));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            dragging = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            dragging = false;
            if(player==null){
                return;
            }
            long duration = player.getDuration();
        }
    }

    protected class PlayerListener implements Player.Listener {
        @Override
        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
            FuLog.d(TAG, "onPlayWhenReadyChanged : playWhenReady=" + playWhenReady + ",reason=" + reason);
//            updatePlayPauseView();
//            updateProgress();
        }

        @Override
        public void onPlaybackStateChanged(int state) {
            FuLog.d(TAG, "onPlayerStateChanged : state=" + state);
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
        public void onPositionDiscontinuity(@NonNull Player.PositionInfo oldPosition, @NonNull Player.PositionInfo newPosition, int reason) {
            FuLog.d(TAG, "onPositionDiscontinuity : reason=" + reason);
//            updateNavigation();
        }
    }
}
