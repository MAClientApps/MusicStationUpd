package com.lakshitasuman.musicstation.radio.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.KeyEvent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.lakshitasuman.musicstation.radio.IPlayerService;
import com.lakshitasuman.musicstation.radio.MainRadioHelper;
import com.lakshitasuman.musicstation.radio.station.DataRadioStation;
import com.lakshitasuman.musicstation.radio.utils.GetRealLinkAndPlayTask;

public class MediaSessionCallback extends MediaSessionCompat.Callback {
    public static final String BROADCAST_PLAY_STATION_BY_ID = "PLAY_STATION_BY_ID";
    public static final String EXTRA_STATION_ID = "STATION_ID";
    public static final String ACTION_PLAY_STATION_BY_UUID = "PLAY_STATION_BY_UUID";
    public static final String EXTRA_STATION_UUID = "STATION_UUID";

    private Context context;
    private IPlayerService playerService;

    public MediaSessionCallback(Context context, IPlayerService playerService) {
        this.context = context;
        this.playerService = playerService;
    }

    @Override
    public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
        final KeyEvent event = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

        if (event.getKeyCode() == KeyEvent.KEYCODE_HEADSETHOOK) {
            if (event.getAction() == KeyEvent.ACTION_UP && !event.isLongPress()) {
                try {
                    if (playerService.isPlaying()) {
                        playerService.Pause(PauseReason.USER);
                    } else {
                        playerService.Resume();
                    }
                } catch (RemoteException exception) {
                    exception.printStackTrace();
                }
            }
            return true;
        } else {
            return super.onMediaButtonEvent(mediaButtonEvent);
        }
    }

    @Override
    public void onPause() {
        try {
            playerService.Pause(PauseReason.USER);
        } catch (RemoteException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onPlay() {
        try {
            playerService.Resume();
        } catch (RemoteException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onSkipToNext() {
        try {
            playerService.SkipToNext();
        } catch (RemoteException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onSkipToPrevious() {
        try {
            playerService.SkipToPrevious();
        } catch (RemoteException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        try {
            playerService.Stop();
        } catch (RemoteException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onPlayFromMediaId(String mediaId, Bundle extras) {
        final String stationId = RadioMainBrowser.stationIdFromMediaId(mediaId);

        if (!stationId.isEmpty()) {
            Intent intent = new Intent(BROADCAST_PLAY_STATION_BY_ID);
            intent.putExtra(EXTRA_STATION_ID, stationId);

            LocalBroadcastManager bm = LocalBroadcastManager.getInstance(context);
            bm.sendBroadcast(intent);
        }
    }

    @Override
    public void onPlayFromSearch(String query, Bundle extras) {
        DataRadioStation station = ((MainRadioHelper) context.getApplicationContext()).getFavouriteManager().getBestNameMatch(query);
        if (station == null)
           station = ((MainRadioHelper) context.getApplicationContext()).getHistoryManager().getBestNameMatch(query);
        if (station != null) {
            GetRealLinkAndPlayTask playTask = new GetRealLinkAndPlayTask(context, station, playerService);
            playTask.execute();
        }
    }
}