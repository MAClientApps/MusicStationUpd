package com.lakshitasuman.musicstation.musicplayer;

import static com.lakshitasuman.musicstation.MainActivity.tempPlaylist;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.musicplayer.adapter.PlaylistSongsAdapter;
import com.lakshitasuman.musicstation.musicplayer.model.LocalTrackModel;

public class ViewPLaylistSongsActivity extends AppCompatActivity {
    ImageView back;
    PlaylistSongsAdapter playlistSongsAdapter;
    RecyclerView recyclerView;
    private List<LocalTrackModel> songListTrack;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_view_playlist_songs);
        songListTrack = new ArrayList();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        back = (ImageView) findViewById(R.id.back);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        for (int i = 0; i < tempPlaylist.getSongList().size(); i++) {
            songListTrack.add(tempPlaylist.getSongList().get(i).getLocalTrack());
        }
        playlistSongsAdapter = new PlaylistSongsAdapter(this, tempPlaylist.getSongList(), songListTrack);
        recyclerView.setAdapter(playlistSongsAdapter);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });
    }
}
