package com.lakshitasuman.musicstation.musicplayer.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.lakshitasuman.chart.model.ChartSet;
import com.lakshitasuman.chart.model.LineSet;
import com.lakshitasuman.chart.renderer.AxisRenderer;
import com.lakshitasuman.chart.view.ChartView;
import com.lakshitasuman.chart.view.LineChartView;

import java.util.ArrayList;


import com.lakshitasuman.musicstation.MainActivity;
import com.lakshitasuman.musicstation.musicplayer.PlayerActivity;
import com.lakshitasuman.musicstation.R;

public class EqualizerFragment extends Fragment {
    ImageView backBtn;
    LineChartView chart;
    Context ctx;
    LineSet dataset;
    FrameLayout equalizerBlocker;
    TextView fragTitle;
    onCheckChangedListener mCallback;
    LinearLayout mLinearLayout;
    short numberOfFrequencyBands;
    Paint paint;
    float[] points;
    Spinner presetSpinner;
    SeekBar[] seekBarFinal = new SeekBar[5];
    ImageView spinnerDropDownIcon;
    int y = 0;
    SeekBar seekBar;
    int min = 0, max = 100, current = 50;


    public interface onCheckChangedListener {
        void onCheckChanged(boolean change);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx = context;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_equalizer, viewGroup, false);
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        backBtn = (ImageView) view.findViewById(R.id.equalizer_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        fragTitle = (TextView) view.findViewById(R.id.equalizer_fragment_title);
        spinnerDropDownIcon = (ImageView) view.findViewById(R.id.spinner_dropdown_icon);
        spinnerDropDownIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presetSpinner.performClick();
            }
        });
        presetSpinner = (Spinner) view.findViewById(R.id.equalizer_preset_spinner);
        equalizerBlocker = (FrameLayout) view.findViewById(R.id.equalizerBlocker);
        chart = (LineChartView) view.findViewById(R.id.lineChart);
        paint = new Paint();
        dataset = new LineSet();
        mLinearLayout = (LinearLayout) view.findViewById(R.id.equalizerContainer);
        TextView textView = new TextView(getContext());
        textView.setText("Equalizer");
        textView.setTextSize(18.0f);
        textView.setGravity(1);
        numberOfFrequencyBands = 5;

        points = new float[this.numberOfFrequencyBands];
        final short s = PlayerActivity.mEqualizer.getBandLevelRange()[0];
        short s2 = PlayerActivity.mEqualizer.getBandLevelRange()[1];
        for (short s3 = 0; s3 < numberOfFrequencyBands; s3 = (short) (s3 + 1)) {
            TextView textView2 = new TextView(getContext());
            textView2.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
            textView2.setGravity(1);
            textView2.setTextColor(Color.parseColor("#FFFFFF"));
            textView2.setText((PlayerActivity.mEqualizer.getCenterFreq(s3) / 1000) + "Hz");
            new LinearLayout(getContext()).setOrientation(1);
            TextView textView3 = new TextView(getContext());
            textView3.setLayoutParams(new ViewGroup.LayoutParams(-2, -1));
            textView3.setTextColor(Color.parseColor("#FFFFFF"));
            textView3.setText((s / 100) + "dB");
            TextView textView4 = new TextView(getContext());
            textView3.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            textView4.setTextColor(Color.parseColor("#FFFFFF"));
            textView4.setText((s2 / 100) + "dB");
            new LinearLayout.LayoutParams(-1, -2).weight = 1.0f;
            SeekBar seekBar = new SeekBar(getContext());
            TextView textView5 = new TextView(getContext());
            switch (s3) {
                case 0:
                    seekBar = (SeekBar) view.findViewById(R.id.seekBar1);
                    textView5 = (TextView) view.findViewById(R.id.textView1);
                    break;
                case 1:
                    seekBar = (SeekBar) view.findViewById(R.id.seekBar2);
                    textView5 = (TextView) view.findViewById(R.id.textView2);
                    break;
                case 2:
                    seekBar = (SeekBar) view.findViewById(R.id.seekBar3);
                    textView5 = (TextView) view.findViewById(R.id.textView3);
                    break;
                case 3:
                    seekBar = (SeekBar) view.findViewById(R.id.seekBar4);
                    textView5 = (TextView) view.findViewById(R.id.textView4);
                    break;
                case 4:
                    seekBar = (SeekBar) view.findViewById(R.id.seekBar5);
                    textView5 = (TextView) view.findViewById(R.id.textView5);
                    break;
            }
            seekBarFinal[s3] = seekBar;
            seekBar.setId(s3);
            seekBar.setMax(s2 - s);
            textView5.setText(textView2.getText());
            textView5.setTextColor(-1);
            textView5.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            if (MainActivity.isEqualizerReloaded) {
                points[s3] = (float) (MainActivity.seekbarPos[s3] - s);
                dataset.addPoint(textView2.getText().toString(), points[s3]);
                seekBar.setProgress(MainActivity.seekbarPos[s3] - s);
            } else {
                points[s3] = (float) (PlayerActivity.mEqualizer.getBandLevel(s3) - s);
                dataset.addPoint(textView2.getText().toString(), points[s3]);
                seekBar.setProgress(PlayerActivity.mEqualizer.getBandLevel(s3) - s);
                MainActivity.seekbarPos[s3] = PlayerActivity.mEqualizer.getBandLevel(s3);
                MainActivity.isEqualizerReloaded = true;
            }
            short finalS = s3;
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                    PlayerActivity.mEqualizer.setBandLevel(finalS, (short) (s + i));
                    points[seekBar.getId()] = (float) (PlayerActivity.mEqualizer.getBandLevel(finalS) - s);
                    MainActivity.seekbarPos[seekBar.getId()] = s + i;
                    MainActivity.equalizerModel.getSeekbarPos()[seekBar.getId()] = i + s;
                    dataset.updateValues(points);
                    chart.notifyDataUpdate();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    presetSpinner.setSelection(0);
                    MainActivity.presetPos = 0;
                    MainActivity.equalizerModel.setPresetPos(0);
                }
            });
        }
        equalizeSound();
        paint.setColor(Color.parseColor("#ffffff"));
        paint.setStrokeWidth((float) (MainActivity.ratio * 1.1d));
        dataset.setColor(Color.parseColor("#ffffff"));
        dataset.setSmooth(true);
        dataset.setThickness(5.0f);
        chart.setXAxis(false);
        chart.setYAxis(false);
        chart.setYLabels(AxisRenderer.LabelPosition.NONE);
        chart.setXLabels(AxisRenderer.LabelPosition.NONE);
        chart.setGrid(ChartView.GridType.NONE, 7, 10, paint);
        chart.setAxisBorderValues(-300, 3300);
        chart.addData((ChartSet) dataset);
        chart.show();
        Button button = new Button(getContext());
        button.setBackgroundColor(Color.parseColor("#ffffff"));
        button.setTextColor(-1);
    }
    public void equalizeSound() {
        ArrayList arrayList = new ArrayList();
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R.layout.spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(17367049);
        arrayList.add("Custom");
        for (short s = 0; s < PlayerActivity.mEqualizer.getNumberOfPresets(); s = (short) (s + 1)) {
            arrayList.add(PlayerActivity.mEqualizer.getPresetName(s));
        }
        presetSpinner.setAdapter(arrayAdapter);
        presetSpinner.setDropDownWidth((MainActivity.screen_width * 3) / 4);
        if (MainActivity.isEqualizerReloaded && MainActivity.presetPos != 0) {
            presetSpinner.setSelection(MainActivity.presetPos);
        }
        presetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (i != 0) {
                    try {
                        PlayerActivity.mEqualizer.usePreset((short) (i - 1));
                        MainActivity.presetPos = i;
                        short s = PlayerActivity.mEqualizer.getBandLevelRange()[0];
                        for (short s2 = 0; s2 < 5; s2 = (short) (s2 + 1)) {
                            seekBarFinal[s2].setProgress(PlayerActivity.mEqualizer.getBandLevel(s2) - s);
                            points[s2] = (float) (PlayerActivity.mEqualizer.getBandLevel(s2) - s);
                            MainActivity.seekbarPos[s2] = PlayerActivity.mEqualizer.getBandLevel(s2);
                            MainActivity.equalizerModel.getSeekbarPos()[s2] = PlayerActivity.mEqualizer.getBandLevel(s2);
                        }
                        dataset.updateValues(points);
                        chart.notifyDataUpdate();
                    } catch (Exception exceptionxception) {
                        Toast.makeText(ctx, "Error while updating Equalizer", 0).show();
                    }
                }
                MainActivity.equalizerModel.setPresetPos(i);
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
