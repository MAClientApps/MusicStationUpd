package com.lakshitasuman.musicstation.musicplayer;

import static com.lakshitasuman.musicstation.MainActivity.tempFolderContent;
import static com.lakshitasuman.musicstation.MainActivity.tempMusicFolder;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.musicplayer.adapter.SongsAdapter;

public class ViewFolderSongsActivity extends AppCompatActivity {
    ImageView back;
    TextView folderName;
    RecyclerView recyclerview;
    SongsAdapter songsAdapter;
    TextView totalFolderSongs;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_view_folder_songs);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        folderName = (TextView) findViewById(R.id.folderName);
        totalFolderSongs = (TextView) findViewById(R.id.totalFolderSongs);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        folderName.setText(tempMusicFolder.getFolderName());
        String str = tempMusicFolder.getLocalTracks().size() > 1 ? "Songs" : "Song";
        totalFolderSongs.setText(tempMusicFolder.getLocalTracks().size() + " " + str);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 1));
        songsAdapter = new SongsAdapter(this, tempFolderContent);
        recyclerview.setAdapter(songsAdapter);
    }




}
