package com.sontme.legacysonty;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.Iterator;
import java.util.Set;

public class notificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int requestCode = intent.getExtras().getInt("requestCode");
        if (intent.getAction() == "exit" || requestCode == 99) {

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
