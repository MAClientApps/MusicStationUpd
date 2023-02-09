package com.lakshitasuman.musicstation.musicplayer.adapter;

import static android.media.audiofx.PresetReverb.PRESET_LARGEHALL;
import static android.media.audiofx.PresetReverb.PRESET_LARGEROOM;
import static android.media.audiofx.PresetReverb.PRESET_MEDIUMHALL;
import static android.media.audiofx.PresetReverb.PRESET_MEDIUMROOM;
import static android.media.audiofx.PresetReverb.PRESET_PLATE;
import static android.media.audiofx.PresetReverb.PRESET_SMALLROOM;

import android.content.Context;
import android.media.audiofx.PresetReverb;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.lakshitasuman.musicstation.musicplayer.PlayerActivity;
import com.lakshitasuman.musicstation.R;

public class EnvironmentEffectAdpater extends RecyclerView.Adapter<EnvironmentEffectAdpater.ViewHolder> {
    public static int isImagePosition;
    Context context;
    ArrayList<String> effects;

    public EnvironmentEffectAdpater(Context context2, ArrayList<String> arrayList) {
        context = context2;
        effects = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_environment_recycler, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.environmentEffect.setText(effects.get(i));
        if (i == isImagePosition) {
            viewHolder.imgView.setImageResource(R.drawable.presed2);
        } else {
            viewHolder.imgView.setImageResource(R.drawable.unpresed2);
        }
        viewHolder.itemView.setOnClickListener(view -> {
            isImagePosition = i;
            if (i == 0) {
                PlayerActivity.presetReverb.setEnabled(false);
                PlayerActivity.mMediaPlayer.setAuxEffectSendLevel(1.0f);
            } else {
                PlayerActivity.presetReverb = new PresetReverb(1, PlayerActivity.mMediaPlayer.getAudioSessionId());
                if (i == 1) {
                    PlayerActivity.presetReverb.setPreset(PRESET_SMALLROOM);
                } else if (i == 2) {
                    PlayerActivity.presetReverb.setPreset(PRESET_MEDIUMROOM);
                } else if (i == 3) {
                    PlayerActivity.presetReverb.setPreset(PRESET_LARGEROOM);
                } else if (i == 4) {
                    PlayerActivity.presetReverb.setPreset(PRESET_MEDIUMHALL);
                } else if (i == 5) {
                    PlayerActivity.presetReverb.setPreset(PRESET_LARGEHALL);
                } else if (i == 6) {
                    PlayerActivity.presetReverb.setPreset(PRESET_PLATE);
                }
                try {
                    PlayerActivity.presetReverb.setEnabled(true);
                    PlayerActivity.mMediaPlayer.setAuxEffectSendLevel(1.0f);
                } catch (Exception exception) {
                    Log.e("100000000", "onClick: " + exception);
                }
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return effects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView environmentEffect;
        ImageView imgView;

        public ViewHolder(View view) {
            super(view);
            environmentEffect = (TextView) view.findViewById(R.id.environmentEffect);
            imgView = (ImageView) view.findViewById(R.id.imgView);
        }
    }
}
