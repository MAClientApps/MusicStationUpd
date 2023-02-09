package com.lakshitasuman.musicstation.musicplayer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.lakshitasuman.musicstation.musicplayer.adapter.EnvironmentEffectAdpater;
import com.lakshitasuman.musicstation.R;

public class EnvironmentFragment extends Fragment {
    ArrayList<String> effects = new ArrayList<>();
    EnvironmentEffectAdpater environmentEffectAdpater;
    RecyclerView recyclerEnvironment;


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_environment, viewGroup, false);
        recyclerEnvironment = (RecyclerView) inflate.findViewById(R.id.recyclerEnvironment);
        recyclerEnvironment.setHasFixedSize(true);
        recyclerEnvironment.setLayoutManager(new GridLayoutManager(getContext(), 1));
        effects.clear();
        effects.add("None");
        effects.add("Small Room");
        effects.add("Medium Room");
        effects.add("Large Room");
        effects.add("Medium Hall");
        effects.add("Large Hall");
        effects.add("Plate");
        environmentEffectAdpater = new EnvironmentEffectAdpater(getContext(), effects);
        recyclerEnvironment.setAdapter(this.environmentEffectAdpater);
        return inflate;
    }
}
