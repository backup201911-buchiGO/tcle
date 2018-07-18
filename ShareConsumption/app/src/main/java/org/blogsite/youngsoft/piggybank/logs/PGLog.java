package org.blogsite.youngsoft.piggybank.logs;

import android.util.Log;

public class PGLog {
    public static void d(String TAG, String log){
        try {
            Log.d(TAG, log);
        }catch (Exception e){

        }
    }

    public static void i(String TAG, String log){
        try {
            Log.e(TAG, log);
        }catch (Exception e){

        }
    }

    public static void w(String TAG, String log){
        try {
            Log.w(TAG, log);
        }catch (Exception e){

        }
    }

    public static void e(String TAG, String log){
        try {
            LogWriter.e(TAG, log);
            Log.e(TAG, log);
        }catch (Exception e){

        }
    }
}
