package com.lakshitasuman.musicstation.ringtone.model;

import android.net.Uri;

public class ContactList {
    String id;
    String name;
    String number;
    String ringtone;
    Uri uri;

    public ContactList(String name, String id, Uri uri, String number, String ringtone) {
        this.name = name;
        this.id = id;
        this.uri = uri;
        this.number = number;
        this.ringtone = ringtone;
    }

    public ContactList() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public Uri getUri() {
        return this.uri;
    }

    public void setUri(Uri uri2) {
        this.uri = uri2;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String str) {
        this.number = str;
    }

    public String getRingtone() {
        return this.ringtone;
    }

    public void setRingtone(String str) {
        this.ringtone = str;
    }
}
