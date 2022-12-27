package com.example.shaalwallpaper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shaalwallpaper.helper.Util;

import java.io.File;

public class SplashscreenActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int WALLPAPER_PERMISSION_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createDirectory();

        if(!isNetworkAvailable()){
            //Log.d(TAG, "onCreate: " + "Hey Why didn't you awake");
            Intent intent = new Intent(SplashscreenActivity.this, Collection.class);
            startActivity(intent);
            finish();
        }
        else{
            Intent intent = new Intent(SplashscreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void createDirectory(){
        Util.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE);
        String WALLPAPER_DIRECTORY = "Shaal-Wallpaper";
        File file = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath() + "/" + WALLPAPER_DIRECTORY);
        //File file = new File(getExternalFilesDir(null) + "/" + WALLPAPER_DIRECTORY);

        if(file.exists()){
            Toast.makeText(this, "Directory created Successfully", Toast.LENGTH_SHORT)
                    .show();
            Log.d("Directory creation", "createDirectory: ");
            return;
        }

        boolean results = file.mkdir();

        if(results){
            Toast.makeText(this, "Directory created Successfully", Toast.LENGTH_SHORT)
                    .show();
        }

//        try{
//            Log.d(TAG, "createDirectory: copying");
//            Toast.makeText(this, "File will Copied Successfully", Toast.LENGTH_SHORT).show();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                FileUtils.copy(new FileInputStream(file2), new FileOutputStream(file));
//                Toast.makeText(this, "File Copied Successfully", Toast.LENGTH_SHORT).show();
//            }
//        }catch (Exception e){
//            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "createDirectory: " + e.getMessage());
//        }
        else{
            Toast.makeText(this, "Directory creation failed", Toast.LENGTH_SHORT)
                    .show();
            Log.d("Directory creation", "Failed at: " + Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(SplashscreenActivity.this,
                        "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(SplashscreenActivity.this, "Storage Permission Denied",
                        Toast.LENGTH_SHORT).show();
            }
        }

        else if(requestCode == WALLPAPER_PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(SplashscreenActivity.this,
                        "Set Wallpaper Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(SplashscreenActivity.this,
                        "Set Wallpaper Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}