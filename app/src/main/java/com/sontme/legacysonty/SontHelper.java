package com.sontme.legacysonty;

import static android.os.Process.THREAD_PRIORITY_AUDIO;
import static android.os.Process.setThreadPriority;

import android.annotation.TargetApi;
import android.app.Instrumentation;
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
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.net.ssl.HttpsURLConnection;

public class SontHelper {

    public static class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;
        private Context context;

        /* (non-Javadoc)
         * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
         */
        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            return gestureDetector.onTouchEvent(motionEvent);
        }

        /**
         * Gets the gesture detector.
         *
         * @return the gesture detector
         */
        public GestureDetector getGestureDetector() {
            return gestureDetector;
        }

        /**
         * Instantiates a new on swipe touch listener.
         *
         * @param context the context
         */
        public OnSwipeTouchListener(Context context) {
            super();
            this.context = context;
            gestureDetector = new GestureDetector(context, new GestureListener());
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            /* (non-Javadoc)
             * @see android.view.GestureDetector.SimpleOnGestureListener#onDown(android.view.MotionEvent)
             */
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            /* (non-Javadoc)
             * @see android.view.GestureDetector.SimpleOnGestureListener#onFling(android.view.MotionEvent, android.view.MotionEvent, float, float)
             */

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getRawY() - e1.getRawY();
                    float diffX = e2.getRawX() - e1.getRawX();
                    if ((Math.abs(diffX) - Math.abs(diffY)) > SWIPE_THRESHOLD) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                        }
                    } else {
                        if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffY > 0) {
                                onSwipeBottom();
                            } else {
                                onSwipeTop();
                            }
                        }
                    }
                } catch (Exception e) {

                }
                return result;
            }
        }

        /**
         * On swipe right.
         */
        public void onSwipeRight() {
        }

        /**
         * On swipe left.
         */
        public void onSwipeLeft() {
        }

        /**
         * On swipe top.
         */
        public void onSwipeTop() {
        }

        /**
         * On swipe bottom.
         */
        public void onSwipeBottom() {
        }
    }

    public static boolean checkPatternString(String str) {
        String pattern = "";
        for (int i = 0; i < str.length() / 2; i++) {
            pattern += str.charAt(i);
            if (str.length() % pattern.length() == 0 && isRepeatingString(str, pattern)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRepeatingString(String str, String pattern) {
        String leftover = str;
        int currIndex = leftover.indexOf(pattern);
        while (currIndex == 0) {
            if (currIndex + pattern.length() == leftover.length()) {
                return true; // you have reached the last possible instance of the pattern at this point
            }
            leftover = leftover.substring(currIndex + pattern.length());
            currIndex = leftover.indexOf(pattern);
        }
        return false;
    }

    public static class NFCHelper {
        public static String detectTagData(Tag tag) {
            StringBuilder sb = new StringBuilder();
            byte[] id = tag.getId();
            sb.append("ID (hex): ").append(toHex(id)).append('\n');
            sb.append("ID (dec): ").append(toDec(id)).append('\n');

            String prefix = "android.nfc.tech.";
            sb.append("Technologies: ");
            for (String tech : tag.getTechList()) {
                sb.append(tech.substring(prefix.length()));
                sb.append(", ");
            }

            sb.delete(sb.length() - 2, sb.length());

            for (String tech : tag.getTechList()) {
                if (tech.equals(MifareClassic.class.getName())) {
                    sb.append('\n');
                    String type = "Unknown";

                    try {
                        MifareClassic mifareTag = MifareClassic.get(tag);

                        switch (mifareTag.getType()) {
                            case MifareClassic.TYPE_CLASSIC:
                                type = "Classic";
                                break;
                            case MifareClassic.TYPE_PLUS:
                                type = "Plus";
                                break;
                            case MifareClassic.TYPE_PRO:
                                type = "Pro";
                                break;
                        }
                        sb.append("Mifare Classic type: ");
                        sb.append(type);
                        sb.append('\n');

                        sb.append("Mifare size: ");
                        sb.append(mifareTag.getSize() + " bytes");
                        sb.append('\n');

                        sb.append("Mifare sectors: ");
                        sb.append(mifareTag.getSectorCount());
                        sb.append('\n');

                        sb.append("Mifare blocks: ");
                        sb.append(mifareTag.getBlockCount());
                    } catch (Exception e) {
                        sb.append("Mifare classic error: " + e.getMessage());
                    }
                }

                if (tech.equals(MifareUltralight.class.getName())) {
                    sb.append('\n');
                    MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                    String type = "Unknown";
                    switch (mifareUlTag.getType()) {
                        case MifareUltralight.TYPE_ULTRALIGHT:
                            type = "Ultralight";
                            break;
                        case MifareUltralight.TYPE_ULTRALIGHT_C:
                            type = "Ultralight C";
                            break;
                    }
                    sb.append("Mifare Ultralight type: ");
                    sb.append(type);
                }
            }
            Log.v("test", sb.toString());
            return sb.toString();
        }

        public static String toHex(byte[] bytes) {
            StringBuilder sb = new StringBuilder();
            for (int i = bytes.length - 1; i >= 0; --i) {
                int b = bytes[i] & 0xff;
                if (b < 0x10)
                    sb.append('0');
                sb.append(Integer.toHexString(b));
                if (i > 0) {
                    sb.append(" ");
                }
            }
            return sb.toString();
        }

        public static long toDec(byte[] bytes) {
            long result = 0;
            long factor = 1;
            for (int i = 0; i < bytes.length; ++i) {
                long value = bytes[i] & 0xffl;
                result += value * factor;
                factor *= 256l;
            }
            return result;
        }
    }

    public static class MD5 {
        private static final String TAG = "MD5";

        public boolean checkMD5(String md5, File updateFile) {
            if (TextUtils.isEmpty(md5) || updateFile == null) {
                Log.e(TAG, "MD5 string empty or updateFile null");
                return false;
            }

            String calculatedDigest = calculateMD5(updateFile);
            if (calculatedDigest == null) {
                Log.e(TAG, "calculatedDigest null");
                return false;
            }

            Log.v(TAG, "Calculated digest: " + calculatedDigest);
            Log.v(TAG, "Provided digest: " + md5);

            return calculatedDigest.equalsIgnoreCase(md5);
        }

        public static String calculateMD5(File updateFile) {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, "Exception while getting digest", e);
                return null;
            }

            InputStream is;
            try {
                is = new FileInputStream(updateFile);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Exception while getting FileInputStream", e);
                return null;
            }

            byte[] buffer = new byte[8192];
            int read;
            try {
                while ((read = is.read(buffer)) > 0) {
                    digest.update(buffer, 0, read);
                }
                byte[] md5sum = digest.digest();
                BigInteger bigInt = new BigInteger(1, md5sum);
                String output = bigInt.toString(16);
                // Fill to 32 chars
                output = String.format("%32s", output).replace(' ', '0');
                return output;
            } catch (IOException e) {
                throw new RuntimeException("Unable to process file for MD5", e);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e(TAG, "Exception on closing MD5 input stream", e);
                }
            }
        }
    }

    public static class BluetoothUtils {
        public static class BleUtil {
            private final static String TAG = BleUtil.class.getSimpleName();

            public static BleAdvertisedData parseAdertisedData(byte[] advertisedData) {
                List<UUID> uuids = new ArrayList<UUID>();
                String name = null;
                if (advertisedData == null) {
                    return new BleAdvertisedData(uuids, name);
                }

                ByteBuffer buffer = ByteBuffer.wrap(advertisedData).order(ByteOrder.LITTLE_ENDIAN);
                while (buffer.remaining() > 2) {
                    byte length = buffer.get();
                    if (length == 0) break;

                    byte type = buffer.get();
                    switch (type) {
                        case 0x02: // Partial list of 16-bit UUIDs
                        case 0x03: // Complete list of 16-bit UUIDs
                            while (length >= 2) {
                                uuids.add(UUID.fromString(String.format(
                                        "%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
                                length -= 2;
                            }
                            break;
                        case 0x06: // Partial list of 128-bit UUIDs
                        case 0x07: // Complete list of 128-bit UUIDs
                            while (length >= 16) {
                                long lsb = buffer.getLong();
                                long msb = buffer.getLong();
                                uuids.add(new UUID(msb, lsb));
                                length -= 16;
                            }
                            break;
                        case 0x09:
                            byte[] nameBytes = new byte[length - 1];
                            buffer.get(nameBytes);
                            try {
                                name = new String(nameBytes, "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            buffer.position(buffer.position() + length - 1);
                            break;
                    }
                }
                return new BleAdvertisedData(uuids, name);
            }
        }

        public static class BleAdvertisedData {
            private List<UUID> mUuids;
            private String mName;

            public BleAdvertisedData(List<UUID> uuids, String name) {
                mUuids = uuids;
                mName = name;
            }

            @Override
            public String toString() {
                return mName + " #" + mUuids.size();
            }

            public List<UUID> getUuids() {
                return mUuids;
            }

            public String getName() {
                return mName;
            }
        }

    }

    public static <T> ArrayList<T> intersectionOfLists(ArrayList<T> list1, ArrayList<T> list2) {
        ArrayList<T> list = new ArrayList<T>();

        for (T t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

    public static class Bounce {
        private final float from;
        private final float to;

        private float current;
        private float step;

        public Bounce(float from, float to, float step) {
            if (step > to - from || to <= from)
                throw new IllegalArgumentException("invalid arguments");
            this.from = from;
            this.to = to;
            this.current = from - step;
            this.step = step;
        }

        public synchronized float next() {
            if (current + step > to || current + step < from) step = -step;
            return current += step;
        }
    }
    /*public static Set<String> sortMapByValue(Set<String> unsortMap, final boolean order) {
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }*/

    public static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);
        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }
        return containedUrls;
    }

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

    public static class CurrentReaderFactory {

        static final String BUILD_MODEL = Build.MODEL.toLowerCase(Locale.ENGLISH);

        @TargetApi(4)
        static public Long getValue() {
            File f = null;

            // Galaxy S3
            if (CurrentReaderFactory.BUILD_MODEL.contains("gt-i9300")
                    || CurrentReaderFactory.BUILD_MODEL.contains("gt-i9300T")
                    || CurrentReaderFactory.BUILD_MODEL.contains("gt-i9305")
                    || CurrentReaderFactory.BUILD_MODEL.contains("gt-i9305N")
                    || CurrentReaderFactory.BUILD_MODEL.contains("gt-i9305T")
                    || CurrentReaderFactory.BUILD_MODEL.contains("shv-e210k")
                    || CurrentReaderFactory.BUILD_MODEL.contains("shv-e210l")
                    || CurrentReaderFactory.BUILD_MODEL.contains("shv-e210s")
                    || CurrentReaderFactory.BUILD_MODEL.contains("sgh-t999")
                    || CurrentReaderFactory.BUILD_MODEL.contains("sgh-t999l")
                    || CurrentReaderFactory.BUILD_MODEL.contains("sgh-t999v")
                    || CurrentReaderFactory.BUILD_MODEL.contains("sgh-i747")
                    || CurrentReaderFactory.BUILD_MODEL.contains("sgh-i747m")
                    || CurrentReaderFactory.BUILD_MODEL.contains("sgh-n064")
                    || CurrentReaderFactory.BUILD_MODEL.contains("sc-06d")
                    || CurrentReaderFactory.BUILD_MODEL.contains("sgh-n035")
                    || CurrentReaderFactory.BUILD_MODEL.contains("sc-03e")
                    || CurrentReaderFactory.BUILD_MODEL.contains("SCH-j021")
                    || CurrentReaderFactory.BUILD_MODEL.contains("scl21")
                    || CurrentReaderFactory.BUILD_MODEL.contains("sch-r530")
                    || CurrentReaderFactory.BUILD_MODEL.contains("sch-i535")
                    || CurrentReaderFactory.BUILD_MODEL.contains("sch-S960l")
                    || CurrentReaderFactory.BUILD_MODEL.contains("gt-i9308")
                    || CurrentReaderFactory.BUILD_MODEL.contains("sch-i939")
                    || CurrentReaderFactory.BUILD_MODEL.contains("sch-s968c")) {
                f = new File("/sys/class/power_supply/battery/current_max");
                if (f.exists()) {
                    return OneLineReader.getValue(f, false);
                }
            }

            if (CurrentReaderFactory.BUILD_MODEL.contains("nexus 7")
                    || CurrentReaderFactory.BUILD_MODEL.contains("one")
                    || CurrentReaderFactory.BUILD_MODEL.contains("lg-d851")) {
                f = new File("/sys/class/power_supply/battery/current_now");
                if (f.exists()) {
                    return OneLineReader.getValue(f, false);
                }
            }

            if (CurrentReaderFactory.BUILD_MODEL.contains("sl930")) {
                f = new File("/sys/class/power_supply/da9052-bat/current_avg");
                if (f.exists()) {
                    return OneLineReader.getValue(f, false);
                }
            }

            // Galaxy S4
            if (CurrentReaderFactory.BUILD_MODEL.contains("sgh-i337")
                    || CurrentReaderFactory.BUILD_MODEL.contains("gt-i9505")
                    || CurrentReaderFactory.BUILD_MODEL.contains("gt-i9500")
                    || CurrentReaderFactory.BUILD_MODEL.contains("sch-i545")
                    || CurrentReaderFactory.BUILD_MODEL.contains("find 5")
                    || CurrentReaderFactory.BUILD_MODEL.contains("sgh-m919")
                    || CurrentReaderFactory.BUILD_MODEL.contains("sgh-i537")) {
                f = new File("/sys/class/power_supply/battery/current_now");
                if (f.exists()) {
                    return OneLineReader.getValue(f, false);
                }
            }

            if (CurrentReaderFactory.BUILD_MODEL.contains("cynus")) {
                f = new File(
                        "/sys/devices/platform/mt6329-battery/FG_Battery_CurrentConsumption");
                if (f.exists()) {
                    return OneLineReader.getValue(f, false);
                }
            }
            // Zopo Zp900, etc.
            if (CurrentReaderFactory.BUILD_MODEL.contains("zp900")
                    || CurrentReaderFactory.BUILD_MODEL.contains("jy-g3")
                    || CurrentReaderFactory.BUILD_MODEL.contains("zp800")
                    || CurrentReaderFactory.BUILD_MODEL.contains("zp800h")
                    || CurrentReaderFactory.BUILD_MODEL.contains("zp810")
                    || CurrentReaderFactory.BUILD_MODEL.contains("w100")
                    || CurrentReaderFactory.BUILD_MODEL.contains("zte v987")) {
                f = new File(
                        "/sys/class/power_supply/battery/BatteryAverageCurrent");
                if (f.exists()) {
                    return OneLineReader.getValue(f, false);
                }
            }

            // Samsung Galaxy Tab 2
            if (CurrentReaderFactory.BUILD_MODEL.contains("gt-p31")
                    || CurrentReaderFactory.BUILD_MODEL.contains("gt-p51")) {
                f = new File("/sys/class/power_supply/battery/current_avg");
                if (f.exists()) {
                    return OneLineReader.getValue(f, false);
                }
            }

            // HTC One X
            if (CurrentReaderFactory.BUILD_MODEL.contains("htc one x")) {
                f = new File("/sys/class/power_supply/battery/batt_attr_text");
                if (f.exists()) {
                    Long value = BattAttrTextReader.getValue(f, "I_MBAT", "I_MBAT");
                    if (value != null)
                        return value;
                }
            }

            // wildfire S
            if (CurrentReaderFactory.BUILD_MODEL.contains("wildfire s")) {
                f = new File("/sys/class/power_supply/battery/smem_text");
                if (f.exists()) {
                    Long value = BattAttrTextReader.getValue(f, "eval_current",
                            "batt_current");
                    if (value != null)
                        return value;
                }
            }

            // trimuph with cm7, lg ls670, galaxy s3, galaxy note 2
            if (CurrentReaderFactory.BUILD_MODEL.contains("triumph")
                    || CurrentReaderFactory.BUILD_MODEL.contains("ls670")
                    || CurrentReaderFactory.BUILD_MODEL.contains("gt-i9300")
                    || CurrentReaderFactory.BUILD_MODEL.contains("sm-n9005")
                    || CurrentReaderFactory.BUILD_MODEL.contains("gt-n7100")
                    || CurrentReaderFactory.BUILD_MODEL.contains("sgh-i317")) {
                f = new File("/sys/class/power_supply/battery/current_now");
                if (f.exists()) {
                    return OneLineReader.getValue(f, false);
                }
            }

            // htc desire hd / desire z / inspire?
            // htc evo view tablet
            if (CurrentReaderFactory.BUILD_MODEL.contains("desire hd")
                    || CurrentReaderFactory.BUILD_MODEL.contains("desire z")
                    || CurrentReaderFactory.BUILD_MODEL.contains("inspire")
                    || CurrentReaderFactory.BUILD_MODEL.contains("pg41200")) {
                f = new File("/sys/class/power_supply/battery/batt_current");
                if (f.exists()) {
                    return OneLineReader.getValue(f, false);
                }
            }

            // nexus one cyangoenmod
            f = new File("/sys/devices/platform/ds2784-battery/getcurrent");
            if (f.exists()) {
                return OneLineReader.getValue(f, true);
            }

            // sony ericsson xperia x1
            f = new File(
                    "/sys/devices/platform/i2c-adapter/i2c-0/0-0036/power_supply/ds2746-battery/current_now");
            if (f.exists()) {
                return OneLineReader.getValue(f, false);
            }

            // xdandroid
            /* if (Build.MODEL.equalsIgnoreCase("MSM")) { */
            f = new File(
                    "/sys/devices/platform/i2c-adapter/i2c-0/0-0036/power_supply/battery/current_now");
            if (f.exists()) {
                return OneLineReader.getValue(f, false);
            }
            /* } */

            // droid eris
            f = new File("/sys/class/power_supply/battery/smem_text");
            if (f.exists()) {
                Long value = SMemTextReader.getValue();
                if (value != null)
                    return value;
            }

            // htc sensation / evo 3d
            f = new File("/sys/class/power_supply/battery/batt_attr_text");
            if (f.exists()) {
                Long value = BattAttrTextReader.getValue(f,
                        "batt_discharge_current", "batt_current");
                if (value != null)
                    return value;
            }

            // some htc devices
            f = new File("/sys/class/power_supply/battery/batt_current");
            if (f.exists()) {
                return OneLineReader.getValue(f, false);
            }

            // Nexus One.
            // TODO: Make this not default but specific for N1 because of the normalization.
            f = new File("/sys/class/power_supply/battery/current_now");
            if (f.exists()) {
                return OneLineReader.getValue(f, true);
            }

            // samsung galaxy vibrant
            f = new File("/sys/class/power_supply/battery/batt_chg_current");
            if (f.exists())
                return OneLineReader.getValue(f, false);

            // sony ericsson x10
            f = new File("/sys/class/power_supply/battery/charger_current");
            if (f.exists())
                return OneLineReader.getValue(f, false);

            // Nook Color
            f = new File("/sys/class/power_supply/max17042-0/current_now");
            if (f.exists())
                return OneLineReader.getValue(f, false);

            // Xperia Arc
            f = new File("/sys/class/power_supply/bq27520/current_now");
            if (f.exists())
                return OneLineReader.getValue(f, true);

            // Motorola Atrix
            f = new File(
                    "/sys/devices/platform/cpcap_battery/power_supply/usb/current_now");
            if (f.exists())
                return OneLineReader.getValue(f, false);

            // Acer Iconia Tab A500
            f = new File("/sys/EcControl/BatCurrent");
            if (f.exists())
                return OneLineReader.getValue(f, false);

            // charge current only, Samsung Note
            f = new File("/sys/class/power_supply/battery/batt_current_now");
            if (f.exists())
                return OneLineReader.getValue(f, false);

            // galaxy note, galaxy s2
            f = new File("/sys/class/power_supply/battery/batt_current_adc");
            if (f.exists())
                return OneLineReader.getValue(f, false);

            // intel
            f = new File("/sys/class/power_supply/max170xx_battery/current_now");
            if (f.exists())
                return OneLineReader.getValue(f, true);

            // Sony Xperia U
            f = new File("/sys/class/power_supply/ab8500_fg/current_now");
            if (f.exists())
                return OneLineReader.getValue(f, true);

            f = new File("/sys/class/power_supply/android-battery/current_now");
            if (f.exists()) {
                return OneLineReader.getValue(f, false);
            }

            // Nexus 10, 4.4.
            f = new File("/sys/class/power_supply/ds2784-fuelgauge/current_now");
            if (f.exists()) {
                return OneLineReader.getValue(f, true);
            }

            f = new File("/sys/class/power_supply/Battery/current_now");
            if (f.exists()) {
                return OneLineReader.getValue(f, false);
            }

            return null;
        }
    }

    public static class OneLineReader {

	/*private File _f = null;
	private boolean _convertToMillis = false;

	public OneLineReader(File f, boolean convertToMillis) {
		_f = f;
		_convertToMillis = convertToMillis;
	}*/

        public static Long getValue(File _f, boolean _convertToMillis) {

            String text = null;

            try {
                FileInputStream fs = new FileInputStream(_f);
                InputStreamReader sr = new InputStreamReader(fs);
                BufferedReader br = new BufferedReader(sr);

                text = br.readLine();

                br.close();
                sr.close();
                fs.close();
            } catch (Exception ex) {
                Log.e("CurrentWidget", ex.getMessage());
                ex.printStackTrace();
            }

            Long value = null;

            if (text != null) {
                try {
                    value = Long.parseLong(text);
                } catch (NumberFormatException nfe) {
                    Log.e("CurrentWidget", nfe.getMessage());
                    value = null;
                }

                if (_convertToMillis && value != null) {
                    value = value / 1000; // convert to milliampere
                }
            }

            return value;
        }

    }

    public static class BattAttrTextReader {

        public static Long getValue(File f, String dischargeField, String chargeField) {

            String text = null;
            Long value = null;

            try {

                // @@@ debug
                //StringReader fr = new StringReader("vref: 1248\r\nbatt_id: 3\r\nbatt_vol: 4068\r\nbatt_current: 0\r\nbatt_discharge_current: 123\r\nbatt_temperature: 329\r\nbatt_temp_protection:normal\r\nPd_M:0\r\nI_MBAT:-313\r\npercent_last(RP): 94\r\npercent_update: 71\r\nlevel: 71\r\nfirst_level: 100\r\nfull_level:100\r\ncapacity:1580\r\ncharging_source: USB\r\ncharging_enabled: Slow\r\n");
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);

                String line = br.readLine();

                final String chargeFieldHead = chargeField + ": ";
                final String dischargeFieldHead = dischargeField + ": ";


                while (line != null) {
                    if (line.contains(chargeField)) {
                        text = line.substring(line.indexOf(chargeFieldHead) + chargeFieldHead.length());
                        try {
                            value = Long.parseLong(text);
                            if (value != 0)
                                break;
                        } catch (NumberFormatException nfe) {
                            Log.e("CurrentWidget", nfe.getMessage(), nfe);
                        }
                    }

                    //  "batt_discharge_current:"
                    if (line.contains(dischargeField)) {
                        text = line.substring(line.indexOf(dischargeFieldHead) + dischargeFieldHead.length());
                        try {
                            value = (-1) * Math.abs(Long.parseLong(text));
                        } catch (NumberFormatException nfe) {
                            Log.e("CurrentWidget", nfe.getMessage(), nfe);
                        }
                        break;
                    }


                    line = br.readLine();
                }

                br.close();
                fr.close();
            } catch (Exception ex) {
                Log.e("CurrentWidget", ex.getMessage(), ex);
            }


            return value;
        }

    }

    public static class SMemTextReader {

        public static Long getValue() {

            boolean success = false;
            String text = null;

            try {

                // @@@ debug StringReader fr = new StringReader("batt_id: 1\r\nbatt_vol: 3840\r\nbatt_vol_last: 0\r\nbatt_temp: 1072\r\nbatt_current: 1\r\nbatt_current_last: 0\r\nbatt_discharge_current: 112\r\nVREF_2: 0\r\nVREF: 1243\r\nADC4096_VREF: 4073\r\nRtemp: 70\r\nTemp: 324\r\nTemp_last: 0\r\npd_M: 20\r\nMBAT_pd: 3860\r\nI_MBAT: -114\r\npd_temp: 0\r\npercent_last: 57\r\npercent_update: 58\r\ndis_percent: 64\r\nvbus: 0\r\nusbid: 1\r\ncharging_source: 0\r\nMBAT_IN: 1\r\nfull_bat: 1300000\r\neval_current: 115\r\neval_current_last: 0\r\ncharging_enabled: 0\r\ntimeout: 30\r\nfullcharge: 0\r\nlevel: 58\r\ndelta: 1\r\nchg_time: 0\r\nlevel_change: 0\r\nsleep_timer_count: 11\r\nOT_led_on: 0\r\noverloading_charge: 0\r\na2m_cable_type: 0\r\nover_vchg: 0\r\n");
                FileReader fr = new FileReader("/sys/class/power_supply/battery/smem_text");
                BufferedReader br = new BufferedReader(fr);

                String line = br.readLine();

                while (line != null) {
                    if (line.contains("I_MBAT")) {
                        text = line.substring(line.indexOf("I_MBAT: ") + 8);
                        success = true;
                        break;
                    }
                    line = br.readLine();
                }

                br.close();
                fr.close();
            } catch (Exception ex) {
                Log.e("CurrentWidget", ex.getMessage());
                ex.printStackTrace();
            }

            Long value = null;

            if (success) {

                try {
                    value = Long.parseLong(text);
                } catch (NumberFormatException nfe) {
                    Log.e("CurrentWidget", nfe.getMessage());
                    value = null;
                }

            }

            return value;
        }

    }
}
