package com.fulvmei.android.media.session;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaLibraryService;
import androidx.media3.session.MediaSession;

public class PlaybackService extends MediaLibraryService {

    private final MediaLibrarySession.Callback librarySessionCallback = new CustomMediaLibrarySessionCallback();
    private MediaLibrarySession mediaLibrarySession;

    @Override
    public void onCreate() {
        super.onCreate();

        Player player = new ExoPlayer.Builder(this).build();
        mediaLibrarySession = new MediaLibrarySession.Builder(this, player, librarySessionCallback).build();
    }

    @Nullable
    @Override
    public MediaLibrarySession onGetSession(@NonNull MediaSession.ControllerInfo controllerInfo) {
        return mediaLibrarySession;
    }

    private static class CustomMediaLibrarySessionCallback implements MediaLibrarySession.Callback {

    }
}
