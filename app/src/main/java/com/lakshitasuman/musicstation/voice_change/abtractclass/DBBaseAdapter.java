package com.lakshitasuman.musicstation.voice_change.abtractclass;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;


import com.lakshitasuman.musicstation.voice_change.utils.ResolutionUtils;

import java.util.ArrayList;

public abstract class DBBaseAdapter extends BaseAdapter {
    public static final long ANIM_DEFAULT_MIN_SPEED = 300;
    public static final long ANIM_DEFAULT_SPEED = 800;
    public static final String TAG = "DBBaseAdapter";
    private Interpolator interpolator;
    private boolean isAnimate;
    public Context mContext;
    public ArrayList<? extends Object> mListObjects;
    private SparseBooleanArray mPositionsMapper;
    private int screenHeight;
    private int screenWidth;


    public abstract View getAnimatedView(int i, View view, ViewGroup viewGroup);

    @Override
    public long getItemId(int i) {
        return (long) i;
    }



    public abstract View getNormalView(int i, View view, ViewGroup viewGroup);



    public DBBaseAdapter(Activity activity, ArrayList<? extends Object> mListObjects) {
        mContext = activity;
        this.mListObjects = mListObjects;
        mPositionsMapper = new SparseBooleanArray(mListObjects.size());
        int[] deviceResolution = ResolutionUtils.getDeviceResolution(activity);
        if (deviceResolution != null) {
            screenWidth = deviceResolution[0];
            screenHeight = deviceResolution[1];
        }
    }

    @Override
    public int getCount() {
        if (mListObjects != null) {
            return mListObjects.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if (mListObjects == null || mListObjects.size() <= 0) {
            return null;
        }
        int size = mListObjects.size();
        if (i <= 0 || i >= size) {
            return null;
        }
        return mListObjects.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (isAnimate) {
            return getAnimatedView(i, view, viewGroup);
        }
        return getNormalView(i, view, viewGroup);
    }



    public ArrayList<? extends Object> getListObjects() {
        return mListObjects;
    }


    public void setListObjects(ArrayList<? extends Object> arrayList, boolean bool) {
        if (arrayList != null) {

            if (mListObjects != null && bool) {
                mListObjects.clear();
                mListObjects = null;
            }
            mPositionsMapper = null;
            mPositionsMapper = new SparseBooleanArray(arrayList.size());
            mListObjects = arrayList;
            notifyDataSetChanged();
        }
    }


    public Interpolator getInterpolator() {
        return interpolator;
    }


    public void setInterpolator(Interpolator interpolator2) {
        interpolator = interpolator2;
    }


    public boolean isAnimate() {
        return isAnimate;
    }

    public void setAnimate(boolean animate) {
        isAnimate = animate;
        notifyDataSetChanged();
    }





    public void onDestroy(boolean b) {

        if (mListObjects != null) {
            mListObjects.clear();
            if (b) {
                mListObjects = null;
            }
        }
    }


    public void addPositionMapper(int i, boolean bool) {
        mPositionsMapper.put(i, bool);
    }

    public boolean checkPositionMapper(int i) {
        return mPositionsMapper.get(i);
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }
}
