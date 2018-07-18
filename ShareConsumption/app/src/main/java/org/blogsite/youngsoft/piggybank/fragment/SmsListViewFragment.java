package org.blogsite.youngsoft.piggybank.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import org.blogsite.youngsoft.piggybank.activity.CardDetailActivity;
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
import org.blogsite.youngsoft.piggybank.utils.BackPressCloseHandler;
import org.blogsite.youngsoft.piggybank.utils.ButtonClick;
import org.blogsite.youngsoft.piggybank.utils.CardEnum;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.tableview.listeners.TableDataClickListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SmsListViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SmsListViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SmsListViewFragment extends Fragment {
    private static final String TAG = "SmsListViewFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mViewMode;
    private String mRefresh;
    private String mParam21;


    private OnFragmentInteractionListener mListener;

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

    private BackPressCloseHandler backPressCloseHandler;
    private CardTableDataAdapter cardTableDataAdapter;

    private ConfirmDialog confirmDialog;
    private boolean isBodyShown = false;

    public SmsListViewFragment() {
        //
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SmsListViewFragment newInstance(String param1, String param2) {
        SmsListViewFragment fragment = new SmsListViewFragment();
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
            mRefresh = getArguments().getString(ARG_PARAM2);
            //System.out.println(">>>>>>>>>>>>>>>>>>>> mViewMode = " + mViewMode);
            //System.out.println(">>>>>>>>>>>>>>>>>>>> mParam2 = " + mParam2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_smslist, container, false);
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
            carTableView = (CardTableView) v.findViewById(R.id.month_tableView);
            carTableView.setHeaderBackground(R.drawable.table_header);
            noData = (TextView)v.findViewById(R.id.nodata);

            backPressCloseHandler = new BackPressCloseHandler(this.getActivity());

            cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;

            current_year = year;
            current_month = month;

            lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            startTime = new GregorianCalendar(year, month, startDay).getTimeInMillis();
            lastTime = new GregorianCalendar(year, month, lastDay).getTimeInMillis();

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

            cardTableDataAdapter = new CardTableDataAdapter(this.getContext(), new ArrayList<CardData>(), carTableView);
            viewData();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }

    private void viewData(){
        day = new Day();
        try{
            if (carTableView != null) {
                cardTableDataAdapter.setTypeface(SmsUtils.getInstance(this.getContext()).typefaceIconFont);
                carTableView.setDataAdapter(cardTableDataAdapter);

                ScrollView scrollView = (ScrollView)getView().findViewById(R.id.my_scroll);

                carTableView.addDataClickListener(new CardClickListener());
                //carTableView.addDataLongClickListener(new CardLongClickListener());
                //carTableView.setSwipeToRefreshEnabled(true);

                // sql = SELECT data FROM smstable where year=2018 and month=2 order by timestamp desc
                String sql = "";
                if("month".equals(mViewMode)) {
                    sql = "select data from smstable where year=" + String.valueOf(year) + " and month=" + String.valueOf(month) + " order by timestamp desc";
                }else {
                    sql = "select data from smstable where year=" + String.valueOf(year) + " order by timestamp desc";
                }


                cardTableDataAdapter.clear();
                List<Data> dataList = DBUtils.getData(this.getContext(), sql);
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
        View v = getView();
        TextView title = (TextView)v.findViewById(R.id.month_LblTitle);
        TextView termTextView = (TextView)v.findViewById(R.id.month_LblTerm);

        cal.set(Calendar.MONTH, month-1);
        lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        String sql = "";
        if("month".equals(mViewMode)) {
            sql = "select sum(amount) as summ from smstable where year=" + String.valueOf(year) + " and month=" + String.valueOf(month) + " order by month";
        }else {
            sql = "select sum(amount) as summ from smstable where year=" + String.valueOf(year) + " order by month";
        }
        int amount = DBUtils.getTotalAmount(getContext(), sql);

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

        TextView grandTotalText = (TextView)v.findViewById(R.id.month_LblGrandTotal);
        grandTotalText.setTypeface(SmsUtils.typefaceNaumGothicCoding);
        grandTotalText.setText(StringUtils.format(amount, "#,### " + getString(R.string.won) + " "));
    }

    public void cardRecycleViewSetting(){
        View v = getView();
        Context context = this.getContext();
        ListView mListView = (ListView) v.findViewById(R.id.month_cardlist);
        TextView nocard = (TextView) v.findViewById(R.id.nocard);
        CardListViewAdapter mAdapter = new CardListViewAdapter(context);
        mListView.setOverScrollMode(v.OVER_SCROLL_ALWAYS);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mItemClickListener);

        String sql = "";
        if("month".equals(mViewMode)) {
            sql = "select year,month, card, sum(amount) as summ from smstable where year=" + String.valueOf(year) + " and month=" + String.valueOf(month)
                    + " group by card,month order by card";
        }else {
            sql = "select year, card, sum(amount) as summ from smstable where year=" + String.valueOf(year) + " group by card,year order by card";
        }
        HashMap<String, Integer> map = DBUtils.getCardTotalAmount(getContext(), sql);

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
            //SmsUtils.setListViewHeightBasedOnChildren(context, mListView);
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
            Intent intent = new Intent(getContext(), CardDetailActivity.class);
            intent.putExtra("cardname", cardData.cardName);
            intent.putExtra("year", year);
            intent.putExtra("month", month);
            intent.putExtra("ViewMode", mViewMode);
            startActivity(intent);
        }
    };

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
            viewData();
        }
        if(!"".equals(msg)){
            LayoutInflater inflater = getLayoutInflater();
            View toastDesign = inflater.inflate(R.layout.toast_design,
                    (ViewGroup) v.findViewById(R.id.toast_design_root));
            Alert.showToast(this.getContext(), toastDesign, msg);
        }
    }

    private class CardClickListener implements TableDataClickListener<CardData> {

        @Override
        public void onDataClicked(final int rowIndex, final CardData clickedData) {
            if (!isBodyShown) {
                isBodyShown = true;
                confirmDialog = new ConfirmDialog(getContext(), getString(R.string.sms_msg), clickedData.getBody(),
                        new ButtonClick(getContext()) {
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
