package com.sontme.legacysonty;

import static com.sontme.legacysonty.SontHelperSonty.invertColor;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.companion.AssociationRequest;
import android.companion.BluetoothLeDeviceFilter;
import android.companion.CompanionDeviceManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellInfoTdscdma;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthNr;
import android.telephony.CellSignalStrengthTdscdma;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;


public class MainActivity extends AppCompatActivity {
    TextView txt;
    TextView statsTextview;
    SeekBar seekBar;
    TextView seekval;
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

        if (!hasPermissions(getApplicationContext(), PERMISSIONS_ALL)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_ALL, 1);
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

        Runnable runnable_no = new Runnable() {
            @Override
            public void run() {
                /*alertText.setText(Html.fromHtml(
                        BackgroundService.locationToStringAddress(getApplicationContext(), BackgroundService.CURRENT_LOCATION),
                        Html.FROM_HTML_MODE_LEGACY));*/
                alertText1.setText(BackgroundService.Live_Http_GET_SingleRecord.lastHandledURL);
                alertText2.setText("Errors: " + BackgroundService.Live_Http_GET_SingleRecord.cnt_httpError);
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
                alertText1.setText(Html.fromHtml(msg, Html.FROM_HTML_MODE_LEGACY));

                handler_yes.postDelayed(this, 1000);
            }
        };

        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            managePermissions();
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            locationManager.addGpsStatusListener(new GpsStatus.Listener() {
                @Override
                public void onGpsStatusChanged(int event) {
                    TextView txt4 = findViewById(R.id.txt4);
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
                                    try {
                                        lastUpdate = BackgroundService.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getTime();
                                        lastUpdate = BackgroundService.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getTime();
                                    } catch (Exception e) {
                                        lastUpdate = BackgroundService.CURRENT_LOCATION.getTime();
                                    }

                                    if ((SystemClock.elapsedRealtime() - BackgroundService.CURRENT_LOCATION_LASTTIME)
                                            < (10000)) {
                                        txt4.setText("Provider: " + BackgroundService.CURRENT_LOCATION.getProvider() + "\n" +
                                                "STILL HAS GPS FIX [" + BackgroundService.LOCATON_CHANGE_COUNTER + "] " + lastUpdate);
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
            });
        } else {
            TextView txt4 = findViewById(R.id.txt4);
            locationManager.registerGnssStatusCallback(new GnssStatus.Callback() {
                @Override
                public void onStarted() {
                    super.onStarted();
                    txt4.setText("GPS/GNSS Started " + System.currentTimeMillis());
                }

                @Override
                public void onStopped() {
                    super.onStopped();
                    //txt4.setText("GPS/GNSS Stopped " + System.currentTimeMillis());
                }

                @Override
                public void onFirstFix(int ttffMillis) {
                    super.onFirstFix(ttffMillis);
                    txt4.setText("First FIX: " + ttffMillis);
                }

                @Override
                public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                    super.onSatelliteStatusChanged(status);
                    try {
                        String summed_constellation = "";
                        String summed_for_notif = "";
                        for (int i = 0; i < status.getSatelliteCount(); i++) {
                            String type = convertConstellation(status.getConstellationType(i));
                            summed_constellation = summed_constellation + type + " ";
                        }
                        String[] b = summed_constellation.split(" ");

                        HashMap<String, Integer> freqMap = new HashMap<String, Integer>();
                        Set<String> mySet = new HashSet<String>(Arrays.asList(b));
                        for (String s : mySet) {
                            summed_for_notif += s + ": " + Collections.frequency(Arrays.asList(b), s);
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
        Intent i = new Intent(MainActivity.this, BackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, i);
        } else {
            startService(i);
        }
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


        boolean rooted = BackgroundService.RootUtil.isDeviceRooted();
        boolean emulator = BackgroundService.AdminTOOLS.checkIfDeviceIsEmulator(getApplicationContext());

        Button exitbtn = findViewById(R.id.exitbutton);
        Button quebtn = findViewById(R.id.quebutton);
        Button wifibtn = findViewById(R.id.openwifibutton);
        Button blfileburstbtn = findViewById(R.id.blfileburst);
        Button testbtn = findViewById(R.id.testbtn);
        Button testbtn2 = findViewById(R.id.testbtn2);
        Button testbtn3 = findViewById(R.id.testbtn3);
        testbtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> intersect = SontHelper.intersectionOfLists(BackgroundService.requestUniqueIDList, BackgroundService.requestUniqueIDList_error);
                Log.d("intersect_", "uni size: " + BackgroundService.requestUniqueIDList.size());
                Log.d("intersect_", "uni_error size: " + BackgroundService.requestUniqueIDList_error.size());

                for (String s : intersect) {
                    Log.d("intersect_", "this -> " + s);
                }
                if (intersect.size() >= 1) {
                    Log.d("intersect_", "count: " + intersect.size());
                } else {
                    Log.d("intersect_", "empty intersect");
                }
            }
        });
        double[] val = {0.1};
        testbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = getGreenToRedAndroid(val[0]);
                testbtn2.setText("Val: " + val[0] + " Color: " + color);
                testbtn2.setBackgroundColor(color);
                if (val[0] <= 1 && val[0] >= 0) {
                    val[0] += 0.1;
                } else {
                    val[0] -= 0.1;
                }
            }

            int getGreenToRedGradientByValue(int currentValue, int max) {
                int r = ((255 * currentValue) / max);
                int g = (255 * (max - currentValue)) / max;
                int b = 0;
                return ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (b & 0x0ff);
            }

            int getGreenToRedAndroid(double value) {
                return android.graphics.Color.HSVToColor(new float[]{(float) value * 120f, 1f, 1f});
            }
        });

        final int[] y = {1};
        testbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int max = 255;
                int x = mapHpToColor(y[0], max);
                testbtn.setBackgroundColor(y[0]);
                testbtn.setText("Value: " + y[0] + " > " + x);
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

        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forceExitApp();
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
                                                        "&longtime=" + System.currentTimeMillis() +
                                                        "&address=" + utf_letter +
                                                        "&macaddress=" + address +
                                                        "&islowenergy=" + "unknown" +
                                                        "&source=" + "legacy_sonty_looper" +
                                                        "&long=" + longitude +
                                                        "&lat=" + latitude +
                                                        "&progress=" + lines_arr.size() + "_" + i + "_" + percentage;
                                        String finalUtf_letter = utf_letter;
                                        Runnable webReqRunnable_bl_init = new Runnable() {
                                            @Override
                                            public void run() {
                                                BackgroundService.RequestTaskListener requestTaskListener_bl_filelooper = new BackgroundService.RequestTaskListener() {
                                                    @Override
                                                    public void update(String string, String URL) {
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
                                                                    name + "\n" + address + "\n" + finalUtf_letter, R.drawable.gps);
                                                        }
                                                    }
                                                };
                                                if (!name.equals("null") && name != null) {
                                                    BackgroundService.RequestTask_Bluetooth requestTask_bl = new BackgroundService.RequestTask_Bluetooth();
                                                    requestTask_bl.addListener(requestTaskListener_bl_filelooper);
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
                //tempt.start();

                /*PolynomialSplineFunction function = new LinearInterpolator().interpolate(
                        new double[]{100, 150, 200},
                        new double[]{1000, 1500, 2000}
                );
                PolynomialFunction[] splines = function.getPolynomials();
                PolynomialFunction first = splines[0];
                PolynomialFunction last = splines[splines.length - 1];
                */
                //BackgroundService.sendMessage_Telegram("FIRST _ " + first.toString());
                //BackgroundService.sendMessage_Telegram("last _ " + last.toString());
                //Log.d("extrapol_","First -> " + first.toString());
                //Log.d("extrapol_","Last -> " + last.toString());
                /*double[] x = BackgroundService.interpolate(2, 4, 5);
                int i = 0;
                for (double y : x) {
                    //Log.d("extrapol_",i + " > interpolation: " + y);
                    i++;
                }*/
                /*
                double[][] d = {
                        { STARTTIME, STARTPERCENT },
                        { NOWTIME, NOWPERCENT }
                };
                double xx = 1; // PERCENTAGE -> eredmeny: WHEN
                */

                /*double[][] d = {
                        { 4, 28 },
                        { 2, 22 }
                };
                double xx = 1;
                double e = BackgroundService.extrapolate(d,xx);
                Log.d("extrapol_","eredmeny: " + e);*/
                /*double[][] series = {
                        {BackgroundService.startedLongTime, BackgroundService.startedLongPercent},
                        {System.currentTimeMillis(), SontHelperSonty.getBatteryLevel(getApplicationContext())}
                };
                double[][] series2 = {
                        {BackgroundService.startedLongPercent, BackgroundService.startedLongTime},
                        {SontHelperSonty.getBatteryLevel(getApplicationContext()) - 1, System.currentTimeMillis()}
                };
                double target = 1;
                double e = BackgroundService.extrapolate(series, target);
                double e2 = BackgroundService.extrapolate(series2, System.currentTimeMillis() + (3600 * 5));
                Toast.makeText(getApplicationContext(), "eredmeny: " + e, Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "eredmeny2: " + e2, Toast.LENGTH_LONG).show();
                BackgroundService.sendMessage_Telegram("eredmeny: " + e);
                BackgroundService.sendMessage_Telegram("eredmeny2: " + e2);
                Log.d("extrapol_", "eredmeny: " + e);
                Log.d("extrapol_", "eredmeny2: " + e2);


                String s = SontHelperSonty.getTimeAgo_Battery((long) e);
                String s2 = SontHelperSonty.getTimeAgo_Battery((long) e2);
                Log.d("battery_test", "timeago: " + s);
                Log.d("battery_test", "timeago: " + s2);
                BackgroundService.sendMessage_Telegram("timeago: " + e);
                BackgroundService.sendMessage_Telegram("timeago: " + e2);
                */

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
                    if (timeago.equalsIgnoreCase("just now") || // need to modify
                            timeago2.equalsIgnoreCase("just now")) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss");
                        Date date1 = new Date(System.currentTimeMillis() - BackgroundService.lastokscan);
                        String val1 = simpleDateFormat.format(date1);
                        if (val1.startsWith("0"))
                            val1 = val1.substring(1);
                        Date date2 = new Date(System.currentTimeMillis() - time1);
                        String val2 = simpleDateFormat.format(date2);
                        if (val2.startsWith("0"))
                            val2 = val2.substring(1);
                        testbtn3.setText("Last OK WiFi Scan: " +
                                val1 + " seconds ago\n" + "Last Location: " + val2 + " seconds ago");
                    } else {
                        testbtn3.setText("Last OK WiFi Scan: " +
                                BackgroundService.getTimeAgo(BackgroundService.lastokscan) + "\n" +
                                "Last Location: " + BackgroundService.getTimeAgo(time1));
                    }
                } catch (Exception e) {
                    testbtn3.setText(e.getMessage());
                    e.printStackTrace();
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
                    testbtn.setBackgroundColor(wifiColor);
                    testbtn.setText("[" + wifiInfo.getRssi() + "] > Color: "
                            + wifiColor + " Time: " + System.currentTimeMillis());

                    getcelldata();
                } else {

                    getcelldata();

                    testbtn.setText("WiFi not connected");
                    testbtn.setBackgroundColor(Color.GRAY);
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

        try {
            CompanionDeviceManager cdm = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                cdm = (CompanionDeviceManager) getSystemService(Context.COMPANION_DEVICE_SERVICE);
                BluetoothLeDeviceFilter deviceFilter = new BluetoothLeDeviceFilter.Builder().build();
                AssociationRequest pairingRequest = new AssociationRequest.Builder()
                        .addDeviceFilter(deviceFilter)
                        .build();
                List<String> associations = cdm.getAssociations();
                for (String a : associations) {
                    Log.d("cdm_", "assoc: " + a);
                }
                cdm.associate(pairingRequest, new CompanionDeviceManager.Callback() {
                    @Override
                    public void onDeviceFound(IntentSender chooserLauncher) {
                        Log.d("cdm_", "found: " + chooserLauncher.toString());

                    }

                    @Override
                    public void onFailure(CharSequence error) {
                        Log.d("cdm_", "error: " + error.toString());
                    }
                }, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            summed_for_notif += s + ": " + Collections.frequency(Arrays.asList(b), s);
        }

        average = (double) sum / (double) infos.size();
        Button testbtn2 = findViewById(R.id.testbtn2);

        double level = (100d - (average * -1d)) / 100d;

        int bc = getGreenToRedAndroid(level);
        testbtn2.setBackgroundColor(bc);

        testbtn2.setText("Cell Towers: " + infos.size() + " / Average: [" + average + "]\n" +
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

}


