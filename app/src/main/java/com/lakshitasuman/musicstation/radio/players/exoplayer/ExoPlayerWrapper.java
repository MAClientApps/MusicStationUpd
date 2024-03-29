package com.lakshitasuman.musicstation.radio.players.exoplayer;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsListener;

import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.android.exoplayer2.metadata.icy.IcyInfo;
import com.google.android.exoplayer2.metadata.id3.Id3Frame;
import com.google.android.exoplayer2.source.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaLoadData;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.HttpDataSource;


import com.lakshitasuman.musicstation.BuildConfig;
import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.radio.Utils;
import com.lakshitasuman.musicstation.radio.players.PlayState;
import com.lakshitasuman.musicstation.radio.recording.RecordableListener;
import com.lakshitasuman.musicstation.radio.station.live.ShoutcastInfo;
import com.lakshitasuman.musicstation.radio.station.live.StreamLiveInfo;
import com.lakshitasuman.musicstation.radio.players.PlayerWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

public class ExoPlayerWrapper implements PlayerWrapper, IcyDataSource.IcyDataSourceListener, MetadataOutput {

    final private String TAG = "ExoPlayerWrapper";

    private SimpleExoPlayer player;
    private PlayListener stateListener;

    private String streamUrl;

    private DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

    private RecordableListener recordableListener;

    private long totalTransferredBytes;
    private long currentPlaybackTransferredBytes;

    private boolean isHls;
    private boolean isPlayingFlag;

    private Handler playerThreadHandler;

    private Context context;
    private MediaSource audioSource;

    private Runnable fullStopTask;

