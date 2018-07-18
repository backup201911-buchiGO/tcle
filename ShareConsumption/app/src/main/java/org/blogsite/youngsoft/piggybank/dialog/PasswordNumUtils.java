package org.blogsite.youngsoft.piggybank.dialog;

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import org.blogsite.youngsoft.piggybank.setting.PBSettings;
import org.blogsite.youngsoft.piggybank.utils.ButtonClick;
import org.blogsite.youngsoft.piggybank.utils.IButtonClickListner;
import org.blogsite.youngsoft.piggybank.utils.KeyboardUtils;

public class PasswordNumUtils {
    private PasswordNumpadDialog passwordNumpadDialog;
    private IButtonClickListner listner;
    private Context context;
    private String title;
    private TextView textView;
    private boolean modifyPwd = false;

    private PBSettings settings;

    private boolean shown = false;

    public PasswordNumUtils(final Context context, String title, TextView textView){
        this.context = context;
        this.title = title;
        this.textView = textView;
        shown = false;

        if(listner==null) {
            listner = new ButtonClick(context) {

                @Override
                public void onYesNoClick(final boolean yes) {
                    if (yes) {
                        KeyboardUtils.hideKeyboard((Activity)context);
                        passwordNumpadDialog.dismiss();
                        shown = false;
                    }else{
                        KeyboardUtils.hideKeyboard((Activity)context);
                        passwordNumpadDialog.dismiss();
                        shown = false;
                    }
                }
            };
        }
        passwordNumpadDialog = new PasswordNumpadDialog(context, title, textView, listner);
    }

    public void setListner(IButtonClickListner listner) {
        this.listner = listner;
    }

    public void show(){
        if(passwordNumpadDialog!=null && !shown) {
            passwordNumpadDialog.setSettings(settings);
            passwordNumpadDialog.setModifyPwd(modifyPwd);
            passwordNumpadDialog.show();
            shown = true;
        }
    }

    public void setModifyPwd(boolean modifyPwd){
        this.modifyPwd = modifyPwd;
    }

    public void setSettings(PBSettings settings){
        this.settings = settings;
    }

    public void hide(){
        passwordNumpadDialog.dismiss();
    }

    public boolean isShown() {
        return shown;
    }
}
