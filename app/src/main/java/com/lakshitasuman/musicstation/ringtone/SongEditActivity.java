package com.lakshitasuman.musicstation.ringtone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.ringtone.designview.CheapSoundFile;
import com.lakshitasuman.musicstation.ringtone.designview.MarkerView;
import com.lakshitasuman.musicstation.ringtone.designview.SeekTest;
import com.lakshitasuman.musicstation.ringtone.designview.SongMetadataReader;
import com.lakshitasuman.musicstation.ringtone.designview.WaveformView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

public class SongEditActivity extends Activity implements MarkerView.MarkerListener, WaveformView.WaveformListener {
    RelativeLayout btnBack;
    Context cn = this;
    LinearLayout layEndTxt;
    LinearLayout layStartTxt;
    private String mAlbum;
    private String mArtist;
    public boolean mCanSeekAccurately;
    private String mCaption = "";
    private float mDensity;
    private File mDstFilename;
    public MarkerView mEndMarker;
    public int mEndPos;
    public TextView mEndText;
    public boolean mEndVisible;
    private String mExtension;
    private ImageView mFfwdButton;
    private View.OnClickListener mFfwdListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mIsPlaying) {
                int currentPosition = mPlayer.getCurrentPosition() + BaseImageDownloader.DEFAULT_HTTP_CONNECT_TIMEOUT;
                if (currentPosition > mPlayEndMsec) {
                    currentPosition = mPlayEndMsec;
                }
                mPlayer.seekTo(currentPosition);
                return;
            }
            mEndMarker.requestFocus();
            markerFocus(mEndMarker);
        }
    };
    public File mFile;
    private String mFilename;
    private int mFlingVelocity;
    private String mGenre;
    public Handler mHandler;
    public boolean mIsPlaying;
    private boolean mKeyDown;
    public int mLastDisplayedEndPos;
    public int mLastDisplayedStartPos;
    public boolean mLoadingKeepGoing;
    public long mLoadingLastUpdateTime;
    private long mLoadingStartTime;
    private View.OnClickListener mMarkEndListener = new View.OnClickListener() {
       
       @Override
       public void onClick(View view) {
            if (mIsPlaying) {
                mEndPos = mWaveformView.millisecsToPixels(mPlayer.getCurrentPosition() + mPlayStartOffset);
                updateDisplay();
                handlePause();
            }
        }
    };
    private View.OnClickListener mMarkStartListener = new View.OnClickListener() {
       @Override
        public void onClick(View view) {
            if (mIsPlaying) {
                mStartPos = mWaveformView.millisecsToPixels(mPlayer.getCurrentPosition() + mPlayStartOffset);
                updateDisplay();
            }
        }
    };
    private int mMarkerBottomOffset = 0;
    private int mMarkerLeftInset = 35;
    private int mMarkerRightInset = 35;
    private int mMarkerTopOffset = 0;
    public int mMaxPos;
    private int mNewFileKind;
    public int mOffset;
    public int mOffsetGoal;
    private ImageView mPlayButton;
    public int mPlayEndMsec;
    private View.OnClickListener mPlayListener = new View.OnClickListener() {
        public void onClick(View view) {
            onPlay(mStartPos);
        }
    };
    public int mPlayStartMsec;
    public int mPlayStartOffset;
    public MediaPlayer mPlayer;
    public ProgressDialog mProgressDialog;
    private String mRecordingFilename;
    private Uri mRecordingUri;
    private ImageView mRewindButton;
    private View.OnClickListener mRewindListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mIsPlaying) {
                int currentPosition = mPlayer.getCurrentPosition() - 5000;
                if (currentPosition < mPlayStartMsec) {
                    currentPosition = mPlayStartMsec;
                }
                mPlayer.seekTo(currentPosition);
                return;
            }
            mStartMarker.requestFocus();
            markerFocus(mStartMarker);
        }
    };
    private ImageView mSaveButton;
    private View.OnClickListener mSaveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onSave();
        }
    };
    public CheapSoundFile mSoundFile;
    public MarkerView mStartMarker;
    public int mStartPos;
    public TextView mStartText;
    public boolean mStartVisible;
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (mStartText.hasFocus()) {
                try {
                    mStartPos = mWaveformView.secondsToPixels(Double.parseDouble(mStartText.getText().toString()));
                    updateDisplay();
                } catch (NumberFormatException exceptionxception) {
                }
            }
            if (mEndText.hasFocus()) {
                try {
                    mEndPos = mWaveformView.secondsToPixels(Double.parseDouble(mEndText.getText().toString()));
                    updateDisplay();
                } catch (NumberFormatException exceptionxception2) {
                }
            }
        }
    };
    public Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if (mStartPos != mLastDisplayedStartPos && !mStartText.hasFocus()) {
                mStartText.setText(formatTime(mStartPos));
                mLastDisplayedStartPos = mStartPos;
            }
            if (mEndPos != mLastDisplayedEndPos && !mEndText.hasFocus()) {
                mEndText.setText(formatTime(mEndPos));
                mLastDisplayedEndPos = mEndPos;
            }
            mHandler.postDelayed(mTimerRunnable, 100);
        }
    };
    private String mTitle;
    private boolean mTouchDragging;
    private int mTouchInitialEndPos;
    private int mTouchInitialOffset;
    private int mTouchInitialStartPos;
    private float mTouchStart;
    private boolean mWasGetContentIntent;
    private long mWaveformTouchStartMsec;
    public WaveformView mWaveformView;
    private int mWidth;
    private int mYear;
    private ImageButton mZoomInButton;
    private View.OnClickListener mZoomInListener = new View.OnClickListener() {
       @Override
        public void onClick(View view) {
            mWaveformView.zoomIn();
            mStartPos = mWaveformView.getStart();
            mEndPos = mWaveformView.getEnd();
            mMaxPos = mWaveformView.maxPos();
            mOffset = mWaveformView.getOffset();
            mOffsetGoal = mOffset;
            enableZoomButtons();
            updateDisplay();
        }
    };
    private ImageButton mZoomOutButton;
    private View.OnClickListener mZoomOutListener = new View.OnClickListener() {
       @Override
        public void onClick(View view) {
            mWaveformView.zoomOut();
            mStartPos = mWaveformView.getStart();
            mEndPos = mWaveformView.getEnd();
            mMaxPos = mWaveformView.maxPos();
            mOffset = mWaveformView.getOffset();
            mOffsetGoal = mOffset;
            enableZoomButtons();
            updateDisplay();
        }
    };

    @Override
    public void markerDraw() {
    }

    @Override
    public void markerEnter(MarkerView markerView) {
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mRecordingFilename = null;
        mRecordingUri = null;
        mPlayer = null;
        mIsPlaying = false;
        mWasGetContentIntent = getIntent().getBooleanExtra("was_get_content_intent", false);
        mFilename = getIntent().getStringExtra("path");
        Log.e("AAA", "path of trim : " + mFilename);
        mSoundFile = null;
        mKeyDown = false;
        mHandler = new Handler();
        loadGui();
        mHandler.postDelayed(mTimerRunnable, 100);
        if (!mFilename.equals("record")) {
            loadFromFile();
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.i("", "EditActivity OnDestroy");
        MediaPlayer mediaPlayer = mPlayer;
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer = null;
        if (mRecordingFilename != null) {
            try {
                if (!new File(mRecordingFilename).delete()) {
                    showFinalAlert(new Exception(), (int) R.string.delete_tmp_error);
                }
                getContentResolver().delete(mRecordingUri, (String) null, (String[]) null);
            } catch (SecurityException exception) {
                showFinalAlert((Exception) exception, (int) R.string.delete_tmp_error);
            }
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 62) {
            return super.onKeyDown(i, keyEvent);
        }
        onPlay(mStartPos);
        return true;
    }

    @Override
    public void waveformDraw() {
        mWidth = mWaveformView.getMeasuredWidth();
        if (mOffsetGoal != mOffset && !mKeyDown) {
            updateDisplay();
        } else if (mIsPlaying) {
            updateDisplay();
        } else if (mFlingVelocity != 0) {
            updateDisplay();
        }
    }

    @Override
    public void waveformTouchStart(float f) {
        mTouchDragging = true;
        mTouchStart = f;
        mTouchInitialOffset = mOffset;
        mFlingVelocity = 0;
        mWaveformTouchStartMsec = System.currentTimeMillis();
    }

    @Override
    public void waveformTouchMove(float f) {
        mOffset = trap((int) (((float) mTouchInitialOffset) + (mTouchStart - f)));
        updateDisplay();
    }

    @Override
    public void waveformTouchEnd() {
        mTouchDragging = false;
        mOffsetGoal = mOffset;
        if (System.currentTimeMillis() - mWaveformTouchStartMsec < 300) {
            if (mIsPlaying) {
                int pixelsToMillisecs = mWaveformView.pixelsToMillisecs((int) (mTouchStart + ((float) mOffset)));
                if (pixelsToMillisecs < mPlayStartMsec || pixelsToMillisecs >= mPlayEndMsec) {
                    handlePause();
                } else {
                    mPlayer.seekTo(pixelsToMillisecs - mPlayStartOffset);
                }
            } else {
                onPlay((int) (mTouchStart + ((float) mOffset)));
            }
        }
    }

    @Override
    public void waveformFling(float f) {
        mTouchDragging = false;
        mOffsetGoal = mOffset;
        mFlingVelocity = (int) (-f);
        updateDisplay();
    }
    @Override
    public void markerTouchStart(MarkerView markerView, float f) {
        mTouchDragging = true;
        mTouchStart = f;
        mTouchInitialStartPos = mStartPos;
        mTouchInitialEndPos = mEndPos;
    }

    @Override
    public void markerTouchMove(MarkerView markerView, float f) {
        float f2 = f - mTouchStart;
        if (markerView == mStartMarker) {
            mStartPos = trap((int) (((float) mTouchInitialStartPos) + f2));
            mEndPos = trap((int) (((float) mTouchInitialEndPos) + f2));
        } else {
            mEndPos= trap((int) (((float) mTouchInitialEndPos) + f2));
            if (mEndPos < mStartPos) {
                mEndPos = mStartPos;
            }
        }
        updateDisplay();
    }

    @Override
    public void markerTouchEnd(MarkerView markerView) {
        mTouchDragging = false;
        if (markerView == mStartMarker) {
            setOffsetGoalStart();
        } else {
            setOffsetGoalEnd();
        }
    }

    @Override
    public void markerLeft(MarkerView markerView, int i) {
        mKeyDown = true;
        if (markerView == mStartMarker) {
            mStartPos= trap(mStartPos - i);
            mEndPos = trap(mEndPos - (mStartPos - mStartPos));
            setOffsetGoalStart();
        }
        if (markerView == mEndMarker) {
            if (mEndPos == mStartPos) {
                mStartPos = trap(mStartPos - i);
                mEndPos = mStartPos;
            } else {
                mEndPos = trap(mEndPos - i);
            }
            setOffsetGoalEnd();
        }
        updateDisplay();
    }

    @Override
    public void markerRight(MarkerView markerView, int i) {
        mKeyDown = true;
        if (markerView == mStartMarker) {
            mStartPos = mStartPos + i;
            if (mStartPos > mMaxPos) {
                mStartPos = mMaxPos;
            }
            mEndPos = mEndPos + (mStartPos - mStartPos);
            if (mEndPos > mMaxPos) {
                mEndPos = mMaxPos;
            }
            setOffsetGoalStart();
        }
        if (markerView == mEndMarker) {
            mEndPos = mEndPos + i;
            if (mEndPos > mMaxPos) {
                mEndPos = mMaxPos;
            }
            setOffsetGoalEnd();
        }
        updateDisplay();
    }

    @Override
    public void markerKeyUp() {
        mKeyDown = false;
        updateDisplay();
    }

    @Override
    public void markerFocus(MarkerView markerView) {
        mKeyDown = false;
        if (markerView == mStartMarker) {
            setOffsetGoalStartNoUpdate();
        } else {
            setOffsetGoalEndNoUpdate();
        }
        mHandler.postDelayed(new Runnable() {
          @Override
            public void run() {
                updateDisplay();
            }
        }, 100);
    }



    private void loadGui() {

        setContentView(R.layout.activity_song_edit);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mDensity = displayMetrics.density;
        mStartText = (TextView) findViewById(R.id.starttext);
        mStartText.addTextChangedListener(mTextWatcher);
        mEndText = (TextView) findViewById(R.id.endtext);
        mEndText.addTextChangedListener(mTextWatcher);
        mPlayButton = (ImageView) findViewById(R.id.play);
        mPlayButton.setOnClickListener(mPlayListener);
        mRewindButton = (ImageView) findViewById(R.id.rew);
        mRewindButton.setOnClickListener(mRewindListener);
        mFfwdButton = (ImageView) findViewById(R.id.ffwd);
        mFfwdButton.setOnClickListener(mFfwdListener);
        btnBack = (RelativeLayout) findViewById(R.id.back);
        layStartTxt = (LinearLayout) findViewById(R.id.laystarttxt);
        layEndTxt = (LinearLayout) findViewById(R.id.layendtxt);
        mSaveButton = (ImageView) findViewById(R.id.save);
        mSaveButton.setOnClickListener(mSaveListener);
        enableDisableButtons();
        mWaveformView = (WaveformView) findViewById(R.id.waveform);
        mWaveformView.setListener(this);
        mMaxPos = 0;
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;
        if (mSoundFile != null) {
            mWaveformView.setSoundFile(mSoundFile);
            mWaveformView.recomputeHeights(mDensity);
            mMaxPos = mWaveformView.maxPos();
        }
        mStartMarker= (MarkerView) findViewById(R.id.startmarker);
        mStartMarker.setListener(this);
        mStartMarker.setAlpha(255);
        mStartMarker.setFocusable(true);
        mStartMarker.setFocusableInTouchMode(true);
        mStartVisible = true;
        mEndMarker = (MarkerView) findViewById(R.id.endmarker);
        mEndMarker.setListener(this);
        mEndMarker.setAlpha(255);
        mEndMarker.setFocusable(true);
        mEndMarker.setFocusableInTouchMode(true);
        mEndVisible = true;
        updateDisplay();
    }

    private void loadFromFile() {
        mFile = new File(mFilename);
        mExtension = getExtensionFromFilename(mFilename);
        SongMetadataReader songMetadataReader = new SongMetadataReader(this, mFilename);
        mTitle = songMetadataReader.mTitle;
        mArtist = songMetadataReader.mArtist;
        mAlbum = songMetadataReader.mAlbum;
        mYear = songMetadataReader.mYear;
        mGenre = songMetadataReader.mGenre;
        if (mArtist != null && mArtist.length() > 0) {
            mTitle = mTitle + " - " + mArtist;
        }
        setTitle(mTitle);
        mLoadingStartTime = System.currentTimeMillis();
        mLoadingLastUpdateTime = System.currentTimeMillis();
        mLoadingKeepGoing = true;
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(1);
        mProgressDialog.setTitle(R.string.progress_dialog_loading);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
           public void onCancel(DialogInterface dialogInterface) {
                mLoadingKeepGoing = false;
            }
        });
        mProgressDialog.show();
        final CheapSoundFile.ProgressListener progressListener = new CheapSoundFile.ProgressListener() {
            @Override
            public boolean reportProgress(double d) {
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - mLoadingLastUpdateTime > 100) {
                    double max = (double) mProgressDialog.getMax();
                    Double.isNaN(max);
                    mProgressDialog.setProgress((int) (max * d));
                    mLoadingLastUpdateTime = currentTimeMillis;
                }
                return mLoadingKeepGoing;
            }
        };
        mCanSeekAccurately = false;
        new Thread() {
           @Override
            public void run() {
                mCanSeekAccurately = SeekTest.CanSeekAccurately(getPreferences(0));
                System.out.println("Seek test done, creating media player.");
                try {
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(mFile.getAbsolutePath());
                    mediaPlayer.setAudioStreamType(3);
                    mediaPlayer.prepare();
                    mPlayer = mediaPlayer;
                } catch (IOException exception) {
                    mHandler.post(new Runnable() {
                      @Override
                        public void run() {
                            handleFatalError("ReadError", getResources().getText(R.string.read_error), exception);
                        }
                    });
                }
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                final String str;
                try {
                    mSoundFile = CheapSoundFile.create(mFile.getAbsolutePath(), progressListener);
                    if (mSoundFile == null) {
                        mProgressDialog.dismiss();
                        String[] split = mFile.getName().toLowerCase().split("\\.");
                        if (split.length < 2) {
                            str = getResources().getString(R.string.no_extension_error);
                        } else {
                            str = getResources().getString(R.string.bad_extension_error) + " " + split[split.length - 1];
                        }
                        mHandler.post(new Runnable() {
                            public void run() {
                                handleFatalError("UnsupportedExtension", str, new Exception());
                            }
                        });
                        return;
                    }
                    mProgressDialog.dismiss();
                    if (mLoadingKeepGoing) {
                        mHandler.post(new Runnable() {
                            public void run() {
                                finishOpeningSoundFile();
                            }
                        });
                    } else {
                        finish();
                    }
                } catch (Exception exception) {
                    mProgressDialog.dismiss();
                    exception.printStackTrace();
                    mHandler.post(new Runnable() {
                        public void run() {
                            handleFatalError("ReadError", getResources().getText(R.string.read_error), exception);
                        }
                    });
                }
            }
        }.start();
    }



    public void finishOpeningSoundFile() {
        mWaveformView.setSoundFile(mSoundFile);
        mWaveformView.recomputeHeights(mDensity);
        mMaxPos = mWaveformView.maxPos();
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;
        mTouchDragging = false;
        mOffset = 0;
        mOffsetGoal = 0;
        mFlingVelocity = 0;
        resetPositions();
        if (mEndPos > mMaxPos) {
            mEndPos = mMaxPos;
        }
        mCaption = mSoundFile.getFiletype() + ", " + mSoundFile.getSampleRate() + " Hz, " + mSoundFile.getAvgBitrateKbps() + " kbps, " + formatTime(mMaxPos) + " " + getResources().getString(R.string.time_seconds);
        updateDisplay();
    }



    public synchronized void updateDisplay() {
        int i = 0;
        if (mIsPlaying) {
            int currentPosition = mPlayer.getCurrentPosition() + mPlayStartOffset;
            int milliSecsToPixels = mWaveformView.millisecsToPixels(currentPosition);
            mWaveformView.setPlayback(milliSecsToPixels);
            setOffsetGoalNoUpdate(milliSecsToPixels - (mWidth / 2));
            if (currentPosition >= mPlayEndMsec) {
                handlePause();
            }
        }
        int i2 = 0;
        if (!mTouchDragging) {
            if (mFlingVelocity != 0) {
                int i3 = mFlingVelocity / 30;
                if (mFlingVelocity > 80) {
                    mFlingVelocity -= 80;
                } else if (mFlingVelocity < -80) {
                    mFlingVelocity += 80;
                } else {
                    mFlingVelocity = 0;
                }
                mOffset = mOffset + i3;
                if (mOffset + (mWidth / 2) > mMaxPos) {
                    mOffset = mMaxPos - (mWidth / 2);
                    mFlingVelocity = 0;
                }
                if (mOffset < 0) {
                    mOffset = 0;
                    mFlingVelocity = 0;
                }
                mOffsetGoal = mOffset;
            } else {
                int i5 = mOffsetGoal - mOffset;
                if (i5 <= 10) {
                    if (i5 > 0) {
                        i = 1;
                    } else if (i5 >= -10) {
                        i = i5 < 0 ? -1 : 0;
                    }
                    mOffset = mOffset + i;
                }
                i = i5 / 10;
                mOffset = mOffset + i;
            }
        }
        mWaveformView.setParameters(mStartPos, mEndPos, mOffset);
        mWaveformView.invalidate();
        mStartMarker.setContentDescription(getResources().getText(R.string.start_marker) + " " + formatTime(mStartPos));
        mEndMarker.setContentDescription(getResources().getText(R.string.end_marker) + " " + formatTime(mEndPos));
        int i7 = (mStartPos - mOffset) - mMarkerLeftInset;
        if (mStartMarker.getWidth() + i7 < 0) {
            if (mStartVisible) {
                mStartMarker.setAlpha(0);
                mStartVisible = false;
            }
            i7 = 0;
        } else if (!mStartVisible) {
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    mStartVisible = true;
                    mStartMarker.setAlpha(255);
                }
            }, 0);
        }
        int width = ((mEndPos - mOffset) - mEndMarker.getWidth()) + mMarkerRightInset;
        if (mEndMarker.getWidth() + width >= 0) {
            if (!mEndVisible) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mEndVisible = true;
                        mEndMarker.setAlpha(255);
                    }
                }, 0);
            }
            i2 = width;
        } else if (mEndVisible) {
            mEndMarker.setAlpha(0);
            mEndVisible = false;
        }
        mStartMarker.setLayoutParams(new AbsoluteLayout.LayoutParams(-2, -2, i7, mMarkerTopOffset));
        mEndMarker.setLayoutParams(new AbsoluteLayout.LayoutParams(-2, -2, i2, (mWaveformView.getMeasuredHeight() - mEndMarker.getHeight()) + 25));
    }


    private void enableDisableButtons() {
        if (mIsPlaying) {
            mPlayButton.setImageResource(R.drawable.pause);
            mPlayButton.setContentDescription(getResources().getText(R.string.stop));
            return;
        }
        mPlayButton.setImageResource(R.drawable.play);
        mPlayButton.setContentDescription(getResources().getText(R.string.play));
    }

    private void resetPositions() {
        mStartPos = mWaveformView.secondsToPixels(0.0d);
        mEndPos = mWaveformView.secondsToPixels(15.0d);
    }

    private int trap(int i) {
        if (i < 0) {
            return 0;
        }
        int i2 = mMaxPos;
        return i > i2 ? i2 : i;
    }

    private void setOffsetGoalStart() {
        setOffsetGoal(mStartPos - (mWidth / 2));
    }

    private void setOffsetGoalStartNoUpdate() {
        setOffsetGoalNoUpdate(mStartPos - (mWidth / 2));
    }

    private void setOffsetGoalEnd() {
        setOffsetGoal(mEndPos - (mWidth / 2));
    }

    private void setOffsetGoalEndNoUpdate() {
        setOffsetGoalNoUpdate(mEndPos - (mWidth / 2));
    }

    private void setOffsetGoal(int i) {
        setOffsetGoalNoUpdate(i);
        updateDisplay();
    }




    private void setOffsetGoalNoUpdate(int i) {
        if (!mTouchDragging) {
            mOffsetGoal = i;
            int i3 = i + (mWidth / 2);
            if (i3 > mMaxPos) {
                mOffsetGoal = mMaxPos - (mWidth / 2);
            }
            if (mOffsetGoal < 0) {
                mOffsetGoal = 0;
            }
        }
    }

    public String formatTime(int i) {
        return (mWaveformView == null || !mWaveformView.isInitialized()) ? "" : formatDecimal(mWaveformView.pixelsToSeconds(i));
    }

    private String formatDecimal(double d) {
        Double.isNaN(d);
        int i2 = (int) (((d - d) * 100.0d) + 0.5d);
        if (i2 >= 100) {
            d++;
            i2 -= 100;
            if (i2 < 10) {
                i2 *= 10;
            }
        }
        if (i2 < 10) {
            return d + ".0" + i2;
        }
        return d + "." + i2;
    }

    public synchronized void handlePause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        mWaveformView.setPlayback(-1);
        mIsPlaying = false;
        enableDisableButtons();
    }

    private synchronized void onPlay(int startPosition) {
        if (mIsPlaying) {
            handlePause();
            return;
        }
        if (mPlayer == null) {
            return;
        }

        try {
            mPlayStartMsec = mWaveformView.pixelsToMillisecs(startPosition);
            if (startPosition < mStartPos) {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mStartPos);
            } else if (startPosition > mEndPos) {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mMaxPos);
            } else {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mEndPos);
            }

            mIsPlaying = true;

            mPlayer.seekTo(mPlayStartMsec);
            mPlayer.start();
            updateDisplay();
            enableDisableButtons();
        } catch (Exception exception) {
            showFinalAlert(exception, R.string.play_error);
            return;
        }
    }



    private void showFinalAlert(Exception exceptionxc, CharSequence charSequence) {
        CharSequence charSequence2;
        if (exceptionxc != null) {
            Log.e("", "Error: " + charSequence);
            Log.e("", getStackTrace(exceptionxc));
            charSequence2 = getResources().getText(R.string.alert_title_failure);
            setResult(0, new Intent());
        } else {
            Log.i("Ringdroid", "Success: " + charSequence);
            charSequence2 = getResources().getText(R.string.alert_title_success);
        }
        new AlertDialog.Builder(this).setTitle(charSequence2).setMessage(charSequence).setPositiveButton((int) R.string.alert_ok_button, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).setCancelable(false).show();
    }


    private void showFinalAlert(Exception exceptionxc, int i) {
        showFinalAlert(exceptionxc, getResources().getText(i));
    }

    private String makeRingtoneFilename(CharSequence charSequence, String str) {
        String str2;

        String str3 = mNewFileKind != 1 ? mNewFileKind != 2 ? mNewFileKind != 3 ? "/sdcard/media/audio/music" : "/sdcard/media/audio/ringtones" : "/sdcard/media/audio/notifications" : "/sdcard/media/audio/alarms";
        File file = new File(str3);
        file.mkdirs();
        if (!file.isDirectory()) {
            str3 = "/sdcard";
        }
        int i2 = 0;
        String str4 = "";
        for (int i3 = 0; i3 < charSequence.length(); i3++) {
            if (Character.isLetterOrDigit(charSequence.charAt(i3))) {
                str4 = str4 + charSequence.charAt(i3);
            }
        }
        while (i2 < 100) {
            if (i2 > 0) {
                str2 = str3 + "/" + str4 + i2 + str;
            } else {
                str2 = str3 + "/" + str4 + str;
            }
            try {
                new RandomAccessFile(new File(str2), "r");
                i2++;
            } catch (Exception exceptionxception) {
                return str2;
            }
        }
        return null;
    }

    private void saveRingtone(CharSequence charSequence) {
        File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_RINGTONES + File.separator +"Ringtone Maker");
        file.mkdirs();
        new File(file, "audiotrim.mp3");
        mDstFilename = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + "SongEdit");
        if (mDstFilename == null) {
            showFinalAlert(new Exception(), (int) R.string.no_unique_filename);
            return;
        }
        double pixelsToSeconds = mWaveformView.pixelsToSeconds(mStartPos);
        double pixelsToSeconds2 = mWaveformView.pixelsToSeconds(mEndPos);
        final int secondsToFrames = mWaveformView.secondsToFrames(pixelsToSeconds);
        final int secondsToFrames2 = mWaveformView.secondsToFrames(pixelsToSeconds2);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(0);
        mProgressDialog.setTitle(R.string.progress_dialog_saving);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        new Thread() {
           @Override
            public void run() {
                final Exception exceptionxc;
                final CharSequence charSequence;
                try {
                    File tempFile =mDstFilename ;
                    tempFile.mkdirs();
                    File tempDirFile = new File(tempFile,"audio.mp3");
                    mSoundFile.WriteFile(tempDirFile, secondsToFrames, secondsToFrames2 - secondsToFrames);
                    CheapSoundFile.create(tempDirFile.getAbsolutePath(), new CheapSoundFile.ProgressListener() {
                        @Override
                        public boolean reportProgress(double d) {
                            return true;
                        }
                    });
                    mProgressDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.putExtra("path", tempDirFile.getAbsolutePath());
                            setResult(-1, intent);
                            finish();
                        }
                    });
                } catch (Exception exception) {
                    mProgressDialog.dismiss();
                    Log.e("error",exception.getMessage());
                    if (exception.getMessage().equals("No space left on device")) {
                        charSequence = getResources().getText(R.string.no_space_error);
                        exception = null;
                    } else {

                        charSequence = getResources().getText(R.string.write_error);
                    }
                    Exception finalException = exception;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            handleFatalError("WriteError", charSequence, finalException);
                        }
                    });
                }
            }
        }.start();
    }

    private void afterSavingRingtone(CharSequence charSequence, String str, File file, int i) {
        if (file.length() <= 512) {
            file.delete();
            new AlertDialog.Builder(this).setTitle((int) R.string.alert_title_failure).setMessage((int) R.string.too_small_error).setPositiveButton((int) R.string.alert_ok_button, (DialogInterface.OnClickListener) null).setCancelable(false).show();
            return;
        }
        long length = file.length();
        ContentValues contentValues = new ContentValues();
        contentValues.put("_data", str);
        contentValues.put("title", charSequence.toString());
        contentValues.put("_size", Long.valueOf(length));
        contentValues.put("mime_type", "audio/mpeg");
        contentValues.put("duration", Integer.valueOf(i));
        contentValues.put("is_music", Boolean.valueOf(mNewFileKind == 0));
        final Uri insert = getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(str), contentValues);
        setResult(-1, new Intent().setData(insert));
        getPreferences(0).edit().commit();
        int i2 = mNewFileKind;
        if (i2 == 0 || i2 == 1) {
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        } else if (i2 == 2) {
            new AlertDialog.Builder(this).setTitle((int) R.string.alert_title_success).setMessage((int) R.string.set_default_notification).setPositiveButton((int) R.string.alert_yes_button, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    RingtoneManager.setActualDefaultRingtoneUri(SongEditActivity.this, 2, insert);
                }
            }).setNegativeButton((int) R.string.alert_no_button, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            }).setCancelable(false).show();
        }
    }


    public void handleFatalError(CharSequence charSequence, CharSequence charSequence2, Exception exceptionxc) {
        Log.i("Ringdroid", "handleFatalError");
    }



    public void onSave() {
        if (mIsPlaying) {
            handlePause();
        }
        saveRingtone("");
    }


    public void enableZoomButtons() {
        mZoomInButton.setEnabled(mWaveformView.canZoomIn());
        mZoomOutButton.setEnabled(mWaveformView.canZoomOut());
    }

    private String getStackTrace(Exception exceptionxc) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        exceptionxc.printStackTrace(new PrintWriter(byteArrayOutputStream, true));
        return byteArrayOutputStream.toString();
    }

    private String getExtensionFromFilename(String str) {
        return str.substring(str.lastIndexOf(46), str.length());
    }

    private String getFilenameFromUri(Uri uri) {
        String[] strArr = null;
        Cursor managedQuery = managedQuery(uri, strArr, "", strArr, (String) null);
        if (managedQuery.getCount() == 0) {
            return null;
        }
        managedQuery.moveToFirst();
        return managedQuery.getString(managedQuery.getColumnIndexOrThrow("_data"));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
