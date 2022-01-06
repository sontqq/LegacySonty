package com.sontme.legacysonty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.GestureDetector;
import android.view.View;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        TextView info = findViewById(R.id.infotext);
        info.setText(Html.fromHtml(BackgroundService.infoText, Html.FROM_HTML_MODE_LEGACY));
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
