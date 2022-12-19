package com.example.shaalwallpaper.Backend;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class inAppUpdate extends AsyncTask<String,Integer,String> {

    private Context context;
    private PowerManager.WakeLock mWakeLock;

    private final String TAG = "inAppUpdate";
    private ProgressDialog mProgressDialog;

    public inAppUpdate(Context context, ProgressDialog progressBar){
        this.context = context;
        mProgressDialog = progressBar;
    }

    public inAppUpdate(){}


    public void setContext(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... arg0) {
        try {
            URL url = new URL(arg0[0]);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.setChunkedStreamingMode(1024);

            c.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "doInBackground: " + "Something wrong happened "
                        + c.getResponseMessage() + " "
                + c.getResponseCode()
                + c.getErrorStream());
                return "doInBackground: " + "Server returned HTTP " + c.getResponseCode()
                        + " " + c.getResponseMessage();
            }

            String PATH = Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS) + "/";

//            String PATH = "/mnt/sdcard/Download/";
            File file = new File(PATH);
            boolean results = file.mkdirs();
            Log.d(TAG, "doInBackground: directory created: " + results);
            File outputFile = new File(file, "update.apk");
            if(outputFile.exists()){
                boolean r = outputFile.delete();
                Log.d(TAG, "doInBackground: file deleted: " + r);
            }
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

//            byte[] buffer = new byte[1024];
//            int len1 = 0;
//            while ((len1 = is.read(buffer)) != -1) {
//                fos.write(buffer, 0, len1);
//
//            }

            int fileLength = c.getContentLength();
            Log.d(TAG, "doInBackground: length: " + fileLength);
            byte[] data = new byte[4096];
            long total = 0;
            int count;
            while ((count = is.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    is.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                fos.write(data, 0, count);
            }
            fos.close();
            is.close();



//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.fromFile(new File(PATH + "update.apk"))
//                    , "application/vnd.android.package-archive");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
//            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("UpdateAPP", "Update error! " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire(10*60*1000L /*10 minutes*/);
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.show();
            }
        });


    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgress(progress[0]);
            }
        });

    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
                if (result != null)
                    Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
            }
        });

    }

}