package com.fulvmei.android.media.ui;

import static androidx.media3.common.Player.COMMAND_SEEK_BACK;
import static androidx.media3.common.Player.COMMAND_SEEK_FORWARD;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;
import androidx.media3.common.Timeline;

import java.util.concurrent.CopyOnWriteArraySet;

public class ControlView extends FrameLayout {

    private static final String TAG = "ControlView";

    public static final int DEFAULT_PROGRESS_UPDATE_INTERVAL_MS = 1000;
    public static final int DEFAULT_SEEK_NUMBER = 1000;

    @Nullable
    private Player player;
    @NonNull
    protected final PlayerEventsHandler playerEventsHandler;
    @NonNull
    protected final ActionHandler actionHandler;
    @NonNull
    protected ProgressAdapter progressAdapter;

    protected ImageButton skipPrevious;
    protected ImageButton fastRewindView;
    protected ImageButton playPauseSwitchView;
    protected ImageButton fastForwardView;
    protected ImageButton skipNext;
    protected ImageButton repeatSwitchView;
    protected ImageButton shuffleSwitchView;
    protected TextView positionView;
    protected SeekBar seekView;
    protected TextView durationView;

    protected int progressUpdateIntervalMs;
    protected int seekNumber;

    protected boolean tracking;
    protected boolean attachedToWindow;

    private final Runnable updateProgressTask = this::updateProgress;

    private final CopyOnWriteArraySet<ProgressUpdateListener> progressUpdateListeners;

    public interface ProgressUpdateListener {
        void onProgressUpdate(long position, long bufferedPosition);
    }

    public ControlView(@NonNull Context context) {
        this(context, null);
    }

    public ControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        playerEventsHandler = getPlayerEventsHandler();
        actionHandler = getActionHandler();
        progressAdapter = new DefaultProgressAdapter();
        progressUpdateListeners=new CopyOnWriteArraySet<>();

        progressUpdateIntervalMs = DEFAULT_PROGRESS_UPDATE_INTERVAL_MS;
        seekNumber = DEFAULT_SEEK_NUMBER;

        LayoutInflater.from(context).inflate(getLayoutResources(), this);

