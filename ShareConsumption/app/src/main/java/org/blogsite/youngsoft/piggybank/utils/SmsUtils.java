package org.blogsite.youngsoft.piggybank.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.data.CardTableView;
import org.blogsite.youngsoft.tableview.TableDataAdapter;

import static org.blogsite.youngsoft.piggybank.utils.StringUtils.replaceAll;

/**
 * Created by klee on 2018-01-22.
 */

public class SmsUtils {
    public static final long superSerialVersionUID = 20180130L;

    private static SmsUtils instance = null;

    public static final int REQUEST_PERMISSION = 1;
    public static int READ_SMS;
    public static int RECEIVE_SMS;
    public static int WRITE_EXTERNAL_STORAGE;
    public static int READ_EXTERNAL_STORAGE;
    public static int GET_ACCOUNTS;
    public static  int CALL_PHONE;

    public static Typeface typefaceNaumGothic = null;
    public static Typeface typefaceNaumGothicCoding = null;
    public static Typeface typefaceIconFont = null;

    private static Context mContext = null;

    public static String[] permissions =  new String[]{
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.GET_ACCOUNTS,
        Manifest.permission.CALL_PHONE
    };

    private SmsUtils(Context context){
        mContext = context;
        typefaceNaumGothic = Typeface.createFromAsset(context.getAssets(), "fonts/NanumGothic.ttf");
        typefaceNaumGothicCoding = Typeface.createFromAsset(context.getAssets(), "fonts/NanumGothicCoding.ttf");
        typefaceIconFont = Typeface.createFromAsset(context.getAssets(), "fonts/materialdesignicons-webfont.ttf");

        READ_SMS = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS);
        RECEIVE_SMS = ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS);
        WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        GET_ACCOUNTS = ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS);
        CALL_PHONE = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
    }

    public static SmsUtils getInstance(Context context){
        if(instance==null){
            instance = new SmsUtils(context);
        }
        return instance;
    }

    public static SmsUtils getNewInstance(Context context){
        READ_SMS = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS);
        RECEIVE_SMS = ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS);
        WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        GET_ACCOUNTS = ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS);
        CALL_PHONE = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);

        return instance;
    }

    public static String getDevicePath(){
        String path = Environment.getExternalStorageDirectory() + "/";
        return path;
    }

    public static boolean checkSelfPermissionAll(Context context){
        boolean ret = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for(String perms : permissions){
                int perm = ContextCompat.checkSelfPermission(context, perms);
                if(perm== PackageManager.PERMISSION_DENIED){
                    ret =  false;
                    break;
                }else{
                    ret = true;
                }
            }
        }else{
            ret = true;
        }
        return ret;
    }

    public static void readSmsPermission(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int perm = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS);
            if(perm== PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions((Activity)context,
                        new String[]{
                                Manifest.permission.READ_SMS
                        },
                        REQUEST_PERMISSION);
            }
        }
    }

    public static void receiveSmsPermission(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int perm = ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS);
            if(perm== PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions((Activity)context,
                        new String[]{
                                Manifest.permission.RECEIVE_SMS
                        },
                        REQUEST_PERMISSION);
            }
        }
    }

    public static void call_phonePermission(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int perm = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
            if(perm== PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions((Activity)context,
                        new String[]{
                                Manifest.permission.CALL_PHONE
                        },
                        REQUEST_PERMISSION);
            }
        }
    }

    public static void getAccountPermission(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int perm = ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS);
            if(perm== PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions((Activity)context,
                        new String[]{
                                Manifest.permission.GET_ACCOUNTS
                        },
                        REQUEST_PERMISSION);
            }
        }
    }

    public static void writeStoragePermission(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int perm = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(perm== PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions((Activity)context,
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        REQUEST_PERMISSION);
            }
        }
    }

    public static void readStoragePermission(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int perm = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
            if(perm== PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions((Activity)context,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        REQUEST_PERMISSION);
            }
        }
    }

    public static void getAccountStoragePermission(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int perm = ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS);
            if(perm== PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions((Activity)context,
                        new String[]{
                                Manifest.permission.GET_ACCOUNTS
                        },
                        REQUEST_PERMISSION);
            }
        }
    }

    public static void setListViewHeightBasedOnChildren(Context context, ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int count = listAdapter.getCount();
        int totalHeight = 0;
        if(count>0) {
            for (int i = 0; i < count; i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            //totalHeight = totalHeight>200 ? 200 : totalHeight;

            int density = (int) context.getResources().getDisplayMetrics().density;
            int padding = 5;
            int dip2px = padding * density;
            params.height = dip2px + totalHeight + (listView.getDividerHeight() * (listAdapter.getCount()));
        }else{
            params.height = 0;
        }

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void setGridViewHeightBasedOnChildren(Context context, GridView gridView) {
        ListAdapter adapter = gridView.getAdapter();
        if (adapter == null) {
            return;
        }
        int verticalSpacing = gridView.getVerticalSpacing();
        int columns = 2; //gridView.getNumColumns();
        int rows = 0;
        int height = 0;
        rows = (adapter.getCount()/columns) + ((adapter.getCount()%columns) != 0 ? 1 : 0);
        if(rows<1){
            return;
        }
        int h0 = 0;
        for(int i=0; i<rows; i++){
            View v =adapter.getView(i*columns, null, gridView);
            v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            height += v.getMeasuredHeight();
            if(i==0){
                h0 = v.getMeasuredHeight();
            }
        }
        height = height + (verticalSpacing*(rows-1)) + h0/5;

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = height;
        gridView.setLayoutParams(params);
        gridView.requestLayout();
    }

    public static void setTableViewHeightBasedOnChildren(Context context, CardTableView carTableView) {
        TableDataAdapter<?> tableAdapter = carTableView.getDataAdapter();
        if (tableAdapter == null) {
            // pre-condition
            return;
        }
        int count = tableAdapter.getCount();
        ViewGroup.LayoutParams params = carTableView.getLayoutParams();
        int density = (int) context.getResources().getDisplayMetrics().density;
        if(count>0) {
            int padding = 10;
            int dip2px = padding * density;
            int height = dip2px + (density * 26 * count);
            int minH = dip2px + 200 * density;
            if (height < minH) {
                params.height = height;
            } else {
                params.height = minH;
            }
        }else {
            params.height = 26*density;
        }
        carTableView.setLayoutParams(params);
        carTableView.requestLayout();
    }

    public static int getSDKInt() {
        return android.os.Build.VERSION.SDK_INT;
    }

    public static String replaceAmount(String value){
        String tamount = replaceAll(value, ",", "");
        tamount = replaceAll(tamount, "원", "");
        tamount = replaceAll(tamount, "취소완료", "");
        tamount = replaceAll(tamount, ")", "");
        tamount = replaceAll(tamount, "(", "");
        tamount = replaceAll(tamount, "USD", "");
        tamount = replaceAll(tamount, "US$", "");
        tamount = replaceAll(tamount, "금액", "");
        tamount = replaceAll(tamount, " 일시불", "");
        tamount = replaceAll(tamount, "사용합계 ", "");
        tamount = replaceAll(tamount, " 체크", "");
        return tamount;
    }

    public static boolean isNum(String value) {
        String tamount = replaceAmount(value);
        return StringUtils.isIntegerNumber(tamount);
    }

    public static int convertResource(Context context, String resourceName, String  packName){
        return context.getResources().getIdentifier(resourceName, "mipmap", packName);
    }

    public static String getResource(int id){
        return mContext.getResources().getString(id);
    }
}
