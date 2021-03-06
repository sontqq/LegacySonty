package com.sontme.legacysonty;

import static com.sontme.legacysonty.SontHelperSonty.FileIOTools.getSharedPref;
import static com.sontme.legacysonty.SontHelperSonty.FileIOTools.saveSharedPref;
import static com.sontme.legacysonty.SontHelperSonty.getCurrentTimeHumanReadable;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.common.io.Files;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.util.InetAddressUtils;
//import jcifs.netbios.NbtAddress;

public class BackgroundService extends AccessibilityService {
    //region INITREGION
    TimeElapsedUtil te = new TimeElapsedUtil();
    public static NearbyHandler nearby;
    public static String android_id;
    public static String android_id_source_device;
    public static long lastokscan = 0;
    public static long lastBL_scan = 0;
    public static boolean tosend_startInfo = false;
    public static WifiManager wifiManager;
    public static ArrayList<Runnable> webReqRunnablesList;
    public static String infoText = "";
    public static ThreadPoolExecutor webRequestExecutor;
    public static Location CURRENT_LOCATION;
    public static int LOCATON_CHANGE_COUNTER;
    public static long CURRENT_LOCATION_LASTTIME;
    public static int allCount;
    public static TimeElapsedUtil startedAtTime;
    public static long startedLongTime = 0;
    public static long startedLongPercent = 0;
    public static HashMap<String, Integer> uni_wifi;
    public static HashMap<String, Integer> uni_blue;

    static Context context;

    public static ArrayList<BluetoothDeviceWithLocation> bluetoothDevicesFound;
    public static boolean connectOpen = false;
    public static boolean toRegisterAP;
    public static int cnt_notrecorded;
    public static int cnt_notrecorded_bl;
    public static int cnt_new;
    public static int cnt_new_bl;
    public static int cnt_updated_time;
    public static int cnt_updated_time_bl;
    public static int cnt_updated_str;
    public static int cnt_nameUpdated;
    public static LocationListener locationListener;
    public static LocationManager locationManager;

    ////String PROVIDER = LocationManager.NETWORK_PROVIDER;
    String PROVIDER = LocationManager.GPS_PROVIDER;
    public static int TIME = 0;
    public static int DISTANCE = 0;

    public static int executor_before = 0;
    public static int executor_after = 0;
    public static int executor_executed = 0;

    public static GoogleAnalytics googleAnalytics;
    public static Tracker analyticsTracker;

    public static boolean FRESH_INSTALL = true;
    public static String lastRun = "FRESHINSTALL";
    public static long lastRunLong;
    public static long firstinstalltime;

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static SontHelperSonty.RandomString randomString;
    public static ArrayList<String> requestUniqueIDList;
    public static ArrayList<String> requestUniqueIDList_error;

