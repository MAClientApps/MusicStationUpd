package com.lakshitasuman.musicstation.radio;

import com.lakshitasuman.musicstation.radio.service.PauseReason;
import com.lakshitasuman.musicstation.radio.station.DataRadioStation;
import com.lakshitasuman.musicstation.radio.station.live.StreamLiveInfo;
import com.lakshitasuman.musicstation.radio.station.live.ShoutcastInfo;
import com.lakshitasuman.musicstation.radio.players.PlayState;
import com.lakshitasuman.musicstation.radio.players.selector.PlayerType;
import android.support.v4.media.session.MediaSessionCompat;

interface IPlayerService
{
void SetStation(in DataRadioStation station);
void Play(boolean isAlarm);
void Pause(in PauseReason pauseReason);
void Resume();
void Stop();
void SkipToNext();
void SkipToPrevious();
void addTimer(int secondsAdd);
void clearTimer();
long getTimerSeconds();
String getCurrentStationID();
DataRadioStation getCurrentStation();
StreamLiveInfo getMetadataLive();
ShoutcastInfo getShoutcastInfo();
MediaSessionCompat.Token getMediaSessionToken();
boolean isPlaying();
PlayState getPlayerState();
void startRecording();
void stopRecording();
boolean isRecording();
String getCurrentRecordFileName();
long getTransferredBytes();
long getBufferedSeconds();
long getLastPlayStartTime();
boolean getIsHls();
PauseReason getPauseReason();
boolean isNotificationActive();

void enableMPD(String hostname, int port);
void disableMPD();

void warnAboutMeteredConnection(in PlayerType playerType);
}
