package org.blogsite.youngsoft.piggybank.dialog;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.blogsite.youngsoft.piggybank.activity.MainActivity;
import org.blogsite.youngsoft.piggybank.adapter.PermissionListAdapter;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import java.util.ArrayList;
import java.util.List;
import org.blogsite.youngsoft.piggybank.R;

/**
 * Created by klee on 2018-01-26.
 */

public class PermissionDialog extends Dialog {
    private final int REQUEST_PERMISSION = 1;
    private final Context context;
    private RecyclerView mRecyclerView;
    private int READ_SMS;
    private int RECEIVE_SMS;
    private int WRITE_EXTERNAL_STORAGE;
    private int READ_EXTERNAL_STORAGE;
    private int GET_ACCOUNTS;
    private int CALL_PHONE;


    private TextView mContentView;
    private Button mOKButton;

    private View.OnClickListener mOKButtonClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.permission_dialog);

        TextView titleTextView = (TextView)findViewById(R.id.title);
        titleTextView.setTypeface(SmsUtils.typefaceNaumGothic, Typeface.BOLD);

        // 기기의 뒤로가기 버튼 비활성
        this.setCanceledOnTouchOutside(false);
        this.setCancelable(false);

        init();
        expandableList();

        mOKButton = (Button) findViewById(R.id.permissionOKButton);

        // 클릭 이벤트 셋팅
        if (mOKButtonClickListener!=null) {
            mOKButton.setOnClickListener(mOKButtonClickListener);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                showToast(SmsUtils.getResource(R.string.permission));
                break;
        }

        return true;
    }

    private void showToast(String msg){
        LayoutInflater inflater = getLayoutInflater();
        View toastDesign = inflater.inflate(R.layout.toast_design,
                (ViewGroup) findViewById(R.id.toast_design_root));

        TextView text = toastDesign.findViewById(R.id.TextView_toast_design);
        text.setText(msg); // toast_design.xml 파일에서 직접 텍스트를 지정 가능

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0); // CENTER를 기준으로 0, 0 위치에 메시지 출력
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastDesign);
        toast.show();
    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public PermissionDialog(Context context, View.OnClickListener mOKButtonClickListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.mOKButtonClickListener = mOKButtonClickListener;
    }

    public void init()
    {
        READ_SMS = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS);
        RECEIVE_SMS = ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS);
        WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        GET_ACCOUNTS = ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS);
        CALL_PHONE = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
    }

    private void expandableList()
    {
        mRecyclerView = (RecyclerView)findViewById(R.id.apppermission);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        List<PermissionListAdapter.Item> data = new ArrayList<>();

        String header = context.getResources().getString(R.string.sms_read_permission);
        String description = context.getResources().getString(R.string.sms_read_permission_description);

        PermissionListAdapter.Item read_sms = new PermissionListAdapter.Item(PermissionListAdapter.HEADER, header); //"\uF45B|SMS 보기 권한");
        read_sms.invisibleChildren = new ArrayList<>();
        read_sms.invisibleChildren.add(new PermissionListAdapter.Item(PermissionListAdapter.CHILD, description));


        header = context.getResources().getString(R.string.storage_permission);
        description = context.getResources().getString(R.string.storage_permission_description);
        PermissionListAdapter.Item storage = new PermissionListAdapter.Item(PermissionListAdapter.HEADER, header);
        storage.invisibleChildren = new ArrayList<>();
        //storage.invisibleChildren.add(new PermissionListAdapter.Item(PermissionListAdapter.CHILD, "저장 장치 접근"));
        storage.invisibleChildren.add(new PermissionListAdapter.Item(PermissionListAdapter.CHILD, description));

        header = context.getResources().getString(R.string.receive_sms_permission);
        description = context.getResources().getString(R.string.receive_sms_permission_description);
        PermissionListAdapter.Item receive_sms = new PermissionListAdapter.Item(PermissionListAdapter.HEADER, header);
        receive_sms.invisibleChildren = new ArrayList<>();
        //receive_sms.invisibleChildren.add(new PermissionListAdapter.Item(PermissionListAdapter.CHILD, "SMS 수신"));
        receive_sms.invisibleChildren.add(new PermissionListAdapter.Item(PermissionListAdapter.CHILD, description));

        header = context.getResources().getString(R.string.receive_sms_contact);
        description = context.getResources().getString(R.string.receive_contact_description);
        PermissionListAdapter.Item contact = new PermissionListAdapter.Item(PermissionListAdapter.HEADER, header);
        contact.invisibleChildren = new ArrayList<>();
        contact.invisibleChildren.add(new PermissionListAdapter.Item(PermissionListAdapter.CHILD, description));

        header = context.getResources().getString(R.string.call_permission);
        description = context.getResources().getString(R.string.call_permission_description);
        PermissionListAdapter.Item call = new PermissionListAdapter.Item(PermissionListAdapter.HEADER, header);
        call.invisibleChildren = new ArrayList<>();
        call.invisibleChildren.add(new PermissionListAdapter.Item(PermissionListAdapter.CHILD, description));

        data.add(read_sms);
        data.add(storage);
        data.add(receive_sms);
        data.add(call);
        data.add(contact);

        mRecyclerView.setAdapter(new PermissionListAdapter(SmsUtils.typefaceIconFont, data));
    }

    private void applyPermission(){
        // Permission Check
        if (Build.VERSION.SDK_INT >= 23) {
            if (READ_SMS != PackageManager.PERMISSION_GRANTED &&
                    WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED && READ_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED &&
                    RECEIVE_SMS != PackageManager.PERMISSION_GRANTED && GET_ACCOUNTS != PackageManager.PERMISSION_GRANTED)
            {
                // Permissions are not granted yet
                grantPermission();
            }
        }
    }

    protected void grantPermission()
    {
        ActivityCompat.requestPermissions(new MainActivity(),
                new String[]{
                Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.GET_ACCOUNTS
                },
                REQUEST_PERMISSION);
    }
}
