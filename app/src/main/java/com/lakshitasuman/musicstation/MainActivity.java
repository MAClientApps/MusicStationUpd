package com.lakshitasuman.musicstation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.lakshitasuman.musicstation.musicplayer.HomeActivity;
import com.lakshitasuman.musicstation.musicplayer.model.AlbumComparatorModel;
import com.lakshitasuman.musicstation.musicplayer.model.AlbumModel;
import com.lakshitasuman.musicstation.musicplayer.model.AllMusicFoldersModel;
import com.lakshitasuman.musicstation.musicplayer.model.AllPlaylistsModel;
import com.lakshitasuman.musicstation.musicplayer.model.ArtistComparatorModel;
import com.lakshitasuman.musicstation.musicplayer.model.ArtistModel;
import com.lakshitasuman.musicstation.musicplayer.model.EqualizerModel;
import com.lakshitasuman.musicstation.musicplayer.model.FavouriteModel;
import com.lakshitasuman.musicstation.musicplayer.model.LocalMusicComparator;
import com.lakshitasuman.musicstation.musicplayer.model.LocalTrackModel;
import com.lakshitasuman.musicstation.musicplayer.model.MusicFolderModel;
import com.lakshitasuman.musicstation.musicplayer.model.PlaylistModel;
import com.lakshitasuman.musicstation.musicplayer.model.QueueModel;
import com.lakshitasuman.musicstation.musicplayer.model.RecentlyPlayedModel;
import com.lakshitasuman.musicstation.musicplayer.model.UnifiedTrackModel;
import com.lakshitasuman.musicstation.radio.ActivityMain;
import com.lakshitasuman.musicstation.ringtone.RingToneMainActivity;
import com.lakshitasuman.musicstation.ringtone.utils.AdsUtils;
import com.lakshitasuman.musicstation.voice_change.RecordActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    LinearLayout layoutMusicPlayer;
    LinearLayout layoutRingTone;
    LinearLayout layoutOnlineRadio;
    LinearLayout layoutChangeVoice;
    LinearLayout layoutCreation;
    LinearLayout adsView;
    public boolean loadFirst = false;
    private static final int REQUEST_CODE = 101;
    public static List<AlbumModel> albums ;
    public static AllMusicFoldersModel allMusicFolders;
    public static AllPlaylistsModel allPlaylists;
    public static List<ArtistModel> artists ;
    public static short bassStrength = -1;
    public static List<UnifiedTrackModel> continuePlayingList ;
    public static EqualizerModel equalizerModel;
    public static FavouriteModel favouriteTracks;
    public static List<AlbumModel> finalAlbums ;
    public static List<ArtistModel> finalArtists ;
    public static List<LocalTrackModel> finalLocalSearchResultList;
    public static List<LocalTrackModel> finalRecentlyAddedTrackSearchResultList ;
    public static List<LocalTrackModel> finalSelectedTracks ;
    public static Gson gson;
    public static boolean isEqualizerEnabled = false;
    public static boolean isEqualizerReloaded = false;
    public static boolean isFavourite = false;
    static boolean isSaveFavouritesRunning = false;
    public static List<LocalTrackModel> localTrackList ;
    public static SharedPreferences.Editor prefsEditor;
    public static int presetPos;
    public static QueueModel queue;
    public static int queueCurrentIndex = 0;
    public static float ratio;
    public static float ratio2;
    public static List<LocalTrackModel> recentlyAddedTrackList ;
    public static RecentlyPlayedModel recentlyPlayed;
    public static boolean repeatEnabled = false;
    public static boolean repeatOnceEnabled = false;
    public static short reverbPreset = -1;
    public static int screen_height;
    public static int screen_width;
    public static int[] seekbarPos = new int[5];
    public static AlbumModel tempAlbum;
    public static ArtistModel tempArtist;
    public static List<LocalTrackModel> tempFolderContent;
    public static MusicFolderModel tempMusicFolder;
    public static PlaylistModel tempPlaylist;
    public SharedPreferences mPrefs;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            getSupportActionBar().hide();
        }catch (Exception e){

        }
        layoutMusicPlayer=findViewById(R.id.layoutMusicPlayer);
        layoutRingTone=findViewById(R.id.layoutRingTone);
        layoutOnlineRadio=findViewById(R.id.layoutOnlineRadio);
        layoutChangeVoice=findViewById(R.id.layoutChangeVoice);
        adsView=findViewById(R.id.adsView);
        layoutCreation=findViewById(R.id.layoutCreation);
        AdsUtils.initAd(this);
        AdsUtils.loadLargeBannerAd(this,adsView);
        albums = new ArrayList();
        artists = new ArrayList();
        continuePlayingList = new ArrayList();
        finalAlbums = new ArrayList();
        finalArtists = new ArrayList();
        finalLocalSearchResultList = new ArrayList();
        finalRecentlyAddedTrackSearchResultList = new ArrayList();
        finalSelectedTracks = new ArrayList();
        localTrackList = new ArrayList();
        recentlyAddedTrackList = new ArrayList();

        mPrefs = getPreferences(0);
        prefsEditor = mPrefs.edit();
        gson = new Gson();
        layoutMusicPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });
        layoutRingTone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdsUtils.initAd(MainActivity.this);
                AdsUtils.loadInterAd(MainActivity.this);
                AdsUtils.showInterAd(MainActivity.this,new Intent(getApplicationContext(), RingToneMainActivity.class));

            }
        });
        layoutCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if (isStoragePermissionGranted()){
                  AdsUtils.initAd(MainActivity.this);
                  AdsUtils.loadInterAd(MainActivity.this);
                  AdsUtils.showInterAd(MainActivity.this,new Intent(getApplicationContext(), MainCreationActivity.class));
              }



            }
        });
        layoutOnlineRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdsUtils.initAd(MainActivity.this);
                AdsUtils.loadInterAd(MainActivity.this);
                AdsUtils.showInterAd(MainActivity.this,new Intent(getApplicationContext(), ActivityMain.class));

            }
        });
        layoutChangeVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdsUtils.initAd(MainActivity.this);
                AdsUtils.loadInterAd(MainActivity.this);
                AdsUtils.showInterAd(MainActivity.this,new Intent(getApplicationContext(), RecordActivity.class));

            }
        });
        initSavedData();
    }

    public void checkPermission() {
        String[] strArr = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.RECORD_AUDIO", "android.permission.READ_PHONE_STATE"};
        if (Build.VERSION.SDK_INT > 22) {
            requestPermissions(strArr, 101);
        } else {
            onSuccess();
        }
    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {
                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }

    public void onSuccess() {
        if (!loadFirst) {
            loadFirst = true;
            new getAllSongs().execute(new Void[0]);
        }
        else {
            new getAllSongs().execute(new Void[0]);
        }
    }


    private void initSavedData() {
        if (favouriteTracks == null) {
            favouriteTracks = new FavouriteModel();
        }
        if (recentlyPlayed == null) {
            recentlyPlayed = new RecentlyPlayedModel();
        }
        if (allPlaylists == null) {
            allPlaylists = new AllPlaylistsModel();
        }
        if (queue == null) {
            queue = new QueueModel();
        }
        if (tempPlaylist == null) {
            tempPlaylist = new PlaylistModel((List<UnifiedTrackModel>) null, (String) null);
        }
        if (allMusicFolders == null) {
            allMusicFolders = new AllMusicFoldersModel();
        }
        if (equalizerModel == null) {
            equalizerModel = new EqualizerModel();
            isEqualizerEnabled = true;
            isEqualizerReloaded = false;
        } else {
            isEqualizerEnabled = equalizerModel.isEqualizerEnabled();
            isEqualizerReloaded = true;
            seekbarPos = equalizerModel.getSeekbarPos();
            presetPos = equalizerModel.getPresetPos();
            reverbPreset = equalizerModel.getReverbPreset();
            bassStrength = equalizerModel.getBassStrength();
        }
        Display defaultDisplay = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        screen_width = defaultDisplay.getWidth();
        screen_height = defaultDisplay.getHeight();
        ratio = ((float) screen_height) / 1920.0f;
        ratio2 = ((float) screen_width) / 1080.0f;
        ratio = Math.min(ratio, ratio2);
        favouriteTracks = (FavouriteModel) gson.fromJson(mPrefs.getString("favouriteTracks", ""), FavouriteModel.class);
        allPlaylists = (AllPlaylistsModel) gson.fromJson(mPrefs.getString("allPlaylists", ""), AllPlaylistsModel.class);
    }
    public class getAllSongs extends AsyncTask<Void, Void, Void> {
        public getAllSongs() {
        }


        @Override
        public Void doInBackground(Void... voidArr) {
            getLocalSongs();
            return null;
        }

        @Override
        public void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            super.onPreExecute();
        }


        @Override
        public void onPostExecute(Void voidR) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    progressDialog.dismiss();
                    AdsUtils.initAd(MainActivity.this);
                    AdsUtils.loadRewardedAd(MainActivity.this,new Intent(MainActivity.this, HomeActivity.class));
                    AdsUtils.loadInterAd(MainActivity.this);
                 //   AdsUtils.showInterAd(MainActivity.this,new Intent(MainActivity.this, HomeActivity.class));
                }
            }, 200);
            super.onPostExecute(voidR);
        }
    }

    @Override
    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i != 101) {
            return;
        }
        if (iArr.length > 0) {
            for (int i2 : iArr) {
                if (i2 != 0) {
                    finish();
                    return;
                }
            }
            onSuccess();
            return;
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        loadFirst = false;
        finish();
    }

    public static class SaveFavourites extends AsyncTask<Void, Void, Void> {

        @Override
        public Void doInBackground(Void... voidArr) {
            if (isSaveFavouritesRunning) {
                return null;
            }
            isSaveFavouritesRunning = true;
            try {
                prefsEditor.putString("favouriteTracks", gson.toJson((Object) favouriteTracks)).commit();
            } catch (Exception exception) {
            }
            isSaveFavouritesRunning = false;
            return null;
        }
    }

    public void getLocalSongs() {
        int artistIndex;
        int idIndex;
        int titleIndex;
        int albumIndex;
        int dataIndex;
        int durationIndex;
        Long albumId;
        localTrackList.clear();
        recentlyAddedTrackList.clear();
        finalLocalSearchResultList.clear();
        finalRecentlyAddedTrackSearchResultList.clear();
        albums.clear();
        finalAlbums.clear();
        artists.clear();
        finalArtists.clear();
        allMusicFolders.getMusicFolders().clear();
        Cursor query = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, (String[]) null, (String) null, (String[]) null, "date_added DESC");
        if (query != null && query.moveToFirst()) {
            titleIndex = query.getColumnIndex("title");
            idIndex = query.getColumnIndex("_id");
            artistIndex = query.getColumnIndex("artist");
            albumIndex = query.getColumnIndex("album");
            dataIndex = query.getColumnIndex("_data");
            albumId = query.getLong(query.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
            durationIndex = query.getColumnIndex("duration");
            while (true) {
                long id = query.getLong(idIndex);
                String title = query.getString(titleIndex);
                String artist = query.getString(artistIndex);
                String album = query.getString(albumIndex);
                String data = query.getString(dataIndex);
                long duration = query.getLong(durationIndex);
                if (duration > 10000) {
                    LocalTrackModel localTrackModel = new LocalTrackModel(id, title, artist, album, data, duration);
                    localTrackList.add(localTrackModel);
                    finalLocalSearchResultList.add(localTrackModel);
                    if (recentlyAddedTrackList.size() <= 50) {
                        recentlyAddedTrackList.add(localTrackModel);
                        finalRecentlyAddedTrackSearchResultList.add(localTrackModel);
                    }
                    if (album != null) {
                        int checkAlbum = checkAlbum(album);
                        if (checkAlbum != -1) {
                            albums.get(checkAlbum).getAlbumSongs().add(localTrackModel);
                        } else {
                            ArrayList arrayList = new ArrayList();
                            arrayList.add(localTrackModel);
                            albums.add(new AlbumModel(album, arrayList,albumId));
                        }
                        if (checkAlbum != -1) {
                            finalAlbums.get(checkAlbum).getAlbumSongs().add(localTrackModel);
                        } else {
                            ArrayList arrayList2 = new ArrayList();
                            arrayList2.add(localTrackModel);
                            finalAlbums.add(new AlbumModel(album, arrayList2,albumId));
                        }
                    }
                    if (artist != null) {
                        int checkArtist = checkArtist(artist);
                        if (checkArtist != -1) {
                            artists.get(checkArtist).getArtistSongs().add(localTrackModel);
                        } else {
                            ArrayList arrayList3 = new ArrayList();
                            arrayList3.add(localTrackModel);
                            artists.add(new ArtistModel(artist, arrayList3));
                        }
                        if (checkArtist != -1) {
                            finalArtists.get(checkArtist).getArtistSongs().add(localTrackModel);
                        } else {
                            ArrayList arrayList4 = new ArrayList();
                            arrayList4.add(localTrackModel);
                            finalArtists.add(new ArtistModel(artist, arrayList4));
                        }
                    }
                    String name = new File(data).getParentFile().getName();
                    if (getFolder(name) == null) {
                        MusicFolderModel musicFolderModel = new MusicFolderModel(name);
                        musicFolderModel.getLocalTracks().add(localTrackModel);
                        allMusicFolders.getMusicFolders().add(musicFolderModel);
                    } else {
                        getFolder(name).getLocalTracks().add(localTrackModel);
                    }
                }
                if (!query.moveToNext()) {
                    break;
                }
            }
        }
        if (query != null) {
            query.close();
        }
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        try {
            if (localTrackList.size() > 0) {
                Collections.sort(localTrackList, new LocalMusicComparator());
                Collections.sort(finalLocalSearchResultList, new LocalMusicComparator());
            }
            if (albums.size() > 0) {
                Collections.sort(albums, new AlbumComparatorModel());
                Collections.sort(finalAlbums, new AlbumComparatorModel());
            }
            if (artists.size() > 0) {
                Collections.sort(artists, new ArtistComparatorModel());
                Collections.sort(finalArtists, new ArtistComparatorModel());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        ArrayList arrayList5 = new ArrayList();
        boolean bool = false;
        int i4 = 0;
        for (int i5 = 0; i5 < queue.getQueue().size(); i5++) {
            UnifiedTrackModel unifiedTrackModel = queue.getQueue().get(i5);
            if (unifiedTrackModel.getType() && !checkTrack(unifiedTrackModel.getLocalTrack())) {
                if (i5 == queueCurrentIndex) {
                    bool = true;
                } else if (i5 < queueCurrentIndex) {
                    i4++;
                }
                arrayList5.add(unifiedTrackModel);
            }
        }
        for (int i6 = 0; i6 < arrayList5.size(); i6++) {
            queue.getQueue().remove(arrayList5.get(i6));
        }
        if (!bool) {
            queueCurrentIndex -= i4;
        } else if (queue.getQueue().size() > 0) {
            queueCurrentIndex = 0;
        } else {
            queue = new QueueModel();
        }
        arrayList5.clear();
        for (int i7 = 0; i7 < recentlyPlayed.getRecentlyPlayed().size(); i7++) {
            UnifiedTrackModel unifiedTrackModel = recentlyPlayed.getRecentlyPlayed().get(i7);
            if (unifiedTrackModel.getType() && !checkTrack(unifiedTrackModel.getLocalTrack())) {
                arrayList5.add(unifiedTrackModel);
            }
        }
        for (int i8 = 0; i8 < arrayList5.size(); i8++) {
            recentlyPlayed.getRecentlyPlayed().remove(arrayList5.get(i8));
        }
        ArrayList arrayList6 = new ArrayList();
        ArrayList arrayList7 = new ArrayList();
        if (allPlaylists == null) {
            allPlaylists = new AllPlaylistsModel();
        }
        for (int i9 = 0; i9 < allPlaylists.getPlaylists().size(); i9++) {
            PlaylistModel playlistModel = allPlaylists.getPlaylists().get(i9);
            for (int i10 = 0; i10 < playlistModel.getSongList().size(); i10++) {
                UnifiedTrackModel unifiedTrackModel = playlistModel.getSongList().get(i10);
                if (unifiedTrackModel.getType() && !checkTrack(unifiedTrackModel.getLocalTrack())) {
                    arrayList6.add(unifiedTrackModel);
                }
            }
            for (int i11 = 0; i11 < arrayList6.size(); i11++) {
                playlistModel.getSongList().remove(arrayList6.get(i11));
            }
            arrayList6.clear();
            if (playlistModel.getSongList().size() == 0) {
                arrayList7.add(playlistModel);
            }
        }
        for (int i12 = 0; i12 < arrayList7.size(); i12++) {
            allPlaylists.getPlaylists().remove(arrayList7.get(i12));
        }
        arrayList7.clear();
    }

    public boolean checkTrack(LocalTrackModel localTrackModel) {
        for (int i = 0; i < localTrackList.size(); i++) {
            if (localTrackList.get(i).getTitle().equals(localTrackModel.getTitle())) {
                return true;
            }
        }
        return false;
    }

    public int checkAlbum(String str) {
        for (int i = 0; i < albums.size(); i++) {
            if (albums.get(i).getName().equals(str)) {
                return i;
            }
        }
        return -1;
    }

    public int checkArtist(String str) {
        for (int i = 0; i < artists.size(); i++) {
            if (artists.get(i).getName().equals(str)) {
                return i;
            }
        }
        return -1;
    }

    public MusicFolderModel getFolder(String str) {
        for (int i = 0; i < allMusicFolders.getMusicFolders().size(); i++) {
            MusicFolderModel musicFolderModel = allMusicFolders.getMusicFolders().get(i);
            if (musicFolderModel.getFolderName().equals(str)) {
                return musicFolderModel;
            }
        }
        return null;
    }

}