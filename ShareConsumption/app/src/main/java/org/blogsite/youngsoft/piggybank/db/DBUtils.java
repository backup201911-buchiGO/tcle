package org.blogsite.youngsoft.piggybank.db;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import org.apache.commons.codec.binary.Base64;
import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.analyzer.Data;
import org.blogsite.youngsoft.piggybank.dialog.ConfirmUtils;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.setting.DonationHistory;
import org.blogsite.youngsoft.piggybank.setting.DonationInfo;
import org.blogsite.youngsoft.piggybank.utils.CategoryEnum;
import org.blogsite.youngsoft.piggybank.utils.IO;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.piggybank.utils.TimeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBUtils {
    private static final String TAG = "DBUtils";

    /**
     * 데이터베이스를 JSON 데이터로 백업한다.
     * @param context
     */
    public static void backupToJson(Context context){
        ProgressDialog dialog = ProgressDialog.show(context, SmsUtils.getResource(R.string.db_backup_title),
                SmsUtils.getResource(R.string.db_backup_msg),
                true, false);
        try{
            DataBaseHelper dbHelper = new DataBaseHelper(context);
            dbHelper.open();
            Cursor cursor = dbHelper.getAllData();
            JSONArray resultSet = new JSONArray();
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    int totalColumn = cursor.getColumnCount();
                    JSONObject rowObject = new JSONObject();
                    for (int i = 0; i < totalColumn; i++) {
                        if (cursor.getColumnName(i) != null) {
                            if (cursor.getString(i) != null) {
                                String cname = cursor.getColumnName(i);
                                if ("timestamp".equals(cname)) {
                                    rowObject.put(cname, cursor.getLong(i));
                                } else if ("data".equals(cname)) {
                                    String value = cursor.getString(i);
                                    String evalue = new String(Base64.encodeBase64(value.getBytes("UTF-8")));
                                    evalue = URLEncoder.encode(evalue, "UTF-8");
                                    rowObject.put(cname, evalue);
                                }
                            } else {
                                rowObject.put(cursor.getColumnName(i), "");
                            }
                        }
                    }
                    resultSet.put(rowObject);
                } while ((cursor.moveToNext()));
            }
            cursor.close();
            dbHelper.close();
            IO io = new IO();
            String path = io.getDbPath();
            TimeUtils timeUtils = new TimeUtils(System.currentTimeMillis());
            String fs = IO.BACKUP_NAME + "_" + timeUtils.getFormattedTime("yyyy_MM_dd") + ".json";
            File file = new File(path+fs);
            if(io.isFileExist(file)){
                io.deleteFile(file);
            }
            File dir = io.makeDirectory(path);
            File dest = io.makeFile(dir, (path+fs));
            io.writeFile(dest, resultSet.toString(4).getBytes("UTF-8"));
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            ConfirmUtils confirm = new ConfirmUtils(context, SmsUtils.getResource(R.string.db_backup_title), SmsUtils.getResource(R.string.db_backup_finish));
            confirm.show();
            dialog.dismiss();
        }
    }

    /**
     * 데이터베이스에 저장된 최소 년을 구한다.
     * @param context
     * @return
     */
    public static int getMinYear(Context context){
        int ret = -1;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        try{
            dbHelper.open();
            String sql = "select min(year) from smstable";
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                ret = cursor.getInt(0);
            }
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return ret;
    }

    public static int getMinYear(Context context, String sql){
        int ret = -1;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                ret = cursor.getInt(0);
            }
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return ret;
    }

    /**
     * 특정 년, 월의 일자별 사용 금액을 구한다.
     * select year,month,day, sum(amount) as summ from smstable where year = 2017 and month=12 group by day order by day
     * @param context
     * @param year
     * @param month
     * @param lastDayOfMonth
     * @return
     */
    public static ArrayList<Integer> getMonthlyData(Context context, int year, int month, int lastDayOfMonth){
        //select year,month,day, sum(amount) as summ from smstable where year = 2017 and month=12 group by day order by day
        String sql = "SELECT year,month,day, sum(amount) as summ FROM " + DataBases.CreateDB._DATA_TABLENAME
                   + " where year=" + String.valueOf(year) + " and month=" + String.valueOf(month)
                   + " group by day order by day";

        return getMonthlyData(context, sql, lastDayOfMonth);
    }

    /**
     * sql과 현재 월의 마지막 일을 이용하여 사용내역 금액을 구한다.
     * 마지막 일은 월별 목록을 구할 때 초기값을 0으로 채우기 위해 사용된다.
     * @param context
     * @param sql
     * @param lastDayOfMonth
     * @return
     */
    public static ArrayList<Integer> getMonthlyData(Context context, String sql, int lastDayOfMonth){
        ArrayList<Integer> amount = new ArrayList<Integer>();
        for(int i=0; i<lastDayOfMonth; i++){
            amount.add(i, 0);
        }
        PGLog.d(TAG, sql);

        DataBaseHelper dbHelper = new DataBaseHelper(context);
        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    int columnIndex = cursor.getColumnIndex("day");
                    int day = cursor.getInt(columnIndex);
                    /**
                     * 배열의 월 인덱스가 0부터 시작하므로
                     */
                    day = day - 1;
                    columnIndex = cursor.getColumnIndex("summ");
                    int value = cursor.getInt(columnIndex);
                    amount.remove(day);
                    amount.add(day, value);
                } while ((cursor.moveToNext()));
            }
            cursor.close();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return amount;
    }

    /**
     * 특정 년의 월별 사용금액을 구한다.
     * select year,month, sum(amount) as summ from smstable where year = 2017 group by month order by month
     * @param context
     * @param year
     * @return
     */
    public static ArrayList<Integer> getYearlyData(Context context, int year){
        ArrayList<Integer> amount = new ArrayList<Integer>();
        for(int i=0; i<12; i++){
            amount.add(i, 0);
        }
        //select year,month,day, sum(amount) as summ from smstable where year = 2017 and month=12 group by day order by day
        String sql = "SELECT year,month,sum(amount) as summ FROM " + DataBases.CreateDB._DATA_TABLENAME
                + " where year=" + String.valueOf(year)
                + " group by month order by month";

        PGLog.d(TAG, sql);

        DataBaseHelper dbHelper = new DataBaseHelper(context);
        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    int columnIndex = cursor.getColumnIndex("month");
                    int month = cursor.getInt(columnIndex);
                    /**
                     * 배열의 월 인덱스가 0부터 시작하므로
                     */
                    month = month - 1;
                    columnIndex = cursor.getColumnIndex("summ");
                    int value = cursor.getInt(columnIndex);
                    amount.add(month, value);
                } while ((cursor.moveToNext()));
            }
            cursor.close();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return amount;
    }

    /**
     * 특정 년, 월의 날자별로 카드 사용 금액을 구한다.
     * select year,month,day, card, sum(amount) as summ from smstable where year = 2017 and month=12 group by card,month,day order by month,day
     * @param context
     * @param year
     * @param month
     * @return
     */
    public static HashMap<Integer, HashMap<String, Integer>> getMonthlyCard(Context context, int year, int month){
        HashMap<Integer, HashMap<String, Integer>> monthlyCard = new HashMap<Integer, HashMap<String, Integer>>();

        String sql = "select year,month,day, card, sum(amount) as summ from smstable "
                  + "where year=" + String.valueOf(year) + " and month=" + String.valueOf(month)
                  + " group by card,month,day order by month,day";

        PGLog.d(TAG, sql);

        DataBaseHelper dbHelper = new DataBaseHelper(context);

        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    int columnIndex = cursor.getColumnIndex("day");
                    int day = cursor.getInt(columnIndex);

                    columnIndex = cursor.getColumnIndex("card");
                    String cardname = cursor.getString(columnIndex);
                    columnIndex = cursor.getColumnIndex("summ");
                    int value = cursor.getInt(columnIndex);

                    if (monthlyCard.containsKey(day)) {
                        monthlyCard.get(day).put(cardname, value);
                    } else {
                        HashMap<String, Integer> cardMap = new HashMap<String, Integer>();
                        cardMap.put(cardname, value);
                        monthlyCard.put(day, cardMap);
                    }
                } while ((cursor.moveToNext()));
            }
            cursor.close();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return monthlyCard;
    }

    /**
     * 특정 년의 월별 카드별 사용 금액을 구한다.
     * @param context
     * @param year
     * @return
     */
    public static HashMap<Integer, HashMap<String, Integer>> getYearlyCard(Context context, int year){
        HashMap<Integer, HashMap<String, Integer>> yearlyCard = new HashMap<Integer, HashMap<String, Integer>>();

        String sql = "select year,month, card, sum(amount) as summ from smstable "
                + "where year=" + String.valueOf(year)
                + " group by card,month order by month";

        PGLog.d(TAG, sql);

        DataBaseHelper dbHelper = new DataBaseHelper(context);

        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    int columnIndex = cursor.getColumnIndex("month");
                    int month = cursor.getInt(columnIndex);

                    columnIndex = cursor.getColumnIndex("card");
                    String cardname = cursor.getString(columnIndex);
                    columnIndex = cursor.getColumnIndex("summ");
                    int value = cursor.getInt(columnIndex);

                    if (yearlyCard.containsKey(month)) {
                        yearlyCard.get(month).put(cardname, value);
                    } else {
                        HashMap<String, Integer> cardMap = new HashMap<String, Integer>();
                        cardMap.put(cardname, value);
                        yearlyCard.put(month, cardMap);
                    }
                } while ((cursor.moveToNext()));
            }
            cursor.close();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return yearlyCard;
    }

    /**
     * 특정 년,월의 날자별 카테고리별 사용금액을 구한다.
     * select year,month,day, category, sum(amount) as summ from smstable where year = 2017 and month=12 group by category,month,day order by month,day
     *
     * @param context
     * @param year
     * @param month
     * @return
     */
    public static HashMap<Integer, HashMap<String, Integer>> getMonthlyCategory(Context context, int year, int month){
        HashMap<Integer, HashMap<String, Integer>> monthlyCategory = new HashMap<Integer, HashMap<String, Integer>>();

        String sql = "SELECT year,month,day, category, sum(amount) as summ FROM " + DataBases.CreateDB._DATA_TABLENAME
                + " where year=" + String.valueOf(year) + " and month=" + String.valueOf(month)
                + " group by category,month,day order by month,day";

        PGLog.d(TAG, sql);

        DataBaseHelper dbHelper = new DataBaseHelper(context);
        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    int columnIndex = cursor.getColumnIndex("day");
                    int day = cursor.getInt(columnIndex);

                    columnIndex = cursor.getColumnIndex("category");
                    String category = cursor.getString(columnIndex);

                    columnIndex = cursor.getColumnIndex("summ");
                    int value = cursor.getInt(columnIndex);

                    if (monthlyCategory.containsKey(day)) {
                        monthlyCategory.get(day).put(category, value);
                    } else {
                        HashMap<String, Integer> catMap = new HashMap<String, Integer>();
                        catMap.put(category, value);
                        monthlyCategory.put(day, catMap);
                    }
                } while ((cursor.moveToNext()));
            }
            cursor.close();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return monthlyCategory;
    }

    /**
     * 특정 년의 월별 카테고리별 사용금액을 구한다.
     * select year,month, category, sum(amount) as summ from smstable where year = 2017  group by category,month order by month
     * @param context
     * @param year
     * @return
     */
    public static HashMap<Integer, HashMap<String, Integer>> getYearlyCategory(Context context, int year){
        HashMap<Integer, HashMap<String, Integer>> monthlyCategory = new HashMap<Integer, HashMap<String, Integer>>();
        //select year,month, category, sum(amount) as summ from smstable where year = 2017  group by category,month order by month
        String sql = "SELECT year,month, category, sum(amount) as summ FROM " + DataBases.CreateDB._DATA_TABLENAME
                + " where year=" + String.valueOf(year)
                + " group by category,month order by month";

        PGLog.d(TAG, sql);

        DataBaseHelper dbHelper = new DataBaseHelper(context);
        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    int columnIndex = cursor.getColumnIndex("month");
                    int month = cursor.getInt(columnIndex);

                    columnIndex = cursor.getColumnIndex("category");
                    String category = cursor.getString(columnIndex);

                    columnIndex = cursor.getColumnIndex("summ");
                    int value = cursor.getInt(columnIndex);

                    if (monthlyCategory.containsKey(month)) {
                        monthlyCategory.get(month).put(category, value);
                    } else {
                        HashMap<String, Integer> catMap = new HashMap<String, Integer>();
                        catMap.put(category, value);
                        monthlyCategory.put(month, catMap);
                    }
                } while ((cursor.moveToNext()));
            }
            cursor.close();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return monthlyCategory;
    }

    /**
     * 문자메시지를 분석한 JSON 데이터를 구한다.
     * @param context
     * @param sql
     * @return
     */
    public static List<Data> getData(Context context, String sql){
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        List<Data> dataList = new ArrayList<Data>();

        PGLog.d(TAG, sql);

        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    int totalColumn = cursor.getColumnCount();
                    JSONObject rowObject = new JSONObject();
                    for (int i = 0; i < totalColumn; i++) {
                        if (cursor.getColumnName(i) != null) {
                            if (cursor.getString(i) != null) {
                                String cname = cursor.getColumnName(i);
                                if ("data".equals(cname)) {
                                    String value = cursor.getString(i);
                                    Gson gson = new Gson();
                                    Data data = gson.fromJson(value, Data.class);
                                    dataList.add(data);
                                }
                            }
                        }
                    }
                } while ((cursor.moveToNext()));
            }
            cursor.close();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return dataList;
    }

    public static int getTotalAmount(Context context, String sql){
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        int totalAmount = 0;
        PGLog.d(TAG, sql);

        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                int summ = cursor.getColumnIndex("summ");
                totalAmount = cursor.getInt(summ);
            }
            cursor.close();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return totalAmount;
    }

    public static long getMaxTimestamp(Context context){
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        long maxTimestamp = -1L;

        String sql = "select max(timestamp) as tmax from smstable";

        PGLog.d(TAG, sql);

        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                int tmax = cursor.getColumnIndex("tmax");
                maxTimestamp = cursor.getLong(tmax);
            }
            cursor.close();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return maxTimestamp;
    }

    public static HashMap<String, Integer> getCardTotalAmount(Context context, String sql){
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        HashMap<String, Integer> amountMap = new HashMap<>();
        PGLog.d(TAG, sql);

        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    int cnIdx = cursor.getColumnIndex("card");
                    String cardname = cursor.getString(cnIdx);

                    cnIdx = cursor.getColumnIndex("summ");
                    int totalAmount = cursor.getInt(cnIdx);

                    amountMap.put(cardname, totalAmount);
                } while ((cursor.moveToNext()));
            }
            cursor.close();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return amountMap;
    }

    public static HashMap<String, Integer> getCategories(Context context, String sql, boolean full){
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        HashMap<String, Integer> catMap = new HashMap<>();

        if(full) {
            for (int i = -1; i < 7; i++) {
                CategoryEnum catenum = CategoryEnum.getByValue(i);
                catMap.put(catenum.getName(), 0);
            }
        }

        PGLog.d(TAG, sql);

        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    int cnIdx = cursor.getColumnIndex("category");
                    String catname = cursor.getString(cnIdx);

                    cnIdx = cursor.getColumnIndex("summ");
                    int value = cursor.getInt(cnIdx);

                    if (catMap.containsKey(catname)) {
                        catMap.remove(catname);
                        catMap.put(catname, value);
                    } else {
                        catMap.put(catname, value);
                    }
                } while ((cursor.moveToNext()));
            }
            cursor.close();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }

        return catMap;
    }

    public static CategoryEnum getCategory(Context context, String sql){
        CategoryEnum cat = CategoryEnum.Unclassified;
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        PGLog.d(TAG, sql);

        try {
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                int cnIdx = cursor.getColumnIndex("category");
                String value = cursor.getString(cnIdx);
                cat = CategoryEnum.getByName(value);
            }
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return cat;
    }

    public static boolean checkSettings(Context context){
        DataBaseHelper dbHelper = new DataBaseHelper(context);

        String sql = "select count(username) as cnt from smssetting";
        int count = 0;
        PGLog.d(TAG, sql);

        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                int cnt = cursor.getColumnIndex("cnt");
                count = cursor.getInt(cnt);
            }
            cursor.close();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return count>0;
    }

    public static String getSettings(Context context){
        DataBaseHelper dbHelper = new DataBaseHelper(context);

        String sql = "select settings from smssetting";
        String settings = "";
        PGLog.d(TAG, sql);

        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                int ccnt = cursor.getColumnIndex("settings");
                settings = cursor.getString(ccnt);
            }
            cursor.close();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return settings;
    }

    public static int getDonatioinTotalAmount(Context context, String sql){
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        int totalAmount = 0;
        PGLog.d(TAG, sql);

        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                int summ = cursor.getColumnIndex("summ");
                totalAmount = cursor.getInt(summ);
            }
            cursor.close();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return totalAmount;
    }

    public static int getDonationInfoCount(Context context){
        int count = 0;
        String sql = "select count(*) as cnt from donation_info";
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex("cnt");
                count = cursor.getInt(idx);
            }
            cursor.close();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return count;
    }

    public static int getDonationInfoCount(Context context, String card, String category){
        int count = 0;
        String sql = "select count(*) as cnt from donation_info where card=\"" + card + "\" and category=\"" + category + "\"";
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex("cnt");
                count = cursor.getInt(idx);
            }
            cursor.close();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return count;
    }

    public static DonationInfo getDonationInfo(Context context, String card, String category){
        DonationInfo donationInfo = new DonationInfo();

        String sql = "select card, category, threshold, percent, thresholdoverall from donation_info where card=\"" + card + "\" and category=\"" + category + "\"";
        DataBaseHelper dbHelper = new DataBaseHelper(context);

        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    int idx = cursor.getColumnIndex("card");
                    String cardname = cursor.getString(idx);
                    donationInfo.setCard(cardname);
                    idx = cursor.getColumnIndex("category");
                    String catname = cursor.getString(idx);
                    donationInfo.setCategory(catname);
                    idx = cursor.getColumnIndex("threshold");
                    int threshold = cursor.getInt(idx);
                    donationInfo.setThreshold(threshold);
                    idx = cursor.getColumnIndex("percent");
                    int percent = cursor.getInt(idx);
                    donationInfo.setPercent(percent);
                    idx = cursor.getColumnIndex("thresholdoverall");
                    int thresholdoverall = cursor.getInt(idx);
                    donationInfo.setThresholdOverall(thresholdoverall == 1 ? true : false);
                } while ((cursor.moveToNext()));
            }
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }

        return donationInfo;
    }

    public static long insertDonationInfo(Context context, DonationInfo info){
        long rst = -1L;

        String cardnanme = info.getCard();
        String catname = info.getCategory();
        DataBaseHelper dbHelper = new DataBaseHelper(context);

        try{
            int cnt = getDonationInfoCount(context, cardnanme, catname);
            dbHelper.open();
            ContentValues values = new ContentValues();

            if(cnt!=0){
                values.put("threshold", info.getThreshold());
                values.put("percent", info.getPercent());
                values.put("thresholdoverall", info.isThresholdOverall()==true ? 1 : 0);
                rst = dbHelper.getmDB().update(DataBases.CreateDB._DONATION_INFO, values,
                        "card=\"" + cardnanme + "\" and category=\"" + catname + "\"", null);
            }else{
                values.put("card", info.getCard());
                values.put("category", info.getCategory());
                values.put("threshold", info.getThreshold());
                values.put("percent", info.getPercent());
                values.put("thresholdoverall", info.isThresholdOverall()==true ? 1 : 0);
                rst = dbHelper.getmDB().insert(DataBases.CreateDB._DONATION_INFO, null, values);
            }
        }catch (Exception e){
            e.printStackTrace();
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }

        return rst;
    }

    public static long deleteDonationInfo(Context context, DonationInfo info){
        long rst = -1L;

        String cardnanme = info.getCard();
        String catname = info.getCategory();
        DataBaseHelper dbHelper = new DataBaseHelper(context);

        try{
            dbHelper.deleteDonationInfo(info);
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }

        return rst;
    }

    public static ArrayList<DonationInfo> getSelectedDonation(Context context){
        ArrayList<DonationInfo> infos = new ArrayList<DonationInfo>();
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        try{
            String sql = "select * from donation_info";
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    int idx = cursor.getColumnIndex("card");
                    String card = cursor.getString(idx);
                    idx = cursor.getColumnIndex("category");
                    String category = cursor.getString(idx);
                    idx = cursor.getColumnIndex("threshold");
                    int threshold = cursor.getInt(idx);
                    idx = cursor.getColumnIndex("percent");
                    int percent = cursor.getInt(idx);
                    idx = cursor.getColumnIndex("thresholdoverall");
                    int thresholdoverall = cursor.getInt(idx);
                    DonationInfo info = new DonationInfo();
                    info.setCard(card);
                    info.setCategory(category);
                    info.setThreshold(threshold);
                    info.setPercent(percent);
                    info.setThresholdOverall(thresholdoverall == 1 ? true : false);
                    infos.add(info);
                } while ((cursor.moveToNext()));
            }
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
            return infos;
        }
    }

    public static boolean checkDonationHistory(Context context, DonationHistory history){
        boolean ret = false;

        int year = history.getYear();
        int month = history.getMonth();
        int day = history.getDay();

        DataBaseHelper dbHelper = new DataBaseHelper(context);
        try{
            dbHelper.open();
            String sql = "select count(*) from " + DataBases.CreateDB._DONATION_HISTORY
                    + " where year=" + String.valueOf(year)
                    + " and month=" + String.valueOf(month);
            SQLiteDatabase db = dbHelper.getmDB();
            Cursor cursor= db.rawQuery(sql, null);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                int count = cursor.getInt(0);
                ret = count > 0 ? true : false;
            }
        }catch (Exception e){
            ret = false;
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return ret;
    }

    public static long insertDonationHistory(Context context, DonationHistory history){
        long rst = -1L;

        int year = history.getYear();
        int month = history.getMonth();
        int day = history.getDay();
        int amount = history.getAmount();
        String donation = history.getDonationName();
        String account = history.getAccount();
        String bankname = history.getBankname();
        String home = history.getHome();
        String tel = history.getTel();
        String address = history.getAddress();
        String category = history.getCategory();

        DataBaseHelper dbHelper = new DataBaseHelper(context);

        try{
            dbHelper.open();
            ContentValues values = new ContentValues();
            values.put("year", year);
            values.put("month", month);
            values.put("day", day);
            values.put("amount", amount);
            values.put("donation", donation);
            values.put("account", account);
            values.put("bankname", bankname);
            values.put("home", home);
            values.put("tel", tel);
            values.put("address", address);
            values.put("category", category);

            rst = dbHelper.getmDB().insert(DataBases.CreateDB._DONATION_HISTORY, null, values);
            dbHelper.close();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
        return rst;
    }

    public static ArrayList<DonationHistory> getDonationHistory(Context context, String sql){
        ArrayList<DonationHistory> histories = new ArrayList<DonationHistory>();
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    int idx = cursor.getColumnIndex("year");
                    int year = cursor.getInt(idx);
                    idx = cursor.getColumnIndex("month");
                    int month = cursor.getInt(idx);
                    idx = cursor.getColumnIndex("day");
                    int day = cursor.getInt(idx);
                    idx = cursor.getColumnIndex("amount");
                    int amount = cursor.getInt(idx);
                    idx = cursor.getColumnIndex("donation");
                    String donation = cursor.getString(idx);
                    idx = cursor.getColumnIndex("account");
                    String account = cursor.getString(idx);
                    idx = cursor.getColumnIndex("bankname");
                    String bankname = cursor.getString(idx);
                    idx = cursor.getColumnIndex("home");
                    String home = cursor.getString(idx);
                    idx = cursor.getColumnIndex("tel");
                    String tel = cursor.getString(idx);
                    idx = cursor.getColumnIndex("address");
                    String address = cursor.getString(idx);
                    idx = cursor.getColumnIndex("category");
                    String category = cursor.getString(idx);

                    DonationHistory history = new DonationHistory();
                    history.setYear(year);
                    history.setMonth(month);
                    history.setDay(day);
                    history.setAmount(amount);
                    history.setDonationName(donation);
                    history.setAmount(amount);
                    history.setBankname(bankname);
                    history.setHome(home);
                    history.setTel(tel);
                    history.setAddress(address);
                    history.setCategory(category);

                    histories.add(history);
                } while ((cursor.moveToNext()));
            }
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
            return histories;
        }
    }

    public static String getCategoryService(Context context){
        DataBaseHelper dbHelper = new DataBaseHelper(context);

        String sql = "select hash from " + DataBases.CreateDB._CATEGORY_SERVICE + " order by timestamp desc";
        String hash = "";
        try{
            dbHelper.open();
            Cursor cursor = dbHelper.getData(sql);
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex("hash");
                hash = cursor.getString(idx);
            }
            cursor.close();
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return hash;
    }

    public static long insertCategoryService(Context context, String newHash, String oldHash){
        long rst = -1L;
        DataBaseHelper dbHelper = new DataBaseHelper(context);

        try{
            dbHelper.open();
            long timestamp = System.currentTimeMillis();
            ContentValues values = new ContentValues();
            values.put("timestamp", timestamp);
            values.put("hash", newHash);
            if("".equals(oldHash)) {
                rst = dbHelper.getmDB().insert(DataBases.CreateDB._CATEGORY_SERVICE, null, values);
            }else{
                rst = dbHelper.getmDB().update(DataBases.CreateDB._CATEGORY_SERVICE,  values, "hash=\"" + oldHash + "\"", null);
            }
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
        return rst;
    }

    public static void dbInitialize(Context context){
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        try{
            dbHelper.open();
            SQLiteDatabase mDB = dbHelper.getmDB();
            mDB.execSQL("DELETE  FROM " + DataBases.CreateDB._DATA_TABLENAME);
            mDB.execSQL("DELETE  FROM " + DataBases.CreateDB._SETTINGS_TABLENAME);
            mDB.execSQL("DELETE  FROM " + DataBases.CreateDB._DONATION_INFO);
            mDB.execSQL("DELETE  FROM " + DataBases.CreateDB._DONATION_HISTORY);
            mDB.execSQL("DELETE  FROM " + DataBases.CreateDB._CATEGORY_SERVICE);
        }catch (Exception e){
            e.printStackTrace();
            //PGLog.e(TAG, StackTrace.getStackTrace(e));
        }finally {
            dbHelper.close();
        }
    }
}
