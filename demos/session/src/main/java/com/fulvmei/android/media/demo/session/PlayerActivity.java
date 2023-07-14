package com.fulvmei.android.media.demo.session;

import android.content.ComponentName;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.fulvmei.android.media.session.PlaybackService;
import com.fulvmei.android.media.ui.DefaultControlView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ExecutionException;

public class PlayerActivity extends AppCompatActivity {

    MediaController mediaController;

    DefaultControlView controlView;

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
    }

    public void initPlayer(){
        mediaController.setMediaItem(MediaItem.fromUri("https://storage.googleapis.com/uamp/The_Kyoto_Connection_-_Wake_Up/02_-_Geisha.mp3"));
//        mediaController.setPlayWhenReady(true);
//        mediaController.prepare();
        mediaController.play();

        controlView.setPlayer(mediaController);
    }
}