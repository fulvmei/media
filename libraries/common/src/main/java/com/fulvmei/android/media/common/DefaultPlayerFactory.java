package com.fulvmei.android.media.common;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

public class DefaultPlayerFactory implements PlayerFactory {
    private final @NonNull Context context;

    public DefaultPlayerFactory(@NonNull Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Player createPlayer() {
        return new ExoPlayer.Builder(context).build();
    }
}
