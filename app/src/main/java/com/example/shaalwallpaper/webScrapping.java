package com.example.shaalwallpaper;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class webScrapping {

    private List<String> imgURL, titles, Res;
    //private String[] titles, Res;
    private List<Integer> ids;
//    private int count;
//    private PyObject obj;

    public webScrapping(Context context){
        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(context));
        }

        imgURL = new ArrayList<>();
        titles = new ArrayList<>();
        Res = new ArrayList<>();
        ids = new ArrayList<>();
    }


    public void getWallpaper(int n){
        Python py = Python.getInstance();
        List<PyObject> pyObject = py.getModule("webScrapping").callAttr("anime_wallpaper", n).asList();
        setTitles(pyObject.get(0).toJava(String[].class));
        //setTitles(pyObject.get(0).toJava(List<String>));
        setImgURL(pyObject.get(1).toJava(String[].class));
        setRes(pyObject.get(2).toJava(String[].class));
        setIDs(pyObject.get(3).toJava(Integer[].class));
    }

    public void setImgURL(String[] imgURL) {
        if(imgURL != null) {
            this.imgURL.clear();
            Collections.addAll(this.imgURL, imgURL);
        }
        //this.imgURL = Arrays.asList(imgURL);
        //this.imgURL = imgURL;
    }

    public void setTitles(String[] titles) {
        if(titles != null) {
            this.titles.clear();
            Collections.addAll(this.titles, titles);
        }
        //this.titles = Arrays.asList(titles);
    }

//    public void setCount(int count) {
//        this.count = count;
//    }

    public void setRes(String[] resolution){
        if(resolution != null) {
            this.Res.clear();
            Collections.addAll(this.Res, resolution);
        }
        //this.Res = Arrays.asList(resolution);
    }

    public void setIDs(Integer[] ids){
        if(ids != null) {
            this.ids.clear();
            Collections.addAll(this.ids, ids);
        }
        //this.ids = Arrays.asList(ids);
    }

    public String getHighest(String str){
        Python py = Python.getInstance();
        return py.getModule("webScrapping").callAttr("getHighImgResolution", str).toString();
    }

    public List<String> getImageURL(){
        Log.d("Wallpaper", "getImageURL: " + imgURL.get(0));
        return imgURL;
    }

    public List<String> getTitle(){
        return titles;
    }

    public List<Integer> getId(){
        return ids;
    }

    public List<String> getRes(){
        return Res;
    }

    public int getCount(){
        Python py = Python.getInstance();
        return py.getModule("webScrapping").callAttr("getCount").toInt();
        //setCount(obj.get(0).toInt());
        //return count;
    }
}
