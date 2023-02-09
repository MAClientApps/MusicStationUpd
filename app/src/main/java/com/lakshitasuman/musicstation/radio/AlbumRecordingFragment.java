package com.lakshitasuman.musicstation.radio;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.radio.recording.RecordingsAdapter;
import com.lakshitasuman.musicstation.radio.recording.RecordingsManager;

public class AlbumRecordingFragment extends Fragment {
    private RecordingsAdapter recordingsAdapter;
    RecyclerView recyclerViewSongHistory;
    private RecordingsManager recordingsManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_album_recording, container, false);
        MainRadioHelper radioDroidApp = (MainRadioHelper) requireActivity().getApplication();

        recordingsManager = radioDroidApp.getRecordingsManager();
        recyclerViewSongHistory=view.findViewById(R.id.recyclerViewSongHistory);
        recordingsAdapter = new RecordingsAdapter(requireContext());
        recordingsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {

            }
        });
        recordingsAdapter.setRecordings(recordingsManager.getSavedRecordings());
        LinearLayoutManager llmRecordings = new LinearLayoutManager(getContext());
        llmRecordings.setOrientation(RecyclerView.VERTICAL);
        recyclerViewSongHistory.setLayoutManager(llmRecordings);

        recyclerViewSongHistory.setAdapter(recordingsAdapter);
        return view;
    }
}