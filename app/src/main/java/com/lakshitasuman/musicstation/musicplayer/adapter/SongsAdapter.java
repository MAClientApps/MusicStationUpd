package com.lakshitasuman.musicstation.musicplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

import com.lakshitasuman.musicstation.musicplayer.PlayerActivity;
import com.lakshitasuman.musicstation.musicplayer.model.LocalTrackModel;
import com.lakshitasuman.musicstation.R;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {
    public Context ctx;
    public List<LocalTrackModel> localTracks;

    public SongsAdapter(Context context, List<LocalTrackModel> localTracks) {
       ctx = context;
       this.localTracks = localTracks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_songs_adapter, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        LocalTrackModel localTrackModel =localTracks.get(i);
        viewHolder.title.setText(localTrackModel.getTitle());
        viewHolder.artist.setText(localTrackModel.getArtist());
        viewHolder.itemView.setOnClickListener(view -> {
            LocalTrackModel localTrackModel1 = (LocalTrackModel) localTracks.get(i);
            PlayerActivity.positionLocalTrack = i;
            Intent intent = new Intent(ctx, PlayerActivity.class);
            intent.putExtra("fromControl", "false");
            Bundle bundle = new Bundle();
            bundle.putSerializable("ARRAYLIST", (Serializable) localTracks);
            intent.putExtra("BUNDLE", bundle);
            ctx.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return localTracks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView art;
        TextView artist;
        TextView title;

        public ViewHolder(View view) {
            super(view);
           art = (ImageView) view.findViewById(R.id.img_2);
           title = (TextView) view.findViewById(R.id.title_2);
           artist = (TextView) view.findViewById(R.id.url_2);
        }
    }


}
