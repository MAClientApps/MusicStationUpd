package com.lakshitasuman.musicstation.voice_change.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class DBSwipeRefreshLayout extends SwipeRefreshLayout {
    public static final String TAG = "DBSwipeRefreshLayout";
    private OnChildScrollUpListener mScrollListenerNeeded;

    public interface OnChildScrollUpListener {
        boolean canChildScrollUp();
    }

    public DBSwipeRefreshLayout(Context context) {
        super(context);
    }


    public DBSwipeRefreshLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setOnChildScrollUpListener(OnChildScrollUpListener onChildScrollUpListener) {
        mScrollListenerNeeded = onChildScrollUpListener;
    }

    @Override
    public boolean canChildScrollUp() {
        if (mScrollListenerNeeded != null) {
            return mScrollListenerNeeded.canChildScrollUp();
        }
        return super.canChildScrollUp();
    }
}
