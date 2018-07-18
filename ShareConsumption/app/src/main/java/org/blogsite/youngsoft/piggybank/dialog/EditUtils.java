package org.blogsite.youngsoft.piggybank.dialog;

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;

import org.blogsite.youngsoft.piggybank.utils.ButtonClick;
import org.blogsite.youngsoft.piggybank.utils.IButtonClickListner;
import org.blogsite.youngsoft.piggybank.utils.KeyboardUtils;

public class EditUtils {
    private EditDialog editDialog;
    private IButtonClickListner listner;
    private Context context;
    private String title;
    private EditText editText;

    private boolean shown = false;

    public EditUtils(final Context context, String title, EditText editText){
        this.context = context;
        this.title = title;
        this.editText = editText;
        shown = false;

        if(listner==null) {
            listner = new ButtonClick(context) {
                private Context mcontex = context;

                @Override
                public void onYesNoClick(final boolean yes) {
                    if (yes) {
                        KeyboardUtils.hideKeyboard((Activity)context);
                        editDialog.dismiss();
                        shown = false;
                    }else{
                        KeyboardUtils.hideKeyboard((Activity)context);
                        editDialog.dismiss();
                        shown = false;
                    }
                }
            };
        }
        editDialog = new EditDialog(context, title, editText, listner);

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
