package org.blogsite.youngsoft.piggybank.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.db.DBUtils;
import org.blogsite.youngsoft.piggybank.dialog.AuthNumpadDialog;
import org.blogsite.youngsoft.piggybank.dialog.ConfirmUtils;
import org.blogsite.youngsoft.piggybank.dialog.EditUtils;
import org.blogsite.youngsoft.piggybank.dialog.SettingsEditUtils;
import org.blogsite.youngsoft.piggybank.dialog.YesNoDialog;
import org.blogsite.youngsoft.piggybank.setting.DonationInfo;
import org.blogsite.youngsoft.piggybank.utils.Alert;
import org.blogsite.youngsoft.piggybank.utils.ButtonClick;
import org.blogsite.youngsoft.piggybank.utils.CardEnum;
import org.blogsite.youngsoft.piggybank.utils.CategoryEnum;
import org.blogsite.youngsoft.piggybank.utils.IButtonClickListner;
import org.blogsite.youngsoft.piggybank.utils.KeyboardUtils;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;

import java.text.NumberFormat;

public class DonationDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final NumberFormat PRICE_FORMATTER = NumberFormat.getNumberInstance();

    private Context context;
    private int donationInfoCount = 0;
    private DonationInfo donationInfo = null;
    private CardEnum card;
    private CategoryEnum category;

    private TextView threshold;
    private TextView percent;
    private Switch swoverall;
    private TextView lbloveralldesc;
    private Button apply;
    private Button delSetting;

    private YesNoDialog confirm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String cardname = intent.getStringExtra("CardName");
        String catname = intent.getStringExtra("Category");
        card = CardEnum.getByName(cardname);
        category = CategoryEnum.getByName(catname);

        context = this;

        delSetting = (Button) findViewById(R.id.delSetting);
        delSetting.setTypeface(SmsUtils.typefaceNaumGothic);
        delSetting.setOnClickListener(this);

        donationInfoCount = DBUtils.getDonationInfoCount(context, cardname, catname);
        if(donationInfoCount!=0){
            donationInfo = DBUtils.getDonationInfo(context, cardname, catname);
            delSetting.setEnabled(true);
        }else{
            donationInfo = new DonationInfo();
            delSetting.setEnabled(false);
        }
        donationInfo.setCard(cardname);
        donationInfo.setCategory(catname);

        apply = (Button) findViewById(R.id.applyDonationDetail);
        apply.setTypeface(SmsUtils.typefaceNaumGothic);
        apply.setOnClickListener(this);

        int resId = -1;
        if(card.equals(CardEnum.KB_CARD)) {
            resId = R.drawable.ic_kbcard;
        }else if(card.equals(CardEnum.KB_CHECKCARD)){
            resId = R.drawable.ic_kbcheck;
        }else if(card.equals(CardEnum.SHINHAN_CARD) || card.equals(CardEnum.SHINHAN_CHECKCARD) || card.equals(CardEnum.SHINHAN_CORPCARD)){
            resId = R.drawable.ic_shinhan;
        }else if(card.equals(CardEnum.NH_CARD) || card.equals(CardEnum.NH_CHECKCARD) || card.equals(CardEnum.NH_CORPCARD) || card.equals(CardEnum.NH_WELFARECARD)) {
            resId = R.drawable.ic_nhcard;
        }else if(card.equals(CardEnum.SAMSUNG_CARD)){
            resId = R.drawable.ic_samsung;
        }else if(card.equals(CardEnum.HYUNDAE_CARD)){
            resId = R.drawable.ic_hyundai;
        }else{
            resId = R.drawable.ic_unknown;
        }

        ImageView imgCard = findViewById(R.id.imgcard);
        imgCard.setImageResource(resId);

        TextView cardText = findViewById(R.id.lblcard);
        cardText.setTypeface(SmsUtils.typefaceNaumGothic);
        cardText.setText(card.getName());

        int catId = -1;
        if(category.equals(CategoryEnum.Communication)){
            catId = R.drawable.communication;
        }else if(category.equals(CategoryEnum.Culture)){
            catId = R.drawable.culture;
        }else if(category.equals(CategoryEnum.Dues)){
            catId = R.drawable.dues;
        }else if(category.equals(CategoryEnum.Meal)){
            catId = R.drawable.meal;
        }else if(category.equals(CategoryEnum.Medical)){
            catId = R.drawable.medical;
        }else if(category.equals(CategoryEnum.Shopping)){
            catId = R.drawable.shopping;
        }else if(category.equals(CategoryEnum.Traffic)){
            catId = R.drawable.traffic;
        }else{
            catId = R.drawable.unclassified;
        }

        String v = "";
        ImageView imgCat = findViewById(R.id.imgcategory);
        imgCat.setImageResource(catId);
        TextView catText = findViewById(R.id.lblcategory);
        catText.setTypeface(SmsUtils.typefaceNaumGothic);
        catText.setText(category.getName());

        TextView lblthroshold = findViewById(R.id.lblthreshold);
        lblthroshold.setTypeface(SmsUtils.typefaceNaumGothic);
        threshold = findViewById(R.id.threshold);
        threshold.setTypeface(SmsUtils.typefaceNaumGothicCoding);
        threshold.setInputType(InputType.TYPE_CLASS_NUMBER);
        if(donationInfo!=null){
            v = "<font color=\"#F36164\">" + PRICE_FORMATTER.format(donationInfo.getThreshold()) + " 원</font>" + getString(R.string.input_tag);
            threshold.setText(Html.fromHtml(v));
        }
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

        TextView lblpercent = findViewById(R.id.lblpercent);
        lblpercent.setTypeface(SmsUtils.typefaceNaumGothic);
        percent = findViewById(R.id.percent);
        percent.setTypeface(SmsUtils.typefaceNaumGothicCoding);
        percent.setInputType(InputType.TYPE_CLASS_NUMBER);
        if(donationInfo!=null){
            v = "<font color=\"#F36164\">" + PRICE_FORMATTER.format(donationInfo.getPercent()) + " %</font>" + getString(R.string.input_tag);
            percent.setText(Html.fromHtml(v));
        }
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

        TextView lbloverall = findViewById(R.id.lbloverall);
        lbloverall.setTypeface(SmsUtils.typefaceNaumGothic);
        Switch overall = findViewById(R.id.overall);
        overall.setTypeface(SmsUtils.typefaceNaumGothic);

        lbloveralldesc = findViewById(R.id.lbloveralldesc);
        lbloveralldesc.setTypeface(SmsUtils.typefaceNaumGothic);

        swoverall = (Switch) findViewById(R.id.overall);
        boolean val = false;
        if(donationInfo!=null) {
            val = donationInfo.isThresholdOverall();
        }
        swoverall.setChecked(val);
        swoverall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ConfirmUtils confirmUtils = new ConfirmUtils(context, getResources().getString(R.string.threshold_title), getResources().getString(R.string.overallon));
                    confirmUtils.show();
                    lbloveralldesc.setText(R.string.overallon);
                } else {
                    ConfirmUtils confirmUtils = new ConfirmUtils(context, getResources().getString(R.string.threshold_title), getResources().getString(R.string.overalloff));
                    confirmUtils.show();
                    lbloveralldesc.setText(R.string.overalloff);
                }
                donationInfo.setThresholdOverall(isChecked);
            }
        });
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
        int i = v.getId();
        if (i == R.id.applyDonationDetail) {
            donationInfo.setThreshold(getValToInt(threshold));
            donationInfo.setPercent(getValToInt(percent));
            donationInfo.setThresholdOverall(swoverall.isChecked());
            long ret = DBUtils.insertDonationInfo(context, donationInfo);
            finish();
        }else if (i == R.id.delSetting) {
            String card = donationInfo.getCard();
            String category = donationInfo.getCategory();
            confirm = new YesNoDialog(context, getString(R.string.donation_delete_title), card + " "
                    + getString(R.string.donation_delete_1) + " " + category + " " + getString(R.string.donation_delete_2), listner);
            confirm.show();
        }
    }

    private IButtonClickListner listner = new ButtonClick(context){
        @Override
        public void onYesNoClick(final boolean yes){
            if(yes){
                long ret = DBUtils.deleteDonationInfo(context, donationInfo);
                delSetting.setEnabled(false);
            }
            confirm.dismiss();
        }
    };

}
