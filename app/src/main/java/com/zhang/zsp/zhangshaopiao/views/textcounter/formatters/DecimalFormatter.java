package com.zhang.zsp.zhangshaopiao.views.textcounter.formatters;



import com.zhang.zsp.zhangshaopiao.views.textcounter.Formatter;

import java.text.DecimalFormat;

/**
 * Created by prem on 10/28/14.
 */
public class DecimalFormatter implements Formatter {

    private final DecimalFormat format = new DecimalFormat("#.0");

    @Override
    public String format(String prefix, String suffix, float value) {
        return prefix + format.format(value) + suffix;
    }
}
