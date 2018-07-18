package org.blogsite.youngsoft.piggybank.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.crypt.SecureHash;
import org.blogsite.youngsoft.piggybank.setting.PBSettings;
import org.blogsite.youngsoft.piggybank.utils.IButtonClickListner;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

public class PasswordNumpadDialog  extends Dialog {
    private final Context context;
    private TextView titleTextView;
    private EditText orgpwd;
    private EditText pwd1;
    private EditText pwd2;
    private TextView textView;
    private IButtonClickListner listner;
    private Button mOKButton;
    private Button mCancelButton;
    private Button btnshowpwd;
    private boolean shownpwd = false;
    private String title;
    private String p0 = "";
    private String p1 = "";
    private String p2 = "";

    private TableRow authmsgrow;

    private boolean modifyPwd = false;

    private PBSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.password_num_dialog);

        TextView des = (TextView)findViewById(R.id.edit_title1);
        String s = SmsUtils.getResource(R.string.authnum_msg_1) + " <b><font color=\"0000ff\">"
                + SmsUtils.getResource(R.string.authnum_msg_2) + "</font></b>" + SmsUtils.getResource(R.string.authnum_msg_3);
        des.setText(Html.fromHtml(s));

        titleTextView = (TextView)findViewById(R.id.edit_title);
        titleTextView.setTypeface(SmsUtils.typefaceNaumGothic, Typeface.BOLD);
        titleTextView.setText(title);

        authmsgrow = (TableRow)findViewById(R.id.authmsgrow);
        authmsgrow.setVisibility(View.GONE);

        orgpwd = (EditText)findViewById(R.id.orgpwd);
        orgpwd.setTypeface(SmsUtils.typefaceNaumGothicCoding);
        orgpwd.setInputType(InputType.TYPE_CLASS_NUMBER);

        pwd1 = (EditText)findViewById(R.id.pwd1);
        pwd1.setTypeface(SmsUtils.typefaceNaumGothicCoding);
        pwd1.setInputType(InputType.TYPE_CLASS_NUMBER);

        pwd2 = (EditText)findViewById(R.id.pwd2);
        pwd2.setTypeface(SmsUtils.typefaceNaumGothicCoding);
        pwd2.setInputType(InputType.TYPE_CLASS_NUMBER);

        TableRow row = (TableRow)findViewById(R.id.pwdrow);
        if(modifyPwd){
            row.setVisibility(View.VISIBLE);
            orgpwd.setEnabled(true);
        }else{
            pwd1.setEnabled(true);
            row.setVisibility(View.GONE);
        }

        mOKButton = (Button) findViewById(R.id.OKButton);
        mOKButton.setEnabled(false);
        mOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s0 = "";
                String s1 = "";
                String s2 = "";
                if(modifyPwd){
                    p0 = orgpwd.getText().toString();
                    s0 = getSha256(p0);
                }
                p1 = pwd1.getText().toString();
                s1 = getSha256(p1);
                p2 = pwd2.getText().toString();
                s2 = getSha256(p2);
                if(!modifyPwd) {
                    if (s1.equals(s2)) {
                        textView.setText("******");
                        textView.invalidate();
                        settings.setRemittance_pwd(s1);
                        listner.onYesNoClick(true);
                    } else {
                        final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(1000);
                        authmsgrow.setVisibility(View.VISIBLE);
                    }
                }else{
                    String opwd = settings.getRemittance_pwd();
                    if (opwd.equals(s1) && s0.equals(s1) && s1.equals(s2)) {
                        textView.setText(pwd1.getText().toString());
                        textView.invalidate();
                        settings.setRemittance_pwd(s1);
                        listner.onYesNoClick(true);
                    } else {
                        final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(1000);
                        authmsgrow.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        mCancelButton = (Button) findViewById(R.id.cancelButton);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onYesNoClick(false);
            }
        });

        btnshowpwd = (Button)findViewById(R.id.btnshowpwd);
        btnshowpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shownpwd){
                    orgpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    pwd1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    pwd2.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    btnshowpwd.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_visibility_white_24dp, 0, 0, 0);
                    btnshowpwd.setText(R.string.showpwd);
                }else{
                    orgpwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    pwd1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    pwd2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                    btnshowpwd.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_visibility_off_white_24dp, 0, 0, 0);
                    btnshowpwd.setText(R.string.hidepwd);
                }
                shownpwd = !shownpwd;
            }
        });

        orgpwd.addTextChangedListener(new TextWatcher() {
            @Override /* TODO Auto-generated method stub */
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* Text 변경 전 문자열 출력
                 * CharSequence s : 현재 에디트텍스트에 입력된 문자열을 담고 있다.
                 * int start : s 에 저장된 문자열 내에 새로 추가될 문자열의 위치값을 담고있다.
                 * int count : s 에 담긴 문자열 가운데 새로 사용자가 입력할 문자열에 의해 변경될 문자열의 수가 담겨있다.
                 * int after : 새로 추가될 문자열의 수
                 */
            }

            @Override /* TODO Auto-generated method stub */
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /* Text가 변경 중 일때 호출되는 함수
                 * CharSequence s : 사용자가 새로 입력한 문자열을 포함한 에디트텍스트의 문자열이 들어있음
                 * int start : 새로 추가된 문자열의 시작 위치의 값
                 * int before : 새 문자열 대신 삭제된 기존 문자열의 수가 들어 있다
                 * int count : 새로 추가된 문자열의 수가 들어있다.
                 */

                String a = s.toString();
                if(a.length()==6){
                    pwd1.setEnabled(true);
                    pwd1.requestFocus();
                }else{
                    pwd1.setEnabled(false);
                }
            }

            @Override /* TODO Auto-generated method stub */
            public void afterTextChanged(Editable s) {
                authmsgrow.setVisibility(View.GONE);

            }
        });

        pwd1.addTextChangedListener(new TextWatcher() {
            @Override /* TODO Auto-generated method stub */
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* Text 변경 전 문자열 출력
                 * CharSequence s : 현재 에디트텍스트에 입력된 문자열을 담고 있다.
                 * int start : s 에 저장된 문자열 내에 새로 추가될 문자열의 위치값을 담고있다.
                 * int count : s 에 담긴 문자열 가운데 새로 사용자가 입력할 문자열에 의해 변경될 문자열의 수가 담겨있다.
                 * int after : 새로 추가될 문자열의 수
                 */
            }

            @Override /* TODO Auto-generated method stub */
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /* Text가 변경 중 일때 호출되는 함수
                 * CharSequence s : 사용자가 새로 입력한 문자열을 포함한 에디트텍스트의 문자열이 들어있음
                 * int start : 새로 추가된 문자열의 시작 위치의 값
                 * int before : 새 문자열 대신 삭제된 기존 문자열의 수가 들어 있다
                 * int count : 새로 추가된 문자열의 수가 들어있다.
                 */

                String a = s.toString();
                if(a.length()==6){
                    pwd2.setEnabled(true);
                    pwd2.requestFocus();
                }else{
                    pwd2.setEnabled(false);
                }
            }

            @Override /* TODO Auto-generated method stub */
            public void afterTextChanged(Editable s) {
                /* Text가 변경이 되었을 경우 호출되는 함수 */
                authmsgrow.setVisibility(View.GONE);
            }
        });

        pwd2.addTextChangedListener(new TextWatcher() {
            @Override /* TODO Auto-generated method stub */
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* Text 변경 전 문자열 출력
                 * CharSequence s : 현재 에디트텍스트에 입력된 문자열을 담고 있다.
                 * int start : s 에 저장된 문자열 내에 새로 추가될 문자열의 위치값을 담고있다.
                 * int count : s 에 담긴 문자열 가운데 새로 사용자가 입력할 문자열에 의해 변경될 문자열의 수가 담겨있다.
                 * int after : 새로 추가될 문자열의 수
                 */
            }

            @Override /* TODO Auto-generated method stub */
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /* Text가 변경 중 일때 호출되는 함수
                 * CharSequence s : 사용자가 새로 입력한 문자열을 포함한 에디트텍스트의 문자열이 들어있음
                 * int start : 새로 추가된 문자열의 시작 위치의 값
                 * int before : 새 문자열 대신 삭제된 기존 문자열의 수가 들어 있다
                 * int count : 새로 추가된 문자열의 수가 들어있다.
                 */

                String a = s.toString();
                if(a.length()==6){
                    mOKButton.setEnabled(true);
                    mOKButton.requestFocus();
                }else{
                    mOKButton.setEnabled(false);
                }
            }

            @Override /* TODO Auto-generated method stub */
            public void afterTextChanged(Editable s) {
                /* Text가 변경이 되었을 경우 호출되는 함수 */
                authmsgrow.setVisibility(View.GONE);
            }
        });
    }


    public PasswordNumpadDialog(Context context, String title, TextView textView, IButtonClickListner listner){
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.title = title;
        this.textView = textView;
        this.listner = listner;
    }

    public void setModifyPwd(boolean modifyPwd){
        this.modifyPwd = modifyPwd;
    }

    public void setSettings(PBSettings settings){
        this.settings = settings;
    }

    private String getSha256(String s){
        return SecureHash.getHashHex(SecureHash.SHA256, s);
    }
}
