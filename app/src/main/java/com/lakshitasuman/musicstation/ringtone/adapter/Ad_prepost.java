package com.lakshitasuman.musicstation.ringtone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lakshitasuman.musicstation.R;


import java.util.ArrayList;
import java.util.Iterator;

public class Ad_prepost extends RecyclerView.Adapter<Ad_prepost.Holder> implements Filterable {
    ArrayList<String> allList;
    ArrayList<String> allListFilter;
    Context cn;
    OnMyCommonItem onMyCommonItem;
    String selected;

    public Ad_prepost(Context context, ArrayList<String> allList, String selected, OnMyCommonItem onMyCommonItem) {
        ArrayList<String> arrayList2 = new ArrayList<>();
        allListFilter = arrayList2;
        cn = context;
        this.allList = allList;
        arrayList2.addAll(allList);
        this.selected = selected;
        this.onMyCommonItem = onMyCommonItem;
    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView btnSelect;
        LinearLayout layMain;
        TextView setName;
        ImageView setNone;


        public Holder(View view) {
            super(view);
            layMain = (LinearLayout) view.findViewById(R.id.laymain);
            setName = (TextView) view.findViewById(R.id.setname);
            setNone = (ImageView) view.findViewById(R.id.setnone);
            btnSelect = (ImageView) view.findViewById(R.id.btnselect);

        }
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(cn).inflate(R.layout.ad_prepost, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, final int i) {
        holder.setName.setText(allListFilter.get(i));
        holder.layMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMyCommonItem.OnMyClick1(i, allListFilter.get(i));
            }
        });
        if (selected.equalsIgnoreCase(allListFilter.get(i))) {
            holder.btnSelect.setImageResource(R.drawable.presed2);
        } else {
            holder.btnSelect.setImageResource(R.drawable.unpresed2);
        }
        if (allListFilter.get(i).equalsIgnoreCase("None")) {
            holder.setNone.setVisibility(View.VISIBLE);
        } else {
            holder.setNone.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return allListFilter.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            public FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                String str = (String) charSequence;
                ArrayList arrayList = new ArrayList();
                if (str.isEmpty()) {
                    arrayList.addAll(allList);
                } else {
                    Iterator<String> it = allList.iterator();
                    while (it.hasNext()) {
                        String next = it.next();
                        if (next.toLowerCase().contains(str.toLowerCase())) {
                            arrayList.add(next);
                        }
                    }
                }
                filterResults.values = arrayList;
                return filterResults;
            }

            @Override
            public void publishResults(CharSequence charSequence, FilterResults filterResults) {
                allListFilter = (ArrayList) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
