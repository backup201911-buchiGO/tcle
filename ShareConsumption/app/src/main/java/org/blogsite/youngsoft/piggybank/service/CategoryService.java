package org.blogsite.youngsoft.piggybank.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.activity.SmsListActivity;
import org.blogsite.youngsoft.piggybank.analyzer.Data;
import org.blogsite.youngsoft.piggybank.crypt.Crypt;
import org.blogsite.youngsoft.piggybank.crypt.SecureHash;
import org.blogsite.youngsoft.piggybank.db.DBUtils;
import org.blogsite.youngsoft.piggybank.db.DataBaseHelper;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.parser.Categorizer;
import org.blogsite.youngsoft.piggybank.parser.RulesUtils;
import org.blogsite.youngsoft.piggybank.parser.SMSParser;
import org.blogsite.youngsoft.piggybank.setting.PBSettings;
import org.blogsite.youngsoft.piggybank.setting.PBSettingsUtils;
import org.blogsite.youngsoft.piggybank.utils.CategoryUtils;
import org.blogsite.youngsoft.piggybank.utils.ObjLoader;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;
import org.blogsite.youngsoft.piggybank.utils.TimeUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CategoryService extends Service {
    private static final String TAG = "CategoryService";
    //파이어베이스 실시간 데이터베이스
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mConditionRef = mDatabase.child("PiggyBank").child("Rules").child("Categories");
    private Categorizer categorizer;
    private int appStart = 0;
    private boolean debug = false;
    private boolean parseStarted = false;
    private TimeUtils timeUtils = null;
    private int dateCode = 0;
    private String oldHash = null;
    private String newHash = null;

    private NotificationManager mNotificationManager;
    private Notification mNotification ;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        run();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if(intent!=null) {
                appStart = intent.getIntExtra("appStart", 0);
            }
            debug = PBSettingsUtils.getInstance().isDebug();
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            return START_STICKY;
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            return START_STICKY;
        }
    }

    //서비스가 종료될 때 할 작업

    public void onDestroy() {
    }

    private void run(){
        mConditionRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            if(appStart==0 && parseStarted==false) {
                                oldHash = DBUtils.getCategoryService(CategoryService.this);
                                CategoryUtils categoryUtils = new CategoryUtils();
                                String cats = dataSnapshot.getValue(String.class);
                                newHash = SecureHash.getHashHex("SHA-256", cats.getBytes());
                                HashMap<String, String> map = categoryUtils.getCategoryMap(cats);
                                SmsUtils.getInstance(CategoryService.this);
                                RulesUtils.getInstance().setCategoryMap(map);
                                categorizer = new Categorizer();
                                categorizer.setCategoryMap(map);
                                RulesUtils.getInstance().setCategoryMap(map);
                                if(!newHash.equals(oldHash)) {
                                    DBUtils.insertCategoryService(CategoryService.this, newHash, oldHash);
                                    oldHash = newHash;
                                    readMessage();
                                }
                            }else{
                                appStart = 0;
                            }
                        }catch (Exception e){
                            PGLog.e(TAG, StackTrace.getStackTrace(e));
                            FirebaseCrash.logcat(6, TAG, StackTrace.getStackTrace(e));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        //반복적으로 수행할 작업을 한다.
/********************
 while(isRun){
 handler.sendEmptyMessage(0);//쓰레드에 있는 핸들러에게 메세지를 보냄
 try{
 Thread.sleep(10000); //10초씩 쉰다.
 }catch (Exception e) {}
 }
 ********************/
    }

    private void readMessage(){
        parseStarted = true;
        timeUtils = new TimeUtils(System.currentTimeMillis());
        dateCode = timeUtils.getDateCode();

        try{
            DataBaseHelper dbHelper = new DataBaseHelper(CategoryService.this);
            dbHelper.open();
            dbHelper.deleteAllColumn();

            Uri inboxURI = Uri.parse("content://sms/inbox");
            String[] reqCols = new String[] {"_id", "address", "date", "body"};
            ContentResolver cr = CategoryService.this.getContentResolver();
            Cursor c = cr.query(inboxURI, reqCols, null, null, "date DESC");

            while(c.moveToNext()) {
                boolean accepted = false;

                long timestamp = c.getLong(2);
                accepted = true;

                if(accepted) {
                    long messageId = c.getLong(0);
                    String address = c.getString(1);
                    String body = c.getString(3);

                    SMSParser parser = new SMSParser(timestamp, address, body);

                    parser.setCategorizer(categorizer);
                    parser.parse();
                    Data data = parser.getData();
                    if (data != null) {
                        if(dbHelper.insertColumn(data) != -1){
                            //msgCount++;
                        }
                    }else{
                        // 카드사로 부터 받은 메시지가 분석에 실패한 경우
                        List<String> cardPhones = RulesUtils.getInstance().getCardPhones();
                        for(String s : cardPhones) {
                            if(s.equals(address)) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(address).append("|").append(timestamp).append("|").append(StringUtils.replaceAll(body, "\n", "|"));
                                PGLog.d(TAG, sb.toString());
                                if(debug) {
                                    insertParsingException(timestamp, sb.toString());
                                }
                                break;
                            }
                        }
                    }
                }
            }
            int dbCount = dbHelper.getTotalColumnCount();
            dbHelper.close();
            showNotification(getString(R.string.cat_srv_noti_1) + " " + String.valueOf(dbCount) + getString(R.string.cat_srv_noti_2));
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
            FirebaseCrash.logcat(6, TAG, StackTrace.getStackTrace(e));
        }
    }

    private void insertParsingException(long timestamp, String msg){
        TimeUtils t = new TimeUtils(System.currentTimeMillis());
        String child = t.getFormattedTime(getString(R.string.date_format));
        PBSettings settings = PBSettingsUtils.getInstance().getSettings();
        if(settings==null){
            if(DBUtils.checkSettings(CategoryService.this)) {
                String str = DBUtils.getSettings(CategoryService.this);
                try {
                    settings = (PBSettings) ObjLoader.loadObjectFromBase64(str);
                    PBSettingsUtils.getInstance(settings);
                } catch (Exception e) {
                    PGLog.e(TAG, StackTrace.getStackTrace(e));
                    FirebaseCrash.logcat(6, TAG, StackTrace.getStackTrace(e));
                }
            }
        }

        try {
            if (settings != null) {
                child = settings.getUserEmail();
                child = StringUtils.replaceAll(child, "@", "_");
                child = StringUtils.replaceAll(child, ".", "_");
            }

            DateFormat df = new SimpleDateFormat(getString(R.string.date_format_kor));
            String s = df.format(new Date(timestamp));

            String cmsg = Crypt.encryptPiggyBank(msg.getBytes());

            DatabaseReference exceptDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference rcvrpyRef = exceptDatabase.child("PiggyBank").child("Rules");
            rcvrpyRef.child("ParsingException").child(child).child(s).setValue(cmsg);
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }

    private void showNotification(String msg){
        parseStarted = false;
        try {
            Intent intent = new Intent(CategoryService.this, SmsListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(CategoryService.this, dateCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Bitmap largeIcon = BitmapFactory.decodeResource(CategoryService.this.getResources(), R.drawable.spinner);

            mNotification = new Notification.Builder(getApplicationContext())
                    .setContentTitle(getString(R.string.cat_srv_noti_title))
                    .setContentText(msg)
                    .setSmallIcon(R.drawable.notification)
                    .setLargeIcon(largeIcon)
                    .setTicker(getString(R.string.cat_srv_noti_tracker))
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{1000, 1000})
                    .build();

            //소리추가
            mNotification.defaults = Notification.DEFAULT_SOUND;

            //알림 소리를 한번만 내도록
            mNotification.flags = Notification.FLAG_ONLY_ALERT_ONCE;

            //확인하면 자동으로 알림이 제거 되도록
            mNotification.flags = Notification.FLAG_AUTO_CANCEL;

            if (mNotificationManager == null) {
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            mNotificationManager.notify(dateCode, mNotification);
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }
}
