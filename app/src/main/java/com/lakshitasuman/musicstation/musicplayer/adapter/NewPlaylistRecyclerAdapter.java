package com.lakshitasuman.musicstation.musicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


import com.lakshitasuman.musicstation.MainActivity;
import com.lakshitasuman.musicstation.musicplayer.model.LocalTrackModel;
import com.lakshitasuman.musicstation.R;

public class NewPlaylistRecyclerAdapter extends RecyclerView.Adapter<NewPlaylistRecyclerAdapter.ViewHolder> {
    private Context ctx;

    public List<LocalTrackModel> localTracks;

    public NewPlaylistRecyclerAdapter(Context context, List<LocalTrackModel> localTracks) {
       ctx = context;
       this.localTracks = localTracks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_newplaylist_recycler, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        LocalTrackModel localTrackModel =localTracks.get(i);
        viewHolder.title.setText(localTrackModel.getTitle());
        viewHolder.artist.setText(localTrackModel.getArtist());
        if (MainActivity.finalSelectedTracks.contains(this.localTracks.get(i))) {
            viewHolder.cb.setChecked(true);
        } else {
            viewHolder.cb.setChecked(false);
        }
        viewHolder.itemView.setOnClickListener(view -> {
            LocalTrackModel localTrackModel1 = (LocalTrackModel) NewPlaylistRecyclerAdapter.this.localTracks.get(i);
            if (MainActivity.finalSelectedTracks.contains(localTrackModel1)) {
                MainActivity.finalSelectedTracks.remove(localTrackModel1);
                viewHolder.cb.setChecked(false);
                return;
            }
            MainActivity.finalSelectedTracks.add(localTrackModel1);
            viewHolder.cb.setChecked(true);
        });
    }

    @Override
    public int getItemCount() {
        return localTracks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView art;
        TextView artist;
        CheckBox cb;
        TextView title;

        public ViewHolder(View view) {
            super(view);
            art = (ImageView) view.findViewById(R.id.img_2);
            title = (TextView) view.findViewById(R.id.title_2);
            artist = (TextView) view.findViewById(R.id.url_2);
            cb = (CheckBox) view.findViewById(R.id.is_selected_checkbox);
        }
    }
}
