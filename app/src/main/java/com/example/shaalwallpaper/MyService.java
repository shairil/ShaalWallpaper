package com.example.shaalwallpaper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.PeriodicWorkRequest;

import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    private static int i=0;
    private final String TAG = "MyService";
    public static boolean isServiceRunning;
    private final String CHANNEL_ID = "NOTIFICATION_CHANNEL";
    private final ScreenLockReceiver screenLockReceiver;
    private Timer timer;

    public MyService() {
        Log.d(TAG, "constructor called");
        isServiceRunning = false;
        screenLockReceiver = new ScreenLockReceiver();
        timer = new Timer();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate called");
        createNotificationChannel();
        isServiceRunning = true;

        // register receiver to listen for screen on events
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenLockReceiver, filter);

        /*// a dummy timer task - can be ignored
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "run called inside scheduleAtFixedRate");
            }
        }, 0, PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS);*/

        //timer.schedule(new Util().setRandomWallpaper(MyService.this), 6000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                new Util().setRandomWallpaper(MyService.this);
                i++;
                Log.d(TAG, "run: " + i);
            }
        }, 0, 500000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand called");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        }
        else{
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Service is Running")
                .setContentText("Listening for Screen Off/On events")
                .setSmallIcon(R.drawable.ic_wallpaper_black_24dp)
                .setContentIntent(pendingIntent)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .build();
        /*
         * A started service can use the startForeground API to put the service in a foreground state,
         * where the system considers it to be something the user is actively aware of and thus not
         * a candidate for killing when low on memory.
         */
        startForeground(1, notification);

        // does not work as expected though, even START_NOT_STICKY gives same result
        // device specific issue?
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String appName = getString(R.string.app_name);
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    appName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy called");
        isServiceRunning = false;
        stopForeground(true);

        // unregister receiver
        unregisterReceiver(screenLockReceiver);

        // cancel the timer
        if (timer != null) {
            timer.cancel();
        }

        // call MyReceiver which will restart this service via a worker
        Intent broadcastIntent = new Intent(this, MyReceiver.class);
        sendBroadcast(broadcastIntent);

        super.onDestroy();
    }

    // Not getting called on Xiaomi Redmi Note 7S even when autostart permission is granted
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved called");
        super.onTaskRemoved(rootIntent);
    }

}
