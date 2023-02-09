package com.lakshitasuman.musicstation.ringtone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.ringtone.adapter.SongAdapter;
import com.lakshitasuman.musicstation.ringtone.model.Songs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class SelectRingtoneActivity extends AppCompatActivity {
    String ASSORDER = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private FrameLayout adContainerView;
    SongAdapter adapter;
    ImageView back;
    ListView music_list;
    ProgressDialog progress;
    EditText search;
    LinearLayout search_lay;
    ArrayList<Songs> songs = new ArrayList<>();
    TextView title;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_select_ringtone);



        title = (TextView) findViewById(R.id.title);
        back = (ImageView) findViewById(R.id.back);
        search = (EditText) findViewById(R.id.search);
        music_list = (ListView) findViewById(R.id.music_list);
        back.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        songs.clear();
        new SongFinder().execute(new String[0]);
        music_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                Intent intent = new Intent(getApplicationContext(), Music_Edit_Activity.class);
                intent.putExtra("mp", songs.get(i).getPath());
                startActivityForResult(intent, 100);
            }
        });
    }

    class SongFinder extends AsyncTask<String, String, String> {
        SongFinder() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            songs.clear();
            progress = new ProgressDialog(SelectRingtoneActivity.this);
            progress.setTitle("Loading....");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        public void onPostExecute(String str) {
            Collections.sort(songs, new NameASS());
            progress.dismiss();
            adapter = new SongAdapter(getApplicationContext(), songs);
            music_list.setAdapter(adapter);
            search.addTextChangedListener(new TextWatcher() {
               @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    adapter.filter(search.getText().toString().toLowerCase(Locale.getDefault()));
                }
            });
            progress.dismiss();
            super.onPostExecute(str);
        }

        @Override
        public String doInBackground(String... strArr) {
            checkDictionary(new File(Environment.getExternalStorageDirectory() + ""));
            return null;
        }
    }

    public class NameASS implements Comparator<Songs> {
        NameASS() {
        }

        @Override
        public int compare(Songs songs, Songs songs2) {
            int i = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < Math.min(songs.getTitle().length(), songs2.getTitle().length()) && i == i2; i3++) {
                i = ASSORDER.indexOf(songs.getTitle().charAt(i3));
                i2 = ASSORDER.indexOf(songs2.getTitle().charAt(i3));
            }
            return (i != i2 || songs.getTitle().length() == songs2.getTitle().length()) ? i - i2 : songs2.getTitle().length() - songs2.getTitle().length();
        }
    }

    public void checkDictionary(File file) {
        try {
            int artistIndex;
            int idIndex;
            int titleIndex;
            int albumIndex;
            int dataIndex;
            int durationIndex;
            Cursor query = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, (String[]) null, (String) null, (String[]) null, "date_added DESC");
            if (query != null && query.moveToFirst()) {
                titleIndex = query.getColumnIndex("title");
                idIndex = query.getColumnIndex("_id");
                artistIndex = query.getColumnIndex("artist");
                albumIndex = query.getColumnIndex("album");
                dataIndex = query.getColumnIndex("_data");
                durationIndex = query.getColumnIndex("duration");
                while (true) {
                    long id = query.getLong(idIndex);
                    String title = query.getString(titleIndex);
                    String artist = query.getString(artistIndex);
                    String album = query.getString(albumIndex);
                    String data = query.getString(dataIndex);
                    Log.e("data path",data);
                    long duration = query.getLong(durationIndex);
                   if (data.contains(".mp3")){
                       songs.add(new Songs(data, title,artist));
                   }
                   if (!query.moveToNext()) {
                        break;
                    }
                }
            }
            if (query != null) {
                query.close();
            }
        } catch (Exception exception) {
            Log.e("Songs_Frege", exception.toString());
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }




}
