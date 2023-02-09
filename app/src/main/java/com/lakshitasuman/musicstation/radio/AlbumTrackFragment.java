package com.lakshitasuman.musicstation.radio;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.radio.history.TrackHistoryAdapter;
import com.lakshitasuman.musicstation.radio.history.TrackHistoryEntry;
import com.lakshitasuman.musicstation.radio.history.TrackHistoryViewModel;


public class AlbumTrackFragment extends Fragment {

    RecyclerView recyclerViewSongHistory;
    private TrackHistoryAdapter trackHistoryAdapter;
    private TrackHistoryViewModel trackHistoryViewModel;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_album_track, container, false);
        recyclerViewSongHistory=view.findViewById(R.id.recyclerViewSongHistory);

        trackHistoryAdapter = new TrackHistoryAdapter(requireActivity());
        trackHistoryAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
//                final LinearLayoutManager lm = (LinearLayoutManager) historyAndRecordsPagerAdapter.recyclerViewSongHistory.getLayoutManager();
//                if (lm.findFirstVisibleItemPosition() < 2) {
//                    historyAndRecordsPagerAdapter.recyclerViewSongHistory.scrollToPosition(0);
//                }
            }
        });


        trackHistoryViewModel = ViewModelProviders.of(this).get(TrackHistoryViewModel.class);
        trackHistoryViewModel.getAllHistoryPaged().observe(getViewLifecycleOwner(), new Observer<PagedList<TrackHistoryEntry>>() {
            @Override
            public void onChanged(@Nullable PagedList<TrackHistoryEntry> songHistoryEntries) {
                trackHistoryAdapter.submitList(songHistoryEntries);
            }
        });
        LinearLayoutManager llmHistory = new LinearLayoutManager(getContext());
        llmHistory.setOrientation(RecyclerView.VERTICAL);
        recyclerViewSongHistory.setLayoutManager(llmHistory);
        recyclerViewSongHistory.setAdapter(trackHistoryAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), llmHistory.getOrientation());
        recyclerViewSongHistory.addItemDecoration(dividerItemDecoration);

        return  view;
    }
}