package com.sontme.legacysonty;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.google.android.gms.analytics.HitBuilders;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;


public class MainActivity extends AppCompatActivity {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    TextView txt;
    TextView statsTextview;
    //SeekBar seekBar;
    //TextView seekval;
    public TextView alertText1;
    public TextView alertText2;
    public TextView alertText3;
    static Handler statsHandler;
    static Runnable statsRunnable;

    Handler handler_no;
    Handler handler_yes;
    Runnable runnable_no;
    Runnable runnable_yes;

    LocationManager locationManager;

    TextView txt3;
    TextView txt4;
    Button BTN_cellTowers;
    Button BTN_BL_customPair;
    Button BTN_nearbySend;
    Button BTN_startNearby;
    Button BTN_stopNearby;
    Button BTN_RerunQues;
    Button BTN_connectOpenWifi;
    Button BTN_wifi_color;
    Button BTN_scans;
    final int[] y = {1};

    public ServiceConnection mServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            //Toast.makeText(getApplicationContext(), "SERVICE CONNECTED", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(getApplicationContext(), "SERVICE DISCONNECTED", Toast.LENGTH_LONG).show();
        }
    };

    public void managePermissions() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(),
                    PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String[] permissions = info.requestedPermissions;

        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_PHONE_STATE
        };

        String[] PERMISSIONS_ALL = Stream.concat(Arrays.stream(permissions), Arrays.stream(PERMISSIONS))
                .toArray(String[]::new);

        /*if (!hasPermissions(getApplicationContext(), PERMISSIONS_ALL)) {
            //ActivityCompat.requestPermissions(this, PERMISSIONS_ALL, 1);
        }*/

        for (String permission : PERMISSIONS_ALL) {
            if (!hasPermissions(getApplicationContext(), permission)) {
                String[] permholder = new String[1];
                permholder[0] = permission;
                ActivityCompat.requestPermissions(this, permholder, 1);
            }
        }

        String packageName = getPackageName();
        Intent powerIgnoreIntent = new Intent();
        powerIgnoreIntent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        powerIgnoreIntent.setData(Uri.parse("package:" + packageName));
        startActivity(powerIgnoreIntent);

    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSION_CHECK", "MISSING PERMISSION NOW GRANTED: " + permission);
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            99);
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

        managePermissions();

        Intent i = new Intent(
                MainActivity.this, BackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, i);
        } else {
            startService(i);
        }

        runnable_no = new Runnable() {
            @Override
            public void run() {
                /*alertText.setText(Html.fromHtml(
                        BackgroundService.locationToStringAddress(getApplicationContext(), BackgroundService.CURRENT_LOCATION),
                        Html.FROM_HTML_MODE_LEGACY));*/
                try {
                    alertText1.setText(BackgroundService.Live_Http_GET_SingleRecord.lastHandledURL);
                    alertText2.setText("Errors: " + BackgroundService.Live_Http_GET_SingleRecord.cnt_httpError);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler_no.postDelayed(this, 1000);
            }
        };

        runnable_yes = new Runnable() {
            @Override
            public void run() {
                try {
                    List<ScanResult> temp = BackgroundService.wifiManager.getScanResults();
                    Collections.sort(temp, new Comparator<ScanResult>() {
                        @Override
                        public int compare(android.net.wifi.ScanResult o1, android.net.wifi.ScanResult o2) {
                            return Integer.compare(o1.level, o2.level);
                        }
                    });
                    Collections.reverse(temp);
                    String msg = "";
                    for (ScanResult sr : temp) {
                        msg = msg + "<b>" + sr.SSID + "</b> | " + sr.level + " | " + BackgroundService.getScanResultSecurity(sr) + "<br>";
                    }
                    alertText1.setText(Html.fromHtml(msg, Html.FROM_HTML_MODE_LEGACY));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler_yes.postDelayed(this, 1000);
            }
        };

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            managePermissions();
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            locationManager.addGpsStatusListener(new GpsStatus.Listener() {
                @Override
                public void onGpsStatusChanged(int event) {
                    try {
                        switch (event) {
                            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                                if (BackgroundService.CURRENT_LOCATION != null) {
                                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                            ActivityCompat.checkSelfPermission(getApplicationContext(),
                                                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    }
                                    long lastUpdate;
                                    String ago = "no info yet";
                                    try {
                                        lastUpdate = BackgroundService.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getTime();
                                        lastUpdate = BackgroundService.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getTime();
                                        ago = getTimeAgo(lastUpdate);
                                        if (ago == null)
                                            ago = getTimeAgo(System.currentTimeMillis());
                                        if (ago == null)
                                            ago = String.valueOf(System.currentTimeMillis());
                                    } catch (Exception e) {
                                        lastUpdate = BackgroundService.CURRENT_LOCATION.getTime();
                                    }
                                    if ((SystemClock.elapsedRealtime() - BackgroundService.CURRENT_LOCATION_LASTTIME)
                                            < (10000)) {
                                        txt4.setText(Html.fromHtml("Provider: " + BackgroundService.CURRENT_LOCATION.getProvider() + "<br>" +
                                                "STILL HAS GPS FIX [" + BackgroundService.LOCATON_CHANGE_COUNTER + "] <b>" + ago + "</b>", Html.FROM_HTML_MODE_LEGACY));
                                    } else {
                                        txt4.setText(Html.fromHtml("LOST GPS FIX [" +
                                                BackgroundService.LOCATON_CHANGE_COUNTER +
                                                "] <b>" + ago + "</b>", Html.FROM_HTML_MODE_LEGACY));
                                    }
                                }
                                break;
                            case GpsStatus.GPS_EVENT_FIRST_FIX:
                                txt4.setText("FIRST LOCATION " +
                                        Html.fromHtml("<b>" + BackgroundService.getTimeAgo(System.currentTimeMillis()) + "</b>", Html.FROM_HTML_MODE_LEGACY));
                                break;
                            case GpsStatus.GPS_EVENT_STARTED:
                                txt4.setText("GPS STARTED " +
                                        Html.fromHtml("<b>" + BackgroundService.getTimeAgo(System.currentTimeMillis()) + "</b>", Html.FROM_HTML_MODE_LEGACY));
                                break;
                            case GpsStatus.GPS_EVENT_STOPPED:
                                txt4.setText("GPS STOPPED " +
                                        Html.fromHtml("<b>" + BackgroundService.getTimeAgo(System.currentTimeMillis()) + "</b>", Html.FROM_HTML_MODE_LEGACY));
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            locationManager.registerGnssStatusCallback(new GnssStatus.Callback() {
                @Override
                public void onStarted() {
                    super.onStarted();
                    try {
                        txt3.setText("GPS/GNSS Started " +
                                Html.fromHtml("<b>" + BackgroundService.getTimeAgo(System.currentTimeMillis()) + "</b>", Html.FROM_HTML_MODE_LEGACY));
                    } catch (Exception e) {
                        txt3.setText("GPS/GNSS Started");
                    }
                }

                @Override
                public void onStopped() {
                    super.onStopped();
                    try {
                        txt3.setText("GPS/GNSS Stopped " +
                                Html.fromHtml("<b>" + BackgroundService.getTimeAgo(System.currentTimeMillis()) + "</b>", Html.FROM_HTML_MODE_LEGACY));
                    } catch (Exception e) {
                        txt3.setText("GPS/GNSS Stopped");
                    }
                }

                @Override
                public void onFirstFix(int ttffMillis) {
                    super.onFirstFix(ttffMillis);
                    try {
                        txt3.setText("First FIX: " + Html.fromHtml("<b>" + BackgroundService.getTimeAgo(ttffMillis) + "</b>", Html.FROM_HTML_MODE_LEGACY));
                    } catch (Exception e) {
                        txt3.setText("First FIX: " + ttffMillis);
                    }
                }

                @Override
                public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                    super.onSatelliteStatusChanged(status);
                    try {
                        ArrayList<String> summed_constellation = new ArrayList<>();
                        String summed_for_notif = "";
                        for (int i = 0; i < status.getSatelliteCount(); i++) {
                            String type = convertConstellation(status.getConstellationType(i));
                            summed_constellation.add(type);
                        }
                        Set<String> uniqueSet = new HashSet<String>(
                                summed_constellation);
                        TreeSet<String> orderedSet = new TreeSet(uniqueSet);
                        orderedSet = (TreeSet) orderedSet.descendingSet();
                        for (String s : orderedSet) {
                            summed_for_notif += s + ": " + Collections.frequency(summed_constellation, s) + "\n";
                        }

                        txt4.setText("Provider: " + BackgroundService.CURRENT_LOCATION.getProvider() +
                                "\nSatellite count: " + status.getSatelliteCount() + "\n" +
                                summed_for_notif);
                    } catch (Exception e) {
                        txt4.setText(e.getMessage());
                    }
                }
            });
        }

        txt = findViewById(R.id.txt);
        statsTextview = findViewById(R.id.txt2);
        txt3 = findViewById(R.id.txt3);
        txt4 = findViewById(R.id.txt4);

        /*seekBar = findViewById(R.id.seekBar);
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
                    seekval.setText("Executor Pool Core Size: 1");
                    seekBar.setProgress(1);
                } else {
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
        */

        try {
            bindService(i, mServerConn, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        statsHandler = new Handler();
        statsHandler.postDelayed(statsRunnable = new Runnable() {
            public void run() {
                try {
                    Intent i = new Intent(MainActivity.this, BackgroundService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(getApplicationContext(), i);
                    } else {
                        startService(i);
                    }
                    int executorServiceQueueSize = BackgroundService.webRequestExecutor.getQueue().size();
                    int executorServiceActiveCount = BackgroundService.webRequestExecutor.getActiveCount();
                    int executorServiceMax = BackgroundService.webRequestExecutor.getLargestPoolSize();
                    String threadString = "Active Threads: " + Thread.activeCount() + "\n" +
                            "Executor Pool Queue: " + executorServiceQueueSize + "[" + executorServiceMax + "]" + "\n" +
                            "Executor Pool Alive: " + executorServiceActiveCount + "\n" +
                            "Elapsed: " + BackgroundService.startedAtTime.getElapsed() + "\n"
                            /*"Bluetooth File: " + roundBandwidth(BackgroundService.readExternalPublic(getApplicationContext(), "BLsession.txt").length())*/;
                    txt.setText(threadString);
                    String stats = "";

                    stats = "Not: " + BackgroundService.cnt_notrecorded + " " +
                            "Error: " + BackgroundService.Live_Http_GET_SingleRecord.cnt_httpError + " " +
                            "New: " + BackgroundService.cnt_new + " " +
                            "Time: " + BackgroundService.cnt_updated_time + " " +
                            "BLName: " + BackgroundService.cnt_nameUpdated + " " +
                            "Str: " + BackgroundService.cnt_updated_str;

                    statsTextview.setText(stats);
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
                            "Error: " + BackgroundService.Live_Http_GET_SingleRecord.cnt_httpError + " " +
                            "New: " + BackgroundService.cnt_new + " " +
                            "Time: " + BackgroundService.cnt_updated_time + " " +
                            "BLName: " + BackgroundService.cnt_nameUpdated + " " +
                            "Str: " + BackgroundService.cnt_updated_str;
                    statsTextview.setText(stats);
                    statsHandler.postDelayed(this, 250);
                }
            }
        }, 3000);

        BTN_RerunQues = findViewById(R.id.btn_reque);
        BTN_connectOpenWifi = findViewById(R.id.btn_openwifi);
        BTN_wifi_color = findViewById(R.id.btn_wifi_color);
        BTN_scans = findViewById(R.id.btn_scans);

        BTN_wifi_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int max = 255;
                int x = mapHpToColor(y[0], max);
                BTN_wifi_color.setBackgroundColor(y[0]);
                BTN_wifi_color.setText("Value: " + y[0] + " > " + x);
                Log.d("color_loop", "i:" + y[0] + " > " + x);
                if (y[0] >= Integer.MAX_VALUE - 1) {
                    y[0] = 1;
                } else {
                    y[0] += Integer.MAX_VALUE / 10;
                }
            }

            public int mapHpToColor(int input, int max) {
                double maxColValue = 255;
                double redValue = (input > max / 2 ? 1 - 2 * (input - max / 2) / max : 1.0) * maxColValue;
                double greenValue = (input > max / 2 ? 1.0 : 2 * input / max) * maxColValue;
                return getIntFromColor((int) redValue, (int) greenValue, 0);
            }

            public int getIntFromColor(int Red, int Green, int Blue) {
                Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
                Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
                Blue = Blue & 0x000000FF; //Mask out anything not blue.
                return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
            }
        });
        BTN_connectOpenWifi.getBackground().setAlpha(128);
        BTN_connectOpenWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ArrayList<ScanResult> openap = BackgroundService.getStrongestOpenAp(
                            BackgroundService.wifiManager.getScanResults()
                    );
                    String message = "Possible: " + openap.size() + "<br><br>";
                    for (ScanResult sr : openap) {
                        message = message + "<b>" + sr.SSID + "</b> | <i>" + sr.BSSID + "</i> | " + sr.level + "<br>";
                    }
                    message += "<br><b>Selected: " + openap.get(openap.size() - 1).SSID + " -> " + openap.get(openap.size() - 1).level + "</b>";
                    if (openap.get(openap.size() - 1).BSSID.length() > 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Do you want to connect?");
                        builder.setMessage(Html.fromHtml(
                                message, Html.FROM_HTML_MODE_LEGACY));
                        builder.setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        BackgroundService.enableApToConnect(
                                                openap.get(openap.size() - 1),
                                                true);
                                        dialog.dismiss();
                                    }
                                });
                        builder.setNegativeButton("NO",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Nothing to connect! #" + BackgroundService.wifiManager.getScanResults().size(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        BTN_RerunQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        for (Runnable run : BackgroundService.webReqRunnablesList) {
                            BackgroundService.webRequestExecutor.submit(run);
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        BackgroundService.webReqRunnablesList.size() +
                                                " webreqs added! Queue: " + BackgroundService.webRequestExecutor.getQueue().size() +
                                                "\nExecuted: " + BackgroundService.executor_executed +
                                                " After: " + BackgroundService.executor_after +
                                                " Before: " + BackgroundService.executor_before,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                        super.run();
                    }
                };
                thread.start();

                for (BackgroundService.Live_Http_GET_SingleRecord http : BackgroundService.httpErrorList) {
                    //BackgroundService.Live_Http_GET_SingleRecord.executeRequest(http);
                }

                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.custom_alert_dialog);
                alertText1 = (TextView) dialog.findViewById(R.id.alerttxt1);
                alertText2 = (TextView) dialog.findViewById(R.id.alerttxt2);
                alertText3 = (TextView) dialog.findViewById(R.id.alerttxt3);
                Button btnYes = dialog.findViewById(R.id.yes);
                Button btnNo = dialog.findViewById(R.id.no);
                Button btnClose = dialog.findViewById(R.id.closebtn);
                dialog.setCancelable(false);
                alertText1.setText(String.valueOf(System.currentTimeMillis()));

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handler_yes = new Handler();
                        handler_yes.postDelayed(runnable_yes, 1000);
                    }
                });
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handler_no = new Handler();
                        handler_no.postDelayed(runnable_no, 1000);
                    }
                });
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            handler_no.removeCallbacks(runnable_no);
                            handler_yes.removeCallbacks(runnable_yes);
                            handler_no = null;
                            handler_yes = null;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });

        BTN_BL_customPair = findViewById(R.id.btn_bl_custompair);
        BTN_nearbySend = findViewById(R.id.btn_nearbysend);
        BTN_startNearby = findViewById(R.id.btn_startnearby);
        BTN_stopNearby = findViewById(R.id.btn_stopnearby);
        BTN_cellTowers = findViewById(R.id.btn_celltowers);

        BTN_BL_customPair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice sma510f =
                        bluetoothAdapter.getRemoteDevice("70:28:8B:0C:61:43");
                BluetoothDevice sma530f =
                        bluetoothAdapter.getRemoteDevice("8C:83:E1:41:A6:7B");
                BluetoothDevice remoteDevice;
                if (BackgroundService.android_id_source_device.equalsIgnoreCase("SMA510F")) {
                    remoteDevice = sma530f;
                } else {
                    remoteDevice = sma510f;
                }
                try {
                    Thread bondthread = new Thread() {
                        @Override
                        public void run() {
                            boolean succ = remoteDevice.createBond();
                            boolean succ2 = remoteDevice.setPin("0000".getBytes(StandardCharsets.UTF_8));
                        }
                    };
                    bondthread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        BTN_nearbySend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tosend = "tesztlol_" + System.currentTimeMillis();
                BackgroundService.nearby.sendKeyObjectPayload("string", tosend);
                try {
                    String sum = "";
                    Set<String> uniqueSet = new HashSet<String>(
                            /*Arrays.asList(*/
                            BackgroundService.bluetooth_types/*)*/);
                    TreeSet<String> orderedSet = new TreeSet(uniqueSet);
                    orderedSet = (TreeSet) orderedSet.descendingSet();
                    for (String s : orderedSet) {
                        sum += s + ": " + Collections.frequency(BackgroundService.bluetooth_types, s) + "\n";
                    }
                    BackgroundService.sendMessage_Telegram(sum);
                    Toast.makeText(getApplicationContext(), sum, Toast.LENGTH_LONG).show();
                    Log.d("tesztelek_", "asd: " + sum + " " + uniqueSet.size());
                } catch (Exception e) {
                    BackgroundService.sendMessage_Telegram(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        BTN_startNearby.setAlpha(0.7f);
        BTN_stopNearby.setAlpha(0.7f);
        BTN_startNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackgroundService.nearby.startAdvertising();
                BackgroundService.nearby.startDiscovering();
                Toast.makeText(getApplicationContext(), "Nearby Started", Toast.LENGTH_SHORT).show();
            }
        });
        BTN_stopNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackgroundService.nearby.stopAdvertising();
                BackgroundService.nearby.stopDiscovering();
                Toast.makeText(getApplicationContext(), "Nearby Stopped", Toast.LENGTH_SHORT).show();
            }
        });

        Handler notif_handler = new Handler();
        notif_handler.postDelayed(new Runnable() {
            SontHelper.Bounce bouncer = new SontHelper.Bounce(0f, 1f, 0.1f);

            public void run() {
                Window window = MainActivity.this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //Random rnd = new Random();
                //String a = String.valueOf(new ArgbEvaluator().evaluate(0.75f, 0x00ff00, 0xff0000));
                float x = bouncer.next();
                int y = ColorUtils.blendARGB(Color.RED, Color.BLUE, x);
                int y2 = ColorUtils.blendARGB(Color.GREEN, Color.MAGENTA, x);
                int y3 = ColorUtils.blendARGB(Color.YELLOW, Color.CYAN, x);
                window.setStatusBarColor(y);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    window.setNavigationBarDividerColor(y3);
                }
                window.setNavigationBarColor(y2);

                notif_handler.postDelayed(this, 100);
            }
        }, 1000);

        Handler wifi_strengthHandler = new Handler();
        wifi_strengthHandler.postDelayed(new Runnable() {
            public void run() {
                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    Location lastlocation = BackgroundService.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (lastlocation == null)
                        lastlocation = BackgroundService.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    long time1 = lastlocation.getTime();
                    String timeago = BackgroundService.getTimeAgo(BackgroundService.lastokscan);
                    String timeago2 = BackgroundService.getTimeAgo(time1);
                    String timeago3 = BackgroundService.getTimeAgo(BackgroundService.lastBL_scan);
                    if (timeago.equalsIgnoreCase("just now") || // need to modify
                            timeago2.equalsIgnoreCase("just now") ||
                            timeago3.equalsIgnoreCase("just now")) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss");
                        Date date1 = new Date(System.currentTimeMillis() - BackgroundService.lastokscan);
                        String val1 = simpleDateFormat.format(date1);
                        if (val1.startsWith("0"))
                            val1 = val1.substring(1);
                        Date date2 = new Date(System.currentTimeMillis() - time1);
                        String val2 = simpleDateFormat.format(date2);
                        if (val2.startsWith("0"))
                            val2 = val2.substring(1);

                        Date date3 = new Date(System.currentTimeMillis() -
                                BackgroundService.lastBL_scan);
                        String val3 = simpleDateFormat.format(date3);
                        if (val3.startsWith("0"))
                            val3 = val3.substring(1);

                        BTN_scans.setText("WiFi Scan: " + val1 + " seconds ago\n" +
                                "Location: " + val2 + " seconds ago\n" +
                                "BL Scan: " + val3 + " seconds ago");
                    } else {
                        BTN_scans.setText("WiFi Scan: " +
                                BackgroundService.getTimeAgo(BackgroundService.lastokscan) + "\n" +
                                "Location: " + BackgroundService.getTimeAgo(time1) + "\n" +
                                "BL Scan: " + BackgroundService.getTimeAgo(BackgroundService.lastBL_scan) + " seconds ago");
                    }
                } catch (Exception e) {
                    BTN_scans.setText(e.getMessage());
                    //e.printStackTrace();
                }
                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (mWifi.isConnected()) {
                    WifiInfo wifiInfo = BackgroundService.wifiManager.getConnectionInfo();
                    double level = (100d - (wifiInfo.getRssi() * -1d)) / 100d;
                    double percentLevel = (100d - (wifiInfo.getRssi() * -1d)) / 100d;
                    int wifiColor = getGreenToRedAndroid(level);
                    if (level > 100d)
                        level = 100d;
                    BTN_wifi_color.setBackgroundColor(wifiColor);
                    BTN_wifi_color.setText("[" + wifiInfo.getRssi() + "] > Color: "
                            + wifiColor + " Time: " + System.currentTimeMillis());

                    getcelldata();
                } else {

                    getcelldata();

                    BTN_wifi_color.setText("WiFi not connected");
                    BTN_wifi_color.setBackgroundColor(Color.GRAY);
                }
                wifi_strengthHandler.postDelayed(this, 250);
            }

            String convertIntToHex(int color) {
                return String.format("#%06X", (0xFFFFFF & color));
            }

            int getGreenToRedAndroid(double value) {
                return android.graphics.Color.HSVToColor(new float[]{(float) value * 120f, 1f, 1f});
            }
        }, 1000);

        try {
            BackgroundService.analyticsTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("MainActivity Started")
                    .build());
        } catch (Exception e) {
        }

    }

    void showNotificationOld(String title, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID_SERVICE = getPackageName();
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_SERVICE, "App Service", NotificationManager.IMPORTANCE_DEFAULT));
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getPackageName());
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.servicetransparenticon)
                    .setGroup("wifi")
                    .setSubText("subtext")
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            //startForeground(2, notification);
            //startForeground(2, notification);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(2, notification);
        } else {

            Intent notificationIntent = new Intent(getApplicationContext(), notificationReceiver.class);
            notificationIntent.putExtra("29294", "29294");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(),
                    29294,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            //region NotificationBUTTONS
            Intent intent = new Intent(getApplicationContext(), notificationReceiver.class);
            intent.setAction("exit");
            intent.putExtra("requestCode", 99);
            PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 99, intent, PendingIntent.FLAG_IMMUTABLE);

            Intent intent_location_network = new Intent(getApplicationContext(), notificationReceiver.class);
            intent_location_network.setAction("network");
            intent_location_network.putExtra("requestCode", 101);
            PendingIntent pi_location_network = PendingIntent.getBroadcast(getApplicationContext(),
                    101, intent_location_network, PendingIntent.FLAG_IMMUTABLE);

            Intent intent_location_gps = new Intent(getApplicationContext(), notificationReceiver.class);
            intent_location_gps.setAction("gps");
            intent_location_gps.putExtra("requestCode", 102);
            PendingIntent pi_location_gps = PendingIntent.getBroadcast(getApplicationContext(),
                    102, intent_location_gps, PendingIntent.FLAG_IMMUTABLE);
            //endregion

            int color2 = Color.argb(255, 220, 237, 193);
            Notification notification = new NotificationCompat.Builder(getApplicationContext(), "sontylegacy")
                    .setColorized(true)
                    .setColor(color2)
                    .setSubText("subtext MainActivity")
                    .setContentTitle(title)
                    .setContentText(text)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            //.addLine(text)
                            //.addLine(text)
                            .bigText(text)
                            .setSummaryText("MainActivity")
                            .setBigContentTitle(text))
                    .setContentInfo("CONTENT INFO")
                    .setContentInfo("CONTENT INFO")
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                    .setGroup("wifi")
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setSmallIcon(R.drawable.servicetransparenticon)
                    .setContentIntent(pendingIntent)
                    .setChannelId("sonty")
                    .addAction(R.drawable.servicetransparenticon, "NET", pi_location_network)
                    .addAction(R.drawable.servicetransparenticon, "GPS", pi_location_gps)
                    .addAction(R.drawable.servicetransparenticon, "EXIT", pi)
                    .build();
            //startForeground(58, notification);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(58, notification);
        }
    }

    void showNotificationNew(String title, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID_SERVICE = getPackageName();
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_SERVICE, "App Service", NotificationManager.IMPORTANCE_DEFAULT));
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getPackageName());
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.servicetransparenticon)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            //startForeground(2, notification);
            //startForeground(2, notification);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(2, notification);
        } else {

            Intent notificationIntent = new Intent(getApplicationContext(), notificationReceiver.class);
            notificationIntent.putExtra("29294", "29294");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(),
                    29294,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            //region NotificationBUTTONS
            Intent intent = new Intent(getApplicationContext(), notificationReceiver.class);
            intent.setAction("exit");
            intent.putExtra("requestCode", 99);
            PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 99, intent, PendingIntent.FLAG_IMMUTABLE);

            Intent intent_location_network = new Intent(getApplicationContext(), notificationReceiver.class);
            intent_location_network.setAction("network");
            intent_location_network.putExtra("requestCode", 101);
            PendingIntent pi_location_network = PendingIntent.getBroadcast(getApplicationContext(),
                    101, intent_location_network, PendingIntent.FLAG_IMMUTABLE);

            Intent intent_location_gps = new Intent(getApplicationContext(), notificationReceiver.class);
            intent_location_gps.setAction("gps");
            intent_location_gps.putExtra("requestCode", 102);
            PendingIntent pi_location_gps = PendingIntent.getBroadcast(getApplicationContext(),
                    102, intent_location_gps, PendingIntent.FLAG_IMMUTABLE);
            //endregion

            int color2 = Color.argb(255, 220, 237, 193);
            Notification notification = new NotificationCompat.Builder(getApplicationContext(), "sontylegacy")
                    .setColorized(true)
                    .setColor(color2)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            //.addLine(text)
                            //.addLine(text)
                            .bigText(text)
                            .setSummaryText("summary")
                            .setBigContentTitle(text))
                    .setContentInfo("CONTENT INFO")
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                    .setGroup("wifi")
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setSmallIcon(R.drawable.servicetransparenticon)
                    .setContentIntent(pendingIntent)
                    .setChannelId("sonty")
                    .addAction(R.drawable.servicetransparenticon, "NET", pi_location_network)
                    .addAction(R.drawable.servicetransparenticon, "GPS", pi_location_gps)
                    .addAction(R.drawable.servicetransparenticon, "EXIT", pi)
                    .build();
            //startForeground(58, notification);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(58, notification);
        }
    }

    private void getcelldata() {
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        List<CellInfo> infos = tel.getAllCellInfo();
        double average = 0d;
        double sum = 0d;
        String summed_tower = "";
        String summed_for_notif = "";
        for (int i = 0; i < infos.size(); ++i) {
            CellInfo info = infos.get(i);
            if (info instanceof CellInfoGsm) {
                CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                sum += gsm.getDbm();
                summed_tower = summed_tower + "gsm" + " ";
            } else if (info instanceof CellInfoLte) {
                CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                sum += lte.getDbm();
                summed_tower = summed_tower + "lte" + " ";
            } else if (info instanceof CellInfoCdma) {
                CellSignalStrengthCdma cdma = ((CellInfoCdma) info).getCellSignalStrength();
                sum += cdma.getDbm();
                summed_tower = summed_tower + "cdma" + " ";
            } else if (info instanceof CellInfoWcdma) {
                CellSignalStrengthWcdma wcdma = ((CellInfoWcdma) info).getCellSignalStrength();
                sum += wcdma.getDbm();
                summed_tower = summed_tower + "wcdma" + " ";
            } else {
                Log.d("celldata_", "Something else!");
                summed_tower = summed_tower + "other" + " ";
            }
        }

        String[] b = summed_tower.split(" ");
        HashMap<String, Integer> freqMap = new HashMap<String, Integer>();
        Set<String> mySet = new HashSet<String>(Arrays.asList(b));
        for (String s : mySet) {
            summed_for_notif += s + ": " + Collections.frequency(Arrays.asList(b), s) + " ";
        }

        average = (double) sum / (double) infos.size();

        double level = (100d - (average * -1d)) / 100d;

        int bc = getGreenToRedAndroid(level);

        BTN_cellTowers.setBackgroundColor(bc);
        BTN_cellTowers.setText("Cell Towers: " + infos.size() + " / Average: [" + average + "] " +
                summed_for_notif);
    }

    String convertIntToHex(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    int getGreenToRedAndroid(double value) {
        return android.graphics.Color.HSVToColor(new float[]{(float) value * 120f, 1f, 1f});
    }

    private void forceExitApp() {
        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.cancelAll();
        try {
            BackgroundService.locationManager.removeUpdates(BackgroundService.locationListener);
            locationManager.removeUpdates(BackgroundService.locationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        stopService(new Intent(getApplicationContext(), BackgroundService.class));

        System.exit(2);
        System.exit(1);
        System.exit(0);
        finish();
    }

    public String convertConstellation(int i) {
        switch (i) {
            case 1:
                return "GPS";
            case 2:
                return "SBAS";
            case 3:
                return "GLONASS";
            case 4:
                return "QZSS";
            case 5:
                return "BEIDOU";
            case 6:
                return "GALILEO";
            case 7:
                return "IRNSS";
            case 8:
                return "COUNT";
            case 0:
                return "0";
            default:
                return "Unknown";
        }
    }

    @Override
    public void onResume() {
        Intent i = new Intent(MainActivity.this, BackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, i);
        } else {
            startService(i);
        }
        bindService(i, mServerConn, Context.BIND_AUTO_CREATE);

        try {
            BackgroundService.analyticsTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("MainActivity onResume")
                    .build());
        } catch (Exception e) {
            //e.printStackTrace();
        }

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

    String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }
}


