package com.sontme.legacysonty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        TextView info = findViewById(R.id.infotext);
        info.setMovementMethod(new ScrollingMovementMethod());
        info.setText(Html.fromHtml(BackgroundService.infoText, Html.FROM_HTML_MODE_LEGACY));
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
        BackgroundService.addInfo("WiFi", msg);
        ScrollView scrollview = findViewById(R.id.scroller);
        final ScrollView scroller = (ScrollView) findViewById(R.id.scroller);
        scroller.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scroller.fullScroll(View.FOCUS_DOWN);
                }
            }
        });

        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            public void run() {
                info.setText(Html.fromHtml(BackgroundService.infoText, Html.FROM_HTML_MODE_LEGACY));
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

}
