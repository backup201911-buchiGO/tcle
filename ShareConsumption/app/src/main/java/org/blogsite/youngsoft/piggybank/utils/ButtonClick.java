package org.blogsite.youngsoft.piggybank.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.blogsite.youngsoft.piggybank.reader.SMSReader;
import org.blogsite.youngsoft.piggybank.R;

/**
 * Created by klee on 2018-02-01.
 */

public class ButtonClick implements IButtonClickListner {
    private final Context context;
    public ButtonClick(Context context){
        this.context = context;
    }
    public void onYesNoClick(final boolean yes){
        if(yes){
            String msg  = SmsUtils.getResource(R.string.db_init_msg);
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            View toastDesign = inflater.inflate(R.layout.toast_design,
                    (ViewGroup)((Activity)context).findViewById(R.id.toast_design_root));

            boolean deleted = false;
            IO io = new IO();
            if(io.isDBExist()){
                deleted = io.deleteDBFile();
            }
            if(deleted) {
                SMSReader reader = new SMSReader(context);
                reader.read();
            }else{
                Alert.showAlert(context, SmsUtils.getResource(R.string.db_init), SmsUtils.getResource(R.string.db_init_error));
            }
        }
    }

}
