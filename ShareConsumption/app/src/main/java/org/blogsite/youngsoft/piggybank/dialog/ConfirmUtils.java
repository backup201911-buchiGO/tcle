package org.blogsite.youngsoft.piggybank.dialog;

import android.content.Context;

import org.blogsite.youngsoft.piggybank.utils.ButtonClick;
import org.blogsite.youngsoft.piggybank.utils.IButtonClickListner;


/**
 * Created by klee on 2018-02-03.
 */

public class ConfirmUtils {
    private ConfirmDialog confirmDialog;
    private IButtonClickListner listner;
    private Context context;
    private String title;
    private String msg;
    private boolean html = false;

    private boolean shown = false;

    public ConfirmUtils(final Context context, String title, String msg){
        this.context = context;
        this.title = title;
        this.msg = msg;
        shown = false;

        if(listner==null) {
            listner = new ButtonClick(context) {
                private Context mcontex = context;

                @Override
                public void onYesNoClick(final boolean yes) {
                    if (yes) {
                        confirmDialog.dismiss();
                        shown = false;
                    }
                }
            };
        }
        confirmDialog = new ConfirmDialog(context, title, msg, listner, html);
    }

    public void setHtml(boolean html){
        this.html = html;
    }

    public void setListner(IButtonClickListner listner) {
        this.listner = listner;
    }

    public void show(){
        if(confirmDialog!=null && !shown) {
            confirmDialog.show();
            shown = true;
        }
    }

    public void hide(){
        confirmDialog.dismiss();
    }

    public boolean isShown() {
        return shown;
    }
}
