package org.blogsite.youngsoft.piggybank.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.analyzer.Day;
import org.blogsite.youngsoft.piggybank.chart.CustomXAxisValueFormatter;
import org.blogsite.youngsoft.piggybank.chart.PBMarkerView;
import org.blogsite.youngsoft.piggybank.db.DBUtils;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.utils.Alert;
import org.blogsite.youngsoft.piggybank.utils.BackPressCloseHandler;
import org.blogsite.youngsoft.piggybank.utils.CardEnum;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SmsChartViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SmsChartViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SmsChartViewFragment extends Fragment {
    private static final String TAG = "SmsChartViewFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mViewMode;
    private String mParam2;

    private float cardTotalAmount = 0f;

    private OnFragmentInteractionListener mListener;

    private LineChart mLineChart;

    private Calendar cal;
    private int minYear = -1;
    private int current_year;
    private int current_month;
    private int prev_Year;
    private int prev_Month;

    private Day day;

    private int year;
    private int month;
    private final int startDay = 1;
    private int lastDay;

    private final int PREVIEW_BUTTON = 0;
    private final int NEXT_BUTTON = 1;
    private int buttonFlag = -1;
    private ImageButton prevButton;
    private ImageButton nextButton;

    private BackPressCloseHandler backPressCloseHandler;

    private int grandTotalAmount = 0;

    public SmsChartViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SmsChartViewFragment newInstance(String param1, String param2) {
        SmsChartViewFragment fragment = new SmsChartViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mViewMode = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sms_chart, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void initView(){
        try {
            View v = getView();
            backPressCloseHandler = new BackPressCloseHandler(this.getActivity());

            cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;

            current_year = year;
            current_month = month;

            if (current_month == 1) {
                prev_Year = current_year - 1;
                prev_Month = 12;
            } else {
                prev_Year = current_year;
                prev_Month = current_month - 1;
            }

            lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            prevButton = (ImageButton) v.findViewById(R.id.month_BtnPrevious);
            prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonFlag = PREVIEW_BUTTON;
                    monthButtonClick();
                }
            });

            nextButton = (ImageButton) v.findViewById(R.id.month_BtnNext);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonFlag = NEXT_BUTTON;
                    monthButtonClick();
                }
            });

            minYear = DBUtils.getMinYear(this.getContext());
            viewData();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }

    private void viewData(){
        day = new Day();
        try{
            statDisplay();
            viewAmountLineChart();
            viewCardPieChart();
            //////viewCategoryRadarChart();
            viewCategoryHalfPieChart();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void statDisplay(){
        View v = getView();
        TextView title = (TextView)v.findViewById(R.id.month_LblTitle);
        TextView termTextView = (TextView)v.findViewById(R.id.month_LblTerm);
        String sql = "";
        if("month".equals(mViewMode)) {
            sql = "select sum(amount) as summ from smstable where year=" + String.valueOf(year) + " and month=" + String.valueOf(month) + " order by month";
        }else {
            sql = "select sum(amount) as summ from smstable where year=" + String.valueOf(year) + " order by month";
        }
        grandTotalAmount = DBUtils.getTotalAmount(getContext(), sql);
        if("month".equals(mViewMode)) {
            title.setText(month + " " + getString(R.string.consumption_by_month));
            DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
            String smonth = String.valueOf(month);
            smonth = month<10 ? "0" + smonth : smonth;
            String s = year + "." + smonth + ".01" + " ~ " + year + "." + smonth + "." + lastDay;
            termTextView.setText(s);
        }else{
            title.setText(year + " " + getString(R.string.consumption_by_year));
            termTextView.setText(year + getString(R.string.term_start_by_year) +" ~ " + year + getString(R.string.term_end_by_year));
        }

        cal.set(Calendar.MONTH, month-1);
        lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        TextView grandTotalText = (TextView)v.findViewById(R.id.month_LblGrandTotal);
        grandTotalText.setTypeface(SmsUtils.typefaceNaumGothicCoding);
        grandTotalText.setText(StringUtils.format(grandTotalAmount, "#,### " + getString(R.string.won) + " "));
    }

    private void monthButtonClick(){
        if("month".equals(mViewMode)) {
            monthNavi();
        }else{
            yearNavi();
        }
    }

    private void monthNavi(){
        View v = getView();
        int oldMonth = month;
        int oldYear = year;
        if(buttonFlag== PREVIEW_BUTTON) {
            if (month == 1) {
                year = year - 1;
                month = 12;
            } else {
                month = month - 1;
            }
        }else if(buttonFlag== NEXT_BUTTON){
            if(month==12){
                year = year + 1;
                month = 1;
            }else{
                month = month + 1;
            }
        }

        String msg = "";
        if(year==current_year && month>current_month) {
            month = current_month;
            msg = String.valueOf(current_month) + getString(R.string.last_month_1) + " " + String.valueOf(current_year) + getString(R.string.last_month_2);
        }else if(year<minYear) {
            msg = String.valueOf(minYear) + getString(R.string.first_year);
            year = oldYear;
            month = oldMonth;
        }else if(year>current_year){
            msg = String.valueOf(current_year) + getString(R.string.last_year);
            year = oldYear;
            month = oldMonth;
        }else {
            if(month==1){
                prev_Month = 12;
                prev_Year = year - 1;
            }else{
                prev_Month = month - 1;
                prev_Year = year;
            }
            viewData();
        }
        if(!"".equals(msg)){
            LayoutInflater inflater = getLayoutInflater();
            View toastDesign = inflater.inflate(R.layout.toast_design,
                    (ViewGroup) v.findViewById(R.id.toast_design_root));
            Alert.showToast(this.getContext(), toastDesign, msg);
        }
    }

    private void yearNavi(){
        View v = getView();

        int oldYear = year;
        if(buttonFlag== PREVIEW_BUTTON) {
            year = year - 1;
        }else if(buttonFlag== NEXT_BUTTON){
            year = year + 1;
        }

        String msg = "";
        if(year>current_year) {
            year = current_year;
            msg = String.valueOf(current_year) + getString(R.string.last_year);
            LayoutInflater inflater = getLayoutInflater();
            View toastDesign = inflater.inflate(R.layout.toast_design,
                    (ViewGroup) v.findViewById(R.id.toast_design_root));
            Alert.showToast(this.getContext(), toastDesign, msg);
        }else if(year<minYear){
            msg = String.valueOf(minYear) + getString(R.string.first_year);
            year = year + 1;
        }else {
            prev_Year = year - 1;
            viewData();
        }
        if(!"".equals(msg)){
            LayoutInflater inflater = getLayoutInflater();
            View toastDesign = inflater.inflate(R.layout.toast_design,
                    (ViewGroup) v.findViewById(R.id.toast_design_root));
            Alert.showToast(this.getContext(), toastDesign, msg);
        }
    }

    private void viewAmountLineChart(){
        View v = getView();
        ArrayList<Integer> data = new ArrayList<Integer>();
        ArrayList<Entry> values = new ArrayList<Entry>();
        float maxValue = -Float.MAX_VALUE;
        float minValue = Float.MAX_VALUE;
        String xLabel = "";

        data.clear();

        TextView chartTitle = (TextView)v.findViewById(R.id.lblLineChart);
        try{
            if("month".equals(mViewMode)) {
                chartTitle.setText(getString(R.string.linechart));
                data = DBUtils.getMonthlyData(getContext(), year, month, lastDay);
                xLabel = "일";
                for (int i = 0; i < lastDay; i++) {
                    float value = (float)data.get(i);
                    maxValue = maxValue>value ? maxValue : value;
                    minValue = minValue<value ? minValue : value;

                    values.add(new Entry(i, value, getResources().getDrawable(R.drawable.star)));
                }
            }else{
                chartTitle.setText(getString(R.string.linechart_month));
                data = DBUtils.getYearlyData(getContext(), year);
                xLabel = getString(R.string.month);
                for (int i = 0; i < 12; i++) {
                    int value = data.get(i);
                    maxValue = maxValue>value ? maxValue : value;
                    minValue = minValue<value ? minValue : value;
                    values.add(new Entry(i, value, getResources().getDrawable(R.drawable.star)));
                }
            }

            mLineChart = (LineChart) v.findViewById(R.id.linechart);
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
            PBMarkerView mv = new PBMarkerView(getContext(), R.layout.custom_marker_view);
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
                //if(lastDay!=31) {
                //    xAxis.setLabelCount(16);
                //}
            }else{
                xAxis.setValueFormatter(new CustomXAxisValueFormatter(getString(R.string.month)));
                xAxis.setLabelCount(11);
            }
            xAxis.setGridColor(Color.GRAY);
            xAxis.setTextColor(Color.BLUE);
            xAxis.setAxisLineColor(Color.GRAY);

            //xAxis.addLimitLine(llXAxis); // add x-axis limit line

            LimitLine upper = new LimitLine(20000f, getString(R.string.amount_limit));
            upper.setLineWidth(2f);
            upper.enableDashedLine(4f, 4f, 0f);
            upper.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            upper.setTextSize(10f);
            upper.setTypeface(SmsUtils.typefaceNaumGothicCoding);

            YAxis leftAxis = mLineChart.getAxisLeft();
            leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
/******************************************************************************************
            if("month".equals(mViewMode)) {
                leftAxis.addLimitLine(upper);
            }
 ******************************************************************************************/
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
                    Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_red);
                    dataset.setFillDrawable(drawable);
                }
                else {
                    dataset.setFillColor(Color.WHITE);
                }
*/
                dataset.setFillColor(Color.WHITE);

                //dataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
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

    private void viewCardPieChart(){
        try {
            View v = getView();

            String title = "";
            String sql = "";
            if ("month".equals(mViewMode)) {
                sql = "select year,month, card, sum(amount) as summ from smstable where year=" + String.valueOf(year) + " and month=" + String.valueOf(month)
                        + " group by card,month order by card";

                title =  "● " + String.valueOf(month) + getString(R.string.consumption_by_card_month);
            } else {
                sql = "select year, card, sum(amount) as summ from smstable where year=" + String.valueOf(year) + " group by card,year order by card";
                title = "● " + String.valueOf(year) + getString(R.string.consumption_by_card_year);
            }

            TextView textView = (TextView)v.findViewById(R.id.lblPieChart);
            textView.setText(title);

            final PieChart mChart = (PieChart) v.findViewById(R.id.piechart);
            mChart.clear();

            HashMap<String, Integer> map = DBUtils.getCardTotalAmount(getContext(), sql);
            if(map.size()==0){
                return;
            }

            mChart.setUsePercentValues(true);
            mChart.getDescription().setEnabled(false);
            mChart.setExtraOffsets(5, 10, 5, 5);

            mChart.setDragDecelerationFrictionCoef(0.95f);

            mChart.setCenterTextTypeface(SmsUtils.typefaceNaumGothicCoding);
            //mChart.setCenterText(generateCenterSpannableText("항목 선택\n     \n     "));

            mChart.setTransparentCircleColor(Color.WHITE);
            mChart.setTransparentCircleAlpha(110);

            mChart.setHoleRadius(58f);
            mChart.setTransparentCircleRadius(61f);

            mChart.setDrawCenterText(true);

            mChart.setRotationAngle(0);
            // enable rotation of the chart by touch
            mChart.setRotationEnabled(true);
            mChart.setHighlightPerTapEnabled(true);

            // add a selection listener
            mChart.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    if(e==null){
                        return;
                    }
                    PieEntry pieEntry = (PieEntry)e;
                    float value = pieEntry.getValue();
                    String cardname = pieEntry.getLabel();
                    float percent = 100f*value / cardTotalAmount;
                    mChart.setCenterText(generateCenterSpannableText(cardname + "\n" + StringUtils.format(value, "##,###") + getString(R.string.won) + "\n"
                            + StringUtils.format(percent, "##,###.0") + "%"));
                }

                @Override
                public void onNothingSelected() {

                }
            });

            ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

            Iterator<String> it = map.keySet().iterator();
            String defcname = "";
            float defValue = -Float.MAX_VALUE;
            while(it.hasNext()){
                String cname = it.next();
                float value = (float)map.get(cname);
                if(value>0f) {
                    if (defValue < value) {
                        defValue = value;
                        defcname = cname;
                    }

                    CardEnum cardName = CardEnum.getByName(cname);

                    int resId = -1;
                    if (cardName.equals(CardEnum.KB_CARD)) {
                        resId = R.drawable.ic_kbcard_48;
                    }else if(cardName.equals(CardEnum.KB_CHECKCARD)){
                        resId = R.drawable.ic_kbcheck_48;
                    } else if (cardName.equals(CardEnum.SHINHAN_CARD) || cardName.equals(CardEnum.SHINHAN_CHECKCARD) || cardName.equals(CardEnum.SHINHAN_CORPCARD)) {
                        resId = R.drawable.ic_shinhan_48;
                    } else if (cardName.equals(CardEnum.NH_CARD) || cardName.equals(CardEnum.NH_CHECKCARD) || cardName.equals(CardEnum.NH_CORPCARD)
                            || cardName.equals(CardEnum.NH_WELFARECARD)) {
                        resId = R.drawable.ic_nhcard_48;
                    }else if(cardName.equals(CardEnum.SAMSUNG_CARD)){
                        resId = R.drawable.ic_samsung_48;
                    }else if(cardName.equals(CardEnum.HYUNDAE_CARD)){
                        resId = R.drawable.ic_hyundai_48;
                    } else {
                        resId = R.drawable.ic_unknown_48;
                    }

                    cardTotalAmount += value;

                    entries.add(new PieEntry(value,
                            cname,
                            getResources().getDrawable(resId)));
                }
            }

            PieDataSet dataSet = new PieDataSet(entries, getString(R.string.consumption_by_card));
            dataSet.setDrawIcons(false);

            dataSet.setSliceSpace(3f);
            dataSet.setIconsOffset(new MPPointF(0, 40));
            dataSet.setSelectionShift(10f);

            dataSet.setHighlightEnabled(true); // allow highlighting for DataSet
            // set this to false to disable the drawing of highlight indicator (lines)
            //dataSet.setDrawHighlightIndicators(true);
            //dataSet.setHighlightColor(Color.BLACK); // color for highlight indicator

            // add a lot of colors
            ArrayList<Integer> colors = new ArrayList<Integer>();

            for (int c : ColorTemplate.VORDIPLOM_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.JOYFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.LIBERTY_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);

            colors.add(ColorTemplate.getHoloBlue());

            dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            dataSet.setColors(colors);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(12f);
            data.setValueTextColor(Color.BLACK);
            data.setValueTypeface(SmsUtils.typefaceNaumGothicCoding);

            mChart.setData(data);

            // undo all highlights
            mChart.highlightValues(null);

            mChart.invalidate();

            mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

            Legend l = mChart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(false);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);

            // entry label styling
            mChart.setEntryLabelColor(Color.WHITE);
            mChart.setEntryLabelTypeface(SmsUtils.typefaceNaumGothic);
            mChart.setEntryLabelColor(Color.BLACK);
            mChart.setEntryLabelTextSize(10f);

            float percent = 100f*defValue / cardTotalAmount;
            mChart.setCenterText(generateCenterSpannableText(defcname + "\n" + StringUtils.format(defValue, "##,###") + getString(R.string.won) + "\n"
                    + StringUtils.format(percent, "##,###.0") + "%"));
        }catch(Exception e){
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
/*
    private void viewCategoryRadarChart(){
        View v = getView();
        RadarChart mChart = (RadarChart) v.findViewById(R.id.radarchart);
        if(mChart!=null){
            mChart.clear();
        }
        try{
            //mChart.setBackgroundColor(Color.WHITE);
            mChart.getDescription().setEnabled(false);
            mChart.setWebLineWidth(1f);
            mChart.setWebColor(Color.LTGRAY);
            mChart.setWebLineWidthInner(1f);
            mChart.setWebColorInner(Color.LTGRAY);
            mChart.setWebAlpha(100);

            // create a custom MarkerView (extend MarkerView) and specify the layout
            // to use for it
            MarkerView mv = new RadarMarkerView(getContext(), R.layout.radar_markerview);
            mv.setChartView(mChart); // For bounds control
            mChart.setMarker(mv); // Set the marker to the chart

            String title1 = "";
            String title2 = "";
            String sql1 = "";
            String sql2 = "";
            String sql3 = "";
            if ("month".equals(mViewMode)) {
                sql1 = "select year,month, category, sum(amount) as summ from smstable where "
                + "year=" + String.valueOf(year) + " and month=" + String.valueOf(month) + " group by category,month order by category,month";

                sql2 = "select year,month, category, sum(amount) as summ from smstable where "
                        + "year=" + String.valueOf(prev_Year) + " and month=" + String.valueOf(prev_Month) + " group by category,month order by category,month";

                sql3 = "select sum(amount) as summ from smstable where year=" + String.valueOf(prev_Year) + " and month=" + String.valueOf(prev_Month) + " order by month";

                title1 = String.valueOf(year) + "." + String.valueOf(month);
                title2 = String.valueOf(prev_Year) + "." + String.valueOf(prev_Month);
            }else{
                sql1 = "select year,category, sum(amount) as summ from smstable where "
                        + "year=" + String.valueOf(year) + " group by category,year order by category,year";

                sql2 = "select year,category, sum(amount) as summ from smstable where "
                        + "year=" + String.valueOf(prev_Year) + " group by category,year order by category,year";

                sql3 = "select sum(amount) as summ from smstable where year=" + String.valueOf(prev_Year) + " order by month";

                title1 = String.valueOf(year);
                title2 = String.valueOf(prev_Year);
            }

            TextView textView = (TextView)v.findViewById(R.id.lblRadarChart);
            textView.setTypeface(SmsUtils.typefaceNaumGothic);
            textView.setText(R.string.radarchart);

            HashMap<String, Integer> catMap1 = DBUtils.getCategories(getContext(), sql1, true);
            HashMap<String, Integer> catMap2 = DBUtils.getCategories(getContext(), sql2, true);
            int prevGrandTotalAmount =  DBUtils.getTotalAmount(getContext(), sql3);

             final ArrayList<String> catName = new ArrayList<>();
            ArrayList<RadarEntry> entries1 = new ArrayList<RadarEntry>();
            ArrayList<RadarEntry> entries2 = new ArrayList<RadarEntry>();
            Iterator<String> it = catMap1.keySet().iterator();
            float maxValue = - Float.MAX_VALUE;
            while(it.hasNext()){
                String key = it.next();
                float value1 = (float)catMap1.get(key);
                value1 = 100f*value1/grandTotalAmount;
                maxValue = maxValue>value1 ? maxValue : value1;
                catName.add(key);
                entries1.add(new RadarEntry(value1));
            }

            it = catMap2.keySet().iterator();
            while(it.hasNext()){
                String key = it.next();
                float value1 = (float)catMap2.get(key);
                value1 = 100f*value1/prevGrandTotalAmount;
                maxValue = maxValue>value1 ? maxValue : value1;
                catName.add(key);
                entries2.add(new RadarEntry(value1));
            }

            ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();

            RadarDataSet set1 = new RadarDataSet(entries2, title2);
            //set1.setColor(Color.rgb(247, 190, 129));
            //set1.setFillColor(Color.rgb(245, 218, 129));

            set1.setColor(Color.RED);
            set1.setDrawFilled(true);
            set1.setFillAlpha(180);
            set1.setLineWidth(1f);
            set1.setDrawHighlightCircleEnabled(true);
            set1.setDrawHighlightIndicators(true);
            sets.add(set1);

            RadarDataSet set2 = new RadarDataSet(entries1, title1);
            //set2.setColor(Color.rgb(172, 250, 88));
            //set2.setFillColor(Color.rgb(190, 247, 129));

            set2.setColor(Color.CYAN);
            set2.setDrawFilled(true);
            set2.setFillAlpha(180);
            set2.setLineWidth(1f);
            set2.setDrawHighlightCircleEnabled(true);
            set2.setDrawHighlightIndicators(true);
            sets.add(set2);

            RadarData data = new RadarData(sets);
            data.setValueTypeface(SmsUtils.typefaceNaumGothicCoding);
            data.setValueTextSize(8f);
            data.setDrawValues(false);
            data.setValueTextColor(Color.BLUE);

            mChart.setData(data);
            mChart.invalidate();

            mChart.animateXY(
                    1400, 1400,
                    Easing.EasingOption.EaseInOutQuad,
                    Easing.EasingOption.EaseInOutQuad);

            XAxis xAxis = mChart.getXAxis();
            xAxis.setTypeface(SmsUtils.typefaceNaumGothicCoding);
            xAxis.setTextSize(9f);
            xAxis.setYOffset(0f);
            xAxis.setXOffset(0f);
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                private String[] mActivities = new String[]{"미분류", "식사", "문화", "의료", "통신", "교통/차량", "공과금", "생활"};

                //private String[] mActivities = new String[catName.size()];

                @Override
                public String getFormattedValue(float value, AxisBase axis) {

//                    for(int i=0; i<catName.size(); i++){
//                        mActivities[i] = catName.get(i);
//                    }

                    return mActivities[(int) value % mActivities.length];
                }
            });

            xAxis.setTextColor(Color.BLUE);

            YAxis yAxis = mChart.getYAxis();
            yAxis.setTypeface(SmsUtils.typefaceNaumGothicCoding);
            yAxis.setLabelCount(5, false);
            yAxis.setTextSize(12f);
            yAxis.setAxisMinimum(0f);
            //yAxis.setAxisMaximum(maxValue);
            yAxis.setAxisMaximum(40f);
            yAxis.setDrawLabels(true);

            Legend l = mChart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDrawInside(false);
            l.setTypeface(SmsUtils.typefaceNaumGothicCoding);
            l.setTextSize(12f);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(5f);
            l.setTextColor(Color.BLUE);
        }catch (Exception e){
            Log.e(TAG, StackTrace.getStackTrace(e));
        }
    }
*/
    private void viewCategoryHalfPieChart(){
        View v = getView();
        final PieChart mChart = (PieChart) v.findViewById(R.id.halfpiechart);
        if(mChart!=null){
            mChart.clear();
        }

        try{
            mChart.setUsePercentValues(true);
            mChart.getDescription().setEnabled(false);
            mChart.setCenterTextTypeface(SmsUtils.typefaceNaumGothicCoding);
            /////////////////mChart.setCenterText(generateCenterSpannableText());

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

            String sql = "";
            String title = "";
            if ("month".equals(mViewMode)) {
                sql = "select year,month, category, sum(amount) as summ from smstable where "
                        + "year=" + String.valueOf(year) + " and month=" + String.valueOf(month) + " group by category,month order by category,month";

                title = "● " + String.valueOf(year) + "." + String.valueOf(month) + " " + getString(R.string.consumption_by_category);
            }else{
                sql = "select year,category, sum(amount) as summ from smstable where "
                        + "year=" + String.valueOf(year) + " group by category,year order by category,year";

                title = "● " + String.valueOf(year) + " " + getString(R.string.consumption_by_category);
            }

            TextView textView = (TextView)v.findViewById(R.id.lblHalfPieChart);
            textView.setTypeface(SmsUtils.typefaceNaumGothic);
            textView.setText(title);
            HashMap<String, Integer> catMap = DBUtils.getCategories(getContext(), sql, false);

            if(catMap.size()==0){
                return;
            }
            ArrayList<PieEntry> values = new ArrayList<PieEntry>();
            Iterator<String> it = catMap.keySet().iterator();
            String catname = "";
            float totalValue = 0f;
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

            PieDataSet dataSet = new PieDataSet(values, getString(R.string.consumption_by_category_1));
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
                    String cardname = pieEntry.getLabel();
                    float percent = 100f*value / cardTotalAmount;
                    mChart.setCenterText(generateCenterSpannableText(cardname + "\n" + StringUtils.format(value, "##,###") + getString(R.string.won) + "\n"
                            + StringUtils.format(percent, "##,###.0") + "%"));
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
}
