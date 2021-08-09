package com.sontme.legacysonty;

import static android.os.Process.THREAD_PRIORITY_AUDIO;
import static android.os.Process.setThreadPriority;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

public class SontHelper {
    public static class Encrypt {
        public static SecretKey generateKey()
                throws NoSuchAlgorithmException, InvalidKeySpecException {
            // 16 or 24 or 32 // 128 192 256
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return keyGenerator.generateKey();
            //return new SecretKeySpec("123456789qwertzuqqqqqqqqqqqqqqqq".getBytes(), "AES");
        }

        public static byte[] encryptMsg(String message, SecretKey secret)
                throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
            /* Encrypt the message. */
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            byte[] cipherText = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return cipherText;
        }

        public static String decryptMsg(byte[] cipherText, SecretKey secret)
                throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
            /* Decrypt the message, given derived encContentValues and initialization vector. */
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret);
            String decryptString = new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
            return decryptString;
        }
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
            Log.d("ERROR: SONTHELPER > locationToStringAddress()", e.getMessage());
            return "Unknown";
        }
        return strAdd;
    }

    public static class AudioPlayer {
        /**
         * The audio stream we're reading from.
         */
        private final InputStream mInputStream;

        /**
         * If true, the background thread will continue to loop and play audio. Once false, the thread
         * will shut down.
         */
        private volatile boolean mAlive;

        /**
         * The background thread recording audio for us.
         */
        private Thread mThread;

        /**
         * A simple audio player.
         *
         * @param inputStream The input stream of the recording.
         */
        public AudioPlayer(InputStream inputStream) {
            mInputStream = inputStream;
        }

        /**
         * @return True if currently playing.
         */
        public boolean isPlaying() {
            return mAlive;
        }

        /**
         * Starts playing the stream.
         */
        public void start() {
            mAlive = true;
            mThread =
                    new Thread() {
                        @Override
                        public void run() {
                            setThreadPriority(THREAD_PRIORITY_AUDIO);

                            Buffer buffer = new Buffer();
                            AudioTrack audioTrack =
                                    new AudioTrack(
                                            AudioManager.STREAM_MUSIC,
                                            buffer.sampleRate,
                                            AudioFormat.CHANNEL_OUT_MONO,
                                            AudioFormat.ENCODING_PCM_16BIT,
                                            buffer.size,
                                            AudioTrack.MODE_STREAM);
                            audioTrack.play();
                            audioTrack.setVolume(0);
                            int len;
                            try {
                                while (isPlaying() && (len = mInputStream.read(buffer.data)) > 0) {
                                    audioTrack.write(buffer.data, 0, len);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                stopInternal();
                                audioTrack.release();
                                onFinish();
                            }
                        }
                    };
            mThread.start();
        }

        private void stopInternal() {
            mAlive = false;
            try {
                mInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Stops playing the stream.
         */
        public void stop() {
            stopInternal();
            try {
                mThread.join();
            } catch (Exception e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        /**
         * The stream has now ended.
         */
        protected void onFinish() {
        }

        public static class Buffer extends AudioBuffer {
            @Override
            protected boolean validSize(int size) {
                return size != AudioTrack.ERROR && size != AudioTrack.ERROR_BAD_VALUE;
            }

            @Override
            protected int getMinBufferSize(int sampleRate) {
                return AudioTrack.getMinBufferSize(
                        sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
            }
        }
    }

    public static abstract class AudioBuffer {

        final int size;
        final int sampleRate;
        final byte[] data;

        protected AudioBuffer() {
            int size = -1;
            int sampleRate = -1;

            // Iterate over all possible sample rates, and try to find the shortest one. The shorter
            // it is, the faster it'll stream.
            int[] POSSIBLE_SAMPLE_RATES = new int[]{8000, 11025, 16000, 22050, 44100, 48000};
            for (int rate : POSSIBLE_SAMPLE_RATES) {
                sampleRate = rate;
                size = getMinBufferSize(sampleRate);
                if (validSize(size)) {
                    break;
                }
            }

            // If none of them were good, then just pick 1kb
            if (!validSize(size)) {
                size = 1024;
            }

            this.size = size;
            this.sampleRate = sampleRate;
            data = new byte[size];
        }

        protected abstract boolean validSize(int size);

        protected abstract int getMinBufferSize(int sampleRate);
    }

    public static class AudioRecorder {
        /**
         * The stream to write to.
         */
        private final OutputStream mOutputStream;

        /**
         * If true, the background thread will continue to loop and record audio. Once false, the thread
         * will shut down.
         */
        private volatile boolean mAlive;

        /**
         * The background thread recording audio for us.
         */
        private Thread mThread;

        /**
         * A simple audio recorder.
         *
         * @param file The output stream of the recording.
         */
        public AudioRecorder(ParcelFileDescriptor file) {
            mOutputStream = new ParcelFileDescriptor.AutoCloseOutputStream(file);
        }

        /**
         * @return True if actively recording. False otherwise.
         */
        public boolean isRecording() {
            return mAlive;
        }

        /**
         * Starts recording audio.
         */
        public void start() {
            if (isRecording()) {
                return;
            }
            mAlive = true;
            mThread = new Thread() {
                @Override
                public void run() {
                    setThreadPriority(THREAD_PRIORITY_AUDIO);

                    Buffer buffer = new Buffer();
                    AudioRecord record =
                            new AudioRecord(
                                    MediaRecorder.AudioSource.DEFAULT,
                                    buffer.sampleRate,
                                    AudioFormat.CHANNEL_IN_MONO,
                                    AudioFormat.ENCODING_PCM_16BIT,
                                    buffer.size);

                    if (record.getState() != AudioRecord.STATE_INITIALIZED) {
                        mAlive = false;
                        return;
                    }

                    record.startRecording();

                    // While we're running, we'll read the bytes from the AudioRecord and write them
                    // to our output stream.
                    try {
                        while (isRecording()) {
                            int len = record.read(buffer.data, 0, buffer.size);
                            if (len >= 0 && len <= buffer.size) {
                                mOutputStream.write(buffer.data, 0, len);
                                mOutputStream.flush();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        stopInternal();
                        try {
                            record.stop();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        record.release();
                    }
                }
            };
            mThread.start();
        }

        private void stopInternal() {
            mAlive = false;
            try {
                mOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Stops recording audio.
         */
        public void stop() {
            stopInternal();
            try {
                mThread.join();
            } catch (Exception e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        public static class Buffer extends AudioBuffer {
            @Override
            protected boolean validSize(int size) {
                return size != AudioRecord.ERROR && size != AudioRecord.ERROR_BAD_VALUE;
            }

            @Override
            protected int getMinBufferSize(int sampleRate) {
                return AudioRecord.getMinBufferSize(
                        sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            }
        }
    }

    public static class SerializationHelper {
        public static byte[] serialize(Object object) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                // transform object to stream and then to a byte array
                objectOutputStream.writeObject(object);
                objectOutputStream.flush();
                objectOutputStream.close();
                return byteArrayOutputStream.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public static Object deserialize(byte[] bytes) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                return objectInputStream.readObject();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class TelegramStuff {
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
                    StringBuilder response = new StringBuilder();
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
                    StringBuilder response = new StringBuilder();
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
                //retryCount++;
                e.printStackTrace();
            }
        }

        public static boolean sendMessage_BetaBot(String message) {
            if (message.length() < 1800) { // http allows 2000 for full url
                int retryCount = 0;
                try {
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
//                                    sendMessage_BetaBot(message + "<-previous message");
                                    //error.printStackTrace();
                                }
                            });
                } catch (Exception e) {
                    retryCount++;
                    sendMessage_BetaBot("[RETRIED_" + retryCount + "]" + message);
                    e.printStackTrace();
                }
            } else {
                ArrayList<String> chunks = SontHelper.getFixedLengthSubStringFromString(message);
                int c = message.length() / 1800;
                sendMessage_BetaBot("TOO LONG! SENDING IN CHUNKS! " + message.length() + "/1800=" + c);
                sendMessage_BetaBot(message);
                for (String str : chunks) {
                    sendMessage_BetaBot(str);
                }
                return true;
            }
            return false;
        }

        public static boolean sendMessage_BetaBotImage(File file) {
            int retryCount = 0;
            try {
                String url = "https://api.telegram.org/bot990712757:AAGyuPqZJUNoRAi1DMl-oRzEYInZz7UP0C4/sendMessage?chat_id=1093250115";
                AndroidNetworking.post(url).addBodyParameter("chat_id", "1093250115").addBodyParameter("photo", file.getAbsolutePath()).build().getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
            } catch (Exception e) {
                retryCount++;
                e.printStackTrace();
            }
            return true;
        }

        public static class SaveMapInSharedPreferences {
            private void saveMap(Map<String, Boolean> inputMap, Context c) {
                SharedPreferences pSharedPref = c.getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
                if (pSharedPref != null) {
                    JSONObject jsonObject = new JSONObject(inputMap);
                    String jsonString = jsonObject.toString();
                    SharedPreferences.Editor editor = pSharedPref.edit();
                    editor.remove("My_map").apply();
                    editor.putString("My_map", jsonString);
                    editor.commit();
                }
            }

            private Map<String, Boolean> loadMap(Context c) {
                Map<String, Boolean> outputMap = new HashMap<>();
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


    }

    public static ArrayList<String> getFixedLengthSubStringFromString(String longString) {
        ArrayList<String> list = new ArrayList<>();
        for (final String token :
                Splitter.fixedLength(4000).split(longString)) {
            list.add(token);
        }
        list.add("next is experiment");
        //list.add(longString.substring(longString.length()-4000,longString.length()));

        return list;
    }

    public static Bitmap getLastImage(Context context) {
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        final Cursor cursor = context.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        if (cursor.moveToFirst()) {
            String imageLocation = cursor.getString(1);
            File imageFile = new File(imageLocation);
            if (imageFile.exists()) {
                SontHelper.SerializationHelper serializer = new SerializationHelper();
                Bitmap bitmap = BitmapFactory.decodeFile(imageLocation);
                cursor.close();
                return bitmap;
            } else {
                cursor.close();
                return null;
            }
        } else {
            cursor.close();
            return null;
        }
    }

    public static Bitmap resizeBitmap(Bitmap source, double w, double h) {

        float imageRatio = (float) source.getWidth() / (float) source.getHeight();

        int imageRealHeight = (int) (w / imageRatio);

        return Bitmap.createScaledBitmap(source, (int) w, imageRealHeight, true);
    }

    public static class BitmapDataObject implements Serializable {
        public Bitmap currentImage;

        public BitmapDataObject(Bitmap bitmap) {
            currentImage = bitmap;
        }

        private void writeObject(java.io.ObjectOutputStream out) throws IOException {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            currentImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            out.writeInt(byteArray.length);
            out.write(byteArray);
        }

        private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
            int bufferLength = in.readInt();
            byte[] byteArray = new byte[bufferLength];
            int pos = 0;
            do {
                int read = in.read(byteArray, pos, bufferLength - pos);
                if (read != -1) {
                    pos += read;
                } else {
                    break;
                }
            } while (pos < bufferLength);
            currentImage = BitmapFactory.decodeByteArray(byteArray, 0, bufferLength);
        }
    }

    public static Bitmap reduceBitmapQuality(Bitmap bitmap, int quality) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.PNG, quality, out);
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
        return BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
    }

    public static File saveImage(Context ctx, Bitmap finalBitmap, String image_name) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();

        String path;
        if ("huawei".equalsIgnoreCase(Build.MANUFACTURER)) {
            path = ctx.getFilesDir().toString();
        } else {
            path = ctx.getExternalFilesDir(null).toString();
        }
        String fname = "Nearby_" + image_name + ".jpg";
        File file = new File(path, fname);

        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<String> getAllImagesPath(Context ctx) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        try {
            Cursor cursor;
            int column_index_data, column_index_folder_name;
            String PathOfImage = null;
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = new String[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                projection = new String[]{MediaStore.MediaColumns.DATA,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
            }

            cursor = ctx.getContentResolver().query(uri, projection, null,
                    null, null);

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                column_index_folder_name = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            }
            while (cursor.moveToNext()) {
                PathOfImage = cursor.getString(column_index_data);

                listOfAllImages.add(PathOfImage);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            return listOfAllImages;
        }
        return listOfAllImages;
    }

    public static Bitmap combineImages(Bitmap c, Bitmap s) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;
        int width, height = 0;
        if (c.getWidth() > s.getWidth()) {
            width = c.getWidth() + s.getWidth();
            height = c.getHeight();
        } else {
            width = s.getWidth() + s.getWidth();
            height = c.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth(), 0f, null);

        return cs;
    }

}
