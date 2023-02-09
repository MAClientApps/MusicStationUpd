package com.lakshitasuman.musicstation.musicplayer;

import static com.lakshitasuman.musicstation.MainActivity.tempAlbum;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.musicplayer.adapter.SongsAdapter;

public class ViewAlbumSongsActivity extends AppCompatActivity {
    TextView albumName;
    ImageView back;
    RecyclerView recyclerview;
    SongsAdapter songsAdapter;
    TextView totalAlbumSongs;


    @Override
    public void onCreate(Bundle bundle) {
        String str;
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_view_album_songs);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        albumName = (TextView) findViewById(R.id.albumName);
        totalAlbumSongs = (TextView) findViewById(R.id.totalAlbumSongs);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        albumName.setText(tempAlbum.getName());
        int size = tempAlbum.getAlbumSongs().size();
        if (size == 1) {
            str = "1 Song ";
        } else {
            str = size + " Songs ";
        }
        totalAlbumSongs.setText(str);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 1));
        songsAdapter = new SongsAdapter(this, tempAlbum.getAlbumSongs());
        recyclerview.setAdapter(songsAdapter);
    }


}
