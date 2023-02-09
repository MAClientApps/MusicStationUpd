package com.lakshitasuman.musicstation.ringtone;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.ringtone.adapter.Ad_prepost;
import com.lakshitasuman.musicstation.ringtone.adapter.OnMyCommonItem;

import java.util.ArrayList;

public class PrePostActivity extends AppCompatActivity {
    Ad_prepost ad_prepost;
    ArrayList<String> allPreOrPost = new ArrayList<>();
    ImageView btnBack;
    Context cn = this;
    EditText edtSearch;
    Boolean isPrefix;
    LinearLayout layEdit;
    RecyclerView recyclerView;
    String selected;
    ImageView sectionSearch;
    TextView setTitle;
    LinearLayout topBar;

    @Override
    public void onCreate(Bundle bundle) {
        Resources resources;
        int i;
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_pre_post);


        isPrefix = Boolean.valueOf(getIntent().getBooleanExtra("ispre", true));
        selected = getIntent().getStringExtra("selected");
        allPreOrPost = getIntent().getStringArrayListExtra("list");
        init();
        sectionSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtSearch.setVisibility(View.VISIBLE);
            }
        });
        click();
        if (isPrefix.booleanValue()) {
            resources = getResources();
            i = R.string.add_prefix;
        } else {
            resources = getResources();
            i = R.string.add_postfix;
        }
        setTitle.setText(resources.getString(i));
        ad_prepost = new Ad_prepost(cn, allPreOrPost, selected, new OnMyCommonItem() {
            @Override
            public void OnMyClick2(int i, Object obj, Object obj2) {
            }

            @Override
            public void OnMyClick1(int i, Object obj) {
                Intent intent = new Intent();
                intent.putExtra("prepost", (String) obj);
                setResult(-1, intent);
                finish();
            }
        });
        recyclerView.setAdapter(ad_prepost);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
             public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                ad_prepost.getFilter().filter(editable.toString());
            }
        });
    }

    private void click() {
        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void init() {
        btnBack = (ImageView) findViewById(R.id.btnback);
        setTitle = (TextView) findViewById(R.id.setTitle);
        layEdit = (LinearLayout) findViewById(R.id.layedit);
        sectionSearch = (ImageView) findViewById(R.id.seticonsearch);
        edtSearch = (EditText) findViewById(R.id.edtsearch);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        topBar = (LinearLayout) findViewById(R.id.topbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(cn, RecyclerView.VERTICAL, false));
    }
}
