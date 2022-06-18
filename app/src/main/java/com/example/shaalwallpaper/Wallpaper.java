package com.example.shaalwallpaper;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

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
    boolean toggle = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWallpaperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        String imgURL="";
        String path = "";
        //String title = "";

        webScrapping web = new webScrapping(this);
        getSupportActionBar().hide();
        int i = getIntent().getIntExtra("type", 0);
        String title = getIntent().getStringExtra("title");
        binding.Name.setText(title);
        //imgURL = getIntent().getStringExtra("imgURL", " ")
        if(i == 1) {
            imgURL = getIntent().getStringExtra("imgURL");

        }
        else if(i==2)
            path = getIntent().getStringExtra("path");

        String id = getIntent().getStringExtra("id");

        if(i == 1) {
            try {
                String imgU = web.getHighest(imgURL);
                //Toast.makeText(this, "" + imgU, Toast.LENGTH_SHORT).show();
                Picasso.get().load(imgU).into(binding.mainWallpaper);
            }catch (Exception e){
                Picasso.get().load(imgURL).into(binding.mainWallpaper);
            }
        }

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

        binding.mainWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle = !toggle;
                if(toggle){
                    hide();
                }
                else{
                    show();
                }
            }
        });


        Window window = this.getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.setNavigationBarColor(this.getResources().getColor(R.color.colorPrimary));
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


//        window.getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE);


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

    void hide(){
//        binding.mainWallpaper.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
//        binding.mainWallpaper.setAdjustViewBounds(true);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        binding.toolbar2.startAnimation(animation);
        binding.linearLayout1.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.toolbar2.setVisibility(View.GONE);
                binding.linearLayout1.setVisibility(View.GONE);
            }
        }, 1000);


        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars());

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

//        binding.mainWallpaper.setLayoutParams(new FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
//        ));
//        binding.mainWallpaper.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    void show(){
        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        // Hide both the status bar and the navigation bar
        windowInsetsController.show(WindowInsetsCompat.Type.navigationBars());
//        binding.mainWallpaper.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
//        binding.mainWallpaper.setScaleType(ImageView.ScaleType.FIT_XY);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        //starting the animation
        binding.toolbar2.setVisibility(View.VISIBLE);
        binding.linearLayout1.setVisibility(View.VISIBLE);
        binding.toolbar2.startAnimation(animation);
        binding.linearLayout1.startAnimation(animation);





//        binding.mainWallpaper.setLayoutParams(new FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
//        ));
//        binding.mainWallpaper.setAdjustViewBounds(true);
    }

}