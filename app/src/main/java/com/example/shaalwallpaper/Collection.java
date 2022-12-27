package com.example.shaalwallpaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shaalwallpaper.Adapter.WallpaperAdapter;
import com.example.shaalwallpaper.databinding.ActivityCollectionBinding;
import com.example.shaalwallpaper.helper.TimerConfig;
import com.example.shaalwallpaper.helper.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Collection extends AppCompatActivity {

    String[] timer = {"1 min", "15 min", "30 min", "45 min", "1 hr", "6 hr", "8 hr", "1 day"};
    ActivityCollectionBinding binding;
    private boolean mBounded;
    private MyService mServer;
    private List<File> filesImages;
    //private static final int STORAGE_PERMISSION_CODE = 101;
    private final String TAG = "CollectionClass";
    private final String WALLPAPER_DIRECTORY = "Shaal-Wallpaper";
    private String initial = "15 min";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityCollectionBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        //titles = new ArrayList<>();
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        //getActionBar().setCustomView(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).hide();
//        setSupportActionBar(binding.toolbar);
//        imgURLs = new ArrayList<>();
//        res = new ArrayList<>();
//        ids = new ArrayList<>();
//        paths = new ArrayList<>();
        filesImages = new ArrayList<>();
        //SharedPreferences sharedPreferences = getSharedPreferences("Timer",MODE_PRIVATE);
        try {
            SharedPreferences sharedPreferences1 = getSharedPreferences("Timer", MODE_PRIVATE);
            initial = sharedPreferences1.getString("time", "15 min");
            Toast.makeText(this, "Your Wallpaper will change automatically after every "
                    + initial, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }

        // Creating an Editor object to edit(write to the file)

        //ArrayAdapter<> a = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, timer);
        ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, timer);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.timer.setAdapter(ad);

        WallpaperAdapter adapter = new WallpaperAdapter(filesImages, this);
//        adapter.setHasStableIds(true);
        binding.recyclerViewCollection.setHasFixedSize(true);
        binding.recyclerViewCollection.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));
        binding.recyclerViewCollection.setAdapter(adapter);

        new Thread(() -> {
            //super.run();
            File wallpaperDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + WALLPAPER_DIRECTORY);
            //File wallpaperDirectory = new File(getExternalFilesDir(null) + "/" + WALLPAPER_DIRECTORY);
            new Util();
            if (wallpaperDirectory.exists()) {
                try {
                    File[] files = wallpaperDirectory.listFiles();
                    if(files != null && files.length > 0){
                        filesImages.clear();
                        filesImages.addAll(Arrays.asList(files));
                        Collection.this.runOnUiThread(adapter::notifyDataSetChanged);
//                            for(File f: files){
//                                new SingleMediaScanner(Collection.this, f);
//                            }
                    }
//                        if (files != null && files.length > 0) {
//
//                            for (File randomFile : files) {
//                                String randomFilePath = randomFile.getAbsolutePath();
//                                BitmapFactory.Options options = new BitmapFactory.Options();
//                                Bitmap image = BitmapFactory.decodeFile(randomFilePath, options);
//                                //String img = image.toString();
//                                imgURLs.add(image);
//                                int i = randomFile.getName().indexOf('(');
//                                titles.add(randomFile.getName().substring(0, i));
//                                ids.add(Integer.valueOf(randomFile.getName().substring(i+1, i+7)));
//                                paths.add(randomFilePath);
//                            }
//
//                            Collection.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    adapter.notifyDataSetChanged();
//                                }
//                            });
//
//
//                        }
                    else {
                        Collection.this.runOnUiThread(() -> Toast.makeText(Collection.this,
                                "Directory is empty", Toast.LENGTH_SHORT).show());

                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }

            } else {
                boolean createDirectoryResult = wallpaperDirectory.mkdirs();
                Log.d(TAG, "Wallpaper directory creation result: " + createDirectoryResult);
            }
        }).start();

        int i = find(initial);
        //Log.d(TAG, "onCreate: " + i);
        if(i!=-1){
            //Toast.makeText(this, Integer.toString(i), Toast.LENGTH_SHORT).show();
            binding.timer.setSelection(i);
            binding.timer.onSaveInstanceState();
        }

        //TimerConfig.saveTotalInPref(this, binding.timer.getSelectedItem().toString());

        //SharedPreferences sharedPreferences = SharedPreferences

        binding.timer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(getColor(R.color.white));
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(mServer != null) {
                    mServer.setTime(adapterView.getItemAtPosition(i).toString());
                    Log.d(TAG, "onItemSelected: service Done");
                }
                Log.d(TAG, "onItemSelected: "  + adapterView.getItemAtPosition(i).toString());
                TimerConfig.saveTotalInPref(Collection.this, adapterView.getItemAtPosition(i).toString());
//                myEdit.putString("time", adapterView.getItemAtPosition(i).toString());
//                myEdit.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.backBtn.setOnClickListener(view -> onBackPressed());

       // Window window = this.getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    int find(String temp){
        for(int i=0;i<7;i++){
            if(timer[i].equals(temp)){
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, MyService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            //Toast.makeText(mServer, "", Toast.LENGTH_SHORT).show();

            //Toast.makeText(Collection.this, "Service is disconnected",).show();
            mBounded = false;
            mServer = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Toast.makeText(Collection.this, "Service is connected", 1000).show();
            mBounded = true;
            MyService.LocalBinder mLocalBinder = (MyService.LocalBinder)service;
            mServer = mLocalBinder.getServerInstance();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    }
}