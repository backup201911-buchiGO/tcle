package org.blogsite.youngsoft.piggybank.dialog;

import android.content.Context;
import android.view.View;

public class DonationRegistUtils {
    private DonationRegistDialog donationRegistDialog;
    private Context context;

    public DonationRegistUtils(Context context){
        this.context = context;
        donationRegistDialog = new DonationRegistDialog(context);
    }

    public void show(){
        donationRegistDialog.show();
    }

}
