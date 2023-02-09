package com.lakshitasuman.musicstation.voice_change;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.ringtone.PlayToneActivity;
import com.lakshitasuman.musicstation.ringtone.utils.AdsUtils;
import com.lakshitasuman.musicstation.voice_change.adapter.NewEffectAdepter;
import com.lakshitasuman.musicstation.voice_change.basseffect.DBMediaPlayer;
import com.lakshitasuman.musicstation.voice_change.basseffect.IDBMediaListener;
import com.lakshitasuman.musicstation.voice_change.constants.IVoiceChangerConstants;
import com.lakshitasuman.musicstation.voice_change.dataMng.JsonParsingUtils;
import com.lakshitasuman.musicstation.voice_change.dataMng.TotalDataManager;

import com.lakshitasuman.musicstation.voice_change.models.EffectModel;
import com.lakshitasuman.musicstation.voice_change.soundMng.SoundManager;
import com.lakshitasuman.musicstation.voice_change.task.DBTask;
import com.lakshitasuman.musicstation.voice_change.task.IDBCallback;
import com.lakshitasuman.musicstation.voice_change.task.IDBTaskListener;
import com.lakshitasuman.musicstation.voice_change.utils.ApplicationUtils;
import com.lakshitasuman.musicstation.voice_change.utils.DBLog;
import com.lakshitasuman.musicstation.voice_change.utils.IOUtils;
import com.lakshitasuman.musicstation.voice_change.utils.StringUtils;
import com.un4seen.bass.BASS;

import java.io.File;
import java.util.ArrayList;

public class EffectActivity extends DBFragmentActivity implements View.OnClickListener, NewEffectAdepter.OnEffectListener {
    public static final String TAG = "EffectActivity";
    private boolean isInit;
    public DBMediaPlayer mDBMedia;

    public NewEffectAdepter newEffectAdepter;
    private ArrayList<EffectModel> mListEffectObjects;

    RecyclerView effect_list;
    public String mNameExportVoice;

