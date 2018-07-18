package org.blogsite.youngsoft.piggybank.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.activity.auth.SettingsActivity;
import org.blogsite.youngsoft.piggybank.adapter.DonationListData;
import org.blogsite.youngsoft.piggybank.adapter.DonationListViewAdapter;
import org.blogsite.youngsoft.piggybank.db.DBUtils;
import org.blogsite.youngsoft.piggybank.db.DataBases;
import org.blogsite.youngsoft.piggybank.dialog.AuthNumpadDialog;
import org.blogsite.youngsoft.piggybank.dialog.ConfirmUtils;
import org.blogsite.youngsoft.piggybank.dialog.YesNoDialog;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.setting.DonationHistory;
import org.blogsite.youngsoft.piggybank.setting.DonationInfo;
import org.blogsite.youngsoft.piggybank.setting.Donations;
import org.blogsite.youngsoft.piggybank.setting.PBSettings;
import org.blogsite.youngsoft.piggybank.setting.PBSettingsUtils;
import org.blogsite.youngsoft.piggybank.utils.Alert;
import org.blogsite.youngsoft.piggybank.utils.BackPressHandler;
import org.blogsite.youngsoft.piggybank.utils.ButtonClick;
import org.blogsite.youngsoft.piggybank.utils.IButtonClickListner;
import org.blogsite.youngsoft.piggybank.utils.ObjLoader;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static java.security.AccessController.getContext;

