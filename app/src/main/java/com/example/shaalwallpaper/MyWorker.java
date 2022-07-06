package com.example.shaalwallpaper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;

public class MyWorker extends Worker {
    private final Context context;
    private int work;
    private final String TAG = "MyWorker";
    private String timer = "15 min";

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }
    @NonNull
    @Override
    public Result doWork() {
        //Log.d(TAG, "doWork called for: " + this.getId());
        //Log.d(TAG, "Service Running: " + MyService.isServiceRunning);

        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("timer", Context.MODE_PRIVATE);
            timer = sharedPreferences.getString("time", "15 min");
        }catch(Exception e){
            e.printStackTrace();
        }

        if (!MyService.isServiceRunning) {
            //Log.d(TAG, "starting service from doWork");
            Intent intent = new Intent(this.context, MyService.class);
            intent.putExtra("time", timer);
            ContextCompat.startForegroundService(context, intent);
            //this.context.startService(intent);
        }
        return Result.success();
    }

    @Override
    public void onStopped() {
        //Log.d(TAG, "onStopped called for: " + this.getId());
        super.onStopped();
    }
}
