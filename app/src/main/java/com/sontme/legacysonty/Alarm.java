package com.sontme.legacysonty;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent2) {
        Intent serviceIntent = new Intent(context, BackgroundService.class);
        context.startService(serviceIntent);
        Log.d("ALARM_", "RAN!");
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm.class);
        intent.putExtra("requestCode", 66);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 66, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                1 * 30 * 1000, pendingIntent);

    }

    public void setAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        i.putExtra("requestCode", 66);
        PendingIntent pi = PendingIntent.getBroadcast(context, 66, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10000, pi); // Millisec * Second * Minute
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, Alarm.class);
        intent.putExtra("requestCode", 66);
        PendingIntent sender = PendingIntent.getBroadcast(context, 66, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
