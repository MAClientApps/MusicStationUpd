package com.lakshitasuman.musicstation.musicplayer;

import static com.lakshitasuman.musicstation.MainActivity.tempArtist;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.musicplayer.adapter.SongsAdapter;

public class ViewArtistSongsActivity extends AppCompatActivity {
    TextView artistName;
    ImageView back;
    RecyclerView recyclerview;
    SongsAdapter songsAdapter;
    TextView totalArtistSongs;


    @Override
    public void onCreate(Bundle bundle) {
        String str;
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_view_artist_songs);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        artistName = (TextView) findViewById(R.id.artistName);
        totalArtistSongs = (TextView) findViewById(R.id.totalArtistSongs);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        artistName.setText(tempArtist.getName());
        int size = tempArtist.getArtistSongs().size();
        if (size == 1) {
            str = "1 Song ";
        } else {
            str = size + " Songs ";
        }
        totalArtistSongs.setText(str);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 1));
        songsAdapter = new SongsAdapter(this, tempArtist.getArtistSongs());
        recyclerview.setAdapter(this.songsAdapter);
    }

}
