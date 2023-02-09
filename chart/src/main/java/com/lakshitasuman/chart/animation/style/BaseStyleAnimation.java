package com.lakshitasuman.chart.animation.style;

import com.lakshitasuman.chart.model.ChartSet;
import com.lakshitasuman.chart.view.ChartView;

public abstract class BaseStyleAnimation {
    private static final long DELAY_BETWEEN_UPDATES = 100;
    private final Runnable mAnimator = new Runnable() {
      @Override
        public void run() {
            if (mChartView.canIPleaseAskYouToDraw()) {
                mChartView.postInvalidate();
                getUpdate(mSet);
            }
        }
    };
    
    public ChartView mChartView;
    
    public ChartSet mSet;

    
    public abstract void nextUpdate(ChartSet chartSet);

    public void play(ChartView chartView, ChartSet chartSet) {
        mChartView = chartView;
        mSet = chartSet;
        getUpdate(mSet);
    }



    public void getUpdate(ChartSet chartSet) {
        nextUpdate(chartSet);
        mChartView.postDelayed(mAnimator, DELAY_BETWEEN_UPDATES);
    }
}
