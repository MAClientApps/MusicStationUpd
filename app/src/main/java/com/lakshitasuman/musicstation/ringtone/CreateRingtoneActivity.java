package com.lakshitasuman.musicstation.ringtone;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.czt.mp3recorder.MP3Recorder;
import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.musicplayer.PlayerActivity;
import com.lakshitasuman.musicstation.radio.service.PauseReason;
import com.lakshitasuman.musicstation.radio.service.PlayerServiceUtil;
import com.lakshitasuman.musicstation.ringtone.adapter.AddBgMusic;
import com.lakshitasuman.musicstation.ringtone.adapter.OnMyCommonItem;
import com.lakshitasuman.musicstation.ringtone.asyncfol.AsyncEcho;
import com.lakshitasuman.musicstation.ringtone.asyncfol.AsyncMergeMp3;
import com.lakshitasuman.musicstation.ringtone.asyncfol.AsyncMp3Overlay;
import com.lakshitasuman.musicstation.ringtone.asyncfol.OnCommonProgress;
import com.lakshitasuman.musicstation.ringtone.helper.SeekbarVC;
import com.lakshitasuman.musicstation.ringtone.utils.AdsUtils;
import com.lakshitasuman.musicstation.ringtone.utils.ContentUtill;

import com.lakshitasuman.musicstation.ringtone.utils.MyStopWatch;
import com.lakshitasuman.musicstation.ringtone.utils.MyUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class CreateRingtoneActivity extends Activity implements TextToSpeech.OnInitListener {
    private static final int REQ_CUT = 11;
    private static final int REQ_EFF = 12;
    private static final int REQ_POST = 14;
    private static final int REQ_PRE = 13;
    private static final int REQ_SPEECH_INPUT = 10;
    public static TextToSpeech txtSpeech;
    String cutPath="";
    int Enable_Text = 1;
    private FrameLayout adContainerView;
    RelativeLayout btn_back;
    LinearLayout btn_play;
    TextView btn_prefix;
    LinearLayout btn_save;
    TextView btn_set;
    LinearLayout btn_share;
    TextView btnDecay;
    TextView btnDelay;
    ImageView btnDeleteRecord;
    ImageView btnEcho;
    TextView btnFemale;
    TextView btnInGain;
    TextView btnMale;
    TextView btnOutGain;
    ImageView btnRecord;
    ImageView btnVoice;
    long cMills;
    Context cn = this;
    String customAfterText;
    String decay = "110";
    String delay = "110";
    Dialog dialog;
    EditText edit_txt;
    File f;
    private String fileName = null;
    StringBuilder final_speak = new StringBuilder();
    String inGain = "0.6";
    int intCommon = 0;
    Boolean isText = false;
    int languagePosition;
    SharedPreferences languageSharedPref;
    LinearLayout layBgMusic;
    LinearLayout layBgVolume;
    LinearLayout layEcho;
    LinearLayout layEdit;
    LinearLayout layMain;
    LinearLayout layMainEcho;
    LinearLayout layPitch;
    LinearLayout layPostFix;
    LinearLayout layPreFix;
    LinearLayout laySpeechRate;
    LinearLayout layVoice;
    LinearLayout first_lin;
    LinearLayout layprefix;
    LinearLayout layedit;
    LinearLayout layvoice;
    LinearLayout topbar;
    LinearLayout layVolume;
    private MediaRecorder mRecorder;
    File mainAudio;
    private String maleoffemale = "#female";
    MediaPlayer mediaPlayer;
    MediaPlayer mediaPlayer1;
    MP3Recorder mp3Recorder;
    OnMyMediaComplete onMyMediaComplete;
    String outGain = "0.6";
    String path5 = "";
    float pitch = 1.0f;
    float rate = 1.0f;
    Boolean recording = false;
    SeekBar seekBarVolume;
    SeekBar seekBgVolume;
    SeekbarVC seekPitch;
    SeekbarVC seekRate;
    String selBgMusic = "";
    TextView setBg;
    TextView setBgVolume;
    ImageView setIconBg;
    ImageView setIconPitch;
    ImageView seticonSpeechRate;
    TextView setRecord;
    SharedPreferences sharedPref;
    Uri shareUri;
    Spinner spInGain;
    final ArrayList<String> spArrayIn = new ArrayList<>();
    final ArrayList<String> spArrayDecay = new ArrayList<>();
    final ArrayList<String> spArrayDelay = new ArrayList<>();
    final ArrayList<String> spArrayOut = new ArrayList<>();
    Spinner spDecay;
    Spinner spDelay;
    Spinner spOutGain;
    MyStopWatch stopWatch;
    TextView textViewVolume;
    TextView txtPhone;
    TextView txtRefix;
    int uttRid = 0;

    public interface OnMyMediaComplete {
        void OnComplete();
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_create_ringtone);
        isText = Boolean.valueOf(getIntent().getBooleanExtra("text", false));
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                StrictMode.class.getMethod("disableDeathOnFileUriExposure", new Class[0]).invoke((Object) null, new Object[0]);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        AdsUtils.initAd(this);
        AdsUtils.loadLargeBannerAd(this,findViewById(R.id.adsView));
        sharedPref = getSharedPreferences("Position", 0);
        languageSharedPref = getSharedPreferences("LanguagePosition", 0);
        languagePosition = languageSharedPref.getInt("languagePosition", 0);
        new File(Environment.getExternalStorageDirectory() + "/" + cn.getResources().getString(R.string.app_name)).mkdirs();
        mainAudio = new File(getFilesDir(), "audio.mp3");
        cMills = System.currentTimeMillis();
        deleteAllTmp(true);
        txtSpeech = new TextToSpeech(this, this);
        Enable_Text = 1;
        btn_save = (LinearLayout) findViewById(R.id.btn_save);
        btn_back = (RelativeLayout) findViewById(R.id.back);
        btn_share = (LinearLayout) findViewById(R.id.btn_share);
        btn_play = (LinearLayout) findViewById(R.id.btn_play);
        edit_txt = (EditText) findViewById(R.id.edit_txt);
        btn_prefix = (TextView) findViewById(R.id.btn_prefix);
        btnRecord = (ImageView) findViewById(R.id.btnrecord);
        btnVoice = (ImageView) findViewById(R.id.btnvoice);
        btnDeleteRecord = (ImageView) findViewById(R.id.btndeleterecord);
        layEdit = (LinearLayout) findViewById(R.id.layedit);
        layVoice = (LinearLayout) findViewById(R.id.layvoice);
        setBg = (TextView) findViewById(R.id.setbg);
        seekRate = (SeekbarVC) findViewById(R.id.seekrate);
        seekPitch = (SeekbarVC) findViewById(R.id.seekpitch);
        layEcho = (LinearLayout) findViewById(R.id.layecho);
        btnEcho = (ImageView) findViewById(R.id.btnecho);
        layMainEcho = (LinearLayout) findViewById(R.id.laymainecho);
        layMain = (LinearLayout) findViewById(R.id.laymain);
        setRecord = (TextView) findViewById(R.id.setrecord);
        txtRefix = (TextView) findViewById(R.id.txt_refix);
        txtRefix.setText("Hey");
        txtPhone = (TextView) findViewById(R.id.txt_phone);
        btn_set = (TextView) findViewById(R.id.btn_set);
        layPreFix = (LinearLayout) findViewById(R.id.layprefix);
        layedit = (LinearLayout) findViewById(R.id.layedit);
        layPostFix = (LinearLayout) findViewById(R.id.laypostfix);
        layVolume = (LinearLayout) findViewById(R.id.layvolume);
        layBgMusic = (LinearLayout) findViewById(R.id.laybgmusic);
        setIconBg = (ImageView) findViewById(R.id.seticonbg);
        laySpeechRate = (LinearLayout) findViewById(R.id.layspeechrate);
        seticonSpeechRate = (ImageView) findViewById(R.id.seticonspeechrate);
        layPitch = (LinearLayout) findViewById(R.id.laypitch);
        setIconPitch = (ImageView) findViewById(R.id.seticonpitch);
        btnInGain = (TextView) findViewById(R.id.btningain);
        topbar = (LinearLayout) findViewById(R.id.topbar);
        btnOutGain = (TextView) findViewById(R.id.btnoutgain);
        btnDelay = (TextView) findViewById(R.id.btndelay);
        btnDecay = (TextView) findViewById(R.id.btndecay);
        seekBgVolume = (SeekBar) findViewById(R.id.seekbgvolume);
        setBgVolume = (TextView) findViewById(R.id.setbgvolume);
        layBgVolume = (LinearLayout) findViewById(R.id.laybgvolume);
        spInGain = (Spinner) findViewById(R.id.spIngain);
        spOutGain = (Spinner) findViewById(R.id.spoutgain);
        spDelay = (Spinner) findViewById(R.id.spdelay);
        first_lin =  findViewById(R.id.first_lin);
        layprefix =  findViewById(R.id.layprefix);
        spDecay = (Spinner) findViewById(R.id.spdecay);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        generateArrayListRange(this.spArrayIn, 0.1f, 1.09f, 0.1f);
        generateArrayListRange(this.spArrayOut, 0.1f, 1.09f, 0.1f);
        generateArrayListRangeFor(this.spArrayDelay, 100, 750, 10);
        generateArrayListRange(this.spArrayDecay, 0.1f, 1.09f, 0.1f);
        spInGain.setAdapter(new ArrayAdapter(this, R.layout.custome_spinner, spArrayIn));
        spInGain.setSelection(5);
        spOutGain.setAdapter(new ArrayAdapter(this, R.layout.custome_spinner, spArrayOut));
        spOutGain.setSelection(5);
        spDelay.setAdapter(new ArrayAdapter(this, R.layout.custome_spinner, spArrayDelay));
        spDelay.setSelection(5);
        spDecay.setAdapter(new ArrayAdapter(this, R.layout.custome_spinner, spArrayDecay));
        spDecay.setSelection(5);
        click();
        seekChange();
        seekRate.setMinVal(0.0f);
        seekRate.setMaxVal(2.0f);
        seekRate.setSteps(0.1f);
        seekRate.setProgress(1.0f);
        seekPitch.setMinVal(0.0f);
        seekPitch.setMaxVal(2.0f);
        seekPitch.setSteps(0.1f);
        seekPitch.setProgress(1.0f);
        int parseInt = Integer.parseInt(this.btnEcho.getTag().toString());
        intCommon = parseInt;
        setlayEcho(parseInt);
        if (this.isText.booleanValue()) {
            layEdit.setVisibility(View.VISIBLE);
            layVoice.setVisibility(View.GONE);
            layMainEcho.setVisibility(View.VISIBLE);
        } else {
            layEdit.setVisibility(View.GONE);
            layVoice.setVisibility(View.VISIBLE);
            layMainEcho.setVisibility(View.GONE);
        }
        deleteRecordVisiblityGone();
        txtPhone.setText("Your Phone is ringing , please answer the phone");
        customAfterText = "Your Phone is ringing , please answer the phone";
        final String[] strArr = {"None", "Your Phone is ringing , Please answer the phone", "Please answer the phone", "Your phone is ringing", "Please pick up call", "Someone is calling you, take your phone", "Please receive your call", "Please check your phone, Call is receiving", "Your phone is ringing please take a look"};
        btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtSpeech != null) {
                    txtSpeech.stop();
                }
                startActivityForResult(new Intent(cn, PrePostActivity.class).putExtra("list", new ArrayList(Arrays.asList(strArr))).putExtra("ispre", false).putExtra("selected", txtPhone.getText().toString()), 14);
            }
        });
        textViewVolume = (TextView) findViewById(R.id.textViewVolume);
        seekBarVolume = (SeekBar) findViewById(R.id.seekBarVolume);
        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        final float streamMaxVolume = (float) audioManager.getStreamMaxVolume(3);
        seekBarVolume.setMax(100);
        int round = Math.round((((float) audioManager.getStreamVolume(3)) * 100.0f) / streamMaxVolume);
        seekBarVolume.setProgress(round);
        textViewVolume.setText(round + "%");
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

       @Override
           public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {

           textViewVolume.setText("" + i + "%");
                audioManager.setStreamVolume(3, Math.round((streamMaxVolume * ((float) i)) / 100.0f), 0);
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
          public void onClick(View view) {
                if (txtSpeech != null) {
                    txtSpeech.stop();
                }
                String trim = edit_txt.getText().toString().trim();
                if (mainAudio.exists() || !trim.isEmpty()) {
                    String charSequence = txtRefix.getText().toString();
                    String charSequence2 = txtPhone.getText().toString();
                    if (mainAudio.exists()) {
                        new Save_RingTone_Main(charSequence, charSequence2, false).execute(new Void[0]);
                        return;
                    }
                    final_speak.setLength(0);
                    new Save_RingTone(false).execute(new Void[0]);
                    return;
                }
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.enter_data), Toast.LENGTH_SHORT).show();
            }
        });
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View view) {
                if (txtSpeech != null) {
                    txtSpeech.stop();
                }
                releaseMedia1();
                if (isText.booleanValue()) {
                    new Save_RingTone(true).execute(new Void[0]);
                    return;
                }
                new Save_RingTone_Main(txtRefix.getText().toString(), txtPhone.getText().toString(), true).execute(new Void[0]);
            }
        });
        btn_play.setOnClickListener(new View.OnClickListener() {
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

                if (txtSpeech != null && txtSpeech.isSpeaking()) {
                    txtSpeech.stop();
                }
                pauseMedia();
                final_speak.setLength(0);
                new File(Environment.getExternalStorageDirectory() + "/" + cn.getResources().getString(R.string.app_name)).mkdirs();
                mainAudio = new File(cutPath);
                String trim = edit_txt.getText().toString().trim();
                if (!mainAudio.exists() && trim.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.enter_data), Toast.LENGTH_SHORT).show();
                } else if (!mainAudio.exists() || recording.booleanValue()) {
                    if (selBgMusic.length() > 0) {
                        Log.e("audio path new",selBgMusic);
                        initMedia1(selBgMusic, new OnMyMediaComplete() {
                            public void OnComplete() {
                            }
                        });
                    }
                    if (!txtRefix.getText().toString().equalsIgnoreCase("None")) {
                        final_speak.append(txtRefix.getText().toString());
                    }
                    final_speak.append(edit_txt.getText().toString());
                    if (!txtPhone.getText().toString().equalsIgnoreCase("None")) {
                        final_speak.append(txtPhone.getText().toString());
                    }
                    if (txtSpeech != null) {
                        String sb = final_speak.toString();
                        int onUtteranceProgressListener = txtSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onStart(String str) {
                                Log.e("AAA", "tts start");
                            }


                            @Override
                            public void onDone(String str) {
                                Log.e("AAA", "tts done");
                                if (intCommon == 1) {
                                    File file = new File(getFilesDir(), "audiotmp.mp3");
                                    Log.e("file path",file.getPath());
                                    if (file.exists()) {
                                        new AsyncEcho(cn, file.getAbsolutePath(), new File(getFilesDir(), "audiooptmp.mp3").getAbsolutePath(), inGain, outGain, delay, decay, new OnCommonProgress() {
                                            public void OnComplete(Boolean bool, Object obj) {
                                                Log.e("AAA", "Echo Success : " + bool + " " + obj);
                                                initmedia((String) obj, new OnMyMediaComplete() {
                                                    public void OnComplete() {
                                                        releaseMedia1();
                                                    }
                                                });
                                            }
                                        }).execute(new Void[0]);
                                        return;
                                    }
                                    return;
                                }
                                releaseMedia1();
                            }

                            public void onError(String str) {
                                Log.e("AAA", "tts error : " + str);
                            }
                        });
                        if (intCommon == 0) {
                            callSpeak(sb, String.valueOf(onUtteranceProgressListener));
                        } else {
                            callSyntheSize(sb, new File(getFilesDir(), "audiotmp.mp3").getAbsolutePath(), String.valueOf(onUtteranceProgressListener));
                        }
                    } else {
                        Log.e("", "===Not Present Speech");
                    }
                } else {
                    if (selBgMusic.length() > 0) {
                        initMedia1(selBgMusic, new OnMyMediaComplete() {
                            public void OnComplete() {
                            }
                        });
                    }
                    String charSequence = txtRefix.getText().toString();
                    final String charSequence2 = txtPhone.getText().toString();
                    if (txtSpeech == null) {
                        Log.e("", "===Not Present Speech");
                    } else if (charSequence.equalsIgnoreCase("None")) {
                        pauseMedia();
                        initmedia(mainAudio.getAbsolutePath(), new OnMyMediaComplete() {
                            @Override
                            public void OnComplete() {
                                int onUtteranceProgressListener = txtSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                                    @Override
                                    public void onError(String str) {
                                    }

                                    @Override
                                    public void onStart(String str) {
                                    }

                                    @Override
                                    public void onDone(String str) {
                                        releaseMedia1();
                                    }
                                });
                                if (!charSequence2.equalsIgnoreCase("None")) {
                                    callSpeak(charSequence2, String.valueOf(onUtteranceProgressListener));
                                } else {
                                    releaseMedia1();
                                }
                            }
                        });
                    } else {
                        uttRid = txtSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onStart(String str) {
                                Log.e("AAA", "tts start");
                            }

                            @Override
                            public void onDone(String str) {
                                Log.e("AAA", "tts done");
                                pauseMedia();
                                initmedia(mainAudio.getAbsolutePath(), new OnMyMediaComplete() {
                                    @Override
                                    public void OnComplete() {
                                        if (!charSequence2.equalsIgnoreCase("None")) {
                                            callSpeak(charSequence2, String.valueOf(txtSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                                                public void onError(String str) {
                                                }

                                                public void onStart(String str) {
                                                }

                                                public void onDone(String str) {
                                                    releaseMedia1();
                                                }
                                            })));
                                        } else {
                                            releaseMedia1();
                                        }
                                    }
                                });
                            }

                            public void onError(String str) {
                                Log.e("AAA", "tts error : " + str);
                            }
                        });
                        callSpeak(charSequence, String.valueOf(uttRid));
                    }
                }
            }
        });
        final String[] strArr2 = {"None", "Hey", "Hi", "Mister", "Miss", "Doctor", "Dear", "Excuse me", "Officer", "Detective", "Colonel", "Chief", "What's up"};
        btn_prefix.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
                if (txtSpeech != null) {
                    txtSpeech.stop();
                }
                startActivityForResult(new Intent(cn, PrePostActivity.class).putExtra("list", new ArrayList(Arrays.asList(strArr2))).putExtra("ispre", true).putExtra("selected", txtRefix.getText().toString()), 13);
            }
        });
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new File(Environment.getExternalStorageDirectory() + "/NameRingtone").mkdirs();
                mainAudio = new File(getFilesDir(), "audio.mp3");
                if (!mainAudio.exists() || recording.booleanValue()) {
                    if (!recording.booleanValue()) {
                        displayRecordingDialog();
                    }
                } else if (mediaPlayer == null) {
                    initmedia(mainAudio.getAbsolutePath(), (OnMyMediaComplete) null);
                } else if (mediaPlayer.isPlaying()) {
                    pauseMedia();
                } else {
                    playMedia();
                }
            }
        });
        layVoice.setOnClickListener(new View.OnClickListener() {
             @Override
            public void onClick(View view) {
                btnRecord.performClick();
            }
        });
    }


    public void setlayEcho(int i) {
        if (i == 0) {
            layEcho.setVisibility(View.GONE);
            btnEcho.setImageResource(R.drawable.off);
            return;
        }
        layEcho.setVisibility(View.VISIBLE);
        btnEcho.setImageResource(R.drawable.on);
    }

    private void click() {

        spInGain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                inGain = spArrayIn.get(i);
                btnInGain.setText(getResources().getString(R.string.in_gain) + " " + inGain);
            }
        });
        spOutGain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
           @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                outGain = spArrayOut.get(i);
                btnOutGain.setText(getResources().getString(R.string.out_gain) + " " + outGain);
            }
        });
        spDelay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                delay = spArrayDelay.get(i);
                btnDelay.setText(getResources().getString(R.string.delay) + " " + delay);
            }
        });
        spDecay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                decay = spArrayDecay.get(i);
                btnDecay.setText(getResources().getString(R.string.delay) + " " + decay);
            }
        });
        btnEcho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intCommon = Integer.parseInt(btnEcho.getTag().toString());
                intCommon = intCommon == 0 ? 1 : 0;
                btnEcho.setTag(Integer.valueOf(intCommon));
                setlayEcho(intCommon);
            }
        });
        setBg.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
                displaybgmusic();
            }
        });
        btnVoice.setOnClickListener(new View.OnClickListener() {
         @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });
        btnDeleteRecord.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
                if (mainAudio.exists()) {
                    mainAudio.delete();
                }
                deleteRecordVisiblityGone();
            }
        });
        seekRate.setOnSeekBarChangeListener(new SeekbarVC.OnMySeekChange() {
           @Override
            public void onChange(int i, float f) {
                txtSpeech.setSpeechRate(f);
                Log.e("AAA", "Speech Rate : " + f);
            }
        });
        seekPitch.setOnSeekBarChangeListener(new SeekbarVC.OnMySeekChange() {
            @Override
            public void onChange(int i, float f) {
                txtSpeech.setPitch(f);
                Log.e("AAA", "Pitch Rate : " + f);
            }
        });
    }


    public void generateArrayListRange(ArrayList<String> arrayList, float f2, float f3, float f4) {
        while (f2 <= f3) {
            float formatFloat = MyUtils.formatFloat(f2);
            arrayList.add(String.valueOf(formatFloat));
            f2 = formatFloat + f4;
        }
    }

    public void generateArrayListRangeFor(ArrayList<String> arrayList, int i, int i2, int i3) {
        while (i <= i2) {
            arrayList.add(String.valueOf(i));
            i += i3;
        }
    }

    public void seekChange() {
        seekBgVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                setBgVolume.setText("" + i + "%");
                setvolumeMedia1((float) i);
            }
        });
    }

    public void displaybgmusic() {
        final Dialog dialog2 = new Dialog(cn);

        dialog2.requestWindowFeature(1);
        dialog2.setContentView(R.layout.dialog_bgmusic);
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView recyclerView = (RecyclerView) dialog2.findViewById(R.id.recyclerView);
        final AddBgMusic adBgMusic = new AddBgMusic(cn, setBg.getText().toString(), new OnMyCommonItem() {
            public void OnMyClick1(int i, Object obj) {
            }

            public void OnMyClick2(int i, Object obj, Object obj2) {
                String str = (String) obj;
                if (str.equalsIgnoreCase("None")) {
                    selBgMusic = "";
                    setIconBg.setVisibility(View.VISIBLE);
                } else {
                    selBgMusic = (String) obj2;
                    setIconBg.setVisibility(View.GONE);
                }
                setBg.setText(str);
                Log.e("AAA", "Selected Bg Music : " + str + " path : " + selBgMusic);
                dialog2.dismiss();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(cn, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adBgMusic);
        dialog2.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                adBgMusic.stopMusic();
            }
        });
        dialog2.show();
    }

    public void initmedia(String str, final OnMyMediaComplete onMyMediaComplete2) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(str);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
               @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    playMedia();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    btnRecord.setImageResource(R.drawable.play);
                    if (onMyMediaComplete2 != null) {
                        onMyMediaComplete2.OnComplete();
                    }
                }
            });
            mediaPlayer.prepareAsync();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void initMedia1(String str, final OnMyMediaComplete onMyMediaComplete2) {
        releaseMedia1();
        mediaPlayer1 = new MediaPlayer();
        try {
            mediaPlayer1.setDataSource(str);
            mediaPlayer1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    playMedia1();
                }
            });
            mediaPlayer1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
               @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    btnRecord.setImageResource(R.drawable.play);
                    onMyMediaComplete2.OnComplete();
                }
            });
            mediaPlayer1.prepareAsync();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void setvolumeMedia1(float f2) {
        if (mediaPlayer1 != null) {
            float log = (float) (1.0d - (Math.log((double) (100.0f - f2)) / Math.log(100.0d)));
            Log.e("AAA", "Volume Real : " + f2 + " & Volume Calculated : " + log);
            try {
                mediaPlayer1.setVolume(log, log);
            } catch (Exception exceptionxception) {
            }
        }
    }

    public void playMedia1() {
        if (mediaPlayer1 != null) {
            setvolumeMedia1((float) seekBgVolume.getProgress());
            if (!mediaPlayer1.isPlaying()) {
                mediaPlayer1.start();
            }
        }
    }

    public void releaseMedia1() {
        try {
            if (mediaPlayer1 != null) {
                if (mediaPlayer1.isPlaying()) {
                    mediaPlayer1.stop();
                }
                mediaPlayer1.release();
            }
        } catch (Exception exceptionxception) {
        }
    }

    public void releaseMedia() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
    }

    public void playMedia() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            btnRecord.setImageResource(R.drawable.pause);
        }
    }
    public void pauseMedia() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();

        }
    }

   @Override
    public void onPause() {
        pauseMedia();
        super.onPause();
    }

    @Override
    public void onInit(int i) {
        if (i == 0) {
            int language = txtSpeech.setLanguage(RingToneMainActivity.languages[languagePosition]);
            if (language == -1 || language == -2) {
                Log.e("TTS", "Language is not supported");
                return;
            }
            return;
        }
        Log.e("TTS", "Initilization Failed");
    }

    @Override
    public void onDestroy() {
        Log.e("AAA", "Destory");
        if (txtSpeech != null) {
            txtSpeech.stop();
            txtSpeech.shutdown();
        }
        releaseMedia();
        deleteAllTmp(true);
        super.onDestroy();
    }

    public void scanMedia(String str) {
        if (Build.VERSION.SDK_INT == 19) {
            Log.e("", "android Kitkat Version File " + Build.VERSION.SDK_INT);
            String substring = str.substring(str.lastIndexOf("/") + 1);
            String substring2 = substring.substring(0, substring.lastIndexOf("."));
            ContentValues contentValues = new ContentValues();
            contentValues.put("_data", str);
            contentValues.put("title", substring2);
            contentValues.put("_size", Integer.valueOf(str.length()));
            contentValues.put("mime_type", "audio/mp3");
            contentValues.put("artist", getResources().getString(R.string.app_name));
            contentValues.put("is_ringtone", true);
            contentValues.put("is_notification", false);
            contentValues.put("is_alarm", false);
            contentValues.put("is_music", false);
            Uri contentUriForPath = MediaStore.Audio.Media.getContentUriForPath(str);
            Log.e("", "=====Enter ====" + contentUriForPath);
            ContentResolver contentResolver = getContentResolver();
            contentResolver.delete(contentUriForPath, "_data=\"" + str + "\"", (String[]) null);
            shareUri = getApplicationContext().getContentResolver().insert(contentUriForPath, contentValues);

            Log.e("", "====file Scan path type%%%% path" + shareUri);
            return;
        }
        Log.e("", "android Version File" + Build.VERSION.SDK_INT);
        shareUri= Uri.fromFile(new File(str));
        Log.e("", "android uri :::" + shareUri);
        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", shareUri));
    }

    class Save_RingTone_Main extends AsyncTask<Void, Void, Void> {
        String destinationFileName;
        Boolean isShare;
        ProgressDialog pd = null;
        String postfix;
        String prefix;

        @Override
        public Void doInBackground(Void... voidArr) {
            return null;
        }

        public Save_RingTone_Main(String str, String str2, Boolean bool) {
            prefix = str;
            postfix = str2;
            isShare = bool;
        }

        @Override
        public void onPreExecute() {
            pd = new ProgressDialog(cn);
            pd.setMessage(getResources().getString(R.string.create_msg));
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        public void onPostExecute(Void voidR) {
            super.onPostExecute(voidR);
            textTomp3(prefix, "prefix", (String) null);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    textTomp3(postfix, "postfix", (String) null);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String str = MyUtils.gettimestring(System.currentTimeMillis(), "dd_MM_hh_mm_ss");
                            destinationFileName = MyUtils.NameLocation + "/Ring_" + str + "_namering.mp3";
                            if (selBgMusic.length() > 0) {
                                path5 = new File(getFilesDir(), "audiotmp.mp3").getAbsolutePath();
                                if (new File(path5).exists()) {
                                    new File(path5).delete();
                                }
                            } else {
                                path5 = destinationFileName;
                            }
                            File filesDir = getFilesDir();
                            new AsyncMergeMp3(cn, new File(filesDir, "prefix.mp3").getAbsolutePath(), mainAudio.getAbsolutePath(), new File(filesDir, "postfix.mp3").getAbsolutePath(), path5, new OnCommonProgress() {
                                @Override
                                public void OnComplete(Boolean bool, Object obj) {
                                    Log.e("AAA", "Is Completed : " + bool);
                                    if (selBgMusic.length() > 0) {
                                        new AsyncMp3Overlay(cn, path5, selBgMusic, destinationFileName, seekBgVolume.getProgress(), new OnCommonProgress() {
                                           @Override
                                            public void OnComplete(Boolean bool, Object obj) {
                                                Log.e("AAA", "is complete : " + bool + " msg  : " + obj);
                                                if (pd != null && pd.isShowing()) {
                                                    pd.dismiss();
                                                }
                                                if (!isShare.booleanValue()) {
                                                    deleteAllTmp(false);
                                                }
                                                Set_Ringtone(destinationFileName);
                                                scanMedia(destinationFileName);
                                                if (isShare.booleanValue()) {
                                                    Intent intent = new Intent("android.intent.action.SEND");
                                                    intent.setType("audio/*");
                                                    intent.putExtra("android.intent.extra.STREAM", shareUri);
                                                    startActivity(Intent.createChooser(intent, "Share File"));
                                                    return;
                                                }
                                                runOnUiThread(new Runnable() {
                                                     @Override
                                                    public void run() {
                                                        Intent intent = new Intent(cn, PlayToneActivity.class);
                                                        intent.putExtra("audiourl", destinationFileName);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                            }
                                        }).execute(new Void[0]);
                                        return;
                                    }
                                    if (pd != null && pd.isShowing()) {
                                        pd.dismiss();
                                    }
                                    if (!isShare.booleanValue()) {
                                        deleteAllTmp(false);
                                    }
                                    Set_Ringtone(destinationFileName);
                                    scanMedia(destinationFileName);
                                    if (isShare.booleanValue()) {
                                        Intent intent = new Intent("android.intent.action.SEND");
                                        intent.setType("audio/*");
                                        intent.putExtra("android.intent.extra.STREAM", shareUri);
                                        startActivity(Intent.createChooser(intent, "Share File"));
                                        return;
                                    }
                                    runOnUiThread(new Runnable() {
                                       @Override
                                        public void run() {
                                            Intent intent = new Intent(cn, PlayToneActivity.class);
                                            intent.putExtra("audiourl", destinationFileName);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            }).execute(new Void[0]);
                        }
                    }, 3000);
                }
            }, 3000);
        }
    }

    public void textTomp3(String str, String str2, String str3) {
        if (!str.equalsIgnoreCase("None")) {
            File filesDir = getFilesDir();
            f = new File(filesDir, str2 + ".mp3");
            txtSpeech.setOnUtteranceProgressListener((UtteranceProgressListener) null);
            callSyntheSize("" + str, f.getAbsolutePath(), str3);
            Log.e("AAA", "file make : " + str2);
        }
    }

    class Save_RingTone extends AsyncTask<Void, Void, Boolean> {
        String destinationFileName;
        Cursor ecursor = null;
        Boolean isShare;
        ProgressDialog pd = null;

        public Save_RingTone(Boolean bool) {
            isShare = bool;
        }

        @Override
        public void onPreExecute() {
            pd = new ProgressDialog(cn);
            pd.setMessage(getResources().getString(R.string.create_msg));
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        public Boolean doInBackground(Void... voidArr) {
            String charSequence = txtRefix.getText().toString();
            if (!charSequence.equalsIgnoreCase("None")) {
                final_speak.append(charSequence);
            }
            final_speak.append(edit_txt.getText().toString());
            if (!txtPhone.getText().toString().equalsIgnoreCase("None")) {
                final_speak.append(txtPhone.getText().toString());
            }
            f = MyUtils.NameLocation;
            if (!f.exists()){
                f.mkdirs();
            }
            destinationFileName = f + "/" + edit_txt.getText().toString().toLowerCase() + cMills + "_namering.mp3";
            checkFile(destinationFileName);
            path5 = new File(getFilesDir(), "audiotmp.mp3").getAbsolutePath();
            if (selBgMusic.length() > 0 || intCommon != 0) {
                path5 = new File(getFilesDir(), "audiotmp.mp3").getAbsolutePath();
            } else {
                path5 = destinationFileName;
            }

            Log.e("file path",path5);
            return true;
        }

        @Override
        public void onPostExecute(Boolean bool) {
            callSyntheSize(final_speak.toString(), path5, String.valueOf(txtSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String str) {
                }

                @Override
                public void onDone(String str) {
                    Log.e("AAA", "File Created");
                    if (intCommon == 1) {
                        File file = new File(path5);
                        if (file.exists()) {
                            new AsyncEcho(cn, file.getAbsolutePath(), new File(selBgMusic.length() > 0 ? new File(getFilesDir(), "audiooptmp.mp3").getAbsolutePath() : Save_RingTone.this.destinationFileName).getAbsolutePath(), inGain, outGain, delay, decay, new OnCommonProgress() {
                                @Override
                                public void OnComplete(Boolean bool, Object obj) {
                                    path5 = (String) obj;
                                    saveSynth(destinationFileName, pd, isShare);
                                }
                            }).execute(new Void[0]);
                            return;
                        }
                        return;
                    }
                    saveSynth(destinationFileName, pd, isShare);
                }

                @Override
                public void onError(String str) {
                    Log.e("AAA", "File Created Error : " + str);
                }
            })));
        }
    }


    public void saveSynth(final String str, final ProgressDialog progressDialog, Boolean bool) {
        if (selBgMusic.length() > 0) {
            new AsyncMp3Overlay(cn, path5, selBgMusic, str, seekBgVolume.getProgress(), new OnCommonProgress() {
                @Override
                public void OnComplete(Boolean bool, Object obj) {
                    Log.e("AAA", "is complete : " + bool + " msg  : " + obj);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Set_Ringtone(str);
                    scanMedia(str);
                    if (bool.booleanValue()) {
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType("audio/*");
                        intent.putExtra("android.intent.extra.STREAM", shareUri);
                        startActivity(Intent.createChooser(intent, "Share File"));
                        return;
                    }
                    runOnUiThread(new Runnable() {
                       @Override
                        public void run() {
                            Intent intent = new Intent(cn, PlayToneActivity.class);
                            intent.putExtra("audiourl", str);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }).execute(new Void[0]);
            return;
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        Set_Ringtone(str);
        scanMedia(str);
        if (bool.booleanValue()) {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("audio/*");
            intent.putExtra("android.intent.extra.STREAM", shareUri);
            startActivity(Intent.createChooser(intent, "Share File"));
            return;
        }
        runOnUiThread(new Runnable() {
              @Override
            public void run() {
                Intent intent = new Intent(cn, PlayToneActivity.class);
                intent.putExtra("audiourl", str);
                startActivity(intent);
                finish();
            }
        });
    }


    public void checkFile(String str) {
        try {
            File file = new File(str);
            if (file.exists()) {
                file.delete();
                try {
                    Uri contentUriForPath = MediaStore.Audio.Media.getContentUriForPath(str);
                    Log.e("", "=====Enter ====" + contentUriForPath);
                    ContentResolver contentResolver = getContentResolver();
                    contentResolver.delete(contentUriForPath, "_data=\"" + str + "\"", (String[]) null);
                } catch (Exception exception) {
                    Log.e("", "=====Error ====" + exception.toString());
                }
            }
        } catch (Exception exception2) {
            Log.e("", "=====Error ====" + exception2.toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        finish();
        if (txtSpeech != null && txtSpeech.isSpeaking()) {
            txtSpeech.stop();
        }
        super.onBackPressed();
    }

    public void Set_Ringtone(String str) {
        String substring = str.substring(str.lastIndexOf("/") + 1);
        String substring2 = substring.substring(0, substring.lastIndexOf("."));
        ContentValues contentValues = new ContentValues();
        contentValues.put("_data", str);
        contentValues.put("title", substring2);
        contentValues.put("_size", Integer.valueOf(str.length()));
        contentValues.put("mime_type", "audio/mp3");
        contentValues.put("artist", getResources().getString(R.string.app_name));
        contentValues.put("is_ringtone", true);
        contentValues.put("is_notification", false);
        contentValues.put("is_alarm", false);
        contentValues.put("is_music", false);
        try {
            Uri contentUriForPath = MediaStore.Audio.Media.getContentUriForPath(str);
            Log.e("", "=====Enter ====" + contentUriForPath);
            ContentResolver contentResolver = getContentResolver();
            contentResolver.delete(contentUriForPath, "_data=\"" + str + "\"", (String[]) null);
            shareUri = getApplicationContext().getContentResolver().insert(contentUriForPath, contentValues);
            Log.e("", "File &&&&& uri " + shareUri);
        } catch (Exception exception) {
            Log.e("", "Error" + exception.getMessage());
        }
    }

    public void ThumbAudio(Context context, String str) {
        Cursor managedQuery = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "_id"}, "_data  like ?", new String[]{"%" + str + "%"}, " _id DESC");
        int count = managedQuery.getCount();
        Log.e("", "count" + count);
        if (count > 0) {
            managedQuery.moveToFirst();
            for (int i = 0; i < count; i++) {
                shareUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ContentUtill.getLong(managedQuery));
                Log.e("", "=== uri ===" + shareUri);
                managedQuery.moveToNext();
            }
        }
    }

    public void find_uri(String str) {
        String[] strArr = {"%" + str + "%"};
        Cursor managedQuery = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "_id"}, "_data  like ?", strArr, " _id DESC");
        if (managedQuery.moveToFirst()) {
            shareUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ContentUtill.getLong(managedQuery));
            Log.e("", "=== Find Uri ===" + shareUri);

        }
    }

    private void startRecording() {
        new File(Environment.getExternalStorageDirectory() + "/" + cn.getResources().getString(R.string.app_name)).mkdirs();
        File file = new File(getFilesDir(), "audiorec.mp3");
        edit_txt.setText("");
        mp3Recorder = new MP3Recorder(file);
        try {
            mp3Recorder.start();
            btnRecord.setImageResource(R.drawable.pause);
            recording = true;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }


    public void stopRecording() {
        mp3Recorder.stop();
        recording = false;
        btnRecord.setImageResource(R.drawable.play);
        btnDeleteRecord.setVisibility(View.VISIBLE);
        setRecord.setText(cn.getResources().getString(R.string.recoded_txt));
        layEdit.setOnClickListener(new View.OnClickListener() {
             @Override
            public void onClick(View view) {
            }
        });
        Toast.makeText(this, getResources().getString(R.string.record_save), Toast.LENGTH_LONG).show();
    }

    public void deleteRecordVisiblityGone() {
        btnDeleteRecord.setVisibility(View.GONE);
        btnRecord.setImageResource(R.drawable.voice_button_selector);
        layEdit.setOnClickListener((View.OnClickListener) null);
        setRecord.setText(cn.getResources().getString(R.string.add_voice_str));
    }


    public void promptSpeechInput() {
        Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
        intent.putExtra("android.speech.extra.LANGUAGE", Locale.getDefault());
        intent.putExtra("android.speech.extra.PROMPT", "Speech");
        try {
            startActivityForResult(intent, 10);
        } catch (ActivityNotFoundException exceptionxception) {
            MyUtils.Toast(cn, "Not Suppoerted");
        }
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 10 && i2 == -1 && intent != null) {
            String str = intent.getStringArrayListExtra("android.speech.extra.RESULTS").get(0);
            edit_txt.setText(str);
            edit_txt.setSelection(str.length());
        } else if (i == 11) {
            if (i2 != -1 || intent == null) {
                File file = new File(getFilesDir(), "audiorec.mp3");
                if (file.exists()) {
                    file.delete();
                }
                deleteRecordVisiblityGone();
                return;
            }
            else{
                String path=intent.getStringExtra("path");
                cutPath=path;
            }
            setRecord.setText(cn.getResources().getString(R.string.recoded_txt));
        } else if (i == 12 && i2 == -1 && intent != null) {
            rate = intent.getFloatExtra("rate", 1.0f);
            pitch = intent.getFloatExtra("pitch", 1.0f);
            txtSpeech.setSpeechRate(rate);
            txtSpeech.setPitch(pitch);
        } else if (i == 13 && i2 == -1 && intent != null) {
            txtRefix.setText(intent.getStringExtra("prepost"));
        } else if (i == 14 && i2 == -1 && intent != null) {
            String stringExtra = intent.getStringExtra("prepost");
            txtPhone.setText(stringExtra);
            customAfterText = stringExtra;
        }
    }


    public void displayRecordingDialog() {
        dialog = new Dialog(cn);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_record);
        dialog.setCancelable(false);
        final TextView textView = (TextView) dialog.findViewById(R.id.settime);
        LinearLayout stopBtn = (LinearLayout) dialog.findViewById(R.id.btnsave);
        stopWatch = new MyStopWatch(cn, new MyStopWatch.OncallBack() {
            public void calling(String str) {
                textView.setText(str);
            }
        });
        startRecording();
        stopWatch.start();
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopWatch.stop();
                stopRecording();
                dialog.dismiss();
                File file = new File(getFilesDir(), "audiorec.mp3");
                Intent intent = new Intent(cn, SongEditActivity.class);
                intent.putExtra("path", file.getAbsolutePath());
                startActivityForResult(intent, 11);
            }
        });
        dialog.show();
    }

    public void deleteAllTmp(boolean bool) {
        File file = new File(getFilesDir(), "prefix.mp3");
        if (file.exists()) {
            file.delete();
        }
        File file2 = new File(getFilesDir(), "postfix.mp3");
        if (file2.exists()) {
            file2.delete();
        }
        if (mainAudio.exists()) {
            mainAudio.delete();
        }
        File file3 = new File(getFilesDir(), "audiotmp.mp3");
        if (file3.exists()) {
            file3.delete();
        }
        Log.e("AAA", "Delete all file");
    }

    public void callSyntheSize(String str, String str2, String str3) {
        if (Build.VERSION.SDK_INT >= 21) {
            txtSpeech.synthesizeToFile(str, (Bundle) null, new File(str2), str3);
            return;
        }
        HashMap hashMap = new HashMap();
        hashMap.put("utteranceId", str3);
        txtSpeech.synthesizeToFile(str.toString(), hashMap, str2);
    }

    public void callSpeak(String str, String str2) {
        if (Build.VERSION.SDK_INT >= 21) {
            txtSpeech.speak(str, 0, (Bundle) null, str2);
            return;
        }
        HashMap hashMap = new HashMap();
        hashMap.put("utteranceId", str2);
        txtSpeech.speak(str, 0, hashMap);
    }


}
