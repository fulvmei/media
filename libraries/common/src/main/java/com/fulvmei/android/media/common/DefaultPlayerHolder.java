package com.fulvmei.android.media.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;

public class DefaultPlayerHolder implements PlayerHolder {
    @Nullable
    private Player player;

    @Nullable
    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void setPlayer(@Nullable Player player) {
        if (this.player == player) {
            return;
        }
        if (this.player != null) {
            onPlayerDetached(this.player);
        }

        this.player = player;
        if (player != null) {
            onPlayerAttached(player);
        }
    }

    protected void onPlayerAttached(@NonNull Player player) {

    }

    protected void onPlayerDetached(@NonNull Player player) {

    }
}
