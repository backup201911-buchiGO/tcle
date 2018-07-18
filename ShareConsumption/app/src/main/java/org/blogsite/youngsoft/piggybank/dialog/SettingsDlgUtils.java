package org.blogsite.youngsoft.piggybank.dialog;

import android.content.Context;
import android.view.View;

/**
 * Created by klee on 2018-02-03.
 */

public class SettingsDlgUtils {
    private SettingsDialog mSettingsDialog;
    private Context context;

    public SettingsDlgUtils(Context context){
        this.context = context;
        mSettingsDialog = new SettingsDialog(context, buttonListener);
    }

    public void show(){
        mSettingsDialog.show();
    }

    private View.OnClickListener buttonListener = new View.OnClickListener() {
        public void onClick(View v) {
            mSettingsDialog.dismiss();
        }
    };
}
