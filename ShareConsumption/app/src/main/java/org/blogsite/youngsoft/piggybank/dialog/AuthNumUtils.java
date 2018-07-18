package org.blogsite.youngsoft.piggybank.dialog;

import android.app.Activity;
import android.content.Context;

import org.blogsite.youngsoft.piggybank.setting.PBSettings;
import org.blogsite.youngsoft.piggybank.utils.ButtonClick;
import org.blogsite.youngsoft.piggybank.utils.IButtonClickListner;
import org.blogsite.youngsoft.piggybank.utils.KeyboardUtils;

public class AuthNumUtils {
    private AuthNumpadDialog authNumpadDialog;
    private IButtonClickListner listner;
    private Context context;
    private String title;

    private boolean shown = false;

    public AuthNumUtils(final Context context, String title, String password){
        this.context = context;
        this.title = title;
        shown = false;

        if(listner==null) {
            listner = new ButtonClick(context) {

                @Override
                public void onYesNoClick(final boolean yes) {
                    if (yes) {
                        KeyboardUtils.hideKeyboard((Activity)context);
                        authNumpadDialog.dismiss();
                        shown = false;
                    }else{
                        KeyboardUtils.hideKeyboard((Activity)context);
                        authNumpadDialog.dismiss();
                        shown = false;
                    }
                }
            };
        }
        authNumpadDialog = new AuthNumpadDialog(context, title, listner);
        authNumpadDialog.setPassword(password);
    }

    public void setListner(IButtonClickListner listner) {
        this.listner = listner;
    }

    public void show(){
        if(authNumpadDialog!=null && !shown) {
            authNumpadDialog.show();
            shown = true;
        }
    }

    public void hide(){
        authNumpadDialog.dismiss();
    }

    public boolean isShown() {
        return shown;
    }
}
