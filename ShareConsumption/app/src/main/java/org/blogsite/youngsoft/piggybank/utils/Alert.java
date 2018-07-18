package org.blogsite.youngsoft.piggybank.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.blogsite.youngsoft.piggybank.R;

/**
 * Created by klee on 2018-01-18.
 */

public class Alert {
    private IButtonClickListner listner;

    public Alert(IButtonClickListner listner){
        this.listner = listner;
    }

    public static void showAlert(Context context, String title, String msg){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setPositiveButton(SmsUtils.getResource(R.string.okbutton),
                new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialog, int id) {
                    }
                });
        alertDialogBuilder.create();
        alertDialogBuilder.show();
    }

    public void showConfirmAlert(Context context, String title, String msg, String ok, String cancel){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setPositiveButton(ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listner.onYesNoClick(true);
                        return;
                    }
                })
                .setNegativeButton(cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listner.onYesNoClick(false);
                        return;
                    }
                });
        alertDialogBuilder.create();
        alertDialogBuilder.show();
    }

    public static void showToast(Context context, View toastDesign, String msg){
        TextView text = toastDesign.findViewById(R.id.TextView_toast_design);
        text.setText(msg); // toast_design.xml 파일에서 직접 텍스트를 지정 가능

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0); // CENTER를 기준으로 0, 0 위치에 메시지 출력
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastDesign);
        toast.show();
    }
}
