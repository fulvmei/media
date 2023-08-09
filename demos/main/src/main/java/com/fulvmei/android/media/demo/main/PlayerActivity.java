package com.fulvmei.android.media.demo.main;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.common.Timeline;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;


import com.bumptech.glide.Glide;
import com.fulvmei.android.media.ui.PlayerControlView;
import com.fulvmei.android.media.ui.PlayerView;
import com.fulvmei.android.media.ui.SampleBufferingView;
import com.fulvmei.android.media.ui.SampleEndedView;
import com.fulvmei.android.media.ui.SampleErrorView;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

import com.fulvmei.android.media.demo.main.bean.Media;

public class PlayerActivity extends AppCompatActivity {

    public static final String TAG = "VideoPlayerActivity";

    private View playerRoot;
    private Player player;
    private SampleBufferingView loadingView;
    private SampleErrorView errorView;
    private SampleEndedView endedView;
    private Bitmap bigIcon;
    PlayerControlView controlView;
    PlayerView playerView;

    List<Media> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        dataList = getIntent().getParcelableArrayListExtra("list");

        ImmersionBar.with(this)
                .statusBarColorInt(Color.BLACK)
                .fitsSystemWindows(true)
                .hideBar(BarHide.FLAG_SHOW_BAR)
                .init();

        findViewById(R.id.add1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.setMediaItem(MediaItem.fromUri(dataList.get(0).getPath()).buildUpon().setMediaMetadata(
                        new MediaMetadata.Builder()
                                .setArtist("111111111111111111111")
                        .build()).build());
            }
        });

        findViewById(R.id.add2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timeline.Window window=new Timeline.Window();
                player.getCurrentTimeline().getWindow(player.getCurrentMediaItemIndex(),window);
                Log.e("rrrr", window.mediaItem.localConfiguration.tag+"");
            }
        });

        findViewById(R.id.previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.seekToPreviousMediaItem();
            }
        });

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.seekToNextMediaItem();
            }
        });

        playerRoot = findViewById(R.id.playerRoot);

        playerView = findViewById(R.id.playerView);

        Glide.with(this)
                .asBitmap()
                .load("http://pic29.nipic.com/20130517/9252150_140653449378_2.jpg")
//                .bitmapTransform(new BlurTransformation(PlayerActivity.this,23,4))  // “23”：设置模糊度(在0.0到25.0之间)，默认”25";"4":图片缩放比例,默认“1”。
                .into(playerView.getUnderlayView());

        loadingView = findViewById(R.id.bufferingView);
        errorView = findViewById(R.id.errorView);
        endedView = findViewById(R.id.endedView);
        controlView = findViewById(R.id.controlView);

        initPlayer();
        onOrientationChanged(getResources().getConfiguration().orientation);
    }


    private void initPlayer() {

        player =new ExoPlayer.Builder(this).build();
        player.setRepeatMode(Player.REPEAT_MODE_OFF);
        player.addListener(new Player.Listener() {

            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {
                Timeline.Window window = new Timeline.Window();
                timeline.getWindow(0, window);
                Log.e("GGGG","onTimelineChanged window.isDynamic="+window.isDynamic);
                Log.e("GGGG","onTimelineChanged window.isSeekable="+window.isSeekable);
                Log.e("GGGG","onTimelineChanged window.isLive="+window.isLive());
            }
        });

        List<MediaItem> mediaItemList = new ArrayList<>();
        for (Media media : dataList) {

            MediaMetadata metadata=new MediaMetadata.Builder()
                    .setTitle(media.getName())
                    .build();

            MediaItem mediaItem = new MediaItem.Builder()
                    .setMediaId(media.getPath())
                    .setUri(media.getPath())
                    .setMediaMetadata(metadata)
                    .setTag("11111111111111")
                    .build();
            mediaItemList.add(mediaItem);
        }

        player.setMediaItems(mediaItemList);
        player.prepare();
        player.play();

        loadingView.setPlayer(player);
        errorView.setPlayer(player);
        endedView.setPlayer(player);
        playerView.setPlayer(player);
        controlView.setPlayer(player);
    }

    private void onOrientationChanged(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ViewGroup.LayoutParams layoutParams = playerRoot.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

            ImmersionBar.with(this)
                    .fitsSystemWindows(false)
                    .hideBar(BarHide.FLAG_HIDE_BAR)
                    .init();
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            ViewGroup.LayoutParams layoutParams = playerRoot.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            layoutParams.height = (int) (ScreenTools.getScreenWidth(this) * 9 * 1.0f / 16);
            layoutParams.height = 1200;

            ImmersionBar.with(this)
                    .statusBarColorInt(Color.BLACK)
                    .fitsSystemWindows(true)
                    .hideBar(BarHide.FLAG_SHOW_BAR)
                    .init();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onOrientationChanged(newConfig.orientation);
    }

    @UnstableApi
    @Override
    protected void onResume() {
        super.onResume();
        playerView.onResume();
    }

    @UnstableApi
    @Override
    protected void onPause() {
        super.onPause();
        playerView.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (player == null) {
            initPlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
