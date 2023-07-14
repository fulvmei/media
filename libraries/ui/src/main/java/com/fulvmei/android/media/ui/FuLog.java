package com.fulvmei.android.media.ui;

import android.util.Log;

public class FuLog {

    public static boolean DEBUG = BuildConfig.DEBUG;

    private FuLog() {
    }

    public static int v(String tag, String msg) {
        if (DEBUG) {
            return Log.v(tag, msg);
        }
        return 0;
    }


    public static int v(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            return Log.v(tag, msg, tr);
        }
        return 0;
    }


    public static int d(String tag, String msg) {
        if (DEBUG) {
            return Log.d(tag, msg);
        }
        return 0;
    }


    public static int d(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            return Log.d(tag, msg, tr);
        }
        return 0;
    }


    public static int i(String tag, String msg) {
        if (DEBUG) {
            return Log.i(tag, msg);
        }
        return 0;
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            return Log.i(tag, msg, tr);
        }
        return 0;
    }

    public static int w(String tag, String msg) {
        if (DEBUG) {
            return Log.w(tag, msg);
        }
        return 0;
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            return Log.w(tag, msg, tr);
        }
        return 0;
    }

    public static int w(String tag, Throwable tr) {
        if (DEBUG) {
            return Log.w(tag, tr);
        }
        return 0;
    }

    public static int e(String tag, String msg) {
        if (DEBUG) {
            return Log.e(tag, msg);
        }
        return 0;
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            return Log.e(tag, msg, tr);
        }
        return 0;
    }
}
