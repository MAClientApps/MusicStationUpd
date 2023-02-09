package com.lakshitasuman.musicstation.ringtone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.ringtone.model.Songs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class SongAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<Songs> list;
    Context mContext;
    private ArrayList<Songs> title;

    @Override
    public long getItemId(int i) {
        return (long) i;
    }

    public SongAdapter(Context context, ArrayList<Songs> arrayList) {
        mContext = context;
        title = arrayList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        list = new ArrayList<>();
        list.addAll(arrayList);
    }

    @Override
    public int getCount() {
        return title.size();
    }

    @Override
    public Object getItem(int i) {
        return Integer.valueOf(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.audio_adapter, (ViewGroup) null);
        }
        ((TextView) view.findViewById(R.id.name)).setText(title.get(i).getTitle());
        ((TextView) view.findViewById(R.id.url_2)).setText(title.get(i).getArtist());
        return view;
    }


    public void filter(String str) {
        String lowerCase = str.toLowerCase(Locale.getDefault());
        title.clear();
        if (lowerCase.length() == 0) {
            title.addAll(list);
        } else {
            Iterator<Songs> it = list.iterator();
            while (it.hasNext()) {
                Songs next = it.next();
                if (next.getTitle().toLowerCase(Locale.getDefault()).contains(lowerCase)) {
                    title.add(next);
                }
            }
        }
        notifyDataSetChanged();
    }
}
