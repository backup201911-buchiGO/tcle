package org.blogsite.youngsoft.piggybank.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import org.blogsite.youngsoft.piggybank.analyzer.Data;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.setting.DonationInfo;
import org.blogsite.youngsoft.piggybank.setting.PBSettings;
import org.blogsite.youngsoft.piggybank.utils.IO;
import org.blogsite.youngsoft.piggybank.utils.ObjSaver;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;

/**
 * Created by klee on 2018-01-31.
 */

public class DataBaseHelper {
    private static final String TAG = "DataBaseHelper";

    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context context;
    private String dbPath;

    private class DatabaseHelper extends SQLiteOpenHelper{

        // 생성자
        public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // 최초 DB를 만들때 한번만 호출된다.
        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                //메인 테이블
                db.execSQL(DataBases.CreateDB._CREATE_DATATABLE);

                //설정 테이블
                db.execSQL(DataBases.CreateDB._CREATE_SETTINGTABLE);

                //기부 상세 설정  테이블
                db.execSQL(DataBases.CreateDB._CREATE_DONATION_INFO);

                //기부 내역 테이블
                db.execSQL(DataBases.CreateDB._CREATE_DONATION_HISTORY);

                //카테고리 내역 테이블(SHA-256 해시)
                db.execSQL(DataBases.CreateDB._CREATE_CATEGORY_SERVICE);

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        // 버전이 업데이트 되었을 경우 DB를 다시 만들어 준다.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB._DATA_TABLENAME);
            onCreate(db);
        }
    }

    public DataBaseHelper(Context context){
        this.context = context;
        IO io = new IO();
        dbPath = io.getDbPath();
    }

    public DataBaseHelper open() throws SQLException {
        IO io = new IO();
        io.makeDirectory(dbPath);
        mDBHelper = new DatabaseHelper(context, dbPath + IO.DATABASE_NAME, null, DATABASE_VERSION);
        io.chmodDB();
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        mDB.close();
    }

    public long insertColumn(Data data){
        long ret = -1L;
        try {
            if (!mDB.isOpen()) {
                open();
            }
            if (!checkColumnExist(data.getTimestamp())) {
                ContentValues values = new ContentValues();
                values.put(DataBases.CreateDB.timestamp, data.getTimestamp());
                values.put(DataBases.CreateDB.second, data.getSecond());
                values.put(DataBases.CreateDB.minute, data.getMinute());
                values.put(DataBases.CreateDB.hour, data.getHour());
                values.put(DataBases.CreateDB.day, data.getDay());
                values.put(DataBases.CreateDB.month, data.getMonth());
                values.put(DataBases.CreateDB.year, data.getYear());
                values.put(DataBases.CreateDB.amount, data.getAmount());
                values.put(DataBases.CreateDB.category, data.getCategory().getName());
                values.put(DataBases.CreateDB.card, data.getCard().getName());
                values.put(DataBases.CreateDB.data, data.toString());
                ret= mDB.insert(DataBases.CreateDB._DATA_TABLENAME, null, values);
            } else {
                ret = -1L;
            }
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
        return ret;
    }

    public boolean updateColumn(long id, Data data){
        boolean ret = false;
        try {
            if (!mDB.isOpen()) {
                open();
            }
            ContentValues values = new ContentValues();
            values.put(DataBases.CreateDB.timestamp, data.getTimestamp());
            values.put(DataBases.CreateDB.second, data.getSecond());
            values.put(DataBases.CreateDB.minute, data.getMinute());
            values.put(DataBases.CreateDB.hour, data.getHour());
            values.put(DataBases.CreateDB.day, data.getDay());
            values.put(DataBases.CreateDB.month, data.getMonth());
            values.put(DataBases.CreateDB.year, data.getYear());
            values.put(DataBases.CreateDB.amount, data.getAmount());
            values.put(DataBases.CreateDB.category, data.getCategory().getName());
            values.put(DataBases.CreateDB.card, data.getCard().getName());
            values.put(DataBases.CreateDB.data, data.toString());
            ret = mDB.update(DataBases.CreateDB._DATA_TABLENAME, values, "_id=" + id, null) > 0;
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
        return ret;
    }

    public long insertSettins(PBSettings data){
        long ret = -1L;
        ContentValues values = new ContentValues();
        if(!mDB.isOpen()){
            open();
        }
        try {
            deleteSettings();
            String set = ObjSaver.ObjectToBase64(data);
            values.put(DataBases.CreateDB.photourl, data.getPhotoUrl());
            values.put(DataBases.CreateDB.username, data.getUserName());
            values.put(DataBases.CreateDB.useremail, data.getUserEmail());
            values.put(DataBases.CreateDB.settings, set);

            ret = mDB.insert(DataBases.CreateDB._SETTINGS_TABLENAME, null, values);
        }catch(Exception e){
            e.printStackTrace();
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
        return ret;
    }

    public boolean deleteColumn(long id){
        if(!mDB.isOpen()){
            open();
        }
        return mDB.delete(DataBases.CreateDB._DATA_TABLENAME, "_id="+id, null) > 0;
    }

    public boolean deleteDonationInfo(DonationInfo info){
        String cardnanme = info.getCard();
        String catname = info.getCategory();

        if(!mDB.isOpen()){
            open();
        }
        return mDB.delete(DataBases.CreateDB._DONATION_INFO, "card=\""+cardnanme + "\" and category=\"" + catname + "\"", null) > 0;
    }

    public void deleteAllColumn(){
        if(!mDB.isOpen()){
            open();
        }
        mDB.execSQL("DELETE  FROM " + DataBases.CreateDB._DATA_TABLENAME);
    }

    public void deleteSettings(){
        if(!mDB.isOpen()){
            open();
        }
        mDB.execSQL("DELETE  FROM " + DataBases.CreateDB._SETTINGS_TABLENAME);
    }

    // Select All
    public Cursor getAllColumns(){
        if(!mDB.isOpen()){
            open();
        }
        return mDB.rawQuery("SELECT * FROM " + DataBases.CreateDB._DATA_TABLENAME + " order by timestamp desc", null);
    }

    // Select All
    public Cursor getAllData(){
        if(!mDB.isOpen()){
            open();
        }
        return mDB.rawQuery("SELECT timestamp, data FROM " + DataBases.CreateDB._DATA_TABLENAME + " order by timestamp desc", null);
    }

    public Cursor getData(String sql){
        if(!mDB.isOpen()){
            open();
        }
        return mDB.rawQuery(sql, null);
    }

    public boolean checkColumnExist(final long timestamp){
        boolean ret = true;
        try {
            String sql = "select count(*) from " + DataBases.CreateDB._DATA_TABLENAME
                    + " where " + DataBases.CreateDB.timestamp + "=" + String.valueOf(timestamp);

            if (getTotalColumnCount() > 0) {
                Cursor mCount = mDB.rawQuery(sql, null);
                mCount.moveToFirst();
                int count = mCount.getInt(0);
                ret = count > 0;
            } else {
                ret = false;
            }
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
        return ret;
    }

    public int getTotalColumnCount(){
        int count = 0;
        if(!mDB.isOpen()){
            open();
        }
        try {
            String sql = "select count(*) from " + DataBases.CreateDB._DATA_TABLENAME;
            Cursor cursor = mDB.rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(0);
            }
        }catch (Exception e){

        }
        return count;
    }

    public SQLiteDatabase getmDB(){
        return mDB;
    }
}
