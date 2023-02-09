package com.lakshitasuman.musicstation.musicplayer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lakshitasuman.musicstation.MainActivity;
import com.lakshitasuman.musicstation.musicplayer.adapter.FolderAdapter;

import com.lakshitasuman.musicstation.R;

public class FragmentFolder extends Fragment {
    FolderAdapter folderAdapter;
    RecyclerView recyclerview;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_folders, viewGroup, false);
        recyclerview = (RecyclerView) inflate.findViewById(R.id.recyclerview);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new GridLayoutManager(getContext(), 1));
        if(MainActivity.allMusicFolders.getMusicFolders() != null){
            folderAdapter = new FolderAdapter(getContext(), MainActivity.allMusicFolders.getMusicFolders());
            recyclerview.setAdapter(folderAdapter);
        }
        return inflate;
    }
}
