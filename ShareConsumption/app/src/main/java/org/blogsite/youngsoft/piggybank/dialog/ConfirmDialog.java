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

public class ConfirmDialog extends Dialog {
    private final Context context;

    private TextView titleTextView;
    private TextView msgTextView;
    private IButtonClickListner listner;
    private Button mOKButton;
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

        setContentView(R.layout.confirm_dialog);

        titleTextView = (TextView)findViewById(R.id.yesno_title);

        titleTextView.setTypeface(SmsUtils.typefaceNaumGothic, Typeface.BOLD);
        titleTextView.setText(title);
        msgTextView = (TextView)findViewById(R.id.yesynoMessage);
        msgTextView.setTypeface(SmsUtils.typefaceNaumGothic);
        msgTextView.setText(Html.fromHtml(msg));

        mOKButton = (Button) findViewById(R.id.OKButton);

        mOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onYesNoClick(true);
            }
        });

    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public ConfirmDialog(Context context, String title, String msg,
                              IButtonClickListner listner) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.title = title;
        this.msg = msg;
        this.listner = listner;
    }

    public ConfirmDialog(Context context, String title, String msg,
                         IButtonClickListner listner, boolean html) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.title = title;
        this.msg = msg;
        this.listner = listner;
        this.html = html;
    }

}
