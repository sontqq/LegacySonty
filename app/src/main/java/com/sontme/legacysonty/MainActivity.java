package com.sontme.legacysonty;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
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
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;
import com.squareup.picasso.Picasso;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;


public class MainActivity extends AppCompatActivity {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    CallbackManager fBcallbackManager;
    Profile fB_profile;
    Button fblogout;
    Button fblogin;
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
    boolean doubleBackToExitPressedOnce = false;

    NfcAdapter nfcadapter;
    PendingIntent nfcPendingIntent;

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

    public void showLocationAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hely adatok hozzáférése a háttérben");
        builder.setMessage("Az alkalmazás működéséhez szükség van a hely adatokra a háttérben, ha az alkalmazás be van zárva");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Prompt the user once explanation has been shown

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(1);
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //showLocationAlert();

        /*ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 100);
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                            } else {
                                // No location access granted.
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 102);
                            }
                        }
                );
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });*/

        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        String[] perms2 = {Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        String locale = Locale.getDefault().getLanguage(); // hu/en
        String askString = "";
        if (locale.equals("hu")) {
            askString = "Az alkalmazás helyes működéséhez kérjük engedélyezze " +
                    "a helyadatokhoz " +
                    "való hozzáférést a háttérben " +
                    "amikor az alkalmazás nincs használatban vagy be van zárva.";
        } else {
            askString = "The application in order to work properly needs your permission to use Location Data in the background, when the application not in use or closed.";
        }

        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, 0, perms)
                            .setRationale("Please enable Location Permission for the Application")
                            .setPositiveButtonText("Ok")
                            .setNegativeButtonText("No")
                            .setTheme(R.style.AppTheme)
                            .build());
        }
        if (!EasyPermissions.hasPermissions(this, perms2)) {
            String rationale = "always on background location";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                PackageManager pm = getPackageManager();
                rationale = pm.getBackgroundPermissionOptionLabel().toString();
            }
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, 1, perms2)
                            .setRationale(askString)
                            .setPositiveButtonText("Ok")
                            .setNegativeButtonText("No")
                            .setTheme(R.style.AppTheme)
                            .build());
        }


        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ImageView image = new ImageView(this);
            image.setImageResource(R.drawable.gpssatellite);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(image);
            builder.setTitle("Hely adatok hozzáférése a háttérben");
            builder.setMessage("Az alkalmazás helyes működéséhez szükség van a hely adatokra a háttérben, ha az alkalmazás be van zárva.\nEngedélyezi az alkalmazás számára a helyadatokhoz való hozzáférést a háttérben?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 100);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //System.exit(1);
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }*/
        //managePermissions();
        Intent i = new Intent(
                MainActivity.this, BackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(MainActivity.this, i);
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
                        public int compare(ScanResult o1, ScanResult o2) {
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
            //managePermissions();
        }
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
                            txt4.setText("No location data yet");
                        }
                    }
                });
            }
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
                    /*Intent i = new Intent(MainActivity.this, BackgroundService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(getApplicationContext(), i);
                    } else {
                        startService(i);
                    }*/
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
            }
        });
        BTN_connectOpenWifi.getBackground().setAlpha(128);
        BTN_connectOpenWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ArrayList<ScanResult> openap = BackgroundService.OpenWifiManager.getStrongestOpenAp(
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
                                        BackgroundService.OpenWifiManager.enableApToConnect(
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
        BTN_RerunQues.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        while (BackgroundService.webRequestExecutor.getQueue().size() < BackgroundService.webRequestExecutor.getQueue().size() * 5) {
                            for (Runnable run : BackgroundService.webReqRunnablesList) {
                                BackgroundService.webRequestExecutor.submit(run);
                            }
                            Log.d("TTTT_", BackgroundService.webRequestExecutor.getQueue().size() + "<- size");
                        }
                    }
                };
                t.start();
                //Toast.makeText(getApplicationContext(), "Height=" + BTN_RerunQues.getHeight() + " | " + BTN_RerunQues.getLayoutParams().height, Toast.LENGTH_LONG).show();
                return true;
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
                //dialog.show();

            }
        });

        BTN_BL_customPair = findViewById(R.id.btn_bl_custompair);
        BTN_nearbySend = findViewById(R.id.btn_nearbysend);
        BTN_startNearby = findViewById(R.id.btn_startnearby);
        BTN_stopNearby = findViewById(R.id.btn_stopnearby);
        BTN_cellTowers = findViewById(R.id.btn_celltowers);
        BTN_cellTowers.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), "Get TRIANGULATE info. Nothing else", Toast.LENGTH_LONG).show();
                return false;
            }
        });
        BTN_cellTowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //RealVector standardDeviation = optimum.getSigma(0);
                //RealMatrix covarianceMatrix = optimum.getCovariances(0);
                int i = 0;
                double[][] locations = new double[BackgroundService.aplist.size()][2];
                double[] distances = new double[BackgroundService.aplist.size()];
                Set entries = BackgroundService.aplist.entrySet();
                Iterator entriesIterator = entries.iterator();
                String distancess = "";
                while (entriesIterator.hasNext()) {
                    Map.Entry<String, ApWithLocation> mapping = (Map.Entry<String, ApWithLocation>) entriesIterator.next();
                    locations[i][0] = mapping.getValue().getLocation().getLatitude();
                    locations[i][1] = mapping.getValue().getLocation().getLongitude();
                    distances[i] = mapping.getValue().getRssi();
                    i++;
                    try {
                        double meters = BackgroundService.calculateDistance(mapping.getValue().getRssi(), mapping.getValue().getFrequency());
                        String s = "[" + i + "] " + mapping.getValue().getSsid() + " | " + mapping.getValue().getMac() + " | " + BackgroundService.round(meters, 2) + " m";
                        BackgroundService.sendMessage_Telegram(s);
                        Log.d("distance_c", s);
                        distancess += s + "\n";
                    } catch (Exception e) {
                        Log.d("distance_c", "error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                try {
                    NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(locations, distances), new LevenbergMarquardtOptimizer());
                    LeastSquaresOptimizer.Optimum optimum = solver.solve();
                    double[] centroid = optimum.getPoint().toArray();
                    Location test = new Location("testlocation");
                    test.setLatitude(centroid[0]);
                    test.setLongitude(centroid[1]);
                    double dist = BackgroundService.CURRENT_LOCATION.distanceTo(test);
                    Log.d("triangulate_", "Distance = " + BackgroundService.round(dist, 2));
                    String address = BackgroundService.locationToStringAddress(getApplicationContext(), test);
                    BackgroundService.sendMessage_Telegram("Distance: " + BackgroundService.round(dist, 2) + " m ADDRESS: " + address);
                    Toast.makeText(getApplicationContext(), distancess + "\n\nDistance: " + BackgroundService.round(dist, 2) + " m ADDRESS: " + address, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.d("triangulate_", "error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
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
                    if (timeago == null)
                        Log.d("timeago_null", "timeago is null " + timeago);
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
                    BTN_wifi_color.setBackgroundColor(wifiColor);
                    BTN_wifi_color.setText("[" + wifiInfo.getRssi() + "] > Color: "
                            + wifiColor + " Time: " + System.currentTimeMillis());

                    try {
                        getcelldata();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                return Color.HSVToColor(new float[]{(float) value * 120f, 1f, 1f});
            }
        }, 1000);

        try {
            BackgroundService.analyticsTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("MainActivity Started")
                    .build());
        } catch (Exception e) {
        }


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("8342526467-9sqd8bsp5ap30nbl1ssjrplut30cn42b.apps.googleusercontent.com")
                //.requestIdToken("8342526467-466kq8rjau1pej8j44bh56ekip7keolf.apps.googleusercontent.com")
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            // not signed in
        } else {
            // already signed in
        }
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1111);
            }
        });

        TextView fbText = findViewById(R.id.fbtext);
        if (fB_profile == null) {
            fbText.setText("Sign in via Facebook");
            ImageView img = findViewById(R.id.fbprofilepic);
            img.setVisibility(View.GONE);
        } else {
            ImageView img = findViewById(R.id.fbprofilepic);
            img.setVisibility(View.VISIBLE);
            fbText.setText("Welcome " + fB_profile.getName());
        }
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        fBcallbackManager = CallbackManager.Factory.create();
        fblogin = findViewById(R.id.fblogin);
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile", "user_friends");
        fblogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
                //ImageView img = findViewById(R.id.fbprofilepic);
                //img.setVisibility(View.VISIBLE);
            }
        });
        loginButton.registerCallback(fBcallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        String accessToken = loginResult.getAccessToken()
                                .getToken();
                        Log.d("FB_LOGIN_", "success login: " + accessToken);
                        fbText.setText("Facebook: Logging In");
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        try {
                                            String id = object.getString("id");
                                            try {
                                                URL profile_pic = new URL(
                                                        "http://graph.facebook.com/" + id + "/picture?type=large");
                                                Log.d("FB_LOGIN_", "profile_picture: " + profile_pic);
                                                ImageView img = findViewById(R.id.fbprofilepic);
                                                img.setVisibility(View.VISIBLE);
                                                Log.d("FB_LOGIN_", "Profile picture: " + profile_pic.toString());
                                                Log.d("FB_LOGIN_", "Profile picture: " + profile_pic.toURI().toString());
                                                LinearLayout proflay = findViewById(R.id.proflay);

                                                ViewTreeObserver observer = proflay.getViewTreeObserver();
                                                observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                                    @Override
                                                    public void onGlobalLayout() {
                                                        int height = proflay.getHeight();
                                                        Log.d("FB_LOGIN_", "height: " + height);
                                                        Log.d("FB_LOGIN_", "height2: " + loginButton.getHeight());
                                                        Picasso.get()
                                                                .load("http://graph.facebook.com/" + id + "/picture?type=large")
                                                                .resize(100, 100)
                                                                .noFade()
                                                                .into(img);
                                                        proflay.getViewTreeObserver().removeGlobalOnLayoutListener(
                                                                this);
                                                    }
                                                });
                                            } catch (Exception ee) {
                                                Log.d("FB_LOGIN_", "profpic: " + ee.getMessage());
                                                ee.printStackTrace();
                                                fbText.setText("Error Logging In");
                                            }
                                            String name = object.getString("name");
                                            String email = object.getString("email");
                                            //String gender = object.getString("gender");
                                            //String birthday = object.getString("birthday");
                                            Log.d("FB_LOGIN_", "logged in 1: " + name);
                                            Log.d("FB_LOGIN_", "logged in 2: " + email);
                                            //Log.d("FB_LOGIN_","logged in 3: " + gender);
                                            //Log.d("FB_LOGIN_","logged in 4: " + birthday);
                                            fbText.setText("Welcome " + name);
                                            fblogin.setVisibility(View.GONE);
                                            fblogout.setVisibility(View.VISIBLE);
                                            ImageView img = findViewById(R.id.fbprofilepic);
                                            img.setVisibility(View.VISIBLE);
                                        } catch (Exception e) {
                                            fbText.setText("Error Logging In");
                                            fblogin.setVisibility(View.VISIBLE);
                                            fblogout.setVisibility(View.GONE);
                                            ImageView img = findViewById(R.id.fbprofilepic);
                                            img.setVisibility(View.GONE);
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        exception.printStackTrace();
                    }
                });
        // fblogin logout
        // LoginManager.getInstance().logOut();
        fblogout = findViewById(R.id.fblogout);
        fblogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                fbText.setText("Signed Out");
                fblogin.setVisibility(View.VISIBLE);
                fblogout.setVisibility(View.GONE);
                ImageView img = findViewById(R.id.fbprofilepic);
                img.setVisibility(View.GONE);
            }
        });
        fB_profile = Profile.getCurrentProfile().getCurrentProfile();
        if (fB_profile != null) {
            // user has logged in
            fbText.setText("Welcome " + fB_profile.getName());
            fblogin.setVisibility(View.GONE);
            fblogout.setVisibility(View.VISIBLE);
            Button reqbtn = findViewById(R.id.btn_reque);
            ImageView img = findViewById(R.id.fbprofilepic);
            /*ViewTreeObserver vto = reqbtn.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int height = reqbtn.getHeight();
                }
            });*/
            int height = reqbtn.getHeight();
            Uri prof = fB_profile.getProfilePictureUri(144, 144);
            Log.d("FB_LOGIN_", "logged in: " + fB_profile.getName() + " " + prof);
            String id = fB_profile.getId();
            Picasso.get()
                    //.load("http://graph.facebook.com/" + id + "/picture?type=large")
                    .load(prof)
                    .resize(144, 144)
                    .noFade()
                    .into(img);
            img.setVisibility(View.VISIBLE);

            //img.setLayoutParams(new ViewGroup.LayoutParams(100,100));
            String profileid = fB_profile.getId();
            GraphRequest request = GraphRequest.newGraphPathRequest(
                    accessToken,
                    "/" + profileid + "/friends",
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            try {
                                Log.d("FB_LOGIN_FRIEND", "Friend List1: " + response.toString());
                                Log.d("FB_LOGIN_FRIEND", "Friend List3: " + response.getJSONObject().toString());
                                JSONArray jsonArrayFriends = response.getJSONObject().getJSONArray("data");
                                //JSONObject friendlistObject = jsonArrayFriends.getJSONObject(0);
                                Log.d("FB_LOGIN_FRIEND", "Friend List4: " + jsonArrayFriends.length());
                                //Log.d("FB_LOGIN_FRIEND","Friend List5: " + friendlistObject.toString());
                            } catch (Exception e) {
                                Log.d("FB_LOGIN_FRIEND", "ERROR: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    });
            //request.executeAsync();

        } else {
            // user has not logged in
            fbText.setText("Please Sign In");
            fblogin.setVisibility(View.VISIBLE);
            fblogout.setVisibility(View.GONE);
            ImageView img = findViewById(R.id.fbprofilepic);
            img.setVisibility(View.GONE);
            Log.d("FB_LOGIN_", "logged out");
        }

        NfcManager manager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        nfcadapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        nfcadapter.setNdefPushMessageCallback(new NfcAdapter.CreateNdefMessageCallback() {
            @Override
            public NdefMessage createNdefMessage(NfcEvent event) {
                Log.d("NGC_TAG", "createndefmessage - " + event.toString());
                NdefMessage ndefmsg = new NdefMessage(
                        new NdefRecord[]{
                                NdefRecord.createApplicationRecord("com.sontme.legacysonty")});
                return ndefmsg;
            }
        }, this);
    }

    private NdefMessage createndefmessage(String msg) {
        byte[] languageCode;
        byte[] msgBytes;
        try {
            languageCode = "en".getBytes("UTF-8");
            msgBytes = msg.getBytes("UTF-8");
        } catch (Exception e) {
            return null;
        }

        byte[] messagePayload = new byte[1 + languageCode.length
                + msgBytes.length];
        messagePayload[0] = (byte) 0x02; // status byte: UTF-8 encoding and
        // length of language code is 2
        System.arraycopy(languageCode, 0, messagePayload, 1,
                languageCode.length);
        System.arraycopy(msgBytes, 0, messagePayload, 1 + languageCode.length,
                msgBytes.length);

        NdefMessage message;
        NdefRecord[] records = new NdefRecord[1];
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[]{}, messagePayload);
        records[0] = textRecord;
        message = new NdefMessage(records);
        return message;
    }

    private void enableNdefExchangeMode() {
        try {
            NdefMessage msg = createndefmessage("hello");
            nfcadapter.enableForegroundNdefPush(this, msg);
            IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            IntentFilter[] mNdefExchangeFilters = new IntentFilter[]{ndefDetected};
            try {
                nfcadapter.enableForegroundDispatch(this, nfcPendingIntent,
                        mNdefExchangeFilters, null);
                nfcadapter.setNdefPushMessage(msg, this);
                nfcadapter.setNdefPushMessageCallback(new NfcAdapter.CreateNdefMessageCallback() {
                    @Override
                    public NdefMessage createNdefMessage(NfcEvent event) {
                        Log.d("NFC_TAG", "event: " + event.toString());
                        return null;
                    }
                }, this);
            } catch (Exception e) {
                nfcadapter.enableForegroundDispatch(this, nfcPendingIntent,
                        new IntentFilter[]{ndefDetected}, null);
                nfcadapter.setNdefPushMessage(msg, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        try {
            BackgroundService.analyticsTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("MainActivity onResume")
                    .build());
        } catch (Exception e) {
            //e.printStackTrace();
        }
        /*
        nfcadapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        */
        try {
            nfcadapter.enableForegroundDispatch(this, nfcPendingIntent, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //enableNdefExchangeMode();

        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (nfcadapter != null) {
            nfcadapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            MifareUltralight mifareUlTag = MifareUltralight.get(tag);
            try {
                mifareUlTag.connect();
                mifareUlTag.writePage(4, "hello".getBytes(Charset.forName("UTF-8")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            byte[] payload = SontHelper.NFCHelper.detectTagData(tag).getBytes();
            String str = new String(payload, StandardCharsets.UTF_8);
            Log.d("NFC_TAG", "nfc payload str: " + str);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1111) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        fBcallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d("Google_SIGNIN", "Success 1 = " + account.getDisplayName());
            Log.d("Google_SIGNIN", "Success 2 = " + account.getEmail());
            Log.d("Google_SIGNIN", "Success 3 = " + account.getAccount().name + " | " + account.getAccount().type);
            Log.d("Google_SIGNIN", "Success 4 = " + account.getPhotoUrl());
            //updateUI(account);
        } catch (ApiException e) {
            Log.d("Google_SIGNIN", "FAILED code = " + e.getStatusCode());
            //updateUI(null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
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
        BTN_cellTowers.setText("Cell Towers: " + infos.size() + " / Average: [" + BackgroundService.round(average, 2) + "] " +
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


