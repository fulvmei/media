package com.fulvmei.android.media.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;

public class VideoPlayView extends FrameLayout {

    @Nullable
    protected Player player;
    @Nullable
    protected MediaItem mediaItem;
    protected PlayerView playerView;
    protected PlayerControlView playerControlView;
    protected StateView bufferingView;
    protected StateView endView;
    protected StateView errorView;

    public VideoPlayView(@NonNull Context context) {
        this(context, null);
    }

    public VideoPlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(getLayoutResources(), this);

        initView();
    }

    @LayoutRes
    protected int getLayoutResources() {
        return R.layout.fu_video_play_view;
    }

    protected void initView() {
        playerView = findViewById(R.id.fu_video_play_player_view);
        playerControlView = findViewById(R.id.fu_video_play_player_control_view);
        bufferingView = findViewById(R.id.fu_video_play_player_buffering_view);
        endView = findViewById(R.id.fu_video_play_player_end_view);
        errorView = findViewById(R.id.fu_video_play_player_error_view);

        updateViews();
    }

    protected void updateViews() {
        if (playerView != null) {
            playerView.setPlayer(player);
        }
        if (playerControlView != null) {
            playerControlView.setPlayer(player);
        }
        if (bufferingView != null) {
            bufferingView.setPlayer(player);
        }
        if (endView != null) {
            endView.setPlayer(player);
        }
        if (errorView != null) {
            errorView.setPlayer(player);
        }
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(@Nullable Player player) {
        if (this.player == player) {
            return;
        }
        this.player = player;
        updateViews();
        updatePlayerMediaItem();
    }

    @Nullable
    public MediaItem getMediaItem() {
        return mediaItem;
    }

    public void setMediaItem(@Nullable MediaItem mediaItem) {
        if (this.mediaItem == mediaItem) {
            return;
        }
        this.mediaItem = mediaItem;
        updatePlayerMediaItem();
    }

    protected void updatePlayerMediaItem() {
        if (player != null) {
            MediaItem currentMediaItem = player.getCurrentMediaItem();
            if (currentMediaItem == mediaItem) {
                return;
            }
            if (mediaItem != null) {
                player.setMediaItem(mediaItem);
            } else {
                player.clearMediaItems();
            }

        }
    }

    public void start() {
        if (player != null) {
            if (player.getPlaybackState() == Player.STATE_IDLE) {
                player.prepare();
            }
            if (player.getPlaybackState() == Player.STATE_ENDED) {
                player.seekToDefaultPosition();
            }
            player.play();
        }
    }

    public void resume() {
        if (player != null) {
            player.play();
        }
        if (playerView != null) {
            playerView.onResume();
        }
    }

    public void pause() {
        if (player != null) {
            player.pause();
        }
        if (playerView != null) {
            playerView.onPause();
        }
    }

    public void stop() {
        if (player != null) {
            player.stop();
        }
    }
}
