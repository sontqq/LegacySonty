package com.sontme.legacysonty;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Iterator;
import java.util.Set;

public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent2) {
        //Set<String> set = BackgroundService.SimpleAlarmManager.getAllRegistrationIds(context);
        //for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
        //int id = Integer.parseInt(it.next());
        //BackgroundService.SimpleAlarmManager.initWithId(context, id).start();
        //}
        //int code = intent2.getIntExtra("requestCode", 1);
        //if (code == 66)
        //  BackgroundService.vibrate(context);
        Intent serviceIntent = new Intent(context, BackgroundService.class);
        context.startService(serviceIntent);

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
