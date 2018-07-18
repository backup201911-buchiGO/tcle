package org.blogsite.youngsoft.piggybank.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.utils.IButtonClickListner;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;

import java.text.NumberFormat;

/**
 * Created by klee on 2018-01-26.
 */

public class SettingsEditDialog extends Dialog {
    private final Context context;

    private static final NumberFormat PRICE_FORMATTER = NumberFormat.getNumberInstance();
    private TextView titleTextView;
    private TextView textView;
    private EditText valueEdit;
    private IButtonClickListner listner;
    private Button mOKButton;
    private Button mCancelButton;

    private String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.edit_dialog);

        titleTextView = (TextView)findViewById(R.id.edit_title);

        titleTextView.setTypeface(SmsUtils.typefaceNaumGothic, Typeface.BOLD);
        titleTextView.setText(title);
        valueEdit = (EditText)findViewById(R.id.value);
        valueEdit.setTypeface(SmsUtils.typefaceNaumGothicCoding);
        valueEdit.setInputType(InputType.TYPE_CLASS_NUMBER);

        String s = textView.getText().toString();
        s = StringUtils.replaceAll(s, ",", "");
        s = StringUtils.replaceAll(s, " 원", "");
        s = StringUtils.replaceAll(s, " %", "");
        s = StringUtils.replaceAll(s, " 일", "");
        s = StringUtils.replaceAll(s, "　", "");
        s = StringUtils.replaceAll(s, ">", "");
        valueEdit.setText(s);
        if(valueEdit.getText().length()!=0){
            valueEdit.setSelection(valueEdit.getText().length());
        }
        mOKButton = (Button) findViewById(R.id.OKButton);
        mOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = Integer.valueOf(valueEdit.getText().toString());
                String tag = " 원";
                if(title.equals(context.getResources().getString(R.string.percent_edit_title))){
                    tag = " %";
                }else if(title.equals(context.getResources().getString(R.string.donation_title))){
                    tag = " 일";
                }
                String msg = "<font color=\"#F36164\">" + PRICE_FORMATTER.format(val) + tag + "</font>" + context.getResources().getString(R.string.input_tag);
                textView.setText(Html.fromHtml(msg));
                textView.invalidate();
                listner.onYesNoClick(true);
            }
        });

        mCancelButton = (Button) findViewById(R.id.cancelButton);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  listner.onYesNoClick(false);
            }
        });
    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public SettingsEditDialog(Context context, String title, TextView textView, IButtonClickListner listner) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.title = title;
        this.textView = textView;
        this.listner = listner;
    }

}
