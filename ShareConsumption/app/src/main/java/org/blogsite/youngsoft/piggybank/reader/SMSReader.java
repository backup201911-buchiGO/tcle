package org.blogsite.youngsoft.piggybank.reader;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.blogsite.youngsoft.piggybank.analyzer.Data;
import org.blogsite.youngsoft.piggybank.crypt.Crypt;
import org.blogsite.youngsoft.piggybank.db.DBUtils;
import org.blogsite.youngsoft.piggybank.db.DataBaseHelper;
import org.blogsite.youngsoft.piggybank.logs.LogWriter;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.parser.Categorizer;
import org.blogsite.youngsoft.piggybank.parser.RulesUtils;
import org.blogsite.youngsoft.piggybank.parser.SMSParser;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.setting.PBSettings;
import org.blogsite.youngsoft.piggybank.setting.PBSettingsUtils;
import org.blogsite.youngsoft.piggybank.utils.Alert;
import org.blogsite.youngsoft.piggybank.utils.CategoryUtils;
import org.blogsite.youngsoft.piggybank.utils.ObjLoader;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;
import org.blogsite.youngsoft.piggybank.utils.TimeUtils;

public class SMSReader {
    private static final String TAG = "SMSReader";

    //파이어베이스 실시간 데이터베이스
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mConditionRef = mDatabase.child("PiggyBank").child("Rules").child("Categories");

    private Categorizer categorizer;
    private int totalCursor = 0;
    private int progressCount = 0;

    private final int ALL_PARSING = 0;
    private final int TERM_PARSING = 1;

    private Context context;
    private long startTime = -1L;
    private long lastTime = -1L;
    private long maxTimestamp = -1L;
    private int msgCount = 0;
    private final  DateFormat df;
    private boolean debug = false;

    private boolean serviceRun = false;

    public SMSReader(Context context){
        this.context = context;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        dbHelper.open();
        dbHelper.close();

        df = new SimpleDateFormat("yy.MM.dd");
        maxTimestamp = DBUtils.getMaxTimestamp(context);
        debug = PBSettingsUtils.getInstance().isDebug();
    }

    public void setStartTime(final long startTime){
        this.startTime = startTime;
    }

    public void setLastTime(final long lastTime){
        this.lastTime = lastTime;
    }

    public void setServiceRun(boolean serviceRun) {
        this.serviceRun = serviceRun;
    }

    private int checkConstraint(){
        if(startTime>0L && lastTime >0L){
            return TERM_PARSING;
        }else{
            return ALL_PARSING;
        }
    }

    public void read(){
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
                            readMessage();
                        }catch (Exception e){
                            PGLog.e(TAG, StackTrace.getStackTrace(e));
                            FirebaseCrash.logcat(6, TAG, StackTrace.getStackTrace(e));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void readMessage(){
        try{
            DataBaseHelper dbHelper = new DataBaseHelper(context);
            dbHelper.open();
            dbHelper.deleteAllColumn();

            int constraint = checkConstraint();

            Uri inboxURI = Uri.parse("content://sms/inbox");
            String[] reqCols = new String[] {"_id", "address", "date", "body"};
            ContentResolver cr = context.getContentResolver();
            Cursor c = cr.query(inboxURI, reqCols, null, null, "date DESC");

            totalCursor = c.getCount();
            CheckTypesTask task = new CheckTypesTask();
            task.execute();

            while(c.moveToNext()) {
                progressCount++;
                boolean accepted = false;

                long timestamp = c.getLong(2);

                if(constraint==TERM_PARSING && timestamp >= startTime && timestamp <= lastTime){
                    accepted = true;
                }else if(constraint==ALL_PARSING){
                    accepted = true;
                }else if(maxTimestamp>0L && timestamp>maxTimestamp){
                    accepted = true;
                }else{
                    accepted = false;
                }

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
                            msgCount++;
                        }
                    }else{
                        // 카드사로 부터 받은 메시지가 분석에 실패한 경우
                        List<String> cardPhones = RulesUtils.getInstance().getCardPhones();
                        for(String s : cardPhones) {
                            if(s.equals(address)) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(address).append("|").append(timestamp).append("|").append(StringUtils.replaceAll(body, "\n", "|"));
                                LogWriter.d(TAG, sb.toString());
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
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
            FirebaseCrash.logcat(6, TAG, StackTrace.getStackTrace(e));
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

    private class CheckTypesTask extends AsyncTask<Void, Integer, Void> {
        ProgressDialog asyncDialog = new ProgressDialog(context);

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            asyncDialog.setTitle(R.string.analysis);
            asyncDialog.setMessage(SmsUtils.getResource(R.string.sms_parse));
            asyncDialog.setMax(totalCursor);
            // show dialog
            asyncDialog.show();
            publishProgress(0);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                for (int i = 0; i < 5; i++) {
                    publishProgress(progressCount);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                PGLog.e(TAG, StackTrace.getStackTrace(e));
                FirebaseCrash.logcat(6, TAG, StackTrace.getStackTrace(e));
            }
            return null;
        }


        // publishProgress() 메서드를 통해 호출됩니다. 진행사항을 표시하는데에 쓰입니다.
        @Override
        protected void onProgressUpdate(Integer... progress)
        {
            asyncDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
            if(msgCount>0 && !serviceRun){
                Alert.showAlert(context, SmsUtils.getResource(R.string.register_card_title), String.valueOf(msgCount) + SmsUtils.getResource(R.string.register_card_msg));
            }
        }
    }
}