    static int longestCommonSubstring(char X[], char Y[], int m, int n) {
        int LCStuff[][] = new int[m + 1][n + 1];
        int result = 0;

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0 || j == 0)
                    LCStuff[i][j] = 0;
                else if (X[i - 1] == Y[j - 1]) {
                    LCStuff[i][j]
                            = LCStuff[i - 1][j - 1] + 1;
                    result = Integer.max(result,
                            LCStuff[i][j]);
                } else
                    LCStuff[i][j] = 0;
            }
        }
        return result;
    }

    public static void addInfo(String title, String desc) {
        BackgroundService.infoText = BackgroundService.infoText
                + "\n<br>" + "<b>" + title + "</b>" + " " + desc;
    }

    public boolean decideIfNew_wifi(List<ScanResult> wifi_scanresult) {
        boolean result = true;
        for (ScanResult sr : wifi_scanresult) {
            if (uni_wifi.containsKey(sr.SSID + "_" + sr.BSSID)) {
                if (uni_wifi.get(sr.SSID + "_" + sr.BSSID) < sr.level) {
                    uni_wifi.put(sr.SSID + "_" + sr.BSSID, sr.level);
                    continue;
                } else {
                    return false;
                }
            } else {
                uni_wifi.put(sr.SSID + "_" + sr.BSSID, sr.level);
                continue;
            }
        }
        return result;
    }

    public static boolean decideIfNew_blue(BluetoothDevice device) {
        if (uni_blue.containsKey(device.getName() + "_" + device.getAddress())) {
            return false;
        } else {
            uni_blue.put(device.getName() + "_" + device.getAddress(), 0);
            //vibrate(getApplicationContext());
            Log.d("UNI_B_" + uni_blue.size(),
                    "new device: " + device.getName() + " -> " + device.getAddress() + " | " +
                            device.getType() + " | " + device.getBondState() + " | " +
                            device.getBluetoothClass().getClass() + " | " + device.getBluetoothClass().getDeviceClass());
            return true;
        }
    }

    public void triangulateScanResults(ScanResult sr) {
        if ((sr.SSID.equals("UPCAEDB2C3") || sr.SSID.equals("5GHZUPCAEDB2C3")) && CURRENT_LOCATION != null) {
            ApWithLocation apWithLocation;
            try {
                apWithLocation = aplist.get(sr.BSSID);
                if (apWithLocation.getRssi() < sr.level) {
                    apWithLocation = new ApWithLocation(sr.SSID, sr.frequency, sr.BSSID, sr.level, CURRENT_LOCATION);
                    aplist.put(sr.BSSID, apWithLocation);
                }
            } catch (Exception e) {
                apWithLocation = new ApWithLocation(sr.SSID, sr.frequency, sr.BSSID, sr.level, CURRENT_LOCATION);
                aplist.put(sr.BSSID, apWithLocation);
            }
        }
    }

    public static double calculateDistance(double signalLevelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    // wifiscan__
    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean newdata = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            List<ScanResult> scanresult = wifiManager.getScanResults();
            if (newdata) {
                for (ScanResult sr : scanresult) {
                    triangulateScanResults(sr);
                }
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!wifiManager.isScanThrottleEnabled()) {
                        if (android_id_source_device.equals("ANYA") == false) {
                            if ((System.currentTimeMillis() - lastokscan) > 2000L) {
                                boolean succ = wifiManager.startScan();
                                Log.d("wifiscan_time", "ran: " + (System.currentTimeMillis() - lastokscan));
                                if (succ == true) {
                                    lastokscan = System.currentTimeMillis();
                                }
                            }
                        }
                    }
                } else {
                    if (android_id_source_device.equals("ANYA") == false) {
                        if ((System.currentTimeMillis() - lastokscan) > 2000L) {
                            long a1 = System.currentTimeMillis();
                            boolean succ = wifiManager.startScan();
                            long a2 = System.currentTimeMillis();
                            Log.d("wifiscan_time", "ran_2: "
                                    + (System.currentTimeMillis() - lastokscan) + " " + succ + " " +
                                    (a2 - a1));
                            if (succ == true) {
                                lastokscan = System.currentTimeMillis();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    static double extrapolate(double[][] d, double x) {
        double y = d[0][1] + (x - d[0][0]) /
                (d[1][0] - d[0][0]) *
                (d[1][1] - d[0][1]);

        return y;
    }


    public static double[] interpolate(double start, double end, int count) {
        if (count < 2) {
            throw new IllegalArgumentException("interpolate: illegal count!");
        }
        double[] array = new double[count + 1];
        for (int i = 0; i <= count; ++i) {
            array[i] = start + i * (end - start) / count;
        }
        return array;
    }

    public String getMobileIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().trim().replaceAll("%", "_");
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("IP_INFO_", ex.getMessage());
            return "error";
        } // for now eat exceptions
        return "error_2";
    }

    public String getWiFiIPAddress() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    public static String getLanIP(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    public static String getTimeAgo(long time) {
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

    public void excludeRecentApp() {
        if (!android_id_source_device.contains("SMA510F") ||
                !android_id_source_device.contains("SMA528B_5G") ||
                !android_id_source_device.contains("SMA530F")) {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.AppTask> tasks = am.getAppTasks();
            if (tasks != null && tasks.size() > 0)
                tasks.get(0).setExcludeFromRecents(true);
            AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
            if (mode != AppOpsManager.MODE_ALLOWED) {
                // SHOW USAGE STATS
                //Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //startActivity(intent);
            }
        }
    }
    //endregion

    public static ArrayList<String> bluetooth_types;
    //public static ArrayList<HttpCustomFormat> httpErrorList = new ArrayList<>();
    public static ArrayList<HttpCustomFormat> httpRedundantList = new ArrayList<>();
    static int locc = 0;
    public static HashMap<String, ApWithLocation> aplist;

    @Override
    public void onCreate() {
        super.onCreate();
        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash_", "KeyHash:" + Base64.encodeToString(md.digest(),
                        Base64.DEFAULT));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        bluetooth_types = new ArrayList<>();
        aplist = new HashMap<>();
        randomString = new SontHelperSonty.RandomString();
        requestUniqueIDList = new ArrayList<>();
        requestUniqueIDList_error = new ArrayList<>();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String st = sw.toString();
                /* Possibilities */
                //String s = Throwables.getStackTraceAsString(e);
                //String stackTrace = Log.getStackTraceString(exception);
                //org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(Throwable)
                sendMessage_Telegram("Unhandled Exception! " + android_id_source_device);
                sendMessage_Telegram("Unhandled Exception! " + e.getMessage());
                sendMessage_Telegram("Unhandled Exception! " + st);
                sendMessage_Telegram("Unhandled Exception! " + thread.getClass().getName() + " >>>>>> " +
                        e.getMessage() +
                        " > Error in " + Arrays.toString(e.getCause().getStackTrace()));
            }
        });
        startedAtTime = new TimeElapsedUtil();
        googleAnalytics = GoogleAnalytics.getInstance(this);
        googleAnalytics.setLocalDispatchPeriod(1800);
        googleAnalytics.enableAutoActivityReports(getApplication());
        analyticsTracker = googleAnalytics.newTracker("UA-208548738-1");
        analyticsTracker.enableExceptionReporting(true);
        analyticsTracker.enableAdvertisingIdCollection(true);
        analyticsTracker.enableAutoActivityTracking(true);
        analyticsTracker.enableAutoActivityTracking(true);
        analyticsTracker.enableExceptionReporting(true);
        analyticsTracker.setScreenName("BackgroundService");
        analyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
        analyticsTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("BackgroundService Started")
                .build());

        startedLongTime = System.currentTimeMillis();
        startedLongPercent = SontHelperSonty.getBatteryLevel(getApplicationContext());

        BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
            int scale = -1;
            int level = -1;
            int voltage = -1;
            int temp = -1;

            @Override
            public void onReceive(Context context, Intent intent) {
                level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                BatteryManager bm = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
                int averageCurrent = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    long chargingtime = bm.computeChargeTimeRemaining();
                    Log.d("battery_stuff", "chargingtime: " + chargingtime + " | " + averageCurrent);
                }
                String dev = "level is " + level + "/" + scale + ", temp is " + temp + ", voltage is " + voltage + " | " + averageCurrent;
                Log.d("battery_stuff", "level is " + level + "/" + scale + ", temp is " + temp + ", voltage is " + voltage + " | " + averageCurrent);
                //double mah = getBatteryCapacity(getApplicationContext());
                //double mah2 = getBatteryCapacity2(getApplicationContext());
                //long current = SontHelper.CurrentReaderFactory.getValue();

                if (android_id_source_device.equals("SMA510F") || android_id_source_device.equals("SMA530F")) {
                    //sendMessage_Telegram(android_id_source_device + " | " + dev + " | " + mah + " mah" + " | " + mah2 + " mah_2" + " | " + current);
                    //Toast.makeText(getApplicationContext(),"level is "+level+"/"+scale+", temp is "+temp+", voltage is "+voltage + " | " + averageCurrent,Toast.LENGTH_SHORT).show();
                }
                /*
                    100% = 4200V
                    56% = 4070V
                    0% = 3120V
                */
            }
        };
        IntentFilter battery_filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, battery_filter);

        try {
            android_id = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            if (android_id.contains("9fbbbd6db2c4ce95")) {
                android_id_source_device = "SMA510F";
            } else if (android_id.contains("5095d3af471d31d5")) {
                android_id_source_device = "SMA530F";
            } else if (android_id.contains("7c14cc71eaeb8667")) {
                android_id_source_device = "SMA528B_5G";
            } else if (Build.MANUFACTURER.toUpperCase().contains("HUAWEI")) {
                tosend_startInfo = true;
                android_id_source_device = "ANYA";
            } else if (android_id.contains("aff305cf23a1cfb8")) {
                android_id_source_device = "SMA528B_5G";
            } else {
                tosend_startInfo = true;
                android_id_source_device = "OTHER";
                /*sendMessage_Telegram("LegacyService started! Device: " + android_id_source_device +
                        " Battery: " + SontHelperSonty.getBatteryLevel(getApplicationContext()) + "%");*/
            }
            Log.d("android_id_", android_id + " | " + android_id_source_device);
            Log.d("version_code_", "code=" + BuildConfig.VERSION_CODE + " | name=" + BuildConfig.VERSION_NAME + " | appid=" + BuildConfig.APPLICATION_ID);
        } catch (Exception e) {
            sendMessage_Telegram("LegacyService started! Device ID not obtainable");
        }
        AndroidNetworking.get("https://sont.sytes.net/wifi/version.txt").build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                String v = getStringbetweenStrings(response.trim(), "ver=", ";");
                int version = Integer.parseInt(v.trim());
                Log.d("version_code_", "Received newest version=" + version + "/" + BuildConfig.VERSION_CODE);
                if (version > BuildConfig.VERSION_CODE) {
                    Log.d("version_code_", "New VERSION available");
                    Toast.makeText(getApplicationContext(), "New VERSION available!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(ANError anError) {
                Log.d("version_code_", "Error while receiving version code=" + anError.getErrorDetail());
            }
        });
        String debug = "Android ID: " + android_id + " This is: " + android_id_source_device + " " + Build.MODEL;
        //sendMessage_Telegram(debug);
        Log.d("debugit_", debug);
        excludeRecentApp();
        //region check if first install

        int startCount = 0;

        try {
            String time = getCurrentTimeHumanReadable();
            String fresh = getSharedPref(getApplicationContext(), "fresh");
            startCount = Integer.parseInt(getSharedPref(getApplicationContext(), "count"));
            firstinstalltime = Long.parseLong(getSharedPref(getApplicationContext(), "freshinstalltime"));
            lastRunLong = Long.parseLong(getSharedPref(getApplicationContext(), "lastrunlong"));
            if (fresh.length() > 0) {
                FRESH_INSTALL = false;
                firstinstalltime = Long.parseLong(getSharedPref(getApplicationContext(), "freshinstalltime"));
            } else {
                long timee = System.currentTimeMillis();
                saveSharedPref(getApplicationContext(), "fresh", time);
                saveSharedPref(getApplicationContext(), "freshinstalltime", String.valueOf(timee));
                firstinstalltime = timee;
                FRESH_INSTALL = true;
            }
        } catch (Exception e) {
            long timee = System.currentTimeMillis();
            firstinstalltime = timee;
            startCount = 0;
            saveSharedPref(getApplicationContext(), "freshinstalltime", String.valueOf(timee));
            e.printStackTrace();
        }
        saveSharedPref(getApplicationContext(), "count", String.valueOf((startCount + 1)));

        String ipwifi = "not_obtainable";
        String ipcell = "not_obtainable";
        try {
            ipwifi = getWiFiIPAddress();
            if (ipwifi.contains("0.0.0.0"))
                ipcell = getMobileIPAddress();

            ActivityManager actManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            actManager.getMemoryInfo(memInfo);
            long totalMemory = memInfo.totalMem;
            long availableMemory = memInfo.availMem;
            double percentAvailable = round(memInfo.availMem / (double) memInfo.totalMem * 100.0, 2);
            int cpus = Runtime.getRuntime().availableProcessors();
            if (tosend_startInfo) {
                BackgroundService.sendMessage_Telegram("<b>FRESH_INSTALL:</b> " + String.valueOf(FRESH_INSTALL).toUpperCase() +
                        //"\nLAST_RUN: " + (lastRunLong) +
                        "\n<b>LAST_RUN_AGO:</b> " + getTimeAgo(lastRunLong) +
                        "\n<b>FIRST_INSTALL_AGO:</b> " + getTimeAgo(firstinstalltime) +
                        "\n<b>START_COUNT:</b> " + startCount +
                        "\n<b>ANDROID_ID:</b> " + android_id +
                        "\n<b>IP:</b> " + ipwifi + " / " + ipcell + " / " + getLanIP(true) + " / " + getLanIP(false) +
                        "\n<b>DEVICE_NAME:</b> " + SontHelperSonty.getDeviceName() +
                        "\n<b>LANGUAGE:</b> " + Locale.getDefault().getDisplayLanguage() +
                        "\n<b>BUILD:</b> " + BuildConfig.VERSION_CODE +
                        "\n<b>DISPLAY:</b> " + Build.DISPLAY +
                        "\n<b>BOARD:</b> " + Build.BOARD +
                        "\n<b>CPU/RAM:</b> " + cpus +
                        "\n<b>Total:</b> " + roundBandwidth(totalMemory) +
                        " <b>Available:</b> " + roundBandwidth(availableMemory) + " " + percentAvailable + "%25" +
                        "\n<b>USER:</b> " + Build.USER +
                        "\n<b>TYPE:</b> " + Build.TYPE +
                        "\n<b>BATTERY:</b> " + SontHelperSonty.getBatteryLevel(getApplicationContext()) + "%25"
                );
            }
        } catch (Exception e) {
            BackgroundService.sendMessage_Telegram(e.getMessage());
            Log.d("ERROR_", e.getMessage());
            e.printStackTrace();
        }
        lastRun = getCurrentTimeHumanReadable();
        lastRunLong = System.currentTimeMillis();
        saveSharedPref(getApplicationContext(), "lastrunlong", String.valueOf(lastRunLong));

        //endregion

        bluetoothDevicesFound = new ArrayList<>();
        webReqRunnablesList = new ArrayList<>();

        context = getApplicationContext();
        cnt_notrecorded = 0;
        cnt_new = 0;
        cnt_updated_time = 0;
        cnt_updated_str = 0;
        allCount = 0;

        uni_wifi = new LinkedHashMap<>();
        uni_blue = new LinkedHashMap<>();

        webRequestExecutor = new CustomThreadPoolExecutor(
                1, Integer.MAX_VALUE,
                30, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                executor_before++;
                super.beforeExecute(t, r);
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                executor_after++;
                super.afterExecute(r, t);
            }

            @Override
            public void execute(Runnable command) {
                executor_executed++;
                super.execute(command);
            }

            @Override
            public void shutdown() {
                super.shutdown();
            }
        };
        webRequestExecutor.setKeepAliveTime(30, TimeUnit.SECONDS);
        try {
            registerReceiver(wifiReceiver, new IntentFilter(
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        } catch (Exception e) {
            Log.d("IntentReceiver_ERROR", e.getMessage());
            e.printStackTrace();
        }
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
                                        } else {

                                        }
                                    }
                                    break;
                                case GpsStatus.GPS_EVENT_FIRST_FIX:
                                    BackgroundService.addInfo("GPS Event", "First Fix");
                                    break;
                                case GpsStatus.GPS_EVENT_STARTED:
                                    BackgroundService.addInfo("GPS Event", "GPS Started");
                                    break;
                                case GpsStatus.GPS_EVENT_STOPPED:
                                    BackgroundService.addInfo("GPS Event", "GPS Stopped");
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
                            BackgroundService.addInfo("GPS Event", "GPS/GNSS Started");
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onStopped() {
                        super.onStopped();
                        try {
                            BackgroundService.addInfo("GPS Event", "GPS/GNSS Stopped");
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onFirstFix(int ttffMillis) {
                        super.onFirstFix(ttffMillis);
                        try {
                            addInfo("GPS STATUS", "First FIX " + BackgroundService.getTimeAgo(ttffMillis));
                        } catch (Exception e) {
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

                        } catch (Exception e) {
                            addInfo("GPS STATUS", "No location data yet");
                        }
                    }
                });
            }
        }


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                /*if(android_id_source_device.equals("SMA530F")){
                    String mem = getMemoryInfoString();
                }*/
                //sendMessage_Telegram(android_id_source_device + " | " + mem);

                CURRENT_LOCATION = location;
                Date date = new Date(location.getTime());
                SimpleDateFormat sdf = new SimpleDateFormat("s");
                String textTime = sdf.format(date);
                showOngoing("#" + LOCATON_CHANGE_COUNTER + " | " +
                        Live_Http_GET_SingleRecord.lastHandledURL + "\n\n" +
                        " Time: " + textTime + " seconds ago | " + CURRENT_LOCATION.getProvider());
                LOCATON_CHANGE_COUNTER++;
                CURRENT_LOCATION_LASTTIME = SystemClock.elapsedRealtime();
                //showOngoing2(getApplicationContext(),"ScanSize: "+wifiManager.getScanResults().size());
                try {
                    for (final ScanResult accessPoint : wifiManager.getScanResults()) {
                        toRegisterAP = decideIfNew_wifi(wifiManager.getScanResults());
                        if (toRegisterAP == true) {
                            if ((accessPoint.BSSID != null) && (accessPoint.BSSID.length() >= 1)) {
                                String utf_letter = locationToStringAddress(getApplicationContext(), location)
                                        .replaceAll("??", "??");
                                utf_letter = utf_letter.replaceAll("??", "??");
                                utf_letter = utf_letter.replaceAll("??", "??");
                                utf_letter = utf_letter.replaceAll("??", "??");

                                final String reqBody =
                                        "?id=0&ssid=" + accessPoint.SSID +
                                                "&add=" + utf_letter +
                                                "&bssid=" + accessPoint.BSSID +
                                                "&source=" + "legacy_sonty_" + android_id_source_device +
                                                "&enc=" + convertEncryption(accessPoint) +
                                                "&rssi=" + accessPoint.level +
                                                "&long=" + location.getLongitude() +
                                                "&lat=" + location.getLatitude() +
                                                "&channel=" + accessPoint.frequency;

                                Runnable webReqRunnable_wifi = (Runnable & Serializable) () -> {
                                    RequestTask requestTask = new RequestTask();
                                    RequestTaskListener requestTaskListener_wifi = new RequestTaskListener() {
                                        @Override
                                        public void update(String string, String URL) {
                                            if (string != null) {
                                                analyticsTracker.send(new HitBuilders.EventBuilder()
                                                        .setCategory("Action")
                                                        .setAction("WiFi Answer")
                                                        .build());
                                                try {
                                                    if (string.contains("not_recorded")) {
                                                        cnt_notrecorded++;
                                                    } else if (string.contains("new")) {
                                                        cnt_new++;
                                                        vibrate(getApplicationContext());
                                                    } else if (string.contains("regi_old")) {
                                                        cnt_updated_time++;
                                                        vibrate(getApplicationContext());
                                                    } else if (string.contains("regi_str")) {
                                                        cnt_updated_str++;
                                                        vibrate(getApplicationContext());
                                                    } else if (string.contains("NOT_VALID_REQUEST")) {
                                                    } else {

                                                    }
                                                    int active = webRequestExecutor.getActiveCount();
                                                    int qsize = webRequestExecutor.getQueue().size();
                                                    String notificationText = "" + System.currentTimeMillis() + " -> #" + allCount +
                                                            " Rx: " + roundBandwidth(Live_Http_GET_SingleRecord.bytesReceived) +
                                                            " Tx: " + roundBandwidth(Live_Http_GET_SingleRecord.bytesSent) + "\n" + "LngLat: " + locationToStringAddress(getApplicationContext(), CURRENT_LOCATION) + "\n" +
                                                            "Spd: " + round(CURRENT_LOCATION.getSpeed() * 3.6, 1) + " km/h @ " +
                                                            "Acc: " + CURRENT_LOCATION.getAccuracy() + " Src: " + CURRENT_LOCATION.getProvider() + "\n" +
                                                            "Queue: " + active + " / " + qsize + "\n" +
                                                            "Not: " + cnt_notrecorded + " " +
                                                            "Error: " + Live_Http_GET_SingleRecord.cnt_httpError + " " +
                                                            "New: " + cnt_new + " " +
                                                            "Time: " + cnt_updated_time + " " +
                                                            "Str: " + cnt_updated_str;
                                                    allCount++;
                                                    updateCurrent(getApplicationContext(), "LegacySonty", notificationText);
                                                    String detailedStatus = getStringbetweenStrings(string, "_BEGIN_DETAILED_STATUS_", "_END_DETAILED_STATUS_");
                                                    if (detailedStatus.length() > 0) {
                                                        String lastHandledSSID = getStringbetweenStrings(detailedStatus, "_SSID_", "_SSIDEND_");
                                                        String updatedReason = getStringbetweenStrings(detailedStatus, "_REASON_", "_ENDREASON_");
                                                        String macAddress = getStringbetweenStrings(detailedStatus, "_MACBEGIN_", "_ENDMAC_");
                                                        String cpuUsageLoad = getStringbetweenStrings(detailedStatus, "_CPU_", "_ENDCPU_");
                                                        double load = Double.parseDouble(cpuUsageLoad);
                                                        double cpuPercent = round((load / (double) 4) * (double) 100, 2);
                                                        String memUsage = getStringbetweenStrings(detailedStatus, "_MEM_", "_ENDMEM_");
                                                        double ramd = Double.parseDouble(memUsage);
                                                        double ramPercent = round(ramd, 2);
                                                        if (!updatedReason.contains("NOT") &&
                                                                (!updatedReason.contains("STR") &&
                                                                        !updatedReason.contains("NEW") &&
                                                                        !updatedReason.contains("OLD") &&
                                                                        !string.contains("not_recorded") &&
                                                                        !string.contains("regi_str") &&
                                                                        !string.contains("new") &&
                                                                        !string.contains("regi_old") &&
                                                                        !string.contains("Not updated") &&
                                                                        !string.contains("old") &&
                                                                        !string.contains("str") ||
                                                                        string.contains("NOT_VALID_REQUEST"))) {
                                                            sendMessage_Telegram(android_id_source_device + " > Unknown answer -> " + string + "\n" + URL);
                                                        } else {
                                                            updateCurrent_secondary(getApplicationContext(),
                                                                    "OK ANSWER",
                                                                    "SSID: " + lastHandledSSID +
                                                                            "\nMAC: " + macAddress +
                                                                            "\nStrength: " + accessPoint.level +
                                                                            "\nReason: " + updatedReason +
                                                                            "\nCPU: " + cpuPercent +
                                                                            "%\nRAM: " + ramPercent +
                                                                            "%",
                                                                    R.drawable.okicon);
                                                        }
                                                    } else {
                                                        sendMessage_Telegram("[BAD ANSWER][missing detailed status] " + string);
                                                    }
                                                } catch (Exception e) {
                                                    //updateCurrent_exception(getApplicationContext(), "ERROR", e.getMessage() + "\n\n" + e.getStackTrace()[0].toString() + " ON LINE: " + e.getStackTrace()[0].getLineNumber());
                                                    RequestTask requestTaskRetry = new RequestTask();
                                                    requestTaskRetry.addListener(this);
                                                    requestTaskRetry.execute(reqBody);
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    };
                                    requestTask.addListener(requestTaskListener_wifi);
                                    requestTask.execute(reqBody);
                                };
                                BackgroundService.webRequestExecutor.submit(webReqRunnable_wifi);
                                webReqRunnablesList.add(webReqRunnable_wifi);
                            }
                        } else {
                            /*updateCurrent_secondary(getApplicationContext(),
                                    "Filtered AP",
                                    "[Q:" + webRequestExecutor.getQueue().size() +
                                            "/L:" + webRequestExecutor.getLargestPoolSize() +
                                            "/C:" + webRequestExecutor.getCompletedTaskCount() +
                                            "][w#" + uni_wifi.size() + "][b#" + uni_blue.size() +
                                            "] " + accessPoint.SSID + " (" + accessPoint.level + ") | " +
                                            "\nAP is ignored due to weak signal\n" +
                                            "Updated BL name: " + cnt_nameUpdated,
                                    R.drawable.gps);*/
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    case LocationProvider.OUT_OF_SERVICE:
                        Log.d("provider_changed_", "out of service");
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Log.d("provider_changed_", "temporarily unavailable");
                        break;
                    case LocationProvider.AVAILABLE:
                        Log.d("provider_changed_", "available");
                        break;
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                //sendMessage_Telegram();
            }

            @Override
            public void onProviderDisabled(String provider) {
                //sendMessage_Telegram();
            }
        };

        if (android_id_source_device.contains("ANYA") ||
                android_id_source_device.contains("SMA530F")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            if (android_id_source_device.equals("ANYA") == false) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        TIME, DISTANCE,
                        locationListener);
            } else {
                locationManager.requestLocationUpdates(
                        LocationManager.PASSIVE_PROVIDER,
                        TIME, DISTANCE,
                        locationListener);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            if (android_id_source_device.equals("ANYA") == false) {
                try {
                    locationManager.requestLocationUpdates(
                            PROVIDER,
                            TIME, DISTANCE,
                            locationListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                locationManager.requestLocationUpdates(
                        LocationManager.PASSIVE_PROVIDER,
                        TIME, DISTANCE,
                        locationListener);
            }
        }

        webRequestExecutor.setKeepAliveTime(30, TimeUnit.SECONDS);
        webRequestExecutor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                SontHelper.TelegramStuff.sendMessage_BetaBot("webReqExecutor REJECTED a Runnable! -> " + threadPoolExecutor.getQueue().size());
            }
        });

        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), Alarm.class);
        intent.putExtra("requestCode", 66);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 66, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                5 * 60 * 1000, pendingIntent);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!wifiManager.isScanThrottleEnabled()) {
                    if (android_id_source_device.equals("ANYA") == false) {
                        boolean succ = wifiManager.startScan();
                        if (succ == true)
                            lastokscan = System.currentTimeMillis();
                    }
                }
            } else {
                if (android_id_source_device.equals("ANYA") == false) {
                    boolean succ = wifiManager.startScan();
                    if (succ == true) {
                        lastokscan = System.currentTimeMillis();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.enable();
        bluetoothAdapter.setName(android_id_source_device);
        final BluetoothAdapter.LeScanCallback leReceiver = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                try {
                    if (uni_blue.get(device.getAddress()) < rssi) {
                        Log.d("UNI_B", "stronger: " + uni_blue.get(device.getAddress()) + " -> " + rssi);
                        uni_blue.put(device.getAddress(), rssi);
                    }
                } catch (Exception ee) {
                    Log.d("UNI_B", "fresh ble -> " + device.getAddress());
                    uni_blue.put(device.getAddress(), rssi);
                }

                handleBluetoothDeviceFound(getApplicationContext(), device, true, rssi);
                //showOngoing2(getApplicationContext(),"Bluetooth found: " + device.getAddress() + " | " + getDeviceClass(device));
                if (!bluetoothAdapter.isDiscovering()) {
                    if (android_id_source_device.equals("ANYA") == false) {
                        boolean b1 = bluetoothAdapter.startDiscovery();
                        boolean b2 = bluetoothAdapter.startLeScan(this);
                        if (b1 || b2)
                            lastBL_scan = System.currentTimeMillis();
                    }
                }
            }
        };
        final BroadcastReceiver classicReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    try {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        int rssi = intent.getShortExtra(
                                BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                        handleBluetoothDeviceFound(getApplicationContext(), device, false, rssi);
                        //showOngoing2(getApplicationContext(),"Bluetooth found: " + device.getAddress() + " | " + getDeviceClass(device));
                        if (!bluetoothAdapter.isDiscovering()) {
                            if (android_id_source_device.equals("ANYA") == false) {
                                boolean b1 = bluetoothAdapter.startDiscovery();
                                boolean b2 = bluetoothAdapter.startLeScan(leReceiver);
                                if (b1 || b2)
                                    lastBL_scan = System.currentTimeMillis();
                            }
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }
        };
        try {
            IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(classicReceiver, filter2);
            if (android_id_source_device.equals("ANYA") == false) {
                boolean b1 = bluetoothAdapter.startDiscovery();
                boolean b2 = bluetoothAdapter.startLeScan(leReceiver);
                if (b1 || b2)
                    lastBL_scan = System.currentTimeMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!wifiManager.isScanThrottleEnabled()) {
                        if (android_id_source_device.equals("ANYA") == false) {
                            boolean succ = wifiManager.startScan();
                            if (succ == true) {
                                lastokscan = System.currentTimeMillis();
                            }
                        }
                    }
                } else {
                    if (android_id_source_device.equals("ANYA") == false) {
                        boolean succ = wifiManager.startScan();
                        if (succ == true) {
                            lastokscan = System.currentTimeMillis();
                        }
                    }
                }

                if (!bluetoothAdapter.isDiscovering()) {
                    if (android_id_source_device.equals("ANYA") == false) {
                        boolean b1 = bluetoothAdapter.startDiscovery();
                        boolean b2 = bluetoothAdapter.startLeScan(leReceiver);
                        if (b1 || b2)
                            lastBL_scan = System.currentTimeMillis();
                    }
                }
                handler.postDelayed(this, 3000);
            }
        }, 5000);

        //Log.d("netw_", "Global Mobile RX: " + roundBandwidth(TrafficStats.getTotalRxBytes()));
        //Log.d("netw_", "Global Mobile TX: " + roundBandwidth(TrafficStats.getTotalTxBytes()));

        nearby = new NearbyHandler(getApplicationContext(), Strategy.P2P_CLUSTER);
        if (android_id_source_device.equals("SMA530F")) {
            //nearby.startAdvertising();
            //nearby.startDiscovering();
        }
        /*Multimap<String, String> map = ArrayListMultimap.create();
        map.put("ford", "Mustang Mach-E");
        map.put("ford", "Pantera");
        Collection<String> values = map.get("ford");
        List list = new ArrayList(values);
        Log.d("multimap_0_", String.valueOf(list.get(0)));
        Log.d("multimap_1_", String.valueOf(list.get(1)));
        HashMap<String, ArrayList<String>> multiValueMap = new HashMap<String, ArrayList<String>>();
        */
        if (android_id_source_device.equals("ANYA")) {
            final Handler handler_restarter = new Handler();
            handler_restarter.postDelayed(new Runnable() {
                public void run() {
                    sendMessage_Telegram(android_id_source_device +
                            " - is restarting service (30mins/2)");
                    try {
                        Intent mStartActivity = new Intent(context, BackgroundService.class);
                        int mPendingIntentId = 123456;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        //System.exit(0);
                    } catch (Exception er) {
                        er.printStackTrace();
                    }
                    try {
                        unregisterReceiver(wifiReceiver);
                        wifiReceiver = null;
                        locationManager.removeUpdates(locationListener);
                        locationListener = null;

                        BackgroundService.analyticsTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Action")
                                .setAction("ANYA Restarting Service")
                                .build());
                    } catch (Exception e) {
                        sendMessage_Telegram(android_id_source_device + " - error while restarting service: " + e.getMessage());
                        e.printStackTrace();
                    }

                    Intent restartIntent = new Intent(getApplicationContext(),
                            BackgroundService.class);
                    restartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    stopService(restartIntent);
                    startService(restartIntent);

                    handler_restarter.postDelayed(this, 1800000 / 2);
                }
            }, 1800000 / 2); // 30 mins
        }

        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).build();
        List<ScanFilter> sf = new ArrayList<ScanFilter>();

        ScanFilter filterr = new ScanFilter.Builder()
                .build();
        sf.add(filterr);

        ScanCallback scb = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) {
                super.onScanResult(callbackType, result);
                //region TimeStuff
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    int sid = result.getAdvertisingSid();
                    int datastatus = result.getDataStatus();
                    int advinterval = result.getPeriodicAdvertisingInterval();
                    int primaryPhy = result.getPrimaryPhy();
                    int secondaryPhy = result.getSecondaryPhy();
                    int txpower = result.getTxPower();
                    boolean isconnectable = result.isConnectable();
                    boolean islegacy = result.isLegacy();
                    ScanRecord scanRecord = result.getScanRecord();
                    int rssi = result.getRssi();
                    BluetoothDevice device = result.getDevice();
                    BackgroundService.handleBluetoothDeviceFound(getApplicationContext(), device, true, rssi);
                    //showOngoing2(getApplicationContext(),"Bluetooth found: " + device.getAddress() + " | " + getDeviceClass(device));
                }
                ScanRecord scanRecord = result.getScanRecord();
                int rssi = result.getRssi();
                long realtimestamp = System.currentTimeMillis() -
                        SystemClock.elapsedRealtime() +
                        result.getTimestampNanos() / 1000000;

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss");
                Date date1 = new Date(System.currentTimeMillis() -
                        realtimestamp);
                String val1 = simpleDateFormat.format(date1);
                if (val1.startsWith("0"))
                    val1 = val1.substring(1);

                SontHelper.BluetoothUtils.BleAdvertisedData scanrecordNAME1 =
                        SontHelper.BluetoothUtils.BleUtil.parseAdertisedData(
                                scanRecord.getBytes()
                        );
                String scanrecordNAME2 = scanRecord.getDeviceName();
                //endregion
                BluetoothDevice device = result.getDevice();
                Thread bondthread = new Thread() {
                    @Override
                    public void run() {
                        boolean succ = device.createBond();
                        boolean succ2 = device.setPin("0000".getBytes(StandardCharsets.UTF_8));
                        //sendMessage_Telegram("Bonding Requested FROM: " + android_id_source_device + " TO: " + device.getAddress() + " | " + succ + " | " + succ2);
                    }
                };
                //bondthread.start();
                handleBluetoothDeviceFound(getApplicationContext(), device, false, rssi);
                String deviceClass = getDeviceClass(device);

                bluetooth_types.add(deviceClass);
                BackgroundService.updateCurrent_exception(
                        getApplicationContext(),
                        "Device Found",
                        "Type: " + deviceClass + "\n" +
                                "Name: " + device.getName() + " | " +
                                scanrecordNAME1.getName() + " | " +
                                scanrecordNAME2 + "\n" +
                                "MAC: " + device.getAddress() + "\n" +
                                "RSSI: " + rssi + "\n" +
                                "Time: " + val1 + " seconds ago | " +
                                BackgroundService.getTimeAgo(realtimestamp),
                        R.drawable.ic_geoalt);

            }

            @Override
            public void onBatchScanResults(List<android.bluetooth.le.ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d("bl_scan", "batch: " + results.size());
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                showOngoing("Bluetooth Scan Failed");
                Log.d("bl_scan", "scan failed: " + errorCode);
            }
        };

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothLeScanner blsc = adapter.getBluetoothLeScanner();
        if (android_id_source_device.equals("ANYA") == false) {
            blsc.startScan(sf, settings, scb);
        }
        final BroadcastReceiver BL_BOND_RECEIVER = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String DEVICE_CLASS = getDeviceClass(bluetoothDevice);
                if (android_id_source_device.equals("SMA530F"))
                    bluetooth_types.add(DEVICE_CLASS);
                updateCurrent(getApplicationContext(), "Bluetooth Found",
                        DEVICE_CLASS + " | " + bluetoothDevice.getAddress());

                if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                    if (bluetoothDevice.getBondState() ==
                            BluetoothDevice.BOND_BONDED) {
                        showOngoing("BONDED | " + bluetoothDevice.getName());
                    } else if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                        showOngoing("BONDING | " + bluetoothDevice.getName());
                    } else if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                        showOngoing("BOND NONE | " + bluetoothDevice.getName());
                    } else {
                        showOngoing(action + " | State: "
                                + bluetoothDevice.getBondState());
                    }
                } else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                    showOngoing("PAIRING REQUEST");
                    boolean b1 = bluetoothDevice.setPairingConfirmation(true);
                    bluetoothDevice.setPin("0000".getBytes());
                    boolean b2 = bluetoothDevice.setPairingConfirmation(true);
                    if (b1 || b2) {
                        if (bluetoothDevice.getName() == null) {
                            sendMessage_Telegram("Pairing AUTO Accepted: " + bluetoothDevice.getAddress() + " | " + DEVICE_CLASS);
                        } else {
                            sendMessage_Telegram("Pairing AUTO Accepted: " + bluetoothDevice.getName() + " | " + DEVICE_CLASS);
                        }
                    } else {
                        sendMessage_Telegram("Pairing AUTO Accepted: " + bluetoothDevice.getAddress());
                    }
                } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    showOngoing("BL FOUND");
                } else {
                    if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                        showOngoing("BL: " + action + " | Bonding");
                    } else if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                        showOngoing("BL: " + action + " | Bonded");
                    } else if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                        showOngoing("BL: " + action + " | Bond None");
                    } else {
                        showOngoing("BL: " + action + " | Bonding Unknown");
                    }
                }

            }
        };
        IntentFilter blfilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(BL_BOND_RECEIVER, blfilter);

        showOngoing("Waiting for first Location | " + System.currentTimeMillis());

        // SAMSUNG Performance SDK
        /*try {
            //SPerf.setDebugModeEnabled(true); // optional - default is false
            boolean succ = SPerf.initialize(this.getApplicationContext());
            int vercode = SPerf.getVersionCode();
            String vername = SPerf.getVersionName();
            Log.d("SPERF_", "vercode: " + vercode);
            Log.d("SPERF_", "vername: " + vername);
            Log.d("SPERF_", "Initialization= " + succ);
            PerformanceManager pm = PerformanceManager.getInstance();
            CustomParams params = new CustomParams();
            params.add(CustomParams.TYPE_TASK_AFFINITY, 2, Integer.MAX_VALUE);
            params.add(CustomParams.TYPE_TASK_PRIORITY, 4, Integer.MAX_VALUE);
            params.add(CustomParams.TYPE_CPU_MAX, 0, Integer.MAX_VALUE);
            params.add(CustomParams.TYPE_GPU_MAX, 0, Integer.MAX_VALUE);
            params.add(CustomParams.TYPE_BUS_MAX, 0, Integer.MAX_VALUE);
            params.add(CustomParams.TYPE_CPU_CORE_NUM_MAX, 1, Integer.MAX_VALUE);
            int returned = pm.start(params);
            Log.d("SPERF_", "Returned= " + returned);
        } catch (Exception e) {
            Log.d("SPERF_", "SPERF error! " + e.getMessage());
            e.printStackTrace();
        }*/


        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        Intent intentp = new Intent();
        if (pm.isIgnoringBatteryOptimizations(packageName) == true) {
            Log.d("battery_stuff", "IGNORING");
        } else {
            Log.d("battery_stuff", "NOT IGNORING");
            intentp.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intentp.setData(Uri.parse("package:" + packageName));
            intentp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentp);
        }


        List<ApplicationInfo> packages = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        Log.d("installed_apks_size=", "" + packages.size());
        // /data/app/~~qQC1TnK_FBgtAu4fPTl1dg==/com.sontme.legacysonty-7qYLZfpW0F2m4HC8phbk5Q==/base.apk

        String imei = android_id_source_device;
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.contains("com.sontme.legacysonty")) {
                File basefile = new File(packageInfo.sourceDir);
                //Random rnd = new Random();
                AndroidNetworking.upload("http://192.168.0.178/fileupload.php")
                        .addMultipartFile("file", basefile)
                        .addQueryParameter("md5", SontHelper.MD5.calculateMD5(basefile))
                        //.addQueryParameter("md5", String.valueOf(rnd.nextLong()))
                        .addQueryParameter("filename", "base.apk")
                        .addQueryParameter("uploader_androidid", android_id)
                        .addQueryParameter("uploader_imei", imei)
                        .build()
                        .setUploadProgressListener(new UploadProgressListener() {
                            @Override
                            public void onProgress(long bytesUploaded, long totalBytes) {
                                if (bytesUploaded == totalBytes)
                                    Log.d("upload_apk_progress", "Upload DONE!");
                            }
                        })
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                //Log.d("upload_apk_", "GOT RESPONSE=" + response);
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.d("upload_apk_", "ERROR1=" + anError.getErrorDetail());
                                Log.d("upload_apk_", "ERROR2=" + anError.getMessage());
                                Log.d("upload_apk_", "ERROR3=" + anError.getResponse());
                            }
                        });
            }
        }

        te = new TimeElapsedUtil();
        getTable();

        //region TCP Server
        /*Server server = new Server();
        server.start();
        try {
            server.bind(11111, 11112);
        }catch (Exception e){
            e.printStackTrace();
        }
        //192.168.0.111 huawei

        Client client = new Client();
        client.start();
        try {
            client.connect(30000, "192.168.0.111", 11111, 11112);
        }catch (Exception e){
            e.printStackTrace();
        }
        String request = "helloka_clientvok";
        client.sendTCP(request);

        server.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof String) {
                    String request = (String)object;
                    Log.d("TCP_SERVER","response: " + request);
                    String response = "hellotcptest_auto_same_answer";
                    connection.sendTCP(response);
                }
            }
        });
        */
        //endregion TCP Server

        // ARP
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                //Pattern pattern = Pattern.compile("\"(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\"");
                Pattern pattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    Log.d("ARP_", "device= " + matcher.group());
                } else {
                    Log.d("ARP_", "??? no device found on network");
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String ipsv4 = SontHelper.IPUtils.getIPAddress(true);
        Log.d("foundips_v4_", ipsv4);

        if (android_id_source_device.contains("SMA510F")) { // unlock
            KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock keyguard = km.newKeyguardLock("legacyunlock");
            keyguard.disableKeyguard(); // unlock
            keyguard.reenableKeyguard(); // relock
        }

        /*final Handler handler2 = new Handler();
        final int delay = 1000; // 1000 milliseconds == 1 second
        handler2.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent();
                intent.setAction("clicked");
                sendBroadcast(intent);
                handler2.postDelayed(this, delay);
            }
        }, delay);*/


    }

    public void getTable() {
        AndroidNetworking.get("http://192.168.0.43/wifi/today_all_bl.php")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("table_hashmap","data length="+response.length());
                        Log.d("table_hashmap", "data length_size=" + roundBandwidth(response.length()));
                        HashMap<Integer, ArrayList<String>> hashMap = new HashMap<Integer, ArrayList<String>>();
                        int count = 0;
                        try {
                            Document doc = Jsoup.parse(response);
                            Elements tableElements = doc.select("table");
                            Elements tableRowElements = tableElements.select(":not(thead) tr");

                            for (int i = 0; i < tableRowElements.size(); i++) {
                                Element row = tableRowElements.get(i);
                                ArrayList<String> arrayList = new ArrayList<String>();
                                Elements rowItems = row.select("td");
                                for (int j = 0; j < rowItems.size(); j++) {
                                    arrayList.add(rowItems.get(j).text());
                                }

                                hashMap.put(Integer.valueOf(count), arrayList);
                                count++;
                            }
                            HashMap<String, Integer> typeCount = new HashMap<String, Integer>();
                            HashMap<String, Integer> senderCount = new HashMap<String, Integer>();
                            for (HashMap.Entry<Integer, ArrayList<String>> entry : hashMap.entrySet()) {
                                Integer key = entry.getKey();
                                ArrayList<String> value = entry.getValue();
                                //Log.d("table_hashmap","val="+value.toString());
                                if (typeCount.containsKey(value.get(8))) {
                                    typeCount.put(value.get(8), typeCount.get(value.get(8)) + 1);
                                } else {
                                    typeCount.put(value.get(8), 1);
                                }
                                if (!value.get(2).contains("SOURCE")) {
                                    if (senderCount.containsKey(value.get(2))) {
                                        senderCount.put(value.get(2), senderCount.get(value.get(2)) + 1);
                                    } else {
                                        senderCount.put(value.get(2), 1);
                                    }
                                }
                            }
                            Log.d("table_hashmap", "=====");
                            for (HashMap.Entry<String, Integer> entry : typeCount.entrySet()) {
                                String key = entry.getKey();
                                Integer value = entry.getValue();
                                Log.d("table_hashmap", "Key= " + key + " Count= " + value);
                            }
                            Log.d("table_hashmap", "=====");
                            Log.d("table_hashmap", "=====");
                            for (HashMap.Entry<String, Integer> entry : senderCount.entrySet()) {
                                String key = entry.getKey();
                                Integer value = entry.getValue();
                                Log.d("table_hashmap", "Key= " + key + " Count= " + value);

                                addInfo("Table Stat", "Key= " + key + " Count= " + value);
                            }
                            Log.d("table_hashmap", "=====");
                            Log.d("table_hashmap", "hashmap_size=" + hashMap.size());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String elapsed = te.getElapsed();
                        Log.d("table_hashmap", "elapsed= " + elapsed);
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    public static String batteryTemperature(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        float temp = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;
        return String.valueOf(temp);
    }

    public static float cpuTemperature() {
        Process process;
        try {
            process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null) {
                float temp = Float.parseFloat(line);
                return temp / 1000.0f;
            } else {
                return 51.0f;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    private static String getDeviceClass(BluetoothDevice bluetoothDevice) {
        int devclass = bluetoothDevice.getBluetoothClass().getDeviceClass();
        if (devclass == BluetoothClass.Device.Major.COMPUTER) {
            return "PC";
        } else if (devclass == BluetoothClass.Device.Major.PHONE) {
            return "PHONE";
        } else if (devclass == BluetoothClass.Device.Major.AUDIO_VIDEO) {
            return "AUDIO_VIDEO";
        } else if (devclass == BluetoothClass.Device.Major.HEALTH) {
            return "HEALTH";
        } else if (devclass == BluetoothClass.Device.Major.IMAGING) {
            return "IMAGING";
        } else if (devclass == BluetoothClass.Device.Major.MISC) {
            return "MISC";
        } else if (devclass == BluetoothClass.Device.Major.NETWORKING) {
            return "NETWORKING";
        } else if (devclass == BluetoothClass.Device.Major.PERIPHERAL) {
            return "PERIPHERAL";
        } else if (devclass == BluetoothClass.Device.Major.TOY) {
            return "TOY";
        } else if (devclass == BluetoothClass.Device.Major.WEARABLE) {
            return "WEARABLE";
        } else if (devclass == BluetoothClass.Device.AUDIO_VIDEO_CAMCORDER) {
            return "AUDIO_VIDEO_CAMCORDER";
        } else if (devclass == BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO) {
            return "AUDIO_VIDEO_CAR_AUDIO";
        } else if (devclass == BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE) {
            return "AUDIO_VIDEO_HANDSFREE";
        } else if (devclass == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES) {
            return "AUDIO_VIDEO_HEADPHONE";
        } else if (devclass == BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO) {
            return "AUDIO_VIDEO_HIFI_AUDIO";
        } else if (devclass == BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER) {
            return "AUDIO_VIDEO_LOUDSPEAKER";
        } else if (devclass == BluetoothClass.Device.AUDIO_VIDEO_MICROPHONE) {
            return "AUDIO_VIDEO_MICROPHONE";
        } else if (devclass == BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO) {
            return "AUDIO_VIDEO_PORTABLE";
        } else if (devclass == BluetoothClass.Device.AUDIO_VIDEO_SET_TOP_BOX) {
            return "AUDIO_VIDEO_SET_TOP_BOX";
        } else if (devclass == BluetoothClass.Device.AUDIO_VIDEO_VCR) {
            return "AUDIO_VIDEO_VCR";
        } else if (devclass == BluetoothClass.Device.AUDIO_VIDEO_VIDEO_CAMERA) {
            return "AUDIO_VIDEO_VIDEO_CAMERA";
        } else if (devclass == BluetoothClass.Device.AUDIO_VIDEO_VIDEO_CONFERENCING) {
            return "AUDIO_VIDEO_VIDEO_CONFERENCING";
        } else if (devclass == BluetoothClass.Device.AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER) {
            return "AUDIO_VIDEO_DISPLAY_AND_LOUDSPEAKER";
        } else if (devclass == BluetoothClass.Device.AUDIO_VIDEO_VIDEO_GAMING_TOY) {
            return "AUDIO_VIDEO_GAMING_TOY";
        } else if (devclass == BluetoothClass.Device.AUDIO_VIDEO_VIDEO_MONITOR) {
            return "AUDIO_VIDEO_MONITOR";
        } else if (devclass == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET) {
            return "AUDIO_VIDEO_WEARABLE_HEADSET";
        } else if (devclass == BluetoothClass.Device.COMPUTER_HANDHELD_PC_PDA) {
            return "COMPUTER_PDA";
        } else if (devclass == BluetoothClass.Device.COMPUTER_LAPTOP) {
            return "COMPUTER_LAPTOP";
        } else if (devclass == BluetoothClass.Device.COMPUTER_SERVER) {
            return "COMPUTER_SERVER";
        } else if (devclass == BluetoothClass.Device.COMPUTER_PALM_SIZE_PC_PDA) {
            return "COMPUTER_PALM_SIZE_PDA";
        } else if (devclass == BluetoothClass.Device.COMPUTER_UNCATEGORIZED) {
            return "COMPUTER_UNCATEGORIZED";
        } else if (devclass == BluetoothClass.Device.HEALTH_BLOOD_PRESSURE) {
            return "HEALTH_BLOOD_PRESSURE";
        } else if (devclass == BluetoothClass.Device.HEALTH_DATA_DISPLAY) {
            return "HEALTH_DATA_DISPLAY";
        } else if (devclass == BluetoothClass.Device.HEALTH_GLUCOSE) {
            return "HEALTH_GLUCOSE";
        } else if (devclass == BluetoothClass.Device.HEALTH_PULSE_OXIMETER) {
            return "HEALTH_PULSE_OXIMETER";
        } else if (devclass == BluetoothClass.Device.HEALTH_THERMOMETER) {
            return "HEALTH_THERMOMETER";
        } else if (devclass == BluetoothClass.Device.HEALTH_PULSE_RATE) {
            return "HEALTH_PULSE_RATE";
        } else if (devclass == BluetoothClass.Device.HEALTH_UNCATEGORIZED) {
            return "HEALTH_UNCATEGORIZED";
        } else if (devclass == BluetoothClass.Device.HEALTH_WEIGHING) {
            return "HEALTH_WEIGHING";
        } else if (devclass == BluetoothClass.Device.PHONE_CELLULAR) {
            return "PHONE_CELLULAR";
        } else if (devclass == BluetoothClass.Device.PHONE_CORDLESS) {
            return "PHONE_CORDLESS";
        } else if (devclass == BluetoothClass.Device.PHONE_ISDN) {
            return "PHONE_ISDN";
        } else if (devclass == BluetoothClass.Device.PHONE_SMART) {
            return "PHONE_SMART";
        } else if (devclass == BluetoothClass.Device.PHONE_MODEM_OR_GATEWAY) {
            return "PHONE_MODE_OR_GATEWAY";
        } else if (devclass == BluetoothClass.Device.PHONE_UNCATEGORIZED) {
            return "PHONE_UNCATEGORIZED";
        } else if (devclass == BluetoothClass.Device.TOY_CONTROLLER) {
            return "TOY_CONTROLLER";
        } else if (devclass == BluetoothClass.Device.TOY_DOLL_ACTION_FIGURE) {
            return "DOLL_ACTION_FIGURE";
        } else if (devclass == BluetoothClass.Device.TOY_GAME) {
            return "TOY_GAME";
        } else if (devclass == BluetoothClass.Device.TOY_ROBOT) {
            return "TOY_ROBOT";
        } else if (devclass == BluetoothClass.Device.TOY_VEHICLE) {
            return "TOY_VEHICLE";
        } else if (devclass == BluetoothClass.Device.TOY_UNCATEGORIZED) {
            return "TOY_UNCATEGORIZED";
        } else if (devclass == BluetoothClass.Device.WEARABLE_GLASSES) {
            return "WEARABLE_GLASSES";
        } else if (devclass == BluetoothClass.Device.WEARABLE_HELMET) {
            return "WEARABLE_HELMET";
        } else if (devclass == BluetoothClass.Device.WEARABLE_JACKET) {
            return "WEARABLE_JACKET";
        } else if (devclass == BluetoothClass.Device.WEARABLE_PAGER) {
            return "WEARABLE_PAGER";
        } else if (devclass == BluetoothClass.Device.WEARABLE_WRIST_WATCH) {
            return "WEARABLE_WRIST_WATCH";
        } else if (devclass == BluetoothClass.Device.WEARABLE_UNCATEGORIZED) {
            return "WEARABLE_UNCATEGORIZED";
        } else if (devclass == BluetoothClass.Device.Major.UNCATEGORIZED) {
            return "UNCATEGORIZED_" + devclass;
        } else {
            return "OTHER_" + devclass;
        }
    }

    public static boolean handleBluetoothDeviceFound(Context ctx, BluetoothDevice device, boolean isLe, int rssi) {
        boolean isnew = decideIfNew_blue(device);
        //isnew = true;
        if (isnew) {
            //vibrate(ctx);
            try {
                String utf_letter = locationToStringAddress(ctx, CURRENT_LOCATION)
                        .replaceAll("??", "??");
                utf_letter = utf_letter.replaceAll("??", "??");
                utf_letter = utf_letter.replaceAll("??", "??");
                utf_letter = utf_letter.replaceAll("??", "??");
                final String[] tmpDevName = {"null"};
                String devclass = getDeviceClass(device);
                if (device.getName() == null || device.getName().length() < 1 || device.getName().equals("null")) {
                    BluetoothManager BluetoothManager = (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
                    tmpDevName[0] = BluetoothManager.getAdapter().getRemoteDevice(device.getAddress()).getName();
                    //String url = "http://macvendors.co/api/vendorname/" + device.getAddress();
                }
                final String reqBody =
                        "?id=0&name=" + tmpDevName[0] +
                                "&address=" + utf_letter +
                                "&rssi=" + rssi +
                                "&longtime=" + System.currentTimeMillis() +
                                "&macaddress=" + device.getAddress() +
                                "&islowenergy=" + isLe +
                                "&source=" + "legacy_sonty_" + android_id_source_device +
                                "&long=" + CURRENT_LOCATION.getLongitude() +
                                "&lat=" + CURRENT_LOCATION.getLatitude() +
                                "&devclass=" + devclass;

                Runnable webReqRunnable_bl = (Runnable & Serializable) () -> {
                    RequestTaskListener requestTaskListener_bl = new RequestTaskListener() {
                        @Override
                        public void update(String string, String URL) {
                            if (string != null) {
                                analyticsTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Action")
                                        .setAction("Bluetooth Answer")
                                        .build());
                                if (string.contains("new_device")) {
                                    cnt_new++;
                                    cnt_new_bl++;
                                } else if (string.contains("rssi_updated")) {
                                    cnt_updated_str++;
                                } else if (string.contains("name_updated")) {
                                    cnt_nameUpdated++;
                                } else if (string.contains("regi_old")) {
                                    cnt_updated_time++;
                                    cnt_updated_time_bl++;
                                } else if (string.contains("not_recorded")) {
                                    cnt_notrecorded++;
                                    cnt_notrecorded_bl++;
                                } else {
                                    updateCurrent(ctx, "Bluetooth", "Unknown Answer #" + allCount);
                                    sendMessage_Telegram(android_id_source_device + " > Unknown answer -> " + string + "\n" + URL);
                                }
                            }
                        }
                    };
                    RequestTask_Bluetooth requestTask_bl = new RequestTask_Bluetooth();
                    requestTask_bl.addListener(requestTaskListener_bl);
                    requestTask_bl.execute(reqBody);
                };
                Future<?> future = BackgroundService.webRequestExecutor.submit(webReqRunnable_bl);
                webReqRunnablesList.add(webReqRunnable_bl);

                Runnable webReqRunnable_bl_indi = (Runnable & Serializable) () -> {
                    RequestTaskListener_indi requestTaskListener_bl_indi =
                            new RequestTaskListener_indi() {
                                @Override
                                public void update(String string, String URL) {
                                    if (string != null) {
                                        analyticsTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory("Action")
                                                .setAction("Bluetooth Answer")
                                                .build());
                                        if (string.contains("new_device")) {
                                            vibrate(ctx);
                                            Log.d("BL_TEST_", "NEW DEVICE FOUND: " + device.getName() + " -> " + device.getAddress());
                                            cnt_new++;
                                            cnt_new_bl++;
                                            updateCurrent(ctx, "Bluetooth", "New Found #" + allCount);
                                        } else if (string.contains("rssi_updated")) {
                                            //cnt_rssi_bl++;
                                            BackgroundService.cnt_updated_str++;
                                            updateCurrent(ctx, "Bluetooth", "Updated Strength #" + allCount);
                                        } else if (string.contains("name_updated")) {
                                            //Toast.makeText(getApplicationContext(),"Name updated!",Toast.LENGTH_LONG).show();
                                            BackgroundService.vibrate(ctx);
                                            Log.d("NameUpdateTest_", "Name updated!");
                                            cnt_nameUpdated++;
                                            updateCurrent(ctx, "Bluetooth", "Updated Name #" + allCount);
                                        } else if (string.contains("regi_old")) {
                                            vibrate(ctx);
                                            cnt_updated_time++;
                                            cnt_updated_time_bl++;
                                            updateCurrent(ctx, "Bluetooth", "Updated Time #" + allCount);
                                        } else if (string.contains("not_recorded")) {
                                            cnt_notrecorded++;
                                            cnt_notrecorded_bl++;
                                            updateCurrent(ctx, "Bluetooth", "Not Recorded #" + allCount);
                                        } else {
                                            Log.d("BL_TEST_", "Got string: " + string);
                                            updateCurrent(ctx, "Bluetooth", "Unknown Answer #" + allCount);
                                            //sendMessage_Telegram(android_id_source_device + " > Unknown answer -> " + string + "\n" + URL);
                                        }
                                    }
                                }
                            };
                    //RequestTask_Bluetooth_indi requestTask_bl_indi = new RequestTask_Bluetooth_indi();
                    //requestTask_bl_indi.addListener(requestTaskListener_bl_indi);
                    //requestTask_bl_indi.execute(reqBody);
                };
                Future<?> future_indi = BackgroundService.webRequestExecutor.submit(webReqRunnable_bl_indi);
                webReqRunnablesList.add(webReqRunnable_bl_indi);


                bluetoothDevicesFound.add(new
                        BluetoothDeviceWithLocation(device, CURRENT_LOCATION));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public static class OpenWifiManager {
        public static ArrayList<ScanResult> getStrongestOpenAp(List<ScanResult> scanResult) {
            Collections.sort(scanResult, new Comparator<android.net.wifi.ScanResult>() {
                @Override
                public int compare(android.net.wifi.ScanResult o1, android.net.wifi.ScanResult o2) {
                    return Integer.compare(o1.level, o2.level);
                }
            });
            // ScanResult are now ordered by DBM

            ArrayList<android.net.wifi.ScanResult> onlyOpenAps = new ArrayList<>();

            for (android.net.wifi.ScanResult sr : scanResult) {
                if (getScanResultSecurity(sr).equalsIgnoreCase("OPEN")) {
                    onlyOpenAps.add(sr);
                }
            }
            //android.net.wifi.ScanResult strongestOpenAp = onlyOpenAps.get(0);
            /*int i = 0;
            for(ScanResult sr : onlyOpenAps){
                Log.d("OPEN_AP_",""+sr.SSID + " -> " + sr.level);
                i++;
            }*/
            //Log.d("OPEN_AP_","First 0: " + onlyOpenAps.get(0).SSID + " -> " + onlyOpenAps.get(0).level);
            //if(onlyOpenAps.size() > 1)
            //Log.d("OPEN_AP_","LAST " + (onlyOpenAps.size()-1)+": " + onlyOpenAps.get(onlyOpenAps.size()-1).SSID + " -> " + onlyOpenAps.get(onlyOpenAps.size()-1).level);
            //return strongestOpenAp;
            return onlyOpenAps;
        }

        public static void enableApToConnect(android.net.wifi.ScanResult accp, boolean forceConnect) {
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + accp.SSID + "\"";
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiManager.addNetwork(conf);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            for (WifiConfiguration aP : list) {
                if (aP.SSID != null && aP.SSID.equals("\"" + accp.SSID + "\"")) {
                    if (!wifiInfo.isConnectedOrConnecting()) {
                        wifiManager.enableNetwork(aP.networkId, false);
                        if (forceConnect)
                            wifiManager.reconnect();
                    } else if (forceConnect) {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(aP.networkId, true);
                        if (forceConnect)
                            wifiManager.reconnect();
                    }
                    Log.d("OPEN_WIFI_", "ENABLED -> " + aP.SSID);
                }
            }
        }

        public static String connectStrongestOpenWifi(Context context, List<android.net.wifi.ScanResult> scanResult) {
            try {
                Collections.sort(scanResult, new Comparator<android.net.wifi.ScanResult>() {
                    @Override
                    public int compare(android.net.wifi.ScanResult o1, android.net.wifi.ScanResult o2) {
                        return Integer.compare(o1.level, o2.level);
                    }
                });
                // ScanResult are now ordered by DBM

                ArrayList<android.net.wifi.ScanResult> onlyOpenAps = new ArrayList<>();
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

                for (android.net.wifi.ScanResult sr : scanResult) {
                    if (getScanResultSecurity(sr).equalsIgnoreCase("OPEN")) {
                        onlyOpenAps.add(sr);
                    }
                }
                android.net.wifi.ScanResult strongestOpenAp = onlyOpenAps.get(0); // strongest

                List<String> banneds = new ArrayList<>();
                banneds.add("VENDEG");
                banneds.add("KMKK");
                banneds.add("helobelokivagy");
                if (onlyOpenAps.size() >= 1) {
                    for (android.net.wifi.ScanResult openApChosed : onlyOpenAps) {
                        String openApChosed_trimmed = openApChosed.SSID;
                        if (!banneds.contains(openApChosed_trimmed)) {
                            if (openApChosed.SSID.length() > 3) {
                                strongestOpenAp = openApChosed;
                                break;
                            }
                        } else {
                            onlyOpenAps.removeAll(banneds);
                        }
                    }
                }

                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = "\"" + strongestOpenAp.SSID + "\"";
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wifiManager.addNetwork(conf);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }
                List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                //NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                for (WifiConfiguration aP : list) {
                    if (aP.SSID != null && aP.SSID.equals("\"" + strongestOpenAp.SSID + "\"")) {
                        android.net.wifi.ScanResult finalStrongestOpenAp = strongestOpenAp;
                        if (!banneds.stream().anyMatch(str -> str.contains(finalStrongestOpenAp.SSID))) {
                            if (!wifiInfo.isConnectedOrConnecting()) {
                                wifiManager.enableNetwork(aP.networkId, false);
                            } else {
                                wifiManager.enableNetwork(aP.networkId, true);
                            }
                            Log.d("OPEN_WIFI_", "ENABLED -> " + aP.SSID);
                            //break;
                            return finalStrongestOpenAp.SSID;
                            //vibrate(context);
                            //Toast.makeText(context, "Enabled: " + aP.SSID, Toast.LENGTH_LONG).show();
                            ////wifiManager.disconnect();
                            ////wifiManager.reconnect();
                        } else {
                            return "";
                            //Log.d("OPEN_WIFI_","BANNED FOUND -> " + aP.SSID + " _ " + aP.BSSID + " _ " + aP.FQDN);
                        }
                    }
                }
            } catch (Exception e) {
                //Toast.makeText(context, "Error! No open WIFI around\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                //BackgroundService.updateCurrentError("OPEN WIFI ERROR", "No OPEN WiFi found", context);
                Log.d("OPEN_WIFI_", "ERROR " + e.getMessage());
                e.printStackTrace();
                return "";
            }
            return "";
        }
    }

    public static String getScanResultSecurity(android.net.wifi.ScanResult scanResult) {
        final String cap = scanResult.capabilities;
        final String[] securityModes = {"WEP", "PSK", "EAP", "WPA", "WPA2"};

        for (int i = securityModes.length - 1; i >= 0; i--) {
            if (cap.contains(securityModes[i])) {
                return securityModes[i];
            }
        }

        return "OPEN";
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

    /*
    public int getAvgSpeed() {
        long data = TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED ? TrafficStats.getTotalRxBytes() : TrafficStats.getUidRxBytes(uid);
        long traffic_data = data - last_data;
        long duration = System.currentTimeMillis() - last_time;
        return (int) (traffic_data * 1000 / (duration * 1024));
    }
    */
    public void showOngoing(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID_SERVICE = getPackageName();
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            i.setData(Uri.parse("package:" + getPackageName()));
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);

            Intent intent_exit = new Intent(getApplicationContext(),
                    notificationReceiver.class);
            intent_exit.setAction("exit");
            intent_exit.putExtra("requestCode", 159);
            PendingIntent pi_exit = PendingIntent.getBroadcast(getApplicationContext(),
                    159, intent_exit, PendingIntent.FLAG_IMMUTABLE);

            Random rnd = new Random();
            int color = Color.argb(0, rnd.nextInt(256 - 0), rnd.nextInt(256 - 0), rnd.nextInt(256 - 0));
            String globalMobileRx_app = "";
            String globalMobileTx_app = "";
            try {
                int uid = getPackageManager().getApplicationInfo(getPackageName(), 0).uid;
                globalMobileRx_app = roundBandwidth(TrafficStats.getUidRxBytes(uid));
                globalMobileTx_app = roundBandwidth(TrafficStats.getUidTxBytes(uid));
            } catch (Exception e) {
                e.printStackTrace();
            }
            nm.createNotificationChannel(new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID_SERVICE, "App Service",
                    NotificationManager.IMPORTANCE_MIN));
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getPackageName());
            Notification notification = notificationBuilder
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)
                    .setNumber(Live_Http_GET_SingleRecord.cnt_httpError)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(text)
                            .setBigContentTitle(text)
                            .setSummaryText(text))
                    .setSmallIcon(R.drawable.servicetransparenticon)
                    .setContentTitle("Temp: " + batteryTemperature(getApplicationContext()) +
                            "/" + cpuTemperature() +
                            "\nData Usage: " + globalMobileRx_app + " / " + globalMobileTx_app + "\n" +
                            roundBandwidth(Live_Http_GET_SingleRecord.bytesReceived) + " / " + roundBandwidth(Live_Http_GET_SingleRecord.bytesSent))
                    .setContentText(text)
                    .setSubText("" + System.currentTimeMillis() + " | " + getTimeAgo(System.currentTimeMillis()))
                    .setContentIntent(pi)
                    .setSound(null)
                    .setSilent(true)
                    .setVibrate(new long[]{0L})
                    .setColorized(true)
                    .setColor(color)
                    .setGroup("wifi")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .addAction(R.drawable.servicetransparenticon, "EXIT", pi_exit)
                    .build();
            startForeground(2, notification);
        } else {

            // app info sysactivity
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
                    .setContentTitle("SontyLegacy Service")
                    .setContentText(text)
                    .setColorized(true)
                    .setColor(color2)
                    .setSubText("BS showongoing id:2")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .setSummaryText("BS showongoing id:2")
                            .setBigContentTitle(text)
                    )
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
            startForeground(2, notification);
        }
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

    public void showOngoing2(Context ctx, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID_SERVICE = ctx.getPackageName();
            NotificationManager nm = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
            Random rnd = new Random();
            int color = Color.argb(0, rnd.nextInt(256 - 0), rnd.nextInt(256 - 0), rnd.nextInt(256 - 0));
            nm.createNotificationChannel(new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID_SERVICE, "App Service 2",
                    NotificationManager.IMPORTANCE_LOW));
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(ctx, ctx.getPackageName());
            Notification notification = notificationBuilder
                    //.setOngoing(true)
                    /*.setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(text)
                            .setBigContentTitle(text)
                            .setSummaryText(text))*/
                    //.setGroupSummary(true)
                    .setSmallIcon(R.drawable.gpssatellite)
                    .setNumber(Live_Http_GET_SingleRecord.cnt_httpError)
                    .setContentTitle(text)
                    .setContentText(text)
                    .setSubText("" + System.currentTimeMillis() + " | " + getTimeAgo(System.currentTimeMillis()))
                    .setSound(null)
                    .setVibrate(new long[]{0L})
                    .setColorized(true)
                    .setColor(color)
                    .setGroup("wifi")
                    //.setChannelId("wifi")
                    .setPriority(NotificationManager.IMPORTANCE_LOW)
                    //.setCategory(Notification.CATEGORY_EVENT)
                    .build();
            startForeground(3, notification);
        } else {

            // app info sysactivity
            Intent notificationIntent = new Intent(ctx, notificationReceiver.class);
            notificationIntent.putExtra("29294", "29294");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    ctx,
                    29294,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            //region NotificationBUTTONS
            Intent intent = new Intent(ctx, notificationReceiver.class);
            intent.setAction("exit");
            intent.putExtra("requestCode", 99);
            PendingIntent pi = PendingIntent.getBroadcast(ctx, 99, intent, PendingIntent.FLAG_IMMUTABLE);

            Intent intent_location_network = new Intent(ctx, notificationReceiver.class);
            intent_location_network.setAction("network");
            intent_location_network.putExtra("requestCode", 101);
            PendingIntent pi_location_network = PendingIntent.getBroadcast(ctx,
                    101, intent_location_network, PendingIntent.FLAG_IMMUTABLE);

            Intent intent_location_gps = new Intent(ctx, notificationReceiver.class);
            intent_location_gps.setAction("gps");
            intent_location_gps.putExtra("requestCode", 102);
            PendingIntent pi_location_gps = PendingIntent.getBroadcast(ctx,
                    102, intent_location_gps, PendingIntent.FLAG_IMMUTABLE);
            //endregion

            int color2 = Color.argb(255, 220, 237, 193);
            Notification notification = new NotificationCompat.Builder(ctx, "sontylegacy")
                    .setContentTitle("SontyLegacy Service")
                    .setContentText(text)
                    .setColorized(true)
                    .setColor(color2)
                    .setSubText("BS showongoing id:2")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .setSummaryText("BS showongoing id:2")
                            .setBigContentTitle(text)
                    )
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
            startForeground(2, notification);
        }
    }

    public static void updateCurrent(Context ctx, String title, String text) {
        //Context ctx = getApplicationContext();
        int color_drawable = R.drawable.servicetransparenticon;
        Intent notificationIntent = new Intent(ctx, notificationReceiver.class);
        notificationIntent.putExtra("29294", "29294");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,
                29294, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent intent_exit = new Intent(ctx, notificationReceiver.class);
        intent_exit.setAction("exit");
        intent_exit.putExtra("requestCode", 99);
        PendingIntent pi_exit = PendingIntent.getBroadcast(ctx,
                99, intent_exit, PendingIntent.FLAG_IMMUTABLE);

        Intent intent_location_network = new Intent(ctx, notificationReceiver.class);
        intent_location_network.setAction("network");
        intent_location_network.putExtra("requestCode", 101);
        PendingIntent pi_location_network = PendingIntent.getBroadcast(ctx,
                101, intent_location_network, PendingIntent.FLAG_IMMUTABLE);

        Intent intent_location_gps = new Intent(ctx, notificationReceiver.class);
        intent_location_gps.setAction("gps");
        intent_location_gps.putExtra("requestCode", 102);
        PendingIntent pi_location_gps = PendingIntent.getBroadcast(ctx,
                102, intent_location_gps, PendingIntent.FLAG_IMMUTABLE);


        Random rnd = new Random();
        int color = Color.argb(0, rnd.nextInt(256 - 0), rnd.nextInt(256 - 0), rnd.nextInt(256 - 0));

        Notification notification = new NotificationCompat.Builder(ctx, "sontylegacy")
                .setContentTitle(title/* + " | Started: " + started_time*/)
                .setContentText(text)
                .setSound(null)
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .setLights(color, 300, 300)
                .setContentInfo("CONTENT-INFO-2")
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setColorized(true)
                .setColor(color)
                /* Known Direct Subclasses
                    NotificationCompat.BigPictureStyle,
                    NotificationCompat.BigTextStyle,
                    NotificationCompat.DecoratedCustomViewStyle,
                    NotificationCompat.InboxStyle,
                    NotificationCompat.MediaStyle,
                    NotificationCompat.MessagingStyle
                */
                .setSubText("BS updatecurrent id:59")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setSummaryText("BS updatecurrent id:59")
                        .setBigContentTitle(text)
                )
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSmallIcon(color_drawable)
                .setContentIntent(pendingIntent)
                .setChannelId("sontylegacy")
                .addAction(R.drawable.servicetransparenticon, "NET", pi_location_network)
                .addAction(R.drawable.servicetransparenticon, "GPS", pi_location_gps)
                .addAction(R.drawable.servicetransparenticon, "EXIT", pi_exit)
                .build();

        NotificationManager nm = ctx.getSystemService(NotificationManager.class);
        nm.notify(59, notification);
    }

    public static void updateCurrent_secondary(Context c, String title, String text, int color_drawable) {
        Intent intent = new Intent(c, notificationReceiver.class);
        intent.setAction("test");
        intent.putExtra("requestCode", 999);
        PendingIntent pi = PendingIntent.getBroadcast(c, 999, intent, PendingIntent.FLAG_IMMUTABLE);

        Random rnd = new Random();
        int color = Color.argb(0, rnd.nextInt(256 - 0), rnd.nextInt(256 - 0), rnd.nextInt(256 - 0));

        Notification notification = new NotificationCompat.Builder(c, "sontylegacy")
                .setContentTitle(title)
                .setContentText(text)
                .setSound(null)
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .setLights(color, 300, 300)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setColorized(true)
                .setColor(color)
                //.setGroupSummary(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setSummaryText("secondary id:60")
                        .setBigContentTitle(text)
                )
                .setSmallIcon(color_drawable)
                .setOngoing(false)
                .setGroup("wifi")
                .setSubText("BS secondary id:60")
                .setChannelId("sontylegacy")
                .build();
        NotificationManager nm = c.getSystemService(NotificationManager.class);
        nm.notify(60, notification);
    }

    public static void updateCurrent_exception(Context c, String title, String text,
                                               int color_drawable) {
        Random rnd = new Random();
        int color = Color.argb(0, rnd.nextInt(256 - 0), rnd.nextInt(256 - 0), rnd.nextInt(256 - 0));
        Notification notification = new NotificationCompat.Builder(c, "sontylegacy")
                .setContentTitle(title)
                .setContentText(text)
                .setSound(null)
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .setLights(color, 300, 300)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setColorized(true)
                .setColor(color)
                .setVibrate(null)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setSummaryText("summary")
                        .setBigContentTitle(text)
                )
                .setSmallIcon(color_drawable)
                .setOngoing(false)
                .setGroup("wifi")
                .setSubText("BS excep id:59")
                .setChannelId("sontylegacy")
                .build();
        NotificationManager nm = c.getSystemService(NotificationManager.class);
        nm.notify(59, notification);
    }


    public static void createNotifGroup(Context ctx, String id, String name) {
        NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);

        NotificationChannelGroup notificationChannelGroup;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannelGroup = new NotificationChannelGroup(id, name);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                notificationChannelGroup.setDescription("SONTY LEGACY NOTIF CHANNEL");
            }
            notificationManager.createNotificationChannelGroup(notificationChannelGroup);
        }
    }

    public static String createNotificationChannel(Context ctx, String channelId, String
            channelName) {
        NotificationChannel chan = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.GREEN);
            chan.setSound(null, null);
            chan.enableLights(true);
            chan.enableVibration(false);
            chan.setImportance(NotificationManager.IMPORTANCE_LOW);

            chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(chan);
        }
        return channelId;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        sendMessage_Telegram("onAccessibilityEvent: " +
                android_id_source_device + " > " +
                event.getPackageName());
    }

    @Override
    public void onInterrupt() {
        sendMessage_Telegram("onInterrupt [accessibility]");
    }

    /*@Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }*/

    @Override
    public void onStart(Intent intent, int startid) {
        Intent intents = new Intent(getBaseContext(), MainActivity.class);
        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intents);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Intent serviceIntent = new Intent(context, BackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
        Log.d("ALARM_", "RAN!");
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, Alarm.class);
        intent.putExtra("requestCode", 66);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 66, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                5 * 60 * 1000, pendingIntent);

        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        ActivityManager actManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totalMemory = memInfo.totalMem;
        long availableMemory = memInfo.availMem;
        double percentAvailable = round(memInfo.availMem / (double) memInfo.totalMem * 100.0, 2);
        String memoryString = roundBandwidth(totalMemory) + " " + roundBandwidth(availableMemory) + " " + percentAvailable + "%";
        sendMessage_Telegram("Low memory! " + android_id_source_device + " | " + memoryString);
        super.onLowMemory();
    }

    public String getMemoryInfoString() {
        ActivityManager actManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totalMemory = memInfo.totalMem;
        long availableMemory = memInfo.availMem;
        double percentAvailable = round(memInfo.availMem / (double) memInfo.totalMem * 100.0, 2);

        long maxMem = Runtime.getRuntime().maxMemory();
        long totalMem = Runtime.getRuntime().totalMemory();
        long freeMem = Runtime.getRuntime().freeMemory();

        long totalFreeMem = maxMem - totalMem + freeMem;
        String memoryString1 = "Total: " + roundBandwidth(totalMemory) +
                "\nAvailable: " + roundBandwidth(availableMemory) +
                "\n" + percentAvailable + "%25";
        /*String memoryString2 = "Total: " + roundBandwidth(totalMem) +
                "\nMax: " + roundBandwidth(maxMem) +
                "\nFree: " + roundBandwidth(freeMem) +
                "\nTotalFree: " + roundBandwidth(totalFreeMem);*/
        sendMessage_Telegram(memoryString1);
        //sendMessage_Telegram(memoryString2); // inaccurate
        return memoryString1;
    }

    @Override
    public void onTrimMemory(int level) {
        String callback;
        ActivityManager actManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totalMemory = memInfo.totalMem;
        long availableMemory = memInfo.availMem;
        double percentAvailable = round(memInfo.availMem / (double) memInfo.totalMem * 100.0, 2);
        String memoryString = roundBandwidth(totalMemory) + " " + roundBandwidth(availableMemory) + " " + percentAvailable + "%";
        if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            callback = "TRIM_MEMORY_BACKGROUND";
        } else if (level >= ComponentCallbacks2.TRIM_MEMORY_COMPLETE) {
            callback = "TRIM_MEMORY_COMPLETE";
        } else if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            callback = "TRIM_MEMORY_MODERATE";
        } else if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL) {
            callback = "TRIM_MEMORY_RUNNING_CRITICAL";
        } else if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            callback = "TRIM_MEMORY_RUNNING_LOW";
        } else if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE) {
            callback = "TRIM_MEMORY_RUNNING_MODERATE";
        } else if (level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            callback = "TRIM_MEMORY_UI_HIDDEN";
        } else {
            callback = "OTHER_" + level;
        }

        //sendMessage_Telegram(android_id_source_device + " | " + "onTrimMemory() > " + callback + " | " + memoryString);
        super.onTrimMemory(level);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        restartService();
        super.onTaskRemoved(rootIntent);
    }

    public static String getStringbetweenStrings(String gotString,
                                                 String whatStringStart, String whatStringEnd) {
        String result = "";
        try {
            result =
                    gotString.substring(
                            gotString.indexOf(whatStringStart) + whatStringStart.length()
                    );
            result =
                    result.substring(
                            0,
                            result.indexOf(whatStringEnd));
        } catch (Exception e) {
            Log.d("PARSE_ERROR_", gotString);
            e.printStackTrace();
        }
        return result;
    }

    public static void sendMessage(String message) {
        int retryCount = 0;
        try {
            //message.replaceAll("\\r?\\n","%0A");
            //message.replaceAll("\\r?\\n","<br>");
            String url = "https://api.telegram.org/bot990712757:AAGyuPqZJUNoRAi1DMl-oRzEYInZz7UP0C4/sendMessage?chat_id=1093250115&text=" +
                    message + "&parse_mode=html";
            AndroidNetworking.get(url)
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {

                        }

                        @Override
                        public void onError(ANError error) {
                            //error.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            retryCount++;
            if (retryCount < 100)
                sendMessage_Telegram(message + "_" + retryCount);
            e.printStackTrace();
        }
    }

    public static void sendMessage_Telegram(String message) {
        //message.replaceAll("\\r?\\n","<br>");
        // 1093250115
        String url = "https://api.telegram.org/bot990712757:AAGyuPqZJUNoRAi1DMl-oRzEYInZz7UP0C4/sendMessage?chat_id=1093250115&text=" +
                message + "&parse_mode=html";
        AsyncHttpClient client = new AsyncHttpClient();
        try {
            client.get(url,
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.d("ERROR_", "error " + message);
                        }
                    });
        } catch (Exception e) {
            Log.d("ERROR_", "error " + e.getMessage());
            sendMessage(message);
        }

    }

    public void restartService() {
        /*int delay = 1000;
        Log.e("", "restarting app");
        Intent restartIntent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName() );
        PendingIntent intent = PendingIntent.getActivity(
                context, 0,
                restartIntent, Intent.FLAG_ACTIVITY_CLEAR_TOP);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + delay, intent);
        System.exit(2);
        */
        // works but finding a new way to restart to make it more realible
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.putExtra("requestCode", 66);
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 66, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);
    }

    public static int convertDBM(int dbm) {
        int quality;
        if (dbm <= -100)
            quality = 0;
        else if (dbm >= -50)
            quality = 100;
        else
            quality = 2 * (dbm + 100);
        //return quality;
        return Math.abs(dbm);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String convertEncryption(ScanResult result) {
        String enc;
        if (!result.capabilities.contains("WEP") && !result.capabilities.contains("WPA") && !(result.capabilities.contains("WPA2"))) {
            enc = "NONE";
        } else if (result.capabilities.contains("WEP")) {
            enc = "WEP";
        } else if (result.capabilities.contains("WPA2")) {
            enc = "WPA2";
        } else if (result.capabilities.contains("WPA")) {
            enc = "WPA";
        } else {
            enc = "Not obtainable";
        }
        return enc;
    }

    public static String locationToStringAddress(Context ctx, Location location) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("");
                }
                strAdd = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            locc++;
            Log.d("LOCATION CONVERSION Error_", e.toString());
            //e.printStackTrace();
            //return "Unknown";
            if (locc <= 3) {
                return locationToStringAddress(ctx, location);
            } else {
                locc = 0;
                return "Unknown";
            }
        }
        locc = 0;
        return strAdd;
    }

    //region HTTP Classes
    public static class Live_Http_GET_SingleRecord implements Serializable {
        public static long bytesSent;
        public static long bytesReceived;
        public static String lastHandledURL;
        public static String lastHttpResponseBody;
        public static int cnt_httpError;
        public static String UNIQUE_ID = BackgroundService.randomString.nextString();

        public static String executeRequest(final String host, final int port, final String URL,
                                            final boolean METHOD_POST,
                                            final String postData) {

            ToneGenerator toneGen1 = new ToneGenerator(
                    AudioManager.STREAM_MUSIC, 100);
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 20);

            requestUniqueIDList.add(UNIQUE_ID);
            lastHandledURL = host + ":" + port + URL;
            Log.d("HTTP_REQ_SENT", "URL > " + lastHandledURL);
            HttpCustomFormat target = new HttpCustomFormat(host, port, URL, METHOD_POST, postData);
            BackgroundService.httpRedundantList.add(target);
            Socket socket;
            try {
                boolean USE_PROXY = false;
                if (USE_PROXY) {
                    // EXPERIMENTAL
                    SocketAddress sa = InetSocketAddress.createUnresolved("172.245.185.119", 1080);
                    Proxy proxy = new Proxy(Proxy.Type.SOCKS, sa);
                    socket = new Socket(proxy);
                    System.getProperties().put("proxySet", "true");
                    System.getProperties().put("socksProxyHost", "172.245.185.119");
                    System.getProperties().put("socksProxyPort", "1080");
                } else {
                    socket = new Socket(InetAddress.getByName(host), port);
                }
                socket.setSoTimeout(3000);
                socket.setTcpNoDelay(true);
                String header = createGetHeader(
                        host,
                        "close",
                        "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:99.0) Gecko/20200101 Firefox/99.0"
                );
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                if (METHOD_POST == true) {
                    String postCommand = "POST " + URL + " HTTP/1.0\r\n" +
                            "Host: sont.sytes.net\r\n" +
                            "Content-Length: " + postData.length() + "\r\n" +
                            "Content-Type: application/x-www-form-urlencoded\r\n";
                    pw.println(postCommand);
                    pw.println("");
                    pw.println(postData);
                    pw.println();
                    bytesSent += postData.length() + postCommand.length();
                } else {
                    String getCommand = "GET " + URL + " HTTP/1.0";
                    pw.println((getCommand));
                    pw.println(header);
                    pw.println();
                    bytesSent += getCommand.length() + header.length();
                }
                pw.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String full_str = "";
                String str = "";

                try {
                    while ((str = br.readLine()) != null) {
                        full_str += str;
                    }
                } catch (Exception eerr) {
                    eerr.printStackTrace();
                }
                lastHttpResponseBody = full_str;
                bytesReceived += full_str.length();
                requestUniqueIDList.remove(UNIQUE_ID);
                try {
                    //BackgroundService.addInfo("HTTP SUCCESS", target.toString());
                } catch (Exception er) {
                    er.printStackTrace();
                }
                return full_str;
            } catch (Exception e) {
                HttpCustomFormat target2 = new HttpCustomFormat(host, port, URL, METHOD_POST, postData);
                BackgroundService.httpRedundantList.add(target2);
                try {
                    BackgroundService.addInfo("HTTP_ERROR", target.toString());
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                requestUniqueIDList_error.add(UNIQUE_ID);
                cnt_httpError++;
                Runnable retryRunnable = (Runnable & Serializable) () -> {
                    executeRequest(host, port, URL, METHOD_POST, postData);
                };
                webRequestExecutor.submit(retryRunnable);
                e.printStackTrace();
                //BackgroundService.httpErrorList.add(this);
                Log.d("Live_Http_GET_SingleRecord", "network error | " + e.getMessage());
                return null;
            }
        }

        public static String createGetHeader(String host, String connection, String userAgent) {
            /* Connection: Keep-Alive / close */
            return "Host: " + host + "\n" +
                    "Connection: " + connection + "\n" +
                    "User-Agent: " + userAgent + "\n\n";

        }
    }

    interface RequestTaskListener {
        void update(String string, String URL);
    }

    interface RequestTaskListener_indi {
        void update(String string, String URL);
    }

    public static class RequestTask
            extends AsyncTask<String, String, String> {
        private final List<RequestTaskListener> listeners = new ArrayList<RequestTaskListener>();

        public void addListener(RequestTaskListener toAdd) {
            listeners.add(toAdd);
        }

        public int count = 0;
        public String URL;

        public RequestTask() {
            count++;
        }

        @Override
        protected String doInBackground(String... uri) {
            URL = "/wifi/register.php" + Arrays.toString(uri);
            return Live_Http_GET_SingleRecord.executeRequest(
                    "sont.sytes.net",
                    80,
                    "/wifi/register.php" + uri[0],
                    false, ""
            );
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            BackgroundService.updateCurrent(context, "HTTP", Live_Http_GET_SingleRecord.lastHandledURL);
            try {
                for (RequestTaskListener hl : listeners) {
                    hl.update(result, URL);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class RequestTask_Bluetooth
            extends AsyncTask<String, String, String> {
        private final List<RequestTaskListener> listeners = new ArrayList<RequestTaskListener>();

        public void addListener(RequestTaskListener toAdd) {
            listeners.add(toAdd);
        }

        public int count = 0;
        public String URL;

        public RequestTask_Bluetooth() {
            count++;
        }

        @Override
        protected String doInBackground(String... uri) {
            URL = "/wifi/register_bl.php" + Arrays.toString(uri);
            return Live_Http_GET_SingleRecord.executeRequest(
                    "sont.sytes.net",
                    80,
                    "/wifi/register_bl.php" + uri[0],
                    false, ""
            );
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            for (RequestTaskListener hl : listeners) {
                hl.update(result, URL);
            }
        }
    }

    public static class RequestTask_Bluetooth_indi
            extends AsyncTask<String, String, String> {
        private final List<RequestTaskListener_indi> listeners = new ArrayList<RequestTaskListener_indi>();

        public void addListener(RequestTaskListener_indi toAdd) {
            listeners.add(toAdd);
        }

        public int count = 0;
        public String URL;

        public RequestTask_Bluetooth_indi() {
            count++;
        }

        @Override
        protected String doInBackground(String... uri) {
            URL = "/wifi/indi_bl.php" + Arrays.toString(uri);
            return Live_Http_GET_SingleRecord.executeRequest(
                    "sont.sytes.net",
                    80,
                    "/wifi/indi_bl.php" + uri[0],
                    false, ""
            );
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            for (RequestTaskListener_indi hl : listeners) {
                hl.update(result, URL);
            }
        }
    }

    //endregion

    public static class TimeElapsedUtil {


        public long startTime;
        public long now;

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public TimeElapsedUtil() {
            this.startTime = System.currentTimeMillis();
            this.now = System.currentTimeMillis();
        }

        public TimeElapsedUtil(long startTime) {
            this.startTime = startTime;
            this.now = System.currentTimeMillis();
        }

        public TimeElapsedUtil(long startTime, long now) {
            this.startTime = startTime;
            this.now = now;
        }

        public String getElapsed() {
            this.now = System.currentTimeMillis();
            return convertLongToHRString(this.now - this.startTime);
        }

        private String convertLongToHRString(long val) {
            Date date = new Date(val);
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            return formatter.format(date);
        }
    }

    public static class AdminTOOLS {
        public static boolean checkIfDeviceIsEmulator(Context context) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String networkOperator = tm.getNetworkOperatorName();
            return Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.startsWith("unknown")
                    || Build.MODEL.contains("google_sdk")
                    || Build.MODEL.contains("Emulator")
                    || Build.MODEL.contains("Android SDK built for x86")
                    || Build.BOARD == "QC_Reference_Phone" //bluestacks
                    || Build.MANUFACTURER.contains("Genymotion")
                    || Build.HOST.startsWith("Build") //MSI App Player
                    || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                    || Build.PRODUCT.contains("sdk_google")
                    || Build.PRODUCT.contains("google_sdk")
                    || Build.PRODUCT.contains("sdk")
                    || Build.PRODUCT.contains("sdk_x86")
                    || Build.PRODUCT.contains("vbox86p")
                    || Build.PRODUCT.contains("emulator")
                    || Build.PRODUCT.contains("simulator")
                    || Build.HARDWARE.equals("goldfish")
                    || Build.HARDWARE.contains("ranchu")
                    || "Android".equals(networkOperator);
        }
    }

    public static class RootUtil {
        public static boolean isDeviceRooted() {
            return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
        }

        private static boolean checkRootMethod1() {
            String buildTags = android.os.Build.TAGS;
            return buildTags != null && buildTags.contains("test-keys");
        }

        private static boolean checkRootMethod2() {
            String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                    "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
            for (String path : paths) {
                if (new File(path).exists()) return true;
            }
            return false;
        }

        private static boolean checkRootMethod3() {
            Process process = null;
            try {
                process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                return in.readLine() != null;
            } catch (Throwable t) {
                return false;
            } finally {
                if (process != null) process.destroy();
            }
        }
    }

    public static boolean writeExternalPublic(Context context, String filename, String content, boolean append) {
        File file;
        File path;
        try {
            if ("huawei".equalsIgnoreCase(Build.MANUFACTURER)) {
                file = new File("/storage/emulated/0/Android/data/" + context.getPackageName() + "/files/", filename);
            } else {
                path = context.getExternalFilesDir(null);
                file = new File(path, filename);
            }
            //Log.d("FILETEST_", file.getAbsolutePath() + " _ " + file.length());
            FileOutputStream stream = new FileOutputStream(file, append);
            try {
                stream.write(content.getBytes());
            } finally {
                stream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String readExternalPublic(Context context, String filename) {
        File file;
        File path;
        try {
            if ("huawei".equalsIgnoreCase(Build.MANUFACTURER)) {
                file = new File("/data/user/0/" + context.getPackageName() + "/files/", filename);
            } else {
                path = context.getExternalFilesDir(null);
                file = new File(path, filename);
            }
            return Files.toString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            //writeExternalPublic(context, filename, "", true);
            e.printStackTrace();
            return null;
        }
    }

    public static void vibrate(final Context ctx) {
        if (!android_id_source_device.contains("SMA530F") ||
                !android_id_source_device.contains("ANYA")) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(50, -1));
                    } else {
                        v.vibrate(50);
                    }
                }
            };
            thread.start();
        }
    }


    // The following data type values are assigned by Bluetooth SIG.
    // For more details refer to Bluetooth 4.0 specification, Volume 3, Part C, Section 18.
    private static final int DATA_TYPE_FLAGS = 0x01;
    private static final int DATA_TYPE_SERVICE_UUIDS_16_BIT_PARTIAL = 0x02;
    private static final int DATA_TYPE_SERVICE_UUIDS_16_BIT_COMPLETE = 0x03;
    private static final int DATA_TYPE_SERVICE_UUIDS_32_BIT_PARTIAL = 0x04;
    private static final int DATA_TYPE_SERVICE_UUIDS_32_BIT_COMPLETE = 0x05;
    private static final int DATA_TYPE_SERVICE_UUIDS_128_BIT_PARTIAL = 0x06;
    private static final int DATA_TYPE_SERVICE_UUIDS_128_BIT_COMPLETE = 0x07;
    private static final int DATA_TYPE_LOCAL_NAME_SHORT = 0x08;
    private static final int DATA_TYPE_LOCAL_NAME_COMPLETE = 0x09;
    private static final int DATA_TYPE_TX_POWER_LEVEL = 0x0A;
    private static final int DATA_TYPE_SERVICE_DATA = 0x16;
    private static final int DATA_TYPE_MANUFACTURER_SPECIFIC_DATA = 0xFF;

    public static final int PARSED_SCAN_RECORD = 2;
    public static final int SCAN_RESPONSE_DATA = 1;
    public static final int ADVERTISING_DATA = 0;

    public ScanRecord parseFromScanRecord(byte[] scanRecord) {
        if (scanRecord == null) {
            return null;
        }
        int currentPos = 0;
        int advertiseFlag = -1;
        List<ParcelUuid> serviceUuids = new ArrayList<ParcelUuid>();
        String localName = null;
        int txPowerLevel = Integer.MIN_VALUE;
        ParcelUuid serviceDataUuid = null;
        byte[] serviceData = null;
        int manufacturerId = -1;
        byte[] manufacturerSpecificData = null;
        try {
            while (currentPos < scanRecord.length) {
                // length is unsigned int.
                int length = scanRecord[currentPos++] & 0xFF;
                if (length == 0) {
                    break;
                }
                // Note the length includes the length of the field type itself.
                int dataLength = length - 1;
                // fieldType is unsigned int.
                int fieldType = scanRecord[currentPos++] & 0xFF;
                switch (fieldType) {
                    case DATA_TYPE_FLAGS:
                        advertiseFlag = scanRecord[currentPos] & 0xFF;
                        break;
                    case DATA_TYPE_SERVICE_UUIDS_16_BIT_PARTIAL:
                    case DATA_TYPE_SERVICE_UUIDS_16_BIT_COMPLETE:
                        parseServiceUuid(scanRecord, currentPos,
                                dataLength, BluetoothUuid.UUID_BYTES_16_BIT, serviceUuids);
                        break;
                    case DATA_TYPE_SERVICE_UUIDS_32_BIT_PARTIAL:
                    case DATA_TYPE_SERVICE_UUIDS_32_BIT_COMPLETE:
                        parseServiceUuid(scanRecord, currentPos, dataLength,
                                BluetoothUuid.UUID_BYTES_32_BIT, serviceUuids);
                        break;
                    case DATA_TYPE_SERVICE_UUIDS_128_BIT_PARTIAL:
                    case DATA_TYPE_SERVICE_UUIDS_128_BIT_COMPLETE:
                        parseServiceUuid(scanRecord, currentPos, dataLength,
                                BluetoothUuid.UUID_BYTES_128_BIT, serviceUuids);
                        break;
                    case DATA_TYPE_LOCAL_NAME_SHORT:
                    case DATA_TYPE_LOCAL_NAME_COMPLETE:
                        localName = new String(
                                extractBytes(scanRecord, currentPos, dataLength));
                        break;
                    case DATA_TYPE_TX_POWER_LEVEL:
                        txPowerLevel = scanRecord[currentPos];
                        break;
                    case DATA_TYPE_SERVICE_DATA:
                        serviceData = extractBytes(scanRecord, currentPos, dataLength);
                        // The first two bytes of the service data are service data uuid.
                        int serviceUuidLength = BluetoothUuid.UUID_BYTES_16_BIT;
                        byte[] serviceDataUuidBytes = extractBytes(scanRecord, currentPos,
                                serviceUuidLength);
                        serviceDataUuid = BluetoothUuid.parseUuidFrom(serviceDataUuidBytes);
                        break;
                    case DATA_TYPE_MANUFACTURER_SPECIFIC_DATA:
                        manufacturerSpecificData = extractBytes(scanRecord, currentPos,
                                dataLength);
                        // The first two bytes of the manufacturer specific data are
                        // manufacturer ids in little endian.
                        manufacturerId = ((manufacturerSpecificData[1] & 0xFF) << 8) +
                                (manufacturerSpecificData[0] & 0xFF);
                        break;
                    default:
                        // Just ignore, we don't handle such data type.
                        break;
                }
                currentPos += dataLength;
            }
            if (serviceUuids.isEmpty()) {
                serviceUuids = null;
            }
            Constructor<ScanRecord> constructor = ScanRecord.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            ScanRecord scanRecordd = constructor.newInstance(PARSED_SCAN_RECORD,
                    serviceUuids, serviceDataUuid, serviceData,
                    manufacturerId, manufacturerSpecificData, advertiseFlag, txPowerLevel,
                    localName);
            return scanRecordd;
            /*return new ScanRecord(PARSED_SCAN_RECORD,
                    serviceUuids, serviceDataUuid, serviceData,
                    manufacturerId, manufacturerSpecificData, advertiseFlag, txPowerLevel,
                    localName);*/
        } catch (IndexOutOfBoundsException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            Log.e("PARSER_TAG",
                    "unable to parse scan record: " + Arrays.toString(scanRecord));

            return null;
        }
    }

    private int parseServiceUuid(byte[] scanRecord, int currentPos, int dataLength,
                                 int uuidLength, List<ParcelUuid> serviceUuids) {
        while (dataLength > 0) {
            byte[] uuidBytes = extractBytes(scanRecord, currentPos,
                    uuidLength);
            serviceUuids.add(BluetoothUuid.parseUuidFrom(uuidBytes));
            dataLength -= uuidLength;
            currentPos += uuidLength;
        }
        return currentPos;
    }

    private static byte[] extractBytes(byte[] scanRecord, int start, int length) {
        byte[] bytes = new byte[length];
        System.arraycopy(scanRecord, start, bytes, 0, length);
        return bytes;
    }

}

