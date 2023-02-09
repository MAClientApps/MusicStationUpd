package com.lakshitasuman.musicstation.musicplayer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lakshitasuman.musicstation.R;


public class TimerActivity extends AppCompatActivity {
    private FrameLayout adContainerView;
    ImageView back;
    TimePicker simpleTimePicker;
    Button start;
    TextView timeSelected;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);



        setContentView((int) R.layout.activity_timer);
        simpleTimePicker = (TimePicker) findViewById(R.id.simpleTimePicker);
        timeSelected = (TextView) findViewById(R.id.timeselected);
        back = (ImageView) findViewById(R.id.back);
        start = (Button) findViewById(R.id.start);
        if (Build.VERSION.SDK_INT >= 23) {
            simpleTimePicker.setHour(0);
            simpleTimePicker.setMinute(0);
        }
        simpleTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i2) {
                timeSelected.setText(i + ":" + i2);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String charSequence = timeSelected.getText().toString();
                String[] split = charSequence.split(":");
                String str = split[0];
                String str2 = split[1];
                if (charSequence.equals("0:0")) {
                    Toast.makeText(TimerActivity.this, "please Select the time first", Toast.LENGTH_LONG).show();
                    return;
                }
                PlayerActivity.isTimerSet = true;
                Intent intent = new Intent(TimerActivity.this, PlayerService.class);
                intent.setAction(Constants.ACTION.STARTTIMER);
                intent.putExtra("hour", str);
                intent.putExtra("minute", str2);
                startService(intent);

                Toast.makeText(TimerActivity.this, "Timer set for " + str + " hour and " + str2 + " minutes", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });
    }




}
