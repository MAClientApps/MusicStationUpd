package com.lakshitasuman.musicstation.musicplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


import com.lakshitasuman.musicstation.MainActivity;
import com.lakshitasuman.musicstation.musicplayer.ViewFolderSongsActivity;
import com.lakshitasuman.musicstation.musicplayer.model.LocalTrackModel;
import com.lakshitasuman.musicstation.musicplayer.model.MusicFolderModel;
import com.lakshitasuman.musicstation.R;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {
    Context ctx;
    List<MusicFolderModel> musicFolders;


    public FolderAdapter(Context context, List<MusicFolderModel> musicFolders) {
        ctx = context;
        this.musicFolders = musicFolders;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_folder_adapter, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        MusicFolderModel musicFolderModel = musicFolders.get(i);
        List<LocalTrackModel> localTracks = musicFolderModel.getLocalTracks();
        viewHolder.playListName.setText(musicFolderModel.getFolderName());
        viewHolder.name.setText(String.valueOf(localTracks.size())+" "+"Songs");
        viewHolder.itemView.setOnClickListener(view -> {
            MainActivity.tempMusicFolder = MainActivity.allMusicFolders.getMusicFolders().get(i);
            MainActivity.tempFolderContent = MainActivity.tempMusicFolder.getLocalTracks();
            ctx.startActivity(new Intent(ctx, ViewFolderSongsActivity.class));
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return musicFolders.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView continuedSymbol;
        ImageView[] img = new ImageView[3];
        TextView name;
        TextView playListName;

        public ViewHolder(View view) {
            super(view);
            img[0] = (ImageView) view.findViewById(R.id.image1);
            img[1] = (ImageView) view.findViewById(R.id.image2);
            img[2] = (ImageView) view.findViewById(R.id.image3);
            playListName = (TextView) view.findViewById(R.id.playlist_name);
            name =  (TextView) view.findViewById(R.id.name1);

        }
    }
}
