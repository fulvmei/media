package com.fulvmei.android.media.ui;

import static androidx.media3.common.Player.COMMAND_SET_VIDEO_SURFACE;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;
import androidx.media3.common.VideoSize;
import androidx.media3.common.util.Assertions;
import androidx.media3.exoplayer.video.VideoDecoderGLSurfaceView;
import androidx.media3.exoplayer.video.spherical.SphericalGLSurfaceView;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;


public class PlayerView extends FrameLayout implements PlayerHolder {

    public static final String TAG = "PlayerView";

    public static final int SURFACE_TYPE_NONE = 0;
    public static final int SURFACE_TYPE_SURFACE_VIEW = 1;
    public static final int SURFACE_TYPE_TEXTURE_VIEW = 2;
    public static final int SURFACE_TYPE_SPHERICAL_GL_SURFACE_VIEW = 3;
    public static final int SURFACE_TYPE_VIDEO_DECODER_GL_SURFACE_VIEW = 4;

    public static final int RESIZE_MODE_FIT = AspectRatioFrameLayout.RESIZE_MODE_FIT;
    public static final int RESIZE_MODE_FIXED_WIDTH = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH;
    public static final int RESIZE_MODE_FIXED_HEIGHT = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT;
    public static final int RESIZE_MODE_FILL = AspectRatioFrameLayout.RESIZE_MODE_FILL;
    public static final int RESIZE_MODE_ZOOM = AspectRatioFrameLayout.RESIZE_MODE_ZOOM;

    private ImageView mUnderlayView;
    private AspectRatioFrameLayout mSurfaceContainer;
    private View mSurfaceView;
    private ImageView shutterView;

    private Player mPlayer;
    private PlayerEventsHandler playerEventsHandler;

    private int mTextureViewRotation;
    private int mSurfaceType = -1;
    private int mResizeMode = -1;

    public PlayerView(Context context) {
        this(context, null);
    }

