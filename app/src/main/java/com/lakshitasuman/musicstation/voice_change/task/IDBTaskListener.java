package com.lakshitasuman.musicstation.voice_change.task;

public interface IDBTaskListener {
    void onDoInBackground();

    void onPostExecute();

    void onPreExecute();
}
