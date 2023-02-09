package com.lakshitasuman.musicstation.radio.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lakshitasuman.musicstation.radio.data.DataStatistics;
import com.lakshitasuman.musicstation.R;

public class ItemAdapterStatistics extends ArrayAdapter<DataStatistics> {
	private Context context;
	private int resourceId;

	public ItemAdapterStatistics(Context context, int resourceId) {
		super(context, resourceId);
		this.resourceId = resourceId;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DataStatistics aData = getItem(position);
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(resourceId, null);
		}
		TextView aTextViewTop = (TextView) convertView.findViewById(R.id.stats_name);
		TextView aTextViewBottom = (TextView) convertView.findViewById(R.id.stats_value);
		if (aTextViewTop != null) {
			aTextViewTop.setText("" + aData.Name);
		}
		if (aTextViewBottom != null) {
			aTextViewBottom.setText("" + aData.Value);
		}
		return convertView;
	}
}
