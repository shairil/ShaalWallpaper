package com.example.shaalwallpaper;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shaalwallpaper.databinding.ActivityWallpaperBinding;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Wallpaper extends AppCompatActivity {
    private final String WALLPAPER_DIRECTORY = "Shaal-Wallpaper";
    ActivityWallpaperBinding binding;
    FileOutputStream outputStream;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWallpaperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        webScrapping web = new webScrapping(this);
        getSupportActionBar().hide();
        String imgURL = getIntent().getStringExtra("imgURL");
        String title = getIntent().getStringExtra("title");
        String id = getIntent().getStringExtra("id");
        imgURL = web.getHighest(imgURL);
        Picasso.get().load(imgURL).into(binding.mainWallpaper);

        binding.setWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable drawable = (BitmapDrawable) binding.mainWallpaper.getDrawable();
                Bitmap image = drawable.getBitmap();
                WallpaperManager manager = WallpaperManager.getInstance(Wallpaper.this);
                try {
                    manager.setBitmap(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        binding.addToCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File wallpaperDirectory = new File(getExternalFilesDir(null) + "/" + WALLPAPER_DIRECTORY);
                if(!wallpaperDirectory.exists()){
                    boolean results = wallpaperDirectory.mkdir();
                    Log.d("Wallpaper", "onClick: " + results);
                }
                if(wallpaperDirectory.exists()) {
                    addCollection(binding.mainWallpaper, wallpaperDirectory, title, id);
                }
            }
        });



    }

    public void addCollection(ImageView imageView, File dir, String title, String id){
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        File file = new File(dir, title + '(' +id + ')' + ".jpg");
        try{
            outputStream = new FileOutputStream(file);
        }catch(FileNotFoundException e){
            Log.d("Wallpaper", "addCollection: " + e.getMessage());
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}