    EffectModel effectObject;
    public String mPathAudio;
    private TextView mTvHeader;
    LinearLayout downloadBtn;
    LinearLayout back;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_effects);
        downloadBtn=findViewById(R.id.downloadBtn);
        AdsUtils.initAd(this);
        AdsUtils.loadLargeBannerAd(this,findViewById(R.id.adsview));
        back=findViewById(R.id.back);
        AdsUtils.initAd(this);
        AdsUtils.loadBannerAd(this,findViewById(R.id.adsview));
        Intent intent = getIntent();
        if (intent != null) {
            mPathAudio = intent.getStringExtra(IVoiceChangerConstants.KEY_PATH_AUDIO);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  mSoundMng.play(EffectActivity.this, (int) R.raw.click);
                    backToHome();

            }
        });
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (effectObject==null){
                    Toast.makeText(EffectActivity.this, "please select voice", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (mDBMedia != null) {
                        resetStateAudio();
                    }
                    SoundManager.getInstance().play(EffectActivity.this, (int) R.raw.click);
                    mNameExportVoice = String.format(IVoiceChangerConstants.FORMAT_NAME_VOICE, new Object[]{String.valueOf(System.currentTimeMillis() / 1000)});
                    showDialogEnterName(new IDBCallback() {
                        @Override
                        public void onAction() {
                            if (mDBMedia != null) {
                                startSaveEffect(effectObject, new IDBCallback() {
                                    public void onAction() {
                                        File file = new File(new File(mPathAudio).getParentFile(), mNameExportVoice);
                                        if (file.exists() && file.isFile()) {
                                            showToast(String.format(getString(R.string.info_save_voice), new Object[]{file.getAbsolutePath()}));
                                            MediaScannerConnection.scanFile(EffectActivity.this, new String[]{file.getAbsolutePath()}, new String[]{"audio/*"}, (MediaScannerConnection.OnScanCompletedListener) null);
                                            Intent intent = new Intent(EffectActivity.this, PlayToneActivity.class);
                                            intent.putExtra("ActivityName", "MusicEdit");
                                            intent.putExtra("audiourl", file.getAbsolutePath());
                                            startActivity(intent);
                                            finish();

                                        }
                                    }
                                });
                            }
                        }
                    });
                }

            }
        });
        effect_list=findViewById(R.id.effect_list);
        mTvHeader = (TextView) findViewById(R.id.tv_header);
        if (!StringUtils.isEmptyString(mPathAudio)) {
            File file = new File(mPathAudio);
            if (!file.exists() || !file.isFile()) {
                showToast("File not found exception");
                backToHome();
                return;
            }
            setupInfo();
            return;
        }
        backToHome();
    }

    public void setupInfo() {
        mListEffectObjects = TotalDataManager.getInstance().getListEffectObjects();
        if (mListEffectObjects == null || mListEffectObjects.size() <= 0) {
            startLoad(new IDBCallback() {
                @Override
                public void onAction() {
                    setupInfo();
                }
            });
            return;
        }

        newEffectAdepter = new NewEffectAdepter( this.mListEffectObjects,this);
        effect_list.setLayoutManager(new GridLayoutManager(this, 3));
        newEffectAdepter.setOnEffectListener(this);
        effect_list.setAdapter(newEffectAdepter);
        onInitAudioDevice();
        createDBMedia();
    }
    private void startLoad(final IDBCallback iDBCallback) {
        new DBTask(new IDBTaskListener() {
            public ArrayList<EffectModel> mListEffects;
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }
            @Override
            public void onDoInBackground() {
                mListEffects = JsonParsingUtils.parsingListEffectObject(IOUtils.readStringFromAssets(EffectActivity.this, "effects.dat"));
                DBLog.d(EffectActivity.TAG, "===============>Size=" + mListEffects.size());
                if (mListEffects != null && mListEffects.size() > 0) {
                    mTotalMng.setListEffectObjects(mListEffects);
                }
            }
           @Override
            public void onPostExecute() {
                dismissProgressDialog();
                if (mListEffects == null || mListEffects.size() == 0) {
                    backToHome();
                    return;
                }
                if (iDBCallback != null) {
                    iDBCallback.onAction();
                }
            }
        }).execute(new Void[0]);
    }

    @Override
    public void onClick(View view) {

    }


    private void deleteMainFile() {
        if (!StringUtils.isEmptyString(mPathAudio)) {
            try {
                new File(mPathAudio).delete();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void onDestroyMedia() {
        try {
            if (mDBMedia != null) {
                mDBMedia.releaseAudio();
            }
            BASS.BASS_PluginFree(0);
            BASS.BASS_Free();
            TotalDataManager.getInstance().onResetState();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4) {
            return super.onKeyDown(i, keyEvent);
        }
        backToHome();
        return true;
    }



    public void backToHome() {
        deleteMainFile();
        onDestroyMedia();
        finish();
    }

    @Override
    public void onPlayEffect(EffectModel effectObject) {
        this.effectObject=effectObject;
        if (effectObject.isPlaying()) {
            effectObject.setPlaying(false);

            if (mDBMedia != null) {
                mDBMedia.pauseAudio();
            }
        } else {
            TotalDataManager.getInstance().onResetState();
            effectObject.setPlaying(true);

            if (mDBMedia != null) {
                mDBMedia.setReverse(effectObject.isReverse());
                mDBMedia.setAudioPitch(effectObject.getPitch());
                mDBMedia.setAudioRate(effectObject.getRate());
                mDBMedia.setAudioReverb(effectObject.getReverb());
                mDBMedia.setAudioEQ(effectObject.getEq());
                mDBMedia.setFlangerEffect(effectObject.isFlanger());
                mDBMedia.setAudioEcho(effectObject.isEcho());
                if (mDBMedia.isPlaying()) {
                    if (!effectObject.isReverse()) {
                        mDBMedia.seekTo(0);
                    } else {
                        mDBMedia.seekTo(mDBMedia.getDuration());
                    }
                }
                mDBMedia.startAudio();
            }
        }
        newEffectAdepter.notifyDataSetChanged();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mDBMedia != null) {
            resetStateAudio();
        }
    }

    @Override
    public void onShareEffect(final EffectModel effectObject) {
        if (mDBMedia != null) {
            resetStateAudio();
        }
        SoundManager.getInstance().play((Context) this, (int) R.raw.click);
        mNameExportVoice = String.format(IVoiceChangerConstants.FORMAT_NAME_VOICE, new Object[]{String.valueOf(System.currentTimeMillis() / 1000)});
        showDialogEnterName(new IDBCallback() {
          @Override
            public void onAction() {
                if (mDBMedia != null) {
                    startSaveEffect(effectObject, new IDBCallback() {
                        public void onAction() {
                            File file = new File(new File(mPathAudio).getParentFile(), mNameExportVoice);
                            if (file.exists() && file.isFile()) {
                                showToast(String.format(getString(R.string.info_save_voice), new Object[]{file.getAbsolutePath()}));
                                MediaScannerConnection.scanFile(EffectActivity.this, new String[]{file.getAbsolutePath()}, new String[]{"audio/*"}, (MediaScannerConnection.OnScanCompletedListener) null);
                                Intent intent = new Intent(EffectActivity.this, PlayToneActivity.class);
                                intent.putExtra("ActivityName", "MusicEdit");
                                intent.putExtra("audiourl", file.getAbsolutePath());
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }



    private void resetStateAudio() {
        TotalDataManager.getInstance().onResetState();

        if (newEffectAdepter != null) {
            newEffectAdepter.notifyDataSetChanged();
        }
        if (mDBMedia != null && mDBMedia.isPlaying()) {
            mDBMedia.pauseAudio();
        }
    }



    public void startSaveEffect(EffectModel effectObject, IDBCallback iDBCallback) {
        final File file = new File(new File(mPathAudio).getParentFile(), mNameExportVoice);
        final DBMediaPlayer dBMediaPlayer = new DBMediaPlayer(mPathAudio);


        new DBTask(new IDBTaskListener() {
          @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onDoInBackground() {
                if (dBMediaPlayer != null && dBMediaPlayer.initMediaToSave()) {
                    dBMediaPlayer.setReverse(effectObject.isReverse());
                    dBMediaPlayer.setAudioPitch(effectObject.getPitch());
                    dBMediaPlayer.setAudioRate(effectObject.getRate());
                    dBMediaPlayer.setAudioReverb(effectObject.getReverb());
                    dBMediaPlayer.setFlangerEffect(effectObject.isFlanger());
                    dBMediaPlayer.setAudioEcho(effectObject.isEcho());
                    dBMediaPlayer.setAudioEQ(effectObject.getEq());
                    dBMediaPlayer.saveToFile(file.getAbsolutePath());
                    dBMediaPlayer.releaseAudio();
                }
            }

            @Override
            public void onPostExecute() {
                dismissProgressDialog();
                if (iDBCallback != null) {
                    iDBCallback.onAction();
                }
            }
        }).execute(new Void[0]);
    }


    public void onInitAudioDevice() {
        if (!isInit) {
            isInit = true;
            if (!BASS.BASS_Init(-1, IVoiceChangerConstants.RECORDER_SAMPLE_RATE, 0)) {
                new Exception(TAG + " Can't initialize device").printStackTrace();
                isInit = false;
                return;
            }
            String str = getApplicationInfo().nativeLibraryDir;
            try {
                BASS.BASS_PluginLoad(str + "/libbass_fx.so", 0);
                BASS.BASS_PluginLoad(str + "/libbassmix.so", 0);
                BASS.BASS_PluginLoad(str + "/libbasswv.so", 0);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void createDBMedia() {
        if (!StringUtils.isEmptyString(mPathAudio)) {
            mDBMedia = new DBMediaPlayer(mPathAudio);
            mDBMedia.prepareAudio();
            mDBMedia.setOnDBMediaListener(new IDBMediaListener() {
                @Override
                public void onMediaError() {
                }

                public void onMediaCompletion() {
                    TotalDataManager.getInstance().onResetState();
                    newEffectAdepter.notifyDataSetChanged();
                }
            });
        }
    }
    private String getIdFromContentUri(String str) {
        if (str == null) {
            return null;
        }
        try {
            String[] strArr = {"_id"};
            Cursor query = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, strArr, "_data = ?", new String[]{str}, (String) null);
            if (query == null || !query.moveToFirst()) {
                return null;
            }
            String string = query.getString(query.getColumnIndex(strArr[0]));
            query.close();
            return string;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private void showDialogEnterName(final IDBCallback iDBCallback) {
        final Dialog dialog = new Dialog(EffectActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_rename_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        EditText edtName= dialog.findViewById(R.id.edtName);
        TextView btnSkip= dialog.findViewById(R.id.btnSkip);
        TextView btnOk= dialog.findViewById(R.id.btnOk);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ApplicationUtils.hiddenVirtualKeyboard(EffectActivity.this, edtName);
                String obj = edtName.getText().toString();
                if (!StringUtils.isEmptyString(obj)) {
                    if (StringUtils.isContainsSpecialCharacter(obj)) {
                        showToast((int) R.string.info_your_name_error);
                        return;
                    }
                    mNameExportVoice = obj + IVoiceChangerConstants.AUDIO_RECORDER_FILE_EXT_WAV;
                    dialog.dismiss();
                }

                if (iDBCallback != null) {
                    iDBCallback.onAction();
                }
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iDBCallback != null) {
                    iDBCallback.onAction();
                }
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
