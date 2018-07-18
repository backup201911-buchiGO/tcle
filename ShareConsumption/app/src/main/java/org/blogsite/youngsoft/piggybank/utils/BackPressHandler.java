package org.blogsite.youngsoft.piggybank.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.blogsite.youngsoft.piggybank.activity.DonationActivity;
import org.blogsite.youngsoft.piggybank.activity.MainActivity;

public class BackPressHandler {
    private Toast toast;

    private Activity activity;

    public BackPressHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        //if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            showGuide();
            return;
        //}
        //if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
        //    activity.finish();
        //    toast.cancel();
        //}
    }

    public void showGuide() {
        toast = Toast.makeText(activity, "화면 상단의 \'뒤로 가기\' \"←\" 버튼을 터치하여야 합니다..", Toast.LENGTH_SHORT);
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTypeface(SmsUtils.typefaceNaumGothic);
        messageTextView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        messageTextView.setPaintFlags(messageTextView.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        toast.show();
    }
}
