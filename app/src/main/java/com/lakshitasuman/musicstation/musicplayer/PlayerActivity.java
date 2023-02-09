package com.lakshitasuman.musicstation.musicplayer;

import static android.media.audiofx.PresetReverb.PRESET_NONE;

import static com.lakshitasuman.musicstation.MainActivity.bassStrength;
import static com.lakshitasuman.musicstation.MainActivity.favouriteTracks;
import static com.lakshitasuman.musicstation.MainActivity.isEqualizerEnabled;
import static com.lakshitasuman.musicstation.MainActivity.isEqualizerReloaded;
import static com.lakshitasuman.musicstation.MainActivity.presetPos;
import static com.lakshitasuman.musicstation.MainActivity.repeatEnabled;
import static com.lakshitasuman.musicstation.MainActivity.repeatOnceEnabled;
import static com.lakshitasuman.musicstation.MainActivity.reverbPreset;
import static com.lakshitasuman.musicstation.MainActivity.seekbarPos;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.musicplayer.fragment.EffectsFragment;
import com.lakshitasuman.musicstation.musicplayer.fragment.EnvironmentFragment;
import com.lakshitasuman.musicstation.musicplayer.fragment.EqualizerFragment;
import com.lakshitasuman.musicstation.musicplayer.fragment.VisualizerFragment;
import com.lakshitasuman.musicstation.musicplayer.model.FavouriteModel;
import com.lakshitasuman.musicstation.musicplayer.model.LocalTrackModel;
import com.lakshitasuman.musicstation.musicplayer.model.TrackModel;
import com.lakshitasuman.musicstation.musicplayer.model.UnifiedTrackModel;
import com.lakshitasuman.musicstation.radio.service.PauseReason;
import com.lakshitasuman.musicstation.radio.service.PlayerServiceUtil;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    public static List<LocalTrackModel> PlayerlocalTracksList = null;
    private static final String TAG = "PlayerAc";
    public static ImageView back = null;
    public static BassBoost bassBoost = null;
    public static ImageView controller = null;
    public static int durationInMilliSec = 0;
    public static Gson gson = null;
    public static boolean isTimerSet = false;
    public static Equalizer mEqualizer;
    public static MediaPlayer mMediaPlayer;
    public static ImageView next;
    public static int positionLocalTrack;
    public static SharedPreferences.Editor prefsEditor;
    public static PresetReverb presetReverb;
    public static ImageView previous;
    public static TextView txt1;
    public static TextView txt2;
    public static TextView txtTimer;
    LocalTrackModel playerLocalTrack;
    Bundle args;
    TextView currTime;
    EffectsFragment effectsFragment;
    EnvironmentFragment environmentFragment;
    EqualizerFragment equalizerFragment;
    public ImageView favouriteIcon;
    String fromControl = "false";
    Intent i;
    public ImageView imgMore;
    boolean isFav = false;
    boolean isRepeatEnable = false;
    boolean isShuffleEnable = false;
    ImageView ll1;
    ImageView ll2;
    ImageView ll3;
    ImageView ll4;
    public SharedPreferences mPrefs;
    SeekBar progressBar;
    ImageView repeat_controller;
    ImageView shuffle_controller;
    Pair<String, String> temp;
    public Timer timer;
    TextView totalTime;
    ViewPager viewpager;
    VisualizerFragment visualizerFragment;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_player);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                StrictMode.class.getMethod("disableDeathOnFileUriExposure", new Class[0]).invoke((Object) null, new Object[0]);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        mPrefs = getPreferences(0);
        prefsEditor = mPrefs.edit();
        gson = new Gson();
        init();
        initIntentData();
        setTime();
        setupVisualizerFxAndUI();
        addTabsToViewpager(viewpager);
        checkFav();
        clicks();
        if (PlayerServiceUtil.isPlaying()) {
            PlayerServiceUtil.pause(PauseReason.NONE);
        }
        Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
        intent.setAction(Constants.ACTION.NOTIFICATION);
        startService(intent);
    }
    private void init() {
        repeat_controller = (ImageView) findViewById(R.id.repeat_controller);
        shuffle_controller = (ImageView) findViewById(R.id.shuffle_controller);
        previous = (ImageView) findViewById(R.id.previous);
        controller = (ImageView) findViewById(R.id.controller);
        next = (ImageView) findViewById(R.id.next);
        back = (ImageView) findViewById(R.id.back);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        progressBar = (SeekBar) findViewById(R.id.progressBar);
        currTime = (TextView) findViewById(R.id.currTime);
        totalTime = (TextView) findViewById(R.id.totalTime);
        txt1 = (TextView) findViewById(R.id.txt1);
        txt2 = (TextView) findViewById(R.id.txt2);
        favouriteIcon = (ImageView) findViewById(R.id.fav_icon);
        imgMore = (ImageView) findViewById(R.id.imgMore);
        txtTimer = (TextView) findViewById(R.id.txtTimer);
        ll1 = (ImageView) findViewById(R.id.ll1);
        ll2 = (ImageView) findViewById(R.id.ll2);
        ll3 = (ImageView) findViewById(R.id.ll3);
        ll4 = (ImageView) findViewById(R.id.ll4);
    }

    public void moreOptionDialog() {
        final Dialog dialog = new Dialog(this, R.style.MaterialDialogSheet);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_moreoption);
        LinearLayout layoutClose = (LinearLayout) dialog.findViewById(R.id.imgCloss);
        ((TextView) dialog.findViewById(R.id.txt11)).setText(playerLocalTrack.getTitle());
        ((TextView) dialog.findViewById(R.id.txt12)).setText(playerLocalTrack.getArtist());
        ((LinearLayout) dialog.findViewById(R.id.setasring)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    File file=new File(playerLocalTrack.getPath());
                    if (Settings.System.canWrite(PlayerActivity.this)) {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.MediaColumns.TITLE, file.getName());
                        values.put(MediaStore.MediaColumns.MIME_TYPE, getMIMEType(file.getAbsolutePath()));//// getMIMEType(k.getAbsolutePath())
                        values.put(MediaStore.MediaColumns.SIZE, file.length());
                        values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
                        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            Uri newUri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
                            try (OutputStream os = getContentResolver().openOutputStream(newUri)) {
                                int size = (int) file.length();
                                byte[] bytes = new byte[size];
                                try {
                                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                                    buf.read(bytes, 0, bytes.length);
                                    buf.close();
                                    os.write(bytes);
                                    os.close();
                                    os.flush();
                                } catch (IOException exception) {
                                }
                            } catch (Exception ignored) {

                                Log.e("error",ignored.getMessage());
                            }
                            RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE, newUri);
                            Toast.makeText(PlayerActivity.this, "Ringtone set", Toast.LENGTH_SHORT).show();
                        }
                       /* try {
                            RingtoneManager.setActualDefaultRingtoneUri(PlayerActivity.this, 1, Uri.parse(playerLocalTrack.getPath()));
                            Toast.makeText(PlayerActivity.this, "Ringtone set successfully", Toast.LENGTH_LONG).show();
                        } catch (Exception exception) {
                            Log.e("error",exception.getMessage());
                        }*/
                    } else {
                        Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS");
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                    dialog.dismiss();
                }
            }
        });

        layoutClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.details)).setOnClickListener(new View.OnClickListener() {
          @Override
            public void onClick(View view) {
                dialog.dismiss();
                detailsDialog();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.share)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareSong(playerLocalTrack.getPath());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void detailsDialog() {
        Dialog dialog = new Dialog(this, R.style.MaterialDialogSheet);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_details);
        ((TextView) dialog.findViewById(R.id.txtSongname)).setText("Song :  " + playerLocalTrack.getTitle());
        ((TextView) dialog.findViewById(R.id.txtArtist)).setText("Artist :  " + playerLocalTrack.getArtist());
        ((TextView) dialog.findViewById(R.id.txtLocation)).setText("Location :  " + playerLocalTrack.getPath());
        durationInMilliSec = (int) playerLocalTrack.getDuration();
        temp = getTime(durationInMilliSec);
        ((TextView) dialog.findViewById(R.id.txtDuration)).setText("Duration :  " + ((String) temp.first) + ":" + ((String) temp.second));
        dialog.show();
    }
    public static String getMIMEType(String url) {
        String mType = null;
        String mExtension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (mExtension != null) {
            mType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mExtension);
        }
        return mType;
    }

    public void shareSong(String str) {
        Uri fromFile = Uri.fromFile(new File(str));
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.TEXT", "");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("android.intent.extra.STREAM", fromFile);
        intent.setType("audio/*");
        startActivity(Intent.createChooser(intent, "Choose one"));
    }
    public void shufflePlayDisable() {
        PlayerlocalTracksList = (List) gson.fromJson(mPrefs.getString("suffleTracks", ""), new TypeToken<List<LocalTrackModel>>() {
        }.getType());
    }

    public void shufflePlayEnable() {
        Collections.shuffle(PlayerlocalTracksList);
    }

    private void clicks() {
        repeat_controller.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (repeatOnceEnabled) {
                    repeatOnceEnabled = false;
                    repeat_controller.setImageResource(R.drawable.repeat_unpresed);
                } else if (repeatEnabled) {
                    repeatEnabled = false;
                    repeatOnceEnabled = true;
                    repeat_controller.setImageResource(R.drawable.repeat_presed1);
                } else {
                    repeatEnabled = true;
                    repeat_controller.setImageResource(R.drawable.repeat_presed);
                }
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                Log.e(TAG, "onError: " + i + "....." + i2);
                return true;
            }
        });
        imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreOptionDialog();
            }
        });
        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewpager.setCurrentItem(0);
                ll1.setImageResource(R.drawable.playlist_play_unpresed);
                ll2.setImageResource(R.drawable.equalizer_presed);
                ll3.setImageResource(R.drawable.bass_presed);
                ll4.setImageResource(R.drawable.environment_presed);
            }
        });
        ll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewpager.setCurrentItem(1);
                ll1.setImageResource(R.drawable.playlist_play_presed);
                ll2.setImageResource(R.drawable.equalizer_unpresed);
                ll3.setImageResource(R.drawable.bass_presed);
                ll4.setImageResource(R.drawable.environment_presed);
            }
        });
        ll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewpager.setCurrentItem(2);
                ll1.setImageResource(R.drawable.playlist_play_presed);
                ll2.setImageResource(R.drawable.equalizer_presed);
                ll3.setImageResource(R.drawable.bass_unpresed);
                ll4.setImageResource(R.drawable.environment_presed);
            }
        });
        ll4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewpager.setCurrentItem(3);
                ll1.setImageResource(R.drawable.playlist_play_presed);
                ll2.setImageResource(R.drawable.equalizer_presed);
                ll3.setImageResource(R.drawable.bass_presed);
                ll4.setImageResource(R.drawable.environment_unpresed);
            }
        });
        shuffle_controller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShuffleEnable) {
                    shuffle_controller.setImageResource(R.drawable.shuffle_unpresed);
                    isShuffleEnable = false;
                    shufflePlayDisable();
                    return;
                }
                shuffle_controller.setImageResource(R.drawable.shuffle_presed);
                isShuffleEnable = true;
                shufflePlayEnable();
            }
        });
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mMediaPlayer.setOnCompletionListener(PlayerActivity.this);
                mMediaPlayer.start();
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
            }
        });
        controller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PlayerServiceUtil.isPlaying()){
                    PlayerServiceUtil.pause(PauseReason.NONE);
                }
                Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                intent.setAction(Constants.ACTION.PLAY_ACTION);
                startService(intent);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playNext();
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPrevious();
            }
        });
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int i) {
            }

            @Override
            public void onPageScrolled(int i, float f, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                viewpager.getAdapter().notifyDataSetChanged();
            }
        });
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int i) {
            }
            @Override
            public void onPageScrolled(int i, float f, int i2) {
            }
            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        viewpager.setCurrentItem(0);
                        ll1.setImageResource(R.drawable.playlist_play_unpresed);
                        ll2.setImageResource(R.drawable.equalizer_presed);
                        ll3.setImageResource(R.drawable.bass_presed);
                        ll4.setImageResource(R.drawable.environment_presed);
                        return;
                    case 1:
                        viewpager.setCurrentItem(1);
                        ll1.setImageResource(R.drawable.playlist_play_presed);
                        ll2.setImageResource(R.drawable.equalizer_unpresed);
                        ll3.setImageResource(R.drawable.bass_presed);
                        ll4.setImageResource(R.drawable.environment_presed);
                        return;
                    case 2:
                        viewpager.setCurrentItem(2);
                        ll1.setImageResource(R.drawable.playlist_play_presed);
                        ll2.setImageResource(R.drawable.equalizer_presed);
                        ll3.setImageResource(R.drawable.bass_unpresed);
                        ll4.setImageResource(R.drawable.environment_presed);
                        return;
                    case 3:
                        viewpager.setCurrentItem(3);
                        ll1.setImageResource(R.drawable.playlist_play_presed);
                        ll2.setImageResource(R.drawable.equalizer_presed);
                        ll3.setImageResource(R.drawable.bass_presed);
                        ll4.setImageResource(R.drawable.environment_unpresed);
                        return;
                    default:
                        return;
                }
            }
        });
        favouriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFav) {
                    favouriteIcon.setImageResource(R.drawable.favourites_unpresed);
                    isFav = false;
                    removeFromFavourite();
                } else {
                    favouriteIcon.setImageResource(R.drawable.favourites_presed);
                    isFav = true;
                    addToFavourite();
                }
                new com.lakshitasuman.musicstation.MainActivity.SaveFavourites().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            }
        });
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                temp = getTime(i);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currTime.setText(((String) temp.first) + ":" + ((String) temp.second));
                    }
                });
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.seekTo(seekBar.getProgress());
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
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


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onResume() {


        super.onResume();
    }



    private void initIntentData() {
        i = getIntent();
        fromControl = i.getStringExtra("fromControl");
        try {
            args = i.getBundleExtra("BUNDLE");
            PlayerlocalTracksList = (ArrayList) args.getSerializable("ARRAYLIST");
        } catch (Exception exception) {
            Log.e(TAG, "initIntentData: " + exception);
        }
        try {
            prefsEditor.putString("suffleTracks", gson.toJson((Object) PlayerlocalTracksList)).commit();
        } catch (Exception exceptionxception) {
        }
        playerLocalTrack = PlayerlocalTracksList.get(positionLocalTrack);
        HomeActivity.txt1.setText(playerLocalTrack.getTitle());
        HomeActivity.txt2.setText(playerLocalTrack.getArtist());
        txt1.setText(playerLocalTrack.getTitle());
        txt2.setText(playerLocalTrack.getArtist());
        if (!fromControl.equals("true")) {
            try {
                mMediaPlayer.pause();
                mMediaPlayer.stop();
                mMediaPlayer.reset();
            } catch (Exception exception2) {
                Log.e(TAG, "initIntentData: " + exception2);
            }
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }
            try {
                mMediaPlayer.setDataSource(playerLocalTrack.getPath());
                mMediaPlayer.prepareAsync();
                Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                intent.setAction(Constants.ACTION.PLAY_ACTION);
                startService(intent);

            } catch (IOException exception3) {
                exception3.printStackTrace();
            }
        } else if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(playerLocalTrack.getPath());
                mMediaPlayer.prepareAsync();
                Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                intent.setAction(Constants.ACTION.PLAY_ACTION);
                startService(intent);
            } catch (IOException exception4) {
                exception4.printStackTrace();
            }
        } else if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition());
            mMediaPlayer.start();
            setTime();
            setupVisualizerFxAndUI();
            Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
            intent.setAction(Constants.ACTION.PLAY_ACTION);
            startService(intent);

        } else {
            try {
                mMediaPlayer.setDataSource(playerLocalTrack.getPath());
                mMediaPlayer.prepareAsync();
                Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                intent.setAction(Constants.ACTION.PLAY_ACTION);
                startService(intent);

            } catch (IOException exception5) {
                exception5.printStackTrace();
            }
        }
        if (isShuffleEnable) {
            shuffle_controller.setImageResource(R.drawable.shuffle_presed);
        } else {
            shuffle_controller.setImageResource(R.drawable.shuffle_unpresed);
        }
        if (!isTimerSet) {
            txtTimer.setVisibility(View.GONE);
        }
        if (repeatEnabled) {
            repeat_controller.setImageResource(R.drawable.repeat_presed);
        } else if (repeatOnceEnabled) {
            repeat_controller.setImageResource(R.drawable.repeat_presed1);
        } else {
            repeat_controller.setImageResource(R.drawable.repeat_unpresed);
        }




    }

    public void checkFav() {
        UnifiedTrackModel unifiedTrackModel = new UnifiedTrackModel(true, playerLocalTrack, (TrackModel) null);
        if (favouriteTracks == null) {
            favouriteTracks = new FavouriteModel();
        }
        int i2 = 0;
        while (true) {
            if (i2 >= favouriteTracks.getFavourite().size()) {
                break;
            }
            UnifiedTrackModel unifiedTrackModel1 = favouriteTracks.getFavourite().get(i2);
            if (!unifiedTrackModel.getType() || !unifiedTrackModel1.getType()) {
                if (!unifiedTrackModel.getType() && !unifiedTrackModel1.getType() && unifiedTrackModel.getStreamTrack().getTitle().equals(unifiedTrackModel1.getStreamTrack().getTitle())) {
                    isFav = true;
                    break;
                }
            } else if (unifiedTrackModel.getLocalTrack().getTitle().equals(unifiedTrackModel1.getLocalTrack().getTitle())) {
                isFav = true;
                break;
            }
            i2++;
        }
        if (isFav) {
            favouriteIcon.setImageResource(R.drawable.favourites_presed);
        } else {
            favouriteIcon.setImageResource(R.drawable.favourites_unpresed);
        }
    }

    public void addToFavourite() {
        UnifiedTrackModel unifiedTrackModel = new UnifiedTrackModel(true, playerLocalTrack, (TrackModel) null);

        try {
            if (favouriteTracks == null) {
                favouriteTracks = new FavouriteModel();
                favouriteTracks.getFavourite().add(unifiedTrackModel);
                return;
            }
            favouriteTracks.getFavourite().add(unifiedTrackModel);
        } catch (Exception exceptionxception) {
        }
    }

    public void removeFromFavourite() {
        UnifiedTrackModel unifiedTrackModel = new UnifiedTrackModel(true, playerLocalTrack, (TrackModel) null);
        for (int i2 = 0; i2 < favouriteTracks.getFavourite().size(); i2++) {
            UnifiedTrackModel unifiedTrackModel1 = favouriteTracks.getFavourite().get(i2);
            if (!unifiedTrackModel.getType() || !unifiedTrackModel1.getType()) {
                if (!unifiedTrackModel.getType() && !unifiedTrackModel1.getType() && unifiedTrackModel.getStreamTrack().getTitle().equals(unifiedTrackModel1.getStreamTrack().getTitle())) {
                    favouriteTracks.getFavourite().remove(i2);
                    return;
                }
            } else if (unifiedTrackModel.getLocalTrack().getTitle().equals(unifiedTrackModel1.getLocalTrack().getTitle())) {
                favouriteTracks.getFavourite().remove(i2);
                return;
            }
        }
    }



    public void setTime() {
        durationInMilliSec = (int) playerLocalTrack.getDuration();
        temp = getTime(durationInMilliSec);
        totalTime.setText(((String) temp.first) + ":" + ((String) temp.second));
        progressBar.setMax(durationInMilliSec);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    temp = getTime(mMediaPlayer.getCurrentPosition());
                    runOnUiThread(new Runnable() {
                       @Override
                        public void run() {
                            TextView textView = currTime;
                            textView.setText(((String) temp.first) + ":" + ((String) temp.second));
                        }
                    });
                    progressBar.setProgress(mMediaPlayer.getCurrentPosition());
                } catch (Exception exception) {
                    Log.e("MEDIA", exception.getMessage() + ":");
                }
            }
        }, 0, 50);
    }

    public Pair<String, String> getTime(int i2) {
        int i3 = i2 / 1000;
        int i4 = i3 / 60;
        int i5 = i3 % 60;
        String valueOf = String.valueOf(i4);
        String valueOf2 = String.valueOf(i5);
        if (i5 < 10) {
            valueOf2 = "0" + valueOf2;
        }
        return Pair.create(valueOf, valueOf2);
    }

    public void addTabsToViewpager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        visualizerFragment = new VisualizerFragment();
        equalizerFragment = new EqualizerFragment();
        effectsFragment = new EffectsFragment();
        environmentFragment = new EnvironmentFragment();
        viewPagerAdapter.addFragment(visualizerFragment);
        viewPagerAdapter.addFragment(equalizerFragment);
        viewPagerAdapter.addFragment(effectsFragment);
        viewPagerAdapter.addFragment(environmentFragment);
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (repeatEnabled) {
            repeat_controller.setImageResource(R.drawable.repeat_presed);
            playNext();
        } else if (repeatOnceEnabled) {
            repeat_controller.setImageResource(R.drawable.repeat_presed1);
            mMediaPlayer.reset();
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(playerLocalTrack.getPath());
                mMediaPlayer.prepare();
                controller.setImageResource(R.drawable.play);
                VisualizerFragment.rotateImage.clearAnimation();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            progressBar.setProgress(0);
            mMediaPlayer.start();
            Intent intent = new Intent(this, PlayerService.class);
            intent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            intent.putExtra("title", playerLocalTrack.getTitle());
            intent.putExtra("subtitle", playerLocalTrack.getArtist());
            startService(intent);
            new VisualizerFragment().startVisualizer();
            setTime();
            setupVisualizerFxAndUI();
        } else {
            repeat_controller.setImageResource(R.drawable.repeat_unpresed);
            playNext();
        }
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> fragmentList = new ArrayList();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }


        public void addFragment(Fragment fragment) {
            fragmentList.add(fragment);
        }
    }

    public static void setupVisualizerFxAndUI() {
        try {
            mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
            mEqualizer.setEnabled(true);
            try {
                bassBoost = new BassBoost(0, mMediaPlayer.getAudioSessionId());
                bassBoost.setEnabled(false);
                BassBoost.Settings settings = new BassBoost.Settings(bassBoost.getProperties().toString());
                settings.strength = 52;
                bassBoost.setProperties(settings);
                mMediaPlayer.setAuxEffectSendLevel(1.0f);
                presetReverb = new PresetReverb(1, mMediaPlayer.getAudioSessionId());
                presetReverb.setPreset(PRESET_NONE);
                presetReverb.setEnabled(true);
                mMediaPlayer.setAuxEffectSendLevel(1.0f);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } catch (Exception exception2) {
            exception2.printStackTrace();
        }
        if (isEqualizerEnabled) {
            try {
                bassBoost.setEnabled(true);
                BassBoost.Settings settings2 = new BassBoost.Settings(bassBoost.getProperties().toString());
                if (bassStrength == -1) {
                    settings2.strength = 52;
                } else {
                    settings2.strength = bassStrength;
                }
                bassBoost.setProperties(settings2);
                mMediaPlayer.setAuxEffectSendLevel(1.0f);
                if (reverbPreset == -1) {
                    presetReverb.setPreset(PRESET_NONE);
                } else {
                    presetReverb.setPreset(reverbPreset);
                }
                presetReverb.setEnabled(true);
                mMediaPlayer.setAuxEffectSendLevel(1.0f);
            } catch (Exception exception3) {
                exception3.printStackTrace();
            }
        }
        if (isEqualizerEnabled && isEqualizerReloaded) {
            try {
                isEqualizerEnabled = true;
                int i2 = presetPos;
                if (i2 != 0) {
                    mEqualizer.usePreset((short) (i2 - 1));
                } else {
                    for (short s = 0; s < 5; s = (short) (s + 1)) {
                        mEqualizer.setBandLevel(s, (short) seekbarPos[s]);
                    }
                }
                if (!(bassStrength == -1 || reverbPreset == -1)) {
                    bassBoost.setEnabled(true);
                    bassBoost.setStrength(bassStrength);
                    presetReverb.setEnabled(true);
                    presetReverb.setPreset(reverbPreset);
                }
                mMediaPlayer.setAuxEffectSendLevel(1.0f);
            } catch (Exception exception4) {
                exception4.printStackTrace();
            }
        }
    }

    public void playNext() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if(repeatEnabled){
                    if (positionLocalTrack != PlayerlocalTracksList.size() - 1) {
                        playerLocalTrack = PlayerlocalTracksList.get(positionLocalTrack);
                        txt1.setText(playerLocalTrack.getTitle());
                        txt2.setText(playerLocalTrack.getArtist());
                        mMediaPlayer.pause();
                        mMediaPlayer.stop();
                        mMediaPlayer.reset();
                        mMediaPlayer = new MediaPlayer();
                        try {
                            mMediaPlayer.setDataSource(playerLocalTrack.getPath());
                            mMediaPlayer.prepareAsync();
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                mMediaPlayer.setOnCompletionListener(PlayerActivity.this);
                                mMediaPlayer.start();
                            }
                        });
                        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                            @Override
                            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                                return true;
                            }
                        });
                        Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                        intent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                        intent.putExtra("title", playerLocalTrack.getTitle());
                        intent.putExtra("subtitle", playerLocalTrack.getArtist());
                        startService(intent);
                        new VisualizerFragment().startVisualizer();
                        setTime();
                        setupVisualizerFxAndUI();
                        ArrayList arrayList = new ArrayList();
                        arrayList.clear();
                        for (int i = 0; i < favouriteTracks.getFavourite().size(); i++) {
                            arrayList.add(favouriteTracks.getFavourite().get(i).getLocalTrack().getTitle());
                        }
                        if (arrayList.contains(playerLocalTrack.getTitle())) {
                            favouriteIcon.setImageResource(R.drawable.favourites_presed);
                            isFav = true;
                        } else {
                            favouriteIcon.setImageResource(R.drawable.favourites_unpresed);
                            isFav = false;
                        }
                        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                            }

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                                temp = getTime(i);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        currTime.setText(((String) temp.first) + ":" + ((String) temp.second));
                                    }
                                });
                            }


                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                mMediaPlayer.seekTo(seekBar.getProgress());
                                if (mMediaPlayer.isPlaying()) {
                                    mMediaPlayer.start();
                                }
                            }
                        });
                        return;
                    }
                }
                else if (positionLocalTrack != PlayerlocalTracksList.size() - 1) {
                    positionLocalTrack++;
                    playerLocalTrack = PlayerlocalTracksList.get(positionLocalTrack);
                    txt1.setText(playerLocalTrack.getTitle());
                    txt2.setText(playerLocalTrack.getArtist());
                    mMediaPlayer.pause();
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                    mMediaPlayer = new MediaPlayer();
                    try {
                        mMediaPlayer.setDataSource(playerLocalTrack.getPath());
                        mMediaPlayer.prepareAsync();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mMediaPlayer.setOnCompletionListener(PlayerActivity.this);
                            mMediaPlayer.start();
                        }
                    });
                    mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                            return true;
                        }
                    });
                    Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                    intent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                    intent.putExtra("title", playerLocalTrack.getTitle());
                    intent.putExtra("subtitle", playerLocalTrack.getArtist());
                    startService(intent);
                    new VisualizerFragment().startVisualizer();
                    setTime();
                    setupVisualizerFxAndUI();
                    ArrayList arrayList = new ArrayList();
                    arrayList.clear();
                    if (favouriteTracks != null){
                        for (int i = 0; i < favouriteTracks.getFavourite().size(); i++) {
                            arrayList.add(favouriteTracks.getFavourite().get(i).getLocalTrack().getTitle());
                        }
                    }

                    if (arrayList.contains(playerLocalTrack.getTitle())) {
                        favouriteIcon.setImageResource(R.drawable.favourites_presed);
                        isFav = true;
                    } else {
                        favouriteIcon.setImageResource(R.drawable.favourites_unpresed);
                        isFav = false;
                    }
                    progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                            temp = getTime(i);
                            runOnUiThread(new Runnable() {
                                 @Override
                                public void run() {
                                    currTime.setText(((String) temp.first) + ":" + ((String) temp.second));
                                }
                            });
                        }


                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            mMediaPlayer.seekTo(seekBar.getProgress());
                            if (mMediaPlayer.isPlaying()) {
                                mMediaPlayer.start();
                            }
                        }
                    });
                    return;
                }
                Toast.makeText(PlayerActivity.this, "Last Song", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void playPrevious() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (positionLocalTrack != 0) {
                    positionLocalTrack--;
                    playerLocalTrack = PlayerlocalTracksList.get(positionLocalTrack);
                    txt1.setText(playerLocalTrack.getTitle());
                    txt2.setText(playerLocalTrack.getArtist());
                    mMediaPlayer.reset();
                    mMediaPlayer = new MediaPlayer();
                    try {
                        mMediaPlayer.setDataSource(playerLocalTrack.getPath());
                        mMediaPlayer.prepareAsync();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mMediaPlayer.setOnCompletionListener(PlayerActivity.this);
                            mMediaPlayer.start();
                        }
                    });
                    mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                            return true;
                        }
                    });
                    Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                    intent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                    intent.putExtra("title", playerLocalTrack.getTitle());
                    intent.putExtra("subtitle", playerLocalTrack.getArtist());
                    startService(intent);
                    new VisualizerFragment().startVisualizer();
                    setTime();
                    setupVisualizerFxAndUI();
                    ArrayList arrayList = new ArrayList();
                    arrayList.clear();
                    for (int i = 0; i < favouriteTracks.getFavourite().size(); i++) {
                        arrayList.add(favouriteTracks.getFavourite().get(i).getLocalTrack().getTitle());
                    }
                    if (arrayList.contains(playerLocalTrack.getTitle())) {
                        favouriteIcon.setImageResource(R.drawable.favourites_presed);
                        isFav = true;
                    } else {
                        favouriteIcon.setImageResource(R.drawable.favourites_unpresed);
                        isFav = false;
                    }
                    progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                            temp = getTime(i);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    currTime.setText(((String) temp.first) + ":" + ((String) temp.second));
                                }
                            });
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            mMediaPlayer.seekTo(seekBar.getProgress());
                            if (mMediaPlayer.isPlaying()) {
                                mMediaPlayer.start();
                            }
                        }
                    });
                    return;
                }
                Toast.makeText(PlayerActivity.this, "First Song", Toast.LENGTH_LONG).show();
            }
        });
    }
}
