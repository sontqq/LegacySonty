package com.sontme.legacysonty;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.NotificationManager;
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
import android.location.GpsStatus;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity implements GpsStatus.Listener {
    TextView txt;
    TextView statsTextview;
    SeekBar seekBar;
    TextView seekval;
    public TextView alertText;
    static Handler statsHandler;
    static Runnable statsRunnable;

    Handler handler_no;
    Handler handler_yes;
    Runnable runnable_no;
    Runnable runnable_yes;

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

        Runnable runnable_no = new Runnable() {
            @Override
            public void run() {
                alertText.setText(Html.fromHtml(
                        BackgroundService.locationToStringAddress(getApplicationContext(), BackgroundService.CURRENT_LOCATION),
                        Html.FROM_HTML_MODE_LEGACY));

                handler_no.postDelayed(this, 1000);
            }
        };

        Runnable runnable_yes = new Runnable() {
            @Override
            public void run() {
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
                alertText.setText(Html.fromHtml(msg, Html.FROM_HTML_MODE_LEGACY));

                handler_yes.postDelayed(this, 1000);
            }
        };

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
            try {
                bindService(i, mServerConn, Context.BIND_AUTO_CREATE);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                                "Error: " + BackgroundService.Live_Http_GET_SingleRecord.cnt_httpError + " " +
                                "New: " + BackgroundService.cnt_new + " " +
                                "Time: " + BackgroundService.cnt_updated_time + " " +
                                "BLName: " + BackgroundService.cnt_nameUpdated + " " +
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

        Button exitbtn = findViewById(R.id.exitbutton);
        Button quebtn = findViewById(R.id.quebutton);
        Button wifibtn = findViewById(R.id.openwifibutton);
        Button blfileburstbtn = findViewById(R.id.blfileburst);

        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationManager nm = getSystemService(NotificationManager.class);
                nm.cancelAll();
                stopService(new Intent(getApplicationContext(), BackgroundService.class));
                System.exit(2);
                System.exit(0);
                System.exit(1);
                finish();
            }
        });

        blfileburstbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread mostfreq_ssid = new Thread() {
                    @Override
                    public void run() {
                        String url = "https://sont.sytes.net/wifi/allssid.php";
                        AndroidNetworking.get(url)
                                .setPriority(Priority.IMMEDIATE)
                                .build()
                                .getAsString(new StringRequestListener() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String tempres = response;
                                            tempres = tempres.trim().replaceAll(" +", " ").replaceAll("\n+", "\n");
                                            int lines = tempres.split("\r\n|\r|\n").length;
                                            String[] splited = tempres.split("\n");
                                            Arrays.sort(splited);
                                            int max = 0;
                                            int count = 1;
                                            String word = splited[0];
                                            String curr = splited[0];
                                            Log.d("most_freq_", "count: " + splited.length + " count2: " + lines);

                                            for (int i = 1; i < splited.length; i++) {
                                                if (splited[i].equals(curr)) {
                                                    count++;
                                                } else {
                                                    count = 1;
                                                    curr = splited[i];
                                                }
                                                if (max < count) {
                                                    max = count;
                                                    word = splited[i];
                                                }
                                            }
                                            Log.d("most_freq_", max + " x " + word);


                                        } catch (Exception e) {
                                            Log.d("most_freq_", "Error! " + e.getMessage());
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError error) {
                                        //error.printStackTrace();
                                    }
                                });
                    }
                };
                //mostfreq_ssid.start();

                Thread tempt = new Thread() {
                    @Override
                    public void run() {
                        File path = getExternalFilesDir(null);
                        File file = new File(path, "BLsession.txt");
                        try {
                            Scanner sc = new Scanner(file, "UTF-8");
                            ArrayList<String> lines_arr = new ArrayList<String>();
                            Collections.shuffle(lines_arr);
                            Log.d("BL_FILE_", "Started!");
                            int namenull = 0;
                            int namenotnull = 0;
                            while (sc.hasNextLine()) {
                                String l = sc.nextLine();
                                if (!l.contains("|  |  |")) {
                                    lines_arr.add(sc.nextLine());
                                }
                            }

                            sc.close();
                            for (int i = lines_arr.size() - 1; i >= 0; i--) {
                                double percentage = (double) 100 - ((double) i / (double) lines_arr.size()) * (double) 100;
                                //Log.d("BL_FILE_","Loop " + i+"/"+lines_arr.size() + " -> " + percentage+"%");
                                String name = BackgroundService.getStringbetweenStrings(lines_arr.get(i), "_name_", "_endname_");
                                String address = BackgroundService.getStringbetweenStrings(lines_arr.get(i), "_address_", "_endaddress_");
                                String latitude = BackgroundService.getStringbetweenStrings(lines_arr.get(i), "_lat_", "_endlat_");
                                String longitude = BackgroundService.getStringbetweenStrings(lines_arr.get(i), "_lng_", "_endlng_");
                                if (name.contains("null")) {
                                    namenull++;
                                    //Log.d("BL_FILE_","null: "+namenull);
                                    continue;
                                } else {
                                    namenotnull++;
                                    //Log.d("BL_FILE_","not: " + namenotnull);
                                }

                                if (!lines_arr.get(i).contains("|  |  |")) {
                                    String utf_letter = BackgroundService.locationToStringAddress(getApplicationContext(), BackgroundService.CURRENT_LOCATION)
                                            .replaceAll("ő", "ö");
                                    utf_letter = utf_letter.replaceAll("ű", "ü");
                                    utf_letter = utf_letter.replaceAll("Ő", "Ö");
                                    utf_letter = utf_letter.replaceAll("Ű", "Ü");
                                    if (!utf_letter.contains("Egri") && !utf_letter.contains("25-") && !utf_letter.contains("Unknown")) {
                                        //Log.d("BL_FILE_", "siker: " + utf_letter);
                                        final String reqBody =
                                                "?id=0&name=" + name +
                                                        "&address=" + utf_letter +
                                                        "&macaddress=" + address +
                                                        "&islowenergy=" + "unknown" +
                                                        "&source=" + "legacy_sonty" +
                                                        "&long=" + longitude +
                                                        "&lat=" + latitude +
                                                        "&progress=" + lines_arr.size() + "_" + i + "_" + percentage;
                                        String finalUtf_letter = utf_letter;
                                        Runnable webReqRunnable_bl_init = new Runnable() {
                                            @Override
                                            public void run() {
                                                BackgroundService.RequestTaskListener requestTaskListener_bl = new BackgroundService.RequestTaskListener() {
                                                    @Override
                                                    public void update(String string) {
                                                        if (string != null) {
                                                            if (string.contains("new_device")) {
                                                                Log.d("BL_TEST_", "NEW DEVICE FOUND: " + name + " -> " + address);
                                                                BackgroundService.cnt_new++;
                                                                BackgroundService.cnt_new_bl++;
                                                            } else if (string.contains("Not updated")) {
                                                                Log.d("BL_TEST_", "Not updated");
                                                                BackgroundService.cnt_notrecorded++;
                                                                BackgroundService.cnt_notrecorded_bl++;
                                                            } else if (string.contains("not_recorded")) {
                                                                BackgroundService.cnt_notrecorded++;
                                                                BackgroundService.cnt_notrecorded_bl++;
                                                            } else if (string.contains("regi_old")) {
                                                                BackgroundService.cnt_updated_time++;
                                                                BackgroundService.cnt_updated_time_bl++;
                                                            }
                                                            BackgroundService.updateCurrent_secondary(getApplicationContext(),
                                                                    "Bluetooth ANSWER",
                                                                    name + "\n" + address + "\n" + finalUtf_letter, R.drawable.error_icon);
                                                        }
                                                    }
                                                };
                                                if (!name.equals("null") && name != null) {
                                                    BackgroundService.RequestTask_Bluetooth requestTask_bl = new BackgroundService.RequestTask_Bluetooth();
                                                    requestTask_bl.addListener(requestTaskListener_bl);
                                                    requestTask_bl.execute(reqBody);
                                                }
                                            }
                                        };
                                        BackgroundService.webRequestExecutor.submit(webReqRunnable_bl_init);
                                        BackgroundService.webReqRunnablesList.add(webReqRunnable_bl_init);
                                    }
                                }
                            }
                            Log.d("BL_FILE_", "Loop ended");
                        } catch (Exception e) {
                            Log.d("BL_FILE_", "Error: " + e.getMessage());
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "Exception happened: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                            e.printStackTrace();
                        }
                    }
                };
                //tempt.setPriority(Thread.MIN_PRIORITY);
                tempt.start();
            }
        });

        wifibtn.setOnClickListener(new View.OnClickListener() {
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
        quebtn.setOnClickListener(new View.OnClickListener() {
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

                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.custom_alert_dialog);
                alertText = (TextView) dialog.findViewById(R.id.alerttxt);
                Button btnYes = dialog.findViewById(R.id.yes);
                Button btnNo = dialog.findViewById(R.id.no);
                Button btnClose = dialog.findViewById(R.id.closebtn);
                dialog.setCancelable(false);
                alertText.setText("asd");

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