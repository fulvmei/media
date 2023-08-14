package com.fulvmei.android.media.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;

import com.fulvmei.android.media.common.PlayerHolder;

public class PlayerHolderView extends FrameLayout implements PlayerHolder {
    @Nullable
    protected Player player;

    public PlayerHolderView(@NonNull Context context) {
        super(context);
    }

    public PlayerHolderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayerHolderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

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
