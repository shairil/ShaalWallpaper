package com.example.shaalwallpaper;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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

        String imgURL="";
        String path = "";

        webScrapping web = new webScrapping(this);
        getSupportActionBar().hide();
        int i = getIntent().getIntExtra("type", 0);
        //imgURL = getIntent().getStringExtra("imgURL", " ")
        if(i == 1) {
            imgURL = getIntent().getStringExtra("imgURL");
            imgURL = web.getHighest(imgURL);
        }
        else if(i==2)
            path = getIntent().getStringExtra("path");
        String title = getIntent().getStringExtra("title");
        String id = getIntent().getStringExtra("id");

        if(i == 1)
            Picasso.get().load(imgURL).into(binding.mainWallpaper);

//        binding.mainWallpaper.setImageResource(R.drawable.thumb_965469);
        else{
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap image = BitmapFactory.decodeFile(path, options);
            binding.mainWallpaper.setImageBitmap(image);

        }


        binding.setWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable drawable = (BitmapDrawable) binding.mainWallpaper.getDrawable();
                Bitmap image = drawable.getBitmap();
                WallpaperManager manager = WallpaperManager.getInstance(Wallpaper.this);
                WallpaperManager manager1 = WallpaperManager.getInstance(Wallpaper.this);

                try {
                    manager.setBitmap(image, null, true, WallpaperManager.FLAG_LOCK);
                    Toast.makeText(Wallpaper.this, "Wallpaper set Successfully.", Toast.LENGTH_SHORT).show();
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