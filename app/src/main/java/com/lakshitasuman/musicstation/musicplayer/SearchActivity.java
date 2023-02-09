package com.lakshitasuman.musicstation.musicplayer;

import static com.lakshitasuman.musicstation.MainActivity.localTrackList;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.musicplayer.adapter.SongsAdapter;
import com.lakshitasuman.musicstation.musicplayer.model.LocalTrackModel;

public class SearchActivity extends AppCompatActivity {
    public static List<LocalTrackModel> finalLocalSearchResultList = new ArrayList();
    RecyclerView searchRecyclerView;
    private FrameLayout adContainerView;
    ImageView back;
    EditText edtSearch;
    SongsAdapter songsAdapter;


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_search);
        edtSearch = (EditText) findViewById(R.id.edtsearch);
        edtSearch.setTypeface(Typeface.createFromAsset(getAssets(), "Heebo-Regular.ttf"));
        searchRecyclerView = (RecyclerView) findViewById(R.id.SearchrecyclerView);
        back = (ImageView) findViewById(R.id.back);
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        songsAdapter = new SongsAdapter(this, finalLocalSearchResultList);
        searchRecyclerView.setAdapter(songsAdapter);
        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                updateList(String.valueOf(charSequence).trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateList(editable.toString().trim());
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    public void updateList(String str) {
        finalLocalSearchResultList.clear();
        for (int i = 0; i < localTrackList.size(); i++) {
            LocalTrackModel localTrackModel = localTrackList.get(i);
            if (localTrackModel.getTitle().toLowerCase().contains(str.toLowerCase())) {
                finalLocalSearchResultList.add(localTrackModel);
            }
        }
        songsAdapter.notifyDataSetChanged();
    }


}
