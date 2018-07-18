package org.blogsite.youngsoft.piggybank.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.adapter.HistoryListData;
import org.blogsite.youngsoft.piggybank.adapter.HistoryViewAdapter;
import org.blogsite.youngsoft.piggybank.chart.CustomXAxisValueFormatter;
import org.blogsite.youngsoft.piggybank.chart.PBMarkerView;
import org.blogsite.youngsoft.piggybank.db.DBUtils;
import org.blogsite.youngsoft.piggybank.dialog.ConfirmUtils;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.setting.DonationHistory;
import org.blogsite.youngsoft.piggybank.utils.Alert;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class HistoryViewActivity extends AppCompatActivity {
    private static final String TAG = "HistoryViewActivity";

    private Context context;

    private ArrayList<DonationHistory> histories;
    private LineChart mLineChart;

    private Calendar cal;
    private int minYear = -1;
    private int current_year;

    private int year;
    private int month;

    private final int PREVIEW_BUTTON = 0;
    private final int NEXT_BUTTON = 1;
    private int buttonFlag = -1;
    private ImageButton prevButton;
    private ImageButton nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_donation_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;

        initView();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
/*
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
*/
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        intent.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
        return super.onOptionsItemSelected(item);
    }

    private void initView(){
        try {
            cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;

            current_year = year;

            prevButton = (ImageButton) findViewById(R.id.navPrevious);
            prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonFlag = PREVIEW_BUTTON;
                    navButtonClick();
                }
            });

            nextButton = (ImageButton) findViewById(R.id.navBtnNext);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonFlag = NEXT_BUTTON;
                    navButtonClick();
                }
            });

            minYear = DBUtils.getMinYear(context, "select min(year) from donation_history");

            viewData();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }

    private void viewData(){
        try{
            String sql = "select * from donation_history where year=" + String.valueOf(year)
                    + " group by year,month order by year,month";
            histories = DBUtils.getDonationHistory(context, sql);
            viewAmountLineChart();
            statDisplay();
            historyListView();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void viewAmountLineChart(){
        Integer[] data = new Integer[12];
        ArrayList<Entry> values = new ArrayList<Entry>();
        float maxValue = -Float.MAX_VALUE;
        float minValue = Float.MAX_VALUE;
        String xLabel = "";


        for(int i=0; i<12; i++){
            data[i] = 0;
        }

        int len = histories.size();
        for(int i=0; i<len; i++){
            int m = histories.get(i).getMonth();
            data[m-1] = histories.get(i).getAmount();
        }


        xLabel = getString(R.string.month);
        for (int i = 0; i < 12; i++) {
            int value = data[i];
            maxValue = maxValue>value ? maxValue : value;
            minValue = minValue<value ? minValue : value;
            values.add(new Entry(i, value, getResources().getDrawable(R.drawable.star)));
        }

        mLineChart = (LineChart)findViewById(R.id.historylinechart);
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
        LimitLine llXAxis = new LimitLine(data.length, xLabel);

        llXAxis.setLineWidth(2f);
        //llXAxis.enableDashedLine(4f, 4f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);
        llXAxis.setLabel(xLabel);

        XAxis xAxis = mLineChart.getXAxis();
        //xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setValueFormatter(new CustomXAxisValueFormatter(getString(R.string.month)));
        xAxis.setGridColor(Color.GRAY);
        xAxis.setTextColor(Color.BLUE);
        xAxis.setAxisLineColor(Color.GRAY);
        xAxis.setLabelCount(11);
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines

//        leftAxis.addLimitLine(upper);

        leftAxis.setGridColor(Color.GRAY);
        leftAxis.setTextColor(Color.BLUE);
        leftAxis.setAxisLineColor(Color.GRAY);
        leftAxis.setAxisMaximum(maxValue);
        leftAxis.setAxisMinimum(minValue);
        //leftAxis.setYOffset(20f);
        //leftAxis.enableGridDashedLine(4f, 4f, 0f);
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
            dataset = new LineDataSet(values, getString(R.string.amount_by_month));

            dataset.setDrawIcons(false);

            // set the line to be drawn like this "- - - - - -"
//                set.enableDashedLine(10f, 5f, 0f);
//                set.enableDashedHighlightLine(10f, 5f, 0f);
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
            mLineChart.setBorderColor(Color.WHITE);
            mLineChart.setData(lineData);
        }

        mLineChart.animateY(1000, Easing.EasingOption.EaseInCubic);

        mLineChart.setVisibleXRange(0, 11);
        mLineChart.setVisibleYRange(minValue, maxValue, YAxis.AxisDependency.LEFT);
        mLineChart.centerViewTo(20, 50, YAxis.AxisDependency.LEFT);

        // get the legend (only possible after setting data)
        Legend l = mLineChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);

        mLineChart.invalidate();
    }

    private void statDisplay(){
        TextView title = (TextView)findViewById(R.id.lblTitle);
        TextView termTextView = (TextView)findViewById(R.id.lblTerm);

        String sql = "select sum(amount) as summ from donation_history where year=" + String.valueOf(year) + " order by month";

        int amount = DBUtils.getTotalAmount(context, sql);

        title.setText(year + " " + getString(R.string.total_amount_by_year));
        String s = year + ".01.01" + " ~ " + year + ".12.31";
        termTextView.setText(s);

        cal.set(Calendar.MONTH, month-1);

        TextView grandTotalText = (TextView)findViewById(R.id.lblGrandTotal);
        grandTotalText.setTypeface(SmsUtils.typefaceNaumGothicCoding);
        grandTotalText.setText(StringUtils.format(amount, "#,### " + getString(R.string.won) + " "));
    }

    public void historyListView(){
        ListView mListView = (ListView)findViewById(R.id.historyList);
        HistoryViewAdapter mAdapter = new HistoryViewAdapter(context);
        mListView.setOverScrollMode(0);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(mItemClickListener);

        for(DonationHistory history : histories){
            mAdapter.addItem(history);
        }

        SmsUtils.setListViewHeightBasedOnChildren(context, mListView);
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
            HistoryListData historyData = (HistoryListData)parent.getAdapter().getItem(position);

            StringBuilder sb = new StringBuilder();
            String date = historyData.date;
            String donationName = historyData.donationName;
            String amountValue = historyData.amountValue;
            String account = historyData.account;
            String bankname = historyData.bankname;
            String home = historyData.home;
            String tel = historyData.tel;
            String address = historyData.address;
            String category = historyData.category;

            sb.append(category).append(" ").append(getString(R.string.donation_already_run_1)).append(" <b>").append(donationName).append("</b").append(getString(R.string.donation_run_1))
                    .append(" ").append(bankname).append(" ").append(getString(R.string.account)).append( " ")
                    .append(account).append("<b>").append(amountValue).append("</b>").append(getString(R.string.donation_already_run_2))
                    .append(" ").append(date).append(getString(R.string.donation_already_run_3));
            sb.append("<br/><br/><b><font color=\"#FCFF33\">").append(donationName).append("</font></b><br/><br/>")
                    .append(address).append("<br/>").append(home).append("<br/>").append(tel);

            ConfirmUtils confirmDialog = new ConfirmUtils(context, getString(R.string.donation_already_run_title), sb.toString());
            confirmDialog.setHtml(true);
            confirmDialog.show();


        }
    };

    private void navButtonClick(){
        yearNavi();
    }

    private void yearNavi(){
        if(buttonFlag== PREVIEW_BUTTON) {
            year = year - 1;
        }else if(buttonFlag== NEXT_BUTTON){
            year = year + 1;
        }
        String msg = "";
        if(year>current_year) {
            year = current_year;
            msg = String.valueOf(current_year) + getString(R.string.last_year);
        }else if(year<minYear){
            msg = String.valueOf(minYear) + getString(R.string.first_year);
            year = year + 1;
        }else {
            viewData();
        }
        if(!"".equals(msg)){
            LayoutInflater inflater = getLayoutInflater();
            View toastDesign = inflater.inflate(R.layout.toast_design,
                    (ViewGroup) findViewById(R.id.toast_design_root));
            Alert.showToast(context, toastDesign, msg);
        }
    }
}
