package com.sontme.legacysonty;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.FaceDetector;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.media.projection.MediaProjectionManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.ParcelUuid;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.provider.Settings;
import android.renderscript.ScriptGroup;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.BATTERY_SERVICE;
import static android.util.Base64.NO_WRAP;
import static android.util.Base64.decode;
import static android.util.Base64.encodeToString;


public class SontHelperSonty {
    static class CalculateObjectSize {

        private static volatile Instrumentation instrumentation;

        public static void premain(Instrumentation inst) {
            instrumentation = inst;
        }

        /*public static long getObjectSize(Object o) {
            return instrumentation.getObjectSize(o);
            sizeOf
        }*/
    }

    static class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context ctx) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 300; //100
            private static final int SWIPE_VELOCITY_THRESHOLD = 300; //100

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }
    }

    public static class RandomString {
        public String nextString() {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }

        public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        public static final String lower = upper.toLowerCase(Locale.ROOT);

        public static final String digits = "0123456789";

        public static final String alphanum = upper + lower + digits;

        private final Random random;

        private final char[] symbols;

        private final char[] buf;

        public RandomString(int length, Random random, String symbols) {
            if (length < 1) throw new IllegalArgumentException();
            if (symbols.length() < 2) throw new IllegalArgumentException();
            this.random = Objects.requireNonNull(random);
            this.symbols = symbols.toCharArray();
            this.buf = new char[length];
        }

        /**
         * Create an alphanumeric string generator.
         */
        public RandomString(int length, Random random) {
            this(length, random, alphanum);
        }

        /**
         * Create an alphanumeric strings from a secure generator.
         */
        public RandomString(int length) {
            this(length, new SecureRandom());
        }

        /**
         * Create session identifiers.
         */
        public RandomString() {
            this(21);
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

    public static void showAlertDialog(Activity a, String title, String text) {
        new AlertDialog.Builder(a)
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                //.setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    public static int countLines(String str) {
        String[] lines = str.split("\r\n|\r|\n");
        return lines.length;
    }

    /**
     * @desc Should not be used. Incomplete stuff here!
     */

    public static class BETA_FEATURES {

        static class TelegramStuff {
            /***
             * @botid like: bot873107019:AAFxjcz8J7p9XQcpOE9OABqNE62MgqHjC44
             * @chatid like: 1093250115
             * @message: any string
             */
            public static String _DEPRECATED_sendMessage_httpsurlconnection(String message) {
                URL url;
                try {
                    url = new URL("https://api.telegram.org/bot873107019:AAFxjcz8J7p9XQcpOE9OABqNE62MgqHjC44/sendMessage?chat_id=1093250115&text=" +
                            message
                    );
                    HttpsURLConnection a = (HttpsURLConnection) url.openConnection();
                    a.setDoOutput(true);
                    a.setRequestMethod("GET");
                    a.setRequestProperty("User-Agent", "Mozilla/99.0");
                    int responseCode = a.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(
                                a.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        return response.toString();
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            public static String convertPrettyJson(String uglyJson) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonParser jp = new JsonParser();
                JsonElement je = jp.parse(uglyJson);
                return gson.toJson(je);
            }

            public static String sendAnyCommand(String botId,
                                                String command,
                                                String params) {
                String urlDone = "https://api.telegram.org/bot" +
                        botId + "/" + /* 873107019:AAFxjcz8J7p9XQcpOE9OABqNE62MgqHjC44 */
                        "" + command + /*sendMessage*/
                        params; /*&chatId=123*/
                Log.d("TELEGRAMTEST_", "HTTP RESPONSE_ " + urlDone);
                URL url;
                try {
                    url = new URL(urlDone);
                    HttpsURLConnection a = (HttpsURLConnection) url.openConnection();
                    a.setDoOutput(true);
                    a.setRequestMethod("GET");
                    a.setRequestProperty("User-Agent", "Mozilla/99.0");
                    int responseCode = a.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(
                                a.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        return response.toString();
                    } else {
                        Log.d("TELEGRAMTEST_", "HTTP RESPONSE_ " + "null1 " + responseCode);
                        return null;
                    }
                } catch (Exception e) {
                    Log.d("TELEGRAMTEST_", "HTTP RESPONSE_ " + "error1");
                    e.printStackTrace();
                }
                Log.d("TELEGRAMTEST_", "HTTP RESPONSE_ " + "null2");
                return null;
            }

            public static void sendLocation(Location location) {
                try {
                    String url = "https://api.telegram.org/bot" +
                            "873107019:AAFxjcz8J7p9XQcpOE9OABqNE62MgqHjC44" + "/" +
                            "sendLocation?chat_id=1093250115&latitude=" + location.getLatitude() +
                            "&longitude=" + location.getLongitude() +
                            "&live_period=0" +
                            "&disable_notification=true";
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
                    e.printStackTrace();
                }
            }

            public static void sendMessage(String message) {
                int retryCount = 0;
                try {
                    String url = "https://api.telegram.org/bot873107019:AAFxjcz8J7p9XQcpOE9OABqNE62MgqHjC44/sendMessage?chat_id=1093250115&text=" +
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
                    sendMessage(message);
                    retryCount++;
                    e.printStackTrace();
                }
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
                    sendMessage_BetaBot(message);
                    retryCount++;
                    e.printStackTrace();
                }
            }
        }

        static class SaveMapInSharedPreferences {
            private void saveMap(Map<String, Boolean> inputMap, Context c) {
                SharedPreferences pSharedPref = c.getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
                if (pSharedPref != null) {
                    JSONObject jsonObject = new JSONObject(inputMap);
                    String jsonString = jsonObject.toString();
                    SharedPreferences.Editor editor = pSharedPref.edit();
                    editor.remove("My_map").commit();
                    editor.putString("My_map", jsonString);
                    editor.commit();
                }
            }

            private Map<String, Boolean> loadMap(Context c) {
                Map<String, Boolean> outputMap = new HashMap<String, Boolean>();
                SharedPreferences pSharedPref = c.getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
                try {
                    if (pSharedPref != null) {
                        String jsonString = pSharedPref.getString("My_map", (new JSONObject()).toString());
                        JSONObject jsonObject = new JSONObject(jsonString);
                        Iterator<String> keysItr = jsonObject.keys();
                        while (keysItr.hasNext()) {
                            String key = keysItr.next();
                            Boolean value = (Boolean) jsonObject.get(key);
                            outputMap.put(key, value);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return outputMap;
            }
        }

        static class BatteryMeasurement {
            /*long time = System.currentTimeMillis();

            // megmondja hány % lesz az adott időben
            long[][] d = {{time, 100}, {time + 1, 75}, {time + 2, 50}};
            long when = time + (5 * 1000 * 60);
            long eq = SontHelperSonty.BETA_FEATURES.BatteryMeasurement.extrapolate(d, when);
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date resultdate2 = new Date(when);

            // megmondja mikor lesz az adott százalékon
            long[][] d2 = {{100, time}, {90, time + 1}, {80, time + 2}};
            long x2 = 1;
            long eq2 = SontHelperSonty.BETA_FEATURES.BatteryMeasurement.extrapolate(d2, x2);

            Log.d("PREDICTION_", "CURRENT: " + System.currentTimeMillis() + " _ " + sdf2.format(time));
            Log.d("PREDICTION_", "FUTURE for: " + when + " / " + eq + " (" + sdf2.format(resultdate2) + ")");
            Log.d("PREDICTION_", "FUTURE for: " + x2 + "% lesz ekkor: " + sdf2.format(eq2));

            */
            static long extrapolate(long[][] d, long x) {
                long y = d[0][1] + (x - d[0][0]) / (d[1][0] - d[0][0]) * (d[1][1] - d[0][1]);
                return y;
            }

            public static long[][] append(long[][] a, long[][] b) {
                long[][] result = new long[a.length + b.length][];
                System.arraycopy(a, 0, result, 0, a.length);
                System.arraycopy(b, 0, result, a.length, b.length);
                return result;
            }

            static String convertReadable(long timestamp) {
                long t = timestamp;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date netDate = (new Date(t));
                return sdf.format(netDate);
            }
        }
    }

    public static boolean allSameLetter(String str) {
        char c1 = str.charAt(0);
        for (int i = 1; i < str.length(); i++) {
            char temp = str.charAt(i);
            if (c1 != temp) {
                //if chars does NOT match,
                //just return false from here itself,
                //there is no need to verify other chars
                return false;
            }
        }
        //As it did NOT return from above if (inside for)
        //it means, all chars matched, so return true
        return true;
    }

    //    static class ExceptionHandler {
//        /*
//        public boolean readIniLogFile() {
//            //ini4j
//            Ini ini = new Ini(new File(filename));
//            java.util.prefs.Preferences prefs = new IniPreferences(ini);
//            System.out.println("grumpy/homePage: " + prefs.node("grumpy").get("homePage", null));
//            return true;
//        }*/
//
//        /*public boolean saveIniLogFile(String key, String value) {
//            Ini ini = new Ini(new File(filename));
//            java.util.prefs.Preferences prefs = new IniPreferences(ini);
//            ini.put("block_name", key, value);
//            try {
//                ini.store();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return true;
//        }*/
//
//        public static boolean sendToRemoteLogServer(String text) {
//            try {
//                DatagramSocket clientSocket = new DatagramSocket();
//                InetAddress IPAddress = InetAddress.getByName("172.245.185.119");
//                byte[] sendData = text.getBytes();
//                DatagramPacket sendPacket = new DatagramPacket(
//                        sendData, sendData.length, IPAddress, 5050
//                );
//                clientSocket.send(sendPacket);
//                clientSocket.close();
//                return true;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }
//            // UDP VPS
//        }
//
//        public static void appendException(String ex, Context context) {
//            // Make human readable exception format:
//            // DateTime - class - linenumber - message
//            /*
//                thread.getClass().getName(),
//                throwable.getMessage()
//                e.getStackTrace()[0].getLineNumber()
//            */
//            String readableFormat = "Datetime: " + SontHelperSonty.getCurrentTimeHumanReadable() + " _ Class: " + ex;
//            Map<String, ?> keys = FileIOTools.getAllKeysOfSharedPreferences(context);
//
//            int count = keys.size();
//            for (Map.Entry<String, ?> entry : keys.entrySet()) {
//                String key = entry.getKey();
//                //String value = (String) entry.getValue();
//            }
//
//            FileIOTools.saveSharedPref(context,
//                    "exception_" + (count + 1), readableFormat
//            );
//        }
//
//
//        public static String convertExceptionHumanReadable(Exception e) {
//            Throwable rootCause = e;
//            while (rootCause.getCause() != null && rootCause.getCause() != rootCause)
//                rootCause = rootCause.getCause();
//
//            String className = rootCause.getStackTrace()[0].getClassName();
//            String methodName = rootCause.getStackTrace()[0].getMethodName();
//            String fileName = rootCause.getStackTrace()[0].getFileName();
//            int lineNumber = rootCause.getStackTrace()[0].getLineNumber();
//
//            String ret = "[Class Name]: " + className + "\n" +
//                    "[Method Name]: " + methodName + "\n" +
//                    "[Line Number]: " + lineNumber + "\n" +
//                    "[File Name]: " + fileName;
//            String ret_singleLine = "[Class Name]: " + className + "_" +
//                    "[Method Name]: " + methodName + "_" +
//                    "[Line Number]: " + lineNumber + "_" +
//                    "[File Name]: " + fileName;
//
//            return ret_singleLine;
//        }
//
//        public static void clearLogcat() {
//            try {
//                Process process = new ProcessBuilder()
//                        .command("logcat", "-c")
//                        .redirectErrorStream(true)
//                        .start();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public static void clearExceptions(Context ctx) {
//            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
//            preferences.edit().clear().commit();
//        }
//
//        public static ArrayList<String> getLogcatArray() {
//            Process logcat;
//            final ArrayList<String> rows = new ArrayList<>();
//            try {
//                //V-Verbose (lowest priority) D-Debug I-Info W-Warning E-Error F-Fatal S-Silent
//                logcat = Runtime.getRuntime().exec(new String[]{
//                        "logcat", "-d", "-E", ""
//                });
//                BufferedReader br = new BufferedReader(new InputStreamReader(logcat.getInputStream()), 4 * 1024);
//                String line;
//
//                while ((line = br.readLine()) != null) {
//                    rows.add(line);
//                    rows.add("====================");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return rows;
//        }
//
//        public static ArrayList<String> getLogcatDebugArray() {
//            Process logcat;
//            final ArrayList<String> rows = new ArrayList<>();
//            try {
//                //V-Verbose (lowest priority) D-Debug I-Info W-Warning E-Error F-Fatal S-Silent
//                logcat = Runtime.getRuntime().exec(new String[]{
//                        "logcat", "-d", "-D", ""
//                });
//                BufferedReader br = new BufferedReader(new InputStreamReader(logcat.getInputStream()), 4 * 1024);
//                String line;
//                int c = 0;
//                while ((line = br.readLine()) != null) {
//                    //if (c >= 15)
//                    //break;
//                    rows.add(line);
//                    rows.add("=== LOGCAT END ===");
//                    c++;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return rows;
//        }
//
////        public static ArrayList<String> readExceptionAllArray(Context context) {
////
////            ArrayList<String> all_exception = new ArrayList<>();
////
////            Map<String, ?> keys = FileIOTools.getAllKeysOfSharedPreferences(context);
////
////            int count = keys.size();
////            for (Map.Entry<String, ?> entry : keys.entrySet()) {
////                String key = entry.getKey();
////                String value_exception = FileIOTools.getSharedPref(context, key);
////                all_exception.add(value_exception);
////                all_exception.add("====================");
////                //all_exception.add(key);
////                //Log.d("tesztelem", "KULCS --> " + key + "_ ERTEK --> " + value_exception);
////            }
////            //Collections.reverse(all_exception);
////            return all_exception;
////        }
//    }


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

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getEstimatedBatteryLife(Context context, long startTime, int startPercentage) {
        String returnText = "";
        try {
            long now = System.currentTimeMillis();

            int percentageForStartPercentage = startPercentage;
            int percentageForCurrentPercentage = getBatteryLevel(context);

            int percentageDifference = Math.abs(percentageForStartPercentage - percentageForCurrentPercentage);
            if (percentageDifference == 0)
                percentageDifference = 5;
            long timeDiffInMillis = now - startTime;
            if (timeDiffInMillis == 0)
                timeDiffInMillis = 1000 * 60 * 5;
            long timeTakenFor1Percentage = Math.round(timeDiffInMillis / percentageDifference);
            long timeLastForNext15Percentage = timeTakenFor1Percentage * percentageForStartPercentage;

            //BETA_FEATURES.BatteryMeasurement.extrapolate()
            long hoursLast = Math.abs(TimeUnit.MILLISECONDS.toHours(timeLastForNext15Percentage));
            long minutesLast = Math.abs(TimeUnit.MILLISECONDS.toMinutes(timeLastForNext15Percentage) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeLastForNext15Percentage)));

            String he = "";
            if (hoursLast > 0) {
                he = minutesLast + " h " + minutesLast + " m";
            } else {
                he = minutesLast + " m";
            }

            DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date();

            return String.format(he, dateFormat.format(date));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnText;
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

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "now ";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1m ";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " m ";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1h ";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " h ";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "1d ";
        } else {
            return diff / DAY_MILLIS + " d ";
        }
    }

    public static String getTimeAgo_Battery(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "NOW! ";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return " <1m ";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " m ";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return " <1h ";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " h ";
        } else if (diff < 48 * HOUR_MILLIS) {
            return " <1d ";
        } else {
            return diff / DAY_MILLIS + " d ";
        }
    }

    public static boolean isHotSpotEnabled(Context c) {
        WifiManager wifi = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        Method[] wmMethods = wifi.getClass().getDeclaredMethods();
        for (Method method : wmMethods) {
            if (method.getName().equals("isWifiApEnabled")) {
                try {
                    return (boolean) method.invoke(wifi);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public static String getStringbetweenStrings(String gotString, String whatStringStart, String whatStringEnd) {
        String result =
                gotString.substring(
                        gotString.indexOf(whatStringStart) + whatStringStart.length(),
                        gotString.length());
        result =
                result.substring(
                        0,
                        result.indexOf(whatStringEnd));
        return result;
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

            Gson gson = new Gson();
            List<String> banneds = new ArrayList<>();
            try {
                String aps_json_object_loaded = FileIOTools.getSharedPref(context, "bannedlist");
                banneds = gson.fromJson(aps_json_object_loaded, new TypeToken<List<String>>() {
                }.getType());
            } catch (Exception e) {
                banneds = Arrays.asList("VENDEG", "VENDEG_GARDEN", "Telekom fon", "Telekom Fon WiFi HU",
                        "Smartwifi", "#EgerFreeWifi", "KMKK", "KMKK_", "EMKK", "MAVSTART",
                        "Telekom", "uni-Eszterhazy", "Volan");

                String aps_json_object_saved = gson.toJson(banneds);
                FileIOTools.saveSharedPref(context, "bannedlist", aps_json_object_saved);
            }

            if (onlyOpenAps.size() >= 1) {
                for (android.net.wifi.ScanResult openApChosed : onlyOpenAps) {
                    String openApChosed_trimmed = openApChosed.SSID;
                    if (banneds.contains(openApChosed_trimmed)) {
                        // skip cuz its banned
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
                        wifiManager.enableNetwork(i.networkId, true);
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

    public static String removeDuplicates(String string) {
        Set<String> tokens = new HashSet<String>(Arrays.asList(string.split("\n")));
        StringBuilder resultBuilder = new StringBuilder();

        boolean first = true;
        for (String token : tokens) {
            if (first) {
                first = false;
            } else {
                resultBuilder.append("\n");
                resultBuilder.append(token);
            }
        }
        return resultBuilder.toString();
    }

    public static int countDuplicates(String string) {
        String[] lines = string.split("\n");
        StringBuilder resultBuilder = new StringBuilder();
        Set<String> alreadyPresent = new HashSet<String>();
        int count = 0;
        boolean first = true;
        for (String line : lines) {
            if (!alreadyPresent.contains(line)) {
                if (first) first = false;
                else resultBuilder.append("\n");

                if (!alreadyPresent.contains(line))
                    resultBuilder.append(line);
            }

            alreadyPresent.add(line);
        }
        return lines.length - resultBuilder.toString().split("\n").length;
    }

    static class FileIOTools {
        public static boolean writeExternalPublic(Context context, String filename, String content, boolean append) {
            File file;
            File path;
            try {
                if ("huawei".equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
                    file = new File("/data/user/0/" + context.getPackageName() + "/files/", filename);
                } else {
                    path = context.getExternalFilesDir(null);
                    file = new File(path, filename);
                }
                Log.d("FILETEST_", file.getAbsolutePath() + " _ " + file.length());
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
                if ("huawei".equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
                    file = new File("/data/user/0/" + context.getPackageName() + "/files/", filename);
                } else {
                    path = context.getExternalFilesDir(null);
                    file = new File(path, filename);
                }
                return Files.toString(file, Charset.forName("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }


        public static long getFileSize(File file) {
            return file.length();
        }

        public static boolean isFileExists(File file) {
            return file.exists();
        }

        public static boolean createDirectory(File path, String name) {
            File directory = new File(path + File.separator + name);
            return directory.mkdirs() == true;
        }

        public static void saveSharedPref(Context ctx, String key, String value) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            editor.apply();
        }

        public static String getSharedPref(Context ctx, String key) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
            String value = preferences.getString(key, "");
            return value;
        }

        public static void appendSharedPref(Context ctx, String key, String value) {
            saveSharedPref(ctx, key, getSharedPref(ctx, key) + "\n" + value);
        }

        public static void removeSharedPref(Context ctx) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
            preferences.edit().clear().commit();
            preferences.getAll().clear();
            preferences.edit().remove("jsontest");
        }


        public static Map<String, ?> getAllKeysOfSharedPreferences(Context c) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
            return preferences.getAll();
        /*
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }
        */
        }
    }

    public static final List<Intent> POWERMANAGER_INTENTS = Arrays.asList(
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity" : "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerSaverModeActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerConsumptionActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),

            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.autostart.AutoStartActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"))
                    .setData(android.net.Uri.parse("mobilemanager://function/entry/AutoStart")),
            new Intent().setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.security.SHOW_APPSEC")).addCategory(Intent.CATEGORY_DEFAULT).putExtra("packageName", BuildConfig.APPLICATION_ID)
    );

    public void setPowerManagementStuff(Context context) {
        for (Intent intent : POWERMANAGER_INTENTS)
            if (context.getPackageManager().
                    resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                // show dialog to ask user action
                context.startActivity(intent);
                break;
            }
    }

    public static Spanned getPlainText(String html) {

        return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT);

        //return Jsoup.parse(html).text();
        // for tags only
        //return html.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
    }


    static class TimeElapsedUtil {


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
            if (Build.FINGERPRINT.startsWith("generic")
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
                    || "Android".equals(networkOperator)) {
                return true;
            } else {
                return false;
            }
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
                if (in.readLine() != null) return true;
                return false;
            } catch (Throwable t) {
                return false;
            } finally {
                if (process != null) process.destroy();
            }
        }
    }

    public static String getJsonConverted(String str) {
        try {
            int lines = str.split("\r\n|\r|\n").length;
            BufferedReader bufReader = new BufferedReader(new StringReader(str));
            int lineCounter = 0;
            String newjson = null;
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = bufReader.readLine()) != null) {
                if (lineCounter == 0) {
                    sb.append("[" + line).append(",");
                }
                if (lineCounter == lines - 1) {
                    sb.append(line + "]");
                }
                if (lineCounter > 0 && lineCounter != lines - 1) {
                    sb.append(line + ",");
                }
                newjson = sb.toString();
                lineCounter++;
            }
            String a = newjson.replace("\n", "").replace("\r", "");
            return a;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getJsonConverted2(String str) {
        try {
            int lines = str.split("\r\n|\r|\n").length;
            BufferedReader bufReader = new BufferedReader(new StringReader(str));
            int lineCounter = 0;
            String newjson = null;
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = bufReader.readLine()) != null) {
                sb.append("[");
                sb.append(line);
                sb.append("]");
                newjson = sb.toString();
                lineCounter++;
            }
            //String a = newjson.replace("\n", "").replace("\r", "");
            return newjson;
        } catch (Exception e) {
            return null;
        }
    }

    static class BluetoothThings {

        interface BluetoothDeviceListener {

            void found(BluetoothDevice device);

            void found(String receivedStr);

            void found(BluetoothDevice device, int rssi);

            void found(int callbackType, ScanResult result);
        }

        static List<BluetoothDeviceListener> listeners = new ArrayList<BluetoothDeviceListener>();
        static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        public void addListener(BluetoothDeviceListener toAdd) {
            listeners.add(toAdd);
        }

        static class IOBLUETOOTH {
            OutputStream outputStream;
            InputStream inputStream;

            List<BluetoothDeviceListener> listeners = new ArrayList<BluetoothDeviceListener>();

            public void addListener(BluetoothDeviceListener toAdd) {
                listeners.add(toAdd);
            }

            private void setDiscoverable(Context ctx) {
                if (mBluetoothAdapter.getScanMode() !=
                        BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    ctx.startActivity(discoverableIntent);
                }
            }

            public void iOBluetooth(int position) {
                try {
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
                    if (bondedDevices.size() > 0) {
                        Object[] devices = (Object[]) bondedDevices.toArray();
                        BluetoothDevice device = (BluetoothDevice) devices[position];
                        ParcelUuid[] uuids = device.getUuids();
                        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                        socket.connect();
                        outputStream = socket.getOutputStream();
                        inputStream = socket.getInputStream();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void send(String s) throws IOException {
                outputStream.write(s.getBytes());
            }

            public void infiniteListen() {
                final int BUFFER_SIZE = 1024;
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytes = 0;
                int b = BUFFER_SIZE;

                while (true) {
                    try {
                        //bytes = inputStream.read(buffer, bytes, BUFFER_SIZE - bytes);
                        String received = IOUtils.toString(inputStream, "UTF-8");
                        for (BluetoothDeviceListener hl : listeners) {
                            hl.found(received);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        public static void stopBLscan(Context c) {
            mBluetoothAdapter.cancelDiscovery();
        }

        public static void startBLscan(Context c) {
            if (mBluetoothAdapter == null) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            mBluetoothAdapter.startDiscovery();
            BroadcastReceiver mReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        for (BluetoothDeviceListener hl : listeners) {
                            hl.found(device);
                        }
                    } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        Log.v("BLUETOOTH_CLASSIC", "SCAN FINISHED");
                        mBluetoothAdapter.startDiscovery(); // -> Scan again
                    }
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

            c.getApplicationContext().registerReceiver(mReceiver, filter);
        }

        public static void startBLLEscan(Context c) {

            BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
            ScanCallback scb = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    Log.i("BLUETOOTH", "Remote device name: " + result.getDevice().getName());
                    for (BluetoothDeviceListener hl : listeners) {
                        hl.found(callbackType, result);
                    }
                }
            };
            ba.getBluetoothLeScanner().startScan(scb);
            // mBluetoothAdapter.startLeScan(scb);
        }

        /*From old*/
        public static boolean isBLDevicePaired(BluetoothDevice device) {
            BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> list = ba.getBondedDevices();
            Log.d("BLUETOOTH_LIBRARY_", "Paired count: " + list.size());
            for (BluetoothDevice dev : list) {
                return device.getAddress() == dev.getAddress();
            }
            return false;
        }

        /*From old*/
        public static Set<BluetoothDevice> getPaired() {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            return mBluetoothAdapter.getBondedDevices();
        }

        /*From old*/
        public static void native_BL() {
            BluetoothSocket socket = null;
            try {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                //bluetoothAdapter.startDiscovery();
                bluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                        try {
                            Log.d("BL_NA", "onLeScan -> name: " + device.getName() + "_ uuid: " + device.getUuids()[0] + "_ rssi: " + rssi + "_ len: " + scanRecord.length);
                        } catch (Exception e) {
                        }
                    }
                });
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                for (BluetoothDevice device : pairedDevices) {
                    //Log.d("BL_NATIVE", "debug0_" + device.getName() + " - " + device.getAddress());
                    Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
                    ParcelUuid[] uuids = (ParcelUuid[]) getUuidsMethod.invoke(bluetoothAdapter, null);

                    //socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                    //socket.connect();
                    //InputStream is = socket.getInputStream();
                    //OutputStream os = socket.getOutputStream();
                    //String debug = null;
                    //String a = IOUtils.toString(is);
                    //Log.d("BL_NATIVE", "debug1_" + debug);
                    //Log.d("BL_NATIVE", "debug2_" + a);
                }
            } catch (Exception e) {
                Log.d("BL_NATIVE", "error " + e.getMessage());
                try {
                    socket.close();
                    native_BL();
                } catch (Exception e2) {
                    Log.d("BL_NATIVE", "error_2 " + e.getMessage());
                    e2.printStackTrace();
                }
                e.printStackTrace();
            }
        }

        private static final UUID MY_UUID_INSECURE =
                UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

        private static final int REQUEST_ENABLE_BT = 1;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        private static BluetoothDevice mmDevice;
        private static UUID deviceUUID;
        static ConnectedThread mConnectedThread;
        private Handler handler;

        static String TAG = "BLUETOOTH__TEST";
        //EditText send_data;
        //TextView view_data;
        StringBuilder messages;

        class AcceptThread extends Thread {

            // The local server socket
            private final BluetoothServerSocket mmServerSocket;

            public AcceptThread() {
                BluetoothServerSocket tmp = null;

                // Create a new listening server socket
                try {
                    tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("appname", MY_UUID_INSECURE);

                    Log.d("BLUETOOTH__TEST", "AcceptThread: Setting up Server using: " + MY_UUID_INSECURE);
                } catch (IOException e) {
                    Log.e("BLUETOOTH__TEST", "AcceptThread: IOException: " + e.getMessage());
                }

                mmServerSocket = tmp;
            }

            public void run() {
                Log.d(TAG, "run: AcceptThread Running.");

                BluetoothSocket socket = null;

                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    Log.d(TAG, "run: RFCOM server socket start.....");

                    socket = mmServerSocket.accept();

                    Log.d(TAG, "run: RFCOM server socket accepted connection.");

                } catch (IOException e) {
                    Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
                }

                //talk about this is in the 3rd
                if (socket != null) {
                    connected(socket);
                }

                Log.i(TAG, "END mAcceptThread ");
            }

            public void cancel() {
                Log.d(TAG, "cancel: Canceling AcceptThread.");
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage());
                }
            }

        }

        static class ConnectThread extends Thread {
            private BluetoothSocket mmSocket;

            public ConnectThread(BluetoothDevice device, UUID uuid) {
                Log.d(TAG, "ConnectThread: started.");
                mmDevice = device;
                deviceUUID = uuid;
            }

            public void run() {
                BluetoothSocket tmp = null;
                Log.i(TAG, "RUN mConnectThread ");

                // Get a BluetoothSocket for a connection with the
                // given BluetoothDevice
                try {
                    Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: "
                            + MY_UUID_INSECURE);
                    tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                } catch (IOException e) {
                    Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
                }

                mmSocket = tmp;

                // Make a connection to the BluetoothSocket

                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    mmSocket.connect();

                } catch (IOException e) {
                    // Close the socket
                    try {
                        mmSocket.close();
                        Log.d(TAG, "run: Closed Socket.");
                    } catch (IOException e1) {
                        Log.e(TAG, "mConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                    }
                    Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + MY_UUID_INSECURE);
                }

                //will talk about this in the 3rd video
                connected(mmSocket);
            }

            public void cancel() {
                try {
                    Log.d(TAG, "cancel: Closing Client Socket.");
                    mmSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
                }
            }
        }

        private static void connected(BluetoothSocket mmSocket) {
            Log.d(TAG, "connected: Starting.");

            // Start the thread to manage the connection and perform transmissions
            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();
        }

        static class ConnectedThread extends Thread {
            private final BluetoothSocket mmSocket;
            private final InputStream mmInStream;
            private final OutputStream mmOutStream;

            public ConnectedThread(BluetoothSocket socket) {
                Log.d(TAG, "ConnectedThread: Starting.");

                mmSocket = socket;
                InputStream tmpIn = null;
                OutputStream tmpOut = null;


                try {
                    tmpIn = mmSocket.getInputStream();
                    tmpOut = mmSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mmInStream = tmpIn;
                mmOutStream = tmpOut;
            }

            public void run() {
                byte[] buffer = new byte[1024];  // buffer store for the stream

                int bytes; // bytes returned from read()

                // Keep listening to the InputStream until an exception occurs
                while (true) {
                    // Read from the InputStream
                    try {
                        bytes = mmInStream.read(buffer);
                        final String incomingMessage = new String(buffer, 0, bytes);
                        Log.d(TAG, "InputStream: " + incomingMessage);
                    } catch (IOException e) {
                        Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
                        break;
                    }
                }
            }


            public void write(byte[] bytes) {
                String text = new String(bytes, Charset.defaultCharset());
                Log.d(TAG, "write: Writing to outputstream: " + text);
                try {
                    mmOutStream.write(bytes);
                } catch (IOException e) {
                    Log.e(TAG, "write: Error writing to output stream. " + e.getMessage());
                }
            }

            /* Call this from the main activity to shutdown the connection */
            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                }
            }
        }


        public void SendMessage(View v) {
            byte[] bytes = "hello".getBytes(Charset.defaultCharset());
            mConnectedThread.write(bytes);
        }

    }

    static class galleryImages {
        static String CAMERA_IMAGE_BUCKET_NAME = Environment.getExternalStorageDirectory().toString()
                + "/DCIM/Camera";
        static String CAMERA_IMAGE_BUCKET_ID = getBucketId(CAMERA_IMAGE_BUCKET_NAME);

        public static String getBucketId(String path) {
            return String.valueOf(path.toLowerCase().hashCode());
        }

        public static List<String> getCameraImages(Context context) {
            final String[] projection = {MediaStore.Images.Media.DATA};
            final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
            final String[] selectionArgs = {CAMERA_IMAGE_BUCKET_ID};
            final Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null);
            ArrayList<String> result = new ArrayList<String>(cursor.getCount());
            if (cursor.moveToFirst()) {
                final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                do {
                    final String data = cursor.getString(dataColumn);
                    result.add(data);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return result;
        }

        public static List<String> getGallery(Context ctx) {
            List<String> list = new ArrayList<String>();
            list = getCameraImages(ctx);
            long allsize = 0;
            for (String s : list) {
                File f = new File(s);
                allsize = allsize + f.length();
            }
            return list;
        }
    }

    static class NsdHelper {

        private static final String TAG = "NsdHelper";
        private static final String SERVICE_NAME = "NsdService";
        private static final String SERVICE_TYPE = "_http._tcp.";


        private boolean discovering = false;
        private boolean registered = false;

        private final Context mContext;
        private NsdServiceInfo mServiceInfo = null;
        private NsdManager mNsdManager;
        private MyRegistrationListener myRegistrationListener;
        private MyDiscoveryListener myDiscoveryListener;
        private MyResolveListener myResolveListener;
        private String mServiceName;

        NsdHelper(Context context) {
            mContext = context;
            mNsdManager = (NsdManager) mContext.getSystemService(Context.NSD_SERVICE);
        }

        void registerService(int port) {
            NsdServiceInfo serviceInfo = new NsdServiceInfo();
            serviceInfo.setServiceName(SERVICE_NAME);
            serviceInfo.setServiceType(SERVICE_TYPE);
            serviceInfo.setPort(port);

            mNsdManager = (NsdManager) mContext.getSystemService(Context.NSD_SERVICE);

            mNsdManager.registerService(
                    serviceInfo, NsdManager.PROTOCOL_DNS_SD, myRegistrationListener);

            mServiceInfo = serviceInfo;
        }

        void initListeners() {
            initRegistrationListener();
            initDiscoveryListener();
            initResolveListener();

        }

        private void initResolveListener() {
            myResolveListener = new MyResolveListener();
        }

        private void initRegistrationListener() {
            myRegistrationListener = new MyRegistrationListener();
        }

        private void initDiscoveryListener() {
            myDiscoveryListener = new MyDiscoveryListener();
        }

        NsdServiceInfo getChosenServiceInfo() {
            return mServiceInfo;
        }

        void discoverServices() {
            if (!isDiscovering() && mNsdManager != null) {
                Log.i(TAG, "Starting Discovery");
                mNsdManager.discoverServices(
                        SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, myDiscoveryListener);
                discovering = true;
            }
        }

        void stopDiscovery() {
            if (isDiscovering()) {
                Log.i(TAG, "Stopping Discovery");
                mNsdManager.stopServiceDiscovery(myDiscoveryListener);
                discovering = false;
            }
        }

        boolean isDiscovering() {
            return discovering;
        }

        public void tearDown() {
            mNsdManager.unregisterService(myRegistrationListener);
        }

        boolean isRegistered() {
            return registered;
        }

        private class MyRegistrationListener implements NsdManager.RegistrationListener {
            @Override
            public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Log.e(TAG, "Registration Failed");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Log.e(TAG, "unregistration Failed");
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
                mServiceInfo = nsdServiceInfo;
                mServiceName = nsdServiceInfo.getServiceName();
                registered = true;
                Log.i(TAG, "Registration Success");
                // Toast.makeText(mContext, "Registered : " + nsdServiceInfo.getServiceName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {
                registered = false;
                Log.i(TAG, "Unregistered");
            }
        }

        private class MyDiscoveryListener implements NsdManager.DiscoveryListener {
            @Override
            public void onStartDiscoveryFailed(String s, int i) {
                Log.i(TAG, "Start Discovery Failed");
            }

            @Override
            public void onStopDiscoveryFailed(String s, int i) {
                Log.i(TAG, "Stop Discovery Failed");
            }

            @Override
            public void onDiscoveryStarted(String s) {
                // Toast.makeText(mContext, "Discovery Started", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Discovery Started");
            }

            @Override
            public void onDiscoveryStopped(String s) {
                Log.i(TAG, "Discovery Stopped");
                // Toast.makeText(mContext, "Discovery Stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceFound(NsdServiceInfo nsdServiceInfo) {
                Log.i(TAG, "Service Discovered " + nsdServiceInfo.getServiceName());
                // Toast.makeText(mContext, "Discovery Found " + nsdServiceInfo.getServiceName(), Toast.LENGTH_SHORT).show();

                if (!nsdServiceInfo.getServiceType().equals(SERVICE_TYPE)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: " + nsdServiceInfo.getServiceType());
                } else if (nsdServiceInfo.getServiceName().equals(mServiceName)) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d(TAG, "Same machine: " + mServiceName);
                    myResolveListener = new MyResolveListener();
                    mNsdManager.resolveService(nsdServiceInfo, myResolveListener);
                } else if (nsdServiceInfo.getServiceName().contains("NsdChat")) {
                    myResolveListener = new MyResolveListener();
                    mNsdManager.resolveService(nsdServiceInfo, myResolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo nsdServiceInfo) {
                Log.i(TAG, "Service Lost");
            }
        }

        private class MyResolveListener implements NsdManager.ResolveListener {
            @Override
            public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Log.i(TAG, "Service Resolution Failed");
            }

            @Override
            public void onServiceResolved(NsdServiceInfo nsdServiceInfo) {
                Log.i(TAG, "Service Resolved");
                mServiceInfo = nsdServiceInfo;
                if (nsdServiceInfo.getServiceName().equals(mServiceName)) {
                    Log.d(TAG, "Same IP.");
                    // Toast.makeText(mContext, "LocalHost : " + nsdServiceInfo.getPort(), Toast.LENGTH_SHORT).show();
                    return;
                }
                // Toast.makeText(mContext, "Service Resolved : " + nsdServiceInfo.getHost() + " : " + nsdServiceInfo.getPort(), Toast.LENGTH_SHORT).show();
                mServiceInfo = nsdServiceInfo;
                int port = mServiceInfo.getPort();
                InetAddress host = mServiceInfo.getHost();
                Log.i(TAG, host + " " + port);
            }
        }
    }

    static class WiFip2p {
        IntentFilter _wifip2p_intentFilter = new IntentFilter();
        BroadcastReceiver _wifip2p_broadcastReceiver;
        WifiP2pManager _wifip2p_manager;
        WifiP2pManager.Channel _wifip2p_channel;
        List _wifip2p_peers = new ArrayList();
        WifiP2pManager.PeerListListener _wifip2p_peerListListener;

        void init(Context c) {
            _wifip2p_intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            _wifip2p_intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            _wifip2p_intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            _wifip2p_intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

            _wifip2p_manager = (WifiP2pManager) c.getSystemService(Context.WIFI_P2P_SERVICE);
            _wifip2p_channel = _wifip2p_manager.initialize(c, Looper.getMainLooper(), new WifiP2pManager.ChannelListener() {
                @Override
                public void onChannelDisconnected() {
                    Log.d("WIFI_P2P_", "channel disconnected");
                }
            });
            c.registerReceiver(_wifip2p_broadcastReceiver, _wifip2p_intentFilter);

            _wifip2p_manager.discoverPeers(_wifip2p_channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d("WIFI_P2P_", "success");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d("WIFI_P2P_", "fail " + reason);
                }
            });
        }

        private void connectTo(WifiP2pDevice peerDevice, WifiP2pManager.Channel channel) {
            WifiP2pConfig config = new WifiP2pConfig();

            config.deviceAddress = peerDevice.deviceAddress;

            _wifip2p_manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d("WIFI_P2P_", "success");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d("WIFI_P2P_", "fail " + reason);
                }
            });
        }

    }

    private void shareFile(Context c, File file, String title) {

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));
        intentShareFile.putExtra(Intent.EXTRA_STREAM,
                Uri.parse("file://" + file.getAbsolutePath()));

        //if you need
        //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
        //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");

        c.startActivity(Intent.createChooser(intentShareFile, title));

    }

    public static void rootCommand(String command) {
        try {
            Process su = Runtime.getRuntime().exec("su");
            String cmd = command + " \n" + "exit\n";
            su.getOutputStream().write(cmd.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String runAdbCommand(String command) {
        Process logcat;
        String str = "";
        try {
            logcat = Runtime.getRuntime().exec(new String[]{
                    "ping google.com", ""
            });
            BufferedReader br = new BufferedReader(new InputStreamReader(logcat.getInputStream()), 4 * 1024);
            String line;

            while ((line = br.readLine()) != null) {
                str += line + "\n";
            }
        } catch (Exception e) {

        }
        return str;
    }

    public static String getCurrentTimeHumanReadable() {
        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy.MM.dd HH:mm:ss",
                Locale.getDefault());
        return sdf.format(new Date());
    }

    public static String getCurrentOnlyTimeHumanReadable() {
        SimpleDateFormat sdf = new SimpleDateFormat(
                "HH:mm:ss",
                Locale.getDefault());

        return sdf.format(new Date());
    }

    public static String getPhoneNumber(Context c) {
        TelephonyManager tMgr = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        if (c.checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                c.checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                c.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "Unknown";
        } else {
            if (tMgr.getLine1Number() != null) {
                return tMgr.getLine1Number();
            } else if (tMgr.getSimSerialNumber() != null) {
                return tMgr.getSimSerialNumber();
            }
        }
        return "Unknown";
    }

    public static String getCallLog(Context ctx) {
        if (ctx.checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
        }
        Cursor cursor = ctx.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                null, null, null, CallLog.Calls.DATE + " DESC");
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
        String log = "";
        while (cursor.moveToNext()) {
            String phNumber = cursor.getString(number);
            String callType = cursor.getString(type);
            String callDate = cursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = cursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            log += ("\nPhone Number: " + phNumber +
                    "\nCall Type: " + dir +
                    "\nCall Date: " + callDayTime
                    + "\nCall duration in sec: " + callDuration);
            log += ("\n\n");
        }
        cursor.close();
        return log;
    }

    public static HashMap<String, String> getAllSMS(Context c) {
        HashMap<String, String> sms = new HashMap<String, String>();
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = c.getContentResolver().query(uriSMSURI, null, null, null, null);
        while (cur != null && cur.moveToNext()) {
            String address = cur.getString(cur.getColumnIndex("address"));
            String body = cur.getString(cur.getColumnIndexOrThrow("body"));
            sms.put(address, body);
        }
        if (cur != null) {
            cur.close();
        }
        return sms;
        /* How to iterate it
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
        }
        */
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

    public static boolean isValidEmail(String target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidPhoneNumber(String target) {
        return target.matches("^[+]?[0-9]{10,13}$");
    }

    public static int HEXtoColor(String colorStr) {
        // eg: #fef6fb or Capital
        int a = Color.parseColor(colorStr);
        return a;
    }

    public static int[] getScreenCenter(Context c) {
        int mWidth = c.getResources().getDisplayMetrics().widthPixels;
        int mHeight = c.getResources().getDisplayMetrics().heightPixels;
        return new int[]{mWidth, mHeight};
    }

    public static long[] getTrafficStats(Context c) {
        int i = c.getApplicationInfo().uid;
        long apprx = TrafficStats.getUidRxBytes(i);
        long apptx = TrafficStats.getUidTxBytes(i);
        long app_r_pa = TrafficStats.getUidRxPackets(i);
        long app_s_pa = TrafficStats.getUidTxPackets(i);
        return new long[]{apprx, apptx, app_r_pa, app_s_pa};
    }


    public static void blinkLed(Context c, int duration) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    CameraManager camManager = (CameraManager) c.getSystemService(Context.CAMERA_SERVICE);

                    boolean flashAvailable = camManager
                            .getCameraCharacteristics("0")
                            .get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    //Log.d("FLASH_CAMERA","enabled? " + flashAvailable);

                    String cameraId = null;
                    try {
                        cameraId = camManager.getCameraIdList()[0];
                        camManager.setTorchMode(cameraId, true);   //Turn ON
                        Thread.sleep(duration);
                        camManager.setTorchMode(cameraId, false);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    public static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    public static boolean isBetweenHours(int start, int stop) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return hour <= 23 && hour >= 8;
    }

    public static long getBatteryCapacity(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager mBatteryManager = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            Integer chargeCounter = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            Integer capacity = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

            if (chargeCounter == Integer.MIN_VALUE || capacity == Integer.MIN_VALUE)
                return 0;

            return (chargeCounter / capacity) * 100;
        }
        return 0;
    }

    public static double getBatteryCapacity_2(Context context) {
        Object mPowerProfile;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(context);

            batteryCapacity = (double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return batteryCapacity;
    }

    public static void createNotifGroup(Context ctx, String id, String name) {
        NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);

        NotificationChannelGroup notificationChannelGroup;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannelGroup = new NotificationChannelGroup(id, name);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                notificationChannelGroup.setDescription("SONTY NOTIF CHANNEL");
            }
            notificationManager.createNotificationChannelGroup(notificationChannelGroup);
        }
    }

    public static byte[] inputStreamToByteArray_istoba(InputStream is) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toByteArray();
    }

    public static boolean isBatteryCharging(Context context) {
        // Check battery sticky broadcast
        final Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return (batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1) == BatteryManager.BATTERY_STATUS_CHARGING);
    }

    public static int getBatteryLevel(Context ctx) {
        BatteryManager bm = (BatteryManager) ctx.getSystemService(BATTERY_SERVICE);
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

/////////////////////

    public static class AudioTools {

        public static void play(short[] audio) {
            AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, 8000 * 10, AudioTrack.MODE_STREAM);
            track.write(audio, 0, audio.length);
            track.stop();
            track.release();

        }

        public static void recordAndPlay(Context ctx) {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

            AudioRecord recorder = null;
            AudioTrack track = null;
            short[][] buffers = new short[256][160];
            int ix = 0;
            String audio = null;

            try {
                int N = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, N * 10);
                track = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, N * 10, AudioTrack.MODE_STREAM);
                recorder.startRecording();
                track.play();
                while (true) {
                    short[] buffer = buffers[ix++ % buffers.length];
                    N = recorder.read(buffer, 0, buffer.length);
                    track.write(buffer, 0, buffer.length);
                    for (int i = 0; i < buffers.length; i++) {
                        for (int j = 0; j < buffers[i].length; j++) {
                            //Log.d("audio_stream","buffers _ " + buffers.length + " - > " + buffers[i][j]);
                            audio = audio + buffers[i][j];

                        }
                    }
                    play(buffer);
                    //if(audio.length() >= 8000)
                    //play(audio.toCharArray());
                    //Log.d("audio_stream","file size: " + mp3.length()/1024 + " kb");

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                recorder.stop();
                recorder.release();
                track.stop();
                track.release();

            }
        }

        public static void record_play(Activity ctx) {
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ctx, new String[]{Manifest.permission.RECORD_AUDIO}, 2);
            }
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.CAPTURE_AUDIO_OUTPUT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ctx, new String[]{Manifest.permission.CAPTURE_AUDIO_OUTPUT}, 1);
            }
            Thread streamThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                        ParcelFileDescriptor[] descriptors = ParcelFileDescriptor.createPipe();
                        ParcelFileDescriptor parcelRead = new ParcelFileDescriptor(descriptors[0]);
                        ParcelFileDescriptor parcelWrite = new ParcelFileDescriptor(descriptors[1]);

                        InputStream inputStream = new ParcelFileDescriptor.AutoCloseInputStream(parcelRead);

                        MediaRecorder recorder = new MediaRecorder();
                        // recorder.release();
                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        recorder.setOutputFile(parcelWrite.getFileDescriptor());
                        recorder.prepare();
                        recorder.start();

                        int read;
                        byte[] data = new byte[8000];
                        while ((read = inputStream.read(data, 0, data.length)) != -1) {
                            Log.d("audio_stream", "stream: " + read + " -> " + data.length / 1024 + " kb");
                            byteArrayOutputStream.write(data, 0, read);
                        }

                        byte[] sound = byteArrayOutputStream.toByteArray();

                        File path = new File(ctx.getCacheDir() + "/wifisound.mp3");

                        FileOutputStream fos = new FileOutputStream(path);
                        fos.write(sound);
                        fos.close();

                        MediaPlayer mediaPlayer = new MediaPlayer();

                        FileInputStream fis = new FileInputStream(path);
                        mediaPlayer.setDataSource(ctx.getCacheDir() + "/wifisound.mp3");

                        mediaPlayer.prepare();
                        mediaPlayer.start();

                        byteArrayOutputStream.flush();
                    } catch (Exception e) {
                        Log.d("audio_stream", "error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
            streamThread.start();
        }

    }

    public static class ImageTools {
        /**
         * For STREAMING
         *
         * @param bitmap
         * @return
         */
        public static ByteArrayInputStream BitmapToByteArrayInputStream(Bitmap bitmap) {
            int byteSize = bitmap.getRowBytes() * bitmap.getHeight();
            ByteBuffer byteBuffer = ByteBuffer.allocate(byteSize);
            bitmap.copyPixelsToBuffer(byteBuffer);

            byte[] byteArray = byteBuffer.array();
            return new ByteArrayInputStream(byteArray);
        }

        public static byte[] BitmapToByteArrayInputStream_2(Bitmap bitmap) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        }

        public static Bitmap byteArrayToBitmap(byte[] imagedata) {
            return BitmapFactory.decodeByteArray(imagedata, 0, imagedata.length);
        }
    }

    public static class Crypt {

        public final static String TOKEN_KEY = "fqJfdzGDvfwbedsKSUGty3VZ9taXxMVw";

        public static String encrypt(String plain) {
            try {
                byte[] iv = new byte[16];
                new SecureRandom().nextBytes(iv);
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(TOKEN_KEY.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec(iv));
                byte[] cipherText = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
                byte[] ivAndCipherText = getCombinedArray(iv, cipherText);
                return encodeToString(ivAndCipherText, NO_WRAP);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public static String decrypt(String encoded) {
            try {
                byte[] ivAndCipherText = decode(encoded, NO_WRAP);
                byte[] iv = Arrays.copyOfRange(ivAndCipherText, 0, 16);
                byte[] cipherText = Arrays.copyOfRange(ivAndCipherText, 16, ivAndCipherText.length);

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(TOKEN_KEY.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec(iv));
                return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private static byte[] getCombinedArray(byte[] one, byte[] two) {
            byte[] combined = new byte[one.length + two.length];
            for (int i = 0; i < combined.length; ++i) {
                combined[i] = i < one.length ? one[i] : two[i - one.length];
            }
            return combined;
        }

    }

    static class Compression {
        public static byte[] GZIPCompress(byte[] uncompressedData) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                //GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                MyGZIPOutputStream gzipOutputStream = new MyGZIPOutputStream(byteArrayOutputStream);
                gzipOutputStream.setLevel(Deflater.BEST_COMPRESSION);
                gzipOutputStream.write(uncompressedData);
                gzipOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Log.d("COMPRESSION_", "Ratio: " + 1.0f * byteArrayOutputStream.size() / uncompressedData.length);
            return byteArrayOutputStream.toByteArray();
        }

        public static byte[] GZIPDEcompress(byte[] compressedData) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(compressedData)), out);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return out.toByteArray();
        }

        public static String decompress_GZIP(byte[] str) throws Exception {
            if (str == null) {
                return null;
            }
            GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str));
            BufferedReader bf = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8));
            String outStr = "";
            String line;
            while ((line = bf.readLine()) != null) {
                outStr += line;
            }
            Log.d("Compress_", "output str length: " + outStr.length());
            return outStr;
        }

        public static String decompress_GZIP_string(String str) throws Exception {
            if (str == null) {
                return null;
            }
            //byte[] strr = str.getBytes();
            byte[] strr = str.getBytes(StandardCharsets.UTF_8);
            GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(strr));
            BufferedReader bf = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8));
            String outStr = "";
            String line;
            while ((line = bf.readLine()) != null) {
                outStr += line;
            }
            Log.d("Compress_", "output str length: " + outStr.length());
            return outStr;
        }

        public static byte[] compress_GZIP(String str) {
            ByteArrayOutputStream obj;
            try {
                if (str == null || str.length() == 0) {
                    return null;
                }
                Log.d("Compress_", "output str length: " + str.length());
                obj = new ByteArrayOutputStream();
                GZIPOutputStream gzip = new GZIPOutputStream(obj);
                gzip.write(str.getBytes(StandardCharsets.UTF_8));
                gzip.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return obj.toByteArray();
        }

    }

    public static void takeFullScreenshot(Context ctx, Activity act) {
        MediaProjectionManager mgr = (MediaProjectionManager) ctx.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        act.startActivityForResult(mgr.createScreenCaptureIntent(), 5000);
    }

    public static void concurrentJob(Callable<Object> func, int allReqCount, int paralell) {
        Thread th2 = new Thread() {
            public void run() {
                try {
                    int allRequestsCount = allReqCount;
                    int parallelism = paralell;

                    ForkJoinPool forkJoinPool = new ForkJoinPool(parallelism);
                    IntStream.range(0, parallelism).forEach(i -> forkJoinPool.submit(() -> {
                        int chunkSize = allRequestsCount / parallelism;
                        IntStream.range(i * chunkSize, i * chunkSize + chunkSize)
                                .forEach(num -> {
                                    try {
                                        func.call();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                    }));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        th2.start();


    }

    public static String byteArrayToString(byte[] barr) {
        String str = null;
        try {
            str = new String(barr, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
        //return new String(barr);
    }

    public static boolean isWifiConnected(Context ctx) {
        ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public static boolean isWifiConnecting(Context ctx) {
        ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMob = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean connecting;
        return (!mWifi.isConnected()) && (mWifi.isConnectedOrConnecting());
    }

    public static void requestPermissions(Activity act) {
        ActivityCompat.requestPermissions(act,
                new String[]{
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH_PRIVILEGED,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, 1);
    }

    public static String getDomainFromURL(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public static boolean isValidURL(String url) {
        Pattern p = Pattern.compile("^(https?:\\/\\/)?([\\w\\Q$-_+!*'(),%\\E]+\\.)+(\\w{2,63})(:\\d{1,4})?([\\w\\Q/$-_+!*'(),%\\E]+\\.?[\\w])*\\/?$");
        Matcher m = p.matcher(url);
        return m.find();

    }


    public static void playTone() {
        Thread thread = new Thread() {
            public void run() {
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
            }
        };
        thread.start();
    }

    public static void vibrate(Context ctx, int amplitude, int time) {
        Thread thread = new Thread() {
            public void run() {
                Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(time, amplitude));
                } else {
                    v.vibrate(50);
                }
            }
        };
        thread.start();
    }

    public static boolean zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;

        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length());
            } else {
                byte[] data = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(sourcePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                entry.setTime(sourceFile.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.close();
        } catch (Exception e) {
            //e.printStackTrace();
            Log.d("ZIP_", "DONE_error" + e.getMessage());
            return false;
        }
        Log.d("ZIP_", "DONE");
        return true;
    }

    private static void zipSubFolder(ZipOutputStream out, File folder,
                                     int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte[] data = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                entry.setTime(file.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    public static String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }

    public static int roundFloat(float f) {
        int c = (int) ((f) + 0.5f);
        float n = f + 0.5f;
        return (n - c) % 2 == 0 ? (int) f : c;
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

    public static int invertColor(int color) {
        return color ^ 0x00ffffff;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    /*public static String round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return String.valueOf((double) tmp / factor);
    }*/

    public static double mpsTokmh(double mps) {
        return mps * 3.6;
    }

    public static String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy.MM.dd. HH:mm:ss");
        return format.format(date);
    }

    public static double getDistance(double lat1, double lat2, double lon1, double lon2) {

        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        double el1 = 0;
        double el2 = 0;
        double height = el1 - el2;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        return Math.sqrt(distance);
    }

    public static String getCompleteAddressString(Context ctx, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("");
                }
                strAdd = strReturnedAddress.toString();
            } else {
            }
        } catch (Exception e) {
            Log.d("Error_", e.toString());
        }
        return strAdd;
    }

    public static void showToast(Context ctx, String text) {
        if (ctx == null) {
            ctx = ctx;
        }
        String id = Settings.Secure.getString(ctx.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (id.equals("73bedfbd149e01de")) {
            Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
        }

    }

    public static String getLocalIpAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0;
                        if (isIPv4)
                            return sAddr;
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    public static String chk_3g_wifi(Context ctx) {
        final ConnectivityManager connMgr = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting()) {
            return "wifi";
        } else if (mobile.isConnectedOrConnecting()) {
            return "3g";
        } else {
            return "no";
        }
    }

    public static Bitmap reduceBitmapQuality(Bitmap bitmap, int quality) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.PNG, quality, out);
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
        Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        return decoded;
    }

    public static boolean check_if_local(Context ctx) {
        Log.d("LAN_", String.valueOf(System.currentTimeMillis()));
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid = info.getSSID();
        if (ssid.contains("UPCAED")) {
            Log.d("LAN_", String.valueOf(System.currentTimeMillis()));
            return true;
        } else {
            Log.d("LAN_", String.valueOf(System.currentTimeMillis()));
            return false;
        }
    }

    public static boolean isNetworkAvailable(Context c) {
        Log.d("NETWORK_", "_" + System.currentTimeMillis());
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Log.d("NETWORK_", "_" + System.currentTimeMillis());
        return activeNetworkInfo != null &&
                activeNetworkInfo.isConnected() &&
                activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * WARNING !! NOT WORKING YET ! UNUSED
     *
     * @param c
     * @return
     */
    public static boolean isNetworkAvailable_2(Context c) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    // NEED TO FIX RETURN VALUE (0)
    public static int stepCounter(Context c) {
        final int[] count = {0};
        SensorManager sensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Log.d("STEP_COUNTER_", "STEP: " + sensor.toString());
        SensorEventListener sel = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                count[0] = (int) event.values[0];
                Log.d("STEP_COUNTER_", "CHANGE: " + "[" + event.values.length + "] " + event.values[0]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        sensorManager.registerListener(sel, sensor, 1000);
        return count[0];
    }

    public static String getCurrentWifiName(Context c) {
        WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        return info.getSSID();
    }

    public static void getUsbDevices(Context c) {
        UsbManager manager = (UsbManager) c.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        Log.d("USB_LIST", "dev list_ " + deviceList.size());
        if (deviceList.size() >= 1) {
            //Toast.makeText(c,"USB device found: " + deviceList.size(),Toast.LENGTH_LONG).show();
        }
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            Toast.makeText(c, "USB Found: " + device.toString(), Toast.LENGTH_LONG).show();
            Log.d("TEST_", "device_" + device.toString());
        }
        //return deviceList;
    }

    public static Bitmap getScreenBitmap(Context c, View view) {

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        view.buildDrawingCache();

        if (view.getDrawingCache() == null) {
            Log.d("STREAMING_", "ERROR NULL !");
            return null;
        }

        Bitmap snapshot = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();

        return snapshot;
    }

    /*
    public static Scalar argbtoScalar(int r, int g, int b, int a) {
        Scalar s = new Scalar(r, b, g, a);
        return s;
    }
    */
    public static byte[] bitmapToArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        bmp.recycle();
        return byteArray;
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    public static Bitmap convertViewToBitmap(View v) {
        Bitmap b = Bitmap.createBitmap(v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    public static Bitmap resize(Bitmap source, int w, int h) {

        float imageRatio = (float) source.getWidth() / (float) source.getHeight();

        int imageViewWidth = w;
        int imageRealHeight = (int) (imageViewWidth / imageRatio);

        Bitmap imageToShow = Bitmap.createScaledBitmap(source, imageViewWidth, imageRealHeight, true);
        return imageToShow;
    }

    public static void turnGPSOn(Context ctx) {
        String provider = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            ctx.sendBroadcast(poke);
        }
    }

    public static void openLocationSettings(Context ctx) {
        Intent intent = new Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        ctx.startActivity(intent);
    }

    public static boolean isLocationServicesEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    public static void requestCameraPermission(Context ctx) {
        if (ctx.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) ctx, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    public static boolean checkLowWifiSignalStr(Context ctx) {
        // ha a jelenleg kapcsolodott wifi jelerosseg NEM kisebb mint x% VAGY NEM KAPCSOLODOTT WIFIHEZ akkor -> TRUE
        // tehát mehet a hálózati forgalom
        ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        if (mWifi.isConnected()) {
            int conn_quality = SontHelperSonty.convertDBM(wifiManager.getConnectionInfo().getRssi());
            return conn_quality >= 30;
        }
        return true;
    }

    public static ArrayList<String> getAllImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String PathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }

    public Bitmap cropBitmapRect(Bitmap bmp, Rect r) {
        return Bitmap.createBitmap(bmp, r.left, r.right, r.width(), r.height());
    }

    public static Bitmap findFaceDrawRectROI(Bitmap bitmap, int maxfaces) {
        Bitmap tempBitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
        Canvas tempCanvas = new Canvas(tempBitmap);
        Log.d("camera_api", "findface()");

        FaceDetector.Face[] faces = new FaceDetector.Face[maxfaces];
        FaceDetector fd = new FaceDetector(tempBitmap.getWidth(), tempBitmap.getHeight(), maxfaces);
        int facesfound = fd.findFaces(tempBitmap, faces);
        for (FaceDetector.Face f : faces) {
            try {
                PointF p = new PointF();
                f.getMidPoint(p);
                Log.d("camera_api", "faces found: " + facesfound + " w: " + p.x + " h: " + p.y);

                Paint.FontMetrics fm = new Paint.FontMetrics();
                Paint paint = new Paint();
                paint.setColor(Color.argb(100, 255, 0, 0));
                paint.setStyle(Paint.Style.FILL);
                paint.setStrokeWidth(15f);
                paint.setTextSize(40);

                Paint circlePaint = new Paint();
                circlePaint.setColor(Color.argb(100, 0, 255, 0));
                circlePaint.setStrokeWidth(10);

                paint.getFontMetrics(fm);

                tempCanvas.drawText(String.valueOf(f.confidence()),
                        tempBitmap.getWidth() / 2,
                        tempBitmap.getHeight() / 2 + -(fm.ascent + fm.descent) / 2, paint);
                tempCanvas.drawCircle(p.x, p.y, 200, circlePaint);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tempBitmap;
    }

    public static Bitmap getCircledBitmap(Bitmap bitmap, PointF point) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(point.x, point.y, bitmap.getWidth() / 3, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap findFaceCropROI(Bitmap bitmap, int maxfaces) {
        Bitmap tempBitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
        Canvas tempCanvas = new Canvas(tempBitmap);
        Log.d("camera_api", "findface()");

        FaceDetector.Face[] faces = new FaceDetector.Face[maxfaces];
        FaceDetector fd = new FaceDetector(tempBitmap.getWidth(), tempBitmap.getHeight(), maxfaces);
        int facesfound = fd.findFaces(tempBitmap, faces);
        for (FaceDetector.Face f : faces) {
            try {
                PointF p = new PointF();
                f.getMidPoint(p);
                tempBitmap = SontHelperSonty.getCircledBitmap(tempBitmap, p);
                Log.d("camera_api", "faces found: " + facesfound + " w: " + p.x + " h: " + p.y);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return tempBitmap;
    }

    public static void pickImage(Activity act) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        act.startActivityForResult(photoPickerIntent, 999);
    }

    public static void wifi_check_enabled(Context ctx) {
        // TURN ON WIFI
        WifiManager wifi = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()) {
            Toast.makeText(ctx, "Turning on WiFi", Toast.LENGTH_SHORT).show();
            wifi.setWifiEnabled(true);
        }
    }

    public static void adminPermission_check(Context ctx, Activity a) {
        DevicePolicyManager mDPM = null;
        ComponentName mAdminName = null;
        try {
            if (!mDPM.isAdminActive(mAdminName)) {
                try {
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "extrainfo");
                    a.startActivityForResult(intent, 0);
                } catch (Exception e) {
                    Log.d("Error_setting_admin_permission_", e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.d("Error_", e.getMessage());
            e.printStackTrace();
        }
    }

    public static String generateGFX(List<Location> points) {
        String name = "gfxfile.gfx";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), name);
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";
        name = "<name>" + name + "</name><trkseg>\n";

        String segments = "";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        for (Location location : points) {
            segments += "<trkpt lat=\"" + location.getLatitude() + "\" lon=\"" + location.getLongitude() + "\"><time>" + df.format(new Date(location.getTime())) + "</time></trkpt>\n";
        }

        String footer = "</trkseg></trk></gpx>";

        try {
            FileWriter writer = new FileWriter(file, false);
            writer.append(header);
            writer.append(name);
            writer.append(segments);
            writer.append(footer);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file.getAbsolutePath();
    }

    public static String generateKML(List<Location> points) {
        String fullKML = "";
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n <kml xmlns=\"http://earth.google.com/kml/2.2\">\n";
        String segments = "";
        String footer = "</kml>";

        for (Location location : points) {
            segments += "<Placemark>\n" +
                    "<name>" + "asd" + "</name>\n" +
                    "<description>desc</description>\n" +
                    "<Point>\n" +
                    "<coordinates>" + location.getLatitude() + "," + location.getLongitude() + "</coordinates>\n" +
                    "</Placemark>\n";
        }

        fullKML = header + segments + footer;
        File file = null;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "wifiloc.kml");
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            StringBuilder sb = new StringBuilder();
            sb.append(fullKML);
            writer.append(sb.toString());
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            Log.d("KML_location", file.toString());
        }
        return file.getAbsolutePath();
    }

    /*public static List<NeighboringCellInfo> getCellTowers(Context ctx) {
        // Please note that this information may not available on your device.
        // Most of the Samsung devices does not support this method.
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        List<NeighboringCellInfo> neighbors = tm.getNeighboringCellInfo();
        return neighbors;
    }*/
}

// COMPRESS UTILITY
class MyGZIPOutputStream extends GZIPOutputStream {

    public MyGZIPOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    public void setLevel(int level) {
        def.setLevel(level);
    }
}

class GZIPCompression {
    public static byte[] compress(final String str) throws IOException {
        if ((str == null) || (str.length() == 0)) {
            return null;
        }
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        MyGZIPOutputStream gzip = new MyGZIPOutputStream(obj);
        gzip.write(str.getBytes("UTF-8"));
        gzip.flush();
        gzip.close();
        return obj.toByteArray();
    }

    public static String decompress(final byte[] compressed) throws IOException {
        final StringBuilder outStr = new StringBuilder();
        if ((compressed == null) || (compressed.length == 0)) {
            return "";
        }
        if (isCompressed(compressed)) {
            final GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed));
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, "UTF-8"));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                outStr.append(line);
            }
        } else {
            outStr.append(compressed);
        }
        return outStr.toString();
    }

    public static boolean isCompressed(final byte[] compressed) {
        return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }
}

class ObjectSenderReceiver {

    class ObjectSender extends AsyncTask<Object, Object, Object> {

        final byte[] key = "1234567890000000".getBytes();
        private static final String transformation = "Blowfish";

        private Object o;

        public SealedObject encryptObject(Serializable obj) {
            try {
                SecretKeySpec sks = new SecretKeySpec(key, transformation);
                Cipher cipher = Cipher.getInstance(transformation);
                cipher.init(Cipher.ENCRYPT_MODE, sks);

                return new SealedObject(obj, cipher);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public ObjectSender(Object o) {
            this.o = o;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Object doInBackground(Object... strings) {
            try {
                //Socket s = new Socket("192.168.0.157", 1234);
                Socket s = new Socket("sont.sytes.net", 1234);
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

                SealedObject send = encryptObject((Serializable) o);

                oos.writeObject(send);
                oos.flush();
                oos.close();

            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println(e.toString());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... progress) {
        }

        @Override
        protected void onPostExecute(Object o) {
        }

    }

    class Responder implements ObjectListener {
        @Override
        public void update(HashMap<Integer, byte[]> byteParts) {
        }

        @Override
        public void update(byte[] data) {
        }
    }

    interface ObjectListener {
        void update(HashMap<Integer, byte[]> byteParts);

        void update(byte[] data);
    }

    class ObjectReceiver extends AsyncTask<Object, Object, Object> {

        private List<ObjectListener> listeners = new ArrayList<ObjectListener>();

        public void addListener(ObjectListener toAdd) {
            listeners.add(toAdd);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Object doInBackground(Object... string) {
            ServerSocket server;
            try {
                server = new ServerSocket(1234);
                while (true) {
                    Socket s = server.accept();

                    InputStream is = s.getInputStream();
                    byte[] data = SontHelperSonty.inputStreamToByteArray_istoba(is);
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
                    Object obj = ois.readObject();

                    if (obj instanceof SealedObject) {
                        byte[] key = "1234567890000000".getBytes();
                        String transformation = "Blowfish";
                        SecretKeySpec sks = new SecretKeySpec(key, transformation);
                        Cipher cipher = Cipher.getInstance(transformation);
                        cipher.init(Cipher.DECRYPT_MODE, sks);
                        Object o = ((SealedObject) obj).getObject(cipher);

                        if (o instanceof String) {
                            String str = (String) ((SealedObject) obj).getObject(cipher);
                            //Gson gson = new Gson();
                            //String received = gson.fromJson(str, String.class);
                            System.out.println("Received: " + str);
                        }
                        if (o instanceof byte[]) {
                            byte[] converted = (byte[]) o;
                            for (ObjectListener hl : listeners) {
                                hl.update(converted);
                            }
                        }
                        if (o instanceof HashMap) {
                            HashMap<Integer, byte[]> got = (HashMap<Integer, byte[]>) o;
                            for (ObjectListener hl : listeners) {
                                hl.update(got);
                            }
                        }
                    }

                }
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println(e.toString());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... progress) {
        }

        @Override
        protected void onPostExecute(Object o) {
        }
    }

}

class NetworkUtils {

    /**
     * Convert byte array to hex string
     *
     * @param bytes toConvert
     * @return hexValue
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sbuf = new StringBuilder();
        for (int idx = 0; idx < bytes.length; idx++) {
            int intVal = bytes[idx] & 0xff;
            if (intVal < 0x10) sbuf.append("0");
            sbuf.append(Integer.toHexString(intVal).toUpperCase());
        }
        return sbuf.toString();
    }

    /**
     * Get utf8 byte array.
     *
     * @param str which to be converted
     * @return array of NULL if error was found
     */
    public static byte[] getUTF8Bytes(String str) {
        try {
            return str.getBytes(StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Load UTF8withBOM or any ansi text file.
     *
     * @param filename which to be converted to string
     * @return String value of File
     * @throws java.io.IOException if error occurs
     */
    public static String loadFileAsString(String filename) throws java.io.IOException {
        final int BUFLEN = 1024;
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
            byte[] bytes = new byte[BUFLEN];
            boolean isUTF8 = false;
            int read, count = 0;
            while ((read = is.read(bytes)) != -1) {
                if (count == 0 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                    isUTF8 = true;
                    baos.write(bytes, 3, read - 3); // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read);
                }
                count += read;
            }
            return isUTF8 ? new String(baos.toByteArray(), StandardCharsets.UTF_8) : new String(baos.toByteArray());
        } finally {
            try {
                is.close();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) buf.append(String.format("%02X:", aMac));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "";
    }

}