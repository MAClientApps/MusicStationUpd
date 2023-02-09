package com.lakshitasuman.musicstation.ringtone.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lakshitasuman.musicstation.ringtone.RingToneMainActivity;
import com.lakshitasuman.musicstation.ringtone.PlayRingtoneActivity;
import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.ringtone.model.SongList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyNameRingtoneListAdapter extends RecyclerView.Adapter<MyNameRingtoneListAdapter.ViewHolder> implements Filterable {
    ArrayList<SongList> allListFilter;
    Context context;
    String folderPath;
    private Filter exampleFilter = new Filter() {

        @Override
        public FilterResults performFiltering(CharSequence charSequence) {
            ArrayList arrayList = new ArrayList();
            if (charSequence == null || charSequence.length() == 0) {
                arrayList.addAll(allListFilter);
            } else {
                String trim = charSequence.toString().toLowerCase().trim();
                Iterator<SongList> it =allListFilter.iterator();
                while (it.hasNext()) {
                    SongList next = it.next();
                    if (next.getName().toLowerCase().contains(trim)) {
                        arrayList.add(next);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = arrayList;
            return filterResults;
        }


        @Override
        public void publishResults(CharSequence charSequence, FilterResults filterResults) {
           songList.clear();
           songList.addAll((List) filterResults.values);
           notifyDataSetChanged();
        }
    };
    ArrayList<SongList> songList;

    public MyNameRingtoneListAdapter(Context context2, ArrayList<SongList> songList,String folderPath1) {
        context = context2;
        this.songList = songList;
        allListFilter = new ArrayList<>(songList);
        folderPath=folderPath1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.song_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        viewHolder.folderName.setText(songList.get(i).getName());
        viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
          @Override
            public void onClick(final View view) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                if (elapsedRealtime - RingToneMainActivity.lastClickTime > RingToneMainActivity.MIN_CLICK_INTERVAL) {
                    RingToneMainActivity.lastClickTime = elapsedRealtime;
                    Intent intent = new Intent(view.getContext(), PlayRingtoneActivity.class);
                    intent.putExtra("Song Position", i);
                    intent.putExtra("path",folderPath);
                    intent.putExtra("Page Code", 1);
                    view.getContext().startActivity(intent);
                    ((Activity) context).finish();

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public Context context;
        ImageView delete;
        TextView folderName;
        ImageView icon;
        RelativeLayout relativeLayout;
        ImageView share;

        public ViewHolder(View view) {
            super(view);
            context = view.getContext();
            relativeLayout = (RelativeLayout) view.findViewById(R.id.folder_relative_layout);
            folderName = (TextView) view.findViewById(R.id.song_name);
        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
}