    private final BroadcastReceiver networkChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (fullStopTask != null && player != null && audioSource != null && Utils.hasAnyConnection(context)) {
                Log.i(TAG, "Regained connection. Resuming playback.");

                cancelStopTask();

                player.prepare(audioSource);
                player.setPlayWhenReady(true);
            }
        }
    };
    final class CustomLoadErrorHandlingPolicy extends DefaultLoadErrorHandlingPolicy {
        final int MIN_RETRY_DELAY_MS = 10;
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        // We need to read the retry delay here on each error again because the user might change
        // this value between retries and experiment with different vales to get the best result for
        // the specific situation. We also need to make sure that a sensible minimum value is chosen.
        int getSanitizedRetryDelaySettingsMs() {
            return Math.max(sharedPrefs.getInt("settings_retry_delay", 100), MIN_RETRY_DELAY_MS);
        }


        @Override
        public long getRetryDelayMsFor(LoadErrorInfo loadErrorInfo) {
            int retryDelay = getSanitizedRetryDelaySettingsMs();

            if (loadErrorInfo.exception instanceof HttpDataSource.InvalidContentTypeException) {
                stateListener.onPlayerError(R.string.error_play_stream);
                return C.TIME_UNSET; // Immediately surface error if we cannot play content type
            }

            if (!Utils.hasAnyConnection(context)) {
                int resumeWithinS = sharedPrefs.getInt("settings_resume_within", 60);
                if (resumeWithinS > 0) {
                    resumeWhenNetworkConnected();
                    retryDelay = 1000 * resumeWithinS + retryDelay;
                }
            }

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Providing retry delay of " + retryDelay + "ms " +
                        "for: data type " + loadErrorInfo.mediaLoadData.dataType  + ", " +
                        "load duration: " + loadErrorInfo.mediaLoadData.mediaEndTimeMs + "ms, " +
                        "error count: " + loadErrorInfo.errorCount + ", " +
                        "exception " + loadErrorInfo.exception.getClass() + ", " );
            }
            return retryDelay;
        }



        @Override
        public int getMinimumLoadableRetryCount(int dataType) {
            return sharedPrefs.getInt("settings_retry_timeout", 10) * 1000 / getSanitizedRetryDelaySettingsMs() + 1;
        }
    }

    @Override
    public void playRemote(@NonNull OkHttpClient httpClient, @NonNull String streamUrl, @NonNull Context context, boolean isAlarm) {
        // I don't know why, but it is still possible that streamUrl is null,
        // I still get exceptions from this from google
        if (streamUrl == null) {
            return;
        }
        if (!streamUrl.equals(this.streamUrl)) {
            currentPlaybackTransferredBytes = 0;
        }

        this.context = context;
        this.streamUrl = streamUrl;

        cancelStopTask();

        stateListener.onStateChanged(PlayState.PrePlaying);

        if (player != null) {
            player.stop();
        }

        if (player == null) {
            player = new SimpleExoPlayer.Builder(context).build();

            player.setAudioAttributes(new AudioAttributes.Builder().setContentType(C.CONTENT_TYPE_MUSIC)
                    .setUsage(isAlarm ? C.USAGE_ALARM : C.USAGE_MEDIA).build(),false);

            player.addListener(new ExoPlayerListener());
            player.addAnalyticsListener(new AnalyticEventListener());

        }

        if (playerThreadHandler == null) {
            playerThreadHandler = new Handler(Looper.getMainLooper());
        }

        isHls = Utils.urlIndicatesHlsStream(streamUrl);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        final int retryTimeout = prefs.getInt("settings_retry_timeout", 10);
        final int retryDelay = prefs.getInt("settings_retry_delay", 100);

        DataSource.Factory dataSourceFactory = new RadioDataSourceFactory(httpClient, bandwidthMeter, this, retryTimeout, retryDelay);
        // Produces Extractor instances for parsing the media data.
        if (!isHls) {
            audioSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .setLoadErrorHandlingPolicy(new CustomLoadErrorHandlingPolicy())
                    .createMediaSource(Uri.parse(streamUrl));
            player.prepare(audioSource);
        } else {
            audioSource = new HlsMediaSource.Factory(dataSourceFactory)
                    .setLoadErrorHandlingPolicy(new CustomLoadErrorHandlingPolicy())
                    .createMediaSource(Uri.parse(streamUrl));
            player.prepare(audioSource);
        }

        player.setPlayWhenReady(true);

        context.registerReceiver(networkChangedReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        // State changed will be called when audio session id is available.
    }

    @Override
    public void pause() {
        Log.i(TAG, "Pause. Stopping exoplayer.");

        cancelStopTask();

        if (player != null) {
            context.unregisterReceiver(networkChangedReceiver);
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    public void stop() {
        Log.i(TAG, "Stopping exoplayer.");

        cancelStopTask();

        if (player != null) {
            context.unregisterReceiver(networkChangedReceiver);
            player.stop();
            player.release();
            player = null;
        }

        stopRecording();
    }

    @Override
    public boolean isPlaying() {
        return player != null && isPlayingFlag;
    }

    @Override
    public long getBufferedMs() {
        if (player != null) {
            return (int) (player.getBufferedPosition() - player.getCurrentPosition());
        }

        return 0;
    }

    @Override
    public int getAudioSessionId() {
        if (player != null) {
            return player.getAudioSessionId();
        }
        return 0;
    }

    @Override
    public long getTotalTransferredBytes() {
        return totalTransferredBytes;
    }

    @Override
    public long getCurrentPlaybackTransferredBytes() {
        return currentPlaybackTransferredBytes;
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    @Override
    public void setVolume(float newVolume) {
        if (player != null) {
            player.setVolume(newVolume);
        }
    }

    @Override
    public void setStateListener(PlayListener listener) {
        stateListener = listener;
    }

    @Override
    public void onDataSourceConnected() {

    }

    @Override
    public void onDataSourceConnectionLost() {

    }

    @Override
    public void onMetadata(Metadata metadata) {
        if (BuildConfig.DEBUG) Log.d(TAG, "META: " + metadata.toString());
        if ((metadata != null)) {
            final int length = metadata.length();
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    final Metadata.Entry entry = metadata.get(i);
                    if (entry == null) {
                        continue;
                    }
                    if (entry instanceof IcyInfo) {
                        final IcyInfo icyInfo = ((IcyInfo) entry);
                        Log.d(TAG, "IcyInfo: " + icyInfo.toString());
                        if (icyInfo.title != null) {
                            Map<String, String> rawMetadata = new HashMap<String, String>() {{
                                put("StreamTitle", icyInfo.title);
                            }};
                            StreamLiveInfo streamLiveInfo = new StreamLiveInfo(rawMetadata);
                            onDataSourceStreamLiveInfo(streamLiveInfo);
                        }
                    } else if (entry instanceof IcyHeaders) {
                        final IcyHeaders icyHeaders = ((IcyHeaders) entry);
                        Log.d(TAG, "IcyHeaders: " + icyHeaders.toString());
                        onDataSourceShoutcastInfo(new ShoutcastInfo(icyHeaders));
                    } else if (entry instanceof Id3Frame) {
                        final Id3Frame id3Frame = ((Id3Frame) entry);
                        Log.d(TAG, "id3 metadata: " + id3Frame.toString());
                    }
                }
            }
        }
    }

    @Override
    public void onDataSourceConnectionLostIrrecoverably() {
        Log.i(TAG, "Connection lost irrecoverably.");
    }

    void resumeWhenNetworkConnected() {
        playerThreadHandler.post(() -> {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            int resumeWithin = sharedPref.getInt("settings_resume_within", 60);
            if (resumeWithin > 0) {
                Log.d(TAG, "Trying to resume playback within " + resumeWithin + "s.");

                // We want user to be able to paused during connection loss.
                // TODO: Find a way to notify user that even if current state is Playing
                //       we are actually trying to reconnect.
                //stateListener.onStateChanged(PlayState.Paused);

                cancelStopTask();

                fullStopTask = () -> {
                    stop();
                    stateListener.onPlayerError(R.string.giving_up_resume);

                    ExoPlayerWrapper.this.fullStopTask = null;
                };
                playerThreadHandler.postDelayed(fullStopTask, resumeWithin * 1000);

                stateListener.onPlayerWarning(R.string.warning_no_network_trying_resume);
            } else {
                stop();

                stateListener.onPlayerError(R.string.error_stream_reconnect_timeout);
            }
        });
    }

    @Override
    public void onDataSourceShoutcastInfo(@Nullable ShoutcastInfo shoutcastInfo) {
        stateListener.onDataSourceShoutcastInfo(shoutcastInfo, false);
    }

    @Override
    public void onDataSourceStreamLiveInfo(StreamLiveInfo streamLiveInfo) {
        stateListener.onDataSourceStreamLiveInfo(streamLiveInfo);
    }

    @Override
    public void onDataSourceBytesRead(byte[] buffer, int offset, int length) {
        totalTransferredBytes += length;
        currentPlaybackTransferredBytes += length;

        if (recordableListener != null) {
            recordableListener.onBytesAvailable(buffer, offset, length);
        }
    }

    @Override
    public boolean canRecord() {
        return player != null;
    }

    @Override
    public void startRecording(@NonNull RecordableListener recordableListener) {
        this.recordableListener = recordableListener;
    }

    @Override
    public void stopRecording() {
        if (recordableListener != null) {
            recordableListener.onRecordingEnded();
            recordableListener = null;
        }
    }

    @Override
    public boolean isRecording() {
        return recordableListener != null;
    }

    @Override
    public Map<String, String> getRecordNameFormattingArgs() {
        return null;
    }

    @Override
    public String getExtension() {
        return isHls ? "ts" : "mp3";
    }

    private void cancelStopTask() {
        if (fullStopTask != null) {
            playerThreadHandler.removeCallbacks(fullStopTask);
            fullStopTask = null;
        }
    }

    private class ExoPlayerListener implements Player.EventListener {

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            // Do nothing
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            // Do nothing
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            // Do nothing
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            // Do nothing
        }

        @Override
        public void onPlayerError(PlaybackException exceptionrror) {
            if (fullStopTask != null ) {
                stop();
                stateListener.onPlayerError(R.string.error_play_stream);
            }
        }


        @Override
        public void onPositionDiscontinuity(int reason) {
            // Do nothing
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            // Do nothing
        }
    }

    private class AnalyticEventListener implements AnalyticsListener {
        @Override
        public void onPlayerStateChanged(EventTime eventTime, boolean playWhenReady, int playbackState) {
            isPlayingFlag = playbackState == Player.STATE_READY || playbackState == Player.STATE_BUFFERING;

            switch (playbackState) {
                case Player.STATE_READY:
                    cancelStopTask();
                    stateListener.onStateChanged(PlayState.Playing);
                    break;
                case Player.STATE_BUFFERING:
                    stateListener.onStateChanged(PlayState.PrePlaying);
                    break;
            }

        }

        @Override
        public void onTimelineChanged(EventTime eventTime, int reason) {

        }

        @Override
        public void onPositionDiscontinuity(EventTime eventTime, int reason) {

        }

        @Override
        public void onSeekStarted(EventTime eventTime) {

        }

        @Override
        public void onSeekProcessed(EventTime eventTime) {

        }

        @Override
        public void onPlaybackParametersChanged(EventTime eventTime, PlaybackParameters playbackParameters) {

        }

        @Override
        public void onRepeatModeChanged(EventTime eventTime, int repeatMode) {

        }

        @Override
        public void onShuffleModeChanged(EventTime eventTime, boolean shuffleModeEnabled) {

        }

        @Override
        public void onLoadingChanged(EventTime eventTime, boolean isLoading) {

        }



        @Override
        public void onTracksChanged(EventTime eventTime, TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadStarted(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {

        }

        @Override
        public void onLoadCompleted(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {

        }

        @Override
        public void onLoadCanceled(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {

        }

        @Override
        public void onLoadError(EventTime eventTime, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException exceptionrror, boolean wasCanceled) {

        }

        @Override
        public void onDownstreamFormatChanged(EventTime eventTime, MediaLoadData mediaLoadData) {

        }

        @Override
        public void onUpstreamDiscarded(EventTime eventTime, MediaLoadData mediaLoadData) {

        }



        @Override
        public void onBandwidthEstimate(EventTime eventTime, int totalLoadTimeMs, long totalBytesLoaded, long bitrateEstimate) {

        }

        @Override
        public void onSurfaceSizeChanged(EventTime eventTime, int width, int height) {

        }

        @Override
        public void onMetadata(EventTime eventTime, Metadata metadata) {

        }

        @Override
        public void onDecoderEnabled(EventTime eventTime, int trackType, DecoderCounters decoderCounters) {

        }

        @Override
        public void onDecoderInitialized(EventTime eventTime, int trackType, String decoderName, long initializationDurationMs) {

        }

        @Override
        public void onDecoderInputFormatChanged(EventTime eventTime, int trackType, Format format) {

        }

        @Override
        public void onDecoderDisabled(EventTime eventTime, int trackType, DecoderCounters decoderCounters) {

        }



        @Override
        public void onAudioAttributesChanged(EventTime eventTime, AudioAttributes audioAttributes) {

        }

        @Override
        public void onVolumeChanged(EventTime eventTime, float volume) {

        }

        @Override
        public void onDroppedVideoFrames(EventTime eventTime, int droppedFrames, long elapsedMs) {

        }

        @Override
        public void onVideoSizeChanged(EventTime eventTime, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

        }



        @Override
        public void onDrmSessionAcquired(EventTime eventTime) {

        }

        @Override
        public void onDrmKeysLoaded(EventTime eventTime) {

        }

        @Override
        public void onDrmSessionManagerError(EventTime eventTime, Exception exceptionrror) {

        }

        @Override
        public void onDrmKeysRestored(EventTime eventTime) {

        }

        @Override
        public void onDrmKeysRemoved(EventTime eventTime) {

        }

        @Override
        public void onDrmSessionReleased(EventTime eventTime) {

        }
    }
}
