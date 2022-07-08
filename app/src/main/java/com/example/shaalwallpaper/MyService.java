package com.example.shaalwallpaper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.example.shaalwallpaper.helper.TimerConfig;
import com.example.shaalwallpaper.helper.Util;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static int i=0;
    private final String TAG = "MyService";
    public static boolean isServiceRunning;
    private final String CHANNEL_ID = "NOTIFICATION_CHANNEL";
    PowerManager pm;
    //private final ScreenLockReceiver screenLockReceiver;
    private final Timer timer;
    private String time = "15 min";
    private long t = 60;
    IBinder mBinder = new LocalBinder();
    private Handler handler;
    private int width=0, height=0;
//    private final WindowManager windowManager = null;
//    private final LinearLayout linearLayout = null;
    //GestureDetector gestureDetector = null;
    //private final HashMap<String, Long> map = new HashMap<String, Long>();

    public MyService() {
        Log.d(TAG, "constructor called");
        isServiceRunning = false;
       // this.context = context;
        //screenLockReceiver = new ScreenLockReceiver();
//        try {
////            new TimerConfig().registerPref(getApplicationContext(), (SharedPreferences.OnSharedPreferenceChangeListener) getApplicationContext());
//            Log.d(TAG, "MyService: " + "registration Successful");
//        }catch (Exception e){
//            Log.d(TAG, "MyService: " + "registration Unsuccessful");
//            Log.d(TAG, "MyService: " + e.getMessage());
//            e.printStackTrace();
//        }


        //pm = (PowerManager) Context.getSystemService(Context.POWER_SERVICE);

        Log.d(TAG, "MyService: " + time);

        timer = new Timer();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate called");
        createNotificationChannel();
        isServiceRunning = true;

        //gestureDetector = new GestureDetector(this, new MyGestureListener());

        pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        Configuration orientation = this.getResources().getConfiguration();

        try{
            new DisplayMetrics();
            DisplayMetrics displayMetrics;
            displayMetrics = this.getResources().getDisplayMetrics();
            width = displayMetrics.widthPixels;
            height = displayMetrics.heightPixels;
        } catch (Exception e) {
            //Log.d(TAG, "onCreate: display" + e.getMessage());
            e.printStackTrace();
        }


        //Log.d(TAG, "onCreate: " + width + " " + height);

        try {
            SharedPreferences sharedPreferences = this.getSharedPreferences("Timer", MODE_PRIVATE);
            time = sharedPreferences.getString("time", "15 min");
            //Log.d(TAG, "onCreate: " + " Hey Successfully got data from the shared preferences. " + time);
            getT(time);
        }catch (Exception e) {
            //Log.d(TAG, "onCreate: " + e.getMessage());
            e.printStackTrace();
        }

//
//        linearLayout = new LinearLayout(this);
//        LinearLayout.LayoutParams lp = new LinearLayout
//                .LayoutParams(1, 1);
//
//        linearLayout.setLayoutParams(lp);
//        linearLayout.setBackgroundColor(Color.TRANSPARENT);
//        linearLayout.setOnTouchListener(new View.OnTouchListener() {
//
//            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
//
//                @Override
//                public boolean onDoubleTap(MotionEvent motionEvent) {
//                    Log.d(TAG, "onDoubleTap: HEY Successfulll");
//                    return super.onDoubleTap(motionEvent);
//                }
//                @Override
//                public void onLongPress(MotionEvent motionEvent) {
//                    Log.d(TAG, "onLongPress: successfull");
//                    super.onLongPress(motionEvent);
//                }
//            });
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // pass the events to the gesture detector
//                // a return value of true means the detector is handling it
//                // a return value of false means the detector didn't
//                // recognize the event
//                //gestureDetector.onTouchEvent(event);
//
//                gestureDetector.onTouchEvent(event);
//                //Log.d(TAG, "onTouch: don't know this is not working");
//                return false;
//
//            }
//        });
//        windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
//        int LAYOUT_FLAG;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        } else {
//            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
//        }
//
//        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
//                1,
//                1,
//                LAYOUT_FLAG,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
//                PixelFormat.TRANSLUCENT
//        );
//
//        linearLayout.setGravity(Gravity.TOP|Gravity.START);
//
//        windowManager.addView(linearLayout, mParams);
//
//
//

        // register receiver to listen for screen on events
//        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
//        filter.addAction(Intent.ACTION_USER_PRESENT);
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        registerReceiver(screenLockReceiver, filter);
//
        /*// a dummy timer task - can be ignored
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "run called inside scheduleAtFixedRate");
            }
        }, 0, PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS);*/

        //timer.schedule(new Util().setRandomWallpaper(MyService.this), 6000);
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if(i!=0)
//                    new Util().setRandomWallpaper(MyService.this);
//                i++;
//                Log.d(TAG, "run: " + i);
//                Log.d(TAG, "run: " + t);
//            }
//        }, 0,t*10);

        //Log.d(TAG, "onCreate: " + time);
        //Log.d(TAG, "Outside run: " + t);



        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(pm.isInteractive()) {
                    if(new Util().setRandomWallpaper(MyService.this, i, width, height) == 1)
                        i++;
//                    Log.d(TAG, "run: " + i);
//                    Log.d(TAG, "run: " + t);

                }
                if(i>10000){
                    i = i%10000;
                }
                handler.postDelayed(this, t*1000);
            }
        });

        //wallpaperChanger.run();

    }

