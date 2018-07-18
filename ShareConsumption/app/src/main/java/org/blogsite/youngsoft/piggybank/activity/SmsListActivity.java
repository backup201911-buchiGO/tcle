package org.blogsite.youngsoft.piggybank.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.adapter.CardListData;
import org.blogsite.youngsoft.piggybank.adapter.CardListViewAdapter;
import org.blogsite.youngsoft.piggybank.analyzer.Data;
import org.blogsite.youngsoft.piggybank.analyzer.Day;
import org.blogsite.youngsoft.piggybank.data.CardData;
import org.blogsite.youngsoft.piggybank.data.CardProducer;
import org.blogsite.youngsoft.piggybank.data.CardTableDataAdapter;
import org.blogsite.youngsoft.piggybank.data.CardTableView;
import org.blogsite.youngsoft.piggybank.db.DBUtils;
import org.blogsite.youngsoft.piggybank.dialog.ConfirmDialog;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.utils.Alert;
import org.blogsite.youngsoft.piggybank.utils.ButtonClick;
import org.blogsite.youngsoft.piggybank.utils.CardEnum;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;
import org.blogsite.youngsoft.tableview.listeners.TableDataClickListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SmsListActivity extends AppCompatActivity {
    private static final String TAG = "SmsListActivity";

    private Context context;

    private LineChart mLineChart;

    private CardTableView carTableView;
    private TextView noData;
    private Calendar cal;
    private int minYear = -1;
    private int current_year;
    private int current_month;

    private Day day;

    private int year;
    private int month;
    private final int startDay = 1;
    private int lastDay;
    private long startTime;
    private long lastTime;

    private final int PREVIEW_BUTTON = 0;
    private final int NEXT_BUTTON = 1;
    private int buttonFlag = -1;
    private ImageButton prevButton;
    private ImageButton nextButton;

    private CardTableDataAdapter cardTableDataAdapter;

    private ConfirmDialog confirmDialog;
    private boolean isBodyShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_smslist);
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
            carTableView = (CardTableView) findViewById(R.id.month_tableView);
            carTableView.setHeaderBackground(R.drawable.table_header);
            noData = (TextView)findViewById(R.id.nodata);
            cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;

            current_year = year;
            current_month = month;

            lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            startTime = new GregorianCalendar(year, month, startDay).getTimeInMillis();
            lastTime = new GregorianCalendar(year, month, lastDay).getTimeInMillis();

            prevButton = (ImageButton) findViewById(R.id.month_BtnPrevious);
            prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonFlag = PREVIEW_BUTTON;
                    monthButtonClick();
                }
            });

            nextButton = (ImageButton) findViewById(R.id.month_BtnNext);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonFlag = NEXT_BUTTON;
                    monthButtonClick();
                }
            });

            minYear = DBUtils.getMinYear(context);

            cardTableDataAdapter = new CardTableDataAdapter(context, new ArrayList<CardData>(), carTableView);
            viewData();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }

    private void viewData(){
        day = new Day();
        try{
            if (carTableView != null) {
                cardTableDataAdapter.setTypeface(SmsUtils.getInstance(context).typefaceIconFont);
                carTableView.setDataAdapter(cardTableDataAdapter);

                ScrollView scrollView = (ScrollView)findViewById(R.id.my_scroll);

                carTableView.addDataClickListener(new CardClickListener());
                //carTableView.addDataLongClickListener(new CardLongClickListener());
                //carTableView.setSwipeToRefreshEnabled(true);

                // sql = SELECT data FROM smstable where year=2018 and month=2 order by timestamp desc
                String sql = "select data from smstable where year=" + String.valueOf(year) + " and month=" + String.valueOf(month) + " order by timestamp desc";

                cardTableDataAdapter.clear();
                List<Data> dataList = DBUtils.getData(context, sql);
                if(dataList.size()>0) {
                    noData.setVisibility(View.GONE);
                    carTableView.setVisibility(View.VISIBLE);
                    for (Data data : dataList) {
                        day.addData(data);

                        CardProducer procedure = new CardProducer(data.getCard());
                        CardData carddata = new CardData(procedure, data.getCategory(), data.getAmount(), data.getTimestamp(), data.isApproval(), data.getBody());
                        cardTableDataAdapter.getData().add(carddata);
                    }

                    cardTableDataAdapter.notifyDataSetChanged();
                }else{
                    noData.setVisibility(View.VISIBLE);
                    carTableView.setVisibility(View.GONE);
                }

                statDisplay();
                cardRecycleViewSetting();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void statDisplay(){
        TextView title = (TextView)findViewById(R.id.month_LblTitle);
        TextView termTextView = (TextView)findViewById(R.id.month_LblTerm);

        String sql = "select sum(amount) as summ from smstable where year=" + String.valueOf(year) + " and month=" + String.valueOf(month) + " order by month";

        int amount = DBUtils.getTotalAmount(context, sql);

        title.setText(month + " " + getString(R.string.consumption_by_month));
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        String smonth = String.valueOf(month);
        smonth = month<10 ? "0" + smonth : smonth;
        String s = year + "." + smonth + ".01" + " ~ " + year + "." + smonth + "." + lastDay;
        termTextView.setText(s);

        cal.set(Calendar.MONTH, month-1);
        lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        TextView grandTotalText = (TextView)findViewById(R.id.month_LblGrandTotal);
        grandTotalText.setTypeface(SmsUtils.typefaceNaumGothicCoding);
        grandTotalText.setText(StringUtils.format(amount, "#,### " + getString(R.string.won) + " "));
    }

    public void cardRecycleViewSetting(){
        ListView mListView = (ListView)findViewById(R.id.month_cardlist);
        TextView nocard = (TextView) findViewById(R.id.nocard);
        CardListViewAdapter mAdapter = new CardListViewAdapter(context);
        mListView.setOverScrollMode(0);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mItemClickListener);

        String sql = "select year,month, card, sum(amount) as summ from smstable where year=" + String.valueOf(year) + " and month=" + String.valueOf(month)
                    + " group by card,month order by card";
        HashMap<String, Integer> map = DBUtils.getCardTotalAmount(context, sql);
        if(map.size()>0) {
            nocard.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            Iterator<String> keys = map.keySet().iterator();
            int[] catSum = new int[8];
            for (int i = 0; i < 8; i++) {
                catSum[i] = 0;
            }

            while (keys.hasNext()) {
                String key = keys.next();
                int amount = map.get(key);

                CardEnum cardName = CardEnum.getByName(key);

                String s = amount + " " + getString(R.string.won);
                int resId = -1;
                if (cardName.equals(CardEnum.KB_CARD)) {
                    resId = R.drawable.ic_kbcard;
                } else if (cardName.equals(CardEnum.KB_CHECKCARD)) {
                    resId = R.drawable.ic_kbcheck;
                } else if (cardName.equals(CardEnum.SHINHAN_CARD) || cardName.equals(CardEnum.SHINHAN_CHECKCARD) || cardName.equals(CardEnum.SHINHAN_CORPCARD)) {
                    resId = R.drawable.ic_shinhan;
                } else if (cardName.equals(CardEnum.NH_CARD) || cardName.equals(CardEnum.NH_CHECKCARD) || cardName.equals(CardEnum.NH_CORPCARD)
                        || cardName.equals(CardEnum.NH_WELFARECARD)) {
                    resId = R.drawable.ic_nhcard;
                } else if (cardName.equals(CardEnum.SAMSUNG_CARD)) {
                    resId = R.drawable.ic_samsung;
                } else if (cardName.equals(CardEnum.HYUNDAE_CARD)) {
                    resId = R.drawable.ic_hyundai;
                } else {
                    resId = R.drawable.ic_unknown;
                }

                mAdapter.addItem(resId, cardName.getName(), s);

            }
            SmsUtils.setListViewHeightBasedOnChildren(context, mListView);
        }else{
            nocard.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
            // parent는 AdapterView의 속성의 모두 사용 할 수 있다.
            CardListData cardData = (CardListData)parent.getAdapter().getItem(position);
            //Toast.makeText(getContext(), cardData.cardName, Toast.LENGTH_SHORT).show();

            // view는 클릭한 Row의 view를 Object로 반환해 준다.
            //TextView tv_view = (TextView)view.findViewById(R.id.tv_row_title);
            //tv_view.setText("바꿈");

            // Position 은 클릭한 Row의 position 을 반환해 준다.
            //Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
            // l_Position 은 클릭한 Row의 long type의 position 을 반환해 준다.
            //Toast.makeText(getContext(), "l = " + l_position, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, CardDetailActivity.class);
            intent.putExtra("cardname", cardData.cardName);
            intent.putExtra("year", year);
            intent.putExtra("month", month);
            intent.putExtra("ViewMode", "month");
            startActivity(intent);
        }
    };

    private void monthButtonClick(){
        monthNavi();
    }

    private void monthNavi(){
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
            msg = String.valueOf(current_month) + getString(R.string.last_month_1 ) + String.valueOf(current_year) + getString(R.string.last_month_2);
        }else if(year<minYear) {
            msg = String.valueOf(minYear) + getString(R.string.first_year);
            year = oldYear;
            month = oldMonth;
        }else if(year>current_year){
            msg = String.valueOf(current_year) + getString(R.string.last_year);
            year = oldYear;
            month = oldMonth;
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

    private class CardClickListener implements TableDataClickListener<CardData> {

        @Override
        public void onDataClicked(final int rowIndex, final CardData clickedData) {
            if (!isBodyShown) {
                isBodyShown = true;
                confirmDialog = new ConfirmDialog(context, getString(R.string.sms_msg), clickedData.getBody(),
                        new ButtonClick(context) {
                            @Override
                            public void onYesNoClick(final boolean yes) {
                                if (yes) {
                                    isBodyShown = false;
                                    confirmDialog.dismiss();
                                }
                            }
                        });
                confirmDialog.show();
            }
        }
    }
}
