package com.lakshitasuman.musicstation.ringtone;

import static com.lakshitasuman.musicstation.ringtone.PlayRingtoneActivity.getMIMEType;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.musicplayer.PlayerActivity;
import com.lakshitasuman.musicstation.radio.service.PauseReason;
import com.lakshitasuman.musicstation.radio.service.PlayerServiceUtil;
import com.lakshitasuman.musicstation.ringtone.model.SongList;

import com.lakshitasuman.musicstation.ringtone.utils.AdsUtils;
import com.lakshitasuman.musicstation.ringtone.utils.TimeUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class PlayToneActivity extends Activity {
    String TAG = "PlayAllSong";
    private FrameLayout adContainerView;
    TextView cancle;
    TextView current;
    long currentPos;
    LinearLayout delete;
    TextView done;
    RelativeLayout lay_back;
    MediaPlayer mp;
    ImageView music_logo;
    String pathSong = null;
    ImageView pause;
    public Dialog ringtoneDialog;
    SeekBar seekBar;
    LinearLayout setRingtone;
    LinearLayout share;
    Uri shareUri;
    ArrayList<SongList> songList = new ArrayList<>();
    TextView text;
    TextView total;
    long totalDuration;
    Uri uri;
    String videoPath = "";



    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_play_tone);
        AdsUtils.initAd(this);
        AdsUtils.loadLargeBannerAd(this,findViewById(R.id.adsView));

        ui();
        lay_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void ui() {
       mp = new MediaPlayer();
       Intent intent = getIntent();
       pathSong = intent.getStringExtra("audiourl");
       videoPath = intent.getStringExtra("audiourl");
       uri = Uri.parse(pathSong);
       seekBar = (SeekBar) findViewById(R.id.all_song_timeline);
       current = (TextView) findViewById(R.id.all_time_current);
       total = (TextView) findViewById(R.id.all_time);
       pause = (ImageView) findViewById(R.id.pause);
       share = (LinearLayout) findViewById(R.id.share);
       music_logo = (ImageView) findViewById(R.id.music_logo);
       setRingtone = (LinearLayout) findViewById(R.id.set_ringtone);
       delete = (LinearLayout) findViewById(R.id.delete);
       text = (TextView) findViewById(R.id.text);
        lay_back = (RelativeLayout) findViewById(R.id.lay_back);
        playSong();
        setPause();
        setRingtone();
        shareSong();
        deleteSong();
       mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
           @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                pause.setImageResource(R.drawable.play);
            }
        });
    }


    public void playSong() {
        try {
            String str =TAG;
            Log.d(str, "playSong: " +pathSong);
           mp.reset();
           mp.setDataSource(pathSong);
           mp.prepare();
           mp.start();
           pause.setImageResource(R.drawable.pause);
           mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
               @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    setSongProgress();
                }
            });
           mp.pause();
           pause.setImageResource(R.drawable.play);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void shareSong() {
       share.setOnClickListener(new View.OnClickListener() {
         @Override
            public void onClick(View view) {
                Uri parse = Uri.parse(pathSong);
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("audio/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra("android.intent.extra.STREAM", parse);
                startActivity(intent);
            }
        });
    }

    public void deleteSong() {
       delete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                clickMe();
            }
        });
    }

    public void clickMe() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.delete_dialog);
        done = (TextView) dialog.findViewById(R.id.done);
        cancle = (TextView) dialog.findViewById(R.id.cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
       done.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
                File file = new File(pathSong);
                if (mp.isPlaying()) {
                    mp.stop();
                }
                if (file.exists()) {
                    file.delete();
                     finish();
                }
            }
        });
        dialog.show();
    }

    public void setPause() {
       pause.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
               if (PlayerServiceUtil.isPlaying()){
                   PlayerServiceUtil.pause(PauseReason.NONE);
               }
               if(PlayerActivity.mMediaPlayer != null){
                   if (PlayerActivity.mMediaPlayer.isPlaying()){
                       PlayerActivity.mMediaPlayer.pause();
                   }
               }

               if (mp.isPlaying()) {
                    mp.pause();
                    pause.setImageResource(R.drawable.play);
                    return;
                }
                mp.start();
                pause.setImageResource(R.drawable.pause);
            }
        });
    }

    private void setRingtone() {
       setRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRingtoneDailog();
            }
        });
    }

    public void setSongProgress() {
       totalDuration = (long)mp.getDuration();
       currentPos = (long)mp.getCurrentPosition();
       total.setText(timeConversion(totalDuration));
       current.setText(timeConversion(currentPos));
       seekBar.setMax((int)totalDuration);
       final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
           @Override
            public void run() {
                try {
                    currentPos = (long) mp.getCurrentPosition();
                    current.setText(timeConversion(currentPos));
                    seekBar.setProgress((int) currentPos);
                    handler.postDelayed(this, 10);
                } catch (IllegalStateException exception) {
                    exception.printStackTrace();
                }
            }
        }, 10);
       seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentPos = (long) seekBar.getProgress();
                mp.seekTo((int) currentPos);
            }
        });
    }

    public String timeConversion(long j) {
        int i = (int) j;
        int i2 = i / TimeUtils.MilliSeconds.ONE_HOUR;
        int i3 = (i / TimeUtils.MilliSeconds.ONE_MINUTE) % TimeUtils.MilliSeconds.ONE_MINUTE;
        int i4 = (i % TimeUtils.MilliSeconds.ONE_MINUTE) / 1000;
        if (i2 > 0) {
            return String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4)});
        }
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i4)});
    }


    public void setRingtoneDailog() {
        ringtoneDialog = new Dialog(this);
        ringtoneDialog.getWindow().requestFeature(1);
        ringtoneDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        ringtoneDialog.setContentView(R.layout.set_ringtone_dialog);
        ((LinearLayout)ringtoneDialog.findViewById(R.id.default_ringtone)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetAsRingtoneAndroid(new File(pathSong));
                ringtoneDialog.dismiss();
            }
        });
        ((LinearLayout)ringtoneDialog.findViewById(R.id.one_contact)).setOnClickListener(new View.OnClickListener() {
         @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListAllContactActivity.class);
                intent.putExtra("song path", pathSong);
                startActivity(intent);
                ringtoneDialog.dismiss();
            }
        });
       ringtoneDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
           @Override
           public void onCancel(DialogInterface dialogInterface) {
            }
        });
       ringtoneDialog.show();
    }



    public void SetAsRingtoneAndroid(File file) {
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
            }
            RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE, newUri);
            Toast.makeText(this, "Ringtone set", Toast.LENGTH_SHORT).show();
        }
        }



        @Override
      public void onBackPressed() {
        if (mp.isPlaying()) {
           mp.stop();
           pause.setImageResource(R.drawable.play);
        }
        finish();
    }






}
