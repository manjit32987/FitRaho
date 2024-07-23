package com.example.fitraho;

import android.Manifest;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private TextView textViewMessage;
    private EditText editTextGuess;
    private Button buttonProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Request window feature to make content edge-to-edge
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(R.layout.activity_main);

        textViewMessage = findViewById(R.id.textViewMessage);
        editTextGuess = findViewById(R.id.editTextGuess);
        buttonProceed = findViewById(R.id.buttonProceed);

        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.PACKAGE_USAGE_STATS) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            showScreenTime();
        }
    }

    private void requestPermission() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivityForResult(intent, PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED) {
                showScreenTime();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showScreenTime() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        if (usageStatsManager != null) {
            long endTime = System.currentTimeMillis();
            long startTime = endTime - TimeUnit.DAYS.toMillis(1);

            List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
            long totalTime = 0;

            for (UsageStats usageStats : usageStatsList) {
                totalTime += usageStats.getTotalTimeInForeground();
            }

            long totalTimeInHours = TimeUnit.MILLISECONDS.toHours(totalTime);

            Toast.makeText(this, "Your actual screen time is " + totalTimeInHours + " hours", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Unable to retrieve screen time", Toast.LENGTH_SHORT).show();
        }
    }
}
