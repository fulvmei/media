package com.fulvmei.android.media.ui;

import android.content.Context;

import android.util.AttributeSet;
import android.view.MotionEvent;

public class DefaultControlView extends ControlView {
    private static final String TAG = "DefaultControlView";

    public DefaultControlView(Context context) {
        this(context, null);
    }

    public DefaultControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        super.initView(context, attrs, defStyleAttr);
        if (mContainerView != null) {
            mContainerView.setVisibility(GONE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() != MotionEvent.ACTION_DOWN) {
            return false;
        }
        return performClick();
    }

    @Override
    public boolean performClick() {
        super.performClick();
        if (!isInShowState()) {
            return false;
        }
        if (isShowing()) {
            hide();
        } else {
            show();
        }
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        if (!isInShowState()) {
            return false;
        }
        if (!isShowing()) {
            show();
        }
        return true;
    }
}
