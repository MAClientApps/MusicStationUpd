package com.lakshitasuman.musicstation.voice_change.task;

import android.os.AsyncTask;

public class DBTask extends AsyncTask<Void, Void, Void> {
    private IDBTaskListener mDownloadListener;

    public DBTask(IDBTaskListener iDBTaskListener) {
        mDownloadListener = iDBTaskListener;
    }


    @Override
    public void onPreExecute() {
        if (mDownloadListener != null) {
            mDownloadListener.onPreExecute();
        }
    }


    @Override
    public Void doInBackground(Void... voidArr) {
        if (mDownloadListener == null) {
            return null;
        }
        mDownloadListener.onDoInBackground();
        return null;
    }


    @Override
    public void onPostExecute(Void voidR) {

        if (mDownloadListener != null) {
            mDownloadListener.onPostExecute();
        }
    }
}
