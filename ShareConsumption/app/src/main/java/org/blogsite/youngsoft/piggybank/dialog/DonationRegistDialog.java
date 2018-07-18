package org.blogsite.youngsoft.piggybank.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.blogsite.youngsoft.piggybank.adapter.SpinnerAdapter;
import org.blogsite.youngsoft.piggybank.setting.PBSettings;
import org.blogsite.youngsoft.piggybank.setting.PBSettingsUtils;
import org.blogsite.youngsoft.piggybank.utils.ButtonClick;
import org.blogsite.youngsoft.piggybank.utils.IButtonClickListner;
import org.blogsite.youngsoft.piggybank.utils.KeyboardUtils;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;

/**
 * Created by klee on 2018-01-26.
 */

public class DonationRegistDialog extends Dialog {
    private final Context context;

    private Button registOKButton;
    private Button registCancelButton;
    private Spinner spinner_donation;
    private EditText donation_name;
    private EditText donation_address;
    private EditText donation_tel;
    private EditText donation_www;
    private EditText donation_bank;
    private EditText donation_account;

    private String name = "";
    private String address = "";
    private String tel = "";
    private String www = "";
    private String bank = "";
    private String account = "";

    private  ArrayList<String> donaton_cat_list;
    private int selectedIndex = 0;
    private String selectedCategory = "";

    private  ConfirmDialog dialog;
    private IButtonClickListner listner;
    private IButtonClickListner register_listner;

