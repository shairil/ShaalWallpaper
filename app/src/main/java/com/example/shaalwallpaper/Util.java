package com.example.shaalwallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class Util {
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int WALLPAPER_PERMISSION_CODE = 102;
    private final String TAG = "Util";
    private final String WALLPAPER_DIRECTORY = "Shaal-Wallpaper";

    /**
     * @return integer in range [min, max]
     */
    public static int getRandomInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public static boolean hasPermission(Context context, String permission) {
        int result = ContextCompat.checkSelfPermission(context, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static long getFileSizeInKb(File file) {
        return file.length() / 1024;
    }

    public void setRandomWallpaper(Context context) {
        //checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        File wallpaperDirectory = new File(context.getExternalFilesDir(null) + "/" + WALLPAPER_DIRECTORY);
        if (wallpaperDirectory.exists()) {
            try {
                File[] files = wallpaperDirectory.listFiles();
                if (files != null && files.length > 0) {
                    Log.d(TAG, "Size: " + files.length);
                    int randomFilePathIndex = getRandomInt(0, files.length - 1);
                    File randomFile = files[randomFilePathIndex];
                    long fileSizeInKb = getFileSizeInKb(randomFile);
                    long maxFileSizeInKb = 10240;
                    Log.d(TAG, "Wallpaper file size: " + fileSizeInKb);
                    if (fileSizeInKb <= maxFileSizeInKb) {
                        //checkPermission(context, Manifest.permission.SET_WALLPAPER, WALLPAPER_PERMISSION_CODE);
                        String randomFilePath = randomFile.getAbsolutePath();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        Bitmap image = BitmapFactory.decodeFile(randomFilePath, options);
                        WallpaperManager manager = WallpaperManager.getInstance(context);
                        WallpaperManager manager1 = WallpaperManager.getInstance(context);
                        try {
                            manager.setBitmap(image, null, true, WallpaperManager.FLAG_LOCK);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        new Thread(() -> {
                            try {
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
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }

        } else {
            boolean createDirectoryResult = wallpaperDirectory.mkdir();
            Log.d(TAG, "Wallpaper directory creation result: " + createDirectoryResult);
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

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.


}
