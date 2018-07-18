package org.blogsite.youngsoft.piggybank.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.activity.auth.SettingsActivity;
import org.blogsite.youngsoft.piggybank.adapter.DonationInfoListData;
import org.blogsite.youngsoft.piggybank.adapter.DonationInfoListViewAdapter;
import org.blogsite.youngsoft.piggybank.db.DBUtils;
import org.blogsite.youngsoft.piggybank.dialog.ConfirmUtils;
import org.blogsite.youngsoft.piggybank.dialog.EditUtils;
import org.blogsite.youngsoft.piggybank.dialog.SettingsEditUtils;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.setting.DonationInfo;
import org.blogsite.youngsoft.piggybank.setting.PBSettings;
import org.blogsite.youngsoft.piggybank.setting.PBSettingsUtils;
import org.blogsite.youngsoft.piggybank.utils.BackPressHandler;
import org.blogsite.youngsoft.piggybank.utils.ButtonClick;
import org.blogsite.youngsoft.piggybank.utils.IButtonClickListner;
import org.blogsite.youngsoft.piggybank.utils.KeyboardUtils;
import org.blogsite.youngsoft.piggybank.utils.ObjLoader;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;
import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;

public class DonationInfoListActivity  extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DonationListActivity";

    private static final NumberFormat PRICE_FORMATTER = NumberFormat.getNumberInstance();
    private Context context;
    private PBSettings settings = null;

    private ArrayList<DonationInfo> dinfo;
    private BackPressHandler backPressHandler;

    private DonationInfoListData info;
    private ScrollView scrollView;
    private TextView threshold;
    private TextView percent;
    private Switch overall;
    private TextView lbloveralldesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_info_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
        settings = PBSettingsUtils.getInstance().getSettings();
        backPressHandler = new BackPressHandler(this);
        if(settings==null){
            if(DBUtils.checkSettings(context)) {
                try {
                    String str = DBUtils.getSettings(context);
                    settings = (PBSettings) ObjLoader.loadObjectFromBase64(str);
                    PBSettingsUtils.getInstance(settings);
                } catch (Exception e) {
                    PGLog.e(TAG, StackTrace.getStackTrace(e));
                }
            }else{
                Intent intent = new Intent(context, SettingsActivity.class);
                startActivityForResult(intent, 1);
            }
        }

        scrollView = (ScrollView)findViewById(R.id.my_scroll);
        scrollView.setVisibility(View.GONE);

        Button cancelButton = (Button)findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);

        Button applyDonationDetail = (Button)findViewById(R.id.applyDonationDetail);
        applyDonationDetail.setOnClickListener(this);

        Button delDonation = (Button)findViewById(R.id.delDonation);
        delDonation.setOnClickListener(this);

        if(settings!=null){
            dialayDonationInfo();
        }else{
            onBackPressed();
        }
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

    private void dialayDonationInfo(){
        try {
                viewDonationList();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }

    private int getValToInt(TextView text){
        String s = text.getText().toString();
        s = StringUtils.replaceAll(s, ",", "");
        s = StringUtils.replaceAll(s, " 원", "");
        s = StringUtils.replaceAll(s, " %", "");
        s = StringUtils.replaceAll(s, " 일", "");
        s = StringUtils.replaceAll(s, "　", "");
        s = StringUtils.replaceAll(s, ">", "");
        return Integer.parseInt(s);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        info.threshold = getValToInt(threshold);
        info.percent = getValToInt(percent);

        if(id==R.id.cancelButton){
            scrollView.setVisibility(View.GONE);
        }else if(id==R.id.delDonation){
            DonationInfo dinfo = new DonationInfo();
            dinfo.setCard(info.cardName);
            dinfo.setCategory(info.categoryName);
            DBUtils.deleteDonationInfo(context, dinfo);
            viewDonationList();
            scrollView.setVisibility(View.GONE);
        }else if(id==R.id.applyDonationDetail){
            DonationInfo dinfo = new DonationInfo();
            dinfo.setCard(info.cardName);
            dinfo.setCategory(info.categoryName);
            dinfo.setThreshold(info.threshold);
            dinfo.setPercent(info.percent);
            dinfo.setThresholdOverall(info.overall);
            dinfo.setAmount(0);
            DBUtils.insertDonationInfo(context, dinfo);
            viewDonationList();
            scrollView.setVisibility(View.GONE);
        }
    }

    private void viewDonationList(){
        dinfo = DBUtils.getSelectedDonation(context);
        ListView mListView = (ListView)findViewById(R.id.donation_info_list);

        DonationInfoListViewAdapter mAdapter = new DonationInfoListViewAdapter(context);
        mListView.setOverScrollMode(0);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(mItemClickListener);
        for(DonationInfo info : dinfo){
            mAdapter.addItem(info);
        }
        SmsUtils.setListViewHeightBasedOnChildren(context, mListView);
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
            // parent는 AdapterView의 속성의 모두 사용 할 수 있다.
            info = (DonationInfoListData)parent.getAdapter().getItem(position);
            scrollView.setVisibility(View.VISIBLE);
            viewdonationInfo();

        }
    };

    private void viewdonationInfo(){
        ImageView imgcard = (ImageView)findViewById(R.id.imgcard);
        imgcard.setImageResource(info.cardResId);

        TextView lblcard = (TextView)findViewById(R.id.lblcard);
        lblcard.setTypeface(SmsUtils.typefaceNaumGothic);
        lblcard.setText(info.cardName);

        ImageView imgcategory = (ImageView)findViewById(R.id.imgcategory);
        imgcategory.setImageResource(info.categorydResId);

        TextView lblcategory = (TextView)findViewById(R.id.lblcategory);
        lblcategory.setTypeface(SmsUtils.typefaceNaumGothic);
        lblcategory.setText(info.categoryName);

        threshold = (TextView)findViewById(R.id.threshold);
        threshold.setTypeface(SmsUtils.typefaceNaumGothicCoding);
        String v = "<font color=\"#F36164\">" + PRICE_FORMATTER.format(info.threshold) + " 원</font>" + getString(R.string.input_tag);
        threshold.setText(Html.fromHtml(v));
        threshold.setInputType(InputType.TYPE_CLASS_NUMBER);
        threshold.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    threshold.requestFocus();
                    SettingsEditUtils edit = new SettingsEditUtils(context, getString(R.string.threshold_edit_title), threshold );
                    edit.show();
                    KeyboardUtils.hideKeyboard((Activity)context);
                }
                return false;
            }
        });

        percent = (TextView)findViewById(R.id.percent);
        percent.setTypeface(SmsUtils.typefaceNaumGothicCoding);
        v = "<font color=\"#F36164\">" + PRICE_FORMATTER.format(info.percent) + " %</font>" + getString(R.string.input_tag);
        percent.setText(Html.fromHtml(v));
        percent.setInputType(InputType.TYPE_CLASS_NUMBER);
        percent.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    percent.requestFocus();
                    SettingsEditUtils edit = new SettingsEditUtils(context, getString(R.string.percent_edit_title), percent );
                    edit.show();
                    KeyboardUtils.hideKeyboard((Activity)context);
                }
                return false;
            }
        });

        lbloveralldesc = (TextView) findViewById(R.id.lbloveralldesc);
        lbloveralldesc.setTypeface(SmsUtils.typefaceNaumGothic);

        overall = (Switch)findViewById(R.id.overall);
        overall.setTypeface(SmsUtils.typefaceNaumGothic);
        overall.setChecked(info.overall);
        overall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lbloveralldesc.setText(R.string.overallon);
                } else {
                    lbloveralldesc.setText(R.string.overalloff);
                }
                info.overall = isChecked;
            }
        });
    }
}
