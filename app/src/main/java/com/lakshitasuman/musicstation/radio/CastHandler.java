package com.lakshitasuman.musicstation.radio;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import com.lakshitasuman.musicstation.radio.cast.CastAwareActivity;


public class CastHandler {

    public boolean isReal() {
        return false;
    }

    public boolean isCastAvailable() {
        return false;
    }

    public void setActivity(CastAwareActivity activity) {

    }

    public void onCreate(ActivityMain activity) {

    }

    public void onPause() {

    }

    public void onResume() {

    }

    public MenuItem getRouteItem(Context context, Menu menu) {
        return null;
    }

    public boolean isCastSessionAvailable() {
        return false;
    }

    public void playRemote(String title, String url, String iconurl) {

    }
}
