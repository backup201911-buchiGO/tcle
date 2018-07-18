package org.blogsite.youngsoft.piggybank.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.chart.CustomXAxisValueFormatter;
import org.blogsite.youngsoft.piggybank.chart.PBMarkerView;
import org.blogsite.youngsoft.piggybank.db.DBUtils;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.utils.CardEnum;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class CardDetailActivity extends AppCompatActivity {
    private static final String TAG = "CardDetailActivity";
    private Context context;

    private CardEnum card;
    private String mViewMode = "month";
    private int year;
    private int month;
    private int lastDay;
    private String sql_LineChart;
    private String sql_PieChart;
    private float totalValue = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_card_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        mViewMode = intent.getExtras().getString("ViewMode");
        year = intent.getExtras().getInt("year");
        month = intent.getExtras().getInt("month");
        String cn = intent.getExtras().getString("cardname");
        card = CardEnum.getByName(cn);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month-1);
        lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        String title = "";
        if("month".equals(mViewMode)) {
            sql_LineChart ="select year,month,day, sum(amount) as summ from smstable "
                    + "where card='" + cn + "' and year=" + String.valueOf(year)
                    + " and month=" + String.valueOf(month) + " group by day order by day";

            sql_PieChart = "select year,month,category, sum(amount) as summ from smstable "
                    + "where card='" + cn + "' and year=" + String.valueOf(year)
                    + " and month=" + String.valueOf(month) + " group by category,month order by category";
            title = String.valueOf(year) + getString(R.string.year) + " " + String.valueOf(month) + getString(R.string.month) + " "
                    + cn + " " + getResources().getString(R.string.activity_title_carddetail);
        }else{
            sql_LineChart ="select year,month, sum(amount) as summ from smstable "
                    + "where card='" + cn + "' and year=" + String.valueOf(year) + " group by month order by month";

            sql_PieChart = "select year,category, sum(amount) as summ from smstable "
                    + "where card='" + cn + "' and year=" + String.valueOf(year) + " group by category,year order by category";

            title = String.valueOf(year) + getString(R.string.year) + " "
                    + cn + " " + getResources().getString(R.string.activity_title_carddetail);
        }

        getSupportActionBar().setTitle(title);

        viewLineChart();
        viewCategoryPieChart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void viewLineChart(){
        ArrayList<Integer> data = new ArrayList<Integer>();
        ArrayList<Entry> values = new ArrayList<Entry>();
        float maxValue = -Float.MAX_VALUE;
        float minValue = Float.MAX_VALUE;
        String xLabel = "";
        try{
            data.clear();
            TextView chartTitle = (TextView)findViewById(R.id.lblLineChart);
            chartTitle.setTypeface(SmsUtils.typefaceNaumGothic);

            if("month".equals(mViewMode)) {
                chartTitle.setText(getString(R.string.linechart));
                data = DBUtils.getMonthlyData(context, sql_LineChart, lastDay);
                xLabel = getString(R.string.day);
                for (int i = 0; i < lastDay; i++) {
                    float value = (float)data.get(i);
                    maxValue = maxValue>value ? maxValue : value;
                    minValue = minValue<value ? minValue : value;

                    values.add(new Entry(i, value, getResources().getDrawable(R.drawable.star)));
                }
            }else{
                chartTitle.setText(getString(R.string.linechart_month));
                data = DBUtils.getYearlyData(context, year);
                xLabel = getString(R.string.month);
                for (int i = 0; i < 12; i++) {
                    int value = data.get(i);
                    maxValue = maxValue>value ? maxValue : value;
                    minValue = minValue<value ? minValue : value;
                    values.add(new Entry(i, value, getResources().getDrawable(R.drawable.star)));
                }
            }

            LineChart mLineChart = (LineChart) findViewById(R.id.linechart);
            mLineChart.clear();

            if(maxValue<=0){
                return ;
            }

            minValue = minValue>0f ? 0f : minValue;
            maxValue *= 1.3f;

            //mLineChart.setOnChartValueSelectedListener(this);

            //mLineChart.setViewPortOffsets(40f, 0f, 0f, 0f);

            mLineChart.setExtraOffsets(10f, 10f, 10f, 10f);
            mLineChart.setBorderColor(R.color.chartborder);
            mLineChart.setBorderWidth(1f);
            mLineChart.setDrawBorders(true);
            mLineChart.setDrawGridBackground(false);
            mLineChart.getDescription().setEnabled(false);
            mLineChart.setTouchEnabled(true);
            mLineChart.setDragEnabled(true);
            mLineChart.setScaleEnabled(true);
            mLineChart.setPinchZoom(true);

            // create a custom MarkerView (extend MarkerView) and specify the layout
            // to use for it
            PBMarkerView mv = new PBMarkerView(context, R.layout.custom_marker_view);
            mv.setChartView(mLineChart); // For bounds control
            mLineChart.setMarker(mv); // Set the marker to the chart

            // x-axis limit line
            LimitLine llXAxis = new LimitLine(data.size(), xLabel);

            llXAxis.setLineWidth(2f);
            //llXAxis.enableDashedLine(4f, 4f, 0f);
            llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            llXAxis.setTextSize(10f);
            llXAxis.setLabel(xLabel);

            XAxis xAxis = mLineChart.getXAxis();
            //xAxis.enableGridDashedLine(10f, 10f, 0f);
            if("month".equals(mViewMode)) {
                xAxis.setValueFormatter(new CustomXAxisValueFormatter(getString(R.string.day)));
            }else{
                xAxis.setValueFormatter(new CustomXAxisValueFormatter(getString(R.string.month)));
            }
            xAxis.setGridColor(Color.GRAY);
            xAxis.setTextColor(Color.BLUE);
            xAxis.setAxisLineColor(Color.GRAY);
            //xAxis.addLimitLine(llXAxis); // add x-axis limit line

            YAxis leftAxis = mLineChart.getAxisLeft();
            leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
            leftAxis.setGridColor(Color.GRAY);
            leftAxis.setTextColor(Color.BLUE);
            leftAxis.setAxisLineColor(Color.GRAY);
            leftAxis.setAxisMaximum(maxValue);
            leftAxis.setAxisMinimum(minValue);
            //leftAxis.setYOffset(20f);
            leftAxis.enableGridDashedLine(4f, 4f, 0f);
            leftAxis.setDrawZeroLine(true);

            // limit lines are drawn behind data (and not on top)
            leftAxis.setDrawLimitLinesBehindData(true);

            mLineChart.getAxisRight().setEnabled(false);

            LineDataSet dataset;
            if (mLineChart.getData() != null &&
                    mLineChart.getData().getDataSetCount() > 0) {
                dataset = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);
                dataset.setValues(values);
                mLineChart.getData().notifyDataChanged();
                mLineChart.notifyDataSetChanged();
            }else{
                // create a dataset and give it a type
                if("month".equals(mViewMode)) {
                    dataset = new LineDataSet(values, getString(R.string.dataset_label));
                }else{
                    dataset = new LineDataSet(values, getString(R.string.dataset_month_label));
                }

                dataset.setDrawIcons(false);

                dataset.setColor(Color.RED);
                dataset.setCircleColor(Color.RED);
                dataset.setValueTextColor(Color.RED);
                dataset.setLineWidth(1f);
                dataset.setCircleRadius(3f);
                dataset.setDrawCircleHole(false);
                dataset.setValueTextSize(8f);
                dataset.setFillAlpha(110);
                dataset.setDrawFilled(true);
                dataset.setFormLineWidth(1f);
                dataset.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                dataset.setFormSize(10.f);
                dataset.setDrawValues(false);
/*
                if (Utils.getSDKInt() >= 18) {
                    // fill drawable only supported on api level 18 and above
                    Drawable drawable = ContextCompat.getDrawable(context, R.drawable.fade_red);
                    dataset.setFillDrawable(drawable);
                }
                else {
                    dataset.setFillColor(Color.WHITE);
                }
*/
                dataset.setFillColor(Color.WHITE);

                dataset.setMode(LineDataSet.Mode.LINEAR);
                ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                dataSets.add(dataset); // add the datasets

                // create a data object with the datasets
                LineData lineData = new LineData(dataSets);

                // set data
                mLineChart.setData(lineData);
            }
            mLineChart.animateY(1000, Easing.EasingOption.EaseInCubic);

            if("month".equals(mViewMode)) {
                mLineChart.setVisibleXRange(0, lastDay-1);
                mLineChart.setVisibleYRange(minValue, maxValue, YAxis.AxisDependency.LEFT);
                mLineChart.centerViewTo(20, 50, YAxis.AxisDependency.LEFT);
            }else{
                mLineChart.setVisibleXRange(0, 11);
                mLineChart.setVisibleYRange(minValue, maxValue, YAxis.AxisDependency.LEFT);
                mLineChart.centerViewTo(20, 50, YAxis.AxisDependency.LEFT);
            }

            // get the legend (only possible after setting data)
            Legend l = mLineChart.getLegend();

            // modify the legend ...
            l.setForm(Legend.LegendForm.LINE);

            mLineChart.invalidate();
        }catch(Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }

    private void viewCategoryPieChart(){
        final PieChart mChart = (PieChart) findViewById(R.id.piechart);
        if(mChart!=null){
            mChart.clear();
        }

        try{
            mChart.setUsePercentValues(true);
            mChart.getDescription().setEnabled(false);
            mChart.setCenterTextTypeface(SmsUtils.typefaceNaumGothicCoding);

            mChart.setDrawHoleEnabled(true);
            mChart.setHoleColor(Color.WHITE);

            mChart.setTransparentCircleColor(Color.WHITE);
            mChart.setTransparentCircleAlpha(110);

            mChart.setHoleRadius(58f);
            mChart.setTransparentCircleRadius(61f);

            mChart.setDrawCenterText(true);

            mChart.setRotationEnabled(true);
            mChart.setHighlightPerTapEnabled(true);

            mChart.setMaxAngle(360f); // HALF CHART
            mChart.setRotationAngle(180f);
            mChart.setCenterTextOffset(0, 0);

            String title = "";
            if ("month".equals(mViewMode)) {
                title = "● " + String.valueOf(year) + "." + String.valueOf(month) + " " + getString(R.string.piechart_title);
            }else{
                title = "● " + String.valueOf(year) + " " + getString(R.string.piechart_title);
            }

            TextView textView = (TextView)findViewById(R.id.lblPieChart);
            textView.setTypeface(SmsUtils.typefaceNaumGothic);
            textView.setText(title);
            HashMap<String, Integer> catMap = DBUtils.getCategories(context, sql_PieChart, false);
            ArrayList<PieEntry> values = new ArrayList<PieEntry>();
            Iterator<String> it = catMap.keySet().iterator();
            String catname = "";
            float defValue = -Float.MAX_VALUE;
            while(it.hasNext()){
                String key = it.next();
                float value = (float)catMap.get(key);
                if (defValue < value) {
                    defValue = value;
                    catname = key;
                }
                totalValue += value;
                values.add(new PieEntry(value, key));
            }

            PieDataSet dataSet = new PieDataSet(values, getString(R.string.piechart_detail_title));
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            //dataSet.setSelectionShift(0f);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(12f);
            data.setValueTextColor(Color.BLACK);
            data.setValueTypeface(SmsUtils.typefaceNaumGothicCoding);
            mChart.setData(data);

            mChart.invalidate();

            mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

            // add a selection listener
            mChart.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    if(e==null){
                        return;
                    }
                    PieEntry pieEntry = (PieEntry)e;
                    float value = pieEntry.getValue();
                    String catname = pieEntry.getLabel();
                    float percent = 100f*value / totalValue;
                    mChart.setCenterText(generateCenterSpannableText(catname + "\n" + StringUtils.format(value, "##,###") + getString(R.string.won) + "\n"
                            + StringUtils.format(percent, "##,###.0") + "%"));

                    Intent intent = new Intent(context, DonationDetailActivity.class);
                    intent.putExtra("CardName", card.getName());
                    intent.putExtra("Category", catname);
                    startActivity(intent);
                }

                @Override
                public void onNothingSelected() {

                }
            });

            Legend l = mChart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(false);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);

            // entry label styling
            mChart.setEntryLabelColor(Color.BLACK);
            mChart.setEntryLabelTypeface(SmsUtils.typefaceNaumGothic);
            mChart.setEntryLabelTextSize(10f);

            float percent = 100f*defValue / totalValue;
            mChart.setCenterText(generateCenterSpannableText(catname + "\n" + StringUtils.format(defValue, "##,###") + getString(R.string.won) + "\n"
                    + StringUtils.format(percent, "##,###.0") + "%"));
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }

    private SpannableString generateCenterSpannableText(String msg) {
        SpannableString s = new SpannableString(msg);
        try {
            String[] ss = StringUtils.split(msg, "\n");
            int l1 = ss[0].length();
            int l2 = ss[1].length();
            int l3 = ss[2].length();

            s.setSpan(new RelativeSizeSpan(1.7f), 0, l1, 0);
            s.setSpan(new StyleSpan(Typeface.NORMAL), l1, s.length() - l1, 0);
            s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 0, l1, 0);

            //        s.setSpan(new ForegroundColorSpan(Color.GRAY), l1+1, s.length() - (l1+1), 0);
            s.setSpan(new RelativeSizeSpan(1f), l1 + 1, s.length() - (l1 + 1), 0);

            s.setSpan(new StyleSpan(Typeface.NORMAL), s.length() - (l1 + l2 + 2), s.length(), 0);
            //        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - (l1+l2+2), s.length(), 0);
        }catch(Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
        return s;
    }
}
