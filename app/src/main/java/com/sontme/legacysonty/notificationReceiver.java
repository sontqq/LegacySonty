package com.sontme.legacysonty;

import static android.content.Context.ALARM_SERVICE;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class notificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int requestCode = intent.getExtras().getInt("requestCode");
        if (intent.getAction().equals("exit") || requestCode == 159) {
            Intent i = new Intent(context, BackgroundService.class);
            try {
                Intent intent2 = new Intent(context, Alarm.class);
                intent2.putExtra("requestCode", 66);
                PendingIntent sender = PendingIntent.getBroadcast(context, 66, intent2, 0);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                alarmManager.cancel(sender);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                context.stopService(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                android.os.Process.killProcess(android.os.Process.myPid());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                System.exit(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (intent.getAction() == "network" || requestCode == 101) {
            try {
                BackgroundService.locationManager.removeUpdates(BackgroundService.locationListener);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }
                BackgroundService.locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        BackgroundService.TIME, BackgroundService.DISTANCE,
                        BackgroundService.locationListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (intent.getAction() == "gps" || requestCode == 102) {
            try {
                BackgroundService.locationManager.removeUpdates(
                        BackgroundService.locationListener);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }
                BackgroundService.locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        BackgroundService.TIME, BackgroundService.DISTANCE,
                        BackgroundService.locationListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (intent.getAction() == "test" || requestCode == 999) {

        }
        if (intent.getAction() == "test2" || requestCode == 888) {

        }
    }
}
