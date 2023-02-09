package com.lakshitasuman.musicstation.ringtone.asyncfol;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;

public class AsyncMp3Overlay extends AsyncTask<Void, Void, Void> {
    Context cn;
    OnCommonProgress onCommonProgress;
    String oppath;
    String path1;
    String path2;
    int volumePercentagePath2;

    interface FfmpegEventListener {
        void onCancel();

        void onFailure(String str);

        void onProgress(String str);

        void onSucces(String str);
    }

    public AsyncMp3Overlay(Context context, String path1, String path2, String oppath, int volumePercentagePath2, OnCommonProgress onCommonProgress) {
        cn = context;
        this.path1 = path1;
        this.path2 = path2;
        this.oppath = oppath;
        this.volumePercentagePath2 = volumePercentagePath2;
        this.onCommonProgress = onCommonProgress;
    }

    @Override
    public Void doInBackground(Void... voidArr) {
        float log = (float) (1.0d - (Math.log((double) (100 - volumePercentagePath2)) / Math.log(100.0d)));
        if (String.valueOf(log).equalsIgnoreCase("Infinity")) {
            log = 1.0f;
        }
        Log.e("AAA", "Bg Music Volume : " + log);
        runFfmpegCmd(new String[]{"-y", "-i", path1, "-i", path2, "-filter_complex", "[0:0]volume=1[a];[1:0]volume=" + log + "[b];[a][b] amix=inputs=2:duration=shortest", "-c:a", "libmp3lame", oppath}, new FfmpegEventListener() {
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





    public static void runFfmpegCmd(String[] strArr, FfmpegEventListener ffmpegEventListener) {
        try {
            int execute = FFmpeg.execute(strArr);
            if (execute == 0) {
                ffmpegEventListener.onSucces("success");
                Log.i(Config.TAG, "Async command execution completed successfully.");
            } else if (execute == 255) {
                ffmpegEventListener.onCancel();
                Log.i(Config.TAG, "Async command execution cancelled by user.");
            } else {
                ffmpegEventListener.onFailure("Fail to load.");
                Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", new Object[]{Integer.valueOf(execute)}));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
