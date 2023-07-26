package com.fulvmei.android.media.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;
import androidx.media3.common.Tracks;
import androidx.media3.common.util.BitmapLoader;
import androidx.media3.session.CacheBitmapLoader;
import androidx.media3.session.SimpleBitmapLoader;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ExecutionException;

public class AudioControlView extends ControlView {

    protected ImageView artworkView;

    public AudioControlView(@NonNull Context context) {
        this(context, null);
    }

    public AudioControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        artworkView = findViewById(R.id.fu_audio_control_artwork);
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.fu_audio_control_view;
    }

    @NonNull
    @Override
    protected ControlPlayerListener getControlPlayerListener() {
        return new AudioControlPlayerListener();
    }

    @Override
    protected void updateAll() {
        super.updateAll();
        updateArtwork();
    }

    protected void updateArtwork() {
        Player player = getPlayer();
        if (!attachedToWindow || artworkView == null || player == null) {
            return;
        }
        BitmapLoader bitmapLoader = new CacheBitmapLoader(new SimpleBitmapLoader());
        ListenableFuture<Bitmap> bitmapLoaderFuture = bitmapLoader.loadBitmapFromMetadata(player.getMediaMetadata());
        if (bitmapLoaderFuture != null) {
            bitmapLoaderFuture.addListener(() -> {
                try {
                    updateArtworkBitmap(bitmapLoaderFuture.get());
                } catch (ExecutionException | InterruptedException e) {
                    updateArtworkBitmap(null);
                    throw new RuntimeException(e);
                }
            }, MoreExecutors.directExecutor());
        }else {
            updateArtworkBitmap(null);
        }
    }

    protected void updateArtworkBitmap(Bitmap bitmap) {
        if (artworkView != null) {
            artworkView.setImageBitmap(bitmap);
        }
    }

    protected class AudioControlPlayerListener extends ControlPlayerListener {

        @Override
        public void onTracksChanged(@NonNull Tracks tracks) {
            super.onTracksChanged(tracks);
            updateArtwork();
        }
    }
}
