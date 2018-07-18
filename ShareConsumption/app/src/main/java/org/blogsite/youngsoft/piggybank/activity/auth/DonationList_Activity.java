package org.blogsite.youngsoft.piggybank.activity.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.adapter.DonationAdapter;
import org.blogsite.youngsoft.piggybank.adapter.SpinnerAdapter;
import org.blogsite.youngsoft.piggybank.dialog.ConfirmUtils;
import org.blogsite.youngsoft.piggybank.dialog.DonationRegistUtils;
import org.blogsite.youngsoft.piggybank.dialog.YesNoDialog;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.setting.DonationList;
import org.blogsite.youngsoft.piggybank.setting.Donations;
import org.blogsite.youngsoft.piggybank.setting.PBSettings;
import org.blogsite.youngsoft.piggybank.setting.PBSettingsUtils;
import org.blogsite.youngsoft.piggybank.utils.ButtonClick;
import org.blogsite.youngsoft.piggybank.utils.IButtonClickListner;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DonationList_Activity  extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DonationList_Activity";
    private DonationList donationList = new DonationList();
    private final Context context = this;
    private GridView gridview;
    private ArrayList<String> nameList;
    private ArrayList<Integer> imageList;
    private int oldId = -1;
    private PBSettings settings = null;
    private IButtonClickListner listner;
    private Donations selDonation = null;
    private YesNoDialog confirm = null;
    private Button registButton;
    private ArrayList<String> donaton_cat_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donation_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridview = (GridView) findViewById(R.id.donationgrid);
        settings = PBSettingsUtils.getInstance().getSettings();

        donationList = settings.getDonationList();

        donaton_cat_list = new ArrayList<String>();
        donaton_cat_list.add(getString(R.string.catagory_all));
        donaton_cat_list.add(getString(R.string.catagory_buddhism));
        donaton_cat_list.add(getString(R.string.catagory_catholic));
        donaton_cat_list.add(getString(R.string.catagory_christian));
        donaton_cat_list.add(getString(R.string.catagory_welfare));
        donaton_cat_list.add(getString(R.string.catagory_sicial));
        donaton_cat_list.add(getString(R.string.catagory_policy));
        donaton_cat_list.add(getString(R.string.catagory_etc));
        oldId = 0;
        initCustomSpinner();

        nameList = new ArrayList<String>();
        imageList = new ArrayList<Integer>();
        HashMap<String, ArrayList<Donations>> donations = donationList.getDonations();
        Iterator<String> it = donations.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            ArrayList<Donations> donationList = donations.get(key);
            for (Donations d : donationList) {
                String name = d.getName();
                if(!existDonationName(name)) {
                    nameList.add(d.getName());
                    if (d.getResId() == -1) {
                        imageList.add(d.getResId());
                    } else {
                        imageList.add(d.getResId());
                    }
                }
            }
        }
        listner = new ButtonClick(context) {
            public void onYesNoClick(final boolean yes){
                confirm.dismiss();
                if(yes){
                    settings.setDonations(selDonation);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Name",selDonation.getName());
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
            }
        };

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, ArrayList<Donations>> donations = donationList.getDonations();
                Iterator<String> it = donations.keySet().iterator();
                String name = nameList.get(position);
                String category = "";
                while(it.hasNext()){
                    category = it.next();
                    ArrayList<Donations> donationList = donations.get(category);
                    for(Donations d : donationList){
                        if(name.equals(d.getName())){
                            selDonation = d;
                            break;
                        }
                    }
                }

                confirm = new YesNoDialog(context, getString(R.string.select_donation), "<b><font color=\"#FCFF33\">" + selDonation.getName() + "</font></b><br/><br/>" +
                        getString(R.string.address) + ":" +
                        selDonation.getAddress() + "<br/><br/>" + getString(R.string.select_donation_question), listner);
                confirm.setHtml(true);
                confirm.show();
            }
        });

        DonationAdapter adapter = new DonationAdapter(context, nameList, imageList);
        adapter.notifyDataSetChanged();
        gridview.setAdapter(adapter);
        SmsUtils.setGridViewHeightBasedOnChildren(context, gridview);

        registButton = findViewById(R.id.registButton);
        registButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DonationRegistUtils donationRegistUtils = new DonationRegistUtils(context);
                donationRegistUtils.show();
            }
        });
    }

    /**
     * 후원기관 이름 중복 테스트
     * @param name
     * @return
     */
    private boolean existDonationName(String name){
        boolean ret = false;

        for(String n : nameList){
            if(n.equals(name)){
                ret = true;
                break;
            }
        }
        return ret;
    }

    private void initCustomSpinner() {
        try {
            Spinner spinner_donation = (Spinner) findViewById(R.id.spinner_donation);
            SpinnerAdapter adapter = new SpinnerAdapter(context, donaton_cat_list);
            spinner_donation.setAdapter(adapter);
            spinner_donation.setSelection(oldId);
            spinner_donation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position!=oldId){
                        oldId = position;
                        String item = parent.getItemAtPosition(position).toString();
                        nameList.clear();
                        imageList.clear();
                        HashMap<String, ArrayList<Donations>> donations = donationList.getDonations();
                        ArrayList<Donations> list = null;
                        if(getString(R.string.catagory_all).equals(item)){
                            Iterator<String> it = donations.keySet().iterator();
                            while(it.hasNext()){
                                String key = it.next();
                                ArrayList<Donations> donationList = donations.get(key);
                                for(Donations d : donationList){
                                    String name = d.getName();
                                    if(!existDonationName(name)) {
                                        nameList.add(name);
                                        if (d.getResId() == -1) {
                                            imageList.add(d.getResId());
                                        } else {
                                            imageList.add(d.getResId());
                                        }
                                    }
                                }
                            }
                        }else{
                            list = donations.get(item);
                            if(list!=null) {
                                for (Donations d : list) {
                                    nameList.add(d.getName());
                                    if(d.getResId()==-1) {
                                        imageList.add(d.getResId());
                                    }else{
                                        imageList.add(d.getResId());
                                    }
                                }
                            }
                        }
                        DonationAdapter adapter = new DonationAdapter(context, nameList, imageList);
                        adapter.notifyDataSetChanged();
                        gridview.setAdapter(adapter);
                        SmsUtils.setGridViewHeightBasedOnChildren(context, gridview);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
