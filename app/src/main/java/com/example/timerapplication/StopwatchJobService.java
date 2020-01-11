package com.example.timerapplication;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class StopwatchJobService extends JobService {
    private static final String TAG = "StopwatchJobService";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        doBackgroundWork(params);

        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences countPref = getApplicationContext().getSharedPreferences("countPref", 0);
                SharedPreferences.Editor editor = countPref.edit();
                while (true) {
                    if (jobCancelled) {
                        return;
                    }

                    int currentCount = countPref.getInt("count",9999);
                    editor.putInt("count", currentCount+1);
                    editor.apply();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return false;
    }
}
