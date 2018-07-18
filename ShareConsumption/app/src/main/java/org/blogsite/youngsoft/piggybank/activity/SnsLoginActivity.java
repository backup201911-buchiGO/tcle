package org.blogsite.youngsoft.piggybank.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.activity.auth.SettingsActivity;
import org.blogsite.youngsoft.piggybank.db.DatabaseInitialize;
import org.blogsite.youngsoft.piggybank.dialog.ConfirmDialog;
import org.blogsite.youngsoft.piggybank.dialog.PermissionDialog;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.reader.SMSReader;
import org.blogsite.youngsoft.piggybank.receiver.ReceiverManager;
import org.blogsite.youngsoft.piggybank.receiver.SmsReceiver;
import org.blogsite.youngsoft.piggybank.service.CategoryService;
import org.blogsite.youngsoft.piggybank.setting.PBSettingsUtils;
import org.blogsite.youngsoft.piggybank.utils.ButtonClick;
import org.blogsite.youngsoft.piggybank.utils.IButtonClickListner;
import org.blogsite.youngsoft.piggybank.utils.IO;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class SnsLoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final String TAG = "SnsLoginActivity";

    private Context context;
    private PermissionDialog mPermissionDialog;
    private IButtonClickListner listner;
    private boolean registredReceiver = false;
    private ConfirmDialog confirm = null;
    private boolean startedCategoryService = false;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 9001;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        //앱 사용허가 요청 처리
        checkPermission();


        if (isSnsLogined(true)) {
            setContentView(R.layout.activity_sns_login);
            ImageButton mTwitterSignInButton = (ImageButton) findViewById(R.id.twitter_login_btn);
            mTwitterSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //attemptLogin();

                    if (isCheckedAgree()) {
                        startActivity(new Intent(getApplication(), MainActivity.class)); //로딩이 끝난 후, MainActivity 이동
                        SnsLoginActivity.this.finish();
                    } else {
                        Toast.makeText(SnsLoginActivity.this, "서비스 이용약관에 동의하셔야 합니다.", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
        } else {


            setContentView(R.layout.activity_sns_login);
            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);



            showProgress(true);

            Handler hd = new Handler();
            hd.postDelayed(new SnsLoginActivity.Scenehandler(), 3000); // 1초 후에 hd handler 실행  3000ms = 3초


        }

    }

    private class Scenehandler implements Runnable{
        public void run(){
            showProgress(true);
            startActivity(new Intent(getApplication(), MainActivity.class)); //로딩이 끝난 후, MainActivity 이동

            SnsLoginActivity.this.finish();
        }
    }

    /*
    서비스 이용약관 체크 유무
    return true 인 경우만 다음으로 넘긴다.
     */
    private boolean isCheckedAgree() {
        CheckBox agreeCheck = (CheckBox)findViewById(R.id.agree_with_services);
        return agreeCheck.isChecked();
    }


    private boolean isSnsLogined(boolean param) {

        return param;
    }

    protected void grantPermission()
    {
        ActivityCompat.requestPermissions(
                (Activity)context,
                new String[]{
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.CALL_PHONE
                },
                SmsUtils.REQUEST_PERMISSION
        );
    }


    private void checkPermission(){
        SmsUtils.getInstance(context);
        mPermissionDialog = new PermissionDialog(context, permissionListener);

        if (Build.VERSION.SDK_INT >= 23) {
            if (!SmsUtils.checkSelfPermissionAll(context)) {
                mPermissionDialog.show();
            }
        }else{
            initializeDB();
            // boolean debug = PBSettingsUtils.getInstance().isDebug();

            if(!startedCategoryService){
                startCategoryService();
            }
        }
    }

    private void initializeDB(){
        int count = 0;
        SmsUtils.readSmsPermission(context);
        SmsUtils.readStoragePermission(context);
        SmsUtils.writeStoragePermission(context);
        SmsUtils.checkSelfPermissionAll(context);
        SmsUtils.getAccountPermission(context);
        SmsUtils.getNewInstance(context);

        if (Build.VERSION.SDK_INT >= 23) {
            if (SmsUtils.READ_SMS != PackageManager.PERMISSION_GRANTED ||
                    SmsUtils.WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED ||
                    SmsUtils.RECEIVE_SMS != PackageManager.PERMISSION_GRANTED ||
                    SmsUtils.GET_ACCOUNTS != PackageManager.PERMISSION_GRANTED ||
                    SmsUtils.CALL_PHONE != PackageManager.PERMISSION_GRANTED)
            {
                listner = new ButtonClick(context) {
                    public void onYesNoClick(final boolean yes){
                        confirm.dismiss();
                        if(yes){
                            finish();
                        }
                    }
                };
                confirm = new ConfirmDialog(context, getString(R.string.app_permission_title), getString(R.string.app_permission_msg), listner, false);
                confirm.show();
            }else{
                IO io = new IO();
                if(!io.isDBExist()){
                    io.makeDirectory(io.getDbPath()).getAbsolutePath();
                    SMSReader reader = new SMSReader(context);
                    reader.read();
                }

                //디비가 있는 경우는 초기화 디비를 실행하지 않음
                //if(!io.isDBExist()){
                //    SMSReader reader = new SMSReader(context);
                //    reader.read();
                //}

                SmsUtils.receiveSmsPermission(context);
                if(!registredReceiver) {
                    registerReceiver();
                }
            }
        }
    }

    private void googleSignIn() {
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
    }

    private void registerReceiver(){
        try {
            IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
            SmsReceiver receiver = new SmsReceiver();
            if(!ReceiverManager.init(context).isReceiverRegistered(receiver)) {
                ReceiverManager.init(context).unregisterReceiver(receiver);
                ReceiverManager.init(context).registerReceiver(receiver, filter);
            }

            //registerReceiver(new SmsReceiver(), filter);
            registredReceiver = true;
        }catch(Exception  e){
            registredReceiver = false;
            FirebaseCrash.log(StackTrace.getStackTrace(e));
            FirebaseCrash.report(e);
        }
    }

    private void startCategoryService(){
        Intent serviceIntent = new Intent(context,CategoryService.class);
        serviceIntent.putExtra("appStart", 1);
        startService(serviceIntent);
        startedCategoryService = true;
    }

    private View.OnClickListener permissionListener = new View.OnClickListener() {
        public void onClick(View v) {
            // Permission Check
            SmsUtils.getNewInstance(context);
            if (Build.VERSION.SDK_INT >= 23) {
                if (SmsUtils.READ_SMS != PackageManager.PERMISSION_GRANTED ||
                        SmsUtils.WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED ||
                        SmsUtils.RECEIVE_SMS != PackageManager.PERMISSION_GRANTED ||
                        SmsUtils.GET_ACCOUNTS != PackageManager.PERMISSION_GRANTED ||
                        SmsUtils.CALL_PHONE != PackageManager.PERMISSION_GRANTED)
                {
                    grantPermission();
                }
            }
            mPermissionDialog.dismiss();

            // boolean debug = PBSettingsUtils.getInstance().isDebug();
        }
    };

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(null, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        //mEmailView.setError(null);
        //mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        //String email = mEmailView.getText().toString();
        //String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
//            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", new String[]{ContactsContract.CommonDataKinds.Email .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(SnsLoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

//        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
//                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

