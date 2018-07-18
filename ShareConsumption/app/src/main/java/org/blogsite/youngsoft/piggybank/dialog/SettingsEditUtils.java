package org.blogsite.youngsoft.piggybank.dialog;

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import org.blogsite.youngsoft.piggybank.utils.ButtonClick;
import org.blogsite.youngsoft.piggybank.utils.IButtonClickListner;
import org.blogsite.youngsoft.piggybank.utils.KeyboardUtils;

public class SettingsEditUtils {
    private SettingsEditDialog editDialog;
    private IButtonClickListner listner;
    private Context context;
    private String title;
    private TextView editText;

    private boolean shown = false;

    public SettingsEditUtils(final Context context, String title, TextView editText){
        this.context = context;
        this.title = title;
        this.editText = editText;
        shown = false;

        if(listner==null) {
            listner = new ButtonClick(context) {

                @Override
                public void onYesNoClick(final boolean yes) {
                    if (yes) {
                        editDialog.dismiss();
                        shown = false;
                    }else{
                        editDialog.dismiss();
                        shown = false;
                    }
                    KeyboardUtils.hideKeyboard((Activity)context);
                }
            };
        }
        editDialog = new SettingsEditDialog(context, title, editText, listner);

    }

    public void setListner(IButtonClickListner listner) {
        this.listner = listner;
    }

    public void show(){
        if(editDialog!=null && !shown) {
            editDialog.show();
            shown = true;
        }
    }

    public void hide(){
        editDialog.dismiss();
    }

    public boolean isShown() {
        return shown;
    }
}
