package com.lakshitasuman.musicstation.ringtone.adapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import com.lakshitasuman.musicstation.ringtone.RingToneMainActivity;
import com.lakshitasuman.musicstation.ringtone.MyNameRingtoneListActivity;
import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.ringtone.model.ContactList;
import java.io.File;
import java.util.List;

public class ListAllContactAdapter extends RecyclerView.Adapter<ListAllContactAdapter.ViewHolder> {
    String TAG = "contact details";
    String check;
    List<ContactList> contactList;
    Context context;
    String folderName = "NameRingtone";
    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + folderName);
    String path = RingToneMainActivity.pathofSong;
    String songPath;
    int songPosition;
    Uri songUri;
    List<ContactList> title;
    View view1;
    ViewHolder viewHolder1;

    int selectPos=-1;

    public ListAllContactAdapter(Context context2, List<ContactList> contactList, int songPosition, String songPath) {
        context = context2;
        this.contactList = contactList;
        this.songPosition = songPosition;
        this.songPath = songPath;
        Log.d(TAG, "ListAllContactAdapter: " + songPath);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        view1 = LayoutInflater.from(context).inflate(R.layout.contact_list, viewGroup, false);
        viewHolder1= new ViewHolder(view1);
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        viewHolder.textView.setText(contactList.get(i).getName());
        check = contactList.get(i).getRingtone();
        if (selectPos == i) {
            viewHolder.ringtone.setImageResource(R.drawable.presed2);
        } else {
            viewHolder.ringtone.setImageResource(R.drawable.unpresed2);
        }
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                if (elapsedRealtime - RingToneMainActivity.lastClickTime > RingToneMainActivity.MIN_CLICK_INTERVAL) {
                    RingToneMainActivity.lastClickTime = elapsedRealtime;
                    if (Build.VERSION.SDK_INT >= 29) {
                       setRingtone(i, view);
                    } else {
                       setRingtone(i, view);
                    }
                }
                selectPos=i;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout;
        public ImageView ringtone;
        public TextView textView;

        public ViewHolder(View view) {
            super(view);
            linearLayout = (LinearLayout) view.findViewById(R.id.list_linear_layout);
            textView = (TextView) view.findViewById(R.id.list_text);
            ringtone = (ImageView) view.findViewById(R.id.ringtone_set_not);
        }
    }

    public void creatsDirsbelow10(int i, View view) {

        ContentValues contentValues = new ContentValues();
        ContentResolver contentResolver = context.getContentResolver();
        String customRingtone = "custom_ringtone";
        CharSequence charSequence = "File does not exist";
        String isRingtone = "is_ringtone";
        if (songPath != null) {
            File file = new File(songPath);
            File file2 = new File("/storage/emulated/0/NameRingtone/prince1619787787177_namering.mp3");
            if (file.exists()) {
                Uri contentUriForPath = MediaStore.Audio.Media.getContentUriForPath(file2.getAbsolutePath());
                String audioMp = "audio/mp3";
                contentResolver.delete(contentUriForPath, "_data=\"" + file2.getAbsolutePath() + "\"", (String[]) null);
                String number = contactList.get(i).getNumber();
                Cursor query = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, number), new String[]{"_id", "lookup"}, (String) null, (String[]) null, (String) null);
                if (query != null && query.moveToFirst()) {
                    query.moveToFirst();
                    Uri lookupUri = ContactsContract.Contacts.getLookupUri(query.getLong(0), query.getString(1));
                    contentValues.put("_data", file.getAbsolutePath());
                    contentValues.put("title", file.getName());
                    contentValues.put("_size", Long.valueOf(file.length()));
                    contentValues.put("mime_type", audioMp);
                    contentValues.put(isRingtone, 1);
                    Uri insert = contentResolver.insert(MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath()), contentValues);
                    if (insert != null) {
                        contentValues.put(customRingtone, insert.toString());
                        Context context2 = context;
                        Toast.makeText(context2, "ringtone set: " + ((long) contentResolver.update(lookupUri, contentValues, (String) null, (String[]) null)), Toast.LENGTH_LONG).show();

                    }
                    query.close();
                    return;
                }
                return;
            }
            Toast.makeText(context, charSequence, Toast.LENGTH_LONG).show();
            return;
        }
        String path2 = Environment.getExternalStorageDirectory().getPath();
        File file3 = new File(path2 + "/" + folderName, MyNameRingtoneListActivity.songList.get(songPosition).getName());
        if (file3.exists()) {
            Uri contentUriForPath2 = MediaStore.Audio.Media.getContentUriForPath(file3.getAbsolutePath());
            contentResolver.delete(contentUriForPath2, "_data=\"" + file3.getAbsolutePath() + "\"", (String[]) null);
            String number2 = contactList.get(i).getNumber();
            Cursor query2 = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, number2), new String[]{"_id", "lookup"}, (String) null, (String[]) null, (String) null);
            if (query2 != null && query2.moveToFirst()) {
                query2.moveToFirst();
                Uri lookupUri2 = ContactsContract.Contacts.getLookupUri(query2.getLong(0), query2.getString(1));
                contentValues.put("_data", file3.getAbsolutePath());
                contentValues.put("title", file3.getName());
                contentValues.put("_size", Long.valueOf(file3.length()));
                contentValues.put("mime_type", "audio/mp3");
                contentValues.put(isRingtone, 1);
                Uri insert2 = contentResolver.insert(MediaStore.Audio.Media.getContentUriForPath(file3.getAbsolutePath()), contentValues);
                if (insert2 != null) {
                    contentValues.put(customRingtone, insert2.toString());
                    Toast.makeText(context, "Updated : " + ((long) contentResolver.update(lookupUri2, contentValues, (String) null, (String[]) null)), Toast.LENGTH_LONG).show();

                }
                query2.close();
                return;
            }
            return;
        }
        Toast.makeText(context, charSequence, Toast.LENGTH_LONG).show();
    }

    public void setRingtone(int i, View view) {
        Cursor query = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contactList.get(i).getNumber()), new String[]{"_id", "lookup"}, (String) null, (String[]) null, (String) null);
        query.moveToFirst();
        try {
            Uri lookupUri = ContactsContract.Contacts.getLookupUri(query.getLong(0), query.getString(1));
            if (lookupUri != null) {
                if (songPath != null) {
                    String uri = Uri.fromFile(new File(songPath)).toString();
                    ContentValues contentValues = new ContentValues(1);
                    contentValues.put("custom_ringtone", uri);
                    context.getContentResolver().update(lookupUri, contentValues, (String) null, (String[]) null);
                    Toast.makeText(context, "Ringtone is set", Toast.LENGTH_LONG).show();

                } else {
                    String path2 = Environment.getExternalStorageDirectory().getPath();
                    String uri2 = Uri.fromFile(new File(path2 + "/" + folderName, MyNameRingtoneListActivity.songList.get(songPosition).getName())).toString();
                    ContentValues contentValues2 = new ContentValues(1);
                    contentValues2.put("custom_ringtone", uri2);
                    context.getContentResolver().update(lookupUri, contentValues2, (String) null, (String[]) null);
                    Toast.makeText(context, "Ringtone is set", Toast.LENGTH_LONG).show();

                }
                query.close();
            }
        } finally {
            query.close();
        }
    }
}
