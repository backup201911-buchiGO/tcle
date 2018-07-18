package org.blogsite.youngsoft.piggybank.dialog;

import android.content.Context;
import android.view.View;

public class PermissionUtils {
    private PermissionDialog mPermissionDialog;
    private Context context;

    public PermissionUtils(Context context, View.OnClickListener listner){
        this.context = context;
        mPermissionDialog = new PermissionDialog(context, listner);
    }

    public void show(){
        mPermissionDialog.show();
    }

}
