package com.example.shaalwallpaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String TAG = "MyReceiver";
        Log.d(TAG, "onReceive called");

        if (!MyService.isServiceRunning) {
            //Log.d(TAG, "starting service from doWork");
            try {
                Intent intent1 = new Intent(context, MyService.class);
                //intent.putExtra("time", timer);
                ContextCompat.startForegroundService(context, intent1);
            }catch (Exception e){
                e.printStackTrace();
            }
            //this.context.startService(intent);
        }

        if(!MyService.isServiceRunning) {
            WorkManager workManager = WorkManager.getInstance(context);
            OneTimeWorkRequest startServiceRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                    .build();
            workManager.enqueue(startServiceRequest);
        }
    }
}
