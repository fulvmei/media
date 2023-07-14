package com.fulvmei.android.media.ui;

public interface StateView extends PlayerHolder {

    interface VisibilityChangeListener {
        void onVisibilityChange(StateView stateView, boolean visibility);
    }

    default void addVisibilityChangeListener(VisibilityChangeListener l) {
    }

    default void removeVisibilityChangeListener(VisibilityChangeListener l) {
    }

    default boolean isFullScreen() {
        return false;
    }

    default void setFullScreen(boolean fullScreen) {
    }

    /**
     * Returns whether the PlayerController is currently showing.
     */
    default boolean isShowing() {
        return false;
    }

    /**
     * Shows the playback controls
     */
    default void show() {
    }

    /**
     * Hides the controller.
     */
    default void hide() {
    }
}
