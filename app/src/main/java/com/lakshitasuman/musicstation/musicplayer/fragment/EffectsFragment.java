package com.lakshitasuman.musicstation.musicplayer.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


import com.lakshitasuman.musicstation.MainActivity;
import com.lakshitasuman.musicstation.musicplayer.PlayerActivity;
import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.musicplayer.customview.AnalogController;

public class EffectsFragment extends Fragment {
    AnalogController bassController;
    AnalogController reverbController;
    int y = 0;


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_effects, viewGroup, false);
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        bassController = (AnalogController) view.findViewById(R.id.controllerBass);
        reverbController = (AnalogController) view.findViewById(R.id.controller3D);
        bassController.setLabel("");
        reverbController.setLabel("");
        bassController.circlePaint2.setColor(Color.parseColor("#f52b7e"));
        bassController.linePaint.setColor(Color.parseColor("#f52b7e"));
        bassController.invalidate();
        reverbController.circlePaint2.setColor(Color.parseColor("#f52b7e"));
        bassController.linePaint.setColor(Color.parseColor("#f52b7e"));
        reverbController.invalidate();
        if (!MainActivity.isEqualizerReloaded) {
            int i = 0;
            if (PlayerActivity.bassBoost != null) {
                try {
                    i = (PlayerActivity.bassBoost.getRoundedStrength() * 19) / 1000;
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            if (PlayerActivity.presetReverb != null) {
                try {
                    y = (PlayerActivity.presetReverb.getPreset() * 19) / 6;
                } catch (Exception exception2) {
                    exception2.printStackTrace();
                }
            }
            if (i == 0) {
                bassController.setProgress(1);
            } else {
                bassController.setProgress(i);
            }
            if (y == 0) {
                reverbController.setProgress(1);
            } else {
                reverbController.setProgress(y);
            }
        } else {
            int i2 = (MainActivity.bassStrength * 19) / 1000;
            y = (MainActivity.reverbPreset * 19) / 6;
            if (i2 == 0) {
                bassController.setProgress(1);
            } else {
                bassController.setProgress(i2);
            }
            if (y == 0) {
                reverbController.setProgress(1);
            } else {
                reverbController.setProgress(y);
            }
        }
        bassController.setOnProgressChangedListener(i -> {
            MainActivity.bassStrength = (short) ((int) (((float) i) * 52.63158f));
            try {
                PlayerActivity.bassBoost.setStrength(MainActivity.bassStrength);
                MainActivity.equalizerModel.setBassStrength(MainActivity.bassStrength);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        reverbController.setOnProgressChangedListener(i -> {
            MainActivity.reverbPreset = (short) ((i * 6) / 19);
            MainActivity.equalizerModel.setReverbPreset(MainActivity.reverbPreset);
            try {
                PlayerActivity.presetReverb.setPreset(MainActivity.reverbPreset);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            y = i;
        });
        super.onViewCreated(view, bundle);
    }
}
