package com.example.shaalwallpaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.shaalwallpaper.helper.Util;

import java.util.Random;

import static java.lang.Math.abs;

public class ScreenLockReceiver extends BroadcastReceiver {

    private final String TAG = "ScreenLockReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //String action = intent.getAction();
        int width = intent.getIntExtra("width", 0);
        int height = intent.getIntExtra("height", 0);
        new Util().setRandomWallpaper(context, abs(new Random().nextInt()), width, height);
//        switch (action) {
//            //case Intent.
//            case Intent.
//            case Intent.ACTION_SCREEN_ON:
//                Log.d(TAG, "onReceive called: screen on");
//                //new Util().setRandomWallpaper(context);
//                break;
//            case Intent.ACTION_SCREEN_OFF:
//                Log.d(TAG, "onReceive called: screen off");
//                break;
//            case Intent.ACTION_USER_PRESENT:
//                Log.d(TAG, "onReceive called: screen unlocked");
//
//                break;
//        }
    }
}
