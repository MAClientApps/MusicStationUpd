package com.lakshitasuman.musicstation.voice_change;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.voice_change.constants.IVoiceChangerConstants;
import com.lakshitasuman.musicstation.voice_change.dataMng.TotalDataManager;

import com.lakshitasuman.musicstation.voice_change.setting.SettingManager;
import com.lakshitasuman.musicstation.voice_change.soundMng.SoundManager;

import com.lakshitasuman.musicstation.voice_change.task.IDBConstantURL;
import com.lakshitasuman.musicstation.voice_change.utils.IDialogFragmentListener;
import com.lakshitasuman.musicstation.voice_change.utils.ResolutionUtils;


import java.util.ArrayList;
import java.util.Random;

public class DBFragmentActivity extends AppCompatActivity implements IDBConstantURL, IDialogFragmentListener, IVoiceChangerConstants {
    public static final String TAG = "DBFragmentActivity";
    private int countToExit;
    private boolean isAllowPressMoreToExit;
    public int mAccentColor;
    public int mBgColor;
    public ArrayList<Fragment> mListFragments;
    private Dialog mProgressDialog;
    private Random mRandom;
    public SoundManager mSoundMng;
    public int mTextColor;
    public TotalDataManager mTotalMng;

    private long pivotTime;
    private int screenHeight;
    private int screenWidth;

    @Override
    public void doNegativeClick(int i) {
    }


    public void onDestroyData() {
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFormat(1);
        getWindow().setSoftInputMode(3);
        createProgressDialog();

        int[] deviceResolution = ResolutionUtils.getDeviceResolution(this);
        if (deviceResolution != null && deviceResolution.length == 2) {
            screenWidth = deviceResolution[0];
            screenHeight = deviceResolution[1];
        }
        mTextColor = getColorCustom(16842904);
        if (SettingManager.getCurrentAccentColor(this) != 0) {
            mAccentColor = SettingManager.getCurrentAccentColor(this);
        } else {
            mAccentColor = getColorCustom(R.attr.colorAccent);
        }
        mTotalMng = TotalDataManager.getInstance();
        mSoundMng = SoundManager.getInstance();
        mBgColor = getColorCustom(16842836);
        mRandom = new Random();
    }

    public int getColorCustom(int i) {
        try {
            TypedValue typedValue = new TypedValue();
            if (getTheme().resolveAttribute(i, typedValue, true)) {
                return typedValue.data;
            }
            return 0;
        } catch (Exception exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4) {
            return super.onKeyDown(i, keyEvent);
        }
        if (!isAllowPressMoreToExit) {

            return true;
        }
        pressMoreToExitApp();
        return true;
    }



    private void pressMoreToExitApp() {
        if (countToExit >= 1) {
            if (System.currentTimeMillis() - pivotTime <= 2000) {
                onDestroyData();
                finish();
                return;
            }
            countToExit = 0;
        }
        pivotTime = System.currentTimeMillis();
        showToast((int) R.string.info_press_again_to_exit);
        countToExit++;
    }





    private void createProgressDialog() {
        mProgressDialog= new Dialog(this);
        mProgressDialog.requestWindowFeature(1);
        mProgressDialog.setContentView(R.layout.item_progress_bar);

        mProgressDialog.setCancelable(false);
        mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
              @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return i == 4;
            }
        });
    }

    public void showProgressDialog() {
        showProgressDialog((int) R.string.info_loading);
    }

    public void showProgressDialog(int i) {
        showProgressDialog(getString(i));
    }

    public void showProgressDialog(String str) {

        if (mProgressDialog != null) {
            ((TextView) mProgressDialog.findViewById(R.id.tv_message)).setText(str);
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void showToast(int i) {
        showToast(getString(i));
    }

    public void showToast(String str) {
        Toast makeText = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        makeText.setGravity(17, 0, 0);
        makeText.show();
    }

    public void showToastWithLongTime(int i) {
        showToastWithLongTime(getString(i));
    }

    public void showToastWithLongTime(String str) {
        Toast makeText = Toast.makeText(this, str, Toast.LENGTH_LONG);
        makeText.setGravity(17, 0, 0);
        makeText.show();
    }

    public void doPositiveClick(int i) {
        if (i == 8) {
            onDestroyData();
            finish();
        }
    }

    public void createArrayFragment() {
        mListFragments = new ArrayList<>();
    }

    public void addFragment(Fragment fragment) {
        ArrayList<Fragment> arrayList;
        if (fragment != null && (arrayList = mListFragments) != null) {
            synchronized (arrayList) {
                mListFragments.add(fragment);
            }
        }
    }






    public void setAllowPressMoreToExit(boolean allowPressMoreToExit) {
        isAllowPressMoreToExit = allowPressMoreToExit;
    }
}
