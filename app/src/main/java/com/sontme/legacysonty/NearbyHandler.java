package com.sontme.legacysonty;

import static android.content.Context.WIFI_SERVICE;
import static com.sontme.legacysonty.BackgroundService.android_id_source_device;
import static com.sontme.legacysonty.BackgroundService.roundBandwidth;
import static com.sontme.legacysonty.BackgroundService.sendMessage_Telegram;

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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
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
    public Context ctx;
    public static String SERVICE_ID;
    public static Strategy STRATEGY;
    public static String currentDevice;
    public static File recvFile;
    public static Set<String> activeEndpoints = new ArraySet<>(); // add and remove
    public static Set<String> everEndpoints = new ArraySet<>(); // just add
    public static SontHelper.AudioPlayer player;
    public static final Strategy PEERTOPEER_STRATEGY =
            Strategy.P2P_CLUSTER;
    public static boolean isTurnedOn = false;

    private void handlePayload(String s, Payload payload) {
        if (payload.getType() == Payload.Type.BYTES) {
            byte[] load = payload.asBytes();
            SontHelper.SerializationHelper serializer = new SontHelper.SerializationHelper();
            Object object = serializer.deserialize(load);
            Map<String, byte[]> container = (Map<String, byte[]>) object;
            for (Map.Entry<String, byte[]> entry : container.entrySet()) {
                String key = entry.getKey();
                byte[] value_data = entry.getValue();
                if (key.contains("welcome")) {
                    String source = (String) SontHelper.SerializationHelper.deserialize(value_data);
                    BackgroundService.sendMessage_Telegram("Welcome from " + source);
                } else {
                    String got = new String(value_data);
                    BackgroundService.sendMessage_Telegram("SENT/RECV DATA: " + got);
                    BackgroundService.sendMessage_Telegram("[" + BackgroundService.android_id_source_device + "] Unknown Command=" + key + " datalen=" + value_data.length);
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

        } else if (payload.getType() == Payload.Type.STREAM) {
            InputStream inputStream = payload.asStream().asInputStream();
            player = new SontHelper.AudioPlayer(inputStream) {
                @Override
                protected void onFinish() {
                    try {
                        player.stop();
                        player = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            player.start();
        } else {
            sendMessage_Telegram("Unknown TYPE of Payload");
        }
    }

    public NearbyHandler(Context ctx, Strategy STRATEGY) {
        this.ctx = ctx;
        NearbyHandler.STRATEGY = STRATEGY;
        SERVICE_ID = ctx.getPackageName();

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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (payloadTransferUpdate.getStatus() == PayloadTransferUpdate.Status.FAILURE) {

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
                        /*sendMessage_Telegram(android_id_source_device +
                                " onConnectionInitiated() | " +
                                s + connectionInfo.getEndpointName());*/
                    }
                });
            }

            @Override
            public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {
                switch (connectionResolution.getStatus().getStatusCode()) {
                    case ConnectionsStatusCodes.STATUS_OK:
                        sendKeyObjectPayload("welcome", android_id_source_device);
                        stopAdvertising();
                        stopDiscovering();
                        Status conres_status = connectionResolution.getStatus();
                        ConnectionResult connectionResult = conres_status.getConnectionResult();

                        sendMessage_Telegram(android_id_source_device +
                                " Connected OK | " + s);
                        isTurnedOn = true;
                        break;
                    case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                        startAdvertising();
                        startDiscovering();
                        break;
                    case ConnectionsStatusCodes.STATUS_ERROR:
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
                sendMessage_Telegram(android_id_source_device +
                        " onConnectionInitiated() DISCONNECTED | " + s);
                startAdvertising();
                startDiscovering();
            }
        };

        endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
            @Override
            public void onEndpointFound(@NonNull String s, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
                everEndpoints.add(s);
                activeEndpoints.add(s);
                com.sontme.legacysonty.Endpoint endpoint = new com.sontme.legacysonty.Endpoint(s, discoveredEndpointInfo.getEndpointName());
                ConnectionsClient c = Nearby.getConnectionsClient(ctx);
                c.requestConnection(endpoint.getName(), endpoint.getId(), connectionLifecycleCallback).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        stopAdvertising();
                        stopDiscovering();
                        /*sendMessage_Telegram(android_id_source_device +
                                " onEndpointFound() | " +
                                s + endpoint.getName());*/
                    }
                });
            }

            @Override
            public void onEndpointLost(@NonNull String s) {
                /*sendMessage_Telegram(android_id_source_device +
                        " onEndpointLost() | " +
                        s);*/
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
                    e.printStackTrace();
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
                    e.printStackTrace();
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
                        }
                        e.printStackTrace();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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
                        }
                        e.printStackTrace();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
    }

    public void stopDiscovering() {
        ConnectionsClient client = Nearby.getConnectionsClient(ctx);
        client.stopDiscovery();
    }

    public void stopAdvertising() {
        ConnectionsClient client = Nearby.getConnectionsClient(ctx);
        client.stopAdvertising();
    }

    public void closeEndpoints() {
        ConnectionsClient client = Nearby.getConnectionsClient(ctx);
        client.stopAllEndpoints();
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

