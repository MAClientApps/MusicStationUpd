package com.lakshitasuman.musicstation.ringtone.designview;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.HashMap;
import java.util.Iterator;

public class SongMetadataReader {
    public Uri GENRES_URI = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
    public Activity mActivity = null;
    public String mAlbum = "";
    public String mArtist = "";
    public String mFilename = "";
    public String mGenre = "";
    public String mTitle = "";
    public int mYear = -1;

    public SongMetadataReader(Activity activity, String str) {
        mActivity = activity;
        mFilename = str;
        mTitle = getBasename(str);
        try {
            ReadMetadata();
        } catch (Exception exceptionxception) {
        }
    }

    private void ReadMetadata() {
        HashMap hashMap = new HashMap();
        Cursor managedQuery = mActivity.managedQuery(GENRES_URI, new String[]{"_id"}, (String) null, (String[]) null, (String) null);
        managedQuery.moveToFirst();
        while (!managedQuery.isAfterLast()) {
            hashMap.put(managedQuery.getString(0), managedQuery.getString(1));
            managedQuery.moveToNext();
        }
        mGenre = "";
        Iterator it = hashMap.keySet().iterator();
        while (true) {
            if (it.hasNext()) {
                String str = (String) it.next();
                if (mActivity.managedQuery(makeGenreUri(str), new String[]{"_data"}, "_data LIKE \"" + mFilename + "\"", (String[]) null, (String) null).getCount() != 0) {
                    mGenre = (String) hashMap.get(str);
                    break;
                }
            } else {
                break;
            }
        }
        Uri contentUriForPath = MediaStore.Audio.Media.getContentUriForPath(mFilename);
        Cursor managedQuery2 = mActivity.managedQuery(contentUriForPath, new String[]{"_id", "title", "artist", "album", "year", "_data"}, "_data LIKE \"" + mFilename + "\"", (String[]) null, (String) null);
        if (managedQuery2.getCount() == 0) {
            mTitle = getBasename(mFilename);
            mArtist = "";
            mAlbum = "";
            mYear = -1;
            return;
        }
        managedQuery2.moveToFirst();
        mTitle = getStringFromColumn(managedQuery2, "title");
        if (mTitle == null || mTitle.length() == 0) {
            mTitle = getBasename(mFilename);
        }
        mArtist = getStringFromColumn(managedQuery2, "artist");
        mAlbum = getStringFromColumn(managedQuery2, "album");
        mYear = getIntegerFromColumn(managedQuery2, "year");
    }

    private Uri makeGenreUri(String str) {
        return Uri.parse(GENRES_URI.toString() + "/" + str + "/members");
    }

    private String getStringFromColumn(Cursor cursor, String str) {
        String string = cursor.getString(cursor.getColumnIndexOrThrow(str));
        if (string == null || string.length() <= 0) {
            return null;
        }
        return string;
    }

    private int getIntegerFromColumn(Cursor cursor, String str) {
        Integer valueOf = Integer.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(str)));
        if (valueOf != null) {
            return valueOf.intValue();
        }
        return -1;
    }

    private String getBasename(String str) {
        return str.substring(str.lastIndexOf(47) + 1, str.lastIndexOf(46));
    }
}
