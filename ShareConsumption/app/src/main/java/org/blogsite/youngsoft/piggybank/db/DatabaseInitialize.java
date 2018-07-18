package org.blogsite.youngsoft.piggybank.db;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.blogsite.youngsoft.piggybank.dialog.ConfirmUtils;
import org.blogsite.youngsoft.piggybank.dialog.YesNoDialog;
import org.blogsite.youngsoft.piggybank.reader.SMSReader;
import org.blogsite.youngsoft.piggybank.utils.ButtonClick;
import org.blogsite.youngsoft.piggybank.utils.IButtonClickListner;
import org.blogsite.youngsoft.piggybank.utils.IO;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;


/**
 * Created by klee on 2018-02-03.
 */

public class DatabaseInitialize {
    private YesNoDialog yesNoDialog;
    private IButtonClickListner listner;
    private Context context;
    private String title;
    private String msg;

    public DatabaseInitialize(final Context context, String title, String msg){
        this.context = context;
        this.title = title;
        this.msg = msg;

        listner = new ButtonClick(context){
            private  Context mcontex = context;

            @Override
            public void onYesNoClick(final boolean yes){
                if(yes){
                    LayoutInflater inflater = ((Activity)mcontex).getLayoutInflater();
                    View toastDesign = inflater.inflate(R.layout.toast_design,
                            (ViewGroup)((Activity)mcontex).findViewById(R.id.toast_design_root));

                    boolean deleted = false;
                    IO io = new IO();

                    if(io.isDBExist()){
                        DBUtils.dbInitialize(mcontex);
                        SMSReader reader = new SMSReader(context);
                        reader.read();
                    }else{
                        ConfirmUtils confirm = new ConfirmUtils(context, SmsUtils.getResource(R.string.init_db_title), SmsUtils.getResource(R.string.no_sms));
                        confirm.show();
                    }
                    yesNoDialog.dismiss();
                }else{
                    yesNoDialog.dismiss();
                }
            }
        };
        yesNoDialog = new YesNoDialog(context, title, msg, listner);

    }

    public void show(){
        yesNoDialog.show();
    }
}