//    Runnable wallpaperChanger = new Runnable() {
//
//        @Override
//        public void run() {
//
//            if(pm.isInteractive()) {
//                new Util().setRandomWallpaper(MyService.this, i, width, height);
//                //Log.d(TAG, "run: " + i);
//                //Log.d(TAG, "run: " + t);
//                i++;
//            }
//            if(i>10000){
//                i = i%10000;
//            }
//            handler.postDelayed(this, t*1000);
//        }
//    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public MyService getServerInstance() {
            return MyService.this;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.d(TAG, "onStartCommand called");
        try {
            time = intent.getStringExtra("time");
        }catch (Exception e){
            e.printStackTrace();
        }

        getT(time);

        Intent broadcastIntent = new Intent(this, ScreenLockReceiver.class);
        broadcastIntent.putExtra("width", width);
        broadcastIntent.putExtra("height", height);
        PendingIntent pendingIntent1;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){

            pendingIntent1 = PendingIntent.getBroadcast(this,
                    0, broadcastIntent, PendingIntent.FLAG_MUTABLE);
        }
        else{

            pendingIntent1 = PendingIntent.getBroadcast(this,
                    0, broadcastIntent, 0);
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Service is Running")
                .setContentText("Wallpaper will change automatically in every " + time)
                .setSmallIcon(R.drawable.ic_wallpaper_black_24dp)
                .setContentIntent(pendingIntent1)
                .setColor(getColor(R.color.colorPrimary))
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
        //TimerConfig.unregisterPref(this, this::onSharedPreferenceChanged);
        stopForeground(true);

        // unregister receiver
        //unregisterReceiver(screenLockReceiver);

        // cancel the timer
        if (timer != null) {
            timer.cancel();
        }

        if(handler != null){
            handler.removeCallbacks(null);

            //handler.removeCallbacks(wallpaperChanger);
        }

        // call MyReceiver which will restart this service via a worker
        Intent broadcastIntent = new Intent(this, MyReceiver.class);
        sendBroadcast(broadcastIntent);

        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved called");
        super.onTaskRemoved(rootIntent);
    }

    public void getT(String time){
        switch (time){
            case "1 min":
                t = 60;
                break;
            case "15 min":
                t = 900;
                break;
            case "30 min":
                t = 1800;
                break;
            case "45 min":
                t = 2700;
                break;
            case "1 hr":
                t = 3600;
                break;
            case "6 hr":
                t = 21600;
                break;
            case "8 hr":
                t = 28800;
                break;
            case "1 day":
                t = 86400;
                break;
            default:
                t = 5000;
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.d(TAG, "onSharedPreferenceChanged: " + s);
        if (s.equals(TimerConfig.PREF_TOTAL_KEY)) {
            time = TimerConfig.loadTotalFromPref(this);
            getT(time);
            //txtTotal.setText("Your total is: " + counter);
        }
    }

    public void setTime(String time){
        if(!this.time.equals(time)) {
            this.time = time;
            getT(time);
            stopForeground(true);
            Intent broadcastIntent = new Intent(this, ScreenLockReceiver.class);
            broadcastIntent.putExtra("width", width);
            broadcastIntent.putExtra("height", height);
            PendingIntent pendingIntent1;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){

                pendingIntent1 = PendingIntent.getBroadcast(this,
                        0, broadcastIntent, PendingIntent.FLAG_MUTABLE);
            }
            else{

                pendingIntent1 = PendingIntent.getBroadcast(this,
                        0, broadcastIntent, 0);
            }
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Service is Running")
                    .setContentText("Wallpaper will change automatically in every " + time)
                    .setSmallIcon(R.drawable.ic_wallpaper_black_24dp)
                    .setContentIntent(pendingIntent1)
                    .setColor(getColor(R.color.colorPrimary))
                    .build();
            /*
             * A started service can use the startForeground API to put the service in a foreground state,
             * where the system considers it to be something the user is actively aware of and thus not
             * a candidate for killing when low on memory.
             */
            startForeground(1, notification);
        }
    }
}

