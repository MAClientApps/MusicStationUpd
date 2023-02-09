package com.lakshitasuman.musicstation.ringtone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.ringtone.adapter.MyNameRingtoneListAdapter;
import com.lakshitasuman.musicstation.ringtone.model.SongList;
import com.lakshitasuman.musicstation.ringtone.utils.AdsUtils;

import java.io.File;
import java.util.ArrayList;

public class MyNameRingtoneListActivity extends AppCompatActivity {
    public static ArrayList<SongList> songList = new ArrayList<>();
    private FrameLayout adContainerView;
    RelativeLayout backBtn;
    Context context;
    EditText edtSearch;
    String path;
    String name;
    TextView setTitle;
    LinearLayout emptyView;
    LinearLayout layoutView;
    MyNameRingtoneListAdapter myNameRingtoneListAdapter;
    RecyclerView rec;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_my_name_ringtone_list);
        AdsUtils.initAd(this);
        AdsUtils.loadLargeBannerAd(this,findViewById(R.id.adsView));
        Intent intent = getIntent();
        path = intent.getStringExtra("ListPath");
        name = intent.getStringExtra("name");
        backBtn = (RelativeLayout) findViewById(R.id.btnback);
        emptyView = (LinearLayout) findViewById(R.id.emptyView);
        layoutView = (LinearLayout) findViewById(R.id.layoutView);
        setTitle = (TextView) findViewById(R.id.setTitle);
        ui();
        if(name!=null){
            setTitle.setText(name);
        }
        else{
            setTitle.setText("My Creation");
        }
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                myNameRingtoneListAdapter.getFilter().filter(editable.toString());
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void ui() {
        rec = (RecyclerView) findViewById(R.id.my_name_ringtone_list);
        edtSearch = (EditText) findViewById(R.id.edtsearch);
        context = this;
        createSongList();
    }

    private void createSongList() {
        songList.clear();
        File file = new File(path);
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            if (listFiles.length <= 0) {
                emptyView.setVisibility(View.VISIBLE);
                layoutView.setVisibility(View.GONE);
            } else {
                 emptyView.setVisibility(View.GONE);
                layoutView.setVisibility(View.VISIBLE);
                for (int i = 0; i < listFiles.length; i++) {
                    SongList songList2 = new SongList();
                    songList2.setName(listFiles[i].getName());
                    songList2.setPath(listFiles[i].getPath());
                    songList.add(songList2);
                }
                if (file.isDirectory()) {
                    rec.setHasFixedSize(true);
                    rec.setLayoutManager(new GridLayoutManager(this.context, 1));
                    myNameRingtoneListAdapter = new MyNameRingtoneListAdapter(this, songList, path);
                    rec.setAdapter(myNameRingtoneListAdapter);
                    myNameRingtoneListAdapter.notifyDataSetChanged();
                }
            }
        } else {
            emptyView.setVisibility(View.VISIBLE);
            layoutView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
