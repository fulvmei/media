package com.fulvmei.android.media.ui;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;

import android.util.AttributeSet;
import android.widget.FrameLayout;


import java.util.concurrent.CopyOnWriteArraySet;


public abstract class BaseStateView extends FrameLayout implements StateView {

    protected Player player;

    private final CopyOnWriteArraySet<VisibilityChangeListener> visibilityChangeListeners;

    protected boolean fullScreen;

    public BaseStateView(@NonNull Context context) {
        this(context, null);
    }

    public BaseStateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseStateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        visibilityChangeListeners = new CopyOnWriteArraySet<>();
    }

    @Override
    public void addVisibilityChangeListener(VisibilityChangeListener l) {
        visibilityChangeListeners.add(l);
    }

    @Override
    public void removeVisibilityChangeListener(VisibilityChangeListener l) {
        visibilityChangeListeners.remove(l);
    }

    @Override
    public boolean isFullScreen() {
        return fullScreen;
    }

    @Override
    public void setFullScreen(boolean fullScreen) {
        if (this.fullScreen == fullScreen) {
            return;
        }
        this.fullScreen = fullScreen;
        onFullScreenChanged(fullScreen);
    }

    @Override
    public boolean isShowing() {
        return getVisibility() == VISIBLE;
    }

    @Override
    public void show() {
        if (!isShowing()) {
            setVisibility(VISIBLE);
            dispatchVisibilityChanged(true);
        }
    }

    @Override
    public void hide() {
        if (isShowing()) {
            setVisibility(GONE);
            dispatchVisibilityChanged(false);
        }
    }

    @Override
    public void setPlayer(Player player) {
        if (this.player == player) {
            return;
        }
        Player tempPlayer = this.player;
        if (player == null) {
            this.player = null;
            onDetachedFromPlayer(tempPlayer);
            return;
        }
        this.player = player;
        onAttachedToPlayer(player);
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    protected abstract void onFullScreenChanged(boolean fullScreen);

    protected abstract void onAttachedToPlayer(@NonNull Player player);

    protected abstract void onDetachedFromPlayer(@NonNull Player player);

    /**
     * Dispatch callbacks to {@link #addVisibilityChangeListener} down
     * the view hierarchy.
     */
    protected void dispatchVisibilityChanged(boolean visibility) {
        for (VisibilityChangeListener listener : visibilityChangeListeners) {
            if (listener != null) {
                listener.onVisibilityChange(this, visibility);
            }
        }
    }

}