class BluetoothDeviceWithLocation {
    public BluetoothDevice bluetoothDevice;
    public Location location;

    public BluetoothDeviceWithLocation(BluetoothDevice bluetoothDevice, Location location) {
        this.bluetoothDevice = bluetoothDevice;
        this.location = location;
    }

    @Override
    public boolean equals(Object obj) {
        // confused ???
        return bluetoothDevice.getAddress().equalsIgnoreCase(((BluetoothDevice) obj).getAddress());
    }
}

class BluetoothUuid {
    /* See Bluetooth Assigned Numbers document - SDP section, to get the values of UUIDs
     * for the various services.
     *
     * The following 128 bit values are calculated as:
     *  uuid * 2^96 + BASE_UUID
     */
    public static final ParcelUuid AudioSink =
            ParcelUuid.fromString("0000110B-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid AudioSource =
            ParcelUuid.fromString("0000110A-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid AdvAudioDist =
            ParcelUuid.fromString("0000110D-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid HSP =
            ParcelUuid.fromString("00001108-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid HSP_AG =
            ParcelUuid.fromString("00001112-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid Handsfree =
            ParcelUuid.fromString("0000111E-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid Handsfree_AG =
            ParcelUuid.fromString("0000111F-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid AvrcpController =
            ParcelUuid.fromString("0000110E-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid AvrcpTarget =
            ParcelUuid.fromString("0000110C-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid ObexObjectPush =
            ParcelUuid.fromString("00001105-0000-1000-8000-00805f9b34fb");
    public static final ParcelUuid Hid =
            ParcelUuid.fromString("00001124-0000-1000-8000-00805f9b34fb");
    public static final ParcelUuid Hogp =
            ParcelUuid.fromString("00001812-0000-1000-8000-00805f9b34fb");
    public static final ParcelUuid PANU =
            ParcelUuid.fromString("00001115-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid NAP =
            ParcelUuid.fromString("00001116-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid BNEP =
            ParcelUuid.fromString("0000000f-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid PBAP_PSE =
            ParcelUuid.fromString("0000112f-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid MAP =
            ParcelUuid.fromString("00001134-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid MNS =
            ParcelUuid.fromString("00001133-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid MAS =
            ParcelUuid.fromString("00001132-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid BASE_UUID =
            ParcelUuid.fromString("00000000-0000-1000-8000-00805F9B34FB");
    /**
     * Length of bytes for 16 bit UUID
     */
    public static final int UUID_BYTES_16_BIT = 2;
    /**
     * Length of bytes for 32 bit UUID
     */
    public static final int UUID_BYTES_32_BIT = 4;
    /**
     * Length of bytes for 128 bit UUID
     */
    public static final int UUID_BYTES_128_BIT = 16;
    public static final ParcelUuid[] RESERVED_UUIDS = {
            AudioSink, AudioSource, AdvAudioDist, HSP, Handsfree, AvrcpController, AvrcpTarget,
            ObexObjectPush, PANU, NAP, MAP, MNS, MAS};

    public static boolean isAudioSource(ParcelUuid uuid) {
        return uuid.equals(AudioSource);
    }

    public static boolean isAudioSink(ParcelUuid uuid) {
        return uuid.equals(AudioSink);
    }

    public static boolean isAdvAudioDist(ParcelUuid uuid) {
        return uuid.equals(AdvAudioDist);
    }

    public static boolean isHandsfree(ParcelUuid uuid) {
        return uuid.equals(Handsfree);
    }

    public static boolean isHeadset(ParcelUuid uuid) {
        return uuid.equals(HSP);
    }

    public static boolean isAvrcpController(ParcelUuid uuid) {
        return uuid.equals(AvrcpController);
    }

    public static boolean isAvrcpTarget(ParcelUuid uuid) {
        return uuid.equals(AvrcpTarget);
    }

    public static boolean isInputDevice(ParcelUuid uuid) {
        return uuid.equals(Hid);
    }

    public static boolean isPanu(ParcelUuid uuid) {
        return uuid.equals(PANU);
    }

    public static boolean isNap(ParcelUuid uuid) {
        return uuid.equals(NAP);
    }

    public static boolean isBnep(ParcelUuid uuid) {
        return uuid.equals(BNEP);
    }

    public static boolean isMap(ParcelUuid uuid) {
        return uuid.equals(MAP);
    }

    public static boolean isMns(ParcelUuid uuid) {
        return uuid.equals(MNS);
    }

    public static boolean isMas(ParcelUuid uuid) {
        return uuid.equals(MAS);
    }

    /**
     * Returns true if ParcelUuid is present in uuidArray
     *
     * @param uuidArray - Array of ParcelUuids
     * @param uuid
     */
    public static boolean isUuidPresent(ParcelUuid[] uuidArray, ParcelUuid uuid) {
        if ((uuidArray == null || uuidArray.length == 0) && uuid == null)
            return true;
        if (uuidArray == null)
            return false;
        for (ParcelUuid element : uuidArray) {
            if (element.equals(uuid)) return true;
        }
        return false;
    }

    /**
     * Returns true if there any common ParcelUuids in uuidA and uuidB.
     *
     * @param uuidA - List of ParcelUuids
     * @param uuidB - List of ParcelUuids
     */
    public static boolean containsAnyUuid(ParcelUuid[] uuidA, ParcelUuid[] uuidB) {
        if (uuidA == null && uuidB == null) return true;
        if (uuidA == null) {
            return uuidB.length == 0 ? true : false;
        }
        if (uuidB == null) {
            return uuidA.length == 0 ? true : false;
        }
        HashSet<ParcelUuid> uuidSet = new HashSet<ParcelUuid>(Arrays.asList(uuidA));
        for (ParcelUuid uuid : uuidB) {
            if (uuidSet.contains(uuid)) return true;
        }
        return false;
    }

    /**
     * Returns true if all the ParcelUuids in ParcelUuidB are present in
     * ParcelUuidA
     *
     * @param uuidA - Array of ParcelUuidsA
     * @param uuidB - Array of ParcelUuidsB
     */
    public static boolean containsAllUuids(ParcelUuid[] uuidA, ParcelUuid[] uuidB) {
        if (uuidA == null && uuidB == null) return true;
        if (uuidA == null) {
            return uuidB.length == 0 ? true : false;
        }
        if (uuidB == null) return true;
        HashSet<ParcelUuid> uuidSet = new HashSet<ParcelUuid>(Arrays.asList(uuidA));
        for (ParcelUuid uuid : uuidB) {
            if (!uuidSet.contains(uuid)) return false;
        }
        return true;
    }

    /**
     * Extract the Service Identifier or the actual uuid from the Parcel Uuid.
     * For example, if 0000110B-0000-1000-8000-00805F9B34FB is the parcel Uuid,
     * this function will return 110B
     *
     * @param parcelUuid
     * @return the service identifier.
     */
    public static int getServiceIdentifierFromParcelUuid(ParcelUuid parcelUuid) {
        UUID uuid = parcelUuid.getUuid();
        long value = (uuid.getMostSignificantBits() & 0x0000FFFF00000000L) >>> 32;
        return (int) value;
    }

    /**
     * Parse UUID from bytes. The {@code uuidBytes} can represent a 16-bit, 32-bit or 128-bit UUID,
     * but the returned UUID is always in 128-bit format.
     * Note UUID is little endian in Bluetooth.
     *
     * @param uuidBytes Byte representation of uuid.
     * @return {@link ParcelUuid} parsed from bytes.
     * @throws IllegalArgumentException If the {@code uuidBytes} cannot be parsed.
     */
    public static ParcelUuid parseUuidFrom(byte[] uuidBytes) {
        if (uuidBytes == null) {
            throw new IllegalArgumentException("uuidBytes cannot be null");
        }
        int length = uuidBytes.length;
        if (length != UUID_BYTES_16_BIT && length != UUID_BYTES_32_BIT &&
                length != UUID_BYTES_128_BIT) {
            throw new IllegalArgumentException("uuidBytes length invalid - " + length);
        }
        // Construct a 128 bit UUID.
        if (length == UUID_BYTES_128_BIT) {
            ByteBuffer buf = ByteBuffer.wrap(uuidBytes).order(ByteOrder.LITTLE_ENDIAN);
            long msb = buf.getLong(8);
            long lsb = buf.getLong(0);
            return new ParcelUuid(new UUID(msb, lsb));
        }
        // For 16 bit and 32 bit UUID we need to convert them to 128 bit value.
        // 128_bit_value = uuid * 2^96 + BASE_UUID
        long shortUuid;
        if (length == UUID_BYTES_16_BIT) {
            shortUuid = uuidBytes[0] & 0xFF;
            shortUuid += (uuidBytes[1] & 0xFF) << 8;
        } else {
            shortUuid = uuidBytes[0] & 0xFF;
            shortUuid += (uuidBytes[1] & 0xFF) << 8;
            shortUuid += (uuidBytes[2] & 0xFF) << 16;
            shortUuid += (uuidBytes[3] & 0xFF) << 24;
        }
        long msb = BASE_UUID.getUuid().getMostSignificantBits() + (shortUuid << 32);
        long lsb = BASE_UUID.getUuid().getLeastSignificantBits();
        return new ParcelUuid(new UUID(msb, lsb));
    }

    /**
     * Check whether the given parcelUuid can be converted to 16 bit bluetooth uuid.
     *
     * @param parcelUuid
     * @return true if the parcelUuid can be converted to 16 bit uuid, false otherwise.
     */
    public static boolean is16BitUuid(ParcelUuid parcelUuid) {
        UUID uuid = parcelUuid.getUuid();
        if (uuid.getLeastSignificantBits() != BASE_UUID.getUuid().getLeastSignificantBits()) {
            return false;
        }
        return ((uuid.getMostSignificantBits() & 0xFFFF0000FFFFFFFFL) == 0x1000L);
    }

    /**
     * Check whether the given parcelUuid can be converted to 32 bit bluetooth uuid.
     *
     * @param parcelUuid
     * @return true if the parcelUuid can be converted to 32 bit uuid, false otherwise.
     */
    public static boolean is32BitUuid(ParcelUuid parcelUuid) {
        UUID uuid = parcelUuid.getUuid();
        if (uuid.getLeastSignificantBits() != BASE_UUID.getUuid().getLeastSignificantBits()) {
            return false;
        }
        if (is16BitUuid(parcelUuid)) {
            return false;
        }
        return ((uuid.getMostSignificantBits() & 0xFFFFFFFFL) == 0x1000L);
    }
}

