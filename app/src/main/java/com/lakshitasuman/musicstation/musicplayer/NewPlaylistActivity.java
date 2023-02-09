package com.lakshitasuman.musicstation.musicplayer;

import static com.lakshitasuman.musicstation.MainActivity.allPlaylists;
import static com.lakshitasuman.musicstation.MainActivity.finalSelectedTracks;
import static com.lakshitasuman.musicstation.MainActivity.gson;
import static com.lakshitasuman.musicstation.MainActivity.localTrackList;
import static com.lakshitasuman.musicstation.MainActivity.prefsEditor;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.musicplayer.adapter.NewPlaylistRecyclerAdapter;
import com.lakshitasuman.musicstation.musicplayer.model.LocalTrackModel;
import com.lakshitasuman.musicstation.musicplayer.model.PlaylistModel;
import com.lakshitasuman.musicstation.musicplayer.model.TrackModel;
import com.lakshitasuman.musicstation.musicplayer.model.UnifiedTrackModel;
import com.lakshitasuman.musicstation.ringtone.utils.AdsUtils;

public class NewPlaylistActivity extends AppCompatActivity {
    static boolean isSavePLaylistsRunning = false;
    ImageView Done;
    private FrameLayout adContainerView;
    ImageView back;
    List<LocalTrackModel> finalList;
    NewPlaylistRecyclerAdapter newPlaylistRecyclerAdapter;
    RecyclerView recyclerview;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_new_playlist);
        AdsUtils.initAd(this);
        AdsUtils.loadLargeBannerAd(this,findViewById(R.id.adsView));
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        Done = (ImageView) findViewById(R.id.Done);
        back = (ImageView) findViewById(R.id.back);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 1));
        finalList = new ArrayList();
        for (int i = 0; i < localTrackList.size(); i++) {
            finalList.add(localTrackList.get(i));
        }
        newPlaylistRecyclerAdapter = new NewPlaylistRecyclerAdapter(this, finalList);
        recyclerview.setAdapter(newPlaylistRecyclerAdapter);
        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalSelectedTracks.size() == 0) {
                    finalSelectedTracks.clear();
                }  else {
                    newPlaylistNameDialog();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }



    public void newPlaylistNameDialog() {
        final Dialog dialog = new Dialog(this, R.style.MaterialDialogSheet);
        dialog. requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_addplaylist);
        final EditText editText = (EditText) dialog.findViewById(R.id.save_image_filename_text);
        editText.setTypeface(Typeface.createFromAsset(getAssets(), "Heebo-Regular.ttf"));
        ((LinearLayout) dialog.findViewById(R.id.layoutDone)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                boolean bool;
                if (editText.getText().toString().trim().equals("")) {
                    editText.setError("Enter Playlist Name!");
                    return;
                }
                int i = 0;
                while (true) {
                    if (i >= allPlaylists.getPlaylists().size()) {
                        bool = false;
                        break;
                    } else if (editText.getText().toString().equals(allPlaylists.getPlaylists().get(i).getPlaylistName())) {
                        editText.setError("Playlist with same name exists!");
                        bool = true;
                        break;
                    } else {
                        i++;
                    }
                }
                if (!bool) {
                    PlaylistModel playlistModel = new PlaylistModel(editText.getText().toString());
                    for (int i2 = 0; i2 < finalSelectedTracks.size(); i2++) {
                        playlistModel.getSongList().add(new UnifiedTrackModel(true, finalSelectedTracks.get(i2), (TrackModel) null));
                    }
                    allPlaylists.addPlaylist(playlistModel);
                    finalSelectedTracks.clear();
                    new SavePlaylists().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    dialog.dismiss();
                    onBackPressed();
                }
            }
        });
        dialog.show();
    }

    public static class SavePlaylists extends AsyncTask<Void, Void, Void> {

        @Override
        public Void doInBackground(Void... voidArr) {
            if (isSavePLaylistsRunning) {
                return null;
            }
            isSavePLaylistsRunning = true;
            try {
                prefsEditor.putString("allPlaylists", gson.toJson((Object) allPlaylists)).commit();
            } catch (Exception exceptionxception) {
            }
            isSavePLaylistsRunning = false;
            return null;
        }
    }
}
