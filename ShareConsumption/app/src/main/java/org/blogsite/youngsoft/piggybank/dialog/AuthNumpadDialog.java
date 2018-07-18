package org.blogsite.youngsoft.piggybank.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.crypt.SecureHash;
import org.blogsite.youngsoft.piggybank.setting.PBSettings;
import org.blogsite.youngsoft.piggybank.utils.IButtonClickListner;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.w3c.dom.Text;

public class AuthNumpadDialog  extends Dialog {
    private final Context context;
    private TextView titleTextView;
    private IButtonClickListner listner;
    private Button mOKButton;
    private Button mCancelButton;
    private String title;

    private String password;
    private TableRow row;

    private ImageView n0;
    private ImageView n1;
    private ImageView n2;
    private ImageView n3;
    private ImageView n4;
    private ImageView n5;
    private ImageView n6;
    private ImageView n7;
    private ImageView n8;
    private ImageView n9;
    private ImageView nback;

    private ImageView toggle;

    private ImageView pimg[];
    private int pres[];

    private String input_val[];
    private int input_count = 0;
    private View.OnTouchListener touchListener;

    private boolean num_show = false;

    private boolean isOK = false;
    private String pwd = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.auth_num_dialog);

        TextView des = (TextView)findViewById(R.id.edit_title1);
        String s = SmsUtils.getResource(R.string.authnum_msg_1) + " <b><font color=\"0000ff\">"
                + SmsUtils.getResource(R.string.authnum_msg_2) + "</font></b>" + SmsUtils.getResource(R.string.authnum_msg_3);
        des.setText(Html.fromHtml(s));

        row = (TableRow)findViewById(R.id.authmsgrow);
        row.setVisibility(View.GONE);

        pimg = new ImageView[6];
        pres = new int[6];
        for(int i=0; i<pres.length; i++){
            pres[i] = -1;
        }
        input_val = new String[6];
        input_count = 0;
        touchListener = new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    // if pressed
                    case MotionEvent.ACTION_DOWN: {
                        /* 터치하고 있는 상태 */
                        v.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                        row.setVisibility(View.GONE);
                        int id = v.getId();
                        if(input_count<6) {
                            switch (id) {
                                case R.id.num0: {
                                    input_val[input_count] = "0";
                                    if(num_show) {
                                        pres[input_count] = R.drawable.num0;
                                    }else{
                                        pres[input_count] = R.drawable.hide_num_sym;
                                    }
                                    pimg[input_count].setImageResource(pres[input_count]);
                                    input_count++;
                                    break;
                                }
                                case R.id.num1: {
                                    input_val[input_count] = "1";
                                    pres[input_count] = R.drawable.num1;
                                    if(num_show) {
                                        pimg[input_count].setImageResource(R.drawable.num1);
                                    }else{
                                        pimg[input_count].setImageResource(R.drawable.hide_num_sym);
                                    }
                                    input_count++;
                                    break;
                                }
                                case R.id.num2: {
                                    input_val[input_count] = "2";
                                    pres[input_count] = R.drawable.num2;
                                    if(num_show) {
                                        pimg[input_count].setImageResource(R.drawable.num2);
                                    }else{
                                        pimg[input_count].setImageResource(R.drawable.hide_num_sym);
                                    }
                                    input_count++;
                                    break;
                                }
                                case R.id.num3: {
                                    input_val[input_count] = "3";
                                    pres[input_count] = R.drawable.num3;
                                    if(num_show) {
                                        pimg[input_count].setImageResource(R.drawable.num3);
                                    }else{
                                        pimg[input_count].setImageResource(R.drawable.hide_num_sym);
                                    }
                                    input_count++;
                                    break;
                                }
                                case R.id.num4: {
                                    input_val[input_count] = "4";
                                    pres[input_count] = R.drawable.num4;
                                    if(num_show) {
                                        pimg[input_count].setImageResource(R.drawable.num4);
                                    }else{
                                        pimg[input_count].setImageResource(R.drawable.hide_num_sym);
                                    }
                                    input_count++;
                                    break;
                                }
                                case R.id.num5: {
                                    input_val[input_count] = "5";
                                    pres[input_count] = R.drawable.num5;
                                    if(num_show) {
                                        pimg[input_count].setImageResource(R.drawable.num5);
                                    }else{
                                        pimg[input_count].setImageResource(R.drawable.hide_num_sym);
                                    }
                                    input_count++;
                                    break;
                                }
                                case R.id.num6: {
                                    input_val[input_count] = "6";
                                    pres[input_count] = R.drawable.num6;
                                    if(num_show) {
                                        pimg[input_count].setImageResource(R.drawable.num6);
                                    }else{
                                        pimg[input_count].setImageResource(R.drawable.hide_num_sym);
                                    }
                                    input_count++;
                                    break;
                                }
                                case R.id.num7: {
                                    input_val[input_count] = "7";
                                    pres[input_count] = R.drawable.num7;
                                    if(num_show) {
                                        pimg[input_count].setImageResource(R.drawable.num7);
                                    }else{
                                        pimg[input_count].setImageResource(R.drawable.hide_num_sym);
                                    }
                                    input_count++;
                                    break;
                                }
                                case R.id.num8: {
                                    input_val[input_count] = "8";
                                    pres[input_count] = R.drawable.num8;
                                    if(num_show) {
                                        pimg[input_count].setImageResource(R.drawable.num8);
                                    }else{
                                        pimg[input_count].setImageResource(R.drawable.hide_num_sym);
                                    }
                                    input_count++;
                                    break;
                                }
                                case R.id.num9: {
                                    input_val[input_count] = "9";
                                    pres[input_count] = R.drawable.num9;
                                    if(num_show) {
                                        pimg[input_count].setImageResource(R.drawable.num9);
                                    }else{
                                        pimg[input_count].setImageResource(R.drawable.hide_num_sym);
                                    }
                                    input_count++;
                                    break;
                                }
                                case R.id.toggle : {
                                    num_show = !num_show;
                                    if(num_show){
                                        toggle.setImageResource(R.drawable.hide_num);
                                        for(int i=0; i<input_count; i++){
                                            if(input_val[i]!=null && pres[i]!=-1){
                                                pimg[i].setImageResource(pres[i]);
                                            }
                                        }
                                    }else{
                                        toggle.setImageResource(R.drawable.show_num);
                                        for(int i=0; i<input_count; i++){
                                            if(input_val[i]!=null && pres[i]!=-1) {
                                                pimg[i].setImageResource(R.drawable.hide_num_sym);
                                            }
                                        }
                                    }
                                    break;
                                }
                                case R.id.back: {
                                    input_count--;
                                    input_count = input_count<0 ? 0 : input_count;
                                    input_val[input_count] = null;
                                    pimg[input_count].setImageResource(R.drawable.num_bg);
                                    break;
                                }
                            }
                        }

                        if(input_count==6 ){
                            if(id==R.id.back) {
                                input_count--;
                                input_count = input_count < 0 ? 0 : input_count;
                                input_val[input_count] = null;
                                pimg[input_count].setImageResource(R.drawable.num_bg);
                            }
                            if(id==R.id.toggle){
                                num_show = !num_show;
                                if(num_show){
                                    toggle.setImageResource(R.drawable.hide_num);
                                    for(int i=0; i<input_count; i++){
                                        if(input_val[i]!=null && pres[i]!=-1){
                                            pimg[i].setImageResource(pres[i]);
                                        }
                                    }
                                }else{
                                    toggle.setImageResource(R.drawable.show_num);
                                    for(int i=0; i<6; i++){
                                        if(input_val[i]!=null && pres[i]!=-1) {
                                            pimg[i].setImageResource(R.drawable.hide_num_sym);
                                        }
                                    }
                                }
                            }
                        }

                        if(input_count==6){
                            mOKButton.setEnabled(true);
                            mOKButton.requestFocus();
                        }else{
                            mOKButton.setEnabled(false);
                        }
                        break;
                    }

                    // if released
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: {
                        /* 터치가 안되고 있는 상태 */
                        break;
                    }

                    default: {
                        break;
                    }
                }

                return false; // false : 이 OnTouchListener 이후에도
                //         다른 Listener들이 동작하게 함
                //         (OnClickListener 등등)
            }
        };

        n0 = (ImageView)findViewById(R.id.num0);
        n0.setOnTouchListener(touchListener);

        n1 = (ImageView)findViewById(R.id.num1);
        n1.setOnTouchListener(touchListener);

        n2 = (ImageView)findViewById(R.id.num2);
        n2.setOnTouchListener(touchListener);

        n3 = (ImageView)findViewById(R.id.num3);
        n3.setOnTouchListener(touchListener);

        n4 = (ImageView)findViewById(R.id.num4);
        n4.setOnTouchListener(touchListener);

        n5 = (ImageView)findViewById(R.id.num5);
        n5.setOnTouchListener(touchListener);

        n6 = (ImageView)findViewById(R.id.num6);
        n6.setOnTouchListener(touchListener);

        n7 = (ImageView)findViewById(R.id.num7);
        n7.setOnTouchListener(touchListener);

        n8 = (ImageView)findViewById(R.id.num8);
        n8.setOnTouchListener(touchListener);

        n9 = (ImageView)findViewById(R.id.num9);
        n9.setOnTouchListener(touchListener);

        nback = (ImageView)findViewById(R.id.back);
        nback.setOnTouchListener(touchListener);

        toggle = (ImageView)findViewById(R.id.toggle);
        toggle.setImageResource(R.drawable.show_num);
        toggle.setOnTouchListener(touchListener);

        pimg[0] = (ImageView)findViewById(R.id.pwd0);
        pimg[0].setImageResource(R.drawable.num_bg);
        pimg[1] = (ImageView)findViewById(R.id.pwd1);
        pimg[1].setImageResource(R.drawable.num_bg);
        pimg[2] = (ImageView)findViewById(R.id.pwd2);
        pimg[2].setImageResource(R.drawable.num_bg);
        pimg[3] = (ImageView)findViewById(R.id.pwd3);
        pimg[3].setImageResource(R.drawable.num_bg);
        pimg[4] = (ImageView)findViewById(R.id.pwd4);
        pimg[4].setImageResource(R.drawable.num_bg);
        pimg[5] = (ImageView)findViewById(R.id.pwd5);
        pimg[5].setImageResource(R.drawable.num_bg);

        titleTextView = (TextView)findViewById(R.id.edit_title);
        titleTextView.setTypeface(SmsUtils.typefaceNaumGothic, Typeface.BOLD);
        titleTextView.setText(title);

        mOKButton = (Button) findViewById(R.id.OKButton);
        mOKButton.setEnabled(false);
        mOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwd = "";
                for(String s : input_val){
                    pwd += s;
                }
                String hashedPwd = getSha256(pwd);
                if(password.equals(hashedPwd)) {
                    isOK = true;
                    listner.onYesNoClick(true);
                }else{
                    row.setVisibility(View.VISIBLE);
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
    }

    public AuthNumpadDialog(Context context, String title, IButtonClickListner listner){
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.title = title;
        this.listner = listner;
    }

    public String getValue(){
        return pwd;
    }

    public boolean getPassed(){
        return isOK;
    }

    public void setPassword(String password){
        this.password = password;
    }

    private String getSha256(String s){
        return SecureHash.getHashHex(SecureHash.SHA256, s);
    }
}
