package org.blogsite.youngsoft.piggybank.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.crash.FirebaseCrash;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.activity.auth.SettingsActivity;
import org.blogsite.youngsoft.piggybank.adapter.DonationInfoListViewAdapter;
import org.blogsite.youngsoft.piggybank.db.DBUtils;
import org.blogsite.youngsoft.piggybank.dialog.ConfirmDialog;
import org.blogsite.youngsoft.piggybank.dialog.DonationRegistUtils;
import org.blogsite.youngsoft.piggybank.dialog.PermissionDialog;
import org.blogsite.youngsoft.piggybank.fragment.HomeFragment;
import org.blogsite.youngsoft.piggybank.fragment.SmsChartViewFragment;
import org.blogsite.youngsoft.piggybank.fragment.SmsListViewFragment;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.other.CircleTransform;
import org.blogsite.youngsoft.piggybank.parser.Categorizer;
import org.blogsite.youngsoft.piggybank.reader.SMSReader;
import org.blogsite.youngsoft.piggybank.receiver.ReceiverManager;
import org.blogsite.youngsoft.piggybank.receiver.SmsReceiver;
import org.blogsite.youngsoft.piggybank.service.CategoryService;
import org.blogsite.youngsoft.piggybank.setting.PBSettings;
import org.blogsite.youngsoft.piggybank.setting.PBSettingsUtils;
import org.blogsite.youngsoft.piggybank.utils.BackPressCloseHandler;
import org.blogsite.youngsoft.piggybank.utils.ButtonClick;
import org.blogsite.youngsoft.piggybank.utils.IButtonClickListner;
import org.blogsite.youngsoft.piggybank.utils.IO;
import org.blogsite.youngsoft.piggybank.utils.ObjLoader;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.utils.TimeUtils;
import org.blogsite.youngsoft.piggybank.db.DatabaseInitialize;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private boolean registredReceiver = false;
    private boolean startedCategoryService = false;

    private PermissionDialog mPermissionDialog;

    private BackPressCloseHandler backPressCloseHandler;

    private Context context;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtEmail;
    private Toolbar toolbar;
    private PBSettings settings;

    private ConfirmDialog confirm = null;
    private IButtonClickListner listner;

    // 탐색 헤더 배경 이미지 및 프로필 이미지를로드하는 URL
    //private static final String urlNavHeaderBg = "http://api.androidhive.info/images/nav-menu-header-bg.jpg";
    //private static final String urlProfileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";

    private Integer[] urlProfileImg = new Integer[2];
    private Integer resourceID;

    // 현재 탐색 메뉴 항목을 식별하는 색인
    public static int navItemIndex = 2;

    // fragments를 첨부하는 데 사용되는 태그
    private static final String TAG_HOME = "home";
    private static final String TAG_MONTH_VIEW = "monthview";
    private static final String TAG_YEAR_VIEW = "yearview";
    private static final String TAG_MONTH_CHART_VIEW = "monthchartview";
    private static final String TAG_YEAR_CHART_VIEW = "yearchartview";
    public static String CURRENT_TAG = TAG_MONTH_VIEW;

    // 선택한 탐색 메뉴 항목에 대한 툴바 제목
    private String[] activityTitles;

    // 사용자가 키를 눌렀을 때 홈 프래그먼트를로드하는 플래그
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private Categorizer categorizer;
    private int appStart = 0;
    private TimeUtils timeUtils = null;
    private int dateCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        backPressCloseHandler = new BackPressCloseHandler(this);

        SmsUtils.getInstance(context);

        urlProfileImg[1] = R.drawable.nav_menu_header_blue_bg;
        urlProfileImg[0] = R.drawable.nav_menu_header_red_bg;

        TimeUtils time = new TimeUtils(System.currentTimeMillis());
        resourceID = time.getDay() % 2;

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // 네비게이션 헤더 뷰
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtEmail = (TextView) navHeader.findViewById(R.id.email);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // 문자열 리소스에서 도구 모음 제목 로드
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // 네비게이션 메뉴 헤더 데이터로드
        loadNavHeader();

        // 네비게이션 메뉴 초기화
        setUpNavigationView();

        //copyDbFromAsset();

        if (savedInstanceState == null) {
            start();
        }

        checkPermission(false);

        if(!startedCategoryService){
            startCategoryService();
        }

        if(!checkSettings()){
            Toast.makeText(MainActivity.this, "처음 사용자는 설정부터 해야 합니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivityForResult(intent, 1);
        }
    }


    private void copyDbFromAsset(){
        try{
            IO io = new IO();
            String path = io.getDbPath();
            File fpath = io.makeDirectory(path);
            InputStream in = getAssets().open("db/piggybank.db");
            File outFile = new File(fpath, "piggybank.db");
            OutputStream out = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1){
                out.write(buffer, 0, read);
            }
            out.flush();
            out.close();
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            checkPermission(false);
        }
    }

    private void start(){
        navItemIndex = 0;
        CURRENT_TAG = TAG_HOME;
        loadHomeFragment();
    }

    /***
     *배경 이미지, 프로필 이미지, 이름과 같은 네비게이션 메뉴 헤더 정보 로드
     */
    private void loadNavHeader() {
        if(settings!=null) {
            Log.v("YKKIM", "userName : " + settings.getUserName()) ;
            txtName.setText(settings.getUserName().concat(" 님"));
            //txtEmail.setText(settings.getUserEmail());
        } else {
            Log.v("YKKIM", "userName : 홍길동 님" ) ;
            txtName.setText("홍길동 님");
        }

        // 헤더 배경 이미지로드 중

        /*Glide.with(this).load(urlProfileImg[resourceID])
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);*/

        imgNavHeaderBg.setBackgroundColor(Color.parseColor("#f53434"));



        // 프로필 이미지로드
        if(settings!=null && !"".equals(settings.getPhotoUrl())) {
            Glide.with(this).load(settings.getPhotoUrl())
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfile);
        }else{
            Glide.with(this).load(R.drawable.user_default)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfile);
        }
    }

    /***
     * 네비게이션 메뉴에서 사용자가 선택한 fragment를 반환
     */
    private void loadHomeFragment() {
        // 네비게이션 메뉴 항목 선택
        selectNavMenu();

        // 툴바 제목 설정
        //setToolbarTitle();

        /**
         * 네비게이션 메뉴를 다시 선택하는 경우 네비게이션 drawer만 닫음
         */
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        /**
         * fragment가 많은 데이터를 가지고 있을 때 네비게이션 메뉴를 이동할 때
         * 화면이 지연되는 경우가 있으므로 fragment에 크로스 fade 효과를 적용
         */
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                setFragmentTitle(activityTitles[navItemIndex]);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // mPendingRunnable가 null이 아닌 경우, 메세지 큐에 추가한다
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //항목 클릭시 drawer를 닫음
        drawer.closeDrawers();

        // 도구 모음 새로 고침 메뉴
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        String param = TimeUtils.getRandom();
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = HomeFragment.newInstance("P1", "P2");
                return homeFragment;
            case 1:
                // 월별 소비 내역
                //SmsListViewFragment monthviewFragment = new SmsListViewFragment();
                SmsListViewFragment monthviewFragment = SmsListViewFragment.newInstance("month", param);

                return monthviewFragment;
            case 2:
                // 연별 소비내역
                //YearViewFragment yearviewFragment = new YearViewFragment();
                SmsListViewFragment yearviewFragment = SmsListViewFragment.newInstance("year", param);
                return yearviewFragment;
            case 3:
                // 월별 차트
                SmsChartViewFragment monthchartviewFragment = SmsChartViewFragment.newInstance("month", param);
                return monthchartviewFragment;

            case 4:
                // 연별 차트
                SmsChartViewFragment yearchartviewFragment = SmsChartViewFragment.newInstance("year", param);
                return yearchartviewFragment;

//            case 5:
                // settings fragment
//                SettingsFragment settingsFragment = SettingsFragment.newInstance("P1","P2");
//                return settingsFragment;
            default:
                return HomeFragment.newInstance("P1", "P2");
        }
    }

    private void setFragmentTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        // 네비게이션 메뉴 선택시 처리할 리스너
        SmsUtils.getInstance(context);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // 이 메소드는 항목 네비게이션 메뉴 클릭으로 트리거됨
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // 클릭 한 항목을 확인하고 적절한 조치 수행
                switch (menuItem.getItemId()) {
                    // ContentFragment의 메인 컨텐트 변경
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_month_view:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_MONTH_VIEW;
                        break;
                    case R.id.nav_year_view:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_YEAR_VIEW;
                        break;
                    case R.id.nav_month_chart_view:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_MONTH_CHART_VIEW;
                        break;
                    case R.id.nav_year_chart_view:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_YEAR_CHART_VIEW;
                        break;
                    case R.id.nav_donation:
                        startActivity(new Intent(MainActivity.this, DonationActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_donation_history:
                        startActivity(new Intent(MainActivity.this, HistoryViewActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_settings:
                        Intent intent = new Intent(context, SettingsActivity.class);
                        intent.putExtra("action", "Settings");
                        startActivityForResult(intent, 1);
                        //startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_regist_donation:
                        DonationRegistUtils donationRegistUtils = new DonationRegistUtils(context);
                        donationRegistUtils.show();
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_donation_info:
                        Intent into_intent = new Intent(context, DonationInfoListActivity.class);
                        into_intent.putExtra("action", "menu");
                        startActivityForResult(into_intent, 1);
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_license:
                        // fragment를 로드하는 대신 새로운 Intent를 실행
                        startActivity(new Intent(MainActivity.this, LicenseActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_db_init:
                        try {
                            SmsUtils.readSmsPermission(context);
                            SmsUtils.readStoragePermission(context);
                            SmsUtils.writeStoragePermission(context);
                            SmsUtils.checkSelfPermissionAll(context);
                            SmsUtils.getAccountPermission(context);

                            SmsUtils.getNewInstance(context);
/*****************************************************************************
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
                                    DatabaseInitialize dbInitializer = new DatabaseInitialize(context, getString(R.string.init_db_title),
                                            getString(R.string.init_db_msg));
                                    dbInitializer.show();
                                }
                            }
 *****************************************************************************/
                            DatabaseInitialize dbInitializer = new DatabaseInitialize(context, getString(R.string.init_db_title),
                                    getString(R.string.init_db_msg));
                            dbInitializer.show();
                        }catch (Exception e){
                            PGLog.e(TAG, StackTrace.getStackTrace(e));
                            FirebaseCrash.log(StackTrace.getStackTrace(e));
                            FirebaseCrash.report(e);
                        }
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_app_permission:
                        checkPermission(true);
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //항목 선택 토글
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                if(checkSettings()){
                    loadNavHeader();
                }else{
                    /*Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivityForResult(intent, 1);*/
                    Toast.makeText(MainActivity.this, "처음 사용자는 설정부터 해야 합니다.", Toast.LENGTH_SHORT).show();
                }
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            //getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            //getMenuInflater().inflate(R.menu.notifications, menu);
        }

        return true;
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

            boolean debug = PBSettingsUtils.getInstance().isDebug();
        }
    };

    private void checkPermission(boolean onMenu){
        SmsUtils.getInstance(context);
        mPermissionDialog = new PermissionDialog(context, permissionListener);
        if(onMenu) {
            mPermissionDialog.show();
        }else {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!onMenu && !SmsUtils.checkSelfPermissionAll(context)) {
                    mPermissionDialog.show();
                }
            }else{
                initializeDB();
                boolean debug = PBSettingsUtils.getInstance().isDebug();

                if(!startedCategoryService){
                    startCategoryService();
                }
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

                if(!checkSettings()){
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivityForResult(intent, 1);
                }
            }
        }
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

    private boolean checkSettings(){
        boolean ret = false;
        IO io = new IO();
        if(io.isDBExist() && DBUtils.checkSettings(context)) {
            String str = DBUtils.getSettings(context);
            try {
                settings = (PBSettings) ObjLoader.loadObjectFromBase64(str);
                PBSettingsUtils.getInstance(settings);
                ret = true;
            } catch (Exception e) {
                ret = false;
                PGLog.e(TAG, StackTrace.getStackTrace(e));
                FirebaseCrash.log(StackTrace.getStackTrace(e));
                FirebaseCrash.report(e);
            }
        }
        return ret;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= 23) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //resume tasks needing this permission
                initializeDB();
            }
        }else{
            initializeDB();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();

        appStart = 1;
    }

    private void startCategoryService(){
        Intent serviceIntent = new Intent(context,CategoryService.class);
        serviceIntent.putExtra("appStart", 1);
        startService(serviceIntent);
        startedCategoryService = true;
    }

}

