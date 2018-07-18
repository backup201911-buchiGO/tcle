package org.blogsite.youngsoft.piggybank.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import org.blogsite.youngsoft.piggybank.analyzer.Data;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.setting.PBSettings;
import org.blogsite.youngsoft.piggybank.utils.IO;
import org.blogsite.youngsoft.piggybank.utils.ObjSaver;

public class SQLiteHelper  extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteHelper";

    private static final String DB_NAME = "DB_NAME";
    private static final int DB_VERSION = 1;

    private static SQLiteHelper INSTANCE;
    private static SQLiteDatabase mDb;

    private Context context;
    private String dbPath;

    public static SQLiteHelper getInstance(Context context, String name, CursorFactory factory) {
        if (INSTANCE == null) {
            INSTANCE = new SQLiteHelper(context, name, factory);
            mDb = INSTANCE.getWritableDatabase();
        }

        return  INSTANCE;
    }

    public void open() {
        if (mDb.isOpen() == false) {
            INSTANCE.onOpen(mDb);
        }
    }

    public SQLiteHelper(Context context, String name, CursorFactory factory) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        IO io = new IO();
        dbPath = io.getDbPath();
    }

    // 최초 DB를 만들때 한번만 호출된다.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DataBases.CreateDB._CREATE_DATATABLE);
        db.execSQL(DataBases.CreateDB._CREATE_SETTINGTABLE);
    }

    // 버전이 업데이트 되었을 경우 DB를 다시 만들어 준다.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB._DATA_TABLENAME);
        onCreate(db);
    }

    public long insertColumn(Data data){
        open();
        if(!checkColumnExist(data.getTimestamp())) {
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
            return mDb.insert(DataBases.CreateDB._DATA_TABLENAME, null, values);
        }else{
            return -1;
        }
    }

    public boolean updateColumn(long id, Data data){
        open();
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
        return mDb.update(DataBases.CreateDB._DATA_TABLENAME, values, "_id="+id, null) > 0;
    }

    public long insertSettins(PBSettings data){
        long ret = -1L;
        ContentValues values = new ContentValues();
        open();

        try {
            deleteSettings();
            String set = ObjSaver.ObjectToBase64(data);
            values.put(DataBases.CreateDB.photourl, data.getPhotoUrl());
            values.put(DataBases.CreateDB.username, data.getUserName());
            values.put(DataBases.CreateDB.useremail, data.getUserEmail());
            values.put(DataBases.CreateDB.settings, set);

            ret = mDb.insert(DataBases.CreateDB._SETTINGS_TABLENAME, null, values);
        }catch(Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
        return ret;
    }

    public boolean deleteColumn(long id){
        open();
        return mDb.delete(DataBases.CreateDB._DATA_TABLENAME, "_id="+id, null) > 0;
    }

    public void deleteAllColumn(){
        open();
        mDb.execSQL("DELETE  FROM " + DataBases.CreateDB._DATA_TABLENAME);
    }

    public void deleteSettings(){
        open();
        mDb.execSQL("DELETE  FROM " + DataBases.CreateDB._SETTINGS_TABLENAME);
    }

    // Select All
    public Cursor getAllColumns(){
        open();
        return mDb.rawQuery("SELECT * FROM " + DataBases.CreateDB._DATA_TABLENAME + " order by timestamp desc", null);
    }

    // Select All
    public Cursor getAllData(){
        open();
        return mDb.rawQuery("SELECT timestamp, data FROM " + DataBases.CreateDB._DATA_TABLENAME + " order by timestamp desc", null);
    }

    public Cursor getData(String sql){
        open();
        return mDb.rawQuery(sql, null);
    }

    public boolean checkColumnExist(final long timestamp){
        boolean ret = true;
        String sql = "select count(*) from " + DataBases.CreateDB._DATA_TABLENAME
                + " where " + DataBases.CreateDB.timestamp + "=" + String.valueOf(timestamp);

        if(getTotalColumnCount()>0) {
            Cursor mCount = mDb.rawQuery(sql, null);
            mCount.moveToFirst();
            int count = mCount.getInt(0);
            ret = count > 0;
        }else{
            ret = false;
        }
        return ret;
    }

    public int getTotalColumnCount(){
        open();
        String sql = "select count(*) from " + DataBases.CreateDB._DATA_TABLENAME;
        Cursor mCount= mDb.rawQuery(sql, null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);

        return count;
    }
}
