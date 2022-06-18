package com.example.shaalwallpaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shaalwallpaper.Adapter.WallapaperAdapter;
import com.example.shaalwallpaper.databinding.ActivityCollectionBinding;
import com.example.shaalwallpaper.databinding.ActivityWallpaperBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Collection extends AppCompatActivity {

    String[] timer = {"15 min", "30 min", "45 min", "1 hr", "6 hr", "8 hr", "1 day"};
    ActivityCollectionBinding binding;
    boolean mBounded;
    MyService mServer;
    private List<Bitmap> imgURLs;
    private List<String> titles, res, paths;
    private List<Integer> ids;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private final String TAG = "CollectionClass";
    private final String WALLPAPER_DIRECTORY = "Shaal-Wallpaper";
    private String initial = "15 min";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityCollectionBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        titles = new ArrayList<>();
        //getActionBar().setCustomView(binding.toolbar);
        getSupportActionBar().hide();
//        setSupportActionBar(binding.toolbar);
        imgURLs = new ArrayList<>();
        res = new ArrayList<>();
        ids = new ArrayList<>();
        paths = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("Timer",MODE_PRIVATE);
        try {
            SharedPreferences sharedPreferences1 = getSharedPreferences("Timer", MODE_PRIVATE);
            initial = sharedPreferences1.getString("time", "");
            Toast.makeText(this, "Your Wallpaper will change automatically after every " + initial, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }

        // Creating an Editor object to edit(write to the file)

        WallapaperAdapter adapter = new WallapaperAdapter(imgURLs, titles, ids,this, paths);
        binding.recyclerViewCollection.setHasFixedSize(true);
        binding.recyclerViewCollection.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        binding.recyclerViewCollection.setAdapter(adapter);


        new Thread(){
            @Override
            public void run() {
                //super.run();
                File wallpaperDirectory = new File(getExternalFilesDir(null) + "/" + WALLPAPER_DIRECTORY);
                new Util();
                if (wallpaperDirectory.exists()) {
                    try {
                        File[] files = wallpaperDirectory.listFiles();
                        if (files != null && files.length > 0) {

                            for (File randomFile : files) {
                                //Log.d(TAG, "Size: " + randomFile.length());
                                //Log.d(TAG, "Name: " + randomFile.getName());
                                int temp = imgURLs.size();
                                String randomFilePath = randomFile.getAbsolutePath();
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                Bitmap image = BitmapFactory.decodeFile(randomFilePath, options);
                                //String img = image.toString();
                                imgURLs.add(image);
                                int i = randomFile.getName().indexOf('(');
                                titles.add(randomFile.getName().substring(0, i));
                                ids.add(Integer.valueOf(randomFile.getName().substring(i+1, i+7)));
                                paths.add(randomFilePath);
                                Collection.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });

                                //Log.d(TAG, "onCreate: "  +img);
                            }
                        } else {
                            Collection.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Collection.this, "Directory is empty", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }

                } else {
                    boolean createDirectoryResult = wallpaperDirectory.mkdirs();
                    Log.d(TAG, "Wallpaper directory creation result: " + createDirectoryResult);
                }
            }
        }.start();






        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, timer);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.timer.setAdapter(ad);

        int i = find(initial);
        //Log.d(TAG, "onCreate: " + i);
        if(i!=-1){
            //Toast.makeText(this, Integer.toString(i), Toast.LENGTH_SHORT).show();
            binding.timer.setSelection(i);
            binding.timer.onSaveInstanceState();
        }


        //TimerConfig.saveTotalInPref(this, binding.timer.getSelectedItem().toString());


        binding.timer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(mServer != null) {
                    mServer.setTime(adapterView.getItemAtPosition(i).toString());
                    Log.d(TAG, "onItemSelected: service Done");
                }
                Log.d(TAG, "onItemSelected: "  + adapterView.getItemAtPosition(i).toString());
                //TimerConfig.saveTotalInPref(Collection.this, adapterView.getItemAtPosition(i).toString());
//                myEdit.putString("time", adapterView.getItemAtPosition(i).toString());
//                myEdit.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



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