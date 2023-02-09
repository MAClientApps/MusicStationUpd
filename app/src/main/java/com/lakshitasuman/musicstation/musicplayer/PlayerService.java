package com.lakshitasuman.musicstation.musicplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.musicplayer.fragment.VisualizerFragment;
import com.lakshitasuman.musicstation.radio.service.PauseReason;
import com.lakshitasuman.musicstation.radio.service.PlayerServiceUtil;

public class PlayerService extends Service {
    static final  boolean $assertionsDisabled = false;
    private static final String TAG = "SERVICE";
    public static Bitmap bit;
    public static CountDownTimer timer;
    Notification notification;
    RemoteViews notificationView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if (PlayerActivity.mMediaPlayer == null) {
            PlayerActivity.mMediaPlayer = new MediaPlayer();
        }
        notificationView = new RemoteViews(getPackageName(), R.layout.status_bar);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i2) {
        Log.e(TAG, "onStartCommand: ");
        if (PlayerActivity.mMediaPlayer == null) {
            PlayerActivity.mMediaPlayer = new MediaPlayer();
        }
        try {

            if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
                HomeActivity.llControl.setVisibility(View.VISIBLE);
                if (PlayerServiceUtil.isPlaying()){
                    PlayerServiceUtil.pause(PauseReason.NONE);
                }
                String stringExtra = intent.getStringExtra("title");
                String stringExtra2 = intent.getStringExtra("subtitle");
                notificationView.setTextViewText(R.id.status_bar_track_name, stringExtra);
                notificationView.setTextViewText(R.id.status_bar_artist_name, stringExtra2);
                HomeActivity.txt1.setText(stringExtra);
                HomeActivity.txt2.setText(stringExtra2);
                if (PlayerActivity.controller != null) {
                    PlayerActivity.controller.setImageResource(R.drawable.pause);
                }
                HomeActivity.btnPlayPause.setImageResource(R.drawable.pause);
                notificationView.setImageViewResource(R.id.status_bar_play, R.drawable.pause);
                if (bit == null) {
                   notificationView.setImageViewResource(R.id.status_bar_icon, R.drawable.song_images);
                } else {
                   notificationView.setImageViewBitmap(R.id.status_bar_icon, bit);
                }
                showNotification();
            }
            else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
                if (PlayerActivity.mMediaPlayer.isPlaying()) {
                    PlayerActivity.mMediaPlayer.pause();
                    if (PlayerActivity.controller != null) {
                        VisualizerFragment.rotateImage.clearAnimation();
                        PlayerActivity.controller.setImageResource(R.drawable.play);
                    }
                    HomeActivity.btnPlayPause.setImageResource(R.drawable.play);
                    notificationView.setImageViewResource(R.id.status_bar_play, R.drawable.play);
                    startForeground(101,notification);
                } else {
                    if (PlayerServiceUtil.isPlaying()){
                        PlayerServiceUtil.pause(PauseReason.NONE);
                    }
                    PlayerActivity.mMediaPlayer.start();
                    if (PlayerActivity.controller != null) {
                        VisualizerFragment.rotateImage.startAnimation(VisualizerFragment.rotate);
                        PlayerActivity.controller.setImageResource(R.drawable.pause);
                    }
                    HomeActivity.btnPlayPause.setImageResource(R.drawable.pause);
                    showNotification();
                }

            } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
                HomeActivity.llControl.setVisibility(View.GONE);
                stopForeground(true);
                stopSelf();
                PlayerActivity.mMediaPlayer.stop();
                PlayerActivity.mMediaPlayer.reset();
                try {
                    PlayerActivity.back.performClick();
                } catch (Exception exceptionxception) {
                }
            } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
                PlayerActivity.previous.performClick();
            } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
                PlayerActivity.next.performClick();
            } else if (intent.getAction().equals(Constants.ACTION.STARTTIMER)) {
                String stringExtra3 = intent.getStringExtra("hour");
                counter((long) ((Integer.parseInt(intent.getStringExtra("minute")) + (Integer.parseInt(stringExtra3) * 60)) * 60 * 1000));
            }
            else if(intent.getAction().equals(Constants.ACTION.NOTIFICATION)){
                showNotification();
            }
        } catch (Exception exception) {
            Log.e(TAG, "onStartCommand: " + exception);
        }
        return START_STICKY;
    }

    private void showNotification() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setAction(Constants.ACTION.MAIN_ACTION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent activity = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        Intent intent2 = new Intent(this, PlayerService.class);
        intent2.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent service = PendingIntent.getService(this, 0, intent2, PendingIntent.FLAG_IMMUTABLE);
        Intent intent3 = new Intent(this, PlayerService.class);
        intent3.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent service2 = PendingIntent.getService(this, 0, intent3, PendingIntent.FLAG_IMMUTABLE);
        Intent intent4 = new Intent(this, PlayerService.class);
        intent4.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent service3 = PendingIntent.getService(this, 0, intent4, PendingIntent.FLAG_IMMUTABLE);
        Intent intent5 = new Intent(this, PlayerService.class);
        intent5.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent service4 = PendingIntent.getService(this, 0, intent5, PendingIntent.FLAG_IMMUTABLE);
        notificationView.setOnClickPendingIntent(R.id.status_bar_play, service);
        notificationView.setOnClickPendingIntent(R.id.status_bar_collapse, service2);
        notificationView.setOnClickPendingIntent(R.id.status_bar_previous, service3);
        notificationView.setOnClickPendingIntent(R.id.status_bar_next, service4);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("Channel123", "Channel", NotificationManager.IMPORTANCE_NONE);
            notificationChannel.setLightColor(-16776961);
            notificationChannel.setLockscreenVisibility(0);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
             notification = new NotificationCompat.Builder(this, "Channel123").setOngoing(true).setSmallIcon(R.mipmap.ic_launcher).setCustomBigContentView(this.notificationView).setContentIntent(activity).setContent(this.notificationView).setPriority(4).setCategory(NotificationCompat.CATEGORY_SERVICE).build();
             startForeground(101,notification);
            return;
        }
       notification = new Notification.Builder(this).build();
       notification.contentView =notificationView;
       notification.flags = 2;
       notification.icon = R.mipmap.ic_launcher;
       notification.contentIntent = activity;
       notification.bigContentView =notificationView;
       startForeground(101,notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent serviceIntent = new Intent(this, PlayerService.class);
        startService(serviceIntent);
    }

    @Override
    public void onTaskRemoved(Intent intent) {
        super.onTaskRemoved(intent);
        startService(new Intent(this, PlayerService.class));
    }


    public void counter(long j) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(j, 1000) {
            @Override
            public void onTick(long j) {
                int i = ((int) (j / 1000)) % 60;
                int i2 = (int) ((j / 60000) % 60);
                int i3 = (int) ((j / 3600000) % 24);
                if (PlayerActivity.txtTimer != null) {
                    PlayerActivity.txtTimer.setText("Stop Music " + String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i2), Integer.valueOf(i)}));
                }
            }
            @Override
            public void onFinish() {
                PlayerActivity.isTimerSet = false;
                Intent intent = new Intent(PlayerService.this, PlayerService.class);
                intent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                startService(intent);
            }
        };
        timer.start();
    }
}
