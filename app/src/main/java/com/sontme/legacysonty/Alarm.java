package com.sontme.legacysonty;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.BATTERY_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent2) {
        /*if (BackgroundService.android_id_source_device.equals("ANYA_XIAOMI")) {
            try {
                BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
                int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                String keepaliveResponse =
                        BackgroundService.Live_Http_GET_SingleRecord.executeRequest(
                                "sont.sytes.net", 80, "keepalive.php?" +
                                        BackgroundService.android_id_source_device + "_" +
                                        BackgroundService.locationToStringAddress(context, BackgroundService.CURRENT_LOCATION).trim() + "_bat:" + batLevel, false,
                                "source=" + BackgroundService.android_id_source_device
                        );
                Log.d("KEEP_ALIVE_", "RESPONSE=" + keepaliveResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Thread keepThread = new Thread() {
                    @Override
                    public void run() {
                        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
                        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                        String keepaliveResponse =
                                BackgroundService.Live_Http_GET_SingleRecord.executeRequest(
                                        "sont.sytes.net", 80, "keepalive.php?" +
                                                BackgroundService.android_id_source_device + "_" +
                                                BackgroundService.locationToStringAddress(context, BackgroundService.CURRENT_LOCATION).trim() + "_bat:" + batLevel, false,
                                        "source=" + BackgroundService.android_id_source_device
                                );
                        Log.d("KEEP_ALIVE_", "RESPONSE=" + keepaliveResponse);
                        super.run();
                    }
                };
                keepThread.start();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }*/
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
