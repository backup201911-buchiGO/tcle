package org.blogsite.youngsoft.piggybank.utils;

import android.app.Activity;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.blogsite.youngsoft.piggybank.R;

/**
 * Created by klee on 2018-01-27.
 */

public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity, SmsUtils.getResource(R.string.exit_msg), Toast.LENGTH_SHORT);
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTypeface(SmsUtils.typefaceNaumGothic);
        messageTextView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        messageTextView.setPaintFlags(messageTextView.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        toast.show();
    }
}
