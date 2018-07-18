package org.blogsite.youngsoft.piggybank.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.blogsite.youngsoft.piggybank.logs.PGLog;

import java.util.ArrayList;
import java.util.List;

public class ReceiverManager {
    private static List<BroadcastReceiver> receivers = new ArrayList<BroadcastReceiver>();
    private static ReceiverManager ref;
    private Context context;

    private ReceiverManager(Context context){
        this.context = context;
    }

    public static synchronized ReceiverManager init(Context context) {
        if (ref == null) ref = new ReceiverManager(context);
        return ref;
    }
/*
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter intentFilter){
        if(!receivers.contains(receiver)) {
            receivers.add(receiver);
            Intent intent = context.registerReceiver(receiver, intentFilter);
            Log.i(getClass().getSimpleName(), "registered receiver: " + receiver + "  with filter: " + intentFilter);
            Log.i(getClass().getSimpleName(), "receiver Intent: " + intent);
            return intent;
        }
    }
*/

    public void registerReceiver(BroadcastReceiver receiver, IntentFilter intentFilter){
        try {
            if (!receivers.contains(receiver)) {
                receivers.add(receiver);
                Intent intent = context.registerReceiver(receiver, intentFilter);
                PGLog.i(getClass().getSimpleName(), "registered receiver: " + receiver + "  with filter: " + intentFilter);
                PGLog.i(getClass().getSimpleName(), "receiver Intent: " + intent);
            }
        }catch (Exception e){

        }
    }

    public boolean isReceiverRegistered(BroadcastReceiver receiver){
        boolean registered = receivers.contains(receiver);
        PGLog.i(getClass().getSimpleName(), "is receiver "+receiver+" registered? "+registered);
        return registered;
    }

    public void unregisterReceiver(BroadcastReceiver receiver){
        try {
            if (isReceiverRegistered(receiver)) {
                receivers.remove(receiver);
                PGLog.i(getClass().getSimpleName(), "unregistered receiver: " + receiver);
            }
            context.unregisterReceiver(receiver);
        }catch (Exception e){

        }
    }
}
