package com.example.shaalwallpaper.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.impl.utils.WorkTimer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.shaalwallpaper.R;
import com.example.shaalwallpaper.Wallpaper;
import com.example.shaalwallpaper.webScrapping;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class WallapaperAdapter extends RecyclerView.Adapter<WallapaperAdapter.WallapaperViewHolder> {
    public List<String> imgUrls, titles, res;
    private List<Integer> ids;
    private int count;
    private webScrapping web;
    int n = 2;
    Context context;
    RecyclerView recyclerView;

    @NonNull
    @Override
    public WallapaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listwallpaperlayout, parent, false);
        return new WallapaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WallapaperViewHolder holder, int position) {
        Log.d("Wallpaper", "onBindViewHolder: " + imgUrls.get(position));
        Glide.with(context)
                .load(imgUrls.get(position))
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.img);

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callWallpaperScreen(imgUrls.get(position), titles.get(position), ids.get(position));
            }
        });
    }

    private void setImageAnimate(ImageView img) {
    }

    private void callWallpaperScreen(String imgURL, String title, int id) {
        Intent intent = new Intent(context, Wallpaper.class);
        intent.putExtra("imgURL", imgURL);
        intent.putExtra("title", title);
        intent.putExtra("id", Integer.toString(id));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        //Log.d("TAG", "getItemCount:" + imgUrls.size());
        return imgUrls.size();
    }

    public WallapaperAdapter(webScrapping web, Context context, RecyclerView recyclerView) {
        this.web = web;
        web.getWallpaper(n);
        this.context = context;
        this.imgUrls = web.getImageURL();
        Log.d("SO rha hu", "WallapaperAdapter: " + imgUrls.size());
        this.titles = web.getTitle();
        this.count = web.getCount();
        this.res = web.getRes();

        this.ids = web.getId();
        this.recyclerView = recyclerView;
        count-=ids.size();
    }

    public WallapaperAdapter(List<String> imgURLs, List<String> titles, List<String> res, List<Integer> ids, Context context) {
        this.context = context;
        this.imgUrls = imgURLs;
        Log.d("SO rha hu", "WallapaperAdapter: " + imgURLs.size());
        this.titles = titles;
        this.ids = ids;
        this.res = res;
    }

    public static class WallapaperViewHolder extends RecyclerView.ViewHolder {

        public ImageView img;

        public WallapaperViewHolder(@NonNull View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.Wallpaperlist);
        }
    }

//    public void loadMore(){
//        n++;
//        //Log.d("TAG", "loadMore: " + n);
//        if(count > 0) {
//            web.getWallpaper(n);
//            count-=ids.size();
//            Log.d("TAG", "loadMore: " + count);
//            //int i = ids.size();
////            int j = web.getId().size();
////            count -= j;
//            this.imgUrls.addAll(web.getImageURL());
////            Log.d("TAG", "loadMore: " + imgUrls);
//            this.titles.addAll(web.getTitle());
//            this.res.addAll(web.getRes());
//            this.ids.addAll(web.getId());
//            //notifyItemRangeInserted(i, j);
//            notifyDataSetChanged();
//        }
//
//
//    }
}