        initView(context, attrs, defStyleAttr);

    }

    @LayoutRes
    private int getLayoutResources() {
        return R.layout.fu_controller_view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        skipPrevious = findViewById(R.id.fu_controller_skip_previous);
        if (skipPrevious != null) {
            skipPrevious.setOnClickListener(actionHandler);
        }

        fastRewindView = findViewById(R.id.fu_controller_fast_rewind);
        if (fastRewindView != null) {
            fastRewindView.setOnClickListener(actionHandler);
        }

        playPauseSwitchView = findViewById(R.id.fu_controller_play_pause_switch);
        if (playPauseSwitchView != null) {
            playPauseSwitchView.setOnClickListener(actionHandler);
        }

        fastForwardView = findViewById(R.id.fu_controller_fast_forward);
        if (fastForwardView != null) {
            fastForwardView.setOnClickListener(actionHandler);
        }

        skipNext = findViewById(R.id.fu_controller_skip_next);
        if (skipNext != null) {
            skipNext.setOnClickListener(actionHandler);
        }

        repeatSwitchView = findViewById(R.id.fu_controller_repeat_switch);
        if (repeatSwitchView != null) {
            repeatSwitchView.setOnClickListener(actionHandler);
        }

        shuffleSwitchView = findViewById(R.id.fu_controller_shuffle_switch);
        if (shuffleSwitchView != null) {
            shuffleSwitchView.setOnClickListener(actionHandler);
        }

        positionView = findViewById(R.id.fu_controller_position);

        seekView = findViewById(R.id.fu_controller_seek);
        if (seekView != null) {
            seekView.setMax(seekNumber);
            seekView.setOnSeekBarChangeListener(actionHandler);
            seekView.setOnTouchListener((View v, MotionEvent event) -> !progressAdapter.isCurrentWindowSeekable());
        }

        durationView = findViewById(R.id.fu_controller_duration);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attachedToWindow = true;
        updateAll();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        attachedToWindow = false;
        removeCallbacks(updateProgressTask);
    }

    @NonNull
    public ProgressAdapter getProgressAdapter() {
        return progressAdapter;
    }

    public void setProgressAdapter(@NonNull ProgressAdapter progressAdapter) {
        this.progressAdapter = progressAdapter;
        this.progressAdapter.setPlayer(getPlayer());
    }

    @NonNull
    protected PlayerEventsHandler getPlayerEventsHandler() {
        return new PlayerEventsHandler();
    }

    @NonNull
    protected ActionHandler getActionHandler() {
        return new ActionHandler();
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(@Nullable Player player) {
        if (this.player == player) {
            return;
        }
        if (this.player != null) {
            this.player.removeListener(playerEventsHandler);
        }

        this.player = player;
        if (this.player != null) {
            player.addListener(playerEventsHandler);
        }
        progressAdapter.setPlayer(player);
        updateAll();
    }

    public void addProgressUpdateListener(ProgressUpdateListener l) {
        progressUpdateListeners.add(l);
    }

    public void removeProgressUpdateListener(ProgressUpdateListener l) {
        progressUpdateListeners.remove(l);
    }

    protected void updateAll() {
        updatePlayPauseView();
        updateProgress();
        updateNavigation();
        updateRepeatView();
        updateShuffleView();
    }

    protected void updatePlayPauseView() {
        if (!attachedToWindow || playPauseSwitchView == null) {
            return;
        }
        if (player == null) {
            setViewEnabled(false, playPauseSwitchView);
            return;
        }
        setViewEnabled(true, playPauseSwitchView);

        updatePlayPauseViewResource(playPauseSwitchView, player.getPlayWhenReady());
    }

    protected void updatePlayPauseViewResource(@NonNull ImageButton imageButton, boolean playWhenReady) {
        if (playWhenReady) {
            imageButton.setImageResource(R.drawable.fu_ic_pause);
        } else {
            imageButton.setImageResource(R.drawable.fu_ic_play);
        }
    }

    private void updateProgress() {
        if (!attachedToWindow) {
            return;
        }
        long position = progressAdapter.getCurrentPosition();
        long duration = progressAdapter.getDuration();
        int bufferedPercent = progressAdapter.getBufferedPercentage();
        long bufferedPosition = progressAdapter.getBufferedPosition();

        for (ProgressUpdateListener listener : progressUpdateListeners) {
            if (listener != null) {
                listener.onProgressUpdate(position, bufferedPosition);
            }
        }

        if (positionView != null && !tracking) {
            positionView.setText(progressAdapter.getPositionText(position));
            positionView.setVisibility(progressAdapter.showPositionViewView() ? VISIBLE : INVISIBLE);
        }

        if (seekView != null) {
            if (duration > 0 && !tracking) {
                long pos = seekNumber * position / duration;
                seekView.setProgress((int) pos);
            }
            if (duration > 0) {
                long pos = seekNumber * bufferedPosition / duration;
                seekView.setSecondaryProgress((int) pos);
            }
            seekView.setVisibility(progressAdapter.showSeekView() ? VISIBLE : INVISIBLE);
        }

        if (durationView != null) {
            durationView.setText(progressAdapter.getPositionText(duration));
            durationView.setVisibility(progressAdapter.showDurationView() ? VISIBLE : INVISIBLE);
        }

        removeCallbacks(updateProgressTask);
        postDelayed(updateProgressTask, progressUpdateIntervalMs);
    }

    protected void updateNavigation() {
        if (!attachedToWindow) {
            return;
        }
        boolean enableSeeking = false;
        boolean enablePrevious = false;
        boolean enableRewind = false;
        boolean enableFastForward = false;
        boolean enableNext = false;
        if (player != null) {
            enableSeeking = player.isCurrentMediaItemSeekable();
            enablePrevious = player.hasPreviousMediaItem();
            enableRewind = player.isCommandAvailable(COMMAND_SEEK_BACK);
            enableFastForward = player.isCommandAvailable(COMMAND_SEEK_FORWARD);
            enableNext = player.hasNextMediaItem();
        }

        setViewEnabled(enablePrevious, skipPrevious);
        setViewEnabled(enableNext, skipNext);
        setViewEnabled(enableFastForward, fastForwardView);
        setViewEnabled(enableRewind, fastRewindView);
        if (seekView != null) {
            seekView.setEnabled(enableSeeking);
        }
    }

    protected void updateRepeatView() {
        if (!attachedToWindow || repeatSwitchView == null) {
            return;
        }
        if (player == null) {
            setViewEnabled(false, repeatSwitchView);
            return;
        }
        setViewEnabled(true, repeatSwitchView);

        repeatSwitchView.setVisibility(View.VISIBLE);

        updateRepeatViewResource(repeatSwitchView, player.getRepeatMode());
    }

    protected void updateRepeatViewResource(@NonNull ImageButton imageButton, int repeatMode) {
        switch (repeatMode) {
            case Player.REPEAT_MODE_ONE:
                imageButton.setImageResource(R.drawable.fu_ic_repeat_one);
                imageButton.setContentDescription("");
                break;
            case Player.REPEAT_MODE_ALL:
                imageButton.setImageResource(R.drawable.fu_ic_repeat_all);
                imageButton.setContentDescription("");
                break;
            default:
                imageButton.setImageResource(R.drawable.fu_ic_repeat_off);
                imageButton.setContentDescription("");
        }
    }

    protected void updateShuffleView() {
        if (!attachedToWindow || shuffleSwitchView == null) {
            return;
        }
        if (player == null) {
            setViewEnabled(false, shuffleSwitchView);
            return;
        }
        setViewEnabled(true, shuffleSwitchView);

        updateShuffleViewResource(shuffleSwitchView, player.getShuffleModeEnabled());
    }

    protected void updateShuffleViewResource(ImageButton imageButton, boolean ended) {
        imageButton.setAlpha(ended ? 1f : 0.3f);
        imageButton.setEnabled(true);
        imageButton.setVisibility(View.VISIBLE);
    }

    protected void setViewEnabled(boolean enabled, View view) {
        if (view == null) {
            return;
        }
        view.setEnabled(enabled);
        view.setAlpha(enabled ? 1f : 0.3f);
        view.setVisibility(VISIBLE);
    }

    protected void setPlayWhenReady(boolean playWhenReady) {
        if (player == null || player.getPlayWhenReady() == playWhenReady) {
            return;
        }
        togglePlayWhenReady();
    }

    protected void togglePlayWhenReady() {
        if (player == null) {
            return;
        }
        if (!player.getPlayWhenReady()) {
            if (player.getPlaybackState() == Player.STATE_ENDED) {
                player.seekTo(0);
            }
            player.setPlayWhenReady(true);
        } else {
            player.setPlayWhenReady(false);
        }
    }

    protected void seekTo(long positionMs) {
        if (positionMs < 0 || player == null) {
            return;
        }
        player.seekTo(positionMs);
    }

    protected void seekToPreviousWindow() {
        if (player == null || !player.hasPreviousMediaItem()) {
            return;
        }
        player.seekToPreviousMediaItem();
    }

    protected void seekToNextWindow() {
        if (player == null || !player.hasNextMediaItem()) {
            return;
        }
        player.seekToNextMediaItem();
    }

    protected void seekBack() {
        if (player == null) {
            return;
        }
        player.seekBack();
    }

    protected void seekForward() {
        if (player == null) {
            return;
        }
        player.seekForward();
    }

    @Player.RepeatMode
    protected int getNextRepeatMode(@Player.RepeatMode int currentRepeatMode) {
        if (currentRepeatMode == Player.REPEAT_MODE_OFF) {
            return Player.REPEAT_MODE_ONE;
        }
        if (currentRepeatMode == Player.REPEAT_MODE_ONE) {
            return Player.REPEAT_MODE_ALL;
        }
        return Player.REPEAT_MODE_OFF;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return dispatchMediaKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    public boolean dispatchMediaKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (player == null || !isHandledMediaKey(keyCode)) {
            return false;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
                seekForward();
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) {
                seekBack();
            } else if (event.getRepeatCount() == 0) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        togglePlayWhenReady();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        setPlayWhenReady(true);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PAUSE:
                        setPlayWhenReady(false);
                        break;
                    default:
                        break;
                }
            }
        }
        return true;
    }

    @SuppressLint("InlinedApi")
    private static boolean isHandledMediaKey(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
                || keyCode == KeyEvent.KEYCODE_MEDIA_REWIND
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY
                || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE
                || keyCode == KeyEvent.KEYCODE_MEDIA_NEXT
                || keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS;
    }

    protected class PlayerEventsHandler implements Player.Listener {
        @Override
        public void onTimelineChanged(@NonNull Timeline timeline, int reason) {
            FuLog.d(TAG, "onTimelineChanged : timeline=" + timeline + ",reason=" + reason);
            updateNavigation();
            updateProgress();
        }

        @Override
        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
            FuLog.d(TAG, "onPlayWhenReadyChanged : playWhenReady=" + playWhenReady + ",reason=" + reason);
            updatePlayPauseView();
            updateProgress();
        }

        @Override
        public void onPlaybackStateChanged(int state) {
            FuLog.d(TAG, "onPlayerStateChanged : state=" + state);
            updatePlayPauseView();
            updateProgress();
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            FuLog.d(TAG, "onRepeatModeChanged : repeatMode=" + repeatMode);
            updateNavigation();
            updateRepeatView();
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            FuLog.d(TAG, "onShuffleModeEnabledChanged : shuffleModeEnabled=" + shuffleModeEnabled);
            updateNavigation();
            updateRepeatView();
            updateShuffleView();
        }

        @Override
        public void onPositionDiscontinuity(@NonNull Player.PositionInfo oldPosition, @NonNull Player.PositionInfo newPosition, int reason) {
            FuLog.d(TAG, "onPositionDiscontinuity : reason=" + reason);
            updateNavigation();
        }
    }

    protected class ActionHandler implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
        private int progress;

        @Override
        public void onClick(View v) {
            if (player == null) {
                return;
            }
            if (playPauseSwitchView == v) {
                togglePlayWhenReady();
            } else if (fastRewindView == v) {
                seekBack();
            } else if (fastForwardView == v) {
                seekForward();
            } else if (repeatSwitchView == v) {
                player.setRepeatMode(getNextRepeatMode(player.getRepeatMode()));
            } else if (shuffleSwitchView == v) {
                if (player.getShuffleModeEnabled()) {
                    player.setShuffleModeEnabled(false);
                } else {
                    player.setShuffleModeEnabled(true);
                }
            } else if (skipPrevious == v) {
                seekToPreviousWindow();
            } else if (skipNext == v) {
                seekToNextWindow();
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser || player == null) {
                return;
            }
            this.progress = progress;
            long duration = player.getDuration();
            long newPosition = (duration * progress) / seekNumber;
            if (positionView != null) {
                positionView.setText(progressAdapter.getPositionText(newPosition));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            tracking = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            tracking = false;
            if (player == null) {
                return;
            }
            long duration = player.getDuration();
            long newPosition = duration * progress / seekNumber;
            seekTo(newPosition);
        }
    }
}
