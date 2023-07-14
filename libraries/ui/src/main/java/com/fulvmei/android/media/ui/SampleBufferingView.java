package com.fulvmei.android.media.ui;

import android.content.Context;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SampleBufferingView extends BaseStateView {

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHOW_MODE_ALWAYS, SHOW_MODE_PLAYING, SHOW_MODE_NEVER})
    @interface ShowMode {
    }

    /**
     * The buffering view is always shown when the player is in the {@link Player#STATE_BUFFERING
     * buffering} state.
     */
    public static final int SHOW_MODE_ALWAYS = 0;

    /**
     * The buffering view is shown when the player is in the {@link Player#STATE_BUFFERING buffering}
     * state and {@link Player#getPlayWhenReady() playWhenReady} is {@code true}.
     */
    public static final int SHOW_MODE_PLAYING = 1;

    /**
     * The buffering view is never shown.
     */
    public static final int SHOW_MODE_NEVER = 2;

    protected final ComponentListener componentListener;

    private int showMode;

    private boolean showInDetachPlayer;

    public SampleBufferingView(@NonNull Context context) {
        this(context, null);
    }

    public SampleBufferingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SampleBufferingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = onCreateView(LayoutInflater.from(context), this);

        if (view != null) {
            addView(view);
        }

        componentListener = new ComponentListener();

        updateVisibility();
    }

    @Override
    protected void onFullScreenChanged(boolean fullScreen) {

    }

    protected View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.sample_buffering_view, parent, false);
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
        } else {
            if (player.getPlaybackState() == Player.STATE_BUFFERING) {
                if (showMode == SHOW_MODE_ALWAYS) {
                    return true;
                } else return showMode == SHOW_MODE_PLAYING && player.getPlayWhenReady();
            }
        }
        return false;
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

    @ShowMode
    public int getShowMode() {
        return showMode;
    }

    public void setShowMode(@ShowMode int showMode) {
        this.showMode = showMode;
        updateVisibility();
    }

    public boolean isShowInDetachPlayer() {
        return showInDetachPlayer;
    }

    public void setShowInDetachPlayer(boolean showInDetachPlayer) {
        this.showInDetachPlayer = showInDetachPlayer;
        updateVisibility();
    }

    private final class ComponentListener implements Player.Listener {

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            updateVisibility();
        }

        @Override
        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
            updateVisibility();
        }

    }
}
