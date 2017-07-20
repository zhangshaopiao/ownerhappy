package com.zhang.zsp.zhangshaopiao.views.textcounter;

import android.util.Log;

/**
 * Created by prem on 10/28/14.
 *
 * Class that handles the counting up/down of the text value
 */
class Counter implements Runnable {

    final CounterView view;
    final float increment, startValue, endValue;
    final long interval;
    float currentValue, newValue;

    Counter(CounterView view, float startValue, float endValue, long interval, float increment) {
        this.view = view;
        this.startValue = startValue;
        this.endValue = endValue;
        this.interval = interval;
        this.increment = increment;
        this.newValue = this.startValue;
        if(startValue <= endValue) this.currentValue = this.startValue - increment;
        else this.currentValue = this.startValue + increment;
    }

    @Override
    public void run() {
        if (valuesAreCorrect()) {
            float valueToSet;
            if(startValue <= endValue){
                if (newValue <= endValue) {
                    valueToSet = newValue;
                } else {
                    valueToSet = endValue;
                }
                view.setCurrentTextValue(valueToSet);
                currentValue = newValue;
                newValue += increment;
                view.removeCallbacks(Counter.this);
                if(valueToSet != endValue) view.postDelayed(Counter.this, interval);
            }else {
                if (newValue >= endValue) {
                    valueToSet = newValue;
                } else {
                    valueToSet = endValue;
                }
                view.setCurrentTextValue(valueToSet);
                currentValue = newValue;
                newValue -= increment;
                view.removeCallbacks(Counter.this);
                if(valueToSet != endValue) view.postDelayed(Counter.this, interval);
            }

        }
    }

    private boolean valuesAreCorrect() {
        //if(increment >= 0) {
        if(startValue <= endValue) {
            return newValue >= currentValue;
        } else {
            return newValue <= currentValue;
        }
    }
}
