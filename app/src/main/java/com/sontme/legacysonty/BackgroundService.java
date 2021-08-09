package com.sontme.legacysonty;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.common.io.Files;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

public class BackgroundService extends Service {

    public static WifiManager wifiManager;
    public static List<ScanResult> scanresult;
    public static ThreadPoolExecutor executor;
    public static Location CURRENT_LOCATION;
    public static int LOCATON_CHANGE_COUNTER;
    public static long CURRENT_LOCATION_LASTTIME;
    public static int count;
    public static TimeElapsedUtil time;

    static Context c;

    public static ArrayList<BluetoothDeviceWithLocation> bluetoothDevicesFound;

    static int cnt_notrecorded;
    static int cnt_new;
    static int cnt_updated_time;
    static int cnt_updated_str;
    public static LocationListener locationListener;
    public static LocationManager locationManager;

    //String PROVIDER = LocationManager.NETWORK_PROVIDER;
    String PROVIDER = LocationManager.GPS_PROVIDER;
    public static int TIME = 0;
    public static int DISTANCE = 0;
    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                scanresult = wifiManager.getScanResults();
                wifiManager.startScan();
            } catch (Exception e) {
                e.printStackTrace();
                //    }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothDevicesFound = new ArrayList<>();
        c = getApplicationContext();
        cnt_notrecorded = 0;
        cnt_new = 0;
        cnt_updated_time = 0;
        cnt_updated_str = 0;
        count = 0;

        //createNotifGroup(getApplicationContext(), "legacysonty", "legacysonty");
        //createNotificationChannel(getApplicationContext(),"legacysonty")

        showOngoing("Waiting..");
        time = new TimeElapsedUtil();

        executor = new ThreadPoolExecutor(1, Integer.MAX_VALUE,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        try {
            registerReceiver(wifiReceiver, new IntentFilter(
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                CURRENT_LOCATION = location;
                LOCATON_CHANGE_COUNTER++;
                CURRENT_LOCATION_LASTTIME = SystemClock.elapsedRealtime();

                try {
                    for (final ScanResult result : wifiManager.getScanResults()) {
                        final String enc = convertEncryption(result);
                        if ((result.BSSID != null) && (result.BSSID.length() >= 1)) {
                            final String reqBody =
                                    "?id=0&ssid=" + result.SSID +
                                            "&add=" + locationToStringAddress(getApplicationContext(),
                                            location) +
                                            "&bssid=" + result.BSSID +
                                            "&source=" + "legacy_sonty" +
                                            "&enc=" + enc +
                                            "&rssi=" + convertDBM(result.level) +
                                            "&long=" + location.getLongitude() +
                                            "&lat=" + location.getLatitude() +
                                            "&channel=" + result.frequency;
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    RequestTaskListener rtl = new RequestTaskListener() {
                                        @Override
                                        public void update(String string) {
                                            if (string != null) {
                                                try {
                                                    Log.d("HTTP_STAT_", "response: " + string);
                                                    if (string.contains("not_recorded")) {
                                                        cnt_notrecorded++;
                                                        //vibrate(getApplicationContext());
                                                    }
                                                    if (string.contains("new")) {
                                                        cnt_new++;
                                                        vibrate(getApplicationContext());
                                                    }
                                                    if (string.contains("regi_old")) {
                                                        cnt_updated_time++;
                                                        //vibrate(getApplicationContext());
                                                    }
                                                    if (string.contains("regi_str")) {
                                                        cnt_updated_str++;
                                                        //vibrate(getApplicationContext());
                                                    }
                                                    String stats = "Not: " + cnt_notrecorded + " " +
                                                            "Error: " + Live_Http_GET_SingleRecord.error_counter + " " +
                                                            "New: " + cnt_new + " " +
                                                            "Time: " + cnt_updated_time + " " +
                                                            "Str: " + cnt_updated_str + " ";
                                                    String text = "" + System.currentTimeMillis() + " -> " + count +
                                                            " R: " + roundBandwidth(Live_Http_GET_SingleRecord.bytesReceived) +
                                                            " S: " + roundBandwidth(Live_Http_GET_SingleRecord.bytesSent) + "\n" +
                                                            "LngLat: " + locationToStringAddress(getApplicationContext(), CURRENT_LOCATION) + "\n" +
                                                            "Spd: " + round(CURRENT_LOCATION.getSpeed() * 3.6, 1) + " km/h @ " +
                                                            "Acc: " + CURRENT_LOCATION.getAccuracy() + " Src: " + CURRENT_LOCATION.getProvider() + "\n" +
                                                            "Queue: " + executor.getActiveCount() + " / " + executor.getQueue().size() +
                                                            "\n" + stats;
                                                    count++;
                                                    updateCurrent("LegacySonty", text);
                                                    String lastHandledSSID = getStringbetweenStrings(string, "_SSID_", "_SSIDEND_");
                                                    String updatedReason = getStringbetweenStrings(string, "_REASON_", "_ENDREASON_");
                                                    String macAddress = getStringbetweenStrings(string, "_MACBEGIN_", "_ENDMAC_");
                                                    //sendMessage_BetaBot(string);

                                                    if (!updatedReason.contains("NOT")) {
                                                        if (!updatedReason.contains("STR") &&
                                                                !updatedReason.contains("NEW") &&
                                                                !updatedReason.contains("OLD") &&
                                                                !string.contains("not_recorded") &&
                                                                !string.contains("regi_str") &&
                                                                !string.contains("new") && // ||
                                                                !string.contains("regi_old")) {
                                                            updateCurrent_error(getApplicationContext(), "UNKNOWN ANSWER", "SSID: " + lastHandledSSID + "\nMAC: " + macAddress);
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    //updateCurrent_error(getApplicationContext(), "LegacySonty [ERROR]", e.getMessage() + "\n\n" + e.getStackTrace()[0].toString() + " ON LINE: " + e.getStackTrace()[0].getLineNumber());
                                                    RequestTask r2 = new RequestTask();
                                                    r2.addListener(this);
                                                    r2.execute(reqBody);
                                                    //updateCurrent_error(getApplicationContext(), "LegacySonty", "GOT NULL HTTP RESPONSE! RETRY!");
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    };
                                    RequestTask r = new RequestTask();
                                    r.addListener(rtl);
                                    r.execute(reqBody);
                                }
                            };
                            BackgroundService.executor.submit(runnable);

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(
                PROVIDER,
                TIME, DISTANCE,
                locationListener);
        showOngoing("Location Requested");
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), Alarm.class);
        intent.putExtra("requestCode", 66);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 66, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                1 * 30 * 1000, pendingIntent);

        new SimpleAlarmManager(getApplicationContext()).setup(SimpleAlarmManager.INTERVAL_DAY,
                10, 0, 0).register(5).start();
        //locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
        //locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
        try {
            wifiManager.startScan();
        } catch (Exception e) {
            e.printStackTrace();
        }
        showOngoing("WiFi Scan initiated");
        final android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (BackgroundService.scanresult.size() >= 1) {
                        if (wifiManager.isWifiEnabled()) {
                            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo wifiinfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                            if (!wifiinfo.isConnectedOrConnecting()) {
                                //connectStrongestOpenWifi(getApplicationContext(),
                                //      BackgroundService.scanresult);
                            } else if (convertDBM(wifiInfo.getRssi()) < 10) {
                                //connectStrongestOpenWifi(getApplicationContext(),
                                //      BackgroundService.scanresult);
                            }
                        }
                    } else {
                        try {
                            wifiManager.startScan();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (BackgroundService.scanresult.size() >= 1) {
                        } else {
                            wifiManager.startScan();
                            if (BackgroundService.scanresult.size() >= 1) {
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, 3000);
            }
        }, 5000);
        showOngoing("Service Started");
        boolean scanfor_bl = true;
        if (scanfor_bl) {
            showOngoing("SCANNING for BLUETOOTH devices");
            final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.enable();
            final BluetoothAdapter.LeScanCallback leReceiver = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    handleBluetoothDeviceFound(device, true);
                    if (!bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.startDiscovery();
                        bluetoothAdapter.startLeScan(this);
                    }
                }
            };
            final BroadcastReceiver classicReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        try {
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            handleBluetoothDeviceFound(device, false);
                            if (!bluetoothAdapter.isDiscovering()) {
                                bluetoothAdapter.startDiscovery();
                                bluetoothAdapter.startLeScan(leReceiver);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            try {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(classicReceiver, filter);
                Set<BluetoothDevice> bondeds = bluetoothAdapter.getBondedDevices();
                Log.d("Bluetooth_Scan_", "BONDED (" + bondeds.size() + ")");
                for (BluetoothDevice dev : bondeds) {
                    //Log.d("Bluetooth_Scan_", "BONDED: " + dev.getName());
                }
                bluetoothAdapter.startDiscovery();
                bluetoothAdapter.startLeScan(leReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("Bluetooth_Scan_Classic/LE_", "Started");
        }

        //NearbyHandler nearby = new NearbyHandler(getApplicationContext(), Strategy.P2P_CLUSTER);
        //nearby.startAdvertising();
        //nearby.startDiscovering();
        try {
            String unenrypted = "test";
            SecretKey secret = SontHelper.Encrypt.generateKey();
            byte[] encrypted = SontHelper.Encrypt.encryptMsg(unenrypted, secret);
            String s = new String(encrypted);
            Log.d("AES_", "Encrypted: " + s);
            Log.d("AES_", "Decrypted: " + SontHelper.Encrypt.decryptMsg(encrypted, secret));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    boolean handleBluetoothDeviceFound(BluetoothDevice device, boolean isLe) {
        try {
            bluetoothDevicesFound.add(new BluetoothDeviceWithLocation(device, CURRENT_LOCATION));
            //Gson gson = new Gson();
            //String saveable = gson.toJson(bluetoothDevicesFound);
            String tosave = "\n_name_" + device.getName() + "_endname__address_" + device.getAddress() + "_endaddress__lat_" + CURRENT_LOCATION.getLatitude() + "_endlat__lng_" + CURRENT_LOCATION.getLongitude() + "_endlng_\n";
            Runnable saveBl = new Runnable() {
                @Override
                public void run() {
                    writeExternalPublic(getApplicationContext(), "BLsession.txt", tosave, true);
                }
            };
            saveBl.run();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void connectStrongestOpenWifi(Context context,
                                                List<android.net.wifi.ScanResult> scanResult) {
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
            android.net.wifi.ScanResult strongestOpenAp = onlyOpenAps.get(0);

            //Gson gson = new Gson();
            List<String> banneds = new ArrayList<>();
            /*try {
                String aps_json_object_loaded = FileIOTools.getSharedPref(context, "bannedlist");
                banneds = gson.fromJson(aps_json_object_loaded, new TypeToken<List<String>>() {
                }.getType());
            } catch (Exception e) {
                banneds = Arrays.asList("VENDEG", "VENDEG_GARDEN", "Telekom fon", "Telekom Fon WiFi HU",
                        "Smartwifi", "#EgerFreeWifi", "KMKK", "KMKK_", "EMKK", "MAVSTART",
                        "Telekom", "uni-Eszterhazy", "Volan");

                String aps_json_object_saved = gson.toJson(banneds);
                FileIOTools.saveSharedPref(context, "bannedlist", aps_json_object_saved);
            }*/

            if (onlyOpenAps.size() >= 1) {
                for (android.net.wifi.ScanResult openApChosed : onlyOpenAps) {
                    String openApChosed_trimmed = openApChosed.SSID;
                    if (banneds.contains(openApChosed_trimmed)) {
                        // skip
                    } else {
                        if (openApChosed.SSID.length() > 3) {
                            strongestOpenAp = openApChosed;
                            break;
                        }
                    }
                }
            }

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + strongestOpenAp.SSID + "\"";
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

            wifiManager.addNetwork(conf);
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + strongestOpenAp.SSID + "\"")) {
                    android.net.wifi.ScanResult finalStrongestOpenAp = strongestOpenAp;
                    if (banneds.stream().anyMatch(str -> str.contains(
                            finalStrongestOpenAp.SSID))) {
                        /*Toast.makeText(context, "BANNED AP!\n" +
                                "<" + i.SSID + ">\n" +
                                "Refusing to connect!", Toast.LENGTH_LONG).show();*/

                        return;
                        //break;
                    } else {
                        //wifiManager.disconnect();
                        //wifiManager.enableNetwork(i.networkId, true);
                        //wifiManager.reconnect();
                        Toast.makeText(context, "Enabled: " + i.SSID, Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }


        } catch (Exception e) {
            //Toast.makeText(context, "Error! No open WIFI around\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            //BackgroundService.updateCurrentError("OPEN WIFI ERROR", "No OPEN WiFi found", context);
            e.printStackTrace();
        }
    }

    public static String getScanResultSecurity(android.net.wifi.ScanResult scanResult) {
        final String cap = scanResult.capabilities;
        final String[] securityModes = {"WEP", "PSK", "EAP"};

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

    public void updateCurrent(String title, String text) {
        int color_drawable = R.drawable.servicetransparenticon;
        Intent notificationIntent = new Intent(getApplicationContext(), notificationReceiver.class);
        notificationIntent.putExtra("29294", "29294");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                29294, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent intent_exit = new Intent(getApplicationContext(), notificationReceiver.class);
        intent_exit.setAction("exit");
        intent_exit.putExtra("requestCode", 99);
        PendingIntent pi_exit = PendingIntent.getBroadcast(getApplicationContext(),
                99, intent_exit, PendingIntent.FLAG_IMMUTABLE);

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


        Random rnd = new Random();
        int color = Color.argb(0, rnd.nextInt(256 - 0), rnd.nextInt(256 - 0), rnd.nextInt(256 - 0));

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "sontylegacy")
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
                NotificationCompat.BigPictureStyle,NotificationCompat.BigTextStyle,NotificationCompat.DecoratedCustomViewStyle,NotificationCompat.InboxStyle,NotificationCompat.MediaStyle,NotificationCompat.MessagingStyle
                */
                .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("")
                        /*.setSummaryText("IP: " + ip)*/
                        //.setBigContentTitle()
                )
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSmallIcon(color_drawable)
                .setContentIntent(pendingIntent)
                .setChannelId("sontylegacy")
                .addAction(R.drawable.servicetransparenticon, "NET", pi_location_network)
                .addAction(R.drawable.servicetransparenticon, "GPS", pi_location_gps)
                .addAction(R.drawable.servicetransparenticon, "EXIT", pi_exit)
                .build();

        NotificationManager nm = getSystemService(NotificationManager.class);

        nm.notify(58, notification);
    }

    public static void updateCurrent_error(Context c, String title, String text) {
        int color_drawable = R.drawable.error_icon;
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
                .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("")
                        //.setSummaryText("IP: " + ip)
                        //.setBigContentTitle()
                )
                .setSmallIcon(color_drawable)
                .setOngoing(false)
                .setChannelId("sontylegacy")
                .build();
        NotificationManager nm = c.getSystemService(NotificationManager.class);
        nm.notify(59, notification);
    }

    public void showOngoing(String text) {
        Intent notificationIntent = new Intent(getApplicationContext(), notificationReceiver.class);
        notificationIntent.putExtra("29294", "29294");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                29294,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

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

        int color2 = Color.argb(255, 220, 237, 193);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "sontylegacy")
                .setContentTitle("SontyLegacy Service")
                .setContentText(text)
                .setColorized(true)
                .setColor(color2)
                .setContentInfo("CONTENT INFO")
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSmallIcon(R.drawable.servicetransparenticon)
                .setContentIntent(pendingIntent)
                .setChannelId("sonty")
                .addAction(R.drawable.servicetransparenticon, "NET", pi_location_network)
                .addAction(R.drawable.servicetransparenticon, "GPS", pi_location_gps)
                .addAction(R.drawable.servicetransparenticon, "EXIT", pi)
                .build();

        startForeground(58, notification);

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
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

    public static void sendMessage_BetaBot(String message) {
        int retryCount = 0;
        try {
            String url = "https://api.telegram.org/bot990712757:AAGyuPqZJUNoRAi1DMl-oRzEYInZz7UP0C4/sendMessage?chat_id=1093250115&text=" +
                    message;
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
            sendMessage_BetaBot(message + "_" + retryCount);
            e.printStackTrace();
        }
    }

    public void restartService() {
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
        if (!result.capabilities.contains("WEP") ||
                !result.capabilities.contains("WPA")) {
            enc = "NONE";
        } else if (result.capabilities.contains("WEP")) {
            enc = "WEP";
        } else if (result.capabilities.contains("WPA")) {
            enc = "WPA";
        } else if (result.capabilities.contains("WPA2")) {
            enc = "WPA2";
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
            Log.d("Error_", e.toString());
            return "Unknown";
        }
        return strAdd;
    }

    public static class Live_Http_GET_SingleRecord {

        public static long bytesSent = 0;
        public static long bytesReceived = 0;

        public static int error_counter;

        public static String executeRequest(final String host, final int port, final String URL,
                                            final boolean METHOD_POST, final String postData) {
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

                while ((str = br.readLine()) != null) {
                    full_str += str;
                }
                bytesReceived += full_str.length();
                Log.d("HTTP_TEST_NEW", full_str.length() + " >> " + full_str);
                return full_str;
            } catch (Exception e) {
                error_counter++;
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        executeRequest(host, port, URL, METHOD_POST, postData);
                    }
                };
                executor.submit(r);
                /*
                StackTraceElement[] st = e.getStackTrace();
                String traces = "";
                traces += "CLASS: " + st[0].getClassName() +
                        " -> METHOD: " + st[0].getMethodName() +
                        " -> LINE: " + st[0].getLineNumber() + "\n";
                traces += "CLASS: " + st[st.length - 1].getClassName() +
                        " -> METHOD: " + st[st.length - 1].getMethodName() +
                        " -> LINE: " + st[st.length - 1].getLineNumber();
                    //BackgroundService.updateCurrent_error(c, "LegacySonty [HTTP ERROR]",
                    //e.getMessage() + "\n\n" + traces);
                */
                e.printStackTrace();
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
        void update(String string);
    }

    public class RequestTask extends AsyncTask<String, String, String> {
        private final List<RequestTaskListener> listeners = new ArrayList<RequestTaskListener>();

        public void addListener(RequestTaskListener toAdd) {
            listeners.add(toAdd);
        }

        public int count = 0;

        public RequestTask() {
            count++;
        }

        @Override
        protected String doInBackground(String... uri) {
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
            for (RequestTaskListener hl : listeners) {
                hl.update(result);
            }
        }
    }

    public static class SimpleAlarmManager {
        private final Context context;
        private final Intent alarmIntent;
        private int hourOfDay;
        private int minuteOfDay;
        private int secondOfDay;
        private Calendar calendar;
        private int id;
        private long interval;
        public final static long INTERVAL_DAY = AlarmManager.INTERVAL_DAY;
        private static Boolean isInitWithId = Boolean.FALSE;
        private PendingIntent pendingIntent;


        public SimpleAlarmManager(Context context) {
            this.context = context;
            this.alarmIntent = new Intent(context, notificationReceiver.class);
        }

        public SimpleAlarmManager register(int id) {
            this.id = id;
            alarmIntent.putExtra("id", id);
            SharedPreferences sharedPreferences = context.getSharedPreferences("alarm_manager", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (sharedPreferences.contains("ids")) {
                Set<String> set = sharedPreferences.getStringSet("ids", null);
                if (set != null && !set.isEmpty()) {
                    set.add(Integer.toString(id));
                }
            } else {
                Set<String> set = new HashSet<>();
                set.add(Integer.toString(id));
                editor.putStringSet("ids", set);
                editor.apply();
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("interval", this.interval);
                jsonObject.put("hourOfDay", this.hourOfDay);
                jsonObject.put("minuteOfDay", this.minuteOfDay);
                jsonObject.put("secondOfDay", this.secondOfDay);
                editor.putString("idExtra" + id, jsonObject.toString()).apply();
            } catch (JSONException e) {

            }
            return this;
        }

        public static SimpleAlarmManager initWithId(Context context, int id) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("alarm_manager", Context.MODE_PRIVATE);
            String registrationExtra = sharedPreferences.getString("idExtra" + id, null);
            int interval, hourDay, minuteOfDay, secondOfDay;
            if (registrationExtra != null) {
                try {
                    JSONObject jsonObject = new JSONObject(registrationExtra);
                    interval = jsonObject.getInt("interval");
                    hourDay = jsonObject.getInt("hourOfDay");
                    minuteOfDay = jsonObject.getInt("minuteOfDay");
                    secondOfDay = jsonObject.getInt("secondOfDay");
                    isInitWithId = Boolean.TRUE;
                    return new SimpleAlarmManager(context).setup(interval, hourDay, minuteOfDay, secondOfDay).register(id);

                } catch (JSONException e) {

                }
            }
            return null;
        }

        public static Set<String> getAllRegistrationIds(Context context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("alarm_manager", Context.MODE_PRIVATE);
            return sharedPreferences.getStringSet("ids", null);
        }

        public SimpleAlarmManager setup(long interval, int hourOfDay, int minuteOfDay, int secondOfDay) {
            this.hourOfDay = hourOfDay;
            this.secondOfDay = secondOfDay;
            this.minuteOfDay = minuteOfDay;
            this.interval = interval;
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minuteOfDay);
            calendar.set(Calendar.SECOND, secondOfDay);
            Calendar now = Calendar.getInstance();
            if (now.after(calendar))
                calendar.add(Calendar.HOUR_OF_DAY, 24);
            return this;
        }

        public Intent getIntent() {
            return alarmIntent;
        }

        public SimpleAlarmManager start() {
            if (isInitWithId == Boolean.FALSE) {
                if (PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_NO_CREATE) == null) {
                    pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (pendingIntent != null) {
                        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        //manager.cancel(pendingIntent);
                        if (interval == -1) {
                            manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        } else {
                            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
                        }
                    }
                }
            } else {
                pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_NO_CREATE);
                if (pendingIntent != null) {
                    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    manager.cancel(pendingIntent);
                    if (interval == -1) {
                        manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } else {
                        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
                    }
                }
            }
            return this;
        }

    }

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
            writeExternalPublic(context, filename, "", true);
            e.printStackTrace();
            return null;
        }
    }

    public static void vibrate(final Context ctx) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(20, -1));
                } else {
                    v.vibrate(20);
                }
            }
        };
        thread.start();
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