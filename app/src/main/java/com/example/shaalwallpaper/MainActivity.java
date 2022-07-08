package com.example.shaalwallpaper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.service.controls.DeviceTypes;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shaalwallpaper.Adapter.WallpaperAdapter;
import com.example.shaalwallpaper.databinding.ActivityMainBinding;
import com.example.shaalwallpaper.helper.Util;
import com.example.shaalwallpaper.helper.webScrapping;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    //ShakeListner shakeListner;
    int n = 0;
    int sha = 0;
    private static int y;
    List<String> titles = null, Res = null, imgUrls=null;
    List<Integer> ids=null;
    //ProgressDialog mProgressDialog;
    boolean isScrolling = false;
    //int currItems, totalItems, scrolledItems;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int WALLPAPER_PERMISSION_CODE = 102;
    private final String TAG = "MAIN ACTIVITY";
    private final Util util = new Util();
    private final String name = "ADD Data";
    private String time = "15 min";
    private boolean toggle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        

//        Intent intent = new Intent(MainActivity.this, Collection.class);
//        startActivity(intent);
        Objects.requireNonNull(getSupportActionBar()).hide();
       // setSupportActionBar(binding.appBar);
        //getSupportActionBar(binding.appBar)

        //Toast.makeText(this, "Network Available " + isNetworkAvailable(), Toast.LENGTH_SHORT).show();
        if(!isNetworkAvailable()){
            //Log.d(TAG, "onCreate: " + "Hey Why didn't you awake");
            Intent intent = new Intent(MainActivity.this, Collection.class);
            startActivity(intent);
        }

        binding.newProgressbar1.setVisibility(View.VISIBLE);

        try {
            SharedPreferences sharedPreferences = getSharedPreferences("Timer", MODE_PRIVATE);
            time = sharedPreferences.getString("time", "15 min");
        }catch (Exception e){
            e.printStackTrace();
        }

        createDirectory();

        if(savedInstanceState == null) {
            titles = new ArrayList<>();
            imgUrls = new ArrayList<>();
            Res = new ArrayList<>();
            ids = new ArrayList<>();
        }
        else{
            titles = savedInstanceState.getStringArrayList("titles");
            imgUrls = savedInstanceState.getStringArrayList("imgURLs");
            Res = savedInstanceState.getStringArrayList("Resolution");
            ids = savedInstanceState.getIntegerArrayList("ids");
            binding.newProgressbar1.setVisibility(View.GONE);
            n = imgUrls.size()/24;
        }

//        SharedPreferences sharedPreferences = getSharedPreferences("Timer",MODE_PRIVATE);
//        SharedPreferences.Editor myEdit = sharedPreferences.edit();
//        myEdit.putString("time", "15 min");
//        myEdit.apply();
        WallpaperAdapter adapter = new WallpaperAdapter(imgUrls, titles, Res, ids, this);
        //binding.wallpaperHome.setHasFixedSize(true);

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
        binding.wallpaperHome.setLayoutManager(manager);
        binding.wallpaperHome.setAdapter(adapter);

        if(imgUrls.size() == 0 && isNetworkAvailable()) {
            AddDataThread dataThread = new AddDataThread(adapter);
            dataThread.setName(name + n);
            dataThread.start();
        }

        binding.extendedFloatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Collection.class);
            startActivity(intent);
        });

        binding.searchButton.setOnClickListener(view -> performSearch(binding.searchEdt.getText().toString()));

        binding.searchEdt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //closeKeyboard();
                performSearch(v.getText().toString());
                return true;
            }
            return false;
        });

        RecyclerThread thread = new RecyclerThread(adapter);
        thread.start();

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.setNavigationBarColor(getColor(R.color.colorPrimary));
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        new Thread(){
            @Override
            public void run() {
                super.run();
                startServiceViaWorker();
            }
        }.start();

    }

//    public void onStartServiceClick(View v) {
//        startService();
//    }
//
//    public void onStopServiceClick(View v) {
//        stopService();
//    }



    @Override
    protected void onDestroy() {
        //Log.d(TAG, "onDestroy called");
       // stopService();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        //Log.d(TAG, "onPause called");
        super.onPause();
    }

    @Override
    protected void onStop() {
        //Log.d(TAG, "onStop called");
        super.onStop();
    }

    @Override
    protected void onStart() {
        //Log.d(TAG, "onStart called");
        super.onStart();
    }

    @Override
    protected void onPostResume() {
        //Log.d(TAG, "onResume called");
        super.onPostResume();
    }

    @Nullable
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return super.onRetainCustomNonConfigurationInstance();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        //savedInstanceState.
        Log.d(TAG, "onSaveInstanceState: called");

        super.onSaveInstanceState(savedInstanceState);

        if(imgUrls!=null && imgUrls.size() != 0){
            savedInstanceState.putStringArrayList("imgURLs", (ArrayList<String>) imgUrls);
            savedInstanceState.putStringArrayList("Resolution", (ArrayList<String>) Res);
            savedInstanceState.putStringArrayList("titles", (ArrayList<String>) titles);
            savedInstanceState.putIntegerArrayList("ids", (ArrayList<Integer>) ids);

        }
    }

    //    public void startService() {
