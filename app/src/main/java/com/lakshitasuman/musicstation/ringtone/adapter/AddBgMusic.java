package com.lakshitasuman.musicstation.ringtone.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lakshitasuman.musicstation.R;

import com.lakshitasuman.musicstation.ringtone.utils.MyUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class AddBgMusic extends RecyclerView.Adapter<AddBgMusic.Holder> {
    ArrayList<String> allList = new ArrayList<>();
    Context cn;
    int currentPlay = -1;
    String[] list;
    MediaPlayer mediaPlayer;
    OnMyCommonItem onMyCommonItem;
    String selectedName;


    public AddBgMusic(Context context, String selectedName, OnMyCommonItem onMyCommonItem) {
        cn = context;
        this.selectedName = selectedName;
        this.onMyCommonItem = onMyCommonItem;
        try {
            list = context.getAssets().list("ring");
            allList.add("None");
            allList.addAll(new ArrayList(Arrays.asList(list)));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }




    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            notifyDataSetChanged();
        }
    }



    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        File file = new File(cn.getFilesDir(), "bgtmp.mp3");
        if (file.exists()) {
            file.delete();
        }
    }


    public class Holder extends RecyclerView.ViewHolder {
        ImageView btnPlay;
        ImageView btnSelect;
        LinearLayout layMain;
        TextView setName;

        public Holder(View view) {
            super(view);
            layMain = (LinearLayout) view.findViewById(R.id.laymain);
            setName = (TextView) view.findViewById(R.id.setname);
            btnSelect = (ImageView) view.findViewById(R.id.btnselect);
            btnPlay = (ImageView) view.findViewById(R.id.btnplay);

        }
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(cn).inflate(R.layout.ad_bgmusic, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, final int i) {
        String str = allList.get(i);
        holder.setName.setText(str);
        if (str.equalsIgnoreCase(selectedName)) {
            holder.btnSelect.setImageResource(R.drawable.presed2);
        } else {
            holder.btnSelect.setImageResource(R.drawable.unpresed2);
        }
        if (str.equalsIgnoreCase("None")) {
            holder.btnPlay.setVisibility(View.GONE);
        } else {
            holder.btnPlay.setVisibility(View.VISIBLE);
        }
        if (currentPlay != i) {
            holder.btnPlay.setImageResource(R.drawable.play);
        } else {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    holder.btnPlay.setImageResource(R.drawable.pause);
                } else {
                    holder.btnPlay.setImageResource(R.drawable.play);
                }
            }
        }
        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = allList.get(i);
                if (!str.equalsIgnoreCase("None")) {
                    AddBgMusic ad_bgmusic = AddBgMusic.this;
                    Context context = ad_bgmusic.cn;
                    ad_bgmusic.initMedia(MyUtils.copyfromassets(context, "ring/" + str, new File(cn.getFilesDir(), "bgtmp.mp3").getAbsolutePath()), i);
                }
            }
        });
        holder.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = allList.get(i);
                onMyCommonItem.OnMyClick2(i, str, MyUtils.copyfromassets(cn, "ring/" + str, new File(cn.getFilesDir(), "bgmusic.mp3").getAbsolutePath()));
            }
        });
    }


    public void initMedia(String str, final int i) {
        if (currentPlay != i || (mediaPlayer) == null || !mediaPlayer.isPlaying()) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(str);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        currentPlay = -1;
                        notifyDataSetChanged();
                    }
                });
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                        currentPlay = i;
                        notifyDataSetChanged();
                    }
                });
               mediaPlayer.prepareAsync();
            } catch (IOException ioException) {
                ioException.printStackTrace();
                Log.e("AAA", "Error Player : " + ioException.toString());
            }
        } else {
            pauseMusic();
        }
    }

    @Override
    public int getItemCount() {
        return list.length;
    }
}
