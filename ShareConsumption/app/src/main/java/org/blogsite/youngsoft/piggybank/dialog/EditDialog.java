package org.blogsite.youngsoft.piggybank.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.utils.IButtonClickListner;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

/**
 * Created by klee on 2018-01-26.
 */

public class EditDialog extends Dialog {
    private final Context context;

    private TextView titleTextView;
    private EditText editText;
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
        valueEdit.setInputType(editText.getInputType());

        valueEdit.setText(editText.getText().toString());
        if(valueEdit.getText().length()!=0){
            valueEdit.setSelection(valueEdit.getText().length());
        }
        mOKButton = (Button) findViewById(R.id.OKButton);
        mOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(valueEdit.getText().toString());
                if(editText.getText().length()!=0){
                    editText.setSelection(editText.getText().length());
                }
                editText.invalidate();
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
    public EditDialog(Context context, String title, EditText editText, IButtonClickListner listner) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.title = title;
        this.editText = editText;
        this.listner = listner;
    }

}
