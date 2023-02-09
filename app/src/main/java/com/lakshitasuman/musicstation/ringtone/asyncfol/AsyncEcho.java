package com.lakshitasuman.musicstation.ringtone.asyncfol;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;

public class AsyncEcho extends AsyncTask<Void, Void, Void> {
    Context cn;
    String decays;
    String delay;
    String ingain;
    OnCommonProgress onCommonProgress;
    String oppath;
    String outgain;
    String path;

    public AsyncEcho(Context context, String path, String oppath, String ingain, String outgain, String delay, String decays, OnCommonProgress onCommonProgress) {
       cn = context;
       this.path = path;
       this.oppath = oppath;
       this.ingain = ingain;
       this.outgain = outgain;
       this.delay = delay;
       this.decays = decays;
       this.onCommonProgress = onCommonProgress;
    }

    @Override
    public Void doInBackground(Void... voidArr) {
        File file = new File(this.oppath);
        if (file.exists()) {
            file.delete();
        }
        String[] strArr = {"-i",path, "-map", "0", "-c:v", "copy", "-af", "aecho=" +ingain + ":" +outgain + ":" +delay + ":" +decays,oppath};
        Log.e("AAA", "ingain : " +ingain + " outgain : " +outgain + " delay : " +delay + " decay : " +decays);
        runFfmpegCmd(strArr, new AsyncMp3Overlay.FfmpegEventListener() {
           @Override
            public void onCancel() {
            }

            @Override
            public void onProgress(String str) {
            }

            @Override
            public void onSucces(String str) {
                onCommonProgress.OnComplete(true, oppath);
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


    public void runFfmpegCmd(String[] strArr, AsyncMp3Overlay.FfmpegEventListener ffmpegEventListener) {
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
