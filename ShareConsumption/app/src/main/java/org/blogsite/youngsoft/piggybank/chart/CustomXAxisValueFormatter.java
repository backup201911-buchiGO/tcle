package org.blogsite.youngsoft.piggybank.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by klee on 2018-02-09.
 */

public class CustomXAxisValueFormatter implements IAxisValueFormatter {

    private DecimalFormat mFormat;

    public CustomXAxisValueFormatter(String unit) {
        mFormat = new DecimalFormat("##" + unit);
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mFormat.format(value+1);
    }
}
