package com.lakshitasuman.musicstation.ringtone.utils;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;

public class MyStopWatch {
    int MilliSeconds;
    long MillisecondTime;
    int Minutes;
    int Seconds;
    long StartTime;
    long TimeBuff;
    long UpdateTime = 0;
    OncallBack callBack;
    Context cn;
    Handler handler;
    Runnable runnable;

    public interface OncallBack {
        void calling(String str);
    }

    public MyStopWatch(Context context, final OncallBack oncallBack) {
        cn = context;
        callBack = oncallBack;
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                MillisecondTime = SystemClock.uptimeMillis() - StartTime;
                UpdateTime = TimeBuff + MillisecondTime;
                Seconds = (int) (UpdateTime / 1000);
                Minutes = Seconds / 60;
                Seconds %= 60;
                MilliSeconds = (int) (UpdateTime % 1000);
                oncallBack.calling("" + Minutes + ":" + String.format("%02d", new Object[]{Integer.valueOf(Seconds)}));
                handler.postDelayed(this, 0);
            }
        };
    }



    public void start() {
        StartTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
    }


    public void stop() {
        handler.removeCallbacks(runnable);
        MillisecondTime = 0;
        StartTime = 0;
        TimeBuff = 0;
        UpdateTime = 0;
        Seconds = 0;
        Minutes = 0;
        MilliSeconds = 0;
        callBack.calling("00:00");
    }
}
