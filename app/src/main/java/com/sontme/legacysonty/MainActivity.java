package com.sontme.legacysonty;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity implements GpsStatus.Listener {
    TextView txt;
    TextView statsTextview;
    SeekBar seekBar;
    TextView seekval;

    static Handler statsHandler;
    static Runnable statsRunnable;

    LocationManager locationManager;

    public ServiceConnection mServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Toast.makeText(getApplicationContext(), "SERVICE CONNECTED", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(getApplicationContext(), "SERVICE DISCONNECTED", Toast.LENGTH_LONG).show();
        }
    };

    public void managePermissions() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String[] permissions = info.requestedPermissions;

        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        String[] PERMISSIONS_ALL = Stream.concat(Arrays.stream(permissions), Arrays.stream(PERMISSIONS))
                .toArray(String[]::new);

        if (!hasPermissions(getApplicationContext(), PERMISSIONS_ALL)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_ALL, 1);
        }

        PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        String packageName = getPackageName();
        Intent ii = new Intent();
        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            ii.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            ii.setData(Uri.parse("package:" + packageName));
            startActivity(ii);
        }

        ii.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        ii.setData(Uri.parse("package:" + packageName));
        startActivity(ii);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        233);
            }
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSION_CHECK", "MISSING PERMISSION NOW GRANTED: " + permission);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        managePermissions();
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String[] permissions = info.requestedPermissions;

        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        String[] PERMISSIONS_ALL = Stream.concat(Arrays.stream(permissions), Arrays.stream(PERMISSIONS))
                .toArray(String[]::new);
        if (!hasPermissions(getApplicationContext(), PERMISSIONS_ALL)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_ALL, 1);
        }


        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    PERMISSIONS_STORAGE,
                    4
            );
        }
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS1 = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
            ActivityCompat.requestPermissions(this,
                    PERMISSIONS1, 1);
        }
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> tasks = am.getAppTasks();
        if (tasks != null && tasks.size() > 0)
            tasks.get(0).setExcludeFromRecents(true);
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        if (mode != AppOpsManager.MODE_ALLOWED) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.addGpsStatusListener(this);

            TextView txt3 = findViewById(R.id.txt3);
            int a = BackgroundService.cnt_new;
            txt3.setText("Service Status: Unknown2");
            txt3.setText("Service Status: Unknown2_2");

            txt = findViewById(R.id.txt);
            statsTextview = findViewById(R.id.txt2);
            seekBar = findViewById(R.id.seekBar);
            seekval = findViewById(R.id.seekval2);
            seekBar.setContentDescription("Executor Pool Core Size");
            seekBar.setHapticFeedbackEnabled(true);
            seekBar.setProgress(1);
            seekval.setText("Executor Pool Core Size: " + seekBar.getProgress());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                seekBar.setMin(1);
            }
            seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.argb(50, 130, 30, 30), PorterDuff.Mode.MULTIPLY));
            seekBar.setMax(20);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (progress < 1) {
                        seekBar.setProgress(1);
                        seekval.setText("Executor Pool Core Size: " + 1);
                        seekBar.setProgress(1);
                    } else if (progress >= 1) {
                        BackgroundService.webRequestExecutor.setCorePoolSize(progress);
                        seekval.setText("Executor Pool Core Size: " + progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            Intent i = new Intent(MainActivity.this, BackgroundService.class);
            startService(i);
            bindService(i, mServerConn, Context.BIND_AUTO_CREATE);

            final Handler locationRequestHandler = new Handler();
            locationRequestHandler.postDelayed(new Runnable() {
                public void run() {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    try {
                        BackgroundService.locationManager.requestSingleUpdate(
                                LocationManager.NETWORK_PROVIDER,
                                BackgroundService.locationListener, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    locationRequestHandler.postDelayed(this, 1000);
                }
            }, 1000);

            statsHandler = new Handler();
            statsHandler.postDelayed(statsRunnable = new Runnable() {
                public void run() {
                    try {
                        Intent i = new Intent(MainActivity.this, BackgroundService.class);
                        startService(i);
                        int executorServiceQueueSize = BackgroundService.webRequestExecutor.getQueue().size();
                        int executorServiceActiveCount = BackgroundService.webRequestExecutor.getActiveCount();
                        String threadString = "Active Threads: " + Thread.activeCount() + "\n" +
                                "Executor Pool Queue: " + executorServiceQueueSize + "\n" +
                                "Executor Pool Alive: " + executorServiceActiveCount + "\n" +
                                "Elapsed: " + BackgroundService.startedAtTime.getElapsed() + "\n" +
                                "Bluetooth File: " + roundBandwidth(BackgroundService.readExternalPublic(getApplicationContext(), "BLsession.txt").length());
                        txt.setText(threadString);
                        String stats = "";

                        stats = "Not: " + BackgroundService.cnt_notrecorded + " " +
                                "Error: " + BackgroundService.Live_Http_GET_SingleRecord.error_counter + " " +
                                "New: " + BackgroundService.cnt_new + " " +
                                "Time: " + BackgroundService.cnt_updated_time + " " +
                                "Str: " + BackgroundService.cnt_updated_str;

                        statsTextview.setText(stats);
                        TextView serviceStatusTextview = findViewById(R.id.txt3);
                        serviceStatusTextview.setText("Service Status: Unknown");
                        statsHandler.postDelayed(this, 250);
                    } catch (Exception e) {
                        int executorServiceQueueSize = BackgroundService.webRequestExecutor.getQueue().size();
                        int executorServiceActiveCount = BackgroundService.webRequestExecutor.getActiveCount();
                        String threadString = "Active Threads: " + Thread.activeCount() + "\n" +
                                "Executor Pool Queue: " + executorServiceQueueSize + "\n" +
                                "Executor Pool Alive: " + executorServiceActiveCount + "\n" +
                                "Elapsed: " + BackgroundService.startedAtTime.getElapsed();
                        txt.setText(threadString);
                        String stats = "";

                        stats = "Not: " + BackgroundService.cnt_notrecorded + " " +
                                "Error: " + BackgroundService.Live_Http_GET_SingleRecord.error_counter + " " +
                                "New: " + BackgroundService.cnt_new + " " +
                                "Time: " + BackgroundService.cnt_updated_time + " " +
                                "Str: " + BackgroundService.cnt_updated_str;

                        statsTextview.setText(stats);
                        TextView serviceStatusTextview = findViewById(R.id.txt3);
                        serviceStatusTextview.setText("Service Status: Unknown");
                        statsHandler.postDelayed(this, 250);
                    }
                }
            }, 3000);

            boolean rooted = BackgroundService.RootUtil.isDeviceRooted();
            boolean emulator = BackgroundService.AdminTOOLS.checkIfDeviceIsEmulator(getApplicationContext());
            statsTextview.setText("ROOTED: " + rooted + "\n" + "EMULATOR: " + emulator);
        } else {
            Toast.makeText(getApplicationContext(), "Missing location permission!", Toast.LENGTH_LONG).show();
        }
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        } else {
            //TODO
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {
        TextView txt4 = findViewById(R.id.txt4);
        try {
            switch (event) {
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    if (BackgroundService.CURRENT_LOCATION != null) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        }
                        long lastUpdate;
                        try {
                            lastUpdate = BackgroundService.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getTime();
                            lastUpdate = BackgroundService.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getTime();
                        } catch (Exception e) {
                            lastUpdate = BackgroundService.CURRENT_LOCATION.getTime();
                        }

                        if ((SystemClock.elapsedRealtime() - BackgroundService.CURRENT_LOCATION_LASTTIME)
                                < (10000)) {
                            txt4.setText("STILL HAS GPS FIX [" + BackgroundService.LOCATON_CHANGE_COUNTER + "] " + lastUpdate);
                        } else {
                            txt4.setText("LOST GPS FIX [" + BackgroundService.LOCATON_CHANGE_COUNTER + "] " + lastUpdate);
                        }
                    }
                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    txt4.setText("FIRST LOCATION");
                    //Toast.makeText(getApplicationContext(), "GPS: FIRST LOCATION", Toast.LENGTH_LONG).show();
                    break;
                case GpsStatus.GPS_EVENT_STARTED:
                    txt4.setText("GPS STARTED");
                    //Toast.makeText(getApplicationContext(), "GPS: STARTED", Toast.LENGTH_LONG).show();
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    txt4.setText("GPS STOPPED");
                    //Toast.makeText(getApplicationContext(), "GPS: STOPPED", Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        Intent i = new Intent(MainActivity.this, BackgroundService.class);
        startService(i);
        bindService(i, mServerConn, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    public static String roundBandwidth(long bytes) {
        long b = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        return b < 1024L ? bytes + " B"
                : b <= 0xfffccccccccccccL >> 40 ? String.format("%.1f kb", bytes / 0x1p10)
                : b <= 0xfffccccccccccccL >> 30 ? String.format("%.1f mb", bytes / 0x1p20)
                : b <= 0xfffccccccccccccL >> 20 ? String.format("%.1f gb", bytes / 0x1p30)
                : b <= 0xfffccccccccccccL >> 10 ? String.format("%.1f tb", bytes / 0x1p40)
                : b <= 0xfffccccccccccccL ? String.format("%.1f pb", (bytes >> 10) / 0x1p40)
                : String.format("%.1f eb", (bytes >> 20) / 0x1p40);
    }
}