package com.fulvmei.android.media.session;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.common.util.Util;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaLibraryService;
import androidx.media3.session.MediaSession;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class PlaybackService extends MediaLibraryService {

    private final MediaLibrarySession.Callback librarySessionCallback = new CustomMediaLibrarySessionCallback();
    private MediaLibrarySession mediaLibrarySession;

    @Override
    public void onCreate() {
        super.onCreate();

        Player player = new ExoPlayer.Builder(this)
                .setMediaSourceFactory(new DefaultMediaSourceFactory(this))
                .build();

        mediaLibrarySession = new MediaLibrarySession.Builder(this, player, librarySessionCallback).build();

        player.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
//                Log.e("GGGG","onMediaItemTransition mediaItem=" + System.currentTimeMillis()+"  reason="+reason);
            }
            @Override
            public void onMediaMetadataChanged(MediaMetadata mediaMetadata) {
                Log.e("GGGG","onMediaMetadataChanged mediaMetadata="+mediaMetadata.title);
            }
        });
    }

    @Nullable
    @Override
    public MediaLibrarySession onGetSession(@NonNull MediaSession.ControllerInfo controllerInfo) {
        return mediaLibrarySession;
    }

    private static class CustomMediaLibrarySessionCallback implements MediaLibrarySession.Callback {

//        @Override
//        public ListenableFuture<List<MediaItem>> onAddMediaItems(MediaSession mediaSession, MediaSession.ControllerInfo controller, List<MediaItem> mediaItems) {
//            for (MediaItem mediaItem : mediaItems) {
//                if (mediaItem.localConfiguration == null) {
//                    return Futures.immediateFailedFuture(new UnsupportedOperationException());
//                }
//            }
//            ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
//            ListenableFuture<List<MediaItem>> future = executorService.submit(new Callable<List<MediaItem>>() {
//
//                @Override
//                public List<MediaItem> call() throws Exception {
//                    Thread.sleep(3000);
//                    return mediaItems;
//                }
//            });
//            return future;
////            return Futures.immediateFuture(mediaItems);
//        }
//
//        @Override
//        public ListenableFuture<MediaSession.MediaItemsWithStartPosition> onSetMediaItems(MediaSession mediaSession, MediaSession.ControllerInfo controller, List<MediaItem> mediaItems, int startIndex, long startPositionMs) {
//            return Util.transformFutureAsync(
//                    onAddMediaItems(mediaSession, controller, mediaItems),
//                    (mediaItemList) ->
//                            Futures.immediateFuture(
//                                    new MediaSession.MediaItemsWithStartPosition(mediaItemList, startIndex, startPositionMs)));
//        }
    }
}
