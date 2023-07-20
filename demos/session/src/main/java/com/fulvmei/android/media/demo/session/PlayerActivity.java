package com.fulvmei.android.media.demo.session;

import android.content.ComponentName;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Metadata;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.fulvmei.android.media.session.PlaybackService;
import com.fulvmei.android.media.ui.ControlView;
import com.fulvmei.android.media.ui.DefaultControlView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PlayerActivity extends AppCompatActivity {

    MediaController mediaController;

    ControlView controlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        controlView=findViewById(R.id.controlView);

        ListenableFuture<MediaController> mediaControllerFuture = new MediaController.Builder(this, new SessionToken(this, new ComponentName(this, PlaybackService.class))).buildAsync();
        mediaControllerFuture.addListener(() -> {
            try {
                mediaController = mediaControllerFuture.get();
                initPlayer();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, MoreExecutors.directExecutor());

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaController.clearMediaItems();
            }
        });
    }

    public void initPlayer(){
        mediaController.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Log.e("GGGG","onMediaItemTransition1 mediaItem=" + mediaItem+"  reason="+reason);
            }
        });
        List<MediaItem> list=new ArrayList<>();
        MediaMetadata mediaMetadata=new MediaMetadata.Builder().setTitle("AAAAAAAAA").build();
        MediaMetadata mediaMetadata2=new MediaMetadata.Builder().setTitle("BBBBBBBBB").build();
        list.add(MediaItem.fromUri("https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/02_-_Geisha.mp3").buildUpon().setMediaMetadata(mediaMetadata).build());
        list.add(MediaItem.fromUri("https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/02_-_Geisha.mp3").buildUpon().setMediaMetadata(mediaMetadata2).build());
        controlView.setPlayer(mediaController);
        mediaController.setMediaItems(list);
//        mediaController.setPlayWhenReady(true);
//        mediaController.prepare();
        mediaController.play();

        controlView.setPlayer(mediaController);
    }
}