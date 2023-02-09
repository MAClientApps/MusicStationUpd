package com.lakshitasuman.musicstation.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Date;

public class CommonReceiver extends BroadcastReceiver {
    public static final String PREFS_NAME = "Path";
    public static String PhoneNumber;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static int lastState;
    Context context;

    public void onMissedCall(Context context2, String str, Date date) {
    }


    public void onOutgoingCallEnded(Context context2, String str, Date date, Date date2) {
    }



    public void onOutgoingCallStarted(Context context2, String str, Date date) {
    }

    @Override
    public void onReceive(Context context2, Intent intent) {
        Log.e("1234567989", "onReceive: START");
        context = context2;
        PhoneNumber = intent.getExtras().getString("incoming_number");
        Log.e("11111111111111Start", "ReceiverStart");
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            PhoneNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            return;
        }
        String string = intent.getExtras().getString("state");
        int i = 0;
        if (!string.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            if (string.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                i = 2;
            } else if (string.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                i = 1;
            }
        }
        onCallStateChanged(context, i, PhoneNumber);
    }





    public void onCallStateChanged(Context context2, int i, String phoneNumber) {
        if (lastState != i) {
            switch (i) {
                case 0:
                    if (lastState != 1) {
                        if (!isIncoming) {
                            onOutgoingCallEnded(context2, PhoneNumber, callStartTime, new Date());
                            break;
                        } else {
                            onIncomingCallEnded(context2, PhoneNumber, callStartTime, new Date());
                            break;
                        }
                    } else {
                        onMissedCall(context2, PhoneNumber, callStartTime);
                        break;
                    }
                case 1:
                    isIncoming = true;
                    callStartTime = new Date();
                    onIncomingCallStarted(context2, phoneNumber, callStartTime);
                    break;
                case 2:
                    if (lastState != 1) {
                        isIncoming = false;
                        callStartTime = new Date();
                        onOutgoingCallStarted(context2, PhoneNumber, callStartTime);
                        break;
                    }
                    break;
            }
            lastState = i;
        }
    }



    public void onIncomingCallStarted(Context context2, String phoneNumber, Date callStartTime) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(Constants.ACTION.PLAY_ACTION);
        context.startService(intent);
    }




    public void onIncomingCallEnded(Context context2, String PhoneNumber, Date callStartTime, Date date2) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(Constants.ACTION.PLAY_ACTION);
        context.startService(intent);
    }
}
