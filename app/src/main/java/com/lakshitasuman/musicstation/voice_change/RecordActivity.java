package com.lakshitasuman.musicstation.voice_change;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;


import com.airbnb.lottie.LottieAnimationView;
import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.musicplayer.PlayerActivity;
import com.lakshitasuman.musicstation.radio.service.PauseReason;
import com.lakshitasuman.musicstation.radio.service.PlayerServiceUtil;
import com.lakshitasuman.musicstation.ringtone.utils.AdsUtils;
import com.lakshitasuman.musicstation.ringtone.utils.MyUtils;
import com.lakshitasuman.musicstation.voice_change.constants.IVoiceChangerConstants;
import com.lakshitasuman.musicstation.voice_change.utils.DBLog;
import com.lakshitasuman.musicstation.voice_change.utils.StringUtils;
import com.lakshitasuman.musicstation.voice_change.view.DBShimmerFrameLayout;
import com.lakshitasuman.musicstation.voice_change.wave.WaveWriter;

import java.io.File;
import java.io.IOException;

public class RecordActivity extends DBFragmentActivity implements View.OnClickListener {
    public static final String TAG = "RecordActivity";
    public long currentTime = 0;
    LinearLayout deleteBtn;
    private boolean isPlaying;
    private boolean isRecording;
    private AudioRecord mAudioRecord;
    private LinearLayout mBtnBack;
    public LinearLayout mBtnPlay;
    private Button mBtnRecord;
    private int mBufferSize;
    private String mFileName;
    private Handler mHandler = new Handler();
    private LinearLayout mLayoutSave;
    private DBShimmerFrameLayout mLayoutShimmer;
    private MediaPlayer mPlayer;
    private Thread mRecordingThread;
    private TextView mTvHeader;
    private TextView mTvInfoRecord;
    public TextView mTvInfoRecord1;
    public LottieAnimationView recordingView;
    private WaveWriter mWaveWriter;
    LinearLayout mainLay;
    LinearLayout menuLay;
    LinearLayout stopBtn;
    LinearLayout firstLayout;
    LinearLayout secondLayout;
    ImageView imagePlay;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_recordingnew);
        verifyPermissions();
        AdsUtils.initAd(this);
        AdsUtils.loadLargeBannerAd(this,findViewById(R.id.adsView));
        mTvInfoRecord = (TextView) findViewById(R.id.tv_record);
        mTvInfoRecord1 = (TextView) findViewById(R.id.tv_record1);
        recordingView = (LottieAnimationView) findViewById(R.id.recordingView);
        stopBtn = (LinearLayout) findViewById(R.id.stopbtn);
        firstLayout = (LinearLayout) findViewById(R.id.firstLayout);
        secondLayout = (LinearLayout) findViewById(R.id.secondLayout);
        deleteBtn = (LinearLayout) findViewById(R.id.deletebtn);
        mTvHeader = (TextView) findViewById(R.id.tv_header);
        mBtnRecord = (Button) findViewById(R.id.btn_record);
        mBtnBack = (LinearLayout) findViewById(R.id.back);
        imagePlay = (ImageView) findViewById(R.id.imagePlay);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBtnPlay = (LinearLayout) findViewById(R.id.btn_play);
        mainLay = (LinearLayout) findViewById(R.id.mainLayout);
        menuLay = (LinearLayout) findViewById(R.id.menuLayout);
        mLayoutSave = (LinearLayout) findViewById(R.id.layout_stored_data);
        mBufferSize = AudioRecord.getMinBufferSize(IVoiceChangerConstants.RECORDER_SAMPLE_RATE, 16, 2);
    }



    private void verifyPermissions() {
        if (checkSystemWritePermission()) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.RECORD_AUDIO", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.RECORD_AUDIO", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.WRITE_SETTINGS"}, 1);
        }
    }


    private boolean checkSystemWritePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            return Settings.System.canWrite(this);
        }
        return true;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLayoutShimmer != null) {
            mLayoutShimmer.stopShimmerAnimation();
        }
        stopAudioRecording(false);
        stopAudioPlaying();
    }

    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4) {
            return super.onKeyDown(i, keyEvent);
        }
        backToHome();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int n, String[] arrstring, int[] arrn) {
        super.onRequestPermissionsResult(n, arrstring, arrn);
        boolean bl = false;
        if (arrstring.length == 0) {
            return;
        }
        if (arrn.length > 0) {
            int n2 = arrn.length;
            for (int i = 0; i < n2; ++i) {
                if (arrn[i] == 0) continue;
                bl = false;
                break;
            }
        } else {
            bl = true;
        }
        if (!bl) {
            int n3 = arrstring.length;
            boolean bl2 = false;
            for (int i = 0; i < n3; ++i) {
                String string2 = arrstring[i];
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, (String) string2)) {
                    Log.e((String) "denied", (String) string2);
                    new AlertDialog.Builder((Context) this).setTitle((CharSequence) "Permissions Required").setMessage((CharSequence) "You Must Allow Permission \nOtherwise Application Will not Work Properly\nGo To Settings And Allow Permission You Denied\n\nIf You cancel Application Will Be close").setPositiveButton((CharSequence) "Setting", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int n) {
                            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts((String) "package", (String) getPackageName(), null));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }).setNegativeButton((CharSequence) "cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int n) {
                            finish();
                        }
                    }).setCancelable(false).create().show();
                    continue;
                }
                if (ActivityCompat.checkSelfPermission((Context) this, (String) string2) == 0) {
                    Log.e((String) "allowed", (String) string2);
                    continue;
                }
                Log.e((String) "set to never ask again", (String) string2);
                bl2 = true;
            }
            if (bl2) {
                new AlertDialog.Builder((Context) this).setTitle((CharSequence) "Permissions Required").setMessage((CharSequence) "Some additional permissions needed for this action. Please click on settings, go to system settings and allow them.").setPositiveButton((CharSequence) "Settings", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int n) {
                        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts((String) "package", (String) getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).setNegativeButton((CharSequence) "Cancel", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialogInterface, int n) {
                        new AlertDialog.Builder((Context) RecordActivity.this).setTitle((CharSequence) "Permissions Denied").setMessage((CharSequence) "You Must Allow Permission \nOtherwise Application Will be closed Automatically\n\nGo To Settings And Allow Permission You Denied\nIf You cancel Application Will Be close").setPositiveButton((CharSequence) "Setting", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int n) {
                                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts((String) "package", (String) getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).setNegativeButton((CharSequence) "cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int n) {
                                finish();
                                dialogInterface.dismiss();
                            }
                        }).setCancelable(false).create().show();
                    }

                }).setCancelable(false).create().show();
            }
        }
    }

    private void backToHome() {
        if (!isRecording) {
            deleteMainFile();
            finish();
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back :
                mSoundMng.play((Context) this, (int) R.raw.click);
                backToHome();
                return;
            case R.id.btn_next :
                mSoundMng.play((Context) this, (int) R.raw.click);
                if (!StringUtils.isEmptyString(mFileName)) {
                    Intent intent = new Intent(this, EffectActivity.class);
                    intent.putExtra(IVoiceChangerConstants.KEY_PATH_AUDIO, mFileName);
                    startActivity(intent);
                    return;
                }
                return;
            case R.id.btn_play :
                if (PlayerServiceUtil.isPlaying()){
                    PlayerServiceUtil.pause(PauseReason.NONE);
                }
                if(PlayerActivity.mMediaPlayer != null){
                    if (PlayerActivity.mMediaPlayer.isPlaying()){
                        PlayerActivity.mMediaPlayer.pause();
                    }
                }
                if (!isPlaying) {
                    startAudioPlaying();
                    return;
                } else {
                    pauseAudioPlaying();
                    return;
                }
            case R.id.btn_record :
                startRecord();
                return;
            case R.id.deletebtn :
                if (isRecording) {
                    mHandler.removeCallbacksAndMessages((Object) null);
                    isRecording = false;
                    mTvInfoRecord1.setVisibility(View.INVISIBLE);
                    recordingView.setVisibility(View.GONE);
                    menuLay.setVisibility(View.GONE);
                    secondLayout.setVisibility(View.GONE);
                    mainLay.setVisibility(View.VISIBLE);
                    firstLayout.setVisibility(View.VISIBLE);
                    mBtnBack.setVisibility(View.VISIBLE);
                    stopAudioRecording(true);
                    return;
                }
                return;
            case R.id.stopbtn :
                stopRecord();
                return;
            default:
                return;
        }
    }
    private void startRecord() {
        mainLay.setVisibility(View.GONE);
        firstLayout.setVisibility(View.GONE);
        menuLay.setVisibility(View.VISIBLE);
        secondLayout.setVisibility(View.VISIBLE);
        mTvInfoRecord1.setVisibility(View.VISIBLE);
        recordingView.setVisibility(View.VISIBLE);
        if (!isRecording) {
            stopAudioPlaying();
            imagePlay.setImageResource(R.drawable.playsmallnew);
            isRecording = true;
            currentTime = 0;
            mTvInfoRecord1.setText("00:00");
            mLayoutSave.setVisibility(View.INVISIBLE);
            mBtnBack.setVisibility(View.INVISIBLE);
            displayTime();
            startAudioRecording();
        }
    }


    public void stopRecord() {
        if (isRecording) {
            mHandler.removeCallbacksAndMessages((Object) null);
            isRecording = false;
            mTvInfoRecord1.setVisibility(View.INVISIBLE);
            recordingView.setVisibility(View.GONE);
            menuLay.setVisibility(View.GONE);
            secondLayout.setVisibility(View.GONE);
            mainLay.setVisibility(View.VISIBLE);
            firstLayout.setVisibility(View.VISIBLE);
            if (currentTime > 0) {
                mLayoutSave.setVisibility(View.VISIBLE);
            }
            mBtnBack.setVisibility(View.VISIBLE);
            stopAudioRecording(true);
        }
    }


    public void displayTime() {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                if (currentTime < IVoiceChangerConstants.MAX_TIME) {
                    currentTime = currentTime + 1000;
                    String valueOf = String.valueOf((currentTime / 1000) / 60);
                    String valueOf2 = String.valueOf((currentTime / 1000) % 60);
                    if (valueOf.length() == 1) {
                        valueOf = "0" + valueOf;
                    }
                    if (valueOf2.length() == 1) {
                        valueOf2 = "0" + valueOf2;
                    }
                    mTvInfoRecord1.setText(valueOf + ":" + valueOf2);
                    displayTime();
                    return;
                }
                stopRecord();
            }
        }, 1000);
    }

    private void startAudioRecording() {
        if (mBufferSize <= 0) {
            stopRecord();
            return;
        }
        deleteMainFile();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mAudioRecord = new AudioRecord(1, IVoiceChangerConstants.RECORDER_SAMPLE_RATE, 16, 2, mBufferSize);
        mFileName = getFilename();
        mWaveWriter = new WaveWriter(new File(mFileName), IVoiceChangerConstants.RECORDER_SAMPLE_RATE, 1, 16);
        try {
            mWaveWriter.createWaveFile();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        if (mAudioRecord.getState() == 1) {
            mAudioRecord.startRecording();
            mRecordingThread = new Thread(new Runnable() {
              @Override
                public void run() {
                    writeAudioDataToFile();
                }
            });
            mRecordingThread.start();
            return;
        }
        showToast((int) R.string.info_record_error);
        stopRecord();
    }



    public void writeAudioDataToFile() {
        short[] sArr = new short[8192];
        if (mWaveWriter != null) {
            while (isRecording) {
                int read = mAudioRecord.read(sArr, 0, 8192);
                if (-3 != read) {
                    try {
                        mWaveWriter.write(sArr, 0, read);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }

    public void stopAudioRecording(boolean b) {
        if (mAudioRecord != null) {
            try {

                if (mRecordingThread != null) {
                    mRecordingThread.interrupt();
                    mRecordingThread = null;
                }
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
                try {
                    mWaveWriter.closeWaveFile();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } catch (Exception exception2) {
               exception2.printStackTrace();
            }
        }
    }

    private void deleteMainFile() {
        if (!StringUtils.isEmptyString(mFileName)) {
            try {
                new File(mFileName).delete();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private String getFilename() {
        File file = MyUtils.ChangeVoiceLocation;
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath() + "/record" + IVoiceChangerConstants.AUDIO_RECORDER_FILE_EXT_WAV;
    }

    private void startAudioPlaying() {
        if (!StringUtils.isEmptyString(mFileName) && !isPlaying) {

            if (mPlayer == null) {
                try {
                    mPlayer = new MediaPlayer();
                    mPlayer.setDataSource(mFileName);
                    mPlayer.prepare();
                    mPlayer.start();
                    imagePlay.setImageResource(R.drawable.pausesmallnew);
                    isPlaying = true;
                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                      @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            stopAudioPlaying();
                          imagePlay.setImageResource(R.drawable.playsmallnew);
                        }
                    });
                    mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                       @Override
                        public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                            stopAudioPlaying();
                           imagePlay.setImageResource(R.drawable.playsmallnew);
                            return false;
                        }
                    });
                } catch (IOException exceptionxception) {
                    DBLog.d(TAG, "prepare() failed");
                }
            } else if (!mPlayer.isPlaying()) {
                mPlayer.start();
                isPlaying = true;
                imagePlay.setImageResource(R.drawable.pausesmallnew);
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        stopRecord();
        pauseAudioPlaying();
    }

    private void pauseAudioPlaying() {
        MediaPlayer mediaPlayer;
        if (isPlaying && (mediaPlayer = mPlayer) != null && mediaPlayer.isPlaying()) {
            mPlayer.pause();
            isPlaying = false;
            imagePlay.setImageResource(R.drawable.playsmallnew);
        }
    }



    public void stopAudioPlaying() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        isPlaying = false;
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}
