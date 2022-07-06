package com.example.shaalwallpaper.helper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Util {
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int WALLPAPER_PERMISSION_CODE = 102;

    /**
     * @return integer in range [min, max]
     */
//    public static int getRandomInt(int min, int max) {
//        return Math.abs(ThreadLocalRandom.current().nextInt()) % max;
//    }

//    public static boolean hasPermission(Context context, String permission) {
//        int result = ContextCompat.checkSelfPermission(context, permission);
//        return result == PackageManager.PERMISSION_GRANTED;
//    }

    public static long getFileSizeInKb(File file) {
        return file.length() / 1024;
    }

    public void setRandomWallpaper(Context context, int i, int width, int height) {
        //checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        String WALLPAPER_DIRECTORY = "Shaal-Wallpaper";
        File wallpaperDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + WALLPAPER_DIRECTORY);
        //File wallpaperDirectory = new File(context.getExternalFilesDir(null) + "/" + WALLPAPER_DIRECTORY);
        String TAG = "Util";
        if (wallpaperDirectory.exists()) {
            try {
                Log.d(TAG, "Size: ");
                File[] files = wallpaperDirectory.listFiles();
                if (files != null && files.length > 0) {
                    Log.d(TAG, "Size: " + files.length);
                    i = i%(files.length-1);
                    //int randomFilePathIndex = getRandomInt(0, files.length - 1);
                    File randomFile = files[i];
                    long fileSizeInKb = getFileSizeInKb(randomFile);
                    long maxFileSizeInKb = 10240;
                    Log.d(TAG, "Wallpaper file size: " + fileSizeInKb);
                    if (fileSizeInKb <= maxFileSizeInKb) {
                        //checkPermission(context, Manifest.permission.SET_WALLPAPER, WALLPAPER_PERMISSION_CODE);
                        if(width == 0 && height == 0) {
                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            displayMetrics = context.getResources().getDisplayMetrics();
                            width = displayMetrics.widthPixels;
                            height = displayMetrics.heightPixels;
                            if (width > height) {
                                swap(width, height);
                            }
                        }
                        String randomFilePath = randomFile.getAbsolutePath();

                        //BitmapFactory.Options options = new BitmapFactory.Options();
                        Bitmap image = BitmapFactory.decodeFile(randomFilePath);

                        WallpaperManager manager = WallpaperManager.getInstance(context);
                        WallpaperManager manager1 = WallpaperManager.getInstance(context);
                        int finalWidth = width;
                        int finalHeight = height;
                        new Thread(()->{
                            try {

                                Bitmap bitmapResized = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
                                manager.suggestDesiredDimensions(bitmapResized.getWidth(), bitmapResized.getHeight());
                                manager.setBitmap(bitmapResized, null, true, WallpaperManager.FLAG_LOCK);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).start();


                        new Thread(() -> {
                            try {
//                                Bitmap blank = BitmapHelper.createNewBitmap(manager1.getDesiredMinimumWidth(), manager1.getDesiredMinimumHeight());
//                                Bitmap overlay = BitmapHelper.overlayIntoCentre(blank, image);
//                                manager1.setBitmap(overlay);
                                manager1.suggestDesiredDimensions(finalWidth, finalHeight);
                                manager1.setBitmap(image);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    } else {
                        Log.d(TAG, "File size exceeds limit: " + maxFileSizeInKb);
                    }
                } else {
                    Log.d(TAG, "Wallpaper directory is empty: ");
                }
            } catch (Exception e) {
                //Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }

        } else {
            boolean createDirectoryResult = wallpaperDirectory.mkdir();
            //Log.d(TAG, "Wallpaper directory creation result: " + createDirectoryResult);
        }

    }

    // Function to check and request permission.
    public static void checkPermission(Context context, String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions((Activity) context, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(context, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }


    public void swap(int a, int b){
        int temp = a;
        a = b;
        b = temp;
    }
}
