package com.example.shaalwallpaper;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.shaalwallpaper.Adapter.WallpaperAdapter;
import com.example.shaalwallpaper.databinding.FragmentsearchresultsBinding;
import com.example.shaalwallpaper.helper.webScrapping;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class seacrhFragment extends BottomSheetDialogFragment {
    FragmentsearchresultsBinding binding;
//    private RecyclerView recyclerView;
//    private ProgressBar center, down;
//    private TextView textView;
    private String url, URL;
    private ProgressBar progressBar;
    private boolean isScrolling = false;
    private final String name = "Find Data";
    private int count = -1, n=1;
    List<String> titles, Res, imgUrls;
    List<Integer> ids;

    private WallpaperAdapter adapter;

    public seacrhFragment(String url, ProgressBar progressBar) {
        this.url = url;
        this.progressBar = progressBar;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentsearchresultsBinding.inflate(inflater, container, false);
        setRetainInstance(true);
        //binding = BottomSheetDialogFragment.in
//        View view = inflater.inflate(R.layout.fragmentsearchresults, container, false);
//        recyclerView = (RecyclerView) view.findViewById(R.id.searchWallpaper);

        //binding.result.setVisibility(View.VISIBLE);
        binding.newProgress.setVisibility(View.VISIBLE);

//        center = (ProgressBar) view.findViewById(R.id.newProgress);
//        down = (ProgressBar) view.findViewById(R.id.loadMoreProgressBar);
//        center.setVisibility(View.VISIBLE);
        titles = new ArrayList<>();
        imgUrls = new ArrayList<>();
        Res = new ArrayList<>();
        ids = new ArrayList<>();
//        textView.setVisibility(View.VISIBLE);


        adapter = new WallpaperAdapter(imgUrls, titles, Res, ids, getContext());

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        binding.searchWallpaper.setLayoutManager(manager);
        binding.searchWallpaper.setAdapter(adapter);

        webScrapping web = new webScrapping(getContext());
        web.getSearchGoogle(url, 1);

        count = web.getCount();
        Toast.makeText(getContext(), "Hey! we found a total of " + count +" Wallpapers", Toast.LENGTH_SHORT).show();
        if(count != -1) {
            List<String> imgs, tit, res;
            List<Integer> id;
            URL = web.getURL();
            imgs = web.getImageURL();
            tit = web.getTitle();
            res = web.getRes();
            id = web.getId();
            imgUrls.addAll(imgs);
            titles.addAll(tit);
            Res.addAll(res);
            ids.addAll(id);
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }

        progressBar.setVisibility(View.GONE);
        binding.newProgress.setVisibility(View.GONE);
        if(imgUrls.size() == 0){
            binding.result.setVisibility(View.VISIBLE);
        }

        binding.searchWallpaper.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                    binding.loadMoreProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isScrolling && !recyclerView.canScrollVertically(1) && !check(n-1)){
                    //sha++;
                    isScrolling = false;
                    binding.loadMoreProgressBar.setVisibility(View.VISIBLE);
                    if(count != -1) {
                        AddDataThread1 thread = new AddDataThread1(adapter);
                        thread.setName(name + n);
                        try {
                            thread.start();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        binding.loadMoreProgressBar.setVisibility(View.GONE);
                    }

                    //Toast.makeText(MainActivity.this, "How many will you be called " + sha, Toast.LENGTH_SHORT).show();

                }
            }
        });




//        AddDataThread1 thread = new AddDataThread1(adapter);
//        thread.setName(name+ n);
//        thread.start();

//        RecyclerThread1 thread2 = new RecyclerThread1(adapter);
//        thread2.start();



        return binding.getRoot();
    }

//    class RecyclerThread1 extends Thread{
//
//        WallapaperAdapter adapter;
//        int n = 1;
//
//        RecyclerThread1(WallapaperAdapter adapter){
//            this.adapter = adapter;
//        }
//
//        @Override
//        public void run() {
//            binding.searchWallpaper.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                    super.onScrollStateChanged(recyclerView, newState);
//                    if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
//                        isScrolling = true;
//                        binding.loadMoreProgressBar.setVisibility(View.GONE);
//                    }
//                }
//
//                @Override
//                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//
//                    if(isScrolling && !recyclerView.canScrollVertically(1) && !check(n-1)){
//                        //sha++;
//                        isScrolling = false;
//                        binding.loadMoreProgressBar.setVisibility(View.VISIBLE);
//                        AddDataThread1 thread = new AddDataThread1(adapter);
//                        thread.setName(name+ n);
//                        try {
//                            thread.start();
//                        }catch (Exception e){
//                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                        //Toast.makeText(MainActivity.this, "How many will you be called " + sha, Toast.LENGTH_SHORT).show();
//
//                    }
//                }
//            });
//        }
//    }

    class AddDataThread1 extends Thread{
        webScrapping web = new webScrapping(getContext());
        WallpaperAdapter adapter;

        List<String> imgs, tit, res;
        List<Integer> id;
        AddDataThread1(WallpaperAdapter adapter){
            this.adapter = adapter;
        }



        @Override
        public void run() {
            n++;
//            requireActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getContext(), "Got here inside Fragment " + n, Toast.LENGTH_SHORT).show();
//                }
//            });

            web.getAfterGoogleWallpaper(n, URL);
//            if(count == -1){
//                count = web.getCount();
//            }
//            requireActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getContext(), "Successfully got the data ", Toast.LENGTH_SHORT).show();
//                }
//            });

            int temp = imgUrls.size();
            imgs = web.getImageURL();
            tit = web.getTitle();
            res = web.getRes();
            id = web.getId();
            imgUrls.addAll(imgs);
            titles.addAll(tit);
            Res.addAll(res);
            ids.addAll(id);
            Log.d("insideFragment", "run: " + imgUrls);

            requireActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if(temp == 0){
                        adapter.notifyDataSetChanged();
                        binding.newProgress.setVisibility(View.GONE);
                        binding.loadMoreProgressBar.setVisibility(View.GONE);
                        if(count == -1){
                            binding.result.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        adapter.notifyItemRangeInserted(temp, imgUrls.size());
                        binding.newProgress.setVisibility(View.GONE);
                        binding.loadMoreProgressBar.setVisibility(View.GONE);
                        binding.searchWallpaper.smoothScrollBy(0, 40);
                    }
                }
            });



        }
    }

    @Override
    public void setRetainInstance(boolean retain) {
        super.setRetainInstance(retain);
    }

    boolean check(int temp){
        //Toast.makeText(, "", Toast.LENGTH_SHORT).show();
        if((temp+1)*24 >= count){
            return true;
        }

        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals(name+temp) && t.isAlive()) return true;
        }
        return false;
    }


}
