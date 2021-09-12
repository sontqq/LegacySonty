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
            try {
                BackgroundService.vibrate(context);
                NotificationManager nm = context.getSystemService(NotificationManager.class);
                nm.cancelAll();
                context.stopService(new Intent(context, BackgroundService.class));
                System.exit(2);
                System.exit(1);
                System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());

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
            try {
                //CustomAlertDialog alert = new CustomAlertDialog(context);
                //alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                //alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
                //alert.show();
            } catch (Exception e) {
                Toast.makeText(context, "Error! " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        if (intent.getAction() == "test2" || requestCode == 888) {
            //String selected = BackgroundService.connectStrongestOpenWifi(context,BackgroundService.wifiManager.getScanResults());
            //Toast.makeText(context, "Connecting Strongest Open WiFi: [" + selected +"]", Toast.LENGTH_LONG).show();
        }
    }
}
