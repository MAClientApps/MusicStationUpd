package com.lakshitasuman.musicstation.radio;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.radio.adapters.ItemAdapterStatistics;
import com.lakshitasuman.musicstation.radio.data.DataStatistics;
import com.lakshitasuman.musicstation.radio.interfaces.IFragmentRefreshable;

import okhttp3.OkHttpClient;

public class FragmentServerInfo extends Fragment implements IFragmentRefreshable {
    private ItemAdapterStatistics itemAdapterStatistics;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_statistics,null);

        if (itemAdapterStatistics == null) {
            itemAdapterStatistics = new ItemAdapterStatistics(getActivity(), R.layout.list_item_statistic);
        }

        ListView lv = (ListView)view.findViewById(R.id.listViewStatistics);
        lv.setAdapter(itemAdapterStatistics);

        Download(false);

        return view;
    }



    void Download(final boolean forceUpdate){
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(ActivityMain.ACTION_SHOW_LOADING));

        MainRadioHelper radioDroidApp = (MainRadioHelper) getActivity().getApplication();
        final OkHttpClient httpClient = radioDroidApp.getHttpClient();

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return Utils.downloadFeedRelative(httpClient, getActivity(), "json/stats", forceUpdate, null);
            }

            @Override
            protected void onPostExecute(String result) {
                if(getContext() != null)
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(ActivityMain.ACTION_HIDE_LOADING));
                if (result != null) {
                    itemAdapterStatistics.clear();
                    DataStatistics[] items = DataStatistics.DecodeJson(result);
                    for(DataStatistics item: items) {
                        itemAdapterStatistics.add(item);
                    }
                }else{
                    try {
                        Toast toast = Toast.makeText(getContext(), getResources().getText(R.string.error_list_update), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    catch(Exception exception){
                        Log.e("ERR",exception.toString());
                    }
                }
                super.onPostExecute(result);
            }
        }.execute();
    }

    @Override
    public void Refresh() {
        Download(true);
    }
}