//        Log.d("TAG", "startService called");
//        if (!MyService.isServiceRunning) {
//            Intent serviceIntent = new Intent(this, MyService.class);
//            ContextCompat.startForegroundService(this, serviceIntent);
//            //startService(serviceIntent);
//        }
//    }

    public void stopService() {
        Log.d("TAG", "stopService called");
        if (MyService.isServiceRunning) {
            Intent serviceIntent = new Intent(this, MyService.class);
            stopService(serviceIntent);
        }
    }

    public void startServiceViaWorker() {
        if (!MyService.isServiceRunning) {
            //Log.d("MainActivty", "starting service from doWork");
            Intent intent = new Intent(this, MyService.class);
            intent.putExtra("time", time);
            ContextCompat.startForegroundService(this, intent);
            //this.context.startService(intent);
        }
        //Log.d("TAG", "startServiceViaWorker called");

        String UNIQUE_WORK_NAME = "StartMyServiceViaWorker";
        //String WORKER_TAG = "MyServiceWorkerTag";
        WorkManager workManager = WorkManager.getInstance(this);

        // As per Documentation: The minimum repeat interval that can be defined is 15 minutes (
        // same as the JobScheduler API), but in practice 15 doesn't work. Using 17 here
        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                        MyWorker.class,
                        17,
                        TimeUnit.MINUTES)
                        //.addTag(WORKER_TAG)
                        .build();
//        // below method will schedule a new work, each time app is opened
//        //workManager.enqueue(request);
//
//        // to schedule a unique work, no matter how many times app is opened i.e. startServiceViaWorker gets called
//        // https://developer.android.com/topic/libraries/architecture/workmanager/how-to/unique-work
//        // do check for AutoStart permission
//
//        Constraints constraints = new Constraints.Builder()
//                .setRequiresDeviceIdle(DeviceTy)
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .build();
        workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP, request);

    }

    public void createDirectory(){
        Util.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        //Log.d("Directory creation", "createDirectory: ");
        String WALLPAPER_DIRECTORY = "Shaal-Wallpaper";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + WALLPAPER_DIRECTORY);
        //File file = new File(getExternalFilesDir(null) + "/" + WALLPAPER_DIRECTORY);

        if(file.exists()){
            Log.d("Directory creation", "createDirectory: ");
            return;
        }

        boolean results = file.mkdir();

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
        if(!results){
            Log.d("Directory creation", "Failed at: " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        }
        //Log.d("Directory creation", ": " + results);
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
                Toast.makeText(MainActivity.this, "Storage Permission Denied",
                        Toast.LENGTH_SHORT).show();
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

        WallpaperAdapter adapter;
        int n = 1;

        RecyclerThread(WallpaperAdapter adapter){
            this.adapter = adapter;
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

                    if(RecyclerView.SCROLL_STATE_IDLE ==newState){
                        // fragProductLl.setVisibility(View.VISIBLE);
                        if(y<=0){
                            if(toggle){
                                show();
                            }
                        }
                        else{
                            y=0;
                            if(!toggle){
                                hide();
                            }
                        }
                    }
                }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                y = dy;
//                if(dy > 0){
//                    if(!toggle)
//                    hide();
//                }
//                else{
//                    if(toggle)
//                    show();
//
//                }
                if(isScrolling && !recyclerView.canScrollVertically(1) && !check(n-1)){
                    sha++;
                    isScrolling = false;
                    binding.SHOWPROGRESS.setVisibility(View.VISIBLE);
                    AddDataThread thread = new AddDataThread(adapter);
                    thread.setName(name+ n);
                    try {
                        thread.start();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    //Toast.makeText(MainActivity.this, "How many will you be called " + sha, Toast.LENGTH_SHORT).show();
                }
            }
            });
        }
    }

    class AddDataThread extends Thread{
        webScrapping web = new webScrapping(MainActivity.this);
        WallpaperAdapter adapter;

        List<String> imgs, tit, res;
        List<Integer> id;
        AddDataThread(WallpaperAdapter adapter){
            this.adapter = adapter;
        }

        @Override
        public void run() {
            n++;
            try {
                web.getWallpaper(n);
            }catch (Exception e){
                MainActivity.this.runOnUiThread(() -> Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            }


            int temp = imgUrls.size();
            imgs = web.getImageURL();
            tit = web.getTitle();
            res = web.getRes();
            id = web.getId();
            imgUrls.addAll(imgs);
            titles.addAll(tit);
            Res.addAll(res);
            ids.addAll(id);

            MainActivity.this.runOnUiThread(() -> {
                binding.newProgressbar1.setVisibility(View.GONE);
                if(temp == 0){
                    adapter.notifyDataSetChanged();
                    binding.SHOWPROGRESS.setVisibility(View.GONE);
                }
                else {
                    adapter.notifyItemRangeInserted(temp, imgUrls.size());
                    binding.SHOWPROGRESS.setVisibility(View.GONE);
                    binding.wallpaperHome.smoothScrollBy(0, 40);
                }
            });
        }
    }

    boolean check(int temp){
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals(name+temp) && t.isAlive()) return true;
        }
        return false;
    }

    void performSearch(String text){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.searchResults, resultFragment.class, null)
//                .setReorderingAllowed(true)
//                .addToBackStack("name")
//                .commit();
//        Bundle args = new Bundle();
//        args.putString("platform", "Spotify");
        binding.newProgressbar1.setVisibility(View.VISIBLE);
        seacrhFragment fragment = new seacrhFragment(text, binding.newProgressbar1);
        fragment.show(getSupportFragmentManager(), "Search Engine");
        //fragment.setArguments(args);
//        fragment.setCancelable(true);

        //show();
    }

    private void closeKeyboard() {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {
            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void hide(){
        toggle = !toggle;
        binding.appBar.setVisibility(View.GONE);
        binding.appBar.animate()
                .translationY(-binding.appBar.getHeight())

                .alpha(0.0f)
                .setDuration(1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        binding.appBar.setVisibility(View.GONE);
                    }
                });
    }

    private void show(){
        toggle = !toggle;
        binding.appBar.setVisibility(View.VISIBLE);
        binding.appBar.animate()
                .translationY(0)
                .setDuration(5000)
                .alpha(1.0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        binding.appBar.setVisibility(View.VISIBLE);
                    }
                });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}


