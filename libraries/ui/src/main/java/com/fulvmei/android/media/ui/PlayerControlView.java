package com.fulvmei.android.media.ui;

import static androidx.media3.common.Player.COMMAND_SEEK_BACK;
import static androidx.media3.common.Player.COMMAND_SEEK_FORWARD;
import static androidx.media3.common.Player.EVENT_AVAILABLE_COMMANDS_CHANGED;
import static androidx.media3.common.Player.EVENT_IS_PLAYING_CHANGED;
import static androidx.media3.common.Player.EVENT_MEDIA_METADATA_CHANGED;
import static androidx.media3.common.Player.EVENT_PLAYBACK_PARAMETERS_CHANGED;
import static androidx.media3.common.Player.EVENT_PLAYBACK_STATE_CHANGED;
import static androidx.media3.common.Player.EVENT_PLAY_WHEN_READY_CHANGED;
import static androidx.media3.common.Player.EVENT_POSITION_DISCONTINUITY;
import static androidx.media3.common.Player.EVENT_REPEAT_MODE_CHANGED;
import static androidx.media3.common.Player.EVENT_SEEK_BACK_INCREMENT_CHANGED;
import static androidx.media3.common.Player.EVENT_SEEK_FORWARD_INCREMENT_CHANGED;
import static androidx.media3.common.Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED;
import static androidx.media3.common.Player.EVENT_TIMELINE_CHANGED;
import static androidx.media3.common.Player.EVENT_VOLUME_CHANGED;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;

import com.fulvmei.android.media.common.PlayerHolder;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

public class PlayerControlView extends PlayerHolderView {

    private static final String TAG = "ControlView";

    public static final int DEFAULT_PROGRESS_UPDATE_INTERVAL_MS = 1000;

    @NonNull
    protected final ControlPlayerListener controlPlayerListener;
    @NonNull
    protected final ProgressListener progressListener;
    @NonNull
    protected final ActionHandler actionHandler;
    @NonNull
    protected ProgressAdapter progressAdapter;
    @NonNull
    protected SpeedAdapter speedAdapter;

    protected TextView titleView;
    protected ImageButton skipPrevious;
    protected ImageButton fastRewindView;
    protected ImageButton playPauseSwitchView;
    protected ImageButton fastForwardView;
    protected ImageButton skipNext;
    protected ImageButton volumeSwitchView;
    protected ImageButton repeatSwitchView;
    protected ImageButton shuffleSwitchView;
    protected TextView speedView;
    protected TextView positionView;
    protected SeekBar seekView;
    protected TextView durationView;

    protected boolean volumeSwitchEnabled = true;

    protected int progressUpdateIntervalMs;

    protected boolean tracking;
    protected boolean attachedToWindow;

    private final Runnable updateProgressTask = this::updateProgress;

    private final CopyOnWriteArraySet<ProgressUpdateListener> progressUpdateListeners;

    public interface ProgressUpdateListener {
        void onProgressUpdate(long position, long bufferedPosition);
    }

    public PlayerControlView(@NonNull Context context) {
        this(context, null);
    }

    public PlayerControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        controlPlayerListener = getControlPlayerListener();
        progressListener = getProgressListener();
        actionHandler = getActionHandler();
        progressAdapter = new DefaultProgressAdapter();
        speedAdapter = new DefaultSpeedAdapter();
        progressUpdateListeners = new CopyOnWriteArraySet<>();

        progressUpdateIntervalMs = DEFAULT_PROGRESS_UPDATE_INTERVAL_MS;

        LayoutInflater.from(context).inflate(getLayoutResources(), this);

