package com.fulvmei.android.media.ui;

public interface ProgressAdapter extends PlayerHolder {

    boolean isCurrentWindowSeekable();

    boolean isCurrentWindowDynamic();

    boolean isCurrentWindowLive();

    long getCurrentPosition();

    long getDuration();

    long getBufferedPosition();

    int getBufferedPercentage();

    boolean showSeekView();

    boolean showPositionViewView();

    boolean showDurationView();

    CharSequence getPositionText(long position);

}
