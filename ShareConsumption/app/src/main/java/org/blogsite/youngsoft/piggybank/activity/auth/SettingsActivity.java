package org.blogsite.youngsoft.piggybank.activity.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.crypt.Key;
import org.blogsite.youngsoft.piggybank.crypt.RSAUtils;
import org.blogsite.youngsoft.piggybank.db.DBUtils;
import org.blogsite.youngsoft.piggybank.db.DataBaseHelper;
import org.blogsite.youngsoft.piggybank.dialog.ConfirmUtils;
import org.blogsite.youngsoft.piggybank.dialog.PasswordNumUtils;
import org.blogsite.youngsoft.piggybank.dialog.SettingsDlgUtils;
import org.blogsite.youngsoft.piggybank.dialog.SettingsEditUtils;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.other.CircleTransform;
import org.blogsite.youngsoft.piggybank.setting.DonationList;
import org.blogsite.youngsoft.piggybank.setting.Donations;
import org.blogsite.youngsoft.piggybank.setting.PBSettings;
import org.blogsite.youngsoft.piggybank.setting.PBSettingsUtils;
import org.blogsite.youngsoft.piggybank.utils.Alert;
import org.blogsite.youngsoft.piggybank.utils.KeyboardUtils;
import org.blogsite.youngsoft.piggybank.utils.ObjLoader;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SettingsActivity  extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SettingsActivity";

    private static final NumberFormat PRICE_FORMATTER = NumberFormat.getNumberInstance();

    //파이어베이스 실시간 데이터베이스
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mConditionRef = mDatabase.child("PiggyBank").child("Rules").child("Donations");

    private ProgressDialog mProgressDialog;

    private Context context;
    private PBSettings settings;
    private boolean selecteddonation = false;

    private Calendar cal;
    private int lastDay;

    private TextView lbloveralldesc;
    private TextView donationday;

    private Button applySettings;
    private Button pwdsetting;
    private Button select;

    private TextView username;
    private TextView useremail;
    private ImageView mImageProfile;

    private static final int RC_SELECT_DONATION = 9000;
    private static final int RC_SIGN_IN = 9001;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private GoogleSignInClient mGoogleSignInClient;

    private TextView threshold;
    private TextView percent;
    private TextView www;
    private TextView tel;

    private TextView pwd;

    private TextView debugdesc;

    private final DonationList donationList = new DonationList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        threshold = (TextView)findViewById(R.id.threshold);
        threshold.setTypeface(SmsUtils.typefaceNaumGothicCoding);

        threshold.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    threshold.requestFocus();
                    SettingsEditUtils edit = new SettingsEditUtils(context, getString(R.string.threshold_edit_title), threshold );
                    edit.show();
                    KeyboardUtils.hideKeyboard((Activity)context);
                }
                return false;
            }
        });

        percent = (TextView)findViewById(R.id.percent);
        percent.setTypeface(SmsUtils.typefaceNaumGothicCoding);
        percent.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    percent.requestFocus();
                    SettingsEditUtils edit = new SettingsEditUtils(context, getString(R.string.percent_edit_title), percent );
                    edit.show();
                    KeyboardUtils.hideKeyboard((Activity)context);
                }
                return false;
            }
        });

        www = (TextView)findViewById(R.id.donation_www);
        www.setTypeface(SmsUtils.typefaceNaumGothic);
        www.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    if(www.getText().length()!=0){
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.parse(www.getText().toString());
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }
                return false;
            }
        });

        tel = (TextView)findViewById(R.id.donation_tel);
        tel.setTypeface(SmsUtils.typefaceNaumGothic);
        tel.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    if(tel.getText().length()!=0){
                        try {
                            String phone = "tel:" + tel.getText().toString().trim();
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse(phone));
                            startActivity(intent);
                        }catch (Exception e){
                            PGLog.e(TAG, StackTrace.getStackTrace(e));
                        }
                    }
                }
                return false;
            }
        });

        initSettings();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initSettings(){
        try {
            cal = Calendar.getInstance();
            lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            settings = new PBSettings();

            if (DBUtils.checkSettings(context)) {
                String str = DBUtils.getSettings(context);
                try {
                    settings = (PBSettings) ObjLoader.loadObjectFromBase64(str);
                } catch (IOException ioe) {
                    // 데이터베이스 파일이 없는 경우

                } catch (Exception e) {
                    PGLog.e(TAG, StackTrace.getStackTrace(e));
                }
            }

            pwd = (TextView) findViewById(R.id.pwd);
            pwd.setTypeface(SmsUtils.typefaceNaumGothicCoding);
            if(!"".equals(settings.getRemittance_pwd())){
                pwd.setText("******");
            }

            username = findViewById(R.id.username);
            username.setTypeface(SmsUtils.typefaceNaumGothic);
            useremail = findViewById(R.id.useremail);
            useremail.setTypeface(SmsUtils.typefaceNaumGothic);
            mImageProfile = (ImageView) findViewById(R.id.img_profile);
            mImageProfile.setOnClickListener(this);

            applySettings = (Button) findViewById(R.id.applySettings);
            applySettings.setOnClickListener(this);

            pwdsetting = (Button)findViewById(R.id.pwdsetting);
            if(settings!=null && !"".equals(settings.getRemittance_pwd())){
                pwdsetting.setText(R.string.setting_lblchangepwd);
            }
            pwdsetting.setOnClickListener(this);

            select = (Button) findViewById(R.id.select_donation);
            select.setOnClickListener(this);

            lbloveralldesc = (TextView) findViewById(R.id.lbloveralldesc);
            lbloveralldesc.setTypeface(SmsUtils.typefaceNaumGothic);
            donationday = (TextView) findViewById(R.id.donationday);
            donationday.setTypeface(SmsUtils.typefaceNaumGothicCoding);

            username.setText(settings.getUserName());

            useremail.setText(settings.getUserEmail());

            String v = "<font color=\"#F36164\">" + PRICE_FORMATTER.format(settings.getThreshold()) + " 원</font>" + getResString(R.string.input_tag);
            threshold.setText(Html.fromHtml(v));

            v = "<font color=\"#F36164\">" + String.valueOf(settings.getPercent()) + " %</font>" + getResString(R.string.input_tag);
            percent.setText(Html.fromHtml(v));

            displayDonationInfo();

            Switch overall = (Switch) findViewById(R.id.overall);
            boolean val = settings.isThresholdOverall();
            overall.setChecked(val);

            overall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ConfirmUtils confirmUtils = new ConfirmUtils(context, getResString(R.string.threshold_title), getResString(R.string.overallon));
                        confirmUtils.show();
                        lbloveralldesc.setText(R.string.overallon);
                    } else {
                        ConfirmUtils confirmUtils = new ConfirmUtils(context, getResString(R.string.threshold_title), getResString(R.string.overalloff));
                        confirmUtils.show();
                        lbloveralldesc.setText(R.string.overalloff);
                    }
                    settings.setThresholdOverall(isChecked);
                }
            });


            if (val) {
                lbloveralldesc.setText(R.string.overallon);
            } else {
                lbloveralldesc.setText(R.string.overalloff);
            }

            Switch lastday = (Switch) findViewById(R.id.lastday);
            boolean val_lastday = settings.getDonationDay() > 0 ? false : true;
            lastday.setChecked(val_lastday);


            lastday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ConfirmUtils confirmUtils = new ConfirmUtils(context, getResString(R.string.donation_title), getResString(R.string.donationdayon));
                        confirmUtils.show();
                        donationday.setEnabled(false);
                    } else {
                        ConfirmUtils confirmUtils = new ConfirmUtils(context, getResString(R.string.donation_title), getResString(R.string.donationdayoff));
                        confirmUtils.show();
                        donationday.setEnabled(true);
                    }
                }
            });

            if (val_lastday) {
                v = "<font color=\"#F36164\">" + String.valueOf(lastDay) + " 일</font>" + getResString(R.string.input_tag);
                donationday.setText(Html.fromHtml(v));
                donationday.setEnabled(false);
            } else {
                donationday.setEnabled(true);
            }

            debugdesc = (TextView) findViewById(R.id.lbldebugdesc);
            boolean debug = settings.isDebug();
            Switch debug_switch = (Switch) findViewById(R.id.debug);
            debug_switch.setChecked(debug);
            if (debug) {
                debugdesc.setTypeface(SmsUtils.typefaceNaumGothic, Typeface.NORMAL);
            } else {
                debugdesc.setTypeface(SmsUtils.typefaceNaumGothic, Typeface.BOLD);
            }

            debug_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        debugdesc.setTypeface(SmsUtils.typefaceNaumGothic, Typeface.NORMAL);
                    } else {
                        debugdesc.setTypeface(SmsUtils.typefaceNaumGothic, Typeface.BOLD);
                    }
                }
            });

            donationday.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction()==MotionEvent.ACTION_DOWN) {
                        donationday.requestFocus();
                        SettingsEditUtils edit = new SettingsEditUtils(context, getString(R.string.donation_title), donationday );
                        edit.show();
                        KeyboardUtils.hideKeyboard((Activity)context);
                    }
                    return false;
                }
            });

            // [START config_signin]
            // Configure Google Sign In

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            // [END config_signin]

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            // [START initialize_auth]
            mAuth = FirebaseAuth.getInstance();
            // [END initialize_auth]
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();

        mConditionRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            String key = dataSnapshot.getKey();
                            ArrayList<HashMap<String, String>> donations = (ArrayList<HashMap<String, String>>) dataSnapshot.getValue();

                            for (HashMap<String, String> map : donations) {
                                if(!"".equals(map.get("BankName"))) {
                                    String category = map.get("Category");
                                    Donations donation = new Donations(context);
                                    donation.setAccount(map.get("Account"));
                                    donation.setAddress(map.get("Address"));
                                    donation.setBankname(map.get("BankName"));
                                    donation.setCategory(category);
                                    donation.setHome(map.get("Home"));
                                    donation.setName(map.get("Name"));
                                    donation.setTel(map.get("Tel"));
                                    donation.setResourceName(map.get("Resource"));
                                    if(getString(R.string.catagory_welfare).equals(category)) { //복지
                                        donation.setResId(R.mipmap.welfare);
                                    }else if(getString(R.string.catagory_christian).equals(category)){ // 기독교
                                        donation.setResId(R.mipmap.christian);
                                    }else if(getString(R.string.catagory_buddhism).equals(category)) { // 불교
                                        donation.setResId(R.mipmap.buddhism);
                                    }else if(getString(R.string.catagory_catholic).equals(category)){ // 천주교
                                        donation.setResId(R.mipmap.catholic);
                                    }else if(getString(R.string.catagory_policy).equals(category)){ // 정치
                                        donation.setResId(R.mipmap.catholic);
                                    }else if(getString(R.string.catagory_sicial).equals(category)){ // 사회
                                        donation.setResId(R.mipmap.social);
                                    }else{ // 기타
                                        donation.setResId(R.mipmap.etc);
                                    }

                                    donationList.setDonations(donation.getCategory(), donation);
                                }
                            }
                            settings.setDonationList(donationList);
                            PBSettingsUtils.getInstance().setSettings(settings);
                        }catch (Exception e){
                            PGLog.e(TAG, StackTrace.getStackTrace(e));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    }
    // [END on_start_check_user]

    private void displayDonationInfo(){
        try {
            TextView donation_name = (TextView) findViewById(R.id.donation_name);
            donation_name.setTypeface(SmsUtils.typefaceNaumGothic);
            TextView donation_bank = (TextView) findViewById(R.id.donation_bank);
            donation_bank.setTypeface(SmsUtils.typefaceNaumGothic);
            TextView donation_account = (TextView) findViewById(R.id.donation_account);
            donation_account.setTypeface(SmsUtils.typefaceNaumGothicCoding);
            TextView donation_address = (TextView) findViewById(R.id.donation_address);
            donation_address.setTypeface(SmsUtils.typefaceNaumGothic);
            TextView donation_tel = (TextView) findViewById(R.id.donation_tel);
            donation_tel.setTypeface(SmsUtils.typefaceNaumGothicCoding);
            TextView donation_www = (TextView) findViewById(R.id.donation_www);
            donation_www.setTypeface(SmsUtils.typefaceNaumGothic);
            Donations donations = settings.getDonations();

            if (donations!=null) {
                selecteddonation = true;
                String name = donations.getName();
                String bankName = donations.getBankname();
                String bankAccount = donations.getAccount();
                String address = donations.getAddress();
                String tel = donations.getTel();
                String home = donations.getHome();

                donation_name.setText(name);
                donation_bank.setText(bankName);
                donation_account.setText(bankAccount);
                donation_address.setText(address);
                donation_tel.setText(tel);
                donation_www.setText(home);
            } else {
                donation_name.setText("");
                donation_bank.setText("");
                donation_account.setText("");
                donation_address.setText("");
                donation_tel.setText("");
                donation_www.setText("");
            }
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }


    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                PGLog.w(TAG, StackTrace.getStackTrace(e));
                Alert.showAlert(context, getResString(R.string.error), StackTrace.getStackTrace(e));
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }else if(requestCode == RC_SELECT_DONATION){
            displayDonationInfo();

        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        PGLog.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            PGLog.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            PGLog.w(TAG, "signInWithCredential:failure : " + task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        SmsUtils.getAccountStoragePermission(context);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            username.setText(user.getDisplayName());
            useremail.setText(user.getEmail());

            settings.setPhotoUrl(user.getPhotoUrl().toString());
            Glide.with(this).load(user.getPhotoUrl())
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mImageProfile);
            //mImageProfile.setClickable(false);
        } else {
            username.setText(null);
            useremail.setText(null);
            mImageProfile.setImageResource(R.drawable.googleplus_icon);
            mImageProfile.setClickable(true);
        }
    }

    private String getResString(int resId){
        return getResources().getString(resId);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_profile) {
            signIn();
        }else if (i == R.id.applySettings) {
            try {
                String uname = username.getText().toString();
                String email = useremail.getText().toString();

                if(uname==null || "".equals(uname) || email==null || "".equals(email) || selecteddonation==false){
                    SettingsDlgUtils settingsUtils = new SettingsDlgUtils(context);
                    settingsUtils.show();
                    return;
                }

                settings.setUserName(uname);
                settings.setUserEmail(email);

                BigInteger modulus = settings.getModulus();
                BigInteger publicExponent = settings.getPublicExponent();
                BigInteger privateExponent = settings.getPrivateExponent();

                if(modulus==null || publicExponent==null  || privateExponent==null){
                    BigInteger[] keys = Key.generateKey(Key.KEY_SIZE_2048);
                    modulus = keys[Key.MODULUS];
                    publicExponent = keys[Key.PUBKICIEY];
                    privateExponent = keys[Key.PRIVATEKEY];
                    settings.setModulus(keys[Key.MODULUS]);
                    settings.setPublicExponent(keys[Key.PUBKICIEY]);
                    settings.setPrivateExponent(keys[Key.PRIVATEKEY]);

                    PublicKey publickey = RSAUtils.getPublicKey(modulus, publicExponent);
                    PrivateKey privatekey = RSAUtils.getPrivateKey(modulus, privateExponent);
                }

                buildSettings();

                if(!checkSetting()){
                    SettingsDlgUtils settingsUtils = new SettingsDlgUtils(context);
                    settingsUtils.show();
                    return;
                }

                DataBaseHelper dbHelper = new DataBaseHelper(context);
                dbHelper.open();
                dbHelper.insertSettins(settings);
                dbHelper.close();

                PBSettingsUtils.getInstance(settings);

                Intent returnIntent = getIntent();
                returnIntent.putExtra("result", "OK");
                setResult(RESULT_OK, returnIntent);
                //finish();
                super.onBackPressed();
            }catch(Exception e){
                PGLog.e(TAG, StackTrace.getStackTrace(e));
            }
        }else if(i == R.id.select_donation){
            selecteddonation = false;
            Donations donations = settings.getDonations();
            Intent intent = new Intent(context, DonationList_Activity.class);
            if(donations!=null) {
                intent.putExtra("Name", donations.getName());
                intent.putExtra("Category", donations.getCategory());
            }
            startActivityForResult(intent, RC_SELECT_DONATION);
        }else if(i == R.id.pwdsetting){
            PasswordNumUtils passwordNumUtils = new PasswordNumUtils(context, getResString(R.string.setting_lbldebugpwd), pwd);
            if(settings!=null && !"".equals(settings.getRemittance_pwd())){
                passwordNumUtils.setSettings(settings);
                passwordNumUtils.setModifyPwd(true);
            }else{
                if(settings==null){
                    settings = PBSettingsUtils.getInstance().getSettings();
                }
                passwordNumUtils.setSettings(settings);
                passwordNumUtils.setModifyPwd(false);
            }
            passwordNumUtils.show();
        }
    }

    private int getValToInt(TextView text){
        String s = text.getText().toString();
        s = StringUtils.replaceAll(s, ",", "");
        s = StringUtils.replaceAll(s, " 원", "");
        s = StringUtils.replaceAll(s, " %", "");
        s = StringUtils.replaceAll(s, " 일", "");
        s = StringUtils.replaceAll(s, "　", "");
        s = StringUtils.replaceAll(s, ">", "");
        return Integer.parseInt(s);
    }

    private void buildSettings(){
        try {
            Switch overall = (Switch) findViewById(R.id.overall);
            settings.setThresholdOverall(overall.isChecked());
            TextView threshold = (TextView) findViewById(R.id.threshold);
            settings.setThreshold(getValToInt(threshold));

            TextView percent = (TextView) findViewById(R.id.percent);
            settings.setPercent(getValToInt(percent));

            Switch lastday = (Switch) findViewById(R.id.lastday);
            if (lastday.isChecked()) {
                settings.setDonationDay(-1);
            } else {
                settings.setDonationDay(getValToInt(donationday));
            }
            Switch debug_switch = (Switch) findViewById(R.id.debug);
            settings.setDebug(debug_switch.isChecked());
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }

    private boolean checkSetting(){
        if(settings.getModulus()==null){
            return false;
        }
        if(settings.getPrivateExponent()==null){
            return false;
        }
        if(settings.getPublicExponent()==null){
            return false;
        }
        if("".equals(settings.getRemittance_pwd())){
            return false;
        }
        if(settings.getDonations()==null){
            return false;
        }

        return true;
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
