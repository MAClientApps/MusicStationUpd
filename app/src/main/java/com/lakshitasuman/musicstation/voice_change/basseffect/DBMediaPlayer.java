package com.lakshitasuman.musicstation.voice_change.basseffect;




import static com.un4seen.bass.BASS.BASS_MUSIC_DECODE;
import static com.un4seen.bass.BASS.BASS_TAG_MUSIC_NAME;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.lakshitasuman.musicstation.voice_change.utils.DBLog;
import com.lakshitasuman.musicstation.voice_change.utils.StringUtils;
import com.un4seen.bass.BASS;
import com.un4seen.bass.BASS_FX;
import com.un4seen.bass.BASSenc;

import java.nio.ByteBuffer;
import java.util.Locale;

public class DBMediaPlayer implements IDBMediaConstants {
    public static final int FLABUFLEN = 350;
    private static final String TAG = "DBMediaPlayer";
    public int currentPosition = 0;
    public int duration = 0;
    private boolean isPausing = false;
    private boolean isPlaying = false;
    public boolean isReverse;
    private int mChanPlay;
    public IDBMediaListener mDBMediaListener;
    private int mFxEQEffect;
    private int mFxEchoEffect;
    private int mFxFlangerEffect;
    private int mFxReverbEffect;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message message) {
            currentPosition = getChannelPosition();
            duration = getChannelLength();
            if (!isReverse) {
                if (currentPosition >= duration) {
                    removeMessages(0);
                    if (mDBMediaListener != null) {
                        mDBMediaListener.onMediaCompletion();
                        return;
                    }
                    return;
                }
                sendEmptyMessageDelayed(0, 50);
            } else if (currentPosition <= 0) {
                removeMessages(0);
                if (mDBMediaListener != null) {
                    mDBMediaListener.onMediaCompletion();
                }
            } else {
                sendEmptyMessageDelayed(0, 50);
            }
        }
    };
    private String mMediaPath;

    
    public DBMediaPlayer(String str) {
        mMediaPath = str;
    }



    public boolean prepareAudio() {
        if (StringUtils.isEmptyString(mMediaPath)) {
            return false;
        }
        if (mMediaPath.toLowerCase(Locale.getDefault()).endsWith(IDBMediaConstants.TYPE_MP3) || mMediaPath.toLowerCase(Locale.getDefault()).endsWith(IDBMediaConstants.TYPE_WAV) || mMediaPath.toLowerCase(Locale.getDefault()).endsWith(IDBMediaConstants.TYPE_OGG) || mMediaPath.toLowerCase(Locale.getDefault()).endsWith(IDBMediaConstants.TYPE_FLAC)) {
            initMedia();
            return true;
        }
        new Exception("DBMidiPlayer:can not support file format").printStackTrace();
        return false;
    }

    public void startAudio() {
        isPlaying = true;
        if (mChanPlay != 0) {
            BASS.BASS_ChannelPlay(mChanPlay, false);
        }
        mHandler.sendEmptyMessage(0);
    }

    public void setAudioPitch(int i) {

        if (mChanPlay != 0) {
            BASS.BASS_ChannelSetAttribute(mChanPlay, BASS.BASS_TAG_MUSIC_MESSAGE, (float) i);
        }
    }

    public void setAudioRate(float f) {
        int i = mChanPlay;
        if (i != 0) {
            BASS.BASS_ChannelSetAttribute(i, BASS_TAG_MUSIC_NAME, f);
        }
    }

    public void setAudioReverb(float[] fArr) {
        if (mChanPlay == 0) {
            return;
        }
        if (fArr != null) {
            if (mFxReverbEffect == 0) {
                mFxReverbEffect = BASS.BASS_ChannelSetFX(mChanPlay, 8, 0);
            }
            if (mFxReverbEffect != 0) {
                BASS.BASS_DX8_REVERB bass_dx8_reverb = new BASS.BASS_DX8_REVERB();
                BASS.BASS_FXGetParameters(mFxReverbEffect, bass_dx8_reverb);
                bass_dx8_reverb.fReverbMix = fArr[0];
                bass_dx8_reverb.fReverbTime = fArr[1];
                bass_dx8_reverb.fHighFreqRTRatio = fArr[2];
                BASS.BASS_FXSetParameters(mFxReverbEffect, bass_dx8_reverb);
                return;
            }
            return;
        }

        if (mFxReverbEffect != 0) {
            BASS.BASS_ChannelRemoveFX(mChanPlay, mFxReverbEffect);
            mFxReverbEffect = 0;
        }
    }

    public void setAudioEQ(float[] fArr) {

        if (mChanPlay == 0) {
            return;
        }
        if (fArr != null) {
            if (mFxEQEffect == 0) {
                mFxEQEffect = BASS.BASS_ChannelSetFX(mChanPlay, 7, 0);
            }
            if (mFxEQEffect != 0) {
                BASS.BASS_DX8_PARAMEQ bass_dx8_parameq = new BASS.BASS_DX8_PARAMEQ();
                BASS.BASS_FXGetParameters(mFxEQEffect, bass_dx8_parameq);
                bass_dx8_parameq.fCenter = fArr[0];
                bass_dx8_parameq.fBandwidth = fArr[1];
                bass_dx8_parameq.fGain = fArr[2];
                BASS.BASS_FXSetParameters(mFxEQEffect, bass_dx8_parameq);
                return;
            }
            return;
        }
        if (mFxEQEffect != 0) {
            BASS.BASS_ChannelRemoveFX(mChanPlay, mFxEQEffect);
            mFxEQEffect = 0;
        }
    }

    public void setAudioEcho(boolean audioEcho) {

        if (mChanPlay == 0) {
            return;
        }
        if (audioEcho) {
            if (mFxEchoEffect == 0) {
                mFxEchoEffect = BASS.BASS_ChannelSetFX(mChanPlay, 3, 0);
            }
            if (mFxEchoEffect != 0) {
                BASS.BASS_DX8_ECHO bass_dx8_echo = new BASS.BASS_DX8_ECHO();
                bass_dx8_echo.fLeftDelay = 1000.0f;
                bass_dx8_echo.fRightDelay = 1000.0f;
                bass_dx8_echo.fFeedback = 50.0f;
                BASS.BASS_FXSetParameters(mChanPlay, Integer.valueOf(mFxEchoEffect));
                return;
            }
            return;
        }
        if (mFxEchoEffect != 0) {
            BASS.BASS_ChannelRemoveFX(mChanPlay, mFxEchoEffect);
            mFxEchoEffect = 0;
        }
    }

    public void setFlangerEffect(boolean flangerEffect) {

        if (mChanPlay == 0) {
            return;
        }
        if (flangerEffect) {
            if (mFxFlangerEffect == 0) {
                mFxFlangerEffect = BASS.BASS_ChannelSetFX(mChanPlay, 4, 0);
            }

            DBLog.d(TAG, "===========>mFxFlangerEffect=" + mFxFlangerEffect);
            if (mFxFlangerEffect != 0) {
                BASS.BASS_DX8_FLANGER bass_dx8_flanger = new BASS.BASS_DX8_FLANGER();
                BASS.BASS_FXGetParameters(mFxFlangerEffect, bass_dx8_flanger);
                bass_dx8_flanger.fWetDryMix = 50.0f;
                bass_dx8_flanger.fDepth = 100.0f;
                bass_dx8_flanger.fFeedback = 80.0f;
                bass_dx8_flanger.fDelay = 10.0f;
                bass_dx8_flanger.lPhase = 3;
                BASS.BASS_FXSetParameters(mFxFlangerEffect, bass_dx8_flanger);
                return;
            }
            return;
        }
        if (mFxFlangerEffect != 0) {
            BASS.BASS_ChannelRemoveFX(mChanPlay, mFxFlangerEffect);
            mFxFlangerEffect = 0;
        }
    }
    public void pauseAudio() {
        if (!isPlaying) {
            new Exception(TAG + " pauseAudio:HanetMediaPlayer not init").printStackTrace();
            return;
        }
        isPausing = true;
        if (mChanPlay != 0) {
            BASS.BASS_ChannelPause(mChanPlay);
        }
    }

    public void releaseAudio() {
        mHandler.removeMessages(0);
        if (mMediaPath != null) {
            if (mFxReverbEffect != 0) {
                BASS.BASS_ChannelRemoveFX(mChanPlay, mFxReverbEffect);
            }
            if (mFxFlangerEffect != 0) {
                BASS.BASS_ChannelRemoveFX(mChanPlay, mFxFlangerEffect);
            }

            if (mFxEchoEffect != 0) {
                BASS.BASS_ChannelRemoveFX(mChanPlay, mFxEchoEffect);
            }

            if (mFxEQEffect != 0) {
                BASS.BASS_ChannelRemoveFX(mChanPlay, mFxEQEffect);
            }
            isPlaying = false;
            isPausing = false;
            BASS.BASS_StreamFree(mChanPlay);
        }
    }

    public void resumeAudio() {
        if (!isPausing) {
            new Exception(TAG + " resumeAudio:HanetMediaPlayer is playing").printStackTrace();
            return;
        }
        isPausing = false;

        if (mChanPlay != 0) {
            BASS.BASS_ChannelPlay(mChanPlay, false);
        }
    }

    public void setOnDBMediaListener(IDBMediaListener iDBMediaListener) {
        mDBMediaListener = iDBMediaListener;
    }

    public int getDuration() {
        if (mChanPlay != 0) {
            duration = getChannelLength();
        }
        return duration;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public boolean isPausing() {
        return !isPausing;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void seekTo(int i) {
        if (!isPlaying) {
            new Exception(TAG + " seekTo:HanetMediaPlayer is not playing").printStackTrace();
            return;
        }
        currentPosition = i;
        seekChannelTo(i);
    }

    private void initMedia() {
        BASS.BASS_StreamFree(mChanPlay);
        if (!StringUtils.isEmptyString(mMediaPath)) {
            mChanPlay = BASS.BASS_StreamCreateFile(mMediaPath, 0, 0, 2097152);
        }
        int i = mChanPlay;
        if (i != 0) {
            int BASS_FX_ReverseCreate = BASS_FX.BASS_FX_ReverseCreate(i, 2.0f, 2162688);
            mChanPlay = BASS_FX_ReverseCreate;
            if (BASS_FX_ReverseCreate != 0) {
                BASS.BASS_ChannelGetInfo(mChanPlay, new BASS.BASS_CHANNELINFO());
                int BASS_FX_TempoCreate = BASS_FX.BASS_FX_TempoCreate(mChanPlay, 65536);
                mChanPlay = BASS_FX_TempoCreate;
                if (BASS_FX_TempoCreate == 0) {
                    new Exception(TAG + " Couldnt create a resampled stream!").printStackTrace();
                    BASS.BASS_StreamFree(mChanPlay);
                    return;
                }
                return;
            }
            new Exception(TAG + " Couldnt create a resampled stream!").printStackTrace();
            BASS.BASS_StreamFree(mChanPlay);
            return;
        }
        new Exception(TAG + " Couldnt create a resampled stream!").printStackTrace();
        BASS.BASS_StreamFree(mChanPlay);
    }

    public boolean initMediaToSave() {
        BASS.BASS_StreamFree(mChanPlay);
        int BASS_StreamCreateFile = BASS.BASS_StreamCreateFile(mMediaPath, 0, 0, BASS_MUSIC_DECODE);
        mChanPlay = BASS_StreamCreateFile;
        if (BASS_StreamCreateFile != 0) {
            int BASS_FX_ReverseCreate = BASS_FX.BASS_FX_ReverseCreate(BASS_StreamCreateFile, 2.0f, BASS_MUSIC_DECODE);
            mChanPlay = BASS_FX_ReverseCreate;
            if (BASS_FX_ReverseCreate != 0) {
                int BASS_FX_TempoCreate = BASS_FX.BASS_FX_TempoCreate(BASS_FX_ReverseCreate, BASS_MUSIC_DECODE);
                mChanPlay = BASS_FX_TempoCreate;
                if (BASS_FX_TempoCreate != 0) {
                    return true;
                }
                new Exception(TAG + " Couldnt create a resampled stream!").printStackTrace();
                BASS.BASS_StreamFree(mChanPlay);
                return false;
            }
            new Exception(TAG + " Couldnt create a resampled stream!").printStackTrace();
            BASS.BASS_StreamFree(mChanPlay);
        }
        return false;
    }

    public void seekChannelTo(int i) {

        if (mChanPlay != 0) {
            BASS.BASS_ChannelSetPosition(mChanPlay, BASS.BASS_ChannelSeconds2Bytes(mChanPlay, (double) i), 0);
        }
    }


    public int getChannelPosition() {
        if (mChanPlay != 0) {
            return (int) BASS.BASS_ChannelBytes2Seconds(mChanPlay, BASS.BASS_ChannelGetPosition(mChanPlay, 0));
        }
        return -1;
    }

    public int getChannelLength() {

        if (mChanPlay != 0) {
            return (int) BASS.BASS_ChannelBytes2Seconds(mChanPlay, BASS.BASS_ChannelGetLength(mChanPlay, 0));
        }
        return 0;
    }

    public void setChannelVolumne(float f) {

        if (mChanPlay != 0) {
            BASS.BASS_ChannelSetAttribute(mChanPlay, 2, f);
        }
    }

    public void setReverse(boolean reverse) {
        isReverse = reverse;
        Log.e("check1 DBMediaPlayer", reverse + " b value");
        if (mChanPlay != 0) {
            Log.e("checkk 2 DBMediaPlayer", reverse + " b value");
            int BASS_FX_TempoGetSource = BASS_FX.BASS_FX_TempoGetSource(mChanPlay);
            if (reverse) {
                Log.e("checkk 3 DBMediaPlayer", reverse + " b value");
                BASS.BASS_ChannelSetAttribute(BASS_FX_TempoGetSource, BASS_FX.BASS_ATTRIB_REVERSE_DIR, -1.0f);
                return;
            }
            Log.e("checkk 4 DBMediaPlayer", reverse + " b value");
            BASS.BASS_ChannelSetAttribute(BASS_FX_TempoGetSource, BASS_FX.BASS_ATTRIB_REVERSE_DIR, 0.0f);
        }
    }

    public void saveToFile(String str) {
        int i;
        int BASS_ChannelGetData;
        if (!StringUtils.isEmptyString(str) && (i = mChanPlay) != 0 && BASSenc.BASS_Encode_Start(i, str, 262208, (BASSenc.ENCODEPROC) null, 0) != 0) {
            try {
                ByteBuffer allocateDirect = ByteBuffer.allocateDirect(20000);
                do {
                    BASS_ChannelGetData = BASS.BASS_ChannelGetData(mChanPlay, allocateDirect, allocateDirect.capacity());
                    if (BASS_ChannelGetData == -1) {
                        return;
                    }
                } while (BASS_ChannelGetData != 0);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
