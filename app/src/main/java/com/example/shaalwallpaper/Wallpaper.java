package com.example.shaalwallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.shaalwallpaper.databinding.ActivityWallpaperBinding;
import com.example.shaalwallpaper.helper.SingleMediaScanner;
import com.example.shaalwallpaper.helper.webScrapping;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

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
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        String imgURL="";
        String path = "";
        //String title = "";

        webScrapping web = new webScrapping(this);
        Objects.requireNonNull(getSupportActionBar()).hide();
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

                Glide.with(this)
                        .load(imgU)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.mainWallpaper);
            }catch (Exception e){
                Toast.makeText(this, "Wallpaper Loaded Successfully", Toast.LENGTH_SHORT).show();
                Picasso.get().load(imgURL).into(binding.mainWallpaper);
            }
        }

//        binding.mainWallpaper.setImageResource(R.drawable.thumb_965469);
        else{
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap image = BitmapFactory.decodeFile(path, options);

            Glide.with(this)
                    .load(image)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.mainWallpaper);

            //binding.mainWallpaper.setImageBitmap(image);

        }


        binding.setWallpaper.setOnClickListener(view -> {
            BitmapDrawable drawable = (BitmapDrawable) binding.mainWallpaper.getDrawable();
            Bitmap image = drawable.getBitmap();
            DisplayMetrics displayMetrics;
            displayMetrics = getResources().getDisplayMetrics();
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            Log.d("Collection", "onClick: " + width + " " + height);
            WallpaperManager manager = WallpaperManager.getInstance(Wallpaper.this);
            WallpaperManager manager1 = WallpaperManager.getInstance(Wallpaper.this);


            try {
                Log.d("Collection", "onClick: Image Width" + image.getWidth() + " " + image.getHeight());
                manager.setBitmap(image, null, true, WallpaperManager.FLAG_LOCK);
                manager1.setBitmap(image);
                Toast.makeText(Wallpaper.this, "Wallpaper set Successfully.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

//                new Thread(() -> {
//                    try {
//                        manager1.setBitmap(image);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }).start();




        });

//
        //binding.addToCollection.setBackground(R.color.UBlack);



        binding.addToCollection.setOnClickListener(view -> {
            //getExternalMediaDirs()
            File wallpaperDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + WALLPAPER_DIRECTORY);
            //File wallpaperDirectory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + WALLPAPER_DIRECTORY);
            //File wallpaperDirectory = new File(getExternalFilesDir(null) + "/" + WALLPAPER_DIRECTORY);
            if(!wallpaperDirectory.exists()){
                boolean results = wallpaperDirectory.mkdir();
                Log.d("Wallpaper", "onClick: " + results);
            }

            Log.d("Wallpaper", "onClick: 1");

            if(wallpaperDirectory.exists()) {
                addCollection(binding.mainWallpaper, wallpaperDirectory, title, id);
            }
        });

        binding.mainWallpaper.setOnClickListener(view -> {
            toggle = !toggle;
            if(toggle){
                hide();
            }
            else{
                show();
            }
        });

        binding.backBtn2.setOnClickListener(view -> onBackPressed());


//        Window window = this.getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);


        // if above lines doesn't work well uncomment below ones.
//        if (Build.VERSION.SDK_INT >= 21) {
//            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
//            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//            getWindow().setNavigationBarColor(Color.TRANSPARENT);
//        }
//
//        else if (Build.VERSION.SDK_INT >= 19) {
//            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
//            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true);
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }



//        WindowInsetsControllerCompat windowInsetsController =
//                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
//        if (windowInsetsController == null) {
//            return;
//        }
//        // Hide both the status bar and the navigation bar
//        windowInsetsController.show(WindowInsetsCompat.Type.navigationBars());

        //window.setNavigationBarColor(this.getResources().getColor(R.color.colorPrimary));
        //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


//        window.getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE);


    }

    public void addCollection(ImageView imageView, File dir, String title, String id){
        Bitmap bitmap = ((BitmapDrawable)binding.mainWallpaper.getDrawable()).getBitmap();
        if(bitmap == null){
            Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show();
            Log.d("Wallpaper123", "addCollection: ");
        }
        title = title.replace(':', '_');
        File file = new File(dir, title + '(' +id + ')' + ".jpg");
        try{
            outputStream = new FileOutputStream(file);
            new SingleMediaScanner(this, file);
        }catch(FileNotFoundException e){
            Toast.makeText(this, "Failed " + e.getMessage() , Toast.LENGTH_SHORT).show();
            Log.d("Wallpaper123", "addCollection: " + e.getMessage());
        }
        try {
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void hide(){
//        binding.mainWallpaper.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
//        binding.mainWallpaper.setAdjustViewBounds(true);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        binding.toolbar2.startAnimation(animation);
        binding.linearLayout1.startAnimation(animation);

        new Handler().postDelayed(() -> {
            binding.toolbar2.setVisibility(View.GONE);
            binding.linearLayout1.setVisibility(View.GONE);
        }, 500);


        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars());
        //WindowInsetsCompat.Type.
        //windowInsetsController.hide(WindowInsetsCompat.Type.statusBars());

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

//                this.getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

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
        //windowInsetsController.show(WindowInsetsCompat.Type.statusBars());
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

}