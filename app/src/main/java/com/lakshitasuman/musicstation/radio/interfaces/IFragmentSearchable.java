package com.lakshitasuman.musicstation.radio.interfaces;

import com.lakshitasuman.musicstation.radio.station.StationsFilter;

public interface IFragmentSearchable {
    void Search(StationsFilter.SearchStyle searchStyle, String query);
}
