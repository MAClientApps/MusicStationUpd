package com.lakshitasuman.musicstation.musicplayer.adapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


import com.lakshitasuman.musicstation.MainActivity;
import com.lakshitasuman.musicstation.musicplayer.NewPlaylistActivity;
import com.lakshitasuman.musicstation.musicplayer.ViewPLaylistSongsActivity;
import com.lakshitasuman.musicstation.musicplayer.interfaces.OnRefreshViewListner;
import com.lakshitasuman.musicstation.musicplayer.model.PlaylistModel;
import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.ringtone.utils.AdsUtils;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    Context ctx;
    List<PlaylistModel> playlists;
    OnRefreshViewListner listner;

    public PlaylistAdapter(Context context, List<PlaylistModel> playlists,OnRefreshViewListner listner1) {
        ctx = context;
        this.playlists = playlists;
        this.listner=listner1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_playlist_adapter, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        PlaylistModel playlistModel = playlists.get(i);
        playlistModel.getSongList();
        viewHolder.playListName.setText(playlistModel.getPlaylistName());
        viewHolder.itemView.setOnClickListener(view -> {
            MainActivity.tempPlaylist = MainActivity.allPlaylists.getPlaylists().get(i);
            AdsUtils.initAd(ctx);
            AdsUtils.loadInterAd(ctx);
            AdsUtils.showInterAd(ctx,new Intent(ctx, ViewPLaylistSongsActivity.class));
           // ctx.startActivity(new Intent(ctx, ViewPLaylistSongsActivity.class));
        });
        viewHolder.btnMore.setOnClickListener(view -> {
            MainActivity.allPlaylists.getPlaylists().remove(i);
            new NewPlaylistActivity.SavePlaylists().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            notifyItemRemoved(i);
            notifyDataSetChanged();
            listner.refreshView();
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView btnMore;
        ImageView img;
        TextView playListName;

        public ViewHolder(View view) {
            super(view);
            img = (ImageView) view.findViewById(R.id.image1);
            btnMore = (ImageView) view.findViewById(R.id.btnMore);
            playListName = (TextView) view.findViewById(R.id.playlist_name);
        }
    }


}





