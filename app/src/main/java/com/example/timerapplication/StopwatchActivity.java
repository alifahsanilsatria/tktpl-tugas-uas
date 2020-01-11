package com.example.timerapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import static com.example.timerapplication.App.CHANNEL_1_ID;

public class StopwatchActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private static final String TAG = "StopwatchActivity";
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        SharedPreferences countPref = getApplicationContext().getSharedPreferences("countPref", 0);
        countPref.registerOnSharedPreferenceChangeListener(this);
        SharedPreferences.Editor editor = countPref.edit();
        editor.putInt("count",0);
        editor.apply();

        TextView countTV = findViewById(R.id.count);
        int currentCount = countPref.getInt("count",9999);
        int[] minutesAndSeconds = convertToMinutesAndSeconds(currentCount);
        String formattedCount = Integer.toString(minutesAndSeconds[0]) + ":" + Integer.toString(minutesAndSeconds[1]);
        countTV.setText(formattedCount);

        notificationManager = NotificationManagerCompat.from(this);
    }

    public void scheduleJob(View v) {
        ComponentName componentName = new ComponentName(this, StopwatchJobService.class);
        JobInfo info = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            info = new JobInfo.Builder(123, componentName)
                    .setOverrideDeadline(0)
                    .build();
        }

        JobScheduler scheduler = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        }
        int resultCode = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            resultCode = scheduler.schedule(info);

        }
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Stopwatch Notification")
                    .setContentText("0:0")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE);

            notificationManager.notify(1, notificationBuilder.build());

            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }

    public void cancelJob(View v) {
        JobScheduler scheduler = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        }
        switch (v.getId()) {
            case R.id.pauseButton:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    scheduler.cancel(123);
                    Log.d(TAG, "Job cancelled");
                }
                break;
            case R.id.resetButton:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    scheduler.cancel(123);
                    Log.d(TAG, "Job cancelled");
                    SharedPreferences countPref = getApplicationContext().getSharedPreferences("countPref", 0);
                    SharedPreferences.Editor editor = countPref.edit();
                    editor.putInt("count",0);
                    editor.apply();
                }
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        int updatedCount = sharedPreferences.getInt(s,9999);
        TextView countTV = findViewById(R.id.count);

        int[] minutesAndSeconds = convertToMinutesAndSeconds(updatedCount);
        String formattedCount = Integer.toString(minutesAndSeconds[0]) + ":" + Integer.toString(minutesAndSeconds[1]);

        Log.d("Output", formattedCount);

        countTV.setText(formattedCount);
        Log.d(TAG, "Count Updated on TextView");

         NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Stopwatch Notification")
                .setContentText(formattedCount)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        notificationManager.notify(1, notificationBuilder.build());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native int[] convertToMinutesAndSeconds(int count);
}
