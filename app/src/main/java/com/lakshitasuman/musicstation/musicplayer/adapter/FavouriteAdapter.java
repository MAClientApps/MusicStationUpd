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

import com.lakshitasuman.musicstation.musicplayer.PlayerActivity;
import com.lakshitasuman.musicstation.musicplayer.model.LocalTrackModel;
import com.lakshitasuman.musicstation.musicplayer.model.UnifiedTrackModel;
import com.lakshitasuman.musicstation.R;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {
    public Context ctx;
    private List<UnifiedTrackModel> favSongList;
    public List<LocalTrackModel> songListTrack;

    public FavouriteAdapter(Context context, List<UnifiedTrackModel> favSongList, List<LocalTrackModel> songListTrack) {
        ctx = context;
        this.favSongList = favSongList;
        this.songListTrack = songListTrack;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_playlist_songs, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        LocalTrackModel localTrack = favSongList.get(i).getLocalTrack();
        viewHolder.title.setText(localTrack.getTitle());
        viewHolder.artist.setText(localTrack.getArtist());
        viewHolder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(ctx, PlayerActivity.class);
            intent.putExtra("fromControl", "false");
            Bundle bundle = new Bundle();
            bundle.putSerializable("ARRAYLIST", (Serializable) songListTrack);
            intent.putExtra("BUNDLE", bundle);
            ctx.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return favSongList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView artist;
        TextView title;

        public ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            artist = (TextView) view.findViewById(R.id.url);
        }
    }


}
