package org.blogsite.youngsoft.piggybank.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.activity.SmsListActivity;
import org.blogsite.youngsoft.piggybank.analyzer.Data;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.utils.CategoryEnum;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;

/**
 * PushHelper.java
 *
 * GCM에 관련된 모든 처리를 하는 메소드.
 *
 */
public class PushHelper {
    private static final String TAG = "PushHelper";

    public static final int FLAG_NOTIFY = 1;
    public static final int FLAG_NOTICE = 2;

    // JSONObject jObj;

    /**
     * 노티피케이션과 다이얼로그를 보여준다.<br>
     * gcm을 받게 되면 이 함수를 사용한다.
     * @param context
     * @param datas
     */
    public static void notify(Context context, Bundle datas, Data data) {
        showNotification(context, datas, data);
    }

    /**
     * 노티피케이션을 표시한다.
     * @param context
     * @param datas
     */
    private static void showNotification(Context context, Bundle datas, Data data) {
        try {
            CategoryEnum category = data.getCategory();
            int resId = R.drawable.unclassified;
            if (category == CategoryEnum.Meal) {
                resId = R.drawable.meal;
            } else if (category == CategoryEnum.Culture) {
                resId = R.drawable.culture;
            } else if (category == CategoryEnum.Medical) {
                resId = R.drawable.medical;
            } else if (category == CategoryEnum.Communication) {
                resId = R.drawable.communication;
            } else if (category == CategoryEnum.Traffic) {
                resId = R.drawable.traffic;
            } else if (category == CategoryEnum.Dues) {
                resId = R.drawable.dues;
            } else if (category == CategoryEnum.Shopping) {
                resId = R.drawable.shopping;
            }

            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), resId);

            String msg = data.getCard().getName() + " " + data.getCategory().getName() + " "
                    + StringUtils.format(data.getAmount(), "#,###") + SmsUtils.getResource(R.string.consumption_won);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.notification)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setTicker(SmsUtils.getResource(R.string.noti_title))
                    .setContentText(msg)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{1000, 1000})
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            //BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle(builder);
            //Bitmap bigPictureBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            //bigPictureStyle.bigPicture(bigPictureBitmap)

            //NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle(builder);
            //bigTextStyle.setBigContentTitle(context.getResources().getString(R.string.app_name)).setSummaryText(msg);

            Intent intent = getContentIntent(context);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            if (datas != null) {
                datas.putBoolean("noticall", true);
                intent.putExtras(datas);
            }

            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    context, (int) System.currentTimeMillis(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(resultPendingIntent);


            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            //mNotificationManager.notify((int) System.currentTimeMillis(), bigTextStyle.build());

            mNotificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }

    }

    /**
     * 메인화면 이동을 위한 인텐트를 얻는다.
     * @param context
     * @return
     */
    public static final Intent getContentIntent (Context context) {
        Intent intent = new Intent(context.getApplicationContext(), SmsListActivity.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
        return intent;
    }
}

