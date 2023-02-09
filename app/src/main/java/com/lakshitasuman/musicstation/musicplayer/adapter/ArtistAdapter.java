package com.lakshitasuman.musicstation.musicplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


import com.lakshitasuman.musicstation.MainActivity;
import com.lakshitasuman.musicstation.musicplayer.ViewArtistSongsActivity;
import com.lakshitasuman.musicstation.musicplayer.model.ArtistModel;
import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.ringtone.utils.AdsUtils;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
    List<ArtistModel> artistList;
    Context ctx;

    public ArtistAdapter(Context context, List<ArtistModel> list) {
        ctx = context;
        artistList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_artist_adapter, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder,  final int i) {
        ArtistModel artistModel = artistList.get(i);
        viewHolder.title.setText(artistModel.getName());
        if (artistModel.getArtistSongs().size() > 1) {
            viewHolder.numSongs.setText(artistModel.getArtistSongs().size() + " Songs");
        } else {
            viewHolder.numSongs.setText(artistModel.getArtistSongs().size() + " Song");
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                MainActivity.tempArtist = MainActivity.finalArtists.get(i);
                AdsUtils.loadInterAd(ctx);
                AdsUtils.loadInterAd(ctx);
                AdsUtils.showInterAd(ctx,new Intent(ctx, ViewArtistSongsActivity.class));
                //ctx.startActivity(new Intent(ctx, ViewArtistSongsActivity.class));
            }
        });
    }


    @Override
    public int getItemCount() {
        return artistList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView numSongs;
        TextView title;


        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.artist_name);
            numSongs = (TextView) view.findViewById(R.id.num_songs);
        }
    }


}
