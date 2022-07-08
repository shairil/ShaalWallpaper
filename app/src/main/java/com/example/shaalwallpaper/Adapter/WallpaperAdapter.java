package com.example.shaalwallpaper.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.shaalwallpaper.R;
import com.example.shaalwallpaper.Wallpaper;

import java.io.File;
import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder> {
    public List<String> imgUrls = null, titles=null, res=null;
    private List<Integer> ids=null;
    private List<File> files = null;
    Context context;

    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listwallpaperlayout, parent, false);
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperViewHolder holder, int position) {
        //Log.d("Wallpaper", "onBindViewHolder: " + imgUrls.get(position));
        if(imgUrls != null) {
            Glide.with(context)
                    .load(imgUrls.get(position))
                    .placeholder(R.drawable.ic_wallpaper_black_24dp)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.img);
        }
        else{
            Glide.with(context)
                    .load(files.get(position))
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.img);
        }


        holder.cardView.setOnClickListener(view -> {
            if(imgUrls != null)
                callWallpaperScreenFromMain(imgUrls.get(position), titles.get(position), ids.get(position));
            else{
                int i = files.get(position).getName().indexOf('(');
                int j = files.get(position).getName().indexOf(')');
                int id = Integer.parseInt(files.get(position).getName().substring(i+1, j));
                String title = files.get(position).getName().substring(0, i);
                String path = files.get(position).getAbsolutePath();
                callWallpaperScreenFromCollection(path, title, id);
            }

        });
    }

//    private void setImageAnimate(ImageView img) {
//    }

    private void callWallpaperScreenFromMain(String imgURL, String title, int id) {
        Intent intent = new Intent(context, Wallpaper.class);

        intent.putExtra("type", 1);
        intent.putExtra("imgURL", imgURL);
        intent.putExtra("title", title);
        intent.putExtra("id", Integer.toString(id));
        context.startActivity(intent);
    }

    private void callWallpaperScreenFromCollection(String path, String title, int id) {
        Intent intent = new Intent(context, Wallpaper.class);

        intent.putExtra("type", 2);
        intent.putExtra("path", path);
        intent.putExtra("title", title);
        intent.putExtra("id", Integer.toString(id));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        //Log.d("TAG", "getItemCount:" + imgUrls.size());
        if(ids == null){
            return files.size();
        }
        return ids.size();
    }

//    public WallpaperAdapter(webScrapping web, Context context, RecyclerView recyclerView) {
//        this.web = web;
//        web.getWallpaper(n);
//        this.context = context;
//        this.imgUrls = web.getImageURL();
//        Log.d("SO rha hu", "WallpaperAdapter: " + imgUrls.size());
//        this.titles = web.getTitle();
//        this.count = web.getCount();
//        this.res = web.getRes();
//
//        this.ids = web.getId();
//        this.recyclerView = recyclerView;
//        count-=ids.size();
//    }

    public WallpaperAdapter(List<String> imgURLs, List<String> titles, List<String> res, List<Integer> ids, Context context) {
        this.context = context;
        this.imgUrls = imgURLs;
//        Log.d("SO rha hu", "WallpaperAdapter: " + imgURLs.size());
        this.titles = titles;
        this.ids = ids;
        this.res = res;
    }

//    public WallpaperAdapter(List<Bitmap> imgURLs, List<String> titles, List<Integer> ids, Context context, List<String> paths) {
//        this.context = context;
//        this.imgs = imgURLs;
//        this.titles = titles;
//        this.ids = ids;
//        this.paths = paths;
//    }

    public WallpaperAdapter(List<File> files, Context context) {
        this.context = context;
        this.files = files;
    }

    public static class WallpaperViewHolder extends RecyclerView.ViewHolder {

        public ImageView img;
        public CardView cardView;

        public WallpaperViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.Wallpaperlist);
            cardView = itemView.findViewById(R.id.idCVWallpaper);
        }
    }

}