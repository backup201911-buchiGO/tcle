package org.blogsite.youngsoft.piggybank.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import org.blogsite.youngsoft.piggybank.utils.IButtonClickListner;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

import org.blogsite.youngsoft.piggybank.R;

/**
 * Created by klee on 2018-01-26.
 */

public class YesNoDialog extends Dialog {
    private final Context context;

    private TextView titleTextView;
    private TextView msgTextView;
    private IButtonClickListner listner;
    private Button mOKButton;
    private Button mCancelButton;
    private boolean html = false;

    private String title;
    private String msg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.yesno_dialog);

        titleTextView = (TextView)findViewById(R.id.yesno_title);

        titleTextView.setTypeface(SmsUtils.typefaceNaumGothic, Typeface.BOLD);
        titleTextView.setText(title);
        msgTextView = (TextView)findViewById(R.id.yesynoMessage);
        msgTextView.setTypeface(SmsUtils.typefaceNaumGothic);
        if(html){
            msgTextView.setText(Html.fromHtml(msg));
        }else {
            msgTextView.setText(msg);
        }

        mOKButton = (Button) findViewById(R.id.OKButton);
        mCancelButton = (Button) findViewById(R.id.CancelButton);

        mOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onYesNoClick(true);
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onYesNoClick(false);
            }
        });
    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public YesNoDialog(Context context, String title, String msg,
                       IButtonClickListner listner) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.title = title;
        this.msg = msg;
        this.listner = listner;
    }

    public void setHtml(boolean html) {
        this.html = html;
    }
}
