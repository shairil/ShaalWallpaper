package com.example.shaalwallpaper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
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
    List<String> titles = null, Res = null, imgUrls=null;
    List<Integer> ids=null;
    //ProgressDialog mProgressDialog;
    boolean isScrolling = false;
    //int currItems, totalItems, scrolledItems;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int WALLPAPER_PERMISSION_CODE = 102;
    private final String WALLPAPER_DIRECTORY = "Shaal-Wallpaper", TAG = "MAIN ACTIVITY";
    private Util util = new Util();
    private final String name = "ADD Data";
    private String time = "15 min";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        

//        Intent intent = new Intent(MainActivity.this, Collection.class);
//        startActivity(intent);
        getSupportActionBar().hide();

        binding.newProgressbar1.setVisibility(View.VISIBLE);

        try {
            SharedPreferences sharedPreferences = getSharedPreferences("Timer", MODE_PRIVATE);
            time = sharedPreferences.getString("time", "15 min");
        }catch (Exception e){
            e.printStackTrace();
        }

        createDirectory();
       // util.checkPermission(this, Manifest.permission.SET_WALLPAPER, WALLPAPER_PERMISSION_CODE);
        //webScrapping web = new webScrapping(this);


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
        }
//        SharedPreferences sharedPreferences = getSharedPreferences("Timer",MODE_PRIVATE);
//        SharedPreferences.Editor myEdit = sharedPreferences.edit();
//        myEdit.putString("time", "15 min");
//        myEdit.apply();
        WallapaperAdapter adapter = new WallapaperAdapter(imgUrls, titles, Res, ids, this);
        //binding.wallpaperHome.setHasFixedSize(true);

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        binding.wallpaperHome.setLayoutManager(manager);
        binding.wallpaperHome.setAdapter(adapter);

        if(imgUrls.size() == 0) {
            AddDataThread dataThread = new AddDataThread(adapter);
            dataThread.setName(name + n);
            dataThread.start();
        }


        binding.extendedFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Collection.class);
                startActivity(intent);
            }
        });

        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSearch(binding.searchEdt.getText().toString());
            }
        });

        binding.searchEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //closeKeyboard();
                    performSearch(v.getText().toString());
                    return true;
                }
                return false;
            }
        });

        RecyclerThread thread = new RecyclerThread(adapter);
        thread.start();

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.setNavigationBarColor(this.getResources().getColor(R.color.colorPrimary));
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);



        startServiceViaWorker();
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
    public void onSaveInstanceState(Bundle savedInstanceState) {
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
            Log.d("MainActivty", "starting service from doWork");
            Intent intent = new Intent(this, MyService.class);
            intent.putExtra("time", time);
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
                        30,
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
        Util.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
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
        int n = 1;
        webScrapping web = new webScrapping(MainActivity.this);

        RecyclerThread(WallapaperAdapter adapter){
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
                }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

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
        WallapaperAdapter adapter;

        List<String> imgs, tit, res;
        List<Integer> id;
        AddDataThread(WallapaperAdapter adapter){
            this.adapter = adapter;
        }

        @Override
        public void run() {
            n++;
            web.getWallpaper(n);


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

    private void closeKeyboard()
    {
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
}


