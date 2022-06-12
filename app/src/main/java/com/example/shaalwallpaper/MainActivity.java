package com.example.shaalwallpaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

import com.example.shaalwallpaper.Adapter.WallapaperAdapter;
import com.example.shaalwallpaper.databinding.ActivityMainBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    int n = 0;
    int sha = 0;
    List<String> titles, Res, imgUrls;
    List<Integer> ids;
    //ProgressDialog mProgressDialog;
    boolean isScrolling = false;
    int currItems, totalItems, scrolledItems;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int WALLPAPER_PERMISSION_CODE = 102;
    private final String WALLPAPER_DIRECTORY = "Shaal-Wallpaper";
    private Util util = new Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        createDirectory();
        util.checkPermission(this, Manifest.permission.SET_WALLPAPER, WALLPAPER_PERMISSION_CODE);
        //webScrapping web = new webScrapping(this);
        titles = new ArrayList<>();
        imgUrls = new ArrayList<>();
        Res = new ArrayList<>();
        ids = new ArrayList<>();
        WallapaperAdapter adapter = new WallapaperAdapter(imgUrls, titles, Res, ids, this);
        //binding.wallpaperHome.setHasFixedSize(true);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        binding.wallpaperHome.setLayoutManager(manager);
        binding.wallpaperHome.setAdapter(adapter);

        AddDataThread dataThread = new AddDataThread(adapter);
        dataThread.start();


        binding.extendedFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Collection.class);
                startActivity(intent);
            }
        });



        RecyclerThread thread = new RecyclerThread(adapter, dataThread);
        thread.start();




        startServiceViaWorker();
    }

    public void onStartServiceClick(View v) {
        startService();
    }

    public void onStopServiceClick(View v) {
        stopService();
    }

    @Override
    protected void onDestroy() {
        Log.d("TAG", "onDestroy called");

        stopService();
        super.onDestroy();
    }

    public void startService() {
        Log.d("TAG", "startService called");
        if (!MyService.isServiceRunning) {
            Intent serviceIntent = new Intent(this, MyService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
            //startService(serviceIntent);
        }
    }

    public void stopService() {
        Log.d("TAG", "stopService called");
        if (MyService.isServiceRunning) {
            Intent serviceIntent = new Intent(this, MyService.class);
            stopService(serviceIntent);
        }
    }

    public void startServiceViaWorker() {
        if (!MyService.isServiceRunning) {
            Log.d("MainActivty", "starting service from doWork");
            Intent intent = new Intent(this, MyService.class);
            ContextCompat.startForegroundService(this, intent);
            //this.context.startService(intent);
        }
        Log.d("TAG", "startServiceViaWorker called");
        String UNIQUE_WORK_NAME = "StartMyServiceViaWorker";
        //String WORKER_TAG = "MyServiceWorkerTag";
        WorkManager workManager = WorkManager.getInstance(this);

        // As per Documentation: The minimum repeat interval that can be defined is 15 minutes (
        // same as the JobScheduler API), but in practice 15 doesn't work. Using 16 here
        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                        MyWorker.class,
                        20,
                        TimeUnit.MINUTES)
                        //.addTag(WORKER_TAG)
                        .build();
        // below method will schedule a new work, each time app is opened
        //workManager.enqueue(request);

        // to schedule a unique work, no matter how many times app is opened i.e. startServiceViaWorker gets called
        // https://developer.android.com/topic/libraries/architecture/workmanager/how-to/unique-work
        // do check for AutoStart permission
        workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);

    }

    public void createDirectory(){
        util.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        File file = new File(getExternalFilesDir(null) + "/" + WALLPAPER_DIRECTORY);
        if(file.exists()){
            return;
        }

        boolean results = file.mkdir();
        if(!results){
            Log.d("Directory creation", "Failed at: " + Environment.getExternalStorageDirectory());
        }
        Log.d("Directory creation", ": " + results);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

        else if(requestCode == WALLPAPER_PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this,
                        "Set Wallpaper Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(MainActivity.this, "Set Wallpaper Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    class RecyclerThread extends Thread{

        WallapaperAdapter adapter;
        AddDataThread dataThread;
        int n = 1;
        webScrapping web = new webScrapping(MainActivity.this);

        RecyclerThread(WallapaperAdapter adapter, AddDataThread dataThread){
            this.adapter = adapter;
            this.dataThread = dataThread;
        }

        @Override
        public void run() {
            binding.wallpaperHome.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                        isScrolling = true;
                        binding.SHOWPROGRESS.setVisibility(View.GONE);
                    }
                }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(isScrolling && !recyclerView.canScrollVertically(1)){
                    sha++;
                    isScrolling = false;
                    binding.SHOWPROGRESS.setVisibility(View.VISIBLE);
                    AddDataThread thread = new AddDataThread(adapter);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        thread.start();
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    //adapter.loadMore();
                    Toast.makeText(MainActivity.this, "How many will you be called " + sha, Toast.LENGTH_SHORT).show();
                    //("How", "onScrollStateChanged: " + "How many will you be called " + sha);
                }
            }
            });
        }
    }





    class AddDataThread extends Thread{
        webScrapping web = new webScrapping(MainActivity.this);
        WallapaperAdapter adapter;

        List<String> imgs, tit, res;
        List<Integer> id;
        AddDataThread(WallapaperAdapter adapter){
            this.adapter = adapter;
        }



        @Override
        public void run() {
            n++;
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Got here " + n, Toast.LENGTH_SHORT).show();
                }
            });

            web.getWallpaper(n);
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Successfully got the data ", Toast.LENGTH_SHORT).show();
                }
            });

            int temp = imgUrls.size();
            imgs = web.getImageURL();
            tit = web.getTitle();
            res = web.getRes();
            id = web.getId();
            imgUrls.addAll(imgs);
            titles.addAll(tit);
            Res.addAll(res);
            ids.addAll(id);

            MainActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if(temp == 0){
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        adapter.notifyItemRangeInserted(temp, imgUrls.size());
                    }
                }
            });



        }
    }



}