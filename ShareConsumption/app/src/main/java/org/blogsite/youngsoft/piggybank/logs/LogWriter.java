package org.blogsite.youngsoft.piggybank.logs;

import android.util.Log;

import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogWriter {
    private static final String TAG = "LogWriter";
    private static final String LOGFILE = "piggybank.log";
    private static String logFile = "";

    private static void logFilePath() throws Exception{
        String dbPath = SmsUtils.getDevicePath() + "PiggyBank/";
        logFile = dbPath + LOGFILE;
        File file = new File(logFile);
        if (!file.exists()) {
            try {
                file.createNewFile();
            }catch (Exception e){
                throw e;
            }
        }
    }
/*
    public static void initLog(){
        String dbPath = SmsUtils.getDevicePath() + "PiggyBank/";
        logFile = dbPath + LOGFILE;
        File file = new File(logFile);
        if (file.exists()) {
            try{
                file.delete();
            }catch (Exception e){
                Log.e(TAG, StackTrace.getStackTrace(e));
            }
        }
    }
*/
    public static void d(String tag, String str){
        log("DEBUG", tag, str);
    }

    public static void e(String tag, String str){
        log("ERROR", tag, str);
    }

    public static void i(String tag, String str){
        log("INFO", tag, str);
    }

    public static void log(String mode, String tag, String str){
        try{
            logFilePath();

            DateFormat df = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
            String s = df.format(new Date());

            BufferedWriter bfw = new BufferedWriter(new FileWriter(logFile,true));
            bfw.write(mode + "\t" + s + "\t" + tag + "\t" + str);
            bfw.write("\n");
            bfw.flush();
            bfw.close();
        }catch (Exception e){
            Log.e(TAG, StackTrace.getStackTrace(e));
        }
    }
}
