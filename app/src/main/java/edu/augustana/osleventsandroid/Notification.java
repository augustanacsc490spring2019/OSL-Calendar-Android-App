package edu.augustana.osleventsandroid;

import android.app.AlarmManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.content.Context;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;

import java.util.Calendar;
import java.util.Date;

public class Notification  extends AppCompatActivity{

    String date;
    String time;
    String name;

    public Notification(String date, String time, String name){
        this.date = date;
        this.time = time;
        this.name = name;
    }

    private void showNotification(Context context) {

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, Notification.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());



        AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
        Date dateNotify = new Date();
        dateNotify.setTime(System.currentTimeMillis());
        //calendar1Notify.set(Calendar.HOUR_OF_DAY, );
        //calendar1Notify.set(Calendar.MINUTE, 00);

        //alarmManager1.set(AlarmManager.RTC_WAKEUP,calendar1Notify.getTimeInMillis(), contentIntent);

        long time24h = 24*60*60*1000;

        //alarmManager1.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar1Notify.getTimeInMillis(),time24h,contentIntent);

    }

}
