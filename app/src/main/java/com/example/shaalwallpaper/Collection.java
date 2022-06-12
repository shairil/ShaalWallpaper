package com.example.shaalwallpaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.example.shaalwallpaper.Adapter.WallapaperAdapter;
import com.example.shaalwallpaper.databinding.ActivityCollectionBinding;
import com.example.shaalwallpaper.databinding.ActivityWallpaperBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Collection extends AppCompatActivity {
    ActivityCollectionBinding binding;
    private List<String> titles, imgURLs, res;
    private List<Integer> ids;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private final String TAG = "CollectionClass";
    private final String WALLPAPER_DIRECTORY = "Shaal-Wallpaper";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityCollectionBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        titles = new ArrayList<>();
        imgURLs = new ArrayList<>();
        res = new ArrayList<>();
        ids = new ArrayList<>();
        WallapaperAdapter adapter = new WallapaperAdapter(titles, imgURLs, res, ids,this);
        binding.recyclerViewCollection.setHasFixedSize(true);
        binding.recyclerViewCollection.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        binding.recyclerViewCollection.setAdapter(adapter);

        File wallpaperDirectory = new File(getExternalFilesDir(null) + "/" + WALLPAPER_DIRECTORY);
        new Util();
        if (wallpaperDirectory.exists()) {
            try {
                File[] files = wallpaperDirectory.listFiles();
                if (files != null && files.length > 0) {

                    for (File randomFile : files) {
                        Log.d(TAG, "Size: " + randomFile.length());
                        Log.d(TAG, "Name: " + randomFile.getName());
                        String randomFilePath = randomFile.getAbsolutePath();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        Bitmap image = BitmapFactory.decodeFile(randomFilePath, options);
                        String img = image.toString();
                        imgURLs.add(img);
                        int i = randomFile.getName().indexOf('(');
                        titles.add(randomFile.getName().substring(0, i));
                        ids.add(Integer.valueOf(randomFile.getName().substring(i+1, i+7)));
                        adapter.notifyItemInserted(ids.size()-1);
                        Log.d(TAG, "onCreate: "  +img);
                    }
                } else {
                    Log.d(TAG, "Wallpaper directory is empty: ");
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



}