public class DonationActivity  extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DonationActivity";

    private static final NumberFormat PRICE_FORMATTER = NumberFormat.getNumberInstance();
    private Context context;
    private PBSettings settings = null;
    private Calendar cal;
    private int year;
    private int month;
    private int lastDay;

    private DonationHistory donationHistory;

    private ArrayList<DonationInfo> dinfo;
    private TextView realTotalAmount;
    private TextView lblAll;
    private TextView www;
    private TextView tel;
    private YesNoDialog confirm = null;
    private BackPressHandler backPressHandler;


    private AuthNumpadDialog authNumpadDialog;
    private IButtonClickListner authListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);
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
                return;
            }
        }
        if(settings!=null){
            if(settings.getDonations()==null){
                Intent intent = new Intent(context, SettingsActivity.class);
                startActivityForResult(intent, 1);
                return;
            }
            dialayDonationInfo();
        }else{
            onBackPressed();
        }

        www = (TextView)findViewById(R.id.donation_www);
        www.setTypeface(SmsUtils.typefaceNaumGothic);
        www.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    if(www.getText().length()!=0){
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.parse(www.getText().toString());
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }
                return false;
            }
        });

        tel = (TextView)findViewById(R.id.donation_tel);
        tel.setTypeface(SmsUtils.typefaceNaumGothicCoding);
        tel.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    if(tel.getText().length()!=0){
                        try {
                            String phone = "tel:" + tel.getText().toString().trim();
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse(phone));
                            startActivity(intent);
                        }catch (Exception e){
                            PGLog.e(TAG, StackTrace.getStackTrace(e));
                        }
                    }
                }
                return false;
            }
        });

        authListner = new ButtonClick(context){
            @Override
            public void onYesNoClick(final boolean yes){
                if(yes){
                    boolean passed = authNumpadDialog.getPassed();
                    if(passed) {
                        authNumpadDialog.dismiss();
                        if(!DBUtils.checkDonationHistory(context, donationHistory)){
                            DBUtils.insertDonationHistory(context, donationHistory);
                        }
                        finish();
                    }else {
                        Alert.showAlert(context, getString(R.string.authnum_error), getString(R.string.authnum_error_msg));
                    }

                }else{
                    authNumpadDialog.dismiss();
                }
            }
        };

        donationHistory = new DonationHistory();
        Donations donations = settings.getDonations();
        String samount = realTotalAmount.getText().toString();
        samount = StringUtils.replaceAll(samount, ",", "");
        samount = StringUtils.replaceAll(samount, getString(R.string.won), "");
        int amount = Integer.parseInt(samount);
        Calendar mcal = Calendar.getInstance();
        int year = mcal.get(Calendar.YEAR);
        int month = mcal.get(Calendar.MONTH) + 1;
        int day = mcal.get(Calendar.DATE);
        String account = donations.getAccount();
        String bankname = donations.getBankname();
        String home = donations.getHome();
        String tel = donations.getTel();
        String address = donations.getAddress();
        String category = donations.getCategory();

        donationHistory.setYear(year);
        donationHistory.setMonth(month);
        donationHistory.setDay(day);
        donationHistory.setAmount(amount);
        donationHistory.setDonationName(donations.getName());
        donationHistory.setAmount(amount);
        donationHistory.setBankname(bankname);
        donationHistory.setHome(home);
        donationHistory.setTel(tel);
        donationHistory.setAddress(address);
        donationHistory.setCategory(category);

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
            cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;
            lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            TextView donationinfo = (TextView) findViewById(R.id.lbldonationinfo);
            donationinfo.setTypeface(SmsUtils.typefaceNaumGothic);
            donationinfo.setText("● " + String.valueOf(year) + getString(R.string.year) + " " + String.valueOf(month) + getString(R.string.donation_info));

            lblAll = (TextView)findViewById(R.id.lblAll);
            lblAll.setTypeface(SmsUtils.typefaceNaumGothic);

            TextView totalAmount = (TextView) findViewById(R.id.totalAmount);
            totalAmount.setTypeface(SmsUtils.typefaceNaumGothicCoding);
            realTotalAmount = (TextView) findViewById(R.id.realTotalAmount);
            realTotalAmount.setTypeface(SmsUtils.typefaceNaumGothicCoding);

            dinfo = DBUtils.getSelectedDonation(context);
            if(dinfo.size()>0){

                lblAll.setText(getString(R.string.donation_card_category));
                calcDonationByCategory();
                int len = dinfo.size();
                int totalAmountValue = 0;
                int realAmountValue = 0;
                for(int i=0; i<len; i++){
                    DonationInfo info = dinfo.get(i);
                    int percent = info.getPercent();
                    totalAmountValue += info.getAmount();
                    realAmountValue += info.getAmount() * percent / 100;
                }
                totalAmount.setText(PRICE_FORMATTER.format(totalAmountValue) + getString(R.string.won));
                realTotalAmount.setText(PRICE_FORMATTER.format(realAmountValue) + getString(R.string.won));
                viewDonationList();
            }else{
                lblAll.setText(getString(R.string.lblAll));

                boolean val = settings.isThresholdOverall();
                String sql = "";
                if (val) {
                    sql = "select sum(amount) as summ from smstable where year=" + String.valueOf(year) +
                            " and month=" + String.valueOf(month) + " and amount>=" + String.valueOf(settings.getThreshold());
                } else {
                    String thvalue = String.valueOf(settings.getThreshold());
                    sql = "select sum(amount-" + thvalue + ") as summ from smstable where year=" + String.valueOf(year) +
                            " and month=" + String.valueOf(month) + " and amount>=" + thvalue;
                }


                int donationAmount = DBUtils.getDonatioinTotalAmount(context, sql);
                totalAmount.setText(PRICE_FORMATTER.format(donationAmount) + getString(R.string.won));

                int realAmount = donationAmount * settings.getPercent() / 100;
                realTotalAmount.setText(PRICE_FORMATTER.format(realAmount) + getString(R.string.won));
            }

            displayDonationInfo();

            Button applyDonation = (Button) findViewById(R.id.applyDonation);
            applyDonation.setOnClickListener(this);
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }

    private boolean checkDonation(){
        boolean ret = true;

        Donations donations = settings.getDonations();
        if(donations==null){
            return false;
        }
        TextView donation_name = (TextView)findViewById(R.id.donation_name);
        if("".equals(donation_name.getText().toString())){
            return false;
        }
        TextView donation_bank = (TextView)findViewById(R.id.donation_bank);
        if("".equals(donation_bank.getText().toString())){
            return false;
        }
        TextView donation_account = (TextView)findViewById(R.id.donation_account);
        if("".equals(donation_account.getText().toString())){
            return false;
        }
        return ret;
    }

    private void displayDonationInfo(){
        TextView donation_name = (TextView)findViewById(R.id.donation_name);
        donation_name.setTypeface(SmsUtils.typefaceNaumGothic);
        TextView donation_bank = (TextView)findViewById(R.id.donation_bank);
        donation_bank.setTypeface(SmsUtils.typefaceNaumGothic);
        TextView donation_account = (TextView)findViewById(R.id.donation_account);
        donation_account.setTypeface(SmsUtils.typefaceNaumGothicCoding);
        TextView donation_address = (TextView)findViewById(R.id.donation_address);
        donation_address.setTypeface(SmsUtils.typefaceNaumGothic);
        TextView donation_tel = (TextView)findViewById(R.id.donation_tel);
        donation_tel.setTypeface(SmsUtils.typefaceNaumGothicCoding);
        TextView donation_www = (TextView)findViewById(R.id.donation_www);
        donation_www.setTypeface(SmsUtils.typefaceNaumGothic);

        Donations donations = settings.getDonations();
        if(donations!=null) {
            String name = donations.getName();
            String bankName = donations.getBankname();
            String bankAccount = donations.getAccount();
            String address = donations.getAddress();
            String tel = donations.getTel();
            String home = donations.getHome();

            donation_name.setText(name);
            donation_bank.setText(bankName);
            donation_account.setText(bankAccount);
            donation_address.setText(address);
            donation_tel.setText(tel);
            donation_www.setText(home);
        }else{
            donation_name.setText("");
            donation_bank.setText("");
            donation_account.setText("");
            donation_address.setText("");
            donation_tel.setText("");
            donation_www.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        if(!checkDonation()){
            ConfirmUtils confirmUtils = new ConfirmUtils(context, getString(R.string.donation_info_title), getString(R.string.donation_info_title_msg));
            confirmUtils.show();
        }else {
            if(!DBUtils.checkDonationHistory(context, donationHistory)) {
                TextView realTotalAmount = (TextView) findViewById(R.id.realTotalAmount);
                String realAmount = realTotalAmount.getText().toString();
                if(!"0원".equals(realAmount)) {
                    StringBuilder sb = new StringBuilder();
                    Donations donations = settings.getDonations();
                    sb.append(donations.getName()).append(getString(R.string.donation_run_1)).append("<br/>")
                            .append(donations.getBankname()).append("( ").append(donations.getAccount()).append(" ) ")
                            .append(getString(R.string.donation_run_2)).append("<br/> <b><font color=\"#FCFF33\">")
                            .append(realAmount).append("</font></b>").append(getString(R.string.donation_run_3));

                    confirm = new YesNoDialog(context, getString(R.string.donation_run_title), sb.toString(), listner);
                    confirm.setHtml(true);
                    confirm.show();
                }else{
                    ConfirmUtils confirmDialog = new ConfirmUtils(context, getString(R.string.donation_run_title), getString(R.string.donation_zero));
                    confirmDialog.show();
                }
            }else{
                String sql = "select * from " + DataBases.CreateDB._DONATION_HISTORY
                        + " where year=" + String.valueOf(year)
                        + " and month=" + String.valueOf(month);

                ArrayList<DonationHistory> data = DBUtils.getDonationHistory(context, sql);
                if(data.size()>0){
                    NumberFormat PRICE_FORMATTER = NumberFormat.getNumberInstance();
                    DonationHistory history = data.get(0);
                    StringBuilder sb = new StringBuilder();
                    String date = history.getYear() + getString(R.string.year) + " " + history.getMonth() + getString(R.string.month) + " " + history.getDay() + getString(R.string.day);
                    String donationName = history.getDonationName();
                    String amountValue = PRICE_FORMATTER.format(history.getAmount()) + getString(R.string.won);
                    String account = history.getAccount();
                    String bankname = history.getBankname();
                    String home = history.getHome();
                    String tel = history.getTel();
                    String address = history.getAddress();
                    String category = history.getCategory();

                    sb.append(category).append(" ").append(getString(R.string.donation_already_run_1)).append(" <b>")
                            .append(donationName).append("</b>").append(getString(R.string.donation_run_1)).append(" ").append(bankname)
                            .append(getString(R.string.donation_run_2)).append( " ")
                            .append(account).append("<b><font color=\"#0000FF\">").append(amountValue).append("</font></b>").append(getString(R.string.donation_already_run_2))
                            .append(" <b><font color=\"#FCFF33\">").append(date).append("</font><b>").append(getString(R.string.donation_already_run_3));
                    sb.append("<br/><br/><b><font color=\"#FCFF33\">").append(donationName).append("</font></b><br/><br/>").append(address).append("<br/>")
                            .append(home).append("<br/>").append(tel);
                    ConfirmUtils confirmDialog = new ConfirmUtils(context, getString(R.string.donation_already_run_title), sb.toString());
                    confirmDialog.setHtml(true);
                    confirmDialog.show();
                }
            }
        }
    }

    private void calcDonationByCategory(){
        try{
            cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;
            lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            int len = dinfo.size();
            for(int i=0; i<len; i++){
                DonationInfo info = dinfo.get(i);
                boolean val = info.isThresholdOverall();
                String card = info.getCard();
                String category = info.getCategory();
                int threshold = info.getThreshold();
                String sql = "";
                if(val){
                    sql = "select sum(amount) as summ from smstable where year=" + String.valueOf(year) +
                            " and month=" + String.valueOf(month) + " and amount>=" + String.valueOf(threshold) + " and card=\"" + card + "\"" +
                            " and category=\"" + category + "\"";
                }else{
                    sql = "select sum(amount-" + threshold + ") as summ from smstable where year=" + String.valueOf(year) +
                            " and month=" + String.valueOf(month) + " and amount>=" + threshold + " and card=\"" + card + "\"" +
                            " and category=\"" + category + "\"";
                }
                int amount = DBUtils.getDonatioinTotalAmount(context, sql);
                info.setAmount(amount);
            }

        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }

    private void viewDonationList(){
        TableRow row = (TableRow)findViewById(R.id.donation_detail_list_row);
        row.setVisibility(View.VISIBLE);
        ListView mListView = (ListView)findViewById(R.id.donation_detail_list);
        mListView.setVisibility(View.VISIBLE);
        mListView.setOnItemClickListener(mItemClickListener);

        DonationListViewAdapter mAdapter = new DonationListViewAdapter(context);
        mListView.setOverScrollMode(0);
        mListView.setAdapter(mAdapter);
        for(DonationInfo info : dinfo){
            mAdapter.addItem(info);
        }
        SmsUtils.setListViewHeightBasedOnChildren(context, mListView);
    }

    private IButtonClickListner listner = new ButtonClick(context){
        @Override
        public void onYesNoClick(final boolean yes){
            if(yes){
                authNumpadDialog = new AuthNumpadDialog(context, getString(R.string.authnum_title), authListner);
                authNumpadDialog.setPassword(settings.getRemittance_pwd());
                authNumpadDialog.show();
            }
            confirm.dismiss();
        }
    };

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
            // parent는 AdapterView의 속성의 모두 사용 할 수 있다.
            DonationListData data = (DonationListData)parent.getAdapter().getItem(position);
            Toast.makeText(context, data.cardName, Toast.LENGTH_SHORT).show();
        }
    };
}