    private View.OnClickListener registOKButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            applyDonationRegist();
        }
    };

    private View.OnClickListener registCancelButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.donation_regist_dialog);

        TextView titleTextView = (TextView)findViewById(R.id.title);
        titleTextView.setTypeface(SmsUtils.typefaceNaumGothic, Typeface.BOLD);

        donaton_cat_list = new ArrayList<String>();
        donaton_cat_list = new ArrayList<String>();
        donaton_cat_list.add(context.getString(R.string.select_category));
        donaton_cat_list.add(context.getString(R.string.catagory_buddhism));
        donaton_cat_list.add(context.getString(R.string.catagory_catholic));
        donaton_cat_list.add(context.getString(R.string.catagory_christian));
        donaton_cat_list.add(context.getString(R.string.catagory_welfare));
        donaton_cat_list.add(context.getString(R.string.catagory_sicial));
        donaton_cat_list.add(context.getString(R.string.catagory_policy));
        donaton_cat_list.add(context.getString(R.string.catagory_etc));
        selectedIndex = 0;
        selectedCategory = donaton_cat_list.get(selectedIndex);

        init();

        registOKButton = (Button) findViewById(R.id.registOKButton);

        // 클릭 이벤트 셋팅
        if (registOKButtonClickListener!=null) {
            registOKButton.setOnClickListener(registOKButtonClickListener);
        }

        registCancelButton = (Button) findViewById(R.id.registCancelButton);
        if (registCancelButtonClickListener!=null) {
            registCancelButton.setOnClickListener(registCancelButtonClickListener);
        }

        listner = new ButtonClick(context) {
            public void onYesNoClick(final boolean yes){
                dialog.dismiss();
            }
        };

        register_listner = new ButtonClick(context) {
            public void onYesNoClick(final boolean yes){
                dialog.dismiss();
                dismiss();
            }
        };
    }

    private void showToast(String msg){
        LayoutInflater inflater = getLayoutInflater();
        View toastDesign = inflater.inflate(R.layout.toast_design,
                (ViewGroup) findViewById(R.id.toast_design_root));

        TextView text = toastDesign.findViewById(R.id.TextView_toast_design);
        text.setText(msg); // toast_design.xml 파일에서 직접 텍스트를 지정 가능

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0); // CENTER를 기준으로 0, 0 위치에 메시지 출력
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastDesign);
        toast.show();
    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public DonationRegistDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
    }

    public void init()
    {
        donation_name = (EditText)findViewById(R.id.donation_name);
        donation_name.setTypeface(SmsUtils.typefaceNaumGothic);
        donation_name.setText(name);
        donation_name.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    donation_name.requestFocus();
                    if(donation_name.getText().length()!=0){
                        donation_name.setSelection(donation_name.getText().length());
                    }
                    EditUtils edit = new EditUtils(context, context.getString(R.string.donation_name), donation_name );
                    edit.show();
                    KeyboardUtils.hideKeyboard((Activity)context);
                }
                return false;
            }
        });

        donation_address = (EditText)findViewById(R.id.donation_address);
        donation_address.setTypeface(SmsUtils.typefaceNaumGothic);
        donation_address.setText(address);
        donation_address.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    donation_address.requestFocus();
                    if(donation_address.getText().length()!=0){
                        donation_address.setSelection(donation_address.getText().length());
                    }
                    EditUtils edit = new EditUtils(context, context.getString(R.string.donation_address), donation_address );
                    edit.show();
                    KeyboardUtils.hideKeyboard((Activity)context);
                }
                return false;
            }
        });

        donation_tel = (EditText)findViewById(R.id.donation_tel);
        donation_tel.setTypeface(SmsUtils.typefaceNaumGothicCoding);
        donation_tel.setText(tel);
        donation_tel.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    donation_tel.requestFocus();
                    if(donation_tel.getText().length()!=0){
                        donation_tel.setSelection(donation_tel.getText().length());
                    }
                    EditUtils edit = new EditUtils(context, context.getString(R.string.donation_tel), donation_tel );
                    edit.show();
                    KeyboardUtils.hideKeyboard((Activity)context);
                }
                return false;
            }
        });

        donation_www = (EditText)findViewById(R.id.donation_www);
        donation_www.setTypeface(SmsUtils.typefaceNaumGothic);
        donation_www.setText(www);
        donation_www.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    donation_www.requestFocus();
                    if(donation_www.getText().length()!=0){
                        donation_www.setSelection(donation_www.getText().length());
                    }
                    EditUtils edit = new EditUtils(context, context.getString(R.string.donation_www), donation_www );
                    edit.show();
                    KeyboardUtils.hideKeyboard((Activity)context);
                }
                return false;
            }
        });

        donation_bank = (EditText)findViewById(R.id.donation_bank);
        donation_bank.setTypeface(SmsUtils.typefaceNaumGothic);
        donation_bank.setText(bank);
        donation_bank.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    donation_bank.requestFocus();
                    if(donation_bank.getText().length()!=0){
                        donation_bank.setSelection(donation_bank.getText().length());
                    }
                    EditUtils edit = new EditUtils(context, context.getString(R.string.donation_bank), donation_bank );
                    edit.show();
                    KeyboardUtils.hideKeyboard((Activity)context);
                }
                return false;
            }
        });

        donation_account = (EditText)findViewById(R.id.donation_account);
        donation_account.setTypeface(SmsUtils.typefaceNaumGothicCoding);
        donation_account.setText(account);
        donation_account.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    donation_account.requestFocus();
                    if(donation_account.getText().length()!=0){
                        donation_account.setSelection(donation_account.getText().length());
                    }
                    EditUtils edit = new EditUtils(context, context.getString(R.string.donation_account), donation_account );
                    edit.show();
                    KeyboardUtils.hideKeyboard((Activity)context);
                }
                return false;
            }
        });

        spinner_donation = (Spinner) findViewById(R.id.spinner_donation);
        SpinnerAdapter adapter = new SpinnerAdapter(context, donaton_cat_list);
        spinner_donation.setAdapter(adapter);
        spinner_donation.setSelection(selectedIndex);
        spinner_donation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!= selectedIndex){
                    selectedIndex = position;
                    selectedCategory = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private boolean checkInput(){
        if(selectedIndex==0){
            return false;
        }
        name = donation_name.getText().toString();
        address = donation_address.getText().toString();
        tel = donation_tel.getText().toString();
        www = donation_www.getText().toString();
        bank = donation_bank.getText().toString();
        account = donation_account.getText().toString();
        if("".equals(name)){
            return false;
        }
        if("".equals(address)){
            return false;
        }
        if("".equals(tel)){
            return false;
        }
        if("".equals(www)){
            return false;
        }
        if("".equals(bank)){
            return false;
        }
        if("".equals(account)){
            return false;
        }

        return true;
    }

    private void applyDonationRegist(){
        try {
            if(checkInput()) {
                PBSettings settings = PBSettingsUtils.getInstance().getSettings();
                DatabaseReference exceptDatabase = FirebaseDatabase.getInstance().getReference();
                DatabaseReference rcvrpyRef = exceptDatabase.child("PiggyBank").child("Rules");
                DateFormat df = new SimpleDateFormat(context.getString(R.string.date_format_kor));
                String s = df.format(new Date());
                String user_email = settings.getUserEmail();
                user_email = StringUtils.replaceAll(user_email, "@", "_");
                user_email = StringUtils.replaceAll(user_email, ".", "_");
                rcvrpyRef.child("RegisteredDonation").child(user_email).child(s).child("Account").setValue(account);
                rcvrpyRef.child("RegisteredDonation").child(user_email).child(s).child("Address").setValue(address);
                rcvrpyRef.child("RegisteredDonation").child(user_email).child(s).child("BankName").setValue(bank);
                rcvrpyRef.child("RegisteredDonation").child(user_email).child(s).child("Category").setValue(selectedCategory);
                rcvrpyRef.child("RegisteredDonation").child(user_email).child(s).child("Home").setValue(www);
                rcvrpyRef.child("RegisteredDonation").child(user_email).child(s).child("Name").setValue(name);
                rcvrpyRef.child("RegisteredDonation").child(user_email).child(s).child("Resource").setValue("");
                rcvrpyRef.child("RegisteredDonation").child(user_email).child(s).child("Tel").setValue(tel);

                dialog = new ConfirmDialog(context, context.getString(R.string.regist_finish_title),
                        context.getString(R.string.regist_finish),
                        register_listner, false);
                dialog.show();

            }else{
                dialog = new ConfirmDialog(context, context.getString(R.string.regist_error_title),
                        context.getString(R.string.regist_error), listner, false);
                dialog.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
