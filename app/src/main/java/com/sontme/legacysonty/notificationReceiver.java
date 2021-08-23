package com.sontme.legacysonty;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import java.util.Iterator;
import java.util.Set;

public class notificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /*Set<String> set = BackgroundService.SimpleAlarmManager.getAllRegistrationIds(context);
        for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
            int id = Integer.parseInt(it.next());
            BackgroundService.SimpleAlarmManager.initWithId(context, id).start();
        }
        if (intent.getAction() == Intent.ACTION_BOOT_COMPLETED || intent.getAction() == Intent.ACTION_REBOOT || intent.getAction() == "android.intent.action.QUICKBOOT_POWERON") {
            Intent i = new Intent(context, BackgroundService.class);
            context.startService(i);
        }*/
        int requestCode = intent.getExtras().getInt("requestCode");
        if (intent.getAction() == "exit" || requestCode == 99) {
            try {
                BackgroundService.vibrate(context);
                context.stopService(new Intent(context, BackgroundService.class));
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(2);
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
                BackgroundService.locationManager.removeUpdates(BackgroundService.locationListener);
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

    }
}
