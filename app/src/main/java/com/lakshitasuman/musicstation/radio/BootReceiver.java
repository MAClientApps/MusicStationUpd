package com.lakshitasuman.musicstation.radio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        MainRadioHelper radioDroidApp = (MainRadioHelper)context.getApplicationContext();
        radioDroidApp.getAlarmManager().resetAllAlarms();
    }
}
