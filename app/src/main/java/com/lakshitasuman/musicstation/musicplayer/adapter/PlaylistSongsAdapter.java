package com.lakshitasuman.musicstation.musicplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

import com.lakshitasuman.musicstation.musicplayer.Constants;
import com.lakshitasuman.musicstation.musicplayer.PlayerActivity;
import com.lakshitasuman.musicstation.musicplayer.PlayerService;
import com.lakshitasuman.musicstation.musicplayer.model.LocalTrackModel;
import com.lakshitasuman.musicstation.musicplayer.model.UnifiedTrackModel;
import com.lakshitasuman.musicstation.R;

public class PlaylistSongsAdapter extends RecyclerView.Adapter<PlaylistSongsAdapter.ViewHolder> {
    public Context ctx;
    private List<UnifiedTrackModel> songList;
    public List<LocalTrackModel> songListTrack;
    public PlaylistSongsAdapter(Context context, List<UnifiedTrackModel> songList, List<LocalTrackModel> songListTrack) {
        ctx = context;
        this.songList = songList;
        this.songListTrack = songListTrack;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_playlist_songs, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        LocalTrackModel localTrack = songList.get(i).getLocalTrack();
        viewHolder.title.setText(localTrack.getTitle());
        viewHolder.artist.setText(localTrack.getArtist());
        viewHolder.itemView.setOnClickListener(view -> {
            LocalTrackModel localTrackModel = (LocalTrackModel) songListTrack.get(i);
            PlayerActivity.positionLocalTrack = i;
            Intent intent = new Intent(ctx, PlayerActivity.class);
            intent.putExtra("fromControl", "false");
            Bundle bundle = new Bundle();
            bundle.putSerializable("ARRAYLIST", (Serializable) songListTrack);
            intent.putExtra("BUNDLE", bundle);
            ctx.startActivity(intent);
            Intent intent2 = new Intent(ctx, PlayerService.class);
            intent2.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            intent2.putExtra("title", localTrackModel.getTitle());
            intent2.putExtra("subtitle", localTrackModel.getArtist());
            ctx.startService(intent2);
        });
    }

    @Override
    public int getItemCount() {
        return this.songList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView artist;
        TextView title;

        public ViewHolder(View view) {
            super(view);

            this.title = (TextView) view.findViewById(R.id.title);
            this.artist = (TextView) view.findViewById(R.id.url);
        }
    }


}
