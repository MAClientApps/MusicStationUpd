package com.lakshitasuman.musicstation.radio.adapters;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.lakshitasuman.musicstation.radio.AlbumRecordingFragment;
import com.lakshitasuman.musicstation.radio.AlbumTrackFragment;

public class AlbumTabLayoutAdapter extends FragmentPagerAdapter {
    Context mContext;
    int mTotalTabs;

    public AlbumTabLayoutAdapter(Context context , FragmentManager fragmentManager , int totalTabs) {
        super(fragmentManager);
        mContext = context;
        mTotalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.d("asasas" , position + "");
        switch (position) {
            case 0:
                return new AlbumTrackFragment();
            case 1:
                return new AlbumRecordingFragment();

                default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return mTotalTabs;
    }
}
