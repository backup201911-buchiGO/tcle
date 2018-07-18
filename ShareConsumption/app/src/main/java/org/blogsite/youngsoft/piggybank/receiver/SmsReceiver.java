package org.blogsite.youngsoft.piggybank.receiver;


import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.analyzer.Data;
import org.blogsite.youngsoft.piggybank.crypt.Crypt;
import org.blogsite.youngsoft.piggybank.db.DBUtils;
import org.blogsite.youngsoft.piggybank.db.DataBaseHelper;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.notification.PushHelper;
import org.blogsite.youngsoft.piggybank.parser.Categorizer;
import org.blogsite.youngsoft.piggybank.parser.RulesUtils;
import org.blogsite.youngsoft.piggybank.parser.SMSParser;
import org.blogsite.youngsoft.piggybank.setting.PBSettings;
import org.blogsite.youngsoft.piggybank.setting.PBSettingsUtils;
import org.blogsite.youngsoft.piggybank.utils.CardEnum;
import org.blogsite.youngsoft.piggybank.utils.CategoryEnum;
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

public class SmsReceiver extends BroadcastReceiver
{
    private static final String TAG = "SmsReceiver";

    // All available column names in SMS table
    // [_id, thread_id, address,
    // person, date, protocol, read,
    // status, type, reply_path_present,
    // subject, body, service_center,
    // locked, error_code, seen]

    public static final String SMS_EXTRA_NAME = "pdus";
    public static final String SMS_URI = "content://sms";

    public static final String ADDRESS = "address";
    public static final String PERSON = "person";
    public static final String DATE = "date";
    public static final String READ = "read";
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String BODY = "body";
    public static final String SEEN = "seen";

    public static final int MESSAGE_TYPE_INBOX = 1;
    public static final int MESSAGE_TYPE_SENT = 2;

    public static final int MESSAGE_IS_NOT_READ = 0;
    public static final int MESSAGE_IS_READ = 1;

    public static final int MESSAGE_IS_NOT_SEEN = 0;
    public static final int MESSAGE_IS_SEEN = 1;

    private Context context;

    //파이어베이스 실시간 데이터베이스
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mConditionRef = mDatabase.child("PiggyBank").child("Rules").child("Categories");

    private boolean debug = false;

    private Categorizer categorizer;

    public SmsReceiver(){

    }

    public void onReceive(Context context, final Intent intent )
    {
        this.context = context;

        debug = PBSettingsUtils.getInstance().isDebug();

        mConditionRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            CategoryUtils categoryUtils = new CategoryUtils();
                            String cats = dataSnapshot.getValue(String.class);
                            HashMap<String, String> map = categoryUtils.getCategoryMap(cats);
                            RulesUtils.getInstance().setCategoryMap(map);
                            categorizer = new Categorizer();
                            categorizer.setCategoryMap(map);
                            RulesUtils.getInstance().setCategoryMap(map);
                            parseReceivedMessage(intent);
                        }catch (Exception e){
                            PGLog.e(TAG, StackTrace.getStackTrace(e));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        // WARNING!!!
        // If you uncomment next line then received SMS will not be put to incoming.
        // Be careful!
        // this.abortBroadcast();
    }

    private void parseReceivedMessage(Intent intent){
        // Get SMS map from Intent
        Bundle extras = intent.getExtras();

        if ( extras != null )
        {

            // Get received SMS array
            Object[] smsExtra = (Object[]) extras.get( SMS_EXTRA_NAME );

            // Get ContentResolver object for pushing encrypted SMS to incoming folder
            ContentResolver contentResolver = context.getContentResolver();

            DataBaseHelper dbHelper = new DataBaseHelper(context);
            dbHelper.open();
            int count = 0;
            try {
                for (int i = 0; i < smsExtra.length; ++i) {
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
                    long timestamp = sms.getTimestampMillis();
                    String body = sms.getMessageBody().toString();
                    String address = sms.getOriginatingAddress();
                    SMSParser parser = new SMSParser(timestamp, address, body);
                    parser.setCategorizer(categorizer);
                    parser.parse();
                    Data data = parser.getData();
                    if (data != null) {
                        count ++;
                        if(dbHelper.insertColumn(data) != -1) {
                            PushHelper.notify(context, null, data);
                        }
                    }else{
/*************************************************************************************************************
                        data = new Data();
                        data.setCard(CardEnum.KB_CARD);
                        data.setCategory(CategoryEnum.Communication);
                        data.setTimestamp(System.currentTimeMillis());
                        data.setAmount(100000);
                        PushHelper.notify(context, null, data);
 *************************************************************************************************************/

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
            }catch(Exception e){
                PGLog.e(TAG, StackTrace.getStackTrace(e));
            }finally {
                dbHelper.close();
            }

            if(count>0) {
                Toast.makeText(context, String.valueOf(count) + SmsUtils.getResource(R.string.register_card_msg), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void insertParsingException(long timestamp, String msg){
        TimeUtils t = new TimeUtils(System.currentTimeMillis());
        String child = t.getFormattedTime(SmsUtils.getResource(R.string.date_format));
        PBSettings settings = PBSettingsUtils.getInstance().getSettings();
        if(settings==null){
            if(DBUtils.checkSettings(context)) {
                String str = DBUtils.getSettings(context);
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

            DateFormat df = new SimpleDateFormat(SmsUtils.getResource(R.string.date_format_kor));
            String s = df.format(new Date(timestamp));

            String cmsg = Crypt.encryptPiggyBank(msg.getBytes());

            DatabaseReference exceptDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference rcvrpyRef = exceptDatabase.child("PiggyBank").child("Rules");
            rcvrpyRef.child("ParsingException").child(child).child(s).setValue(cmsg);
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }
}

