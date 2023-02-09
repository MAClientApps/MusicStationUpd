package com.lakshitasuman.musicstation.ringtone;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.ringtone.adapter.ListAllContactAdapter;
import com.lakshitasuman.musicstation.ringtone.model.ContactList;
import com.lakshitasuman.musicstation.ringtone.utils.AdsUtils;


import java.util.ArrayList;
import java.util.List;

public class ListAllContactActivity extends AppCompatActivity {
    public static List<ContactList> allContactList = new ArrayList();
    private FrameLayout adContainerView;
    ListAllContactAdapter adapter;
    ImageView back;

    Context context;
    ListAllContactAdapter listAllContactAdapter;
    private SimpleCursorAdapter mAdapter;
    private Uri mRingtoneUri;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    RelativeLayout relativeLayout;
    String songPath;
    int songPosition;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);


        setContentView(R.layout.activity_list_all_contact);
        back=findViewById(R.id.back);
        AdsUtils.initAd(this);
        AdsUtils.loadLargeBannerAd(this,findViewById(R.id.adsView));
        context = getApplicationContext();
        Intent intent = getIntent();
        songPosition = intent.getIntExtra("song position", 0);
        songPath = intent.getStringExtra("song path");
        Toast.makeText(context, "path:" + songPath, Toast.LENGTH_SHORT).show();
        Log.d("tag", "onCreate: " + songPath);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getAllContacts();
    }

    @SuppressLint("Range")
    private void getAllContacts() {
        allContactList.clear();
        ContentResolver contentResolver = getContentResolver();
        Cursor query = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, (String[]) null, (String) null, (String[]) null, "display_name ASC");
        if (query.getCount() > 0) {
            while (query.moveToNext()) {
                if (Integer.parseInt(query.getString(query.getColumnIndex("has_phone_number"))) > 0) {
                    String string = query.getString(query.getColumnIndex("_id"));
                    String string2 = query.getString(query.getColumnIndex("display_name"));
                    String string3 = query.getString(query.getColumnIndex("custom_ringtone"));
                    Uri withAppendedPath = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, string);
                    ContactList contactList = new ContactList();
                    contactList.setName(string2);
                    contactList.setId(string);
                    contactList.setUri(withAppendedPath);
                    contactList.setRingtone(string3);
                    Cursor query2 = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, (String[]) null, "contact_id = ?", new String[]{string}, (String) null);
                    if (query2.moveToNext()) {
                        contactList.setNumber(query2.getString(query2.getColumnIndex("data1")));
                    }
                    query2.close();
                    allContactList.add(contactList);
                }
            }
            relativeLayout = (RelativeLayout) findViewById(R.id.contact_list_relative_layout);
            recyclerView = (RecyclerView) findViewById(R.id.contact_list_recycle_view);
            recyclerViewLayoutManager= new LinearLayoutManager(context);
            recyclerView.setLayoutManager(recyclerViewLayoutManager);
            listAllContactAdapter = new ListAllContactAdapter(context, allContactList, songPosition, songPath);
            recyclerView.setAdapter(listAllContactAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }



}
