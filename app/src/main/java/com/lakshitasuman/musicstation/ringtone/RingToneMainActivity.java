package com.lakshitasuman.musicstation.ringtone;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.ringtone.utils.AdsUtils;


import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class RingToneMainActivity extends AppCompatActivity {
    public static long MIN_CLICK_INTERVAL = 1000;
    public static String[] addPostfix = {"None", "Your phone is ringing, Please answer the phone", "Please answer the phone", "Your phone is ringing", "Please pick up call", "Some one is calling you tack your phone", "Please receive your call", "Please check your phone,Call is receiving", "Your phone is ringing, Please check your phone"};
    public static String[] addPrefix = {"None", "Hey", "Hii", "Mister", "Miss", "Doctor", "Dear", "Excuse me", "Officer", "Detective", "Colonel", "Chief", "What's up"};
    public static String[] language = {"US", "UK", "CANADA", "CANADA_FRENCH", "CHINA", "CHINESE", "FRANCE", "FRENCH", "GERMAN", "GERMANY", "ITALY", "KOREA", "KOREAN", "JAPAN"};
    public static SharedPreferences languageSharedPref;
    public static Locale[] languages = {Locale.US, Locale.UK, Locale.CANADA, Locale.CANADA_FRENCH, Locale.CHINA, Locale.CHINESE, Locale.FRANCE, Locale.FRENCH, Locale.GERMAN, Locale.GERMANY, Locale.ITALY, Locale.KOREA, Locale.KOREAN, Locale.JAPAN};
    public static long lastClickTime;
    public static String pathofSong;
    private final int MULTIPLE_PERMISSIONS = 1;
    String TAG = "destory";
    private FrameLayout adContainerView;
    LinearLayout create_ringtone;
    LinearLayout my_name_ringtone;
    ImageView myNameRingtone;
    RelativeLayout back;
    String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.RECORD_AUDIO", "android.permission.READ_CONTACTS", "android.permission.WRITE_CONTACTS"};
    SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ring_activity_main);
        my_name_ringtone = findViewById(R.id.my_name_ringtone);
        create_ringtone = findViewById(R.id.create_ringtone);
        back = findViewById(R.id.back);
        checkPermissions();
        AdsUtils.initAd(this);
        AdsUtils.loadLargeBannerAd(this,findViewById(R.id.adsView));
        ui();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void ui() {
        languageSharedPref = getSharedPreferences("Mypref", 0);
        create_ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SelectRingtoneActivity.class));

            }
        });
        my_name_ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 29) {
                    pathofSong = createDirs(getResources().getString(R.string.app_name1));
                } else {
                    pathofSong = createsDirsbeLow10(getResources().getString(R.string.app_name1));
                }
                long elapsedRealtime = SystemClock.elapsedRealtime();
                if (elapsedRealtime - lastClickTime > MIN_CLICK_INTERVAL) {
                    lastClickTime = elapsedRealtime;
                    createClick();
                }
            }
        });
    }

    public void createClick() {
        final Dialog dialog = new Dialog(RingToneMainActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_selection);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.btntext);
        ImageView imageView2 = (ImageView) dialog.findViewById(R.id.btnvoice);
        LinearLayout layoutClose = (LinearLayout) dialog.findViewById(R.id.imgCloss);

        layoutClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(RingToneMainActivity.this, CreateRingtoneActivity.class).putExtra("text", true));
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(RingToneMainActivity.this, CreateRingtoneActivity.class).putExtra("text", false));
            }
        });
        dialog.show();
    }

    public static String createsDirsbeLow10(String str) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + str);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    public static String createDirs(String str) {
        File file = new File(Environment.getExternalStoragePublicDirectory(str).getAbsolutePath());
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }


    private boolean checkPermissions() {
        ArrayList arrayList = new ArrayList();
        for (String str : permissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), str) != 0) {
                arrayList.add(str);
            }
        }
        if (arrayList.isEmpty()) {
            return true;
        }
        ActivityCompat.requestPermissions(this, (String[]) arrayList.toArray(new String[arrayList.size()]), 1);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 1) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