        initView(context, attrs, defStyleAttr);

    }

    @LayoutRes
    protected int getLayoutResources() {
        return R.layout.fu_player_control_view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        titleView = findViewById(R.id.fu_player_control_title);

        skipPrevious = findViewById(R.id.fu_player_control_previous);
        if (skipPrevious != null) {
            skipPrevious.setOnClickListener(actionHandler);
        }

        fastRewindView = findViewById(R.id.fu_player_control_back);
        if (fastRewindView != null) {
            fastRewindView.setOnClickListener(actionHandler);
        }

        playPauseSwitchView = findViewById(R.id.fu_player_control_play_pause);
        if (playPauseSwitchView != null) {
            playPauseSwitchView.setOnClickListener(actionHandler);
        }

        fastForwardView = findViewById(R.id.fu_player_control_forward);
        if (fastForwardView != null) {
            fastForwardView.setOnClickListener(actionHandler);
        }

        skipNext = findViewById(R.id.fu_player_control_next);
        if (skipNext != null) {
            skipNext.setOnClickListener(actionHandler);
        }

        volumeSwitchView = findViewById(R.id.fu_player_control_volume);
        if (volumeSwitchView != null) {
            volumeSwitchView.setOnClickListener(actionHandler);
        }

        repeatSwitchView = findViewById(R.id.fu_player_control_repeat);
        if (repeatSwitchView != null) {
            repeatSwitchView.setOnClickListener(actionHandler);
        }

        shuffleSwitchView = findViewById(R.id.fu_player_control_shuffle);
        if (shuffleSwitchView != null) {
            shuffleSwitchView.setOnClickListener(actionHandler);
        }

        speedView = findViewById(R.id.fu_player_control_speed);
        if (speedView != null) {
            speedView.setOnClickListener(actionHandler);
        }

        positionView = findViewById(R.id.fu_player_control_position);

        seekView = findViewById(R.id.fu_player_control_seek);
        if (seekView != null) {
            seekView.setOnSeekBarChangeListener(actionHandler);
            seekView.setOnTouchListener((View v, MotionEvent event) -> !progressAdapter.isCurrentWindowSeekable());
        }

        durationView = findViewById(R.id.fu_player_control_duration);
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
    public SpeedAdapter getSpeedAdapter() {
        return speedAdapter;
    }

    public void setSpeedAdapter(@NonNull SpeedAdapter speedAdapter) {
        this.speedAdapter = speedAdapter;
    }

    @NonNull
    protected ControlPlayerListener getControlPlayerListener() {
        return new ControlPlayerListener();
    }

    @NonNull
    public ProgressListener getProgressListener() {
        return new ProgressListener();
    }

    @NonNull
    protected ActionHandler getActionHandler() {
        return new ActionHandler();
    }

    @Override
    protected void onPlayerAttached(@NonNull Player player) {
        super.onPlayerAttached(player);
        player.addListener(controlPlayerListener);
        progressAdapter.setPlayer(null);
        updateAll();
    }

    @Override
    protected void onPlayerDetached(@NonNull Player player) {
        super.onPlayerDetached(player);
        player.removeListener(controlPlayerListener);
        progressAdapter.setPlayer(player);
        updateAll();
    }

    public void addProgressUpdateListener(ProgressUpdateListener l) {
        progressUpdateListeners.add(l);
    }

    public void removeProgressUpdateListener(ProgressUpdateListener l) {
        progressUpdateListeners.remove(l);
    }

    public boolean isVolumeSwitchEnabled() {
        return volumeSwitchEnabled;
    }

    public void setVolumeSwitchEnabled(boolean volumeSwitchEnabled) {
        if (this.volumeSwitchEnabled == volumeSwitchEnabled) {
            return;
        }
        this.volumeSwitchEnabled = volumeSwitchEnabled;
        updateVolumeView();
    }

    protected void updateAll() {
        updatePlayPauseView();
        updateNavigation();
        updateRepeatView();
        updateShuffleView();
        updateSpeedView();
        updateTimeline();
        updateMediaMetadata();
        updateVolumeView();
    }

    protected void updateMediaMetadata() {
        if (!attachedToWindow || titleView == null || player == null) {
            return;
        }
        titleView.setText(player.getMediaMetadata().title);
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

    protected void updateTimeline() {
        if (player == null) {
            return;
        }
        long duration = player.getDuration();

        if (positionView != null && !tracking) {
            positionView.setVisibility(progressAdapter.showPositionViewView() ? VISIBLE : INVISIBLE);
        }
        if (seekView != null) {
            seekView.setMax((int) duration);
            seekView.setVisibility(progressAdapter.showSeekView() ? VISIBLE : INVISIBLE);
        }
        if (durationView != null) {
            durationView.setText(progressAdapter.getPositionText(duration));
            durationView.setVisibility(progressAdapter.showDurationView() ? VISIBLE : INVISIBLE);
        }
        updateProgress();
    }

    protected void updateProgress() {
        if (!attachedToWindow) {
            return;
        }
        long position = progressAdapter.getCurrentPosition();
        long duration = progressAdapter.getDuration();
        long bufferedPosition = progressAdapter.getBufferedPosition();

        if (positionView != null && !tracking) {
            positionView.setText(progressAdapter.getPositionText(position));
        }

        if (seekView != null) {
            if (duration > 0 && !tracking) {
                seekView.setProgress((int) position);
            }
            if (duration > 0) {
                seekView.setSecondaryProgress((int) position);
            }
        }

        onProgressUpdated(position, bufferedPosition);

        removeCallbacks(updateProgressTask);
        postDelayed(updateProgressTask, progressUpdateIntervalMs);
    }

    protected void onProgressUpdated(long position, long bufferedPosition) {
        for (ProgressUpdateListener listener : progressUpdateListeners) {
            if (listener != null) {
                listener.onProgressUpdate(position, bufferedPosition);
            }
        }
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

    protected void updateVolumeView() {
        if (!attachedToWindow || volumeSwitchView == null) {
            return;
        }
        if (!volumeSwitchEnabled) {
            volumeSwitchView.setVisibility(GONE);
            return;
        }
        volumeSwitchView.setVisibility(VISIBLE);
        if (player == null) {
            setViewEnabled(false, volumeSwitchView);
            return;
        }
        setViewEnabled(true, volumeSwitchView);
        updateVolumeViewResource(volumeSwitchView, player.getVolume());
    }

    protected void updateVolumeViewResource(@NonNull ImageButton imageButton, float volume) {
        if (volume > 0.0f) {
            imageButton.setImageResource(R.drawable.fu_ic_volume_up);
        } else {
            imageButton.setImageResource(R.drawable.fu_ic_volume_off);
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

    protected void updateSpeedView() {
        if (!attachedToWindow || speedView == null || player == null) {
            return;
        }

        float speed = player.getPlaybackParameters().speed;
        speedView.setText(MessageFormat.format("{0}X", speed));

        boolean speedHide = player != null && player.isCurrentMediaItemDynamic();
        speedView.setVisibility(speedHide ? GONE : VISIBLE);
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

    protected class ControlPlayerListener implements Player.Listener {

        @Override
        public void onEvents(@NonNull Player player, Player.Events events) {
            if (events.containsAny(
                    EVENT_PLAYBACK_STATE_CHANGED,
                    EVENT_PLAY_WHEN_READY_CHANGED,
                    EVENT_AVAILABLE_COMMANDS_CHANGED)) {
                updatePlayPauseView();
            }
            if (events.containsAny(
                    EVENT_PLAYBACK_STATE_CHANGED,
                    EVENT_PLAY_WHEN_READY_CHANGED,
                    EVENT_IS_PLAYING_CHANGED,
                    EVENT_AVAILABLE_COMMANDS_CHANGED)) {
                updateProgress();
            }
            if (events.containsAny(
                    EVENT_REPEAT_MODE_CHANGED,
                    EVENT_AVAILABLE_COMMANDS_CHANGED)) {
                updateRepeatView();
            }
            if (events.containsAny(
                    EVENT_SHUFFLE_MODE_ENABLED_CHANGED,
                    EVENT_AVAILABLE_COMMANDS_CHANGED)) {
                updateShuffleView();
            }
            if (events.containsAny(
                    EVENT_REPEAT_MODE_CHANGED,
                    EVENT_SHUFFLE_MODE_ENABLED_CHANGED,
                    EVENT_POSITION_DISCONTINUITY,
                    EVENT_TIMELINE_CHANGED,
                    EVENT_SEEK_BACK_INCREMENT_CHANGED,
                    EVENT_SEEK_FORWARD_INCREMENT_CHANGED,
                    EVENT_AVAILABLE_COMMANDS_CHANGED)) {
                updateNavigation();
            }
            if (events.containsAny(
                    EVENT_POSITION_DISCONTINUITY,
                    EVENT_TIMELINE_CHANGED,
                    EVENT_AVAILABLE_COMMANDS_CHANGED)) {
                updateTimeline();
            }
            if (events.containsAny(
                    EVENT_PLAYBACK_PARAMETERS_CHANGED,
                    EVENT_AVAILABLE_COMMANDS_CHANGED)) {
                updateSpeedView();
            }
            if (events.containsAny(
                    EVENT_MEDIA_METADATA_CHANGED)) {
                updateMediaMetadata();
            }
            if (events.containsAny(
                    EVENT_VOLUME_CHANGED)) {
                updateVolumeView();
            }
        }
    }

    protected class ActionHandler implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

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
            } else if (volumeSwitchView == v) {
                if (player.getVolume() > 0) {
                    player.setVolume(0.0f);
                } else {
                    player.setVolume(1.0f);
                }
            } else if (repeatSwitchView == v) {
                player.setRepeatMode(getNextRepeatMode(player.getRepeatMode()));
            } else if (shuffleSwitchView == v) {
                player.setShuffleModeEnabled(!player.getShuffleModeEnabled());
            } else if (skipPrevious == v) {
                seekToPreviousWindow();
            } else if (skipNext == v) {
                seekToNextWindow();
            } else if (speedView == v) {
                onSpeedViewClick(v);
            }
        }

        public void onSpeedViewClick(View v) {
            if (player == null) {
                return;
            }
            PopupMenu popupMenu = new PopupMenu(getContext(), v);
            popupMenu.setOnMenuItemClickListener(item -> {
                List<SpeedAdapter.Speed> speedList = speedAdapter.getSpeedList();
                for (int i = 0; i < speedList.size(); i++) {
                    SpeedAdapter.Speed speed = speedList.get(i);
                    if (Objects.equals(speed.name, item.getTitle())) {
                        onSpeedItemClick(speed);
                        break;
                    }
                }
                return true;
            });
            speedAdapter.getSpeedList();
            List<SpeedAdapter.Speed> speedList = speedAdapter.getSpeedList();
            for (int i = 0; i < speedList.size(); i++) {
                SpeedAdapter.Speed speed = speedList.get(i);
                MenuItem menuItem = popupMenu.getMenu().add(speed.name);
                menuItem.setCheckable(true);
                if (Objects.equals(speedAdapter.getSpeed(player.getPlaybackParameters().speed).name, speed.name)) {
                    menuItem.setChecked(true);
                }
            }
            popupMenu.show();
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser || player == null) {
                return;
            }
            if (positionView != null) {
                positionView.setText(progressAdapter.getPositionText(progress));
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
            seekTo(seekView.getProgress());
        }

        public void onSpeedItemClick(@NonNull SpeedAdapter.Speed speed) {
            if (player == null) {
                return;
            }
            player.setPlaybackSpeed(speed.value);
        }
    }

    protected class ProgressListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        public void onStartTrackingTouch() {
            tracking = true;
        }

        public void onProgressChanged(int progress, boolean fromUser) {
            if (!fromUser || player == null) {
                return;
            }
            if (positionView != null) {
                positionView.setText(progressAdapter.getPositionText(progress));
            }
        }

        public void onStopTrackingTouch() {
            tracking = false;
            if (player == null) {
                return;
            }
            seekTo(seekView.getProgress());
        }
    }
}
