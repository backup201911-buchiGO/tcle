package org.blogsite.youngsoft.piggybank.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import org.blogsite.youngsoft.piggybank.R;

/**
 * Created by klee on 2018-01-26.
 */

public class SettingsDialog extends Dialog {
    private final Context context;

    private TextView mContentView;
    private Button mOKButton;

    private View.OnClickListener mOKButtonClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.settings_dialog);

        mOKButton = (Button) findViewById(R.id.settingsOKButton);

        // 클릭 이벤트 셋팅
        if (mOKButtonClickListener!=null) {
            mOKButton.setOnClickListener(mOKButtonClickListener);
        }
    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public SettingsDialog(Context context, View.OnClickListener mOKButtonClickListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.mOKButtonClickListener = mOKButtonClickListener;
    }

}
