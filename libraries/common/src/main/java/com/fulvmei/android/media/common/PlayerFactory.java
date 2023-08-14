package com.fulvmei.android.media.common;

import androidx.annotation.NonNull;
import androidx.media3.common.Player;

public interface PlayerFactory {
    @NonNull
    Player createPlayer();
}
