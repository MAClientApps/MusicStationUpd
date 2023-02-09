package com.lakshitasuman.musicstation.ringtone.asyncfol;

import android.content.Context;
import android.os.AsyncTask;

import com.lakshitasuman.musicstation.ringtone.utils.MyUtils;

import java.io.File;
import java.io.IOException;

public class AsyncMergeMp3 extends AsyncTask<Void, Void, Void> {
    Context cn;
    OnCommonProgress onCommonProgress;
    String oppath;
    String path1;
    String path2;
    String path3;
    File txtfile;

    public AsyncMergeMp3(Context context, String path1, String path2, String path3, String oppath, OnCommonProgress onCommonProgress2) {
        cn = context;
        this.path1 = path1;
        this.path2 = path2;
        this.path3 = path3;
        this.oppath = oppath;
        onCommonProgress = onCommonProgress2;
    }

    @Override
    public void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    public Void doInBackground(Void... voidArr) {
        String[] strArr;
        if (new File(path1).exists() && new File(path2).exists() && new File(path3).exists()) {
            strArr = new String[]{"-i", path1, "-i", path2, "-i", path3, "-filter_complex", "[0:0][1:0]concat=n=3:v=0:a=1[out]", "-map", "[out]", "-preset", "ultrafast", oppath};
        } else if (new File(path1).exists() && new File(path2).exists()) {
            strArr = new String[]{"-i", path1, "-i", path2, "-filter_complex", "[0:0][1:0]concat=n=2:v=0:a=1[out]", "-map", "[out]", "-preset", "ultrafast", oppath};
        }
        else if (!new File(path2).exists() || !new File(path3).exists()) {
            try {
                MyUtils.copyFileUsingStream(new File(path2), new File(oppath).getParent(), new File(oppath).getName());
                if (onCommonProgress == null) {
                    return null;
                }
                onCommonProgress.OnComplete(true, "");
                onCommonProgress = null;
                return null;
            } catch (IOException exception) {
                exception.printStackTrace();
                if (onCommonProgress == null) {
                    return null;
                }
                onCommonProgress.OnComplete(false, path2);
                onCommonProgress = null;
                return null;
            }
        } else {
            strArr = new String[]{"-i", path2, "-i", path3, "-filter_complex", "[0:0][1:0]concat=n=2:v=0:a=1[out]", "-map", "[out]", "-preset", "ultrafast", this.oppath};
        }
        AsyncMp3Overlay.runFfmpegCmd(strArr, new AsyncMp3Overlay.FfmpegEventListener() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onProgress(String str) {
            }

            @Override
            public void onSucces(String str) {
               onCommonProgress.OnComplete(true, " ");
               onCommonProgress = null;
            }

            @Override
            public void onFailure(String str) {
               onCommonProgress.OnComplete(false, "Fail");
               onCommonProgress = null;
            }
        });
        return null;
    }
}