    public PlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            setBackgroundResource(R.color.fu_play_view_edit_mode_bg);
            return;
        }

        int shutterColor = Color.BLACK;
        int surfaceType = SURFACE_TYPE_SURFACE_VIEW;
        int resizeMode = RESIZE_MODE_FIT;

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FuPlayerView, 0, 0);
            try {
                surfaceType = a.getInt(R.styleable.FuPlayerView_surface_type, SURFACE_TYPE_SURFACE_VIEW);
                resizeMode = a.getInt(R.styleable.FuPlayerView_resize_mode, RESIZE_MODE_FIT);
                shutterColor = a.getColor(R.styleable.FuPlayerView_shutter_background_color, shutterColor);
            } finally {
                a.recycle();
            }
        }
        LayoutInflater.from(context).inflate(R.layout.default_player_view, this);

        mUnderlayView = findViewById(R.id.underlay);
        mSurfaceContainer = findViewById(R.id.surface_container);
        shutterView = findViewById(R.id.view_shutter);

        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

        playerEventsHandler = initPlayerEventsHandler();

        setShutterBackgroundColor(shutterColor);

        setSurfaceViewType(surfaceType);

        setResizeMode(resizeMode);

        updateScreenOn();
    }

    protected PlayerEventsHandler initPlayerEventsHandler() {
        return new PlayerEventsHandler();
    }

    private void updateScreenOn() {
        boolean keepScreenOn = false;
        if (mPlayer != null && mPlayer.getPlaybackState() != Player.STATE_IDLE
                && mPlayer.getPlaybackState() != Player.STATE_ENDED && mPlayer.getPlayWhenReady()) {
            keepScreenOn = true;
        }
        setKeepScreenOn(keepScreenOn);
    }

    public ImageView getUnderlayView() {
        return mUnderlayView;
    }

    public void setShutterBackgroundColor(int color) {
        if (shutterView != null) {
            shutterView.setBackgroundColor(color);
            FuLog.d(TAG, "setShutterBackgroundColor : color=" + color);
        }
    }

    public void setSurfaceViewType(int surfaceType) {
        if (mSurfaceType == surfaceType
                || (surfaceType != SURFACE_TYPE_NONE
                && surfaceType != SURFACE_TYPE_SURFACE_VIEW
                && surfaceType != SURFACE_TYPE_TEXTURE_VIEW
                && surfaceType != SURFACE_TYPE_SPHERICAL_GL_SURFACE_VIEW
                && surfaceType != SURFACE_TYPE_VIDEO_DECODER_GL_SURFACE_VIEW)) {
            return;
        }
        if (mSurfaceView != null && mSurfaceView.getParent() != null && mSurfaceView.getParent() instanceof AspectRatioFrameLayout) {
            mSurfaceContainer.removeView(mSurfaceView);
        }

        mSurfaceType = surfaceType;
        switch (surfaceType) {
            case SURFACE_TYPE_NONE:
                mSurfaceView = null;
                break;
            case SURFACE_TYPE_SURFACE_VIEW:
                mSurfaceView = new SurfaceView(getContext());
                break;
            case SURFACE_TYPE_TEXTURE_VIEW:
                mSurfaceView = new TextureView(getContext());
                break;
            case SURFACE_TYPE_SPHERICAL_GL_SURFACE_VIEW:
                SphericalGLSurfaceView sphericalGLSurfaceView = new SphericalGLSurfaceView(getContext());
//                sphericalGLSurfaceView.setSingleTapListener(componentListener);
                mSurfaceView = sphericalGLSurfaceView;
                break;
            case SURFACE_TYPE_VIDEO_DECODER_GL_SURFACE_VIEW:
                mSurfaceView = new VideoDecoderGLSurfaceView(getContext());
                break;
        }

        if (mSurfaceView != null) {
            ViewGroup.LayoutParams params =
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mSurfaceView.setLayoutParams(params);
            mSurfaceContainer.addView(mSurfaceView, 0);
        }

        if (mPlayer != null) {
            if (mSurfaceView instanceof TextureView) {
                mPlayer.setVideoTextureView((TextureView) mSurfaceView);
            } else if (mSurfaceView instanceof SurfaceView) {
                mPlayer.setVideoSurfaceView((SurfaceView) mSurfaceView);
            }
        }

        FuLog.i(TAG, "setSurfaceView : surfaceType : " + (mSurfaceView == null ? "None" : mSurfaceView.getClass().getSimpleName()));

    }

    public int getSurfaceType() {
        return mSurfaceType;
    }

    public void setResizeMode(int resizeMode) {
        if (mResizeMode == resizeMode
                || (resizeMode != RESIZE_MODE_FIT
                && resizeMode != RESIZE_MODE_FIXED_WIDTH
                && resizeMode != RESIZE_MODE_FIXED_HEIGHT
                && resizeMode != RESIZE_MODE_FILL
                && resizeMode != RESIZE_MODE_ZOOM)) {
            return;
        }
        mResizeMode = resizeMode;
        mSurfaceContainer.setResizeMode(resizeMode);

        FuLog.i(TAG, "setResizeMode : resizeMode : " + resizeMode);
    }

    public int getResizeMode() {
        return mResizeMode;
    }

    /**
     * Switches the view targeted by a given {@link Player}.
     *
     * @param player        The player whose target view is being switched.
     * @param oldPlayerView The old view to detach from the player.
     * @param newPlayerView The new view to attach to the player.
     */
    public static void switchTargetView(
            Player player, @Nullable PlayerView oldPlayerView, @Nullable PlayerView newPlayerView) {
        if (oldPlayerView == newPlayerView) {
            return;
        }
        // We attach the new view before detaching the old one because this ordering allows the player
        // to swap directly from one surface to another, without transitioning through a state where no
        // surface is attached. This is significantly more efficient and achieves a more seamless
        // transition when using platform provided video decoders.
        if (newPlayerView != null) {
            newPlayerView.setPlayer(player);
        }
        if (oldPlayerView != null) {
            oldPlayerView.setPlayer(null);
        }
    }

    /**
     * Returns the player currently set on this view, or null if no player is set.
     */
    @Override
    @Nullable
    public Player getPlayer() {
        return mPlayer;
    }

    /**
     * Set the {@link Player} to use.
     *
     * <p>To transition a {@link Player} from targeting one view to another, it's recommended to use
     * {@link #switchTargetView(Player, PlayerView, PlayerView)} rather than this method. If you do
     * wish to use this method directly, be sure to attach the player to the new view <em>before</em>
     * calling {@code setPlayer(null)} to detach it from the old one. This ordering is significantly
     * more efficient and may allow for more seamless transitions.
     *
     * @param player The {@link Player} to use, or {@code null} to detach the current player. Only
     *               players which are accessed on the main thread are supported ({@code
     *               player.getApplicationLooper() == Looper.getMainLooper()}).
     */
    @Override
    public void setPlayer(@Nullable Player player) {
        Assertions.checkState(Looper.myLooper() == Looper.getMainLooper());
        Assertions.checkArgument(
                player == null || player.getApplicationLooper() == Looper.getMainLooper());
        if (mPlayer == player) {
            return;
        }
        if (mPlayer != null) {
            mPlayer.removeListener(playerEventsHandler);
            if (mPlayer.isCommandAvailable(COMMAND_SET_VIDEO_SURFACE)) {
                if (mSurfaceView instanceof TextureView) {
                    mPlayer.clearVideoTextureView((TextureView) mSurfaceView);
                } else if (mSurfaceView instanceof SurfaceView) {
                    mPlayer.clearVideoSurfaceView((SurfaceView) mSurfaceView);
                }
            }
        }

        mPlayer = player;
        if (player != null) {
            if (player.isCommandAvailable(COMMAND_SET_VIDEO_SURFACE)) {
                if (mSurfaceView instanceof TextureView) {
                    player.setVideoTextureView((TextureView) mSurfaceView);
                } else if (mSurfaceView instanceof SurfaceView) {
                    player.setVideoSurfaceView((SurfaceView) mSurfaceView);
                }
            }
            player.addListener(playerEventsHandler);
        }
        updateScreenOn();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (mSurfaceView instanceof SurfaceView) {
            mSurfaceView.setVisibility(visibility);
        }
    }

    /**
     * Should be called when the player is visible to the user and if {@code surface_type} is {@code
     * spherical_view}. It is the counterpart to {@link #onPause()}.
     *
     * <p>This method should typically be called in {@link Activity# onStart()}, or {@link
     * Activity# onResume()} for API versions &lt;= 23.
     */
    public void onResume() {
        if (mSurfaceView instanceof SphericalGLSurfaceView) {
            ((SphericalGLSurfaceView) mSurfaceView).onResume();
        }
    }

    /**
     * Should be called when the player is no longer visible to the user and if {@code surface_type}
     * is {@code spherical_view}. It is the counterpart to {@link #onResume()}.
     *
     * <p>This method should typically be called in {@link Activity# onStop()}, or {@link
     * Activity# onPause()} for API versions &lt;= 23.
     */
    public void onPause() {
        if (mSurfaceView instanceof SphericalGLSurfaceView) {
            ((SphericalGLSurfaceView) mSurfaceView).onPause();
        }
    }

    /**
     * Applies a texture rotation to a {@link TextureView}.
     */
    private static void applyTextureViewRotation(TextureView textureView, int textureViewRotation) {
        float textureViewWidth = textureView.getWidth();
        float textureViewHeight = textureView.getHeight();
        if (textureViewWidth == 0 || textureViewHeight == 0 || textureViewRotation == 0) {
            textureView.setTransform(null);
        } else {
            Matrix transformMatrix = new Matrix();
            float pivotX = textureViewWidth / 2;
            float pivotY = textureViewHeight / 2;
            transformMatrix.postRotate(textureViewRotation, pivotX, pivotY);

            // After rotation, scale the rotated texture to fit the TextureView size.
            RectF originalTextureRect = new RectF(0, 0, textureViewWidth, textureViewHeight);
            RectF rotatedTextureRect = new RectF();
            transformMatrix.mapRect(rotatedTextureRect, originalTextureRect);
            transformMatrix.postScale(
                    textureViewWidth / rotatedTextureRect.width(),
                    textureViewHeight / rotatedTextureRect.height(),
                    pivotX,
                    pivotY);
            textureView.setTransform(transformMatrix);
        }
    }

    protected class PlayerEventsHandler implements Player.Listener, OnLayoutChangeListener {

        @Override
        public void onPlaybackStateChanged(int state) {
            FuLog.d(TAG, "onPlayerStateChanged : state=" + state);
            updateScreenOn();
        }

        @Override
        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
            FuLog.d(TAG, "onPlayWhenReadyChanged : playWhenReady=" + playWhenReady + " ,reason=" + reason);
            updateScreenOn();
        }

        @Override
        public void onVideoSizeChanged(VideoSize videoSize) {
            int width = videoSize.width;
            int height = videoSize.height;
            int unappliedRotationDegrees = videoSize.unappliedRotationDegrees;
            float pixelWidthHeightRatio = videoSize.pixelWidthHeightRatio;
            FuLog.d(TAG, "onVideoSizeChanged : width=" + width + ",height=" + height + ",unappliedRotationDegrees=" + unappliedRotationDegrees + ",pixelWidthHeightRatio=" + pixelWidthHeightRatio);
            float videoAspectRatio =
                    (height == 0 || width == 0) ? 1 : (width * pixelWidthHeightRatio) / height;

            if (mSurfaceView instanceof TextureView) {
                // Try to apply rotation transformation when our surface is a TextureView.
                if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                    // We will apply a rotation 90/270 degree to the output texture of the TextureView.
                    // In this case, the output video's width and height will be swapped.
                    videoAspectRatio = 1 / videoAspectRatio;
                }
                if (mTextureViewRotation != 0) {
                    mSurfaceView.removeOnLayoutChangeListener(this);
                }
                mTextureViewRotation = unappliedRotationDegrees;
                if (mTextureViewRotation != 0) {
                    // The texture view's dimensions might be changed after layout step.
                    // So add an OnLayoutChangeListener to apply rotation after layout step.
                    mSurfaceView.addOnLayoutChangeListener(this);
                }
                applyTextureViewRotation((TextureView) mSurfaceView, mTextureViewRotation);
            }

            if (mSurfaceContainer != null && mSurfaceView != null) {
                mSurfaceContainer.setAspectRatio(
                        mSurfaceView instanceof SphericalGLSurfaceView ? 0 : videoAspectRatio);
            }
        }

        @Override
        public void onRenderedFirstFrame() {
            FuLog.d(TAG, "onRenderedFirstFrame");
            if (shutterView != null && shutterView.getVisibility() == VISIBLE) {
                shutterView.setVisibility(INVISIBLE);
                shutterView.setImageBitmap(null);
            }
        }

        @Override
        public void onSurfaceSizeChanged(int width, int height) {
            FuLog.d(TAG, "onSurfaceSizeChanged : width=" + width + ",height=" + height);
            if (width == 0 || height == 0) {
                if (shutterView != null) {
                    shutterView.setVisibility(VISIBLE);
                }
            }
        }

        @Override
        public void onPositionDiscontinuity(@NonNull Player.PositionInfo oldPosition, @NonNull Player.PositionInfo newPosition, int reason) {
            FuLog.d(TAG, "onPositionDiscontinuity : oldPosition=" + oldPosition + ",newPosition=" + newPosition + ",reason=" + reason);
            if (mSurfaceView instanceof TextureView) {
                TextureView surfaceView = (TextureView) mSurfaceView;
                shutterView.setImageBitmap(surfaceView.getBitmap());
            }
        }

        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            applyTextureViewRotation((TextureView) v, mTextureViewRotation);
        }
    }